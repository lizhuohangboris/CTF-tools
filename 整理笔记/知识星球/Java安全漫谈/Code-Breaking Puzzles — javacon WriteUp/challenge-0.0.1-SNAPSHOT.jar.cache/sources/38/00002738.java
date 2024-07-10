package org.springframework.web.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/ContentCachingRequestWrapper.class */
public class ContentCachingRequestWrapper extends HttpServletRequestWrapper {
    private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private final ByteArrayOutputStream cachedContent;
    @Nullable
    private final Integer contentCacheLimit;
    @Nullable
    private ServletInputStream inputStream;
    @Nullable
    private BufferedReader reader;

    public ContentCachingRequestWrapper(HttpServletRequest request) {
        super(request);
        int contentLength = request.getContentLength();
        this.cachedContent = new ByteArrayOutputStream(contentLength >= 0 ? contentLength : 1024);
        this.contentCacheLimit = null;
    }

    public ContentCachingRequestWrapper(HttpServletRequest request, int contentCacheLimit) {
        super(request);
        this.cachedContent = new ByteArrayOutputStream(contentCacheLimit);
        this.contentCacheLimit = Integer.valueOf(contentCacheLimit);
    }

    @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
    public ServletInputStream getInputStream() throws IOException {
        if (this.inputStream == null) {
            this.inputStream = new ContentCachingInputStream(getRequest().getInputStream());
        }
        return this.inputStream;
    }

    @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
    public String getCharacterEncoding() {
        String enc = super.getCharacterEncoding();
        return enc != null ? enc : "ISO-8859-1";
    }

    @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
    public BufferedReader getReader() throws IOException {
        if (this.reader == null) {
            this.reader = new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
        }
        return this.reader;
    }

    @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
    public String getParameter(String name) {
        if (this.cachedContent.size() == 0 && isFormPost()) {
            writeRequestParametersToCachedContent();
        }
        return super.getParameter(name);
    }

    @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
    public Map<String, String[]> getParameterMap() {
        if (this.cachedContent.size() == 0 && isFormPost()) {
            writeRequestParametersToCachedContent();
        }
        return super.getParameterMap();
    }

    @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
    public Enumeration<String> getParameterNames() {
        if (this.cachedContent.size() == 0 && isFormPost()) {
            writeRequestParametersToCachedContent();
        }
        return super.getParameterNames();
    }

    @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
    public String[] getParameterValues(String name) {
        if (this.cachedContent.size() == 0 && isFormPost()) {
            writeRequestParametersToCachedContent();
        }
        return super.getParameterValues(name);
    }

    private boolean isFormPost() {
        String contentType = getContentType();
        return contentType != null && contentType.contains("application/x-www-form-urlencoded") && HttpMethod.POST.matches(getMethod());
    }

    private void writeRequestParametersToCachedContent() {
        try {
            if (this.cachedContent.size() == 0) {
                String requestEncoding = getCharacterEncoding();
                Map<String, String[]> form = super.getParameterMap();
                Iterator<String> nameIterator = form.keySet().iterator();
                while (nameIterator.hasNext()) {
                    String name = nameIterator.next();
                    List<String> values = Arrays.asList(form.get(name));
                    Iterator<String> valueIterator = values.iterator();
                    while (valueIterator.hasNext()) {
                        String value = valueIterator.next();
                        this.cachedContent.write(URLEncoder.encode(name, requestEncoding).getBytes());
                        if (value != null) {
                            this.cachedContent.write(61);
                            this.cachedContent.write(URLEncoder.encode(value, requestEncoding).getBytes());
                            if (valueIterator.hasNext()) {
                                this.cachedContent.write(38);
                            }
                        }
                    }
                    if (nameIterator.hasNext()) {
                        this.cachedContent.write(38);
                    }
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to write request parameters to cached content", ex);
        }
    }

    public byte[] getContentAsByteArray() {
        return this.cachedContent.toByteArray();
    }

    protected void handleContentOverflow(int contentCacheLimit) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/ContentCachingRequestWrapper$ContentCachingInputStream.class */
    public class ContentCachingInputStream extends ServletInputStream {
        private final ServletInputStream is;
        private boolean overflow = false;

        public ContentCachingInputStream(ServletInputStream is) {
            this.is = is;
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            int ch2 = this.is.read();
            if (ch2 != -1 && !this.overflow) {
                if (ContentCachingRequestWrapper.this.contentCacheLimit == null || ContentCachingRequestWrapper.this.cachedContent.size() != ContentCachingRequestWrapper.this.contentCacheLimit.intValue()) {
                    ContentCachingRequestWrapper.this.cachedContent.write(ch2);
                } else {
                    this.overflow = true;
                    ContentCachingRequestWrapper.this.handleContentOverflow(ContentCachingRequestWrapper.this.contentCacheLimit.intValue());
                }
            }
            return ch2;
        }

        @Override // java.io.InputStream
        public int read(byte[] b) throws IOException {
            int count = this.is.read(b);
            writeToCache(b, 0, count);
            return count;
        }

        private void writeToCache(byte[] b, int off, int count) {
            if (!this.overflow && count > 0) {
                if (ContentCachingRequestWrapper.this.contentCacheLimit == null || count + ContentCachingRequestWrapper.this.cachedContent.size() <= ContentCachingRequestWrapper.this.contentCacheLimit.intValue()) {
                    ContentCachingRequestWrapper.this.cachedContent.write(b, off, count);
                    return;
                }
                this.overflow = true;
                ContentCachingRequestWrapper.this.cachedContent.write(b, off, ContentCachingRequestWrapper.this.contentCacheLimit.intValue() - ContentCachingRequestWrapper.this.cachedContent.size());
                ContentCachingRequestWrapper.this.handleContentOverflow(ContentCachingRequestWrapper.this.contentCacheLimit.intValue());
            }
        }

        @Override // java.io.InputStream
        public int read(byte[] b, int off, int len) throws IOException {
            int count = this.is.read(b, off, len);
            writeToCache(b, off, count);
            return count;
        }

        @Override // javax.servlet.ServletInputStream
        public int readLine(byte[] b, int off, int len) throws IOException {
            int count = this.is.readLine(b, off, len);
            writeToCache(b, off, count);
            return count;
        }

        @Override // javax.servlet.ServletInputStream
        public boolean isFinished() {
            return this.is.isFinished();
        }

        @Override // javax.servlet.ServletInputStream
        public boolean isReady() {
            return this.is.isReady();
        }

        @Override // javax.servlet.ServletInputStream
        public void setReadListener(ReadListener readListener) {
            this.is.setReadListener(readListener);
        }
    }
}