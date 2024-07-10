package org.springframework.http.converter.protobuf;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.util.JsonFormat;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/converter/protobuf/ProtobufJsonFormatHttpMessageConverter.class */
public class ProtobufJsonFormatHttpMessageConverter extends ProtobufHttpMessageConverter {
    public ProtobufJsonFormatHttpMessageConverter() {
        this((JsonFormat.Parser) null, (JsonFormat.Printer) null, (ExtensionRegistry) null);
    }

    public ProtobufJsonFormatHttpMessageConverter(@Nullable JsonFormat.Parser parser, @Nullable JsonFormat.Printer printer) {
        this(parser, printer, (ExtensionRegistry) null);
    }

    public ProtobufJsonFormatHttpMessageConverter(@Nullable JsonFormat.Parser parser, @Nullable JsonFormat.Printer printer, @Nullable ExtensionRegistry extensionRegistry) {
        super(new ProtobufHttpMessageConverter.ProtobufJavaUtilSupport(parser, printer), extensionRegistry);
    }

    @Deprecated
    public ProtobufJsonFormatHttpMessageConverter(@Nullable JsonFormat.Parser parser, @Nullable JsonFormat.Printer printer, @Nullable ExtensionRegistryInitializer registryInitializer) {
        super(new ProtobufHttpMessageConverter.ProtobufJavaUtilSupport(parser, printer), null);
        if (registryInitializer != null) {
            registryInitializer.initializeExtensionRegistry(this.extensionRegistry);
        }
    }
}