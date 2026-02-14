package com.ocean.member.core.domain.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MemberIdTest {

    @Test
    fun `should generate unique MemberId`() {
        val id1 = MemberId.generate()
        val id2 = MemberId.generate()

        assertTrue(id1.value() > 0)
        assertTrue(id2.value() > 0)
        assertTrue(id2.value() > id1.value())
    }

    @Test
    fun `should return correct value`() {
        val memberId = MemberId(12345L)
        assertEquals(12345L, memberId.value())
    }

    @Test
    fun `should generate sequential IDs`() {
        val ids = (1..10).map { MemberId.generate() }
        val values = ids.map { it.value() }

        assertTrue(values.distinct().size == 10)
        assertTrue(values.zip(values.drop(1)).all { (a, b) -> b > a })
    }
}
