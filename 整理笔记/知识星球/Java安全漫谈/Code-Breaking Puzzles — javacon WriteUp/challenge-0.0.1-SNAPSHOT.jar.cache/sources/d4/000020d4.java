package org.springframework.http.codec.xml;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractSingleValueEncoder;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.EncodingException;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/xml/Jaxb2XmlEncoder.class */
public class Jaxb2XmlEncoder extends AbstractSingleValueEncoder<Object> {
    private final JaxbContextContainer jaxbContexts;

    public Jaxb2XmlEncoder() {
        super(MimeTypeUtils.APPLICATION_XML, MimeTypeUtils.TEXT_XML);
        this.jaxbContexts = new JaxbContextContainer();
    }

    @Override // org.springframework.core.codec.AbstractEncoder, org.springframework.core.codec.Encoder
    public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
        if (super.canEncode(elementType, mimeType)) {
            Class<?> outputClass = elementType.toClass();
            return outputClass.isAnnotationPresent(XmlRootElement.class) || outputClass.isAnnotationPresent(XmlType.class);
        }
        return false;
    }

    @Override // org.springframework.core.codec.AbstractSingleValueEncoder
    protected Flux<DataBuffer> encode(Object value, DataBufferFactory dataBufferFactory, ResolvableType type, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        if (!Hints.isLoggingSuppressed(hints)) {
            LogFormatUtils.traceDebug(this.logger, traceOn -> {
                String formatted = LogFormatUtils.formatValue(value, !traceOn.booleanValue());
                return Hints.getLogPrefix(hints) + "Encoding [" + formatted + "]";
            });
        }
        boolean release = true;
        DataBuffer buffer = dataBufferFactory.allocateBuffer(1024);
        OutputStream outputStream = buffer.asOutputStream();
        Class<?> clazz = ClassUtils.getUserClass(value);
        try {
            try {
                Marshaller marshaller = this.jaxbContexts.createMarshaller(clazz);
                marshaller.setProperty("jaxb.encoding", StandardCharsets.UTF_8.name());
                marshaller.marshal(value, outputStream);
                release = false;
                Flux<DataBuffer> just = Flux.just(buffer);
                if (0 != 0) {
                    DataBufferUtils.release(buffer);
                }
                return just;
            } catch (JAXBException ex) {
                Flux<DataBuffer> error = Flux.error(new CodecException("Invalid JAXB configuration", ex));
                if (release) {
                    DataBufferUtils.release(buffer);
                }
                return error;
            } catch (MarshalException ex2) {
                Flux<DataBuffer> error2 = Flux.error(new EncodingException("Could not marshal " + value.getClass() + " to XML", ex2));
                if (release) {
                    DataBufferUtils.release(buffer);
                }
                return error2;
            }
        } catch (Throwable th) {
            if (release) {
                DataBufferUtils.release(buffer);
            }
            throw th;
        }
    }
}