package org.springframework.http;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/HttpCookie.class */
public class HttpCookie {
    private final String name;
    private final String value;

    public HttpCookie(String name, @Nullable String value) {
        Assert.hasLength(name, "'name' is required and must not be empty.");
        this.name = name;
        this.value = value != null ? value : "";
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HttpCookie)) {
            return false;
        }
        HttpCookie otherCookie = (HttpCookie) other;
        return this.name.equalsIgnoreCase(otherCookie.getName());
    }

    public String toString() {
        return this.name + '=' + this.value;
    }
}