package org.thymeleaf.processor.xmldeclaration;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IXMLDeclaration;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/xmldeclaration/AbstractXMLDeclarationProcessor.class */
public abstract class AbstractXMLDeclarationProcessor extends AbstractProcessor implements IXMLDeclarationProcessor {
    protected abstract void doProcess(ITemplateContext iTemplateContext, IXMLDeclaration iXMLDeclaration, IXMLDeclarationStructureHandler iXMLDeclarationStructureHandler);

    public AbstractXMLDeclarationProcessor(TemplateMode templateMode, int precedence) {
        super(templateMode, precedence);
    }

    @Override // org.thymeleaf.processor.xmldeclaration.IXMLDeclarationProcessor
    public final void process(ITemplateContext context, IXMLDeclaration xmlDeclaration, IXMLDeclarationStructureHandler structureHandler) {
        try {
            doProcess(context, xmlDeclaration, structureHandler);
        } catch (TemplateProcessingException e) {
            if (xmlDeclaration.hasLocation()) {
                if (!e.hasTemplateName()) {
                    e.setTemplateName(xmlDeclaration.getTemplateName());
                }
                if (!e.hasLineAndCol()) {
                    e.setLineAndCol(xmlDeclaration.getLine(), xmlDeclaration.getCol());
                }
            }
            throw e;
        } catch (Exception e2) {
            throw new TemplateProcessingException("Error during execution of processor '" + getClass().getName() + "'", xmlDeclaration.getTemplateName(), xmlDeclaration.getLine(), xmlDeclaration.getCol(), e2);
        }
    }
}