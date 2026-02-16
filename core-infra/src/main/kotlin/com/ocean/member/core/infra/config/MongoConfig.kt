package com.ocean.member.core.infra.config

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(basePackages = ["com.ocean.member.core.infra.mongo"])
@ConditionalOnProperty(name = ["infra.mongo.enabled"], havingValue = "true", matchIfMissing = true)
class MongoConfig : AbstractMongoClientConfiguration() {

    override fun mongoClient(): MongoClient {
        return MongoClients.create("mongodb://root:root@localhost:27017")
    }

    override fun getDatabaseName(): String {
        return "member_db"
    }

    @Bean
    fun mongoTemplate(): MongoTemplate {
        return MongoTemplate(mongoClient(), databaseName)
    }
}
