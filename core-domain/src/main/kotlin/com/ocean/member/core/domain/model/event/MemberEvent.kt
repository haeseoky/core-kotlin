package com.ocean.member.core.domain.model.event

import java.time.LocalDateTime

sealed class MemberEvent {
    abstract val memberId: Long
    abstract val occurredAt: LocalDateTime

    data class MemberCreatedEvent(
        override val memberId: Long,
        val email: String,
        val name: String,
        val status: String,
        override val occurredAt: LocalDateTime = LocalDateTime.now()
    ) : MemberEvent()

    data class MemberUpdatedEvent(
        override val memberId: Long,
        val oldName: String?,
        val newName: String,
        override val occurredAt: LocalDateTime = LocalDateTime.now()
    ) : MemberEvent()

    data class MemberStatusChangedEvent(
        override val memberId: Long,
        val oldStatus: String,
        val newStatus: String,
        override val occurredAt: LocalDateTime = LocalDateTime.now()
    ) : MemberEvent()

    data class MemberDeletedEvent(
        override val memberId: Long,
        val email: String,
        override val occurredAt: LocalDateTime = LocalDateTime.now()
    ) : MemberEvent()
}
