package com.galacsh.vvrite.share

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.concurrent.TimeoutException

private const val API_PATH = "/api"

@ControllerAdvice
class ApiExceptionInterceptor(val apiExceptionHandler: ApiExceptionHandler) {

    @ExceptionHandler
    fun handleException(ex: Exception, request: WebRequest): ResponseEntity<Any>? {
        if (isApiRequest(request)) {
            return apiExceptionHandler.handle(ex, request)
        }

        // API 예외가 아닌 경우 다시 throw 하여 에러 핸들링 체인을 타도록 함
        throw ex
    }

    private fun isApiRequest(request: WebRequest): Boolean {
        return getRequestUri(request).startsWith(API_PATH)
    }

    private fun getRequestUri(request: WebRequest) = when (request) {
        is ServletWebRequest -> request.request.requestURI
        else -> throw RuntimeException("Failed to find request URI.")
    }
}

@Component
class ApiExceptionHandler : ResponseEntityExceptionHandler() {
    private val log = logger {}

    fun handle(ex: Exception, request: WebRequest): ResponseEntity<Any>? {
        return try {
            handleException(ex, request)
        } catch (_: Exception) {
            try {
                handleUncommonException(ex, request)
            } catch (_: Exception) {
                fallback(ex, request)
            }
        }
    }

    /**
     * [ResponseEntityExceptionHandler.handleException] 에서 처리되지 않은 예외 중
     * 상세한 응답 내용이 필요한 예외들을 처리함.
     *
     * @throws ex 예상된 예외가 아닌 경우, 다시 throw
     */
    private fun handleUncommonException(ex: Exception, request: WebRequest): ResponseEntity<Any>? {
        return when (ex) {
            is TimeoutException -> {
                val body = createProblemDetail(
                    ex, HttpStatus.GATEWAY_TIMEOUT, "I/O Timeout", "Request failed due to I/O timeout", null, request
                )
                handleExceptionInternal(ex, body, HttpHeaders(), HttpStatus.GATEWAY_TIMEOUT, request)
            }

            else -> throw ex
        }
    }

    /**
     * [handleUncommonException]에서도 처리되지 않은 예외는 서버 내부 에러로 판단하고 간단한 응답 반환.
     * 대신, 에러 로그를 남기도록 함.
     */
    private fun fallback(ex: Exception, request: WebRequest): ResponseEntity<Any>? {
        log.error("Unexpected exception occurred.", ex)
        val body = createProblemDetail(
            ex, HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", null, null, request
        )
        return handleExceptionInternal(ex, body, HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request)
    }
}
