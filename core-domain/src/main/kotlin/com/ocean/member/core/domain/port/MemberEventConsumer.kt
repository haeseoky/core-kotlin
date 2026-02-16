package com.ocean.member.core.domain.port

/**
 * Port interface for consuming external member-related events
 * Infrastructure layer will implement this port to consume from Kafka, RabbitMQ, etc.
 */
interface MemberEventConsumer {
    fun onMemberCreated(memberId: Long, email: String, name: String)
    fun onMemberUpdated(memberId: Long, oldName: String?, newName: String)
    fun onMemberStatusChanged(memberId: Long, oldStatus: String, newStatus: String)
    fun onMemberDeleted(memberId: Long, email: String)
}
