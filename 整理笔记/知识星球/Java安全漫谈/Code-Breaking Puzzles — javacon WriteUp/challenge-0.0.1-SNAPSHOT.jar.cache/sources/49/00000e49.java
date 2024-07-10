package org.attoparser;

import org.thymeleaf.engine.DocType;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/HtmlHeadCDATAContentElement.class */
final class HtmlHeadCDATAContentElement extends HtmlAutoOpenCDATAContentElement {
    private static final String[] ARRAY_HTML_HEAD = {DocType.DEFAULT_ELEMENT_NAME, "head"};

    public HtmlHeadCDATAContentElement(String name) {
        super(name, ARRAY_HTML_HEAD, null);
    }
}