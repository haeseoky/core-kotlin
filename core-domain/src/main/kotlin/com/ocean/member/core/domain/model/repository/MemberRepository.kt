package com.ocean.member.core.domain.model.repository

import com.ocean.member.core.domain.model.Member
import java.util.Optional

interface MemberRepository {
    fun save(member: Member): Member
    fun findById(id: Long): Optional<Member>
    fun findAll(): List<Member>
    fun existsByEmail(email: String): Boolean
    fun deleteById(id: Long)
}
