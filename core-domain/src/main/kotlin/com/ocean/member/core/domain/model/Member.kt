package com.ocean.member.core.domain.model

import com.ocean.member.core.domain.model.valueobject.Email
import com.ocean.member.core.domain.model.valueobject.MemberStatus
import java.time.LocalDateTime
import java.util.Objects

class Member(
    val id: MemberId,
    val email: Email,
    var name: String,
    var status: MemberStatus,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime,
    var deletedAt: LocalDateTime?
) {

    companion object {
        fun create(email: Email, name: String): Member {
            val now = LocalDateTime.now()
            return Member(
                id = MemberId.generate(),
                email = email,
                name = name,
                status = MemberStatus.ACTIVE,
                createdAt = now,
                updatedAt = now,
                deletedAt = null
            )
        }

        fun restore(
            id: Long,
            email: Email,
            name: String,
            status: MemberStatus,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime,
            deletedAt: LocalDateTime?
        ): Member {
            return Member(
                id = MemberId.of(id),
                email = email,
                name = name.trim(),
                status = status,
                createdAt = createdAt,
                updatedAt = updatedAt,
                deletedAt = deletedAt
            )
        }
    }

    fun updateInformation(newName: String) {
        this.name = newName.trim()
        this.updatedAt = LocalDateTime.now()
    }

    fun changeStatus(newStatus: MemberStatus) {
        requireNotNull(newStatus) { "New status cannot be null" }
        require(this.status.canTransitTo(newStatus)) {
            "Cannot transition from ${this.status} to $newStatus"
        }
        this.status = newStatus
        this.updatedAt = LocalDateTime.now()
    }

    fun softDelete() {
        require(deletedAt == null) { "Member is already deleted" }
        this.status = MemberStatus.INACTIVE
        this.deletedAt = LocalDateTime.now()
        this.updatedAt = LocalDateTime.now()
    }

    fun isActive(): Boolean = status == MemberStatus.ACTIVE && deletedAt == null
    fun isDeleted(): Boolean = deletedAt != null
    fun getIdValue(): Long = id.value()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Member) return false
        return id == other.id
    }

    override fun hashCode(): Int = Objects.hash(id)

    override fun toString(): String {
        return "Member(id=$id, email=$email, name='$name', status=$status)"
    }
}
