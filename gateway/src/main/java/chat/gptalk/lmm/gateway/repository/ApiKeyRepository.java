package chat.gptalk.lmm.gateway.repository;

import chat.gptalk.lmm.gateway.entity.ApiKeyEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ApiKeyRepository extends ReactiveCrudRepository<ApiKeyEntity, Integer> {

    Mono<Boolean> existsByHashedKey(String token);
}
