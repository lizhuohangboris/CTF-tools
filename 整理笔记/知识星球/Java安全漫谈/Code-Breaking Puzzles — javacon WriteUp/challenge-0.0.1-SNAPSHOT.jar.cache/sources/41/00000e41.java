package org.attoparser;

import org.thymeleaf.engine.DocType;
import org.thymeleaf.standard.processor.StandardRemoveTagProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/HtmlBodyAutoCloseElement.class */
final class HtmlBodyAutoCloseElement extends HtmlAutoOpenCloseElement {
    private static final String[] ARRAY_HTML_BODY = {DocType.DEFAULT_ELEMENT_NAME, StandardRemoveTagProcessor.VALUE_BODY};

    /* JADX INFO: Access modifiers changed from: package-private */
    public HtmlBodyAutoCloseElement(String name, String[] autoCloseElements, String[] autoCloseLimits) {
        super(name, ARRAY_HTML_BODY, null, autoCloseElements, autoCloseLimits);
    }
}