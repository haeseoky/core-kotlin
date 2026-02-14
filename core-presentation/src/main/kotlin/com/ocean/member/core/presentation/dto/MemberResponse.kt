package com.ocean.member.core.presentation.dto

import com.ocean.member.core.domain.model.valueobject.MemberStatus

data class MemberResponse(
    val id: Long,
    val email: String,
    val name: String,
    val status: MemberStatus,
    val createdAt: String,
    val updatedAt: String
)
