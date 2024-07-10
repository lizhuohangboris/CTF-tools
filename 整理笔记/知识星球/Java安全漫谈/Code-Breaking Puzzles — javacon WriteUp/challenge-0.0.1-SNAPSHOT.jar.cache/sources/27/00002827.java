package org.thymeleaf.engine;

import java.util.Set;
import org.thymeleaf.processor.element.IElementProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/TextAttributeDefinition.class */
public final class TextAttributeDefinition extends AttributeDefinition {
    /* JADX INFO: Access modifiers changed from: package-private */
    public TextAttributeDefinition(TextAttributeName name, Set<IElementProcessor> associatedProcessors) {
        super(name, associatedProcessors);
    }
}