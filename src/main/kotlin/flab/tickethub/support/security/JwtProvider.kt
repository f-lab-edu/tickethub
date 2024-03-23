package flab.tickethub.support.security

import flab.tickethub.auth.domain.TokenPayload
import flab.tickethub.auth.domain.TokenPair
import flab.tickethub.support.config.time.DateTimeProvider
import flab.tickethub.support.error.ErrorCode
import flab.tickethub.support.properties.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*
import javax.crypto.SecretKey

const val BEARER_PREFIX = "Bearer "

@Component
class JwtProvider(
    private val jwtProperties: JwtProperties,
    private val dateTimeProvider: DateTimeProvider
) : TokenProvider {

    private val accessSecretKey =
        Keys.hmacShaKeyFor(jwtProperties.accessKey.toByteArray(Charsets.UTF_8))

    private val refreshSecretKey =
        Keys.hmacShaKeyFor(jwtProperties.refreshKey.toByteArray(Charsets.UTF_8))

    override fun generateTokenPair(tokenPayload: TokenPayload): TokenPair {
        val accessToken = generateAccessToken(tokenPayload)
        val refreshToken = generateRefreshToken(tokenPayload)
        return TokenPair(tokenPayload, accessToken, refreshToken)
    }

    override fun generateAccessToken(tokenPayload: TokenPayload): String {
        return generateToken(
            accessSecretKey,
            jwtProperties.accessExp,
            tokenPayload.claims()
        )
    }

    override fun generateRefreshToken(tokenPayload: TokenPayload): String {
        return generateToken(
            refreshSecretKey,
            jwtProperties.refreshExp,
            tokenPayload.claims()
        )
    }

    override fun validateAccessToken(token: String?): TokenPayload {
        return validateToken(token, accessSecretKey)
    }

    override fun validateRefreshToken(token: String?): TokenPayload {
        return validateToken(token, refreshSecretKey)
    }

    private fun generateToken(
        secretKey: SecretKey,
        exp: Duration,
        claims: Map<String, Any>
    ): String {
        val now = dateTimeProvider.nowDate()

        return BEARER_PREFIX + Jwts.builder()
            .signWith(secretKey, Jwts.SIG.HS256)
            .issuedAt(now)
            .expiration(Date.from(now.toInstant().plus(exp)))
            .claims(claims)
            .compact()
    }

    private fun validateToken(
        token: String?,
        secretKey: SecretKey
    ): TokenPayload {
        try {
            require(!token.isNullOrBlank() && token.startsWith(BEARER_PREFIX))
            val parsedClaims = parsedClaims(token.removePrefix(BEARER_PREFIX), secretKey)
            return TokenPayload.from(parsedClaims)
        } catch (e: ExpiredJwtException) {
            throw CredentialsExpiredException(ErrorCode.EXPIRED_TOKEN.message, e)
        } catch (e: JwtException) {
            throw BadCredentialsException(ErrorCode.INVALID_TOKEN.message, e)
        } catch (e: IllegalArgumentException) {
            throw BadCredentialsException(ErrorCode.INVALID_TOKEN.message, e)
        }
    }

    private fun parsedClaims(
        token: String,
        secretKey: SecretKey
    ): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .clock { dateTimeProvider.nowDate() }
            .build()
            .parseSignedClaims(token)
            .payload
    }

}