package chat.gptalk.lmm.gateway.engine.openai;

import chat.gptalk.lmm.gateway.engine.ImageEngine;
import chat.gptalk.lmm.gateway.model.image.LLMImageRequest;
import chat.gptalk.lmm.gateway.model.image.LLMImageResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.ai.openai.api.OpenAiImageApi.Data;
import org.springframework.ai.openai.metadata.OpenAiImageGenerationMetadata;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenAIImageEngine implements ImageEngine {

    private final OpenAiImageModel imageModel;

    @Override
    public Set<String> supportedModels() {
        return Arrays.stream(OpenAiImageApi.ImageModel.values())
            .map(OpenAiImageApi.ImageModel::getValue)
            .collect(Collectors.toSet());
    }

    @Override
    public boolean support(String model) {
        return ImageEngine.super.support(model);
    }

    @Override
    public ImageModel imageModel() {
        return imageModel;
    }

    @Override
    public ImagePrompt buildImageRequest(LLMImageRequest llmImageRequest) {
        Integer width = null;
        Integer height = null;
        if (llmImageRequest.size() != null) {
            String[] dimensions = llmImageRequest.size().split("x");
            if (dimensions.length == 2) {
                width = Integer.parseInt(dimensions[0]);
                height = Integer.parseInt(dimensions[1]);
            }
        }
        return new ImagePrompt(
            llmImageRequest.prompt(),
            OpenAiImageOptions.builder()
                .model(llmImageRequest.model())
                .N(llmImageRequest.n())
                .user(llmImageRequest.user())
                .responseFormat(llmImageRequest.responseFormat())
                .quality(llmImageRequest.quality())
                .style(llmImageRequest.style())
                .width(width)
                .height(height)
                .build()
        );
    }

    @Override
    public LLMImageResponse mapToLLMImageResponse(ImageResponse imageResponse) {
        List<Data> data = imageResponse.getResults().stream()
            .map(it -> new Data(it.getOutput().getUrl(), it.getOutput().getB64Json(),
                ((OpenAiImageGenerationMetadata) it.getMetadata()).getRevisedPrompt())).toList();
        return new LLMImageResponse(imageResponse.getMetadata().getCreated(), data);
    }
}
