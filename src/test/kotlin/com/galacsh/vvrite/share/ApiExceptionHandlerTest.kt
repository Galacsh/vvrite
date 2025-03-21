package com.galacsh.vvrite.share

import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.ErrorResponseException
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import java.util.concurrent.TimeoutException
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ApiExceptionHandlerTest {
    private lateinit var apiExceptionHandler: ApiExceptionHandler
    private lateinit var request: WebRequest
    private lateinit var servletRequest: HttpServletRequest

    @BeforeEach
    fun setUp() {
        apiExceptionHandler = ApiExceptionHandler()
        servletRequest = mockk()
        request = ServletWebRequest(servletRequest)
    }

    @Test
    fun `ErrorResponseException 등은 기본 예외 처리기가 처리`() {
        // Given
        val exception = ErrorResponseException(HttpStatus.UNAUTHORIZED)

        // When
        val response = apiExceptionHandler.handle(exception, request)

        // Then
        assertNotNull(response)
        assertTrue(response.body is ProblemDetail)
        assertEquals(response.statusCode, HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `기본 예외 처리기가 처리하지 못하면 uncommon 예외로 판단하고 처리`() {
        // Given
        val exception = TimeoutException("어디선가 타임 아웃 발생")

        // When
        val response = apiExceptionHandler.handle(exception, request)

        // Then
        assertNotNull(response)
        assertTrue(response.body is ProblemDetail)
        assertEquals(response.statusCode, HttpStatus.GATEWAY_TIMEOUT)
    }

    @Test
    fun `알 수 없는 예외는 500 코드의 ProblemDetail 로 반환`() {
        // Given
        val exception = SomeUnexpectedException("처음 보는 예외 발생")

        // When
        val response = apiExceptionHandler.handle(exception, request)

        // Then
        assertNotNull(response)
        assertTrue(response.body is ProblemDetail)
        assertEquals(response.statusCode, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    class SomeUnexpectedException(msg: String) : RuntimeException(msg)
}
