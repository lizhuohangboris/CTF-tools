package org.springframework.http.converter.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/converter/xml/SourceHttpMessageConverter.class */
public class SourceHttpMessageConverter<T extends Source> extends AbstractHttpMessageConverter<T> {
    private static final EntityResolver NO_OP_ENTITY_RESOLVER = publicId, systemId -> {
        return new InputSource(new StringReader(""));
    };
    private static final XMLResolver NO_OP_XML_RESOLVER = publicID, systemID, base, ns -> {
        return StreamUtils.emptyInput();
    };
    private static final Set<Class<?>> SUPPORTED_CLASSES = new HashSet(8);
    private final TransformerFactory transformerFactory;
    private boolean supportDtd;
    private boolean processExternalEntities;

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected /* bridge */ /* synthetic */ void writeInternal(Object obj, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        writeInternal((SourceHttpMessageConverter<T>) ((Source) obj), httpOutputMessage);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    @Nullable
    public /* bridge */ /* synthetic */ Long getContentLength(Object obj, @Nullable MediaType mediaType) throws IOException {
        return getContentLength((SourceHttpMessageConverter<T>) ((Source) obj), mediaType);
    }

    static {
        SUPPORTED_CLASSES.add(DOMSource.class);
        SUPPORTED_CLASSES.add(SAXSource.class);
        SUPPORTED_CLASSES.add(StAXSource.class);
        SUPPORTED_CLASSES.add(StreamSource.class);
        SUPPORTED_CLASSES.add(Source.class);
    }

    public SourceHttpMessageConverter() {
        super(MediaType.APPLICATION_XML, MediaType.TEXT_XML, new MediaType("application", "*+xml"));
        this.transformerFactory = TransformerFactory.newInstance();
        this.supportDtd = false;
        this.processExternalEntities = false;
    }

    public void setSupportDtd(boolean supportDtd) {
        this.supportDtd = supportDtd;
    }

    public boolean isSupportDtd() {
        return this.supportDtd;
    }

    public void setProcessExternalEntities(boolean processExternalEntities) {
        this.processExternalEntities = processExternalEntities;
        if (processExternalEntities) {
            setSupportDtd(true);
        }
    }

    public boolean isProcessExternalEntities() {
        return this.processExternalEntities;
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public boolean supports(Class<?> clazz) {
        return SUPPORTED_CLASSES.contains(clazz);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        InputStream body = inputMessage.getBody();
        if (DOMSource.class == clazz) {
            return readDOMSource(body, inputMessage);
        }
        if (SAXSource.class == clazz) {
            return readSAXSource(body, inputMessage);
        }
        if (StAXSource.class == clazz) {
            return (T) readStAXSource(body, inputMessage);
        }
        if (StreamSource.class == clazz || Source.class == clazz) {
            return readStreamSource(body);
        }
        throw new HttpMessageNotReadableException("Could not read class [" + clazz + "]. Only DOMSource, SAXSource, StAXSource, and StreamSource are supported.", inputMessage);
    }

    private DOMSource readDOMSource(InputStream body, HttpInputMessage inputMessage) throws IOException {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", !isSupportDtd());
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", isProcessExternalEntities());
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            if (!isProcessExternalEntities()) {
                documentBuilder.setEntityResolver(NO_OP_ENTITY_RESOLVER);
            }
            Document document = documentBuilder.parse(body);
            return new DOMSource(document);
        } catch (NullPointerException ex) {
            if (!isSupportDtd()) {
                throw new HttpMessageNotReadableException("NPE while unmarshalling: This can happen due to the presence of DTD declarations which are disabled.", ex, inputMessage);
            }
            throw ex;
        } catch (ParserConfigurationException ex2) {
            throw new HttpMessageNotReadableException("Could not set feature: " + ex2.getMessage(), ex2, inputMessage);
        } catch (SAXException ex3) {
            throw new HttpMessageNotReadableException("Could not parse document: " + ex3.getMessage(), ex3, inputMessage);
        }
    }

    private SAXSource readSAXSource(InputStream body, HttpInputMessage inputMessage) throws IOException {
        try {
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            xmlReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", !isSupportDtd());
            xmlReader.setFeature("http://xml.org/sax/features/external-general-entities", isProcessExternalEntities());
            if (!isProcessExternalEntities()) {
                xmlReader.setEntityResolver(NO_OP_ENTITY_RESOLVER);
            }
            byte[] bytes = StreamUtils.copyToByteArray(body);
            return new SAXSource(xmlReader, new InputSource(new ByteArrayInputStream(bytes)));
        } catch (SAXException ex) {
            throw new HttpMessageNotReadableException("Could not parse document: " + ex.getMessage(), ex, inputMessage);
        }
    }

    private Source readStAXSource(InputStream body, HttpInputMessage inputMessage) {
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            inputFactory.setProperty("javax.xml.stream.supportDTD", Boolean.valueOf(isSupportDtd()));
            inputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", Boolean.valueOf(isProcessExternalEntities()));
            if (!isProcessExternalEntities()) {
                inputFactory.setXMLResolver(NO_OP_XML_RESOLVER);
            }
            XMLStreamReader streamReader = inputFactory.createXMLStreamReader(body);
            return new StAXSource(streamReader);
        } catch (XMLStreamException ex) {
            throw new HttpMessageNotReadableException("Could not parse document: " + ex.getMessage(), ex, inputMessage);
        }
    }

    private StreamSource readStreamSource(InputStream body) throws IOException {
        byte[] bytes = StreamUtils.copyToByteArray(body);
        return new StreamSource(new ByteArrayInputStream(bytes));
    }

    @Nullable
    protected Long getContentLength(T t, @Nullable MediaType contentType) {
        if (t instanceof DOMSource) {
            try {
                CountingOutputStream os = new CountingOutputStream();
                transform(t, new StreamResult(os));
                return Long.valueOf(os.count);
            } catch (TransformerException e) {
                return null;
            }
        }
        return null;
    }

    protected void writeInternal(T t, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        try {
            Result result = new StreamResult(outputMessage.getBody());
            transform(t, result);
        } catch (TransformerException ex) {
            throw new HttpMessageNotWritableException("Could not transform [" + t + "] to output message", ex);
        }
    }

    private void transform(Source source, Result result) throws TransformerException {
        this.transformerFactory.newTransformer().transform(source, result);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/converter/xml/SourceHttpMessageConverter$CountingOutputStream.class */
    public static class CountingOutputStream extends OutputStream {
        long count;

        private CountingOutputStream() {
            this.count = 0L;
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            this.count++;
        }

        @Override // java.io.OutputStream
        public void write(byte[] b) throws IOException {
            this.count += b.length;
        }

        @Override // java.io.OutputStream
        public void write(byte[] b, int off, int len) throws IOException {
            this.count += len;
        }
    }
}