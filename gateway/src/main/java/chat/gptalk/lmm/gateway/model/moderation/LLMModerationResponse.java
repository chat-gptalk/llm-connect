package chat.gptalk.lmm.gateway.model.moderation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.openai.api.OpenAiModerationApi.OpenAiModerationResult;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LLMModerationResponse(
    @JsonProperty("id") String id,
    @JsonProperty("model") String model,
    @JsonProperty("results") OpenAiModerationResult[] results) {

}
