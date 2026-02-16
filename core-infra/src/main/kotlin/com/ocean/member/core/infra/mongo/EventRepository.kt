package com.ocean.member.core.infra.mongo

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface EventRepository : MongoRepository<EventDocument, String> {

    fun findByAggregateIdOrderByCreatedAtDesc(aggregateId: String): List<EventDocument>

    fun findByEventTypeAndProcessed(eventType: String, processed: Boolean): List<EventDocument>
}
