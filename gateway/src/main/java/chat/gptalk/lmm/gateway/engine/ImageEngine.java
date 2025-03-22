package chat.gptalk.lmm.gateway.engine;

import chat.gptalk.lmm.gateway.model.image.LLMImageRequest;
import chat.gptalk.lmm.gateway.model.image.LLMImageResponse;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public interface ImageEngine extends Engine {

    ImageModel imageModel();

    default Mono<LLMImageResponse> generation(LLMImageRequest imageRequest) {
        ImagePrompt imagePrompt = buildImageRequest(imageRequest);
        return Mono.fromCallable(() -> mapToLLMImageResponse(imageModel().call(imagePrompt)))
            .subscribeOn(Schedulers.boundedElastic());
    }

    ImagePrompt buildImageRequest(LLMImageRequest imageRequest);

    LLMImageResponse mapToLLMImageResponse(ImageResponse imageResponse);
}
