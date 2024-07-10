package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.inline.NoOpInliner;
import org.thymeleaf.standard.inline.StandardCSSInliner;
import org.thymeleaf.standard.inline.StandardHTMLInliner;
import org.thymeleaf.standard.inline.StandardInlineMode;
import org.thymeleaf.standard.inline.StandardJavaScriptInliner;
import org.thymeleaf.standard.inline.StandardTextInliner;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardInlineHTMLTagProcessor.class */
public final class StandardInlineHTMLTagProcessor extends AbstractStandardTextInlineSettingTagProcessor {
    public static final int PRECEDENCE = 1000;
    public static final String ATTR_NAME = "inline";

    public StandardInlineHTMLTagProcessor(String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, "inline", 1000);
    }

    @Override // org.thymeleaf.standard.processor.AbstractStandardTextInlineSettingTagProcessor
    protected IInliner getInliner(ITemplateContext context, StandardInlineMode inlineMode) {
        switch (inlineMode) {
            case NONE:
                return NoOpInliner.INSTANCE;
            case HTML:
                return new StandardHTMLInliner(context.getConfiguration());
            case TEXT:
                return new StandardTextInliner(context.getConfiguration());
            case JAVASCRIPT:
                return new StandardJavaScriptInliner(context.getConfiguration());
            case CSS:
                return new StandardCSSInliner(context.getConfiguration());
            default:
                throw new TemplateProcessingException("Invalid inline mode selected: " + inlineMode + ". Allowed inline modes in template mode " + getTemplateMode() + " are: \"" + StandardInlineMode.HTML + "\", \"" + StandardInlineMode.TEXT + "\", \"" + StandardInlineMode.JAVASCRIPT + "\", \"" + StandardInlineMode.CSS + "\" and \"" + StandardInlineMode.NONE + "\"");
        }
    }
}