package chat.gptalk.lmm.gateway.engine.openai;

import chat.gptalk.lmm.gateway.engine.TTSEngine;
import chat.gptalk.lmm.gateway.model.speech.LLMSpeechRequest;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi.TtsModel;
import org.springframework.ai.openai.audio.speech.SpeechModel;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenAITTSEngine implements TTSEngine {

    private final OpenAiAudioSpeechModel speechModel;

    @Override
    public Set<String> supportedModels() {
        return Arrays.stream(TtsModel.values())
            .map(TtsModel::getValue)
            .collect(Collectors.toSet());
    }

    @Override
    public SpeechModel speechModel() {
        return speechModel;
    }

    @Override
    public SpeechPrompt buildSpeechRequest(LLMSpeechRequest speechRequest) {
        return new SpeechPrompt(
            speechRequest.input(),
            OpenAiAudioSpeechOptions.builder()
                .model(speechRequest.model())
                .input(speechRequest.input())
                .responseFormat(speechRequest.responseFormat())
                .speed(speechRequest.speed())
                .voice(speechRequest.voice())
                .build()
        );
    }

    @Override
    public byte[] mapToSpeechResponse(SpeechResponse speechResponse) {
        return speechResponse.getResult().getOutput();
    }
}
