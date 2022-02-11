package finki.ukim.kgt.kgtgateway.filters.gateway

import finki.ukim.kgt.kgtgateway.filters.config.AuthenticationFilterConfig
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.security.Key
import java.util.*

@Component
class AuthenticationFilter() :
    AbstractGatewayFilterFactory<AuthenticationFilterConfig>(AuthenticationFilterConfig::class.java) {

    @Value("\${jwt.signing-key}")
    lateinit var secret: String
    lateinit var key: Key

    val logger: Logger = LoggerFactory.getLogger(AuthenticationFilter::class.java)

    val authorizationHeader: String = "Authorization"

    override fun apply(config: AuthenticationFilterConfig?): GatewayFilter {
        return GatewayFilter { exchange, chain ->
            if (!exchange.request.headers.containsKey(authorizationHeader))
                return@GatewayFilter onError(config, exchange)

            val headerValue = exchange.request.headers.getValue(authorizationHeader)

            if (headerValue.size != 1)
                return@GatewayFilter onError(config, exchange)

            val token = getToken(headerValue[0])
            val expired = isTokenExpired(token)
            if (expired != null && expired) {
                return@GatewayFilter onError(config, exchange)
            }

            chain.filter(exchange)
        }
    }

    private fun getAllClaimsFromToken(token: String?): Claims? {
        key = Keys.hmacShaKeyFor(secret.toByteArray())
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body
    }

    private fun isTokenExpired(token: String): Boolean? {
        return getAllClaimsFromToken(token)?.expiration?.before(Date())
    }

    private fun getToken(header: String): String {
        return header.split(" ")[1]
    }

    private fun onError(config: AuthenticationFilterConfig?, exchange: ServerWebExchange): Mono<Void> {
        logger.error("Someone tried to access a locked resource!")
        exchange.response.rawStatusCode = config?.errorCode
        exchange.response.headers.set("Error", config?.errorMessage)
        return exchange.response.setComplete()
    }

}