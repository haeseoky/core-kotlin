package com.ocean.member.core.infra.kafka

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture

@Service
@ConditionalOnProperty(name = ["infra.kafka.enabled"], havingValue = "true", matchIfMissing = true)
class KafkaProducerService(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    fun send(topic: String, key: String, message: Any): CompletableFuture<SendResult<String, Any>> {
        return kafkaTemplate.send(topic, key, message)
    }

    fun send(topic: String, message: Any): CompletableFuture<SendResult<String, Any>> {
        return kafkaTemplate.send(topic, message)
    }
}
