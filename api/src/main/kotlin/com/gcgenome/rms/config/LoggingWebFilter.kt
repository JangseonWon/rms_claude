import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class LoggingWebFilter : WebFilter {
    private val logger = LoggerFactory.getLogger(LoggingWebFilter::class.java)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request
        val method = request.method
        val path = request.uri.path

        val startTime = System.currentTimeMillis()

        logger.info("Receive request: $method $path")
        return chain.filter(exchange)
            .doOnSuccess {
                val duration = System.currentTimeMillis() - startTime
                logger.info("Completed request: $method $path duration=${duration}ms")
            }
            .doOnError { error ->
                val duration = System.currentTimeMillis() - startTime
                val statusCode = exchange.response.statusCode?.value() ?: "unknown"
                logger.error("Failed request: $method $path status=${statusCode} duration=${duration}ms error=${error.message}")
            }
    }
}