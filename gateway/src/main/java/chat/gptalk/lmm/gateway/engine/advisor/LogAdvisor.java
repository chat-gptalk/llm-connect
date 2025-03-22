package chat.gptalk.lmm.gateway.engine.advisor;

import chat.gptalk.lmm.gateway.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;

@Slf4j
public class LogAdvisor implements CallAroundAdvisor {

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        log.info("LLM Request: {}", JsonUtils.toJson(advisedRequest));
        AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);
        Usage usage = advisedResponse.response().getMetadata().getUsage();
        log.info("prompt_tokens: {}", usage.getPromptTokens());
        return advisedResponse;
    }

    @Override
    public String getName() {
        return LogAdvisor.class.getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
