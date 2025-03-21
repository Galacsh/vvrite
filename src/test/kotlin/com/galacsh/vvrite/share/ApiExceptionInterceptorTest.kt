package com.galacsh.vvrite.share

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.ResponseEntity
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest

class ApiExceptionInterceptorTest {

    private lateinit var apiExceptionHandler: ApiExceptionHandler
    private lateinit var apiExceptionInterceptor: ApiExceptionInterceptor
    private lateinit var request: WebRequest
    private lateinit var servletRequest: HttpServletRequest

    @BeforeEach
    fun setUp() {
        apiExceptionHandler = mockk()
        apiExceptionInterceptor = ApiExceptionInterceptor(apiExceptionHandler)
        servletRequest = mockk()
        request = ServletWebRequest(servletRequest)
    }

    @Test
    fun `API 경로 요청에서 발생한 예외는 API 예외 핸들러가 처리함`() {
        // Given
        val exception = RuntimeException("Test exception")
        every { servletRequest.requestURI } returns "/api/test"
        every { apiExceptionHandler.handle(exception, request) } returns ResponseEntity.ok().build()

        // When
        apiExceptionInterceptor.handleException(exception, request)

        // Then
        verify { apiExceptionHandler.handle(exception, request) }
    }

    @Test
    fun `API 경로가 아닌 요청에서 발생한 예외는 API 예외 핸들러가 처리하지 않음`() {
        // Given
        val exception = RuntimeException("Test exception")
        every { servletRequest.requestURI } returns "/non-api"
        every { apiExceptionHandler.handle(exception, request) } returns ResponseEntity.ok().build()

        // When
        assertThrows<RuntimeException> {
            apiExceptionInterceptor.handleException(exception, request)
        }

        // Then
        verify(exactly = 0) { apiExceptionHandler.handle(exception, request) }
    }
}
