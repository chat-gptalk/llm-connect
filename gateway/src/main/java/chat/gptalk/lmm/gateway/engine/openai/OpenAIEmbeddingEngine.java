package chat.gptalk.lmm.gateway.engine.openai;

import chat.gptalk.lmm.gateway.engine.EmbeddingEngine;
import chat.gptalk.lmm.gateway.model.embedding.LLMEmbedding;
import chat.gptalk.lmm.gateway.model.embedding.LLMEmbeddingList;
import chat.gptalk.lmm.gateway.model.embedding.LLMEmbeddingRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenAIEmbeddingEngine implements EmbeddingEngine {

    private final OpenAiEmbeddingModel embeddingModel;

    @Override
    public Set<String> supportedModels() {
        return Arrays.stream(OpenAiApi.EmbeddingModel.values())
            .map(OpenAiApi.EmbeddingModel::getValue)
            .collect(Collectors.toSet());
    }

    @Override
    public EmbeddingModel embeddingModel() {
        return embeddingModel;
    }

    @Override
    public <T> EmbeddingRequest buildEmbeddingRequest(LLMEmbeddingRequest<T> llmEmbeddingRequest) {
        List<String> inputs;
        T input = llmEmbeddingRequest.input();
        if (input instanceof String str) {
            inputs = List.of(str);
        } else if (input instanceof List<?> list) {
            inputs = list.stream().map(it -> {
                if (it instanceof String str) {
                    return str;
                }
                throw new IllegalArgumentException("unsupported input type");
            }).toList();
        } else {
            throw new IllegalArgumentException("unsupported input type" + input.getClass().getName());
        }
        return new EmbeddingRequest(inputs, OpenAiEmbeddingOptions.builder()
            .model(llmEmbeddingRequest.model())
            .dimensions(llmEmbeddingRequest.dimensions())
            .encodingFormat(llmEmbeddingRequest.encodingFormat())
            .user(llmEmbeddingRequest.user())
            .build());
    }

    @Override
    public LLMEmbeddingList<LLMEmbedding> mapToLLMEmbeddingList(EmbeddingResponse embeddingResponse) {
        Usage usage = embeddingResponse.getMetadata().getUsage();
        OpenAiApi.Usage openaiUsage = new OpenAiApi.Usage(
            usage.getPromptTokens(),
            usage.getCompletionTokens(),
            usage.getTotalTokens()
        );
        return new LLMEmbeddingList<>(
            "list",
            embeddingResponse.getResults().stream().map(it -> {
                return new LLMEmbedding(
                    it.getIndex(), it.getOutput(), "embedding"
                );
            }).toList(),
            embeddingResponse.getMetadata().getModel(),
            openaiUsage
        );
    }

}
