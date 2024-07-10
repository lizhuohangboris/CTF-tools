package org.thymeleaf.processor.text;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IText;
import org.thymeleaf.processor.IProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/text/ITextProcessor.class */
public interface ITextProcessor extends IProcessor {
    void process(ITemplateContext iTemplateContext, IText iText, ITextStructureHandler iTextStructureHandler);
}