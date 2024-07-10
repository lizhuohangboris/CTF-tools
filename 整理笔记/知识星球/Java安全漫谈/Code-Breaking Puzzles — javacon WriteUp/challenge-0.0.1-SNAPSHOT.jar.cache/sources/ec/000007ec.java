package org.apache.catalina.core;

import java.io.IOException;
import java.util.Locale;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/ApplicationHttpResponse.class */
class ApplicationHttpResponse extends HttpServletResponseWrapper {
    protected boolean included;

    public ApplicationHttpResponse(HttpServletResponse response, boolean included) {
        super(response);
        this.included = false;
        setIncluded(included);
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public void reset() {
        if (!this.included || getResponse().isCommitted()) {
            getResponse().reset();
        }
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public void setContentLength(int len) {
        if (!this.included) {
            getResponse().setContentLength(len);
        }
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public void setContentLengthLong(long len) {
        if (!this.included) {
            getResponse().setContentLengthLong(len);
        }
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public void setContentType(String type) {
        if (!this.included) {
            getResponse().setContentType(type);
        }
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public void setLocale(Locale loc) {
        if (!this.included) {
            getResponse().setLocale(loc);
        }
    }

    @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
    public void setBufferSize(int size) {
        if (!this.included) {
            getResponse().setBufferSize(size);
        }
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void addCookie(Cookie cookie) {
        if (!this.included) {
            ((HttpServletResponse) getResponse()).addCookie(cookie);
        }
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void addDateHeader(String name, long value) {
        if (!this.included) {
            ((HttpServletResponse) getResponse()).addDateHeader(name, value);
        }
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void addHeader(String name, String value) {
        if (!this.included) {
            ((HttpServletResponse) getResponse()).addHeader(name, value);
        }
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void addIntHeader(String name, int value) {
        if (!this.included) {
            ((HttpServletResponse) getResponse()).addIntHeader(name, value);
        }
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void sendError(int sc) throws IOException {
        if (!this.included) {
            ((HttpServletResponse) getResponse()).sendError(sc);
        }
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void sendError(int sc, String msg) throws IOException {
        if (!this.included) {
            ((HttpServletResponse) getResponse()).sendError(sc, msg);
        }
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void sendRedirect(String location) throws IOException {
        if (!this.included) {
            ((HttpServletResponse) getResponse()).sendRedirect(location);
        }
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void setDateHeader(String name, long value) {
        if (!this.included) {
            ((HttpServletResponse) getResponse()).setDateHeader(name, value);
        }
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void setHeader(String name, String value) {
        if (!this.included) {
            ((HttpServletResponse) getResponse()).setHeader(name, value);
        }
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void setIntHeader(String name, int value) {
        if (!this.included) {
            ((HttpServletResponse) getResponse()).setIntHeader(name, value);
        }
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    public void setStatus(int sc) {
        if (!this.included) {
            ((HttpServletResponse) getResponse()).setStatus(sc);
        }
    }

    @Override // javax.servlet.http.HttpServletResponseWrapper, javax.servlet.http.HttpServletResponse
    @Deprecated
    public void setStatus(int sc, String msg) {
        if (!this.included) {
            ((HttpServletResponse) getResponse()).setStatus(sc, msg);
        }
    }

    void setIncluded(boolean included) {
        this.included = included;
    }

    void setResponse(HttpServletResponse response) {
        super.setResponse((ServletResponse) response);
    }
}