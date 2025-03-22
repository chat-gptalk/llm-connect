package chat.gptalk.lmm.gateway.config;

import chat.gptalk.lmm.gateway.security.ApiKeyAuthenticationToken;
import chat.gptalk.lmm.gateway.service.ApiKeysService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.FormLoginSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HttpBasicSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ApiKeysService apiKeysService;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(CsrfSpec::disable)
            .httpBasic(HttpBasicSpec::disable)
            .formLogin(FormLoginSpec::disable)
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .authorizeExchange(it -> it.anyExchange().authenticated())
            .addFilterAt(apiKeyAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }

    @Bean
    public AuthenticationWebFilter apiKeyAuthenticationFilter() {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(apiKeyAuthenticationManager());
        filter.setServerAuthenticationConverter(apiKeyAuthenticationConverter());
        filter.setRequiresAuthenticationMatcher(new PathPatternParserServerWebExchangeMatcher("/**"));
        return filter;
    }

    @Bean
    public ReactiveAuthenticationManager apiKeyAuthenticationManager() {
        return authentication -> {
            ApiKeyAuthenticationToken token = (ApiKeyAuthenticationToken) authentication;
            return apiKeysService.isValidKey((String) token.getCredentials())
                .flatMap(it -> {
                    if (it) {
                        return Mono.just(new ApiKeyAuthenticationToken((String) token.getCredentials(), true));
                    }
                    return Mono.error(new BadCredentialsException("Invalid API Key"));
                });
        };
    }

    @Bean
    public ServerAuthenticationConverter apiKeyAuthenticationConverter() {
        return exchange -> {
            String apiKey = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (StringUtils.isBlank(apiKey)) {
                return Mono.error(new ResponseStatusException(HttpStatusCode.valueOf(401), "api key is required"));
            }
            if(!apiKey.startsWith("Bearer ")) {
                return Mono.error(new ResponseStatusException(HttpStatusCode.valueOf(401), "api key must start with 'Bearer '"));
            }
            return Mono.just(new ApiKeyAuthenticationToken(apiKey.substring("Bearer ".length())));
        };
    }
}
