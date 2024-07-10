package org.thymeleaf.inline;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IText;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/inline/IInliner.class */
public interface IInliner {
    String getName();

    CharSequence inline(ITemplateContext iTemplateContext, IText iText);

    CharSequence inline(ITemplateContext iTemplateContext, ICDATASection iCDATASection);

    CharSequence inline(ITemplateContext iTemplateContext, IComment iComment);
}