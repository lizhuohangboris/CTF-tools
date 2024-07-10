package org.thymeleaf.spring5.processor;

import java.util.LinkedHashMap;
import java.util.Map;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring5.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor;
import org.thymeleaf.standard.util.StandardProcessorUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;
import org.unbescape.html.HtmlEscape;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/processor/SpringActionTagProcessor.class */
public final class SpringActionTagProcessor extends AbstractStandardExpressionAttributeTagProcessor implements IAttributeDefinitionsAware {
    public static final int ATTR_PRECEDENCE = 1000;
    public static final String TARGET_ATTR_NAME = "action";
    private static final TemplateMode TEMPLATE_MODE = TemplateMode.HTML;
    private static final String METHOD_ATTR_NAME = "method";
    private static final String TYPE_ATTR_NAME = "type";
    private static final String NAME_ATTR_NAME = "name";
    private static final String VALUE_ATTR_NAME = "value";
    private static final String METHOD_ATTR_DEFAULT_VALUE = "GET";
    private AttributeDefinition targetAttributeDefinition;
    private AttributeDefinition methodAttributeDefinition;

    public SpringActionTagProcessor(String dialectPrefix) {
        super(TEMPLATE_MODE, dialectPrefix, "action", 1000, false, false);
    }

    @Override // org.thymeleaf.engine.IAttributeDefinitionsAware
    public void setAttributeDefinitions(AttributeDefinitions attributeDefinitions) {
        Validate.notNull(attributeDefinitions, "Attribute Definitions cannot be null");
        this.targetAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, "action");
        this.methodAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, "method");
    }

    @Override // org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor
    protected final void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, Object expressionResult, IElementTagStructureHandler structureHandler) {
        Map<String, String> extraHiddenFields;
        String newAttributeValue = HtmlEscape.escapeHtml4Xml(expressionResult == null ? "" : expressionResult.toString());
        String methodAttributeValue = tag.getAttributeValue(this.methodAttributeDefinition.getAttributeName());
        String httpMethod = methodAttributeValue == null ? "GET" : methodAttributeValue;
        String newAttributeValue2 = RequestDataValueProcessorUtils.processAction(context, newAttributeValue, httpMethod);
        StandardProcessorUtils.replaceAttribute(structureHandler, attributeName, this.targetAttributeDefinition, "action", newAttributeValue2 == null ? "" : newAttributeValue2);
        if ("form".equalsIgnoreCase(tag.getElementCompleteName()) && (extraHiddenFields = RequestDataValueProcessorUtils.getExtraHiddenFields(context)) != null && extraHiddenFields.size() > 0) {
            IModelFactory modelFactory = context.getModelFactory();
            IModel extraHiddenElementTags = modelFactory.createModel();
            for (Map.Entry<String, String> extraHiddenField : extraHiddenFields.entrySet()) {
                Map<String, String> extraHiddenAttributes = new LinkedHashMap<>(4, 1.0f);
                extraHiddenAttributes.put("type", SpringInputGeneralFieldTagProcessor.HIDDEN_INPUT_TYPE_ATTR_VALUE);
                extraHiddenAttributes.put("name", extraHiddenField.getKey());
                extraHiddenAttributes.put("value", extraHiddenField.getValue());
                IStandaloneElementTag extraHiddenElementTag = modelFactory.createStandaloneElementTag("input", extraHiddenAttributes, AttributeValueQuotes.DOUBLE, false, true);
                extraHiddenElementTags.add(extraHiddenElementTag);
            }
            structureHandler.insertImmediatelyAfter(extraHiddenElementTags, false);
        }
    }
}