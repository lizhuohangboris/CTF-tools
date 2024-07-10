package org.springframework.http.converter.feed;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.WireFeedInput;
import com.rometools.rome.io.WireFeedOutput;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/converter/feed/AbstractWireFeedHttpMessageConverter.class */
public abstract class AbstractWireFeedHttpMessageConverter<T extends WireFeed> extends AbstractHttpMessageConverter<T> {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected /* bridge */ /* synthetic */ void writeInternal(Object obj, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        writeInternal((AbstractWireFeedHttpMessageConverter<T>) ((WireFeed) obj), httpOutputMessage);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractWireFeedHttpMessageConverter(MediaType supportedMediaType) {
        super(supportedMediaType);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        WireFeedInput feedInput = new WireFeedInput();
        MediaType contentType = inputMessage.getHeaders().getContentType();
        Charset charset = (contentType == null || contentType.getCharset() == null) ? DEFAULT_CHARSET : contentType.getCharset();
        try {
            Reader reader = new InputStreamReader(inputMessage.getBody(), charset);
            return (T) feedInput.build(reader);
        } catch (FeedException ex) {
            throw new HttpMessageNotReadableException("Could not read WireFeed: " + ex.getMessage(), ex, inputMessage);
        }
    }

    protected void writeInternal(T wireFeed, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        Charset charset = StringUtils.hasLength(wireFeed.getEncoding()) ? Charset.forName(wireFeed.getEncoding()) : DEFAULT_CHARSET;
        MediaType contentType = outputMessage.getHeaders().getContentType();
        if (contentType != null) {
            outputMessage.getHeaders().setContentType(new MediaType(contentType.getType(), contentType.getSubtype(), charset));
        }
        WireFeedOutput feedOutput = new WireFeedOutput();
        try {
            Writer writer = new OutputStreamWriter(outputMessage.getBody(), charset);
            feedOutput.output(wireFeed, writer);
        } catch (FeedException ex) {
            throw new HttpMessageNotWritableException("Could not write WireFeed: " + ex.getMessage(), ex);
        }
    }
}