package com.ocean.member.core.application.port

interface MemberEventConsumer {
    fun onMemberCreated(memberId: Long, email: String, name: String)
    fun onMemberStatusChanged(memberId: Long, oldStatus: String, newStatus: String)
}
