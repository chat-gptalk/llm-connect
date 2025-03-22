package chat.gptalk.lmm.gateway.engine.openai;

import chat.gptalk.lmm.gateway.engine.ChatEngine;
import chat.gptalk.lmm.gateway.model.chat.LLMChatCompletion;
import chat.gptalk.lmm.gateway.model.chat.LLMChatCompletionChunk;
import chat.gptalk.lmm.gateway.model.chat.LLMChatCompletionRequest;
import chat.gptalk.lmm.gateway.util.JsonUtils;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.AssistantMessage.ToolCall;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage.ToolResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.Media;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletion.Choice;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionChunk.ChunkChoice;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionFinishReason;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionMessage;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionMessage.AudioOutput;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionMessage.ChatCompletionFunction;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionMessage.MediaContent;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionMessage.MediaContent.InputAudio;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionMessage.MediaContent.InputAudio.Format;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionMessage.Role;
import org.springframework.ai.openai.api.OpenAiApi.Usage;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class OpenAIChatEngine implements ChatEngine {

    private final ChatClient chatClient;

    public OpenAIChatEngine(OpenAiChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    @Override
    public ChatClient chatClient() {
        return chatClient;
    }

    @Override
    public Set<String> supportedModels() {
        return Arrays.stream(OpenAiApi.ChatModel.values())
            .map(OpenAiApi.ChatModel::getValue)
            .collect(Collectors.toSet());
    }

    @Override
    public Prompt buildPrompt(LLMChatCompletionRequest request) {
        List<Message> messages = request.messages().stream()
            .map(it -> {
                Pair<String, List<Media>> pair = convertRawContentToMedias(it.rawContent());
                return switch (it.role()) {
                    case USER -> new UserMessage(pair.getFirst(), pair.getSecond());
                    case SYSTEM -> new SystemMessage(pair.getFirst());
                    case ASSISTANT -> mapToAssistantMessage(it, pair.getFirst(), pair.getSecond());
                    case TOOL -> new ToolResponseMessage(
                        List.of(new ToolResponse(it.toolCallId(), it.name(), pair.getFirst())));
                };
            })
            .toList();
        return new Prompt(
            messages,
            OpenAiChatOptions.builder()
                .frequencyPenalty(request.frequencyPenalty())
                .internalToolExecutionEnabled(Boolean.TRUE)
                .logitBias(request.logitBias())
                .model(request.model())
                .maxTokens(request.maxCompletionTokens() == null ? request.maxTokens() : request.maxCompletionTokens())
                .metadata(request.metadata())
                .N(request.n())
                .outputAudio(request.audioParameters())
                .outputModalities(
                    request.outputModalities() != null ? request.outputModalities().stream().map(Enum::name).toList()
                        : null)
                .parallelToolCalls(request.parallelToolCalls())
                .reasoningEffort(request.reasoningEffort())
                .responseFormat(request.responseFormat())
                .seed(request.seed())
                .stop(request.stop())
                .store(request.store())
                .streamUsage(Boolean.TRUE.equals(request.stream()))
                .temperature(request.temperature())
                .topLogprobs(request.topLogprobs())
                .topP(request.topP())
                .toolChoice(request.toolChoice())
                .tools(request.tools())
                .user(request.user())
                .build()
        );
    }

    private Message mapToAssistantMessage(ChatCompletionMessage chatCompletionMessage, String text, List<Media> media) {
        Map<String, Object> meta = new HashMap<>();
        meta.put("refusal", chatCompletionMessage.refusal());
        List<ToolCall> toolCalls = chatCompletionMessage.toolCalls()
            .stream()
            .map(it -> new ToolCall(it.id(), it.type(), it.function().name(), it.function().arguments()))
            .collect(Collectors.toList());
        return new AssistantMessage(text, meta, toolCalls, media);
    }


    private Pair<String, List<Media>> convertRawContentToMedias(Object rawContent) {
        if (rawContent instanceof String) {
            return Pair.of(rawContent.toString(), new ArrayList<>());
        }
        if (rawContent instanceof List<?> contents) {
            List<MediaContent> mediaContent = contents.stream()
                .map(content -> JsonUtils.convert(content, MediaContent.class))
                .toList();
            List<Media> medias = mediaContent.stream()
                .filter(media -> !"text".equals(media.type()))
                .map(OpenAIChatEngine::mapToMedia)
                .toList();
            String text = mediaContent.stream().filter(media -> "text".equals(media.type()))
                .map(MediaContent::text)
                .collect(Collectors.joining());
            return Pair.of(text, medias);
        }
        throw new IllegalArgumentException("invalid message content");
    }

    private static Media mapToMedia(MediaContent mediaContent) {
        return switch (mediaContent.type()) {
            case "image_url" -> {
                String url = mediaContent.imageUrl() != null ? mediaContent.imageUrl().url() : "";
                yield Media.builder()
                    .mimeType(toMediaMimeType(url))
                    .data(url)
                    .build();
            }
            case "input_audio" -> {
                InputAudio inputAudio = mediaContent.inputAudio();
                if (inputAudio == null) {
                    throw new IllegalArgumentException("audio data must be not null");
                }
                yield Media.builder()
                    .mimeType(inputAudio.format().equals(Format.MP3)
                        ? MimeType.valueOf("audio/mp3")
                        : MimeType.valueOf("audio/wav"))
                    .data(toAudioData(inputAudio.data()))
                    .build();
            }
            default -> throw new IllegalArgumentException("Unsupported message type: " + mediaContent.type());
        };
    }

    private static byte[] toAudioData(String data) {
        if (data == null) {
            return "".getBytes(StandardCharsets.UTF_8);
        }
        try {
            return Base64.getDecoder().decode(data);
        } catch (Exception e) {
            throw new IllegalArgumentException("Audio data must be encoded as base64");
        }
    }

    private static MimeType toMediaMimeType(String mediaData) {
        try {
            if (mediaData.startsWith("data:")) {
                String mimeTypePart = mediaData.substring(5, mediaData.indexOf(";"));
                return MimeTypeUtils.parseMimeType(mimeTypePart);
            } else {
                if (mediaData.endsWith(".jpg") || mediaData.endsWith(".jpeg")) {
                    return MimeTypeUtils.IMAGE_JPEG;
                }
                if (mediaData.endsWith(".png")) {
                    return MimeTypeUtils.IMAGE_PNG;
                }
                if (mediaData.endsWith(".gif")) {
                    return MimeTypeUtils.parseMimeType("image/gif");
                }
                if (mediaData.endsWith(".bmp")) {
                    return MimeTypeUtils.parseMimeType("image/bmp");
                }
                if (mediaData.endsWith(".webp")) {
                    return MimeTypeUtils.parseMimeType("image/webp");
                }
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        return MimeTypeUtils.IMAGE_JPEG;//set default image type
    }

    @Override
    public LLMChatCompletion mapToLLMChatCompletion(ChatResponse chatResponse) {
        return new LLMChatCompletion(
            chatResponse.getMetadata().getId(),
            chatResponse.getResults().stream().map(this::mapToChoice).toList(),
            chatResponse.getMetadata().getOrDefault("created", 0L),
            chatResponse.getMetadata().getModel(),
            //todo spring ai not impl
            //chatResponse.getMetadata().getOrDefault("service_tier", (String)null),
            null,
            chatResponse.getMetadata().getOrDefault("system-fingerprint", (String)null),
            "chat.completion",
            mapToUsage(chatResponse.getMetadata().getUsage())
        );
    }

    private Usage mapToUsage(org.springframework.ai.chat.metadata.Usage usage) {
        Object nativeUsage = usage.getNativeUsage();
        return JsonUtils.convert(nativeUsage, Usage.class);
    }

    @Override
    public LLMChatCompletionChunk mapToLLMChatCompletionChunk(ChatResponse chatResponse) {
        return new LLMChatCompletionChunk(
            chatResponse.getMetadata().getId(),
            chatResponse.getResults().stream().map(this::mapToChunkChoice).toList(),
            chatResponse.getMetadata().getOrDefault("created", 0L),
            chatResponse.getMetadata().getModel(),
            //todo spring ai not impl
            //chatResponse.getMetadata().getOrDefault("service_tier", (String) null),
            null,
            chatResponse.getMetadata().getOrDefault("system-fingerprint", (String)null),
            "chat.completion.chunk",
            mapToUsage(chatResponse.getMetadata().getUsage())
        );
    }

    private Choice mapToChoice(Generation generation) {
        AssistantMessage assistantMessage = generation.getOutput();
        return new Choice(
            ChatCompletionFinishReason.valueOf(generation.getMetadata().getFinishReason()),
            generation.getMetadata().getOrDefault("index", 0),
            new ChatCompletionMessage(
                assistantMessage.getText(),
                Role.valueOf(assistantMessage.getMetadata().get("role").toString()),
                //todo spring ai don't support it --> name
                "",
                null,
                toToolCall(generation),
                generation.getMetadata().get("refusal"),
                getAudioOutput(generation)
            ),
            //todo generation.getMetadata().get("logprobs")
            null
        );
    }

    private ChunkChoice mapToChunkChoice(Generation generation) {
        AssistantMessage assistantMessage = generation.getOutput();
        return new ChunkChoice(
            StringUtils.hasText(generation.getMetadata().getFinishReason()) ? ChatCompletionFinishReason.valueOf(
                generation.getMetadata().getFinishReason()) : null,
            generation.getMetadata().getOrDefault("index", null),
            new ChatCompletionMessage(
                assistantMessage.getText(),
                assistantMessage.getMetadata().get("role") != null
                    && StringUtils.hasText(assistantMessage.getMetadata().get("role").toString())
                    ? Role.valueOf(assistantMessage.getMetadata().get("role").toString())
                    : null,
                //todo spring ai don't support it --> name
                null,
                null,
                toToolCall(generation),
                generation.getMetadata().get("refusal"),
                getAudioOutput(generation)
            ),
            //todo generation.getMetadata().get("logprobs")
            null
        );
    }

    private static List<ChatCompletionMessage.ToolCall> toToolCall(Generation generation) {
        AtomicInteger toolCallIndex = new AtomicInteger(0);
        List<ChatCompletionMessage.ToolCall> toolCalls = generation.getOutput().getToolCalls().stream()
            .map(toolCall -> new ChatCompletionMessage.ToolCall(toolCallIndex.getAndIncrement(), toolCall.id(),
                toolCall.type(),
                new ChatCompletionFunction(toolCall.name(), toolCall.arguments())))
            .toList();
        return toolCalls.isEmpty() ? null : toolCalls;
    }

    private AudioOutput getAudioOutput(Generation generation) {
        List<Media> media = generation.getOutput().getMedia();
        if (media == null || media.isEmpty()) {
            return null;
        }
        Media audio = media.get(0);
        return new AudioOutput(audio.getId(), Base64.getEncoder().encodeToString(audio.getDataAsByteArray()),
            generation.getMetadata().getOrDefault("audioExpiresAt", 0L),
            //todo spring ai non-impl
            generation.getOutput().getText());
    }
}
