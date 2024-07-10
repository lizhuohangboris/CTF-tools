package org.attoparser;

import org.thymeleaf.engine.DocType;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/HtmlVoidHeadElement.class */
final class HtmlVoidHeadElement extends HtmlVoidAutoOpenElement {
    private static final String[] ARRAY_HTML_HEAD = {DocType.DEFAULT_ELEMENT_NAME, "head"};

    /* JADX INFO: Access modifiers changed from: package-private */
    public HtmlVoidHeadElement(String name) {
        super(name, ARRAY_HTML_HEAD, null);
    }
}