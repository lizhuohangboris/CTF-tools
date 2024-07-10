package org.springframework.http.codec.json;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Collections;
import java.util.Map;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/json/Jackson2JsonEncoder.class */
public class Jackson2JsonEncoder extends AbstractJackson2Encoder {
    @Nullable
    private final PrettyPrinter ssePrettyPrinter;

    public Jackson2JsonEncoder() {
        this(Jackson2ObjectMapperBuilder.json().build(), new MimeType[0]);
    }

    public Jackson2JsonEncoder(ObjectMapper mapper, MimeType... mimeTypes) {
        super(mapper, mimeTypes);
        setStreamingMediaTypes(Collections.singletonList(MediaType.APPLICATION_STREAM_JSON));
        this.ssePrettyPrinter = initSsePrettyPrinter();
    }

    private static PrettyPrinter initSsePrettyPrinter() {
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
        printer.indentObjectsWith(new DefaultIndenter("  ", "\ndata:"));
        return printer;
    }

    @Override // org.springframework.http.codec.json.AbstractJackson2Encoder
    protected ObjectWriter customizeWriter(ObjectWriter writer, @Nullable MimeType mimeType, ResolvableType elementType, @Nullable Map<String, Object> hints) {
        return (this.ssePrettyPrinter != null && MediaType.TEXT_EVENT_STREAM.isCompatibleWith(mimeType) && writer.getConfig().isEnabled(SerializationFeature.INDENT_OUTPUT)) ? writer.with(this.ssePrettyPrinter) : writer;
    }
}