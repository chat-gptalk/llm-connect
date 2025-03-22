package chat.gptalk.lmm.gateway.engine;

import java.util.Set;

public interface Engine {

    Set<String> supportedModels();

    default boolean support(String model) {
        return supportedModels().contains(model);
    }
}
