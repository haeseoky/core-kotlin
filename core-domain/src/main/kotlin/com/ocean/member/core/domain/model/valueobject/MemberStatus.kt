package com.ocean.member.core.domain.model.valueobject

enum class MemberStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED;

    fun canTransitTo(newStatus: MemberStatus): Boolean {
        return when (this) {
            ACTIVE -> newStatus == INACTIVE || newStatus == SUSPENDED
            INACTIVE -> newStatus == SUSPENDED
            SUSPENDED -> newStatus == ACTIVE || newStatus == INACTIVE
        }
    }
}
