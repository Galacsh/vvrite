package com.galacsh.vvrite.share

import io.mockk.*
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import kotlin.test.assertEquals

/**
 * 외부에서 초기화 시 <클래스명>Kt 기준으로 로깅되는지 테스트하기 위한 로거
 */
val outsideLogger = logger {}

class LoggerUtilKtTest {

    @Test
    fun `클래스 내에서 선언하면 주어진 이름은 클래스명과 동일함`() {
        val innerLogger = logger { }
        assertEquals(this::class.qualifiedName, innerLogger.name)
    }

    @Test
    fun `클래스 밖에서 선언하면 주어진 이름은 클래스명 + Kt`() {
        assertEquals(this::class.qualifiedName + "Kt", outsideLogger.name)
    }

    @Test
    fun `error 레벨이면 error 로그 출력 시 출력되어야 함`() {
        val log = mockk<Logger>()
        every { log.isErrorEnabled } returns true
        every { log.error(any()) } just Runs

        log.error { "Error message" }

        verify { log.error("Error message") }
    }

    @Test
    fun `error 레벨이 아니면 error 로그 출력 시 출력되지 않아야 함`() {
        val log = mockk<Logger>()
        every { log.isErrorEnabled } returns false
        every { log.error(any()) } just Runs

        log.error { "Error message" }

        verify(exactly = 0) { log.error(any()) }
    }

    @Test
    fun `warn 레벨이면 warn 로그 출력 시 출력되어야 함`() {
        val log = mockk<Logger>()
        every { log.isWarnEnabled } returns true
        every { log.warn(any()) } just Runs

        log.warn { "Warn message" }

        verify { log.warn("Warn message") }
    }

    @Test
    fun `warn 레벨이 아니면 warn 로그 출력 시 출력되지 않아야 함`() {
        val log = mockk<Logger>()
        every { log.isWarnEnabled } returns false
        every { log.warn(any()) } just Runs

        log.warn { "Warn message" }

        verify(exactly = 0) { log.warn(any()) }
    }

    @Test
    fun `info 레벨이면 info 로그 출력 시 출력되어야 함`() {
        val log = mockk<Logger>()
        every { log.isInfoEnabled } returns true
        every { log.info(any()) } just Runs

        log.info { "Info message" }

        verify { log.info("Info message") }
    }

    @Test
    fun `info 레벨이 아니면 info 로그 출력 시 출력되지 않아야 함`() {
        val log = mockk<Logger>()
        every { log.isInfoEnabled } returns false
        every { log.info(any()) } just Runs

        log.info { "Info message" }

        verify(exactly = 0) { log.info(any()) }
    }

    @Test
    fun `debug 레벨이면 debug 로그 출력 시 출력되어야 함`() {
        val log = mockk<Logger>()
        every { log.isDebugEnabled } returns true
        every { log.debug(any()) } just Runs

        log.debug { "Debug message" }

        verify { log.debug("Debug message") }
    }

    @Test
    fun `debug 레벨이 아니면 debug 로그 출력 시 출력되지 않아야 함`() {
        val log = mockk<Logger>()
        every { log.isDebugEnabled } returns false
        every { log.debug(any()) } just Runs

        log.debug { "Debug message" }

        verify(exactly = 0) { log.debug(any()) }
    }

    @Test
    fun `trace 레벨이면 trace 로그 출력 시 출력되어야 함`() {
        val log = mockk<Logger>()
        every { log.isTraceEnabled } returns true
        every { log.trace(any()) } just Runs

        log.trace { "Trace message" }

        verify { log.trace("Trace message") }
    }

    @Test
    fun `trace 레벨이 아니면 trace 로그 출력 시 출력되지 않아야 함`() {
        val log = mockk<Logger>()
        every { log.isTraceEnabled } returns false
        every { log.trace(any()) } just Runs

        log.trace { "Trace message" }

        verify(exactly = 0) { log.trace(any()) }
    }
}
