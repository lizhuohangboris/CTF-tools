package org.thymeleaf.spring5.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/processor/SpringOptionInSelectFieldTagProcessor.class */
public final class SpringOptionInSelectFieldTagProcessor extends AbstractElementTagProcessor {
    public static final int ATTR_PRECEDENCE = 1005;
    public static final String OPTION_TAG_NAME = "option";

    public SpringOptionInSelectFieldTagProcessor(String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, OPTION_TAG_NAME, false, null, false, ATTR_PRECEDENCE);
    }

    @Override // org.thymeleaf.processor.element.AbstractElementTagProcessor
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
        AttributeName selectAttrNameToAdd = (AttributeName) context.getVariable("%%OPTION_IN_SELECT_ATTR_NAME%%");
        if (selectAttrNameToAdd == null) {
            return;
        }
        String selectAttrValueToAdd = (String) context.getVariable("%%OPTION_IN_SELECT_ATTR_VALUE%%");
        if (tag.hasAttribute(selectAttrNameToAdd) && !selectAttrValueToAdd.equals(tag.getAttributeValue(selectAttrNameToAdd))) {
            throw new TemplateProcessingException("If specified (which is not required), attribute \"" + selectAttrNameToAdd + "\" in \"option\" tag must have exactly the same value as in its containing \"select\" tag");
        }
        structureHandler.setAttribute(selectAttrNameToAdd.getCompleteAttributeNames()[0], selectAttrValueToAdd);
    }
}