package com.ocean.member.core.domain.port

import com.ocean.member.core.domain.model.event.MemberEvent

/**
 * Port interface for publishing member domain events
 * Infrastructure layer will implement this port with Kafka, RabbitMQ, etc.
 */
interface MemberEventPort {
    fun publish(event: MemberEvent)
}
