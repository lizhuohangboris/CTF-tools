package org.thymeleaf.engine;

import java.util.Set;
import org.thymeleaf.processor.element.IElementProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/HTMLAttributeDefinition.class */
public final class HTMLAttributeDefinition extends AttributeDefinition {
    final boolean booleanAttribute;

    /* JADX INFO: Access modifiers changed from: package-private */
    public HTMLAttributeDefinition(HTMLAttributeName name, boolean booleanAttribute, Set<IElementProcessor> associatedProcessors) {
        super(name, associatedProcessors);
        this.booleanAttribute = booleanAttribute;
    }

    public boolean isBooleanAttribute() {
        return this.booleanAttribute;
    }
}