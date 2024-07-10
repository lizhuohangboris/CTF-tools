package org.springframework.http.codec.json;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Hints;
import org.springframework.http.HttpLogging;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/json/Jackson2CodecSupport.class */
public abstract class Jackson2CodecSupport {
    private static final String JSON_VIEW_HINT_ERROR = "@JsonView only supported for write hints with exactly 1 class argument: ";
    protected final Log logger = HttpLogging.forLogName(getClass());
    private final ObjectMapper objectMapper;
    private final List<MimeType> mimeTypes;
    public static final String JSON_VIEW_HINT = Jackson2CodecSupport.class.getName() + ".jsonView";
    private static final List<MimeType> DEFAULT_MIME_TYPES = Collections.unmodifiableList(Arrays.asList(new MimeType("application", "json", StandardCharsets.UTF_8), new MimeType("application", "*+json", StandardCharsets.UTF_8)));

    @Nullable
    protected abstract <A extends Annotation> A getAnnotation(MethodParameter methodParameter, Class<A> cls);

    /* JADX INFO: Access modifiers changed from: protected */
    public Jackson2CodecSupport(ObjectMapper objectMapper, MimeType... mimeTypes) {
        Assert.notNull(objectMapper, "ObjectMapper must not be null");
        this.objectMapper = objectMapper;
        this.mimeTypes = !ObjectUtils.isEmpty((Object[]) mimeTypes) ? Collections.unmodifiableList(Arrays.asList(mimeTypes)) : DEFAULT_MIME_TYPES;
    }

    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<MimeType> getMimeTypes() {
        return this.mimeTypes;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean supportsMimeType(@Nullable MimeType mimeType) {
        return mimeType == null || this.mimeTypes.stream().anyMatch(m -> {
            return m.isCompatibleWith(mimeType);
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public JavaType getJavaType(Type type, @Nullable Class<?> contextClass) {
        TypeFactory typeFactory = this.objectMapper.getTypeFactory();
        return typeFactory.constructType(GenericTypeResolver.resolveType(type, contextClass));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Map<String, Object> getHints(ResolvableType resolvableType) {
        JsonView annotation;
        MethodParameter param = getParameter(resolvableType);
        if (param != null && (annotation = (JsonView) getAnnotation(param, JsonView.class)) != null) {
            Class<?>[] classes = annotation.value();
            Assert.isTrue(classes.length == 1, JSON_VIEW_HINT_ERROR + param);
            return Hints.from(JSON_VIEW_HINT, classes[0]);
        }
        return Hints.none();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public MethodParameter getParameter(ResolvableType type) {
        if (type.getSource() instanceof MethodParameter) {
            return (MethodParameter) type.getSource();
        }
        return null;
    }
}