package org.thymeleaf.processor.doctype;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.processor.IProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/doctype/IDocTypeProcessor.class */
public interface IDocTypeProcessor extends IProcessor {
    void process(ITemplateContext iTemplateContext, IDocType iDocType, IDocTypeStructureHandler iDocTypeStructureHandler);
}