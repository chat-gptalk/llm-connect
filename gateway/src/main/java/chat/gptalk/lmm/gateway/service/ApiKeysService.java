package chat.gptalk.lmm.gateway.service;

import chat.gptalk.lmm.gateway.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ApiKeysService {

    private final ApiKeyRepository apiKeyRepository;

    public Mono<Boolean> isValidKey(String key) {
        return apiKeyRepository.existsByHashedKey(key);
    }
}
