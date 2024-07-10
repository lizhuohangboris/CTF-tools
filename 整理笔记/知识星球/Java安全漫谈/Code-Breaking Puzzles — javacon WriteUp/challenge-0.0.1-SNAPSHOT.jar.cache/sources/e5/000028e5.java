package org.thymeleaf.spring5.processor;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
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

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/processor/SpringMethodTagProcessor.class */
public final class SpringMethodTagProcessor extends AbstractStandardExpressionAttributeTagProcessor implements IAttributeDefinitionsAware {
    public static final int ATTR_PRECEDENCE = 990;
    public static final String TARGET_ATTR_NAME = "method";
    private static final TemplateMode TEMPLATE_MODE = TemplateMode.HTML;
    private static final String TYPE_ATTR_NAME = "type";
    private static final String NAME_ATTR_NAME = "name";
    private static final String VALUE_ATTR_NAME = "value";
    private AttributeDefinition targetAttributeDefinition;

    public SpringMethodTagProcessor(String dialectPrefix) {
        super(TEMPLATE_MODE, dialectPrefix, "method", 990, false, false);
    }

    @Override // org.thymeleaf.engine.IAttributeDefinitionsAware
    public void setAttributeDefinitions(AttributeDefinitions attributeDefinitions) {
        Validate.notNull(attributeDefinitions, "Attribute Definitions cannot be null");
        this.targetAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, "method");
    }

    @Override // org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor
    protected final void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, Object expressionResult, IElementTagStructureHandler structureHandler) {
        String newAttributeValue = HtmlEscape.escapeHtml4Xml(expressionResult == null ? null : expressionResult.toString());
        if (newAttributeValue == null || newAttributeValue.length() == 0) {
            structureHandler.removeAttribute(this.targetAttributeDefinition.getAttributeName());
            structureHandler.removeAttribute(attributeName);
        } else {
            StandardProcessorUtils.replaceAttribute(structureHandler, attributeName, this.targetAttributeDefinition, "method", newAttributeValue);
        }
        if (newAttributeValue != null && "form".equalsIgnoreCase(tag.getElementCompleteName()) && !isMethodBrowserSupported(newAttributeValue)) {
            StandardProcessorUtils.setAttribute(structureHandler, this.targetAttributeDefinition, "method", "post");
            IModelFactory modelFactory = context.getModelFactory();
            IModel hiddenMethodModel = modelFactory.createModel();
            String value = RequestDataValueProcessorUtils.processFormFieldValue(context, "_method", newAttributeValue, SpringInputGeneralFieldTagProcessor.HIDDEN_INPUT_TYPE_ATTR_VALUE);
            Map<String, String> hiddenAttributes = new LinkedHashMap<>(4, 1.0f);
            hiddenAttributes.put("type", SpringInputGeneralFieldTagProcessor.HIDDEN_INPUT_TYPE_ATTR_VALUE);
            hiddenAttributes.put("name", "_method");
            hiddenAttributes.put("value", value);
            IStandaloneElementTag hiddenMethodElementTag = modelFactory.createStandaloneElementTag("input", hiddenAttributes, AttributeValueQuotes.DOUBLE, false, true);
            hiddenMethodModel.add(hiddenMethodElementTag);
            structureHandler.insertImmediatelyAfter(hiddenMethodModel, false);
        }
    }

    protected boolean isMethodBrowserSupported(String method) {
        return BeanUtil.PREFIX_GETTER_GET.equalsIgnoreCase(method) || "post".equalsIgnoreCase(method);
    }
}