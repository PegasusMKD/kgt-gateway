package finki.ukim.kgt.kgtgateway.filters.gateway

import finki.ukim.kgt.kgtgateway.filters.config.AuthenticationFilterConfig
import finki.ukim.kgt.kgtgateway.services.AuthorizationServiceProxy
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AuthenticationFilter(@Lazy private val authorizationServiceProxy: AuthorizationServiceProxy) :
    AbstractGatewayFilterFactory<AuthenticationFilterConfig>(AuthenticationFilterConfig::class.java) {

    val logger: Logger = LoggerFactory.getLogger(AuthenticationFilter::class.java)

    val authorizationHeader: String = "Authorization"

    override fun apply(config: AuthenticationFilterConfig?): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            if (!exchange.request.headers.containsKey(authorizationHeader))
                return@GatewayFilter onError(config, exchange)

            val headerValue = exchange.request.headers.getValue(authorizationHeader)

            if (headerValue.size != 1)
                return@GatewayFilter onError(config, exchange)

            try {
                authorizationServiceProxy.getUser(headerValue[0]).flatMap {
                    if (it == null)
                        return@flatMap onError(config, exchange)

                    exchange.request.mutate()
                        .header("Roles", it.roles.joinToString(","))
                        .header("User-ID", it.id)
                        .build()
                    chain.filter(exchange)
                }
            } catch (e: Exception) {
                return@GatewayFilter onError(config, exchange)
            }

        }
    }

    private fun onError(config: AuthenticationFilterConfig?, exchange: ServerWebExchange): Mono<Void> {
        logger.error("Someone tried to access a locked resource!")
        exchange.response.rawStatusCode = config?.errorCode
        exchange.response.headers.set("Error", config?.errorMessage)
        return exchange.response.setComplete()
    }

}