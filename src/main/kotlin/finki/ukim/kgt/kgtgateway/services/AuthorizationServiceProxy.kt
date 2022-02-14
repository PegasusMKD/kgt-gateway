package finki.ukim.kgt.kgtgateway.services

import feign.RequestLine
import finki.ukim.kgt.kgtgateway.models.SpringUser
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import reactivefeign.spring.config.ReactiveFeignClient
import reactor.core.publisher.Mono

@Service
@ReactiveFeignClient(name = "authorization")
interface AuthorizationServiceProxy {
    @PostMapping("/authorization/user-info")
    fun getUser(@RequestHeader(name = "Authorization") token: String): Mono<SpringUser?>
}