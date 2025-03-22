package chat.gptalk.lmm.gateway.engine;

import chat.gptalk.lmm.gateway.engine.advisor.LogAdvisor;
import chat.gptalk.lmm.gateway.engine.advisor.UsageAdvisor;
import chat.gptalk.lmm.gateway.model.chat.LLMChatCompletion;
import chat.gptalk.lmm.gateway.model.chat.LLMChatCompletionChunk;
import chat.gptalk.lmm.gateway.model.chat.LLMChatCompletionRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public interface ChatEngine extends Engine {

    ChatClient chatClient();

    default Mono<LLMChatCompletion> chatCompletion(LLMChatCompletionRequest completionRequest) {
        return Mono.fromCallable(() -> chatClient()
                .prompt(buildPrompt(completionRequest))
                .advisors(new UsageAdvisor(), new LogAdvisor())
                .call()
                .chatResponse()
            )
            .map(this::mapToLLMChatCompletion)
            .subscribeOn(Schedulers.boundedElastic());
    }

    default Flux<LLMChatCompletionChunk> chatCompletionStream(LLMChatCompletionRequest request) {
        return chatClient()
            .prompt(buildPrompt(request))
            .advisors(new UsageAdvisor(), new LogAdvisor())
            .stream()
            .chatResponse()
            .map(this::mapToLLMChatCompletionChunk);
    }

    Prompt buildPrompt(LLMChatCompletionRequest request);

    LLMChatCompletion mapToLLMChatCompletion(ChatResponse chatResponse);

    LLMChatCompletionChunk mapToLLMChatCompletionChunk(ChatResponse chatResponse);
}
