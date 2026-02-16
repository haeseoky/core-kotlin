package com.ocean.member.core.infra.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(name = ["infra.kafka.enabled"], havingValue = "true", matchIfMissing = true)
class KafkaConsumerService {

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
            println("Received message: $message from topic: $topic, key: $key")
            // Process message here
            acknowledgment?.acknowledge()
        } catch (e: Exception) {
            println("Error processing message: ${e.message}")
            // Implement error handling or DLQ logic
        }
    }
}
