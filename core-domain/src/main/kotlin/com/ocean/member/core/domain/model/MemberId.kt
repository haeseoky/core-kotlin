package com.ocean.member.core.domain.model

import java.util.concurrent.atomic.AtomicLong

@JvmInline
value class MemberId(private val value: Long) {
    companion object {
        private val counter = AtomicLong(System.currentTimeMillis())
        fun generate(): MemberId = MemberId(counter.incrementAndGet())
    }
    fun value(): Long = value
}
