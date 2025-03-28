package chat.gptalk.lmm.gateway.model.embedding;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public record LLMEmbedding(
    @JsonProperty("index") Integer index,
    @JsonProperty("embedding") float[] embedding,
    @JsonProperty("object") String object) {
}
