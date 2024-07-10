package org.thymeleaf.processor.text;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IText;
import org.thymeleaf.processor.AbstractProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/text/AbstractTextProcessor.class */
public abstract class AbstractTextProcessor extends AbstractProcessor implements ITextProcessor {
    protected abstract void doProcess(ITemplateContext iTemplateContext, IText iText, ITextStructureHandler iTextStructureHandler);

    public AbstractTextProcessor(TemplateMode templateMode, int precedence) {
        super(templateMode, precedence);
    }

    @Override // org.thymeleaf.processor.text.ITextProcessor
    public final void process(ITemplateContext context, IText text, ITextStructureHandler structureHandler) {
        try {
            doProcess(context, text, structureHandler);
        } catch (TemplateProcessingException e) {
            if (text.hasLocation()) {
                if (!e.hasTemplateName()) {
                    e.setTemplateName(text.getTemplateName());
                }
                if (!e.hasLineAndCol()) {
                    e.setLineAndCol(text.getLine(), text.getCol());
                }
            }
            throw e;
        } catch (Exception e2) {
            throw new TemplateProcessingException("Error during execution of processor '" + getClass().getName() + "'", text.getTemplateName(), text.getLine(), text.getCol(), e2);
        }
    }
}