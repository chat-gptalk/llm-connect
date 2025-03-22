package chat.gptalk.lmm.gateway.engine;

import chat.gptalk.lmm.gateway.model.embedding.LLMEmbedding;
import chat.gptalk.lmm.gateway.model.embedding.LLMEmbeddingList;
import chat.gptalk.lmm.gateway.model.embedding.LLMEmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public interface EmbeddingEngine extends Engine {

    EmbeddingModel embeddingModel();

    default <T> Mono<LLMEmbeddingList<LLMEmbedding>> embed(LLMEmbeddingRequest<T> llmEmbeddingRequest) {
        EmbeddingRequest embeddingRequest = buildEmbeddingRequest(llmEmbeddingRequest);
        return Mono.fromCallable(() -> mapToLLMEmbeddingList(embeddingModel().call(embeddingRequest)))
            .subscribeOn(Schedulers.boundedElastic());
    }

    <T> EmbeddingRequest buildEmbeddingRequest(LLMEmbeddingRequest<T> llmEmbeddingRequest);

    LLMEmbeddingList<LLMEmbedding> mapToLLMEmbeddingList(EmbeddingResponse embeddingResponse);
}
