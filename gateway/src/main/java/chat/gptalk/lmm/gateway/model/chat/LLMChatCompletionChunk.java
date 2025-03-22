package chat.gptalk.lmm.gateway.model.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionChunk.ChunkChoice;
import org.springframework.ai.openai.api.OpenAiApi.Usage;

@JsonInclude(Include.NON_NULL)
public record LLMChatCompletionChunk(
    @JsonProperty("id") String id,
    @JsonProperty("choices") List<ChunkChoice> choices,
    @JsonProperty("created") Long created,
    @JsonProperty("model") String model,
    @JsonProperty("service_tier") String serviceTier,
    @JsonProperty("system_fingerprint") String systemFingerprint,
    @JsonProperty("object") String object,
    @JsonProperty("usage") Usage usage) {

}
