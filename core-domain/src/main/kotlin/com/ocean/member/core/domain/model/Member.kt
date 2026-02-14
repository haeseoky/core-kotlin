package com.ocean.member.core.domain.model

import com.ocean.member.core.domain.model.valueobject.Email
import com.ocean.member.core.domain.model.valueobject.MemberStatus

class Member(
    val id: MemberId,
    val email: Email,
    val name: String,
    val status: MemberStatus
) {
    fun isActive(): Boolean = status == MemberStatus.ACTIVE
}
