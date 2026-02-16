package com.ocean.member.core.infra.mongo

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "member_events")
data class EventDocument(
    @Id
    val id: String? = null,

    @Indexed
    val aggregateId: String,

    val eventType: String,

    val eventData: String,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Indexed
    val processed: Boolean = false
)
