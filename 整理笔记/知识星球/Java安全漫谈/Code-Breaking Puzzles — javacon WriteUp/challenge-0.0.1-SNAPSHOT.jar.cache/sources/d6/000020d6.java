package org.springframework.http.codec.xml;

import com.fasterxml.aalto.AsyncByteBufferFeeder;
import com.fasterxml.aalto.AsyncXMLInputFactory;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.evt.EventAllocatorImpl;
import com.fasterxml.aalto.stax.InputFactoryImpl;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.xml.StaxUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/xml/XmlEventDecoder.class */
public class XmlEventDecoder extends AbstractDecoder<XMLEvent> {
    private static final XMLInputFactory inputFactory = StaxUtils.createDefensiveInputFactory();
    private static final boolean aaltoPresent = ClassUtils.isPresent("com.fasterxml.aalto.AsyncXMLStreamReader", XmlEventDecoder.class.getClassLoader());
    boolean useAalto;

    public XmlEventDecoder() {
        super(MimeTypeUtils.APPLICATION_XML, MimeTypeUtils.TEXT_XML);
        this.useAalto = aaltoPresent;
    }

    @Override // org.springframework.core.codec.Decoder
    public Flux<XMLEvent> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        Flux<DataBuffer> flux = Flux.from(inputStream);
        if (this.useAalto) {
            AaltoDataBufferToXmlEvent aaltoMapper = new AaltoDataBufferToXmlEvent();
            return flux.flatMap(aaltoMapper).doFinally(signalType -> {
                aaltoMapper.endOfInput();
            });
        }
        Mono<DataBuffer> singleBuffer = DataBufferUtils.join(flux);
        return singleBuffer.flatMapMany(dataBuffer -> {
            try {
                InputStream is = dataBuffer.asInputStream();
                XMLEventReader createXMLEventReader = inputFactory.createXMLEventReader(is);
                return Flux.fromIterable(() -> {
                    return createXMLEventReader;
                }).doFinally(t -> {
                    DataBufferUtils.release(dataBuffer);
                });
            } catch (XMLStreamException ex) {
                return Mono.error(ex);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/xml/XmlEventDecoder$AaltoDataBufferToXmlEvent.class */
    public static class AaltoDataBufferToXmlEvent implements Function<DataBuffer, Publisher<? extends XMLEvent>> {
        private static final AsyncXMLInputFactory inputFactory = new InputFactoryImpl();
        private final AsyncXMLStreamReader<AsyncByteBufferFeeder> streamReader;
        private final XMLEventAllocator eventAllocator;

        private AaltoDataBufferToXmlEvent() {
            this.streamReader = inputFactory.createAsyncForByteBuffer();
            this.eventAllocator = EventAllocatorImpl.getDefaultInstance();
        }

        @Override // java.util.function.Function
        public Publisher<? extends XMLEvent> apply(DataBuffer dataBuffer) {
            try {
                try {
                    this.streamReader.getInputFeeder().feedInput(dataBuffer.asByteBuffer());
                    List<XMLEvent> events = new ArrayList<>();
                    while (this.streamReader.next() != 257) {
                        XMLEvent event = this.eventAllocator.allocate(this.streamReader);
                        events.add(event);
                        if (event.isEndDocument()) {
                            break;
                        }
                    }
                    Flux fromIterable = Flux.fromIterable(events);
                    DataBufferUtils.release(dataBuffer);
                    return fromIterable;
                } catch (XMLStreamException ex) {
                    Mono error = Mono.error(ex);
                    DataBufferUtils.release(dataBuffer);
                    return error;
                }
            } catch (Throwable th) {
                DataBufferUtils.release(dataBuffer);
                throw th;
            }
        }

        public void endOfInput() {
            this.streamReader.getInputFeeder().endOfInput();
        }
    }
}