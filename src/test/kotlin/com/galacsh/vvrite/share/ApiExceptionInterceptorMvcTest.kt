package com.galacsh.vvrite.share

import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@WebMvcTest(controllers = [TestRestController::class])
@Import(ApiExceptionHandler::class)
class ApiExceptionInterceptorMvcTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `정상 요청 시 정상 응답`() {
        mockMvc.get("/api/known")
            .andExpect {
                status { isOk() }
                content { string("known") }
            }
    }

    @Test
    fun `존재하지 않는 API 경로 요청 시 404 ProblemDetails 응답`() {
        mockMvc.get("/api/unknown")
            .andExpect {
                status { isNotFound() }
                content {
                    contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    jsonPath("$.status") { isNumber() }
                    jsonPath("$.detail") { isString() }
                }
            }
    }

    @Test
    fun `존재하지 않는 페이지 요청 시 404 ProblemDetails X`() {
        mockMvc.get("/unknown")
            .andExpect {
                status { isNotFound() }
                content {
                    not(MediaType.APPLICATION_PROBLEM_JSON)
                }
            }
    }
}

@RestController
@RequestMapping("/api")
class TestRestController {
    @GetMapping("/known")
    fun known() = "known"
}
