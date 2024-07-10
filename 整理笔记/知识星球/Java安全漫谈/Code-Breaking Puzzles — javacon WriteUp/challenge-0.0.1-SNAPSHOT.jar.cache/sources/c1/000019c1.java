package org.springframework.boot.json;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;
import org.springframework.beans.PropertyAccessor;
import org.springframework.util.ReflectionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/json/AbstractJsonParser.class */
public abstract class AbstractJsonParser implements JsonParser {
    /* JADX INFO: Access modifiers changed from: protected */
    public final Map<String, Object> parseMap(String json, Function<String, Map<String, Object>> parser) {
        return (Map) trimParse(json, "{", parser);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final List<Object> parseList(String json, Function<String, List<Object>> parser) {
        return (List) trimParse(json, PropertyAccessor.PROPERTY_KEY_PREFIX, parser);
    }

    protected final <T> T trimParse(String json, String prefix, Function<String, T> parser) {
        String trimmed = json != null ? json.trim() : "";
        if (trimmed.startsWith(prefix)) {
            return parser.apply(trimmed);
        }
        throw new JsonParseException();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final <T> T tryParse(Callable<T> parser, Class<? extends Exception> check) {
        try {
            return parser.call();
        } catch (Exception ex) {
            if (check.isAssignableFrom(ex.getClass())) {
                throw new JsonParseException(ex);
            }
            ReflectionUtils.rethrowRuntimeException(ex);
            throw new IllegalStateException(ex);
        }
    }
}