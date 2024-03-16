package flab.tickethub.auth.adaptor.`in`

import flab.tickethub.auth.adaptor.`in`.request.LoginRequest
import flab.tickethub.auth.application.port.`in`.AuthQueryUseCase
import flab.tickethub.auth.domain.TokenPair
import flab.tickethub.support.constant.ApiEndpoint
import flab.tickethub.support.response.ApiResult
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping(ApiEndpoint.AUTH)
@RestController
class AuthWebAdaptor(private val authQueryUseCase: AuthQueryUseCase) {

    @PostMapping(ApiEndpoint.LOGIN_ENDPOINT)
    fun login(@RequestBody request: LoginRequest): ResponseEntity<ApiResult<TokenPair>> {
        val tokenPair = authQueryUseCase.login(request)

        return ApiResult.ok(tokenPair)
    }

}