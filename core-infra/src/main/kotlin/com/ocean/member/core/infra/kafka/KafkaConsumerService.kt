package com.ocean.member.core.infra.kafka

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ocean.member.core.infra.adapter.MemberEventConsumerImpl
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["infra.kafka.enabled"], havingValue = "true", matchIfMissing = true)
class KafkaConsumerService(
    private val memberEventConsumer: MemberEventConsumerImpl
) {

    private val objectMapper = jacksonObjectMapper()

    @KafkaListener(
        topics = ["member-events"],
        groupId = "member-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun listen(
        @Payload message: Any,
        @Header(KafkaHeaders.RECEIVED_KEY) key: String?,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String?,
        acknowledgment: Acknowledgment?
    ) {
        try {
            val payload = objectMapper.convertValue(message, Map::class.java) as Map<String, Any>
            val eventType = payload["eventType"] as? String ?: "Unknown"
            val data = payload["data"] as? Map<*, *> ?: emptyMap<String, Any>()
            val memberId = (payload["memberId"] as? Number)?.toLong() ?: 0

            when (eventType) {
                "MemberCreated" -> {
                    val email = data["email"] as? String ?: ""
                    val name = data["name"] as? String ?: ""
                    memberEventConsumer.onMemberCreated(memberId, email, name)
                }
                "MemberUpdated" -> {
                    val oldName = data["oldName"] as? String
                    val newName = data["newName"] as? String ?: ""
                    memberEventConsumer.onMemberUpdated(memberId, oldName, newName)
                }
                "MemberStatusChanged" -> {
                    val oldStatus = data["oldStatus"] as? String ?: ""
                    val newStatus = data["newStatus"] as? String ?: ""
                    memberEventConsumer.onMemberStatusChanged(memberId, oldStatus, newStatus)
                }
                "MemberDeleted" -> {
                    val email = data["email"] as? String ?: ""
                    memberEventConsumer.onMemberDeleted(memberId, email)
                }
                else -> {
                    println("Unknown event type: $eventType")
                }
            }

            acknowledgment?.acknowledge()
        } catch (e: Exception) {
            println("Error processing message: ${e.message}")
            // Implement error handling or DLQ logic
            throw e
        }
    }
}
