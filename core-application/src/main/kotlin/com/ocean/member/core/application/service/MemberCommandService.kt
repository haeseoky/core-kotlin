package com.ocean.member.core.application.service

import com.ocean.member.core.domain.model.Member
import com.ocean.member.core.domain.model.event.MemberEvent
import com.ocean.member.core.domain.model.repository.MemberRepository
import com.ocean.member.core.domain.model.valueobject.Email
import com.ocean.member.core.domain.model.valueobject.MemberStatus
import com.ocean.member.core.domain.port.MemberEventPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberCommandService(
    private val memberRepository: MemberRepository,
    private val memberEventPort: MemberEventPort
) {
    fun createMember(email: String, name: String): Member {
        val validatedEmail = Email.of(email)

        require(!memberRepository.existsByEmail(validatedEmail.value)) {
            "Member with email '$email' already exists"
        }

        val member = Member.create(validatedEmail, name)
        val saved = memberRepository.save(member)

        // 이벤트 발행
        memberEventPort.publish(
            MemberEvent.MemberCreatedEvent(
                memberId = saved.id.value(),
                email = saved.email.value,
                name = saved.name,
                status = saved.status.name
            )
        )

        return saved
    }

    fun updateMemberInformation(memberId: Long, newName: String): Member {
        validateId(memberId)
        val member = findMemberOrThrow(memberId)

        require(newName.isNotBlank()) { "Name cannot be null or blank" }

        val oldName = member.name
        member.updateInformation(newName)
        val saved = memberRepository.save(member)

        // 이벤트 발행
        memberEventPort.publish(
            MemberEvent.MemberUpdatedEvent(
                memberId = saved.id.value(),
                oldName = oldName,
                newName = saved.name
            )
        )

        return saved
    }

    fun changeMemberStatus(memberId: Long, newStatus: MemberStatus): Member {
        validateId(memberId)
        val member = findMemberOrThrow(memberId)

        requireNotNull(newStatus) { "New status cannot be null" }

        val oldStatus = member.status.name
        member.changeStatus(newStatus)
        val saved = memberRepository.save(member)

        // 이벤트 발행
        memberEventPort.publish(
            MemberEvent.MemberStatusChangedEvent(
                memberId = saved.id.value(),
                oldStatus = oldStatus,
                newStatus = saved.status.name
            )
        )

        return saved
    }

    fun deleteMember(memberId: Long) {
        validateId(memberId)
        val member = findMemberOrThrow(memberId)

        val email = member.email.value
        member.softDelete()
        memberRepository.save(member)

        // 이벤트 발행
        memberEventPort.publish(
            MemberEvent.MemberDeletedEvent(
                memberId = memberId,
                email = email
            )
        )
    }

    private fun validateId(id: Long?) {
        require(id != null && id > 0) { "Invalid member ID: $id" }
    }

    private fun findMemberOrThrow(memberId: Long): Member {
        return memberRepository.findById(memberId)
            .orElseThrow { IllegalArgumentException("Member not found with ID: $memberId") }
    }
}
