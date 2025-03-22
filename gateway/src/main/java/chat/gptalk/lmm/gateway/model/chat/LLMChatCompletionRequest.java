package chat.gptalk.lmm.gateway.model.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionMessage;
import org.springframework.ai.openai.api.OpenAiApi.FunctionTool;
import org.springframework.ai.openai.api.OpenAiApi.OutputModality;
import org.springframework.ai.openai.api.ResponseFormat;

@JsonInclude(Include.NON_NULL)
public record LLMChatCompletionRequest(
    @JsonProperty("messages")
    List<ChatCompletionMessage> messages,
    @JsonProperty("model")
    String model,
    @JsonProperty("store")
    Boolean store,
    @JsonProperty("metadata")
    Map<String, String> metadata,
    @JsonProperty("frequency_penalty")
    Double frequencyPenalty,
    @JsonProperty("logit_bias")
    Map<String, Integer> logitBias,
    @JsonProperty("logprobs")
    Boolean logprobs,
    @JsonProperty("top_logprobs")
    Integer topLogprobs,
    @JsonProperty("max_tokens")
    @Deprecated
    Integer maxTokens, // Use maxCompletionTokens instead
    @JsonProperty("max_completion_tokens")
    Integer maxCompletionTokens,
    @JsonProperty("n")
    Integer n,
    @JsonProperty("modalities")
    List<OutputModality> outputModalities,
    @JsonProperty("audio")
    OpenAiApi.ChatCompletionRequest.AudioParameters audioParameters,
    @JsonProperty("presence_penalty")
    Double presencePenalty,
    @JsonProperty("response_format")
    ResponseFormat responseFormat,
    @JsonProperty("seed")
    Integer seed,
    @JsonProperty("service_tier")
    String serviceTier,
    @JsonProperty("stop")
    List<String> stop,
    @JsonProperty("stream")
    Boolean stream,
    @JsonProperty("stream_options")
    OpenAiApi.ChatCompletionRequest.StreamOptions streamOptions,
    @JsonProperty("temperature")
    Double temperature,
    @JsonProperty("top_p")
    Double topP,
    @JsonProperty("tools")
    List<FunctionTool> tools,
    @JsonProperty("tool_choice")
    Object toolChoice,
    @JsonProperty("parallel_tool_calls")
    Boolean parallelToolCalls,
    @JsonProperty("user")
    String user,
    @JsonProperty("reasoning_effort")
    String reasoningEffort,
    @JsonProperty("model_options")
    Map<String, Object> modelOptions) {
}