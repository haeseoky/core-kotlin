package com.ocean.member.core.application.service

import com.ocean.member.core.domain.model.Member
import com.ocean.member.core.domain.model.repository.MemberRepository
import com.ocean.member.core.domain.model.valueobject.Email
import com.ocean.member.core.domain.model.valueobject.MemberStatus
import org.springframework.stereotype.Service

@Service
class MemberCommandService(
    private val memberRepository: MemberRepository
) {
    fun createMember(email: String, name: String): Member {
        val emailVO = Email.of(email)
        if (memberRepository.existsByEmail(email)) {
            throw IllegalArgumentException("Member with email $email already exists")
        }
        val member = Member.create(emailVO, name)
        return memberRepository.save(member)
    }

    fun updateMemberInformation(memberId: Long, newName: String): Member {
        val member = getMemberById(memberId)
        member.updateInformation(newName)
        return memberRepository.save(member)
    }

    fun changeMemberStatus(memberId: Long, newStatus: MemberStatus): Member {
        val member = getMemberById(memberId)
        member.changeStatus(newStatus)
        return memberRepository.save(member)
    }

    fun deleteMember(memberId: Long) {
        val member = getMemberById(memberId)
        member.softDelete()
        memberRepository.save(member)
    }

    private fun getMemberById(memberId: Long): Member {
        return memberRepository.findById(memberId)
            .orElseThrow { IllegalArgumentException("Member not found with ID: $memberId") }
    }
}
