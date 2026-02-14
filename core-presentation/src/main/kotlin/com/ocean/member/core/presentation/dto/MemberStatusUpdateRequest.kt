package com.ocean.member.core.presentation.dto

import com.ocean.member.core.domain.model.valueobject.MemberStatus
import jakarta.validation.constraints.NotNull

data class MemberStatusUpdateRequest(
    @field:NotNull(message = "Status is required")
    val status: MemberStatus
)
