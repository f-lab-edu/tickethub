package flab.tickethub.member.adaptor.`in`

import flab.tickethub.member.adaptor.`in`.request.CreateMemberRequest
import flab.tickethub.support.endpoint.MEMBER_URL
import flab.tickethub.support.response.ApiResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping(MEMBER_URL)
@RestController
class MemberWebAdaptor {

    @PostMapping
    fun create(@RequestBody request: CreateMemberRequest): ApiResponse<Unit> {
        return ApiResponse.created()
    }

}
