package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.EngineEventUtils;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.inline.NoOpInliner;
import org.thymeleaf.model.IText;
import org.thymeleaf.processor.text.AbstractTextProcessor;
import org.thymeleaf.processor.text.ITextStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardInliningTextProcessor.class */
public final class StandardInliningTextProcessor extends AbstractTextProcessor {
    public static final int PRECEDENCE = 1000;

    public StandardInliningTextProcessor(TemplateMode templateMode) {
        super(templateMode, 1000);
    }

    @Override // org.thymeleaf.processor.text.AbstractTextProcessor
    protected void doProcess(ITemplateContext context, IText text, ITextStructureHandler structureHandler) {
        IInliner inliner;
        CharSequence inlined;
        if (!EngineEventUtils.isWhitespace(text) && (inliner = context.getInliner()) != null && inliner != NoOpInliner.INSTANCE && (inlined = inliner.inline(context, text)) != null && inlined != text) {
            structureHandler.setText(inlined);
        }
    }
}