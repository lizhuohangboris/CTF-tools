package org.thymeleaf.model;

import org.thymeleaf.engine.ElementDefinition;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/model/IElementTag.class */
public interface IElementTag extends ITemplateEvent {
    TemplateMode getTemplateMode();

    String getElementCompleteName();

    ElementDefinition getElementDefinition();

    boolean isSynthetic();
}