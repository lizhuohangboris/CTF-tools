package org.thymeleaf.util;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/NumberPointType.class */
public enum NumberPointType {
    POINT("POINT"),
    COMMA("COMMA"),
    WHITESPACE("WHITESPACE"),
    NONE("NONE"),
    DEFAULT("DEFAULT");
    
    private final String name;

    public static NumberPointType match(String name) {
        if ("NONE".equals(name)) {
            return NONE;
        }
        if ("DEFAULT".equals(name)) {
            return DEFAULT;
        }
        if ("POINT".equals(name)) {
            return POINT;
        }
        if ("COMMA".equals(name)) {
            return COMMA;
        }
        if ("WHITESPACE".equals(name)) {
            return WHITESPACE;
        }
        return null;
    }

    NumberPointType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.name;
    }
}