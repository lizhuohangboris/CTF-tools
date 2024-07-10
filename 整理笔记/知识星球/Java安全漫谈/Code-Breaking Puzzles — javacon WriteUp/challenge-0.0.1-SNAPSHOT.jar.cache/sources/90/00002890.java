package org.thymeleaf.processor.element;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/element/IElementTagProcessor.class */
public interface IElementTagProcessor extends IElementProcessor {
    void process(ITemplateContext iTemplateContext, IProcessableElementTag iProcessableElementTag, IElementTagStructureHandler iElementTagStructureHandler);
}