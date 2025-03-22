package chat.gptalk.lmm.gateway.model.embedding;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.ai.openai.api.OpenAiApi.Usage;

@JsonInclude(Include.NON_NULL)
public record LLMEmbeddingList<T>(
    @JsonProperty("object") String object,
    @JsonProperty("data") List<T> data,
    @JsonProperty("model") String model,
    @JsonProperty("usage") Usage usage) {

}