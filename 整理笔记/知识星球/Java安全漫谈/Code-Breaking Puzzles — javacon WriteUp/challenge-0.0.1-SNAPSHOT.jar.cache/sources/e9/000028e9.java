package org.thymeleaf.spring5.processor;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;
import org.thymeleaf.spring5.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.standard.util.StandardProcessorUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/processor/SpringSelectFieldTagProcessor.class */
public final class SpringSelectFieldTagProcessor extends AbstractSpringFieldTagProcessor {
    static final String OPTION_IN_SELECT_ATTR_NAME = "%%OPTION_IN_SELECT_ATTR_NAME%%";
    static final String OPTION_IN_SELECT_ATTR_VALUE = "%%OPTION_IN_SELECT_ATTR_VALUE%%";

    public SpringSelectFieldTagProcessor(String dialectPrefix) {
        super(dialectPrefix, "select", null, null, true);
    }

    @Override // org.thymeleaf.spring5.processor.AbstractSpringFieldTagProcessor
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IThymeleafBindStatus bindStatus, IElementTagStructureHandler structureHandler) {
        String name = bindStatus.getExpression();
        String name2 = name == null ? "" : name;
        String id = computeId(context, tag, name2, false);
        boolean multiple = tag.hasAttribute(this.multipleAttributeDefinition.getAttributeName());
        StandardProcessorUtils.setAttribute(structureHandler, this.idAttributeDefinition, "id", id);
        StandardProcessorUtils.setAttribute(structureHandler, this.nameAttributeDefinition, "name", name2);
        structureHandler.setLocalVariable(OPTION_IN_SELECT_ATTR_NAME, attributeName);
        structureHandler.setLocalVariable(OPTION_IN_SELECT_ATTR_VALUE, attributeValue);
        if (multiple && !isDisabled(tag)) {
            IModelFactory modelFactory = context.getModelFactory();
            IModel hiddenMethodElementModel = modelFactory.createModel();
            String hiddenName = "_" + name2;
            String value = RequestDataValueProcessorUtils.processFormFieldValue(context, hiddenName, CustomBooleanEditor.VALUE_1, SpringInputGeneralFieldTagProcessor.HIDDEN_INPUT_TYPE_ATTR_VALUE);
            Map<String, String> hiddenAttributes = new LinkedHashMap<>(4, 1.0f);
            hiddenAttributes.put("type", SpringInputGeneralFieldTagProcessor.HIDDEN_INPUT_TYPE_ATTR_VALUE);
            hiddenAttributes.put("name", hiddenName);
            hiddenAttributes.put("value", value);
            IStandaloneElementTag hiddenElementTag = modelFactory.createStandaloneElementTag("input", hiddenAttributes, AttributeValueQuotes.DOUBLE, false, true);
            hiddenMethodElementModel.add(hiddenElementTag);
            structureHandler.insertBefore(hiddenMethodElementModel);
        }
    }

    private final boolean isDisabled(IProcessableElementTag tag) {
        return tag.hasAttribute(this.disabledAttributeDefinition.getAttributeName());
    }
}