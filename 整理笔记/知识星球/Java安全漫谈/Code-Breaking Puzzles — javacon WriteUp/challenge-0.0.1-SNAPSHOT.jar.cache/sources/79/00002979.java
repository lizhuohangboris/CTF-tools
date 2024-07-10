package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.inline.NoOpInliner;
import org.thymeleaf.standard.inline.StandardCSSInliner;
import org.thymeleaf.standard.inline.StandardInlineMode;
import org.thymeleaf.standard.inline.StandardJavaScriptInliner;
import org.thymeleaf.standard.inline.StandardTextInliner;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardInlineTextualTagProcessor.class */
public final class StandardInlineTextualTagProcessor extends AbstractStandardTextInlineSettingTagProcessor {
    public static final int PRECEDENCE = 1000;
    public static final String ATTR_NAME = "inline";

    public StandardInlineTextualTagProcessor(TemplateMode templateMode, String dialectPrefix) {
        super(templateMode, dialectPrefix, "inline", 1000);
        Validate.isTrue(templateMode.isText(), "Template mode must be a textual one");
    }

    @Override // org.thymeleaf.standard.processor.AbstractStandardTextInlineSettingTagProcessor
    protected IInliner getInliner(ITemplateContext context, StandardInlineMode inlineMode) {
        TemplateMode templateMode = getTemplateMode();
        switch (inlineMode) {
            case NONE:
                return NoOpInliner.INSTANCE;
            case TEXT:
                if (templateMode == TemplateMode.TEXT) {
                    return new StandardTextInliner(context.getConfiguration());
                }
                break;
            case JAVASCRIPT:
                if (templateMode == TemplateMode.JAVASCRIPT) {
                    return new StandardJavaScriptInliner(context.getConfiguration());
                }
                break;
            case CSS:
                if (templateMode == TemplateMode.CSS) {
                    return new StandardCSSInliner(context.getConfiguration());
                }
                break;
        }
        throw new TemplateProcessingException("Invalid inline mode selected: " + inlineMode + ". Allowed inline modes in template mode " + getTemplateMode() + " are: \"" + getTemplateMode() + "\" and \"" + StandardInlineMode.NONE + "\"");
    }
}