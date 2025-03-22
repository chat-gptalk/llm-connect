package chat.gptalk.lmm.gateway.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Slf4j
@Order(-2)
@Component
public class CustomExceptionHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);

        log.error(ex.getMessage(), ex);
        String errorMessage = "Custom Error: " + ex.getMessage();
        byte[] bytes = errorMessage.getBytes();

        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
            .bufferFactory().wrap(bytes)));
    }
}
