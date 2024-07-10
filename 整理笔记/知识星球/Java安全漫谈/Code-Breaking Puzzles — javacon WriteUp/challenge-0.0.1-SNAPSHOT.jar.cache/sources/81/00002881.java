package org.thymeleaf.processor.cdatasection;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.processor.IProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/cdatasection/ICDATASectionProcessor.class */
public interface ICDATASectionProcessor extends IProcessor {
    void process(ITemplateContext iTemplateContext, ICDATASection iCDATASection, ICDATASectionStructureHandler iCDATASectionStructureHandler);
}