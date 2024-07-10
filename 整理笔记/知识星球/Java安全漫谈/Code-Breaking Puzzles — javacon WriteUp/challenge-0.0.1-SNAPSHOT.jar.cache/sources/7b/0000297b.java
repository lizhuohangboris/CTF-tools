package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.inline.NoOpInliner;
import org.thymeleaf.standard.inline.StandardInlineMode;
import org.thymeleaf.standard.inline.StandardTextInliner;
import org.thymeleaf.standard.inline.StandardXMLInliner;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardInlineXMLTagProcessor.class */
public final class StandardInlineXMLTagProcessor extends AbstractStandardTextInlineSettingTagProcessor {
    public static final int PRECEDENCE = 1000;
    public static final String ATTR_NAME = "inline";

    public StandardInlineXMLTagProcessor(String dialectPrefix) {
        super(TemplateMode.XML, dialectPrefix, "inline", 1000);
    }

    @Override // org.thymeleaf.standard.processor.AbstractStandardTextInlineSettingTagProcessor
    protected IInliner getInliner(ITemplateContext context, StandardInlineMode inlineMode) {
        switch (inlineMode) {
            case NONE:
                return NoOpInliner.INSTANCE;
            case XML:
                return new StandardXMLInliner(context.getConfiguration());
            case TEXT:
                return new StandardTextInliner(context.getConfiguration());
            default:
                throw new TemplateProcessingException("Invalid inline mode selected: " + inlineMode + ". Allowed inline modes in template mode " + getTemplateMode() + " are: \"" + StandardInlineMode.XML + "\", \"" + StandardInlineMode.TEXT + "\", \"" + StandardInlineMode.NONE + "\"");
        }
    }
}