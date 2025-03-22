package chat.gptalk.lmm.gateway.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> Mono<T> parse(String json, Class<T> clazz) {
        if (!StringUtils.hasLength(json)) {
            return Mono.empty();
        }
        try {
            return Mono.justOrEmpty(OBJECT_MAPPER.readValue(json, clazz));
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
            return Mono.error(e);
        }
    }

    public static <T> Mono<T> parse(String json, TypeReference<T> typeReference) {
        if (!StringUtils.hasLength(json)) {
            return Mono.empty();
        }
        try {
            return Mono.justOrEmpty(OBJECT_MAPPER.readValue(json, typeReference));
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
            return Mono.error(e);
        }
    }

    public static <T> String toJson(T obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Mono<Boolean> isJson(String json) {
        try {
            OBJECT_MAPPER.readTree(json);
            return Mono.just(true);
        } catch (Exception e) {
            return Mono.just(false);
        }
    }

    public static <T> T convert(Object obj, Class<T> clazz) {
        return OBJECT_MAPPER.convertValue(obj, clazz);
    }
}
