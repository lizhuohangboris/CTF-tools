package org.springframework.http;

import java.time.Duration;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/ResponseCookie.class */
public final class ResponseCookie extends HttpCookie {
    private final Duration maxAge;
    @Nullable
    private final String domain;
    @Nullable
    private final String path;
    private final boolean secure;
    private final boolean httpOnly;
    @Nullable
    private final String sameSite;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/ResponseCookie$ResponseCookieBuilder.class */
    public interface ResponseCookieBuilder {
        ResponseCookieBuilder maxAge(Duration duration);

        ResponseCookieBuilder maxAge(long j);

        ResponseCookieBuilder path(String str);

        ResponseCookieBuilder domain(String str);

        ResponseCookieBuilder secure(boolean z);

        ResponseCookieBuilder httpOnly(boolean z);

        ResponseCookieBuilder sameSite(@Nullable String str);

        ResponseCookie build();
    }

    private ResponseCookie(String name, String value, Duration maxAge, @Nullable String domain, @Nullable String path, boolean secure, boolean httpOnly, @Nullable String sameSite) {
        super(name, value);
        Assert.notNull(maxAge, "Max age must not be null");
        this.maxAge = maxAge;
        this.domain = domain;
        this.path = path;
        this.secure = secure;
        this.httpOnly = httpOnly;
        this.sameSite = sameSite;
    }

    public Duration getMaxAge() {
        return this.maxAge;
    }

    @Nullable
    public String getDomain() {
        return this.domain;
    }

    @Nullable
    public String getPath() {
        return this.path;
    }

    public boolean isSecure() {
        return this.secure;
    }

    public boolean isHttpOnly() {
        return this.httpOnly;
    }

    @Nullable
    public String getSameSite() {
        return this.sameSite;
    }

    @Override // org.springframework.http.HttpCookie
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ResponseCookie)) {
            return false;
        }
        ResponseCookie otherCookie = (ResponseCookie) other;
        return getName().equalsIgnoreCase(otherCookie.getName()) && ObjectUtils.nullSafeEquals(this.path, otherCookie.getPath()) && ObjectUtils.nullSafeEquals(this.domain, otherCookie.getDomain());
    }

    @Override // org.springframework.http.HttpCookie
    public int hashCode() {
        int result = super.hashCode();
        return (31 * ((31 * result) + ObjectUtils.nullSafeHashCode(this.domain))) + ObjectUtils.nullSafeHashCode(this.path);
    }

    @Override // org.springframework.http.HttpCookie
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append('=').append(getValue());
        if (StringUtils.hasText(getPath())) {
            sb.append("; Path=").append(getPath());
        }
        if (StringUtils.hasText(this.domain)) {
            sb.append("; Domain=").append(this.domain);
        }
        if (!this.maxAge.isNegative()) {
            sb.append("; Max-Age=").append(this.maxAge.getSeconds());
            sb.append("; Expires=");
            long millis = this.maxAge.getSeconds() > 0 ? System.currentTimeMillis() + this.maxAge.toMillis() : 0L;
            sb.append(HttpHeaders.formatDate(millis));
        }
        if (this.secure) {
            sb.append("; Secure");
        }
        if (this.httpOnly) {
            sb.append("; HttpOnly");
        }
        if (StringUtils.hasText(this.sameSite)) {
            sb.append("; SameSite=").append(this.sameSite);
        }
        return sb.toString();
    }

    public static ResponseCookieBuilder from(final String name, final String value) {
        return new ResponseCookieBuilder() { // from class: org.springframework.http.ResponseCookie.1
            private Duration maxAge = Duration.ofSeconds(-1);
            @Nullable
            private String domain;
            @Nullable
            private String path;
            private boolean secure;
            private boolean httpOnly;
            @Nullable
            private String sameSite;

            @Override // org.springframework.http.ResponseCookie.ResponseCookieBuilder
            public ResponseCookieBuilder maxAge(Duration maxAge) {
                this.maxAge = maxAge;
                return this;
            }

            @Override // org.springframework.http.ResponseCookie.ResponseCookieBuilder
            public ResponseCookieBuilder maxAge(long maxAgeSeconds) {
                this.maxAge = maxAgeSeconds >= 0 ? Duration.ofSeconds(maxAgeSeconds) : Duration.ofSeconds(-1L);
                return this;
            }

            @Override // org.springframework.http.ResponseCookie.ResponseCookieBuilder
            public ResponseCookieBuilder domain(String domain) {
                this.domain = domain;
                return this;
            }

            @Override // org.springframework.http.ResponseCookie.ResponseCookieBuilder
            public ResponseCookieBuilder path(String path) {
                this.path = path;
                return this;
            }

            @Override // org.springframework.http.ResponseCookie.ResponseCookieBuilder
            public ResponseCookieBuilder secure(boolean secure) {
                this.secure = secure;
                return this;
            }

            @Override // org.springframework.http.ResponseCookie.ResponseCookieBuilder
            public ResponseCookieBuilder httpOnly(boolean httpOnly) {
                this.httpOnly = httpOnly;
                return this;
            }

            @Override // org.springframework.http.ResponseCookie.ResponseCookieBuilder
            public ResponseCookieBuilder sameSite(@Nullable String sameSite) {
                this.sameSite = sameSite;
                return this;
            }

            @Override // org.springframework.http.ResponseCookie.ResponseCookieBuilder
            public ResponseCookie build() {
                return new ResponseCookie(name, value, this.maxAge, this.domain, this.path, this.secure, this.httpOnly, this.sameSite);
            }
        };
    }
}