package chat.gptalk.lmm.gateway.engine;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class EngineFactory {

    private final Map<String, ChatEngine> chatEngineMap;
    private final Map<String, EmbeddingEngine> embeddingEngineMap;
    private final Map<String, ImageEngine> imageEngineMap;
    private final Map<String, ModerationEngine> moderationEngineMap;

    @Autowired
    public EngineFactory(ApplicationContext context) {
        this.chatEngineMap = context.getBeansOfType(ChatEngine.class);
        this.embeddingEngineMap = context.getBeansOfType(EmbeddingEngine.class);
        this.imageEngineMap = context.getBeansOfType(ImageEngine.class);
        this.moderationEngineMap = context.getBeansOfType(ModerationEngine.class);
    }

    public ChatEngine getChatEngine(String model) {
        return chatEngineMap.values().stream()
            .filter(service -> service.support(model))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unsupported model: " + model));
    }

    public EmbeddingEngine getEmbeddingEngine(String model) {
        return embeddingEngineMap.values().stream()
            .filter(service -> service.support(model))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unsupported model: " + model));
    }

    public ImageEngine getImageEngine(String model) {
        return imageEngineMap.values().stream()
            .filter(service -> service.support(model))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unsupported model: " + model));
    }

    public ModerationEngine getModerationEngine(String model) {
        return moderationEngineMap.values().stream()
            .filter(service -> service.support(model))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unsupported model: " + model));
    }
}

