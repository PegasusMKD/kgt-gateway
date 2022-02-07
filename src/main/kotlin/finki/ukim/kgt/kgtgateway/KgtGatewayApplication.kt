package finki.ukim.kgt.kgtgateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableDiscoveryClient
@SpringBootApplication
class KgtGatewayApplication

fun main(args: Array<String>) {
    runApplication<KgtGatewayApplication>(*args)
}
