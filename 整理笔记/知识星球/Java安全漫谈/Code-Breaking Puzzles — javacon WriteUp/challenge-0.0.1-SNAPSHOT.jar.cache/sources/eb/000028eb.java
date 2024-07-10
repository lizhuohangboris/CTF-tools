package org.thymeleaf.spring5.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;
import org.thymeleaf.spring5.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.spring5.util.SpringValueFormatter;
import org.thymeleaf.standard.util.StandardProcessorUtils;
import org.thymeleaf.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/processor/SpringTextareaFieldTagProcessor.class */
public final class SpringTextareaFieldTagProcessor extends AbstractSpringFieldTagProcessor {
    public SpringTextareaFieldTagProcessor(String dialectPrefix) {
        super(dialectPrefix, "textarea", null, null, true);
    }

    @Override // org.thymeleaf.spring5.processor.AbstractSpringFieldTagProcessor
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IThymeleafBindStatus bindStatus, IElementTagStructureHandler structureHandler) {
        String name = bindStatus.getExpression();
        String name2 = name == null ? "" : name;
        String id = computeId(context, tag, name2, false);
        String value = SpringValueFormatter.getDisplayString(bindStatus.getValue(), bindStatus.getEditor(), true);
        String processedValue = RequestDataValueProcessorUtils.processFormFieldValue(context, name2, value, "textarea");
        if (!StringUtils.isEmpty(processedValue)) {
            char c0 = processedValue.charAt(0);
            if (c0 == '\n') {
                processedValue = '\n' + processedValue;
            } else if (c0 == '\r' && processedValue.length() > 1 && processedValue.charAt(1) == '\n') {
                processedValue = "\r\n" + processedValue;
            } else if (c0 == '\r') {
                processedValue = '\r' + processedValue;
            }
        }
        StandardProcessorUtils.setAttribute(structureHandler, this.idAttributeDefinition, "id", id);
        StandardProcessorUtils.setAttribute(structureHandler, this.nameAttributeDefinition, "name", name2);
        structureHandler.setBody((CharSequence) (processedValue == null ? "" : processedValue), false);
    }
}