package org.thymeleaf.engine;

import java.util.Set;
import org.thymeleaf.processor.element.IElementProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/XMLElementDefinition.class */
public final class XMLElementDefinition extends ElementDefinition {
    /* JADX INFO: Access modifiers changed from: package-private */
    public XMLElementDefinition(XMLElementName name, Set<IElementProcessor> associatedProcessors) {
        super(name, associatedProcessors);
    }
}