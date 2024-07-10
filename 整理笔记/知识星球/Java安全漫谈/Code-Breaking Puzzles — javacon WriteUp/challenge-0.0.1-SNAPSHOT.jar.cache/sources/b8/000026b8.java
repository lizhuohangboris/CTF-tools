package org.springframework.web.servlet.tags;

import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/tags/Param.class */
public class Param {
    @Nullable
    private String name;
    @Nullable
    private String value;

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public String getName() {
        return this.name;
    }

    public void setValue(@Nullable String value) {
        this.value = value;
    }

    @Nullable
    public String getValue() {
        return this.value;
    }

    public String toString() {
        return "JSP Tag Param: name '" + this.name + "', value '" + this.value + "'";
    }
}