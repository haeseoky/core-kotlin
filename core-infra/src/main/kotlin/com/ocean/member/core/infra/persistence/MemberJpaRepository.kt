package com.ocean.member.core.infra.persistence

import com.ocean.member.core.infra.entity.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberJpaRepository : JpaRepository<MemberEntity, Long> {
    fun findByEmail(email: String): MemberEntity?
    fun existsByEmail(email: String): Boolean
}
