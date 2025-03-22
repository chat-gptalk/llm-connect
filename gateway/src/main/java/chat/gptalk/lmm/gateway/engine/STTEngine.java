package chat.gptalk.lmm.gateway.engine;

import org.springframework.ai.audio.transcription.AudioTranscriptionOptions;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.api.OpenAiAudioApi.StructuredResponse;
import org.springframework.core.io.Resource;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public interface STTEngine extends Engine {

    OpenAiAudioTranscriptionModel sttModel();

    default Mono<StructuredResponse> transcriptions(Resource resource, AudioTranscriptionOptions options) {
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(resource, options);
        return Mono.fromCallable(() -> mapToSpeechResponse(sttModel().call(prompt)))
            .subscribeOn(Schedulers.boundedElastic());
    }

    StructuredResponse mapToSpeechResponse(AudioTranscriptionResponse transcriptionResponse);
}
