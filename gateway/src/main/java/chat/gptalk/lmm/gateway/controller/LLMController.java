package chat.gptalk.lmm.gateway.controller;

import chat.gptalk.lmm.gateway.engine.ChatEngine;
import chat.gptalk.lmm.gateway.engine.EmbeddingEngine;
import chat.gptalk.lmm.gateway.engine.EngineFactory;
import chat.gptalk.lmm.gateway.engine.ImageEngine;
import chat.gptalk.lmm.gateway.engine.ModerationEngine;
import chat.gptalk.lmm.gateway.model.chat.LLMChatCompletionRequest;
import chat.gptalk.lmm.gateway.model.embedding.LLMEmbedding;
import chat.gptalk.lmm.gateway.model.embedding.LLMEmbeddingList;
import chat.gptalk.lmm.gateway.model.embedding.LLMEmbeddingRequest;
import chat.gptalk.lmm.gateway.model.image.LLMImageRequest;
import chat.gptalk.lmm.gateway.model.image.LLMImageResponse;
import chat.gptalk.lmm.gateway.model.moderation.LLMModerationRequest;
import chat.gptalk.lmm.gateway.model.moderation.LLMModerationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class LLMController {

    private final EngineFactory engineFactory;

    @PostMapping("/v1/chat/completions")
    public ResponseEntity<?> chatCompletions(@RequestBody LLMChatCompletionRequest chatCompletionRequest) {
        ChatEngine chatEngine = engineFactory.getChatEngine(chatCompletionRequest.model());
        if (Boolean.TRUE.equals(chatCompletionRequest.stream())) {
            return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_EVENT_STREAM_VALUE)
                .body(Flux.concat(chatEngine.chatCompletionStream(chatCompletionRequest)
                        .map(it -> ServerSentEvent.builder(it).build()),
                    Flux.just(ServerSentEvent.builder("[DONE]").build()))
                );
        }
        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(chatEngine.chatCompletion(chatCompletionRequest));
    }

    @PostMapping("/v1/embeddings")
    public ResponseEntity<Mono<LLMEmbeddingList<LLMEmbedding>>> embeddings(
        @RequestBody LLMEmbeddingRequest<?> embeddingRequest) {
        EmbeddingEngine embeddingEngine = engineFactory.getEmbeddingEngine(embeddingRequest.model());
        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(embeddingEngine.embed(embeddingRequest));
    }

    @PostMapping("/v1/images/generations")
    public ResponseEntity<Mono<LLMImageResponse>> imageGenerations(
        @RequestBody LLMImageRequest llmImageRequest) {
        ImageEngine imageEngine = engineFactory.getImageEngine(llmImageRequest.model());
        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(imageEngine.generation(llmImageRequest));
    }

    @PostMapping("/v1/moderations")
    public ResponseEntity<Mono<LLMModerationResponse>> moderations(
        @RequestBody LLMModerationRequest request) {
        ModerationEngine moderationEngine = engineFactory.getModerationEngine(request.model());
        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body(moderationEngine.createModeration(request));
    }
}
