package com.ocean.member.core.application.service

import com.ocean.member.core.domain.model.Member
import com.ocean.member.core.domain.model.repository.MemberRepository
import com.ocean.member.core.domain.model.valueobject.Email
import com.ocean.member.core.domain.model.valueobject.MemberStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberCommandService(
    private val memberRepository: MemberRepository
) {
    fun createMember(email: String, name: String): Member {
        val validatedEmail = Email.of(email)

        require(!memberRepository.existsByEmail(validatedEmail.value)) {
            "Member with email '$email' already exists"
        }

        val member = Member.create(validatedEmail, name)
        return memberRepository.save(member)
    }

    fun updateMemberInformation(memberId: Long, newName: String): Member {
        validateId(memberId)
        val member = findMemberOrThrow(memberId)

        require(newName.isNotBlank()) { "Name cannot be null or blank" }

        member.updateInformation(newName)
        return memberRepository.save(member)
    }

    fun changeMemberStatus(memberId: Long, newStatus: MemberStatus): Member {
        validateId(memberId)
        val member = findMemberOrThrow(memberId)

        requireNotNull(newStatus) { "New status cannot be null" }

        member.changeStatus(newStatus)
        return memberRepository.save(member)
    }

    fun deleteMember(memberId: Long) {
        validateId(memberId)
        val member = findMemberOrThrow(memberId)
        member.softDelete()
        memberRepository.save(member)
    }

    private fun validateId(id: Long?) {
        require(id != null && id > 0) { "Invalid member ID: $id" }
    }

    private fun findMemberOrThrow(memberId: Long): Member {
        return memberRepository.findById(memberId)
            .orElseThrow { IllegalArgumentException("Member not found with ID: $memberId") }
    }
}
