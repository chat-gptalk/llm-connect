package chat.gptalk.lmm.gateway.model.image;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.ai.openai.api.OpenAiImageApi.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LLMImageResponse(
    @JsonProperty("created") Long created,
    @JsonProperty("data") List<Data> data) {

}
