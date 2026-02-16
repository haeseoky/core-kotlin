package com.ocean.member.core.infra.adapter

import com.ocean.member.core.domain.port.MemberEventConsumer
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

/**
 * Adapter for consuming member events from Kafka
 * Implements the application port with infrastructure (Kafka)
 */
@Component
class MemberEventConsumerImpl : MemberEventConsumer {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun onMemberCreated(memberId: Long, email: String, name: String) {
        logger.info("Processing MemberCreated event: memberId=$memberId, email=$email, name=$name")
        // Implement business logic when member is created
        // e.g., send welcome email, initialize user preferences, etc.
    }

    override fun onMemberUpdated(memberId: Long, oldName: String?, newName: String) {
        logger.info("Processing MemberUpdated event: memberId=$memberId, oldName=$oldName, newName=$newName")
        // Implement business logic when member is updated
        // e.g., update search index, invalidate cache, etc.
    }

    override fun onMemberStatusChanged(memberId: Long, oldStatus: String, newStatus: String) {
        logger.info("Processing MemberStatusChanged event: memberId=$memberId, oldStatus=$oldStatus, newStatus=$newStatus")
        // Implement business logic when member status changes
        // e.g., revoke/access permissions, notify related systems, etc.
    }

    override fun onMemberDeleted(memberId: Long, email: String) {
        logger.info("Processing MemberDeleted event: memberId=$memberId, email=$email")
        // Implement business logic when member is deleted
        // e.g., archive data, cleanup related resources, etc.
    }
}
