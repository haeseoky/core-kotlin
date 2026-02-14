package com.ocean.member.core.domain.model.valueobject

@JvmInline
value class Email(val value: String) {
    companion object {
        private val EMAIL_REGEX = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")

        fun of(email: String?): Email {
            requireNotNull(email) { "Email value cannot be null" }
            require(email.isNotBlank()) { "Email cannot be null or blank" }

            val trimmed = email.trim().lowercase()
            require(isValidEmail(trimmed)) { "Invalid email format: $email" }

            return Email(trimmed)
        }

        private fun isValidEmail(email: String): Boolean {
            if (".." in email) return false
            return EMAIL_REGEX.matches(email)
        }
    }
}
