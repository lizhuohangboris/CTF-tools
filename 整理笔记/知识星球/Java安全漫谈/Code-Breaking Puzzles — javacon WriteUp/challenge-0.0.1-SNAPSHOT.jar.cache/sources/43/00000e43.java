package org.attoparser;

import org.thymeleaf.engine.DocType;
import org.thymeleaf.standard.processor.StandardRemoveTagProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/HtmlBodyElement.class */
final class HtmlBodyElement extends HtmlAutoOpenCloseElement {
    private static final String[] ARRAY_HTML_BODY = {DocType.DEFAULT_ELEMENT_NAME, StandardRemoveTagProcessor.VALUE_BODY};
    private static final String[] ARRAY_HEAD = {"head"};
    private static final String[] AUTO_CLOSE_LIMITS = {"script", "template", "element", "decorator", "content", "shadow"};

    /* JADX INFO: Access modifiers changed from: package-private */
    public HtmlBodyElement(String name) {
        super(name, ARRAY_HTML_BODY, null, ARRAY_HEAD, AUTO_CLOSE_LIMITS);
    }
}