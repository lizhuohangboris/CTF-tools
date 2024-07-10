package org.thymeleaf.engine;

import java.util.Set;
import org.thymeleaf.processor.element.IElementProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/HTMLElementDefinition.class */
public final class HTMLElementDefinition extends ElementDefinition {
    final HTMLElementType type;

    /* JADX INFO: Access modifiers changed from: package-private */
    public HTMLElementDefinition(HTMLElementName name, HTMLElementType type, Set<IElementProcessor> associatedProcessors) {
        super(name, associatedProcessors);
        this.type = type;
    }

    public HTMLElementType getType() {
        return this.type;
    }
}