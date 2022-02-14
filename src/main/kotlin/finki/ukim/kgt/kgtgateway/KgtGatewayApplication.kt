package finki.ukim.kgt.kgtgateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients
import reactivefeign.spring.config.EnableReactiveFeignClients


@EnableFeignClients
@EnableDiscoveryClient
@EnableReactiveFeignClients
@EnableConfigurationProperties
@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
class KgtGatewayApplication

fun main(args: Array<String>) {
    runApplication<KgtGatewayApplication>(*args)
}
