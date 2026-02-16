package com.ocean.member.core.infra

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(
    exclude = [
        RedisAutoConfiguration::class,
        KafkaAutoConfiguration::class,
        MongoDataAutoConfiguration::class
    ]
)
@EnableJpaRepositories(basePackages = ["com.ocean.member.core.infra.persistence"])
@EntityScan(basePackages = ["com.ocean.member.core.infra.entity"])
@ComponentScan(
    basePackages = ["com.ocean.member.core.infra"],
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = [".*\\.config\\..*"]
        )
    ]
)
class MemberInfraTestConfig
