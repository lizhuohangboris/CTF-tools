package org.thymeleaf.engine;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/HTMLElementType.class */
public enum HTMLElementType {
    VOID(true),
    RAW_TEXT(false),
    ESCAPABLE_RAW_TEXT(false),
    FOREIGN(false),
    NORMAL(false);
    
    final boolean isVoid;

    HTMLElementType(boolean voidElement) {
        this.isVoid = voidElement;
    }

    public boolean isVoid() {
        return this.isVoid;
    }
}