package org.springframework.remoting.httpinvoker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.web.servlet.support.WebContentGenerator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/remoting/httpinvoker/SimpleHttpInvokerRequestExecutor.class */
public class SimpleHttpInvokerRequestExecutor extends AbstractHttpInvokerRequestExecutor {
    private int connectTimeout = -1;
    private int readTimeout = -1;

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    @Override // org.springframework.remoting.httpinvoker.AbstractHttpInvokerRequestExecutor
    protected RemoteInvocationResult doExecuteRequest(HttpInvokerClientConfiguration config, ByteArrayOutputStream baos) throws IOException, ClassNotFoundException {
        HttpURLConnection con = openConnection(config);
        prepareConnection(con, baos.size());
        writeRequestBody(config, con, baos);
        validateResponse(config, con);
        InputStream responseBody = readResponseBody(config, con);
        return readRemoteInvocationResult(responseBody, config.getCodebaseUrl());
    }

    protected HttpURLConnection openConnection(HttpInvokerClientConfiguration config) throws IOException {
        URLConnection con = new URL(config.getServiceUrl()).openConnection();
        if (!(con instanceof HttpURLConnection)) {
            throw new IOException("Service URL [" + config.getServiceUrl() + "] does not resolve to an HTTP connection");
        }
        return (HttpURLConnection) con;
    }

    protected void prepareConnection(HttpURLConnection connection, int contentLength) throws IOException {
        Locale locale;
        if (this.connectTimeout >= 0) {
            connection.setConnectTimeout(this.connectTimeout);
        }
        if (this.readTimeout >= 0) {
            connection.setReadTimeout(this.readTimeout);
        }
        connection.setDoOutput(true);
        connection.setRequestMethod(WebContentGenerator.METHOD_POST);
        connection.setRequestProperty(HttpHeaders.CONTENT_TYPE, getContentType());
        connection.setRequestProperty(HttpHeaders.CONTENT_LENGTH, Integer.toString(contentLength));
        LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
        if (localeContext != null && (locale = localeContext.getLocale()) != null) {
            connection.setRequestProperty(HttpHeaders.ACCEPT_LANGUAGE, locale.toLanguageTag());
        }
        if (isAcceptGzipEncoding()) {
            connection.setRequestProperty(HttpHeaders.ACCEPT_ENCODING, "gzip");
        }
    }

    protected void writeRequestBody(HttpInvokerClientConfiguration config, HttpURLConnection con, ByteArrayOutputStream baos) throws IOException {
        baos.writeTo(con.getOutputStream());
    }

    protected void validateResponse(HttpInvokerClientConfiguration config, HttpURLConnection con) throws IOException {
        if (con.getResponseCode() >= 300) {
            throw new IOException("Did not receive successful HTTP response: status code = " + con.getResponseCode() + ", status message = [" + con.getResponseMessage() + "]");
        }
    }

    protected InputStream readResponseBody(HttpInvokerClientConfiguration config, HttpURLConnection con) throws IOException {
        if (isGzipResponse(con)) {
            return new GZIPInputStream(con.getInputStream());
        }
        return con.getInputStream();
    }

    protected boolean isGzipResponse(HttpURLConnection con) {
        String encodingHeader = con.getHeaderField(HttpHeaders.CONTENT_ENCODING);
        return encodingHeader != null && encodingHeader.toLowerCase().contains("gzip");
    }
}