package org.thymeleaf.processor.element;

import org.thymeleaf.processor.IProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/element/IElementProcessor.class */
public interface IElementProcessor extends IProcessor {
    MatchingElementName getMatchingElementName();

    MatchingAttributeName getMatchingAttributeName();
}