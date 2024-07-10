package org.thymeleaf.util;

import java.io.IOException;
import java.io.Writer;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.TemplateModel;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/LazyProcessingCharSequence.class */
public final class LazyProcessingCharSequence extends AbstractLazyCharSequence {
    private final ITemplateContext context;
    private final TemplateModel templateModel;

    public LazyProcessingCharSequence(ITemplateContext context, TemplateModel templateModel) {
        if (context == null) {
            throw new IllegalArgumentException("Template Context is null, which is forbidden");
        }
        if (templateModel == null) {
            throw new IllegalArgumentException("Template Model is null, which is forbidden");
        }
        this.context = context;
        this.templateModel = templateModel;
    }

    @Override // org.thymeleaf.util.AbstractLazyCharSequence
    protected String resolveText() {
        Writer stringWriter = new FastStringWriter();
        this.context.getConfiguration().getTemplateManager().process(this.templateModel, this.context, stringWriter);
        return stringWriter.toString();
    }

    @Override // org.thymeleaf.util.AbstractLazyCharSequence
    protected void writeUnresolved(Writer writer) throws IOException {
        this.context.getConfiguration().getTemplateManager().process(this.templateModel, this.context, writer);
    }
}