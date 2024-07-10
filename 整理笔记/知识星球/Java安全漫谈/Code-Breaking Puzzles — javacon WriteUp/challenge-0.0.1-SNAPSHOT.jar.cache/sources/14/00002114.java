package org.springframework.http.converter.xml;

import java.io.IOException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/converter/xml/AbstractXmlHttpMessageConverter.class */
public abstract class AbstractXmlHttpMessageConverter<T> extends AbstractHttpMessageConverter<T> {
    private final TransformerFactory transformerFactory;

    protected abstract T readFromSource(Class<? extends T> cls, HttpHeaders httpHeaders, Source source) throws Exception;

    protected abstract void writeToResult(T t, HttpHeaders httpHeaders, Result result) throws Exception;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractXmlHttpMessageConverter() {
        super(MediaType.APPLICATION_XML, MediaType.TEXT_XML, new MediaType("application", "*+xml"));
        this.transformerFactory = TransformerFactory.newInstance();
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public final T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        try {
            return readFromSource(clazz, inputMessage.getHeaders(), new StreamSource(inputMessage.getBody()));
        } catch (IOException | HttpMessageConversionException ex) {
            throw ex;
        } catch (Exception ex2) {
            throw new HttpMessageNotReadableException("Could not unmarshal to [" + clazz + "]: " + ex2.getMessage(), ex2, inputMessage);
        }
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected final void writeInternal(T t, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        try {
            writeToResult(t, outputMessage.getHeaders(), new StreamResult(outputMessage.getBody()));
        } catch (IOException | HttpMessageConversionException ex) {
            throw ex;
        } catch (Exception ex2) {
            throw new HttpMessageNotWritableException("Could not marshal [" + t + "]: " + ex2.getMessage(), ex2);
        }
    }

    protected void transform(Source source, Result result) throws TransformerException {
        this.transformerFactory.newTransformer().transform(source, result);
    }
}