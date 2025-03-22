package chat.gptalk.lmm.gateway.model.speech;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.openai.api.OpenAiAudioApi.SpeechRequest.AudioResponseFormat;
import org.springframework.ai.openai.api.OpenAiAudioApi.SpeechRequest.Voice;

@JsonInclude(Include.NON_NULL)
public record LLMSpeechRequest(
    @JsonProperty("model") String model,
    @JsonProperty("input") String input,
    @JsonProperty("voice") Voice voice,
    @JsonProperty("response_format") AudioResponseFormat responseFormat,
    @JsonProperty("speed") Float speed) {

}
