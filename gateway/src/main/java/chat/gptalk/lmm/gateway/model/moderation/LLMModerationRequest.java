package chat.gptalk.lmm.gateway.model.moderation;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LLMModerationRequest(
    @JsonProperty("input") String prompt,
    @JsonProperty("model") String model
) {

}
