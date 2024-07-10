package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardRemoveTagProcessor.class */
public final class StandardRemoveTagProcessor extends AbstractStandardExpressionAttributeTagProcessor {
    public static final int PRECEDENCE = 1600;
    public static final String ATTR_NAME = "remove";
    public static final String VALUE_ALL = "all";
    public static final String VALUE_ALL_BUT_FIRST = "all-but-first";
    public static final String VALUE_TAG = "tag";
    public static final String VALUE_TAGS = "tags";
    public static final String VALUE_BODY = "body";
    public static final String VALUE_NONE = "none";

    public StandardRemoveTagProcessor(TemplateMode templateMode, String dialectPrefix) {
        super(templateMode, dialectPrefix, ATTR_NAME, (int) PRECEDENCE, true, false);
    }

    @Override // org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, Object expressionResult, IElementTagStructureHandler structureHandler) {
        if (expressionResult != null) {
            String resultStr = expressionResult.toString();
            if ("all".equalsIgnoreCase(resultStr)) {
                structureHandler.removeElement();
            } else if (VALUE_TAG.equalsIgnoreCase(resultStr) || VALUE_TAGS.equalsIgnoreCase(resultStr)) {
                structureHandler.removeTags();
            } else if (VALUE_ALL_BUT_FIRST.equalsIgnoreCase(resultStr)) {
                structureHandler.removeAllButFirstChild();
            } else if (VALUE_BODY.equalsIgnoreCase(resultStr)) {
                structureHandler.removeBody();
            } else if (!"none".equalsIgnoreCase(resultStr)) {
                throw new TemplateProcessingException("Invalid value specified for \"" + attributeName + "\": only 'all', 'tag', 'body', 'none' and 'all-but-first' are allowed, but \"" + attributeValue + "\" was specified.");
            }
        }
    }
}