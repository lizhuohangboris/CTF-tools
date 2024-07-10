package org.thymeleaf.spring5;

import java.nio.charset.Charset;
import java.util.Set;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.thymeleaf.context.IContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/ISpringWebFluxTemplateEngine.class */
public interface ISpringWebFluxTemplateEngine extends ISpringTemplateEngine {
    Publisher<DataBuffer> processStream(String str, Set<String> set, IContext iContext, DataBufferFactory dataBufferFactory, MediaType mediaType, Charset charset);

    Publisher<DataBuffer> processStream(String str, Set<String> set, IContext iContext, DataBufferFactory dataBufferFactory, MediaType mediaType, Charset charset, int i);
}