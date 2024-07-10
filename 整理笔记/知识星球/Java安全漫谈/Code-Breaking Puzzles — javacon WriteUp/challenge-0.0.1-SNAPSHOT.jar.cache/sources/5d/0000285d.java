package org.thymeleaf.inline;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IText;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/inline/NoOpInliner.class */
public final class NoOpInliner implements IInliner {
    public static final NoOpInliner INSTANCE = new NoOpInliner();

    private NoOpInliner() {
    }

    @Override // org.thymeleaf.inline.IInliner
    public String getName() {
        return "NOOP";
    }

    @Override // org.thymeleaf.inline.IInliner
    public CharSequence inline(ITemplateContext context, IText text) {
        return null;
    }

    @Override // org.thymeleaf.inline.IInliner
    public CharSequence inline(ITemplateContext context, ICDATASection cdataSection) {
        return null;
    }

    @Override // org.thymeleaf.inline.IInliner
    public CharSequence inline(ITemplateContext context, IComment comment) {
        return null;
    }
}