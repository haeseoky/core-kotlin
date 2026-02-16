package com.ocean.member.core.infra.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import com.ocean.member.core.domain.model.event.MemberEvent
import com.ocean.member.core.domain.port.MemberEventPort
import com.ocean.member.core.infra.kafka.KafkaProducerService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Adapter for publishing member events to Kafka
 * Implements the domain port with infrastructure (Kafka)
 */
@Component
class MemberEventPublisher(
    private val kafkaProducerService: KafkaProducerService,
    private val objectMapper: ObjectMapper
) : MemberEventPort {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val topic = "member-events"

    override fun publish(event: MemberEvent) {
        try {
            val eventType = when (event) {
                is MemberEvent.MemberCreatedEvent -> "MemberCreated"
                is MemberEvent.MemberUpdatedEvent -> "MemberUpdated"
                is MemberEvent.MemberStatusChangedEvent -> "MemberStatusChanged"
                is MemberEvent.MemberDeletedEvent -> "MemberDeleted"
            }

            val payload = mapOf(
                "eventType" to eventType,
                "memberId" to event.memberId,
                "data" to when (event) {
                    is MemberEvent.MemberCreatedEvent -> mapOf(
                        "email" to event.email,
                        "name" to event.name,
                        "status" to event.status
                    )
                    is MemberEvent.MemberUpdatedEvent -> mapOf(
                        "oldName" to event.oldName,
                        "newName" to event.newName
                    )
                    is MemberEvent.MemberStatusChangedEvent -> mapOf(
                        "oldStatus" to event.oldStatus,
                        "newStatus" to event.newStatus
                    )
                    is MemberEvent.MemberDeletedEvent -> mapOf(
                        "email" to event.email
                    )
                },
                "occurredAt" to event.occurredAt.toString()
            )

            val key = "member-${event.memberId}"
            kafkaProducerService.send(topic, key, payload)
            logger.info("Event published successfully: $eventType for member ${event.memberId}")
        } catch (e: Exception) {
            logger.error("Failed to publish event: ${e.message}", e)
            throw e
        }
    }
}
