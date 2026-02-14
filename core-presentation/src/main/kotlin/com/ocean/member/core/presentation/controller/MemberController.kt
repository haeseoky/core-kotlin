package com.ocean.member.core.presentation.controller

import com.ocean.member.core.application.service.MemberCommandService
import com.ocean.member.core.application.service.MemberQueryService
import com.ocean.member.core.presentation.dto.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/members")
class MemberController(
    private val commandService: MemberCommandService,
    private val queryService: MemberQueryService
) {

    @PostMapping
    fun createMember(@Valid @RequestBody request: MemberRequest): ResponseEntity<MemberResponse> {
        val member = commandService.createMember(request.email, request.name)
        return ResponseEntity.status(HttpStatus.CREATED).body(member.toResponse())
    }

    @GetMapping("/{id}")
    fun getMember(@PathVariable id: Long): ResponseEntity<MemberResponse> {
        val member = queryService.getMemberById(id)
        return ResponseEntity.ok(member.toResponse())
    }

    @GetMapping
    fun getAllMembers(): ResponseEntity<List<MemberResponse>> {
        val members = queryService.getAllMembers()
        return ResponseEntity.ok(members.map { it.toResponse() })
    }

    @PatchMapping("/{id}")
    fun updateMember(
        @PathVariable id: Long,
        @Valid @RequestBody request: MemberRequest
    ): ResponseEntity<MemberResponse> {
        val member = commandService.updateMemberInformation(id, request.name)
        return ResponseEntity.ok(member.toResponse())
    }

    @PatchMapping("/{id}/status")
    fun changeStatus(
        @PathVariable id: Long,
        @Valid @RequestBody request: MemberStatusUpdateRequest
    ): ResponseEntity<MemberResponse> {
        val member = commandService.changeMemberStatus(id, request.status)
        return ResponseEntity.ok(member.toResponse())
    }

    @DeleteMapping("/{id}")
    fun deleteMember(@PathVariable id: Long): ResponseEntity<Void> {
        commandService.deleteMember(id)
        return ResponseEntity.noContent().build()
    }
}

fun com.ocean.member.core.domain.model.Member.toResponse(): MemberResponse {
    return MemberResponse(
        id = getIdValue(),
        email = email.value(),
        name = name,
        status = status,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString()
    )
}
