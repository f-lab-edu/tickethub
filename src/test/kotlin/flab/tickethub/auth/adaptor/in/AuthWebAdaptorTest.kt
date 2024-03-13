package flab.tickethub.auth.adaptor.`in`

import flab.tickethub.auth.adaptor.`in`.request.LoginRequest
import flab.tickethub.auth.application.port.`in`.AuthQueryUseCase
import flab.tickethub.auth.domain.TokenPair
import flab.tickethub.support.RestDocsSupport
import flab.tickethub.support.endpoint.AUTH_URL
import flab.tickethub.support.endpoint.LOGIN_ENDPOINT
import io.restassured.http.ContentType
import org.hamcrest.core.IsEqual.equalTo
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatus
import org.springframework.restdocs.payload.PayloadDocumentation.*
import java.net.URI

class AuthWebAdaptorTest : RestDocsSupport() {

    private val authQueryUseCase = mock(AuthQueryUseCase::class.java)

    @Test
    fun `로그인 성공`() {
        val request = LoginRequest(
            email = "email@email.com",
            password = "password",
        )
        val tokenPair = TokenPair(
            accessToken = "accessToken",
            refreshToken = "refreshToken"
        )

        given(authQueryUseCase.login(request)).willReturn(tokenPair)

        mockMvc
            .contentType(ContentType.JSON)
            .body(convert(request))
            .post(URI.create("${AUTH_URL}${LOGIN_ENDPOINT}"))
            .then()
            .status(HttpStatus.OK)
            .body(
                "accessToken", equalTo("accessToken"),
                "refreshToken", equalTo("refreshToken")
            )
            .apply(
                document(
                    requestFields(
                        fieldWithPath("email").description("이메일"),
                        fieldWithPath("password").description("비밀번호"),
                    ),
                    responseFields(
                        fieldWithPath("accessToken")
                            .description("엑세스 토큰"),
                        fieldWithPath("refreshToken")
                            .description("리프레시 토큰"),
                    )
                )
            )
    }

    override fun controller(): Any {
        return AuthWebAdaptor(authQueryUseCase)
    }

}
