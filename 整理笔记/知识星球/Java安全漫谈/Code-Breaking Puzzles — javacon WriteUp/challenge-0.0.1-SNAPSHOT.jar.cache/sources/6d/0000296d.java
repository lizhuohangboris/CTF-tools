package org.thymeleaf.standard.processor;

import org.springframework.validation.DefaultBindingErrorProcessor;
import org.springframework.web.servlet.tags.form.AbstractHtmlInputElementTag;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring5.processor.SpringInputGeneralFieldTagProcessor;
import org.thymeleaf.standard.util.StandardProcessorUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EvaluationUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardConditionalFixedValueTagProcessor.class */
public final class StandardConditionalFixedValueTagProcessor extends AbstractStandardExpressionAttributeTagProcessor implements IAttributeDefinitionsAware {
    public static final int PRECEDENCE = 1000;
    public static final String[] ATTR_NAMES = {"async", "autofocus", "autoplay", "checked", "controls", "declare", "default", "defer", "disabled", "formnovalidate", SpringInputGeneralFieldTagProcessor.HIDDEN_INPUT_TYPE_ATTR_VALUE, "ismap", "loop", "multiple", "novalidate", "nowrap", "open", "pubdate", AbstractHtmlInputElementTag.READONLY_ATTRIBUTE, DefaultBindingErrorProcessor.MISSING_FIELD_ERROR_CODE, "reversed", "selected", "scoped", "seamless"};
    private static final TemplateMode TEMPLATE_MODE = TemplateMode.HTML;
    private final String targetAttributeCompleteName;
    private AttributeDefinition targetAttributeDefinition;

    public StandardConditionalFixedValueTagProcessor(String dialectPrefix, String attrName) {
        super(TEMPLATE_MODE, dialectPrefix, attrName, 1000, true, false);
        this.targetAttributeCompleteName = attrName;
    }

    @Override // org.thymeleaf.engine.IAttributeDefinitionsAware
    public void setAttributeDefinitions(AttributeDefinitions attributeDefinitions) {
        Validate.notNull(attributeDefinitions, "Attribute Definitions cannot be null");
        this.targetAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, this.targetAttributeCompleteName);
    }

    @Override // org.thymeleaf.standard.processor.AbstractStandardExpressionAttributeTagProcessor
    protected final void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, Object expressionResult, IElementTagStructureHandler structureHandler) {
        if (EvaluationUtils.evaluateAsBoolean(expressionResult)) {
            StandardProcessorUtils.setAttribute(structureHandler, this.targetAttributeDefinition, this.targetAttributeCompleteName, this.targetAttributeCompleteName);
        } else {
            structureHandler.removeAttribute(this.targetAttributeDefinition.getAttributeName());
        }
    }
}