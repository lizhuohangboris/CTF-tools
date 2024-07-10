package org.thymeleaf.spring5.processor;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;
import org.thymeleaf.spring5.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.spring5.util.SpringSelectedValueComparator;
import org.thymeleaf.standard.util.StandardProcessorUtils;
import org.unbescape.html.HtmlEscape;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/processor/SpringInputCheckboxFieldTagProcessor.class */
public final class SpringInputCheckboxFieldTagProcessor extends AbstractSpringFieldTagProcessor {
    public static final String CHECKBOX_INPUT_TYPE_ATTR_VALUE = "checkbox";
    private final boolean renderHiddenMarkersBeforeCheckboxes;

    public SpringInputCheckboxFieldTagProcessor(String dialectPrefix) {
        this(dialectPrefix, false);
    }

    public SpringInputCheckboxFieldTagProcessor(String dialectPrefix, boolean renderHiddenMarkersBeforeCheckboxes) {
        super(dialectPrefix, "input", "type", new String[]{CHECKBOX_INPUT_TYPE_ATTR_VALUE}, true);
        this.renderHiddenMarkersBeforeCheckboxes = renderHiddenMarkersBeforeCheckboxes;
    }

    @Override // org.thymeleaf.spring5.processor.AbstractSpringFieldTagProcessor
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IThymeleafBindStatus bindStatus, IElementTagStructureHandler structureHandler) {
        String value;
        boolean checked;
        String name = bindStatus.getExpression();
        String name2 = name == null ? "" : name;
        String id = computeId(context, tag, name2, true);
        Object boundValue = bindStatus.getValue();
        Class<?> valueType = bindStatus.getValueType();
        if (Boolean.class.equals(valueType) || Boolean.TYPE.equals(valueType)) {
            if (boundValue instanceof String) {
                boundValue = Boolean.valueOf((String) boundValue);
            }
            Boolean booleanValue = boundValue != null ? (Boolean) boundValue : Boolean.FALSE;
            value = "true";
            checked = booleanValue.booleanValue();
        } else {
            value = tag.getAttributeValue(this.valueAttributeDefinition.getAttributeName());
            if (value == null) {
                throw new TemplateProcessingException("Attribute \"value\" is required in \"input(checkbox)\" tags when binding to non-boolean values");
            }
            checked = SpringSelectedValueComparator.isSelected(bindStatus, HtmlEscape.unescapeHtml(value));
        }
        StandardProcessorUtils.setAttribute(structureHandler, this.idAttributeDefinition, "id", id);
        StandardProcessorUtils.setAttribute(structureHandler, this.nameAttributeDefinition, "name", name2);
        StandardProcessorUtils.setAttribute(structureHandler, this.valueAttributeDefinition, "value", RequestDataValueProcessorUtils.processFormFieldValue(context, name2, value, CHECKBOX_INPUT_TYPE_ATTR_VALUE));
        if (checked) {
            StandardProcessorUtils.setAttribute(structureHandler, this.checkedAttributeDefinition, "checked", "checked");
        } else {
            structureHandler.removeAttribute(this.checkedAttributeDefinition.getAttributeName());
        }
        if (!isDisabled(tag)) {
            IModelFactory modelFactory = context.getModelFactory();
            IModel hiddenTagModel = modelFactory.createModel();
            String hiddenName = "_" + name2;
            Map<String, String> hiddenAttributes = new LinkedHashMap<>(4, 1.0f);
            hiddenAttributes.put("type", SpringInputGeneralFieldTagProcessor.HIDDEN_INPUT_TYPE_ATTR_VALUE);
            hiddenAttributes.put("name", hiddenName);
            hiddenAttributes.put("value", RequestDataValueProcessorUtils.processFormFieldValue(context, hiddenName, CustomBooleanEditor.VALUE_ON, SpringInputGeneralFieldTagProcessor.HIDDEN_INPUT_TYPE_ATTR_VALUE));
            IStandaloneElementTag hiddenTag = modelFactory.createStandaloneElementTag("input", hiddenAttributes, AttributeValueQuotes.DOUBLE, false, true);
            hiddenTagModel.add(hiddenTag);
            if (this.renderHiddenMarkersBeforeCheckboxes) {
                structureHandler.insertBefore(hiddenTagModel);
            } else {
                structureHandler.insertImmediatelyAfter(hiddenTagModel, false);
            }
        }
    }

    private final boolean isDisabled(IProcessableElementTag tag) {
        return tag.hasAttribute(this.disabledAttributeDefinition.getAttributeName());
    }
}