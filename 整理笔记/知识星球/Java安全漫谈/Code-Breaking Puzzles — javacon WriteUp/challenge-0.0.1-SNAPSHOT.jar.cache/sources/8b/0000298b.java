package org.thymeleaf.standard.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.AttributeNames;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.LoggingUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardSubstituteByTagProcessor.class */
public final class StandardSubstituteByTagProcessor extends AbstractStandardFragmentInsertionTagProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(StandardSubstituteByTagProcessor.class);
    public static final int PRECEDENCE = 100;
    public static final String ATTR_NAME = "substituteby";

    public StandardSubstituteByTagProcessor(TemplateMode templateMode, String dialectPrefix) {
        super(templateMode, dialectPrefix, ATTR_NAME, 100, true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.thymeleaf.standard.processor.AbstractStandardFragmentInsertionTagProcessor, org.thymeleaf.processor.element.AbstractAttributeTagProcessor
    public void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn("[THYMELEAF][{}][{}] Deprecated attribute {} found in template {}, line {}, col {}. Please use {} instead, this deprecated attribute will be removed in future versions of Thymeleaf.", TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(context.getTemplateData().getTemplate()), attributeName, tag.getTemplateName(), Integer.valueOf(tag.getAttribute(attributeName).getLine()), Integer.valueOf(tag.getAttribute(attributeName).getCol()), AttributeNames.forHTMLName(attributeName.getPrefix(), "replace"));
        }
        super.doProcess(context, tag, attributeName, attributeValue, structureHandler);
    }
}