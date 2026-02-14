package com.ocean.member.core.infra.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "members")
class MemberEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true, length = 255)
    var email: String,

    @Column(nullable = false, length = 100)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: com.ocean.member.core.domain.model.valueobject.MemberStatus,

    @Column(nullable = false, name = "created_at")
    var createdAt: LocalDateTime,

    @Column(nullable = false, name = "updated_at")
    var updatedAt: LocalDateTime,

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null
) {
    @PrePersist
    fun prePersist() {
        val now = LocalDateTime.now()
        createdAt = now
        updatedAt = now
    }

    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    fun toDomain(): Member {
        return com.ocean.member.core.domain.model.Member.restore(
            id = id ?: throw IllegalStateException("ID cannot be null"),
            email = com.ocean.member.core.domain.model.valueobject.Email.of(email),
            name = name,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt
        )
    }
}
