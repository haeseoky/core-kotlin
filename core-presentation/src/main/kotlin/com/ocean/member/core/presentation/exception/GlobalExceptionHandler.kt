package com.ocean.member.core.presentation.exception

import com.ocean.member.core.presentation.dto.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(
        ex: IllegalArgumentException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            message = ex.message ?: "Invalid request",
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(
        ex: IllegalStateException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            error = "Conflict",
            message = ex.message ?: "Invalid state",
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error)
    }
}
