package com.ocean.member.core.application.service

import com.ocean.member.core.domain.model.Member
import com.ocean.member.core.domain.model.repository.MemberRepository
import org.springframework.stereotype.Service

@Service
class MemberQueryService(
    private val memberRepository: MemberRepository
) {
    fun getMemberById(memberId: Long): Member {
        validateId(memberId)
        return memberRepository.findById(memberId)
            .orElseThrow { IllegalArgumentException("Member not found with ID: $memberId") }
    }

    fun getAllMembers(): List<Member> {
        return memberRepository.findAll()
    }

    fun getActiveMembers(): List<Member> {
        return memberRepository.findAll().filter { it.isActive() }
    }

    private fun validateId(id: Long?) {
        require(id != null && id > 0) { "Invalid member ID: $id" }
    }
}
