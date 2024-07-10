package org.springframework.http.codec.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractDecoder;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.xml.StaxUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/xml/Jaxb2XmlDecoder.class */
public class Jaxb2XmlDecoder extends AbstractDecoder<Object> {
    private static final String JAXB_DEFAULT_ANNOTATION_VALUE = "##default";
    private final XmlEventDecoder xmlEventDecoder;
    private final JaxbContextContainer jaxbContexts;

    public Jaxb2XmlDecoder() {
        super(MimeTypeUtils.APPLICATION_XML, MimeTypeUtils.TEXT_XML);
        this.xmlEventDecoder = new XmlEventDecoder();
        this.jaxbContexts = new JaxbContextContainer();
    }

    @Override // org.springframework.core.codec.AbstractDecoder, org.springframework.core.codec.Decoder
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        Class<?> outputClass;
        return super.canDecode(elementType, mimeType) && (outputClass = elementType.getRawClass()) != null && (outputClass.isAnnotationPresent(XmlRootElement.class) || outputClass.isAnnotationPresent(XmlType.class));
    }

    @Override // org.springframework.core.codec.Decoder
    public Flux<Object> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        Class<?> outputClass = elementType.getRawClass();
        Assert.state(outputClass != null, "Unresolvable output class");
        Flux<XMLEvent> xmlEventFlux = this.xmlEventDecoder.decode(inputStream, ResolvableType.forClass(XMLEvent.class), mimeType, hints);
        QName typeName = toQName(outputClass);
        Flux<List<XMLEvent>> splitEvents = split(xmlEventFlux, typeName);
        return splitEvents.map(events -> {
            Object value = unmarshal(events, outputClass);
            LogFormatUtils.traceDebug(this.logger, traceOn -> {
                String formatted = LogFormatUtils.formatValue(value, !traceOn.booleanValue());
                return Hints.getLogPrefix(hints) + "Decoded [" + formatted + "]";
            });
            return value;
        });
    }

    @Override // org.springframework.core.codec.AbstractDecoder, org.springframework.core.codec.Decoder
    public Mono<Object> decodeToMono(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return decode(inputStream, elementType, mimeType, hints).singleOrEmpty();
    }

    private Object unmarshal(List<XMLEvent> events, Class<?> outputClass) {
        try {
            Unmarshaller unmarshaller = this.jaxbContexts.createUnmarshaller(outputClass);
            XMLEventReader eventReader = StaxUtils.createXMLEventReader(events);
            if (outputClass.isAnnotationPresent(XmlRootElement.class)) {
                return unmarshaller.unmarshal(eventReader);
            }
            JAXBElement<?> jaxbElement = unmarshaller.unmarshal(eventReader, outputClass);
            return jaxbElement.getValue();
        } catch (UnmarshalException ex) {
            throw new DecodingException("Could not unmarshal XML to " + outputClass, ex);
        } catch (JAXBException ex2) {
            throw new CodecException("Invalid JAXB configuration", ex2);
        }
    }

    QName toQName(Class<?> outputClass) {
        String localPart;
        String namespaceUri;
        if (outputClass.isAnnotationPresent(XmlRootElement.class)) {
            XmlRootElement annotation = outputClass.getAnnotation(XmlRootElement.class);
            localPart = annotation.name();
            namespaceUri = annotation.namespace();
        } else if (outputClass.isAnnotationPresent(XmlType.class)) {
            XmlType annotation2 = outputClass.getAnnotation(XmlType.class);
            localPart = annotation2.name();
            namespaceUri = annotation2.namespace();
        } else {
            throw new IllegalArgumentException("Output class [" + outputClass.getName() + "] is neither annotated with @XmlRootElement nor @XmlType");
        }
        if ("##default".equals(localPart)) {
            localPart = ClassUtils.getShortNameAsProperty(outputClass);
        }
        if ("##default".equals(namespaceUri)) {
            Package outputClassPackage = outputClass.getPackage();
            if (outputClassPackage != null && outputClassPackage.isAnnotationPresent(XmlSchema.class)) {
                namespaceUri = outputClassPackage.getAnnotation(XmlSchema.class).namespace();
            } else {
                namespaceUri = "";
            }
        }
        return new QName(namespaceUri, localPart);
    }

    Flux<List<XMLEvent>> split(Flux<XMLEvent> xmlEventFlux, QName desiredName) {
        return xmlEventFlux.flatMap(new SplitFunction(desiredName));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/xml/Jaxb2XmlDecoder$SplitFunction.class */
    public static class SplitFunction implements Function<XMLEvent, Publisher<? extends List<XMLEvent>>> {
        private final QName desiredName;
        @Nullable
        private List<XMLEvent> events;
        private int elementDepth = 0;
        private int barrier = Integer.MAX_VALUE;

        public SplitFunction(QName desiredName) {
            this.desiredName = desiredName;
        }

        @Override // java.util.function.Function
        public Publisher<? extends List<XMLEvent>> apply(XMLEvent event) {
            if (event.isStartElement()) {
                if (this.barrier == Integer.MAX_VALUE) {
                    QName startElementName = event.asStartElement().getName();
                    if (this.desiredName.equals(startElementName)) {
                        this.events = new ArrayList();
                        this.barrier = this.elementDepth;
                    }
                }
                this.elementDepth++;
            }
            if (this.elementDepth > this.barrier) {
                Assert.state(this.events != null, "No XMLEvent List");
                this.events.add(event);
            }
            if (event.isEndElement()) {
                this.elementDepth--;
                if (this.elementDepth == this.barrier) {
                    this.barrier = Integer.MAX_VALUE;
                    Assert.state(this.events != null, "No XMLEvent List");
                    return Mono.just(this.events);
                }
            }
            return Mono.empty();
        }
    }
}