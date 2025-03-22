package chat.gptalk.lmm.gateway.engine.openai;

import chat.gptalk.lmm.gateway.engine.ModerationEngine;
import chat.gptalk.lmm.gateway.model.moderation.LLMModerationRequest;
import chat.gptalk.lmm.gateway.model.moderation.LLMModerationResponse;
import chat.gptalk.lmm.gateway.util.JsonUtils;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.moderation.ModerationModel;
import org.springframework.ai.moderation.ModerationPrompt;
import org.springframework.ai.moderation.ModerationResponse;
import org.springframework.ai.openai.OpenAiModerationModel;
import org.springframework.ai.openai.OpenAiModerationOptions;
import org.springframework.ai.openai.api.OpenAiModerationApi;
import org.springframework.ai.openai.api.OpenAiModerationApi.OpenAiModerationResult;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenAIModerationEngine implements ModerationEngine {

    private final OpenAiModerationModel moderationModel;

    @Override
    public Set<String> supportedModels() {
        return Set.of("omni-moderation-latest", "omni-moderation-2024-09-26", "text-moderation-latest",
            "text-moderation-007", "text-moderation-stable", "");
    }


    @Override
    public ModerationModel moderationModel() {
        return moderationModel;
    }

    @Override
    public ModerationPrompt buildModerationPrompt(LLMModerationRequest moderationRequest) {
        return new ModerationPrompt(moderationRequest.prompt(), OpenAiModerationOptions.builder()
            .model(moderationRequest.model())
            .build());
    }

    @Override
    public LLMModerationResponse mapToModerationResponse(ModerationResponse moderationResponse) {
        OpenAiModerationResult[] data = moderationResponse.getResult().getOutput().getResults().stream()
            .map(it -> new OpenAiModerationResult(it.isFlagged(),
                JsonUtils.convert(it.getCategories(), OpenAiModerationApi.Categories.class),
                JsonUtils.convert(it.getCategoryScores(), OpenAiModerationApi.CategoryScores.class))).toArray(OpenAiModerationResult[]::new);
        return new LLMModerationResponse(moderationResponse.getResult().getOutput().getId(),
            moderationResponse.getResult().getOutput().getModel(), data);
    }
}
