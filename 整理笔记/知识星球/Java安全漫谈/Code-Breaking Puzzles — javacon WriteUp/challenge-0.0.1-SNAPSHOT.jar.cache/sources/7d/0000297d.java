package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.inline.NoOpInliner;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.processor.cdatasection.AbstractCDATASectionProcessor;
import org.thymeleaf.processor.cdatasection.ICDATASectionStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardInliningCDATASectionProcessor.class */
public final class StandardInliningCDATASectionProcessor extends AbstractCDATASectionProcessor {
    public static final int PRECEDENCE = 1000;

    public StandardInliningCDATASectionProcessor(TemplateMode templateMode) {
        super(templateMode, 1000);
    }

    @Override // org.thymeleaf.processor.cdatasection.AbstractCDATASectionProcessor
    protected void doProcess(ITemplateContext context, ICDATASection cdataSection, ICDATASectionStructureHandler structureHandler) {
        CharSequence inlined;
        IInliner inliner = context.getInliner();
        if (inliner != null && inliner != NoOpInliner.INSTANCE && (inlined = inliner.inline(context, cdataSection)) != null && inlined != cdataSection) {
            structureHandler.setContent(inlined);
        }
    }
}