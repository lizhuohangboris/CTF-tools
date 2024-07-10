package org.thymeleaf.processor.element;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/element/IElementModelProcessor.class */
public interface IElementModelProcessor extends IElementProcessor {
    void process(ITemplateContext iTemplateContext, IModel iModel, IElementModelStructureHandler iElementModelStructureHandler);
}