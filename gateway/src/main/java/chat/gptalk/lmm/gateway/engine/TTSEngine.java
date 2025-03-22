package chat.gptalk.lmm.gateway.engine;

import chat.gptalk.lmm.gateway.model.speech.LLMSpeechRequest;
import org.springframework.ai.openai.audio.speech.SpeechModel;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public interface TTSEngine extends Engine {

    SpeechModel speechModel();

    default Mono<byte[]> tts(LLMSpeechRequest speechRequest) {
        SpeechPrompt speechPrompt = buildSpeechRequest(speechRequest);
        return Mono.fromCallable(() -> mapToSpeechResponse(speechModel().call(speechPrompt)))
            .subscribeOn(Schedulers.boundedElastic());
    }

    SpeechPrompt buildSpeechRequest(LLMSpeechRequest speechRequest);

    byte[] mapToSpeechResponse(SpeechResponse speechResponse);
}
