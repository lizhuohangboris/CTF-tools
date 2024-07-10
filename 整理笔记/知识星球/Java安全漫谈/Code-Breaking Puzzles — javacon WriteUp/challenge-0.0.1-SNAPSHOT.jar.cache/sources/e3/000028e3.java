package org.thymeleaf.spring5.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;
import org.thymeleaf.spring5.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.standard.util.StandardProcessorUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/processor/SpringInputPasswordFieldTagProcessor.class */
public final class SpringInputPasswordFieldTagProcessor extends AbstractSpringFieldTagProcessor {
    public static final String PASSWORD_INPUT_TYPE_ATTR_VALUE = "password";

    public SpringInputPasswordFieldTagProcessor(String dialectPrefix) {
        super(dialectPrefix, "input", "type", new String[]{PASSWORD_INPUT_TYPE_ATTR_VALUE}, true);
    }

    @Override // org.thymeleaf.spring5.processor.AbstractSpringFieldTagProcessor
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IThymeleafBindStatus bindStatus, IElementTagStructureHandler structureHandler) {
        String name = bindStatus.getExpression();
        String name2 = name == null ? "" : name;
        String id = computeId(context, tag, name2, false);
        StandardProcessorUtils.setAttribute(structureHandler, this.idAttributeDefinition, "id", id);
        StandardProcessorUtils.setAttribute(structureHandler, this.nameAttributeDefinition, "name", name2);
        StandardProcessorUtils.setAttribute(structureHandler, this.valueAttributeDefinition, "value", RequestDataValueProcessorUtils.processFormFieldValue(context, name2, "", PASSWORD_INPUT_TYPE_ATTR_VALUE));
    }
}