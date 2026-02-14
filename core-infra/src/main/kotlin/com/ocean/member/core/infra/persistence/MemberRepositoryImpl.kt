package com.ocean.member.core.infra.persistence

import com.ocean.member.core.domain.model.Member
import com.ocean.member.core.domain.model.repository.MemberRepository
import com.ocean.member.core.infra.entity.MemberEntity
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface MemberRepositoryImpl : org.springframework.data.jpa.repository.JpaRepository<MemberEntity, Long> {
    fun existsByEmail(email: String): Boolean
    fun toDomainRepository(): MemberRepository {
        return object : MemberRepository {
            override fun save(member: Member): Member {
                val entity = toEntity(member)
                val saved = this@MemberRepositoryImpl.save(entity)
                return saved.toDomain()
            }
            override fun findById(id: Long): Optional<Member> {
                return this@MemberRepositoryImpl.findById(id).map { it.toDomain() }
            }
            override fun findAll(): List<Member> {
                return this@MemberRepositoryImpl.findAll().map { it.toDomain() }
            }
            override fun existsByEmail(email: String): Boolean {
                return this@MemberRepositoryImpl.existsByEmail(email)
            }
            override fun deleteById(id: Long) {
                this@MemberRepositoryImpl.deleteById(id)
            }
        }
    }
    private fun toEntity(member: Member): MemberEntity {
        return MemberEntity().apply {
            id = member.getIdValue()
            email = member.email.value()
            name = member.name
            status = member.status
            createdAt = member.createdAt
            updatedAt = member.updatedAt
            deletedAt = member.deletedAt
        }
    }
}
