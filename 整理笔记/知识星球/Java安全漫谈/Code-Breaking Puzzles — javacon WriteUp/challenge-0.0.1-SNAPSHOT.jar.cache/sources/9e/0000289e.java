package org.thymeleaf.processor.xmldeclaration;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IXMLDeclaration;
import org.thymeleaf.processor.IProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/xmldeclaration/IXMLDeclarationProcessor.class */
public interface IXMLDeclarationProcessor extends IProcessor {
    void process(ITemplateContext iTemplateContext, IXMLDeclaration iXMLDeclaration, IXMLDeclarationStructureHandler iXMLDeclarationStructureHandler);
}