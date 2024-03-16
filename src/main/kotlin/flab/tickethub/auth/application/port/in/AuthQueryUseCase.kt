package flab.tickethub.auth.application.port.`in`

import flab.tickethub.auth.adaptor.`in`.request.LoginRequest
import flab.tickethub.auth.domain.TokenPair

interface AuthQueryUseCase {

    fun login(request: LoginRequest): TokenPair

}