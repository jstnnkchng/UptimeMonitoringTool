package org.jc.uptimemonitor.controller

import io.swagger.v3.oas.annotations.Hidden
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@Hidden
@RestControllerAdvice
class ControllerAdvice {

    private val logger = LoggerFactory.getLogger(ControllerAdvice::class.java)

    @ExceptionHandler(
        HttpMessageNotReadableException::class,
        IllegalArgumentException::class,
        UnsupportedOperationException::class
    )
    fun badRequestHandler(ex: Exception): ResponseEntity<Map<String, String?>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(mapOf("error" to ex.message))
    }

    @ExceptionHandler(DataAccessException::class)
    fun databaseErrorHandler(ex: DataAccessException): ResponseEntity<Map<String, String?>> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(mapOf("error" to "A database error occurred"))
    }

    @ExceptionHandler(Exception::class)
    fun internalServerErrorHandler(ex: Exception): ResponseEntity<Map<String, String?>> {
        logger.error(ex.message, ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(mapOf("error" to "An unexpected error occurred"))
    }
}