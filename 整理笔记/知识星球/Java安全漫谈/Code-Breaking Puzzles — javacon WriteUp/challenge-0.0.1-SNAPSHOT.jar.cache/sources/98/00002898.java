package org.thymeleaf.processor.templateboundaries;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.ITemplateEnd;
import org.thymeleaf.model.ITemplateStart;
import org.thymeleaf.processor.IProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/templateboundaries/ITemplateBoundariesProcessor.class */
public interface ITemplateBoundariesProcessor extends IProcessor {
    void processTemplateStart(ITemplateContext iTemplateContext, ITemplateStart iTemplateStart, ITemplateBoundariesStructureHandler iTemplateBoundariesStructureHandler);

    void processTemplateEnd(ITemplateContext iTemplateContext, ITemplateEnd iTemplateEnd, ITemplateBoundariesStructureHandler iTemplateBoundariesStructureHandler);
}