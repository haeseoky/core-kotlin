package com.ocean.member.core.domain.port

import com.ocean.member.core.domain.model.Member

interface MemberEventPort {
    fun publishMemberCreated(member: Member)
    fun publishMemberStatusChanged(member: Member)
}
