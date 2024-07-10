package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.ITemplateEnd;
import org.thymeleaf.model.ITemplateStart;
import org.thymeleaf.processor.templateboundaries.AbstractTemplateBoundariesProcessor;
import org.thymeleaf.processor.templateboundaries.ITemplateBoundariesStructureHandler;
import org.thymeleaf.standard.inline.StandardCSSInliner;
import org.thymeleaf.standard.inline.StandardHTMLInliner;
import org.thymeleaf.standard.inline.StandardJavaScriptInliner;
import org.thymeleaf.standard.inline.StandardTextInliner;
import org.thymeleaf.standard.inline.StandardXMLInliner;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardInlineEnablementTemplateBoundariesProcessor.class */
public final class StandardInlineEnablementTemplateBoundariesProcessor extends AbstractTemplateBoundariesProcessor {
    public static final int PRECEDENCE = 10;

    public StandardInlineEnablementTemplateBoundariesProcessor(TemplateMode templateMode) {
        super(templateMode, 10);
    }

    @Override // org.thymeleaf.processor.templateboundaries.AbstractTemplateBoundariesProcessor
    public void doProcessTemplateStart(ITemplateContext context, ITemplateStart templateStart, ITemplateBoundariesStructureHandler structureHandler) {
        switch (getTemplateMode()) {
            case HTML:
                structureHandler.setInliner(new StandardHTMLInliner(context.getConfiguration()));
                return;
            case XML:
                structureHandler.setInliner(new StandardXMLInliner(context.getConfiguration()));
                return;
            case TEXT:
                structureHandler.setInliner(new StandardTextInliner(context.getConfiguration()));
                return;
            case JAVASCRIPT:
                structureHandler.setInliner(new StandardJavaScriptInliner(context.getConfiguration()));
                return;
            case CSS:
                structureHandler.setInliner(new StandardCSSInliner(context.getConfiguration()));
                return;
            case RAW:
                structureHandler.setInliner(null);
                return;
            default:
                throw new TemplateProcessingException("Unrecognized template mode: " + getTemplateMode() + ", cannot initialize inlining!");
        }
    }

    @Override // org.thymeleaf.processor.templateboundaries.AbstractTemplateBoundariesProcessor
    public void doProcessTemplateEnd(ITemplateContext context, ITemplateEnd templateEnd, ITemplateBoundariesStructureHandler structureHandler) {
    }
}