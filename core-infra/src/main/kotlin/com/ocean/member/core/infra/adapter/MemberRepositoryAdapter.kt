package com.ocean.member.core.infra.adapter

import com.ocean.member.core.domain.model.Member
import com.ocean.member.core.domain.model.repository.MemberRepository
import com.ocean.member.core.infra.entity.MemberEntity
import com.ocean.member.core.infra.persistence.MemberJpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class MemberRepositoryAdapter(
    private val jpaRepository: MemberJpaRepository
) : MemberRepository {

    override fun save(member: Member): Member {
        val entity = MemberEntity.fromDomain(member)
        val saved = jpaRepository.save(entity)
        return saved.toDomain()
    }

    override fun findById(id: Long): Optional<Member> {
        return jpaRepository.findById(id).map { it.toDomain() }
    }

    override fun findAll(): List<Member> {
        return jpaRepository.findAll().map { it.toDomain() }
    }

    override fun existsByEmail(email: String): Boolean {
        return jpaRepository.existsByEmail(email)
    }

    override fun deleteById(id: Long) {
        return jpaRepository.deleteById(id)
    }
}
