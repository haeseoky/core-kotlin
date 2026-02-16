package com.ocean.member.core.infra

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories(basePackages = ["com.ocean.member.core.infra.persistence"])
@EntityScan(basePackages = ["com.ocean.member.core.infra.entity"])
class MemberInfraTestConfig
