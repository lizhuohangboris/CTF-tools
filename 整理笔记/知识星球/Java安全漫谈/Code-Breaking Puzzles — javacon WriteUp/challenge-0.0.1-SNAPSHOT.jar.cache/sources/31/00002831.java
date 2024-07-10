package org.thymeleaf.engine;

import java.util.Set;
import org.thymeleaf.processor.element.IElementProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/XMLAttributeDefinition.class */
public final class XMLAttributeDefinition extends AttributeDefinition {
    /* JADX INFO: Access modifiers changed from: package-private */
    public XMLAttributeDefinition(XMLAttributeName name, Set<IElementProcessor> associatedProcessors) {
        super(name, associatedProcessors);
    }
}