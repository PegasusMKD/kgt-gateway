package finki.ukim.kgt.kgtgateway.configs

import feign.FeignException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

import javax.servlet.http.HttpServletResponse


@ControllerAdvice
class FeignExceptionHandler {
    @ExceptionHandler(FeignException::class)
    fun handleFeignStatusException(e: FeignException, response: HttpServletResponse): String {
        response.status = e.status()
        return "feignError"
    }
}