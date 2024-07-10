package org.springframework.http;

import java.util.concurrent.TimeUnit;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/CacheControl.class */
public class CacheControl {
    private long maxAge = -1;
    private boolean noCache = false;
    private boolean noStore = false;
    private boolean mustRevalidate = false;
    private boolean noTransform = false;
    private boolean cachePublic = false;
    private boolean cachePrivate = false;
    private boolean proxyRevalidate = false;
    private long staleWhileRevalidate = -1;
    private long staleIfError = -1;
    private long sMaxAge = -1;

    protected CacheControl() {
    }

    public static CacheControl empty() {
        return new CacheControl();
    }

    public static CacheControl maxAge(long maxAge, TimeUnit unit) {
        CacheControl cc = new CacheControl();
        cc.maxAge = unit.toSeconds(maxAge);
        return cc;
    }

    public static CacheControl noCache() {
        CacheControl cc = new CacheControl();
        cc.noCache = true;
        return cc;
    }

    public static CacheControl noStore() {
        CacheControl cc = new CacheControl();
        cc.noStore = true;
        return cc;
    }

    public CacheControl mustRevalidate() {
        this.mustRevalidate = true;
        return this;
    }

    public CacheControl noTransform() {
        this.noTransform = true;
        return this;
    }

    public CacheControl cachePublic() {
        this.cachePublic = true;
        return this;
    }

    public CacheControl cachePrivate() {
        this.cachePrivate = true;
        return this;
    }

    public CacheControl proxyRevalidate() {
        this.proxyRevalidate = true;
        return this;
    }

    public CacheControl sMaxAge(long sMaxAge, TimeUnit unit) {
        this.sMaxAge = unit.toSeconds(sMaxAge);
        return this;
    }

    public CacheControl staleWhileRevalidate(long staleWhileRevalidate, TimeUnit unit) {
        this.staleWhileRevalidate = unit.toSeconds(staleWhileRevalidate);
        return this;
    }

    public CacheControl staleIfError(long staleIfError, TimeUnit unit) {
        this.staleIfError = unit.toSeconds(staleIfError);
        return this;
    }

    @Nullable
    public String getHeaderValue() {
        StringBuilder ccValue = new StringBuilder();
        if (this.maxAge != -1) {
            appendDirective(ccValue, "max-age=" + Long.toString(this.maxAge));
        }
        if (this.noCache) {
            appendDirective(ccValue, "no-cache");
        }
        if (this.noStore) {
            appendDirective(ccValue, "no-store");
        }
        if (this.mustRevalidate) {
            appendDirective(ccValue, "must-revalidate");
        }
        if (this.noTransform) {
            appendDirective(ccValue, "no-transform");
        }
        if (this.cachePublic) {
            appendDirective(ccValue, "public");
        }
        if (this.cachePrivate) {
            appendDirective(ccValue, "private");
        }
        if (this.proxyRevalidate) {
            appendDirective(ccValue, "proxy-revalidate");
        }
        if (this.sMaxAge != -1) {
            appendDirective(ccValue, "s-maxage=" + Long.toString(this.sMaxAge));
        }
        if (this.staleIfError != -1) {
            appendDirective(ccValue, "stale-if-error=" + Long.toString(this.staleIfError));
        }
        if (this.staleWhileRevalidate != -1) {
            appendDirective(ccValue, "stale-while-revalidate=" + Long.toString(this.staleWhileRevalidate));
        }
        String ccHeaderValue = ccValue.toString();
        if (StringUtils.hasText(ccHeaderValue)) {
            return ccHeaderValue;
        }
        return null;
    }

    private void appendDirective(StringBuilder builder, String value) {
        if (builder.length() > 0) {
            builder.append(", ");
        }
        builder.append(value);
    }

    public String toString() {
        return "CacheControl [" + getHeaderValue() + "]";
    }
}