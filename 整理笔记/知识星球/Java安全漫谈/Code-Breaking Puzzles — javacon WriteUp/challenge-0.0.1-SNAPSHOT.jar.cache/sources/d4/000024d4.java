package org.springframework.web.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/filter/AbstractRequestLoggingFilter.class */
public abstract class AbstractRequestLoggingFilter extends OncePerRequestFilter {
    public static final String DEFAULT_BEFORE_MESSAGE_PREFIX = "Before request [";
    public static final String DEFAULT_BEFORE_MESSAGE_SUFFIX = "]";
    public static final String DEFAULT_AFTER_MESSAGE_PREFIX = "After request [";
    public static final String DEFAULT_AFTER_MESSAGE_SUFFIX = "]";
    private static final int DEFAULT_MAX_PAYLOAD_LENGTH = 50;
    private boolean includeQueryString = false;
    private boolean includeClientInfo = false;
    private boolean includeHeaders = false;
    private boolean includePayload = false;
    private int maxPayloadLength = 50;
    private String beforeMessagePrefix = DEFAULT_BEFORE_MESSAGE_PREFIX;
    private String beforeMessageSuffix = "]";
    private String afterMessagePrefix = DEFAULT_AFTER_MESSAGE_PREFIX;
    private String afterMessageSuffix = "]";

    protected abstract void beforeRequest(HttpServletRequest httpServletRequest, String str);

    protected abstract void afterRequest(HttpServletRequest httpServletRequest, String str);

    public void setIncludeQueryString(boolean includeQueryString) {
        this.includeQueryString = includeQueryString;
    }

    protected boolean isIncludeQueryString() {
        return this.includeQueryString;
    }

    public void setIncludeClientInfo(boolean includeClientInfo) {
        this.includeClientInfo = includeClientInfo;
    }

    protected boolean isIncludeClientInfo() {
        return this.includeClientInfo;
    }

    public void setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }

    protected boolean isIncludeHeaders() {
        return this.includeHeaders;
    }

    public void setIncludePayload(boolean includePayload) {
        this.includePayload = includePayload;
    }

    protected boolean isIncludePayload() {
        return this.includePayload;
    }

    public void setMaxPayloadLength(int maxPayloadLength) {
        Assert.isTrue(maxPayloadLength >= 0, "'maxPayloadLength' should be larger than or equal to 0");
        this.maxPayloadLength = maxPayloadLength;
    }

    protected int getMaxPayloadLength() {
        return this.maxPayloadLength;
    }

    public void setBeforeMessagePrefix(String beforeMessagePrefix) {
        this.beforeMessagePrefix = beforeMessagePrefix;
    }

    public void setBeforeMessageSuffix(String beforeMessageSuffix) {
        this.beforeMessageSuffix = beforeMessageSuffix;
    }

    public void setAfterMessagePrefix(String afterMessagePrefix) {
        this.afterMessagePrefix = afterMessagePrefix;
    }

    public void setAfterMessageSuffix(String afterMessageSuffix) {
        this.afterMessageSuffix = afterMessageSuffix;
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override // org.springframework.web.filter.OncePerRequestFilter
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean isFirstRequest = !isAsyncDispatch(request);
        HttpServletRequest requestToUse = request;
        if (isIncludePayload() && isFirstRequest && !(request instanceof ContentCachingRequestWrapper)) {
            requestToUse = new ContentCachingRequestWrapper(request, getMaxPayloadLength());
        }
        boolean shouldLog = shouldLog(requestToUse);
        if (shouldLog && isFirstRequest) {
            beforeRequest(requestToUse, getBeforeMessage(requestToUse));
        }
        try {
            filterChain.doFilter(requestToUse, response);
            if (shouldLog && !isAsyncStarted(requestToUse)) {
                afterRequest(requestToUse, getAfterMessage(requestToUse));
            }
        } catch (Throwable th) {
            if (shouldLog && !isAsyncStarted(requestToUse)) {
                afterRequest(requestToUse, getAfterMessage(requestToUse));
            }
            throw th;
        }
    }

    private String getBeforeMessage(HttpServletRequest request) {
        return createMessage(request, this.beforeMessagePrefix, this.beforeMessageSuffix);
    }

    private String getAfterMessage(HttpServletRequest request) {
        return createMessage(request, this.afterMessagePrefix, this.afterMessageSuffix);
    }

    protected String createMessage(HttpServletRequest request, String prefix, String suffix) {
        String payload;
        String queryString;
        StringBuilder msg = new StringBuilder();
        msg.append(prefix);
        msg.append("uri=").append(request.getRequestURI());
        if (isIncludeQueryString() && (queryString = request.getQueryString()) != null) {
            msg.append('?').append(queryString);
        }
        if (isIncludeClientInfo()) {
            String client = request.getRemoteAddr();
            if (StringUtils.hasLength(client)) {
                msg.append(";client=").append(client);
            }
            HttpSession session = request.getSession(false);
            if (session != null) {
                msg.append(";session=").append(session.getId());
            }
            String user = request.getRemoteUser();
            if (user != null) {
                msg.append(";user=").append(user);
            }
        }
        if (isIncludeHeaders()) {
            msg.append(";headers=").append(new ServletServerHttpRequest(request).getHeaders());
        }
        if (isIncludePayload() && (payload = getMessagePayload(request)) != null) {
            msg.append(";payload=").append(payload);
        }
        msg.append(suffix);
        return msg.toString();
    }

    @Nullable
    protected String getMessagePayload(HttpServletRequest request) {
        ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                int length = Math.min(buf.length, getMaxPayloadLength());
                try {
                    return new String(buf, 0, length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException e) {
                    return "[unknown]";
                }
            }
            return null;
        }
        return null;
    }

    protected boolean shouldLog(HttpServletRequest request) {
        return true;
    }
}