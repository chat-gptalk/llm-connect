package chat.gptalk.lmm.gateway.model.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiApi.Usage;

@JsonInclude(Include.NON_NULL)
public record LLMChatCompletion(

    @JsonProperty("id")
    String id,
    @JsonProperty("choices")
    List<OpenAiApi.ChatCompletion.Choice> choices,
    @JsonProperty("created")
    Long created,
    @JsonProperty("model")
    String model,
    @JsonProperty("service_tier")
    String serviceTier,
    @JsonProperty("system_fingerprint")
    String systemFingerprint,
    @JsonProperty("object")
    String object,
    @JsonProperty("usage")
    Usage usage
) {

}
