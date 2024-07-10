package org.thymeleaf.spring5.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeDefinition;
import org.thymeleaf.engine.AttributeDefinitions;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.IAttributeDefinitionsAware;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;
import org.thymeleaf.spring5.naming.SpringContextVariableNames;
import org.thymeleaf.spring5.util.FieldUtils;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/processor/AbstractSpringFieldTagProcessor.class */
public abstract class AbstractSpringFieldTagProcessor extends AbstractAttributeTagProcessor implements IAttributeDefinitionsAware {
    public static final int ATTR_PRECEDENCE = 1700;
    public static final String ATTR_NAME = "field";
    private static final TemplateMode TEMPLATE_MODE = TemplateMode.HTML;
    protected static final String INPUT_TAG_NAME = "input";
    protected static final String SELECT_TAG_NAME = "select";
    protected static final String OPTION_TAG_NAME = "option";
    protected static final String TEXTAREA_TAG_NAME = "textarea";
    protected static final String ID_ATTR_NAME = "id";
    protected static final String TYPE_ATTR_NAME = "type";
    protected static final String NAME_ATTR_NAME = "name";
    protected static final String VALUE_ATTR_NAME = "value";
    protected static final String CHECKED_ATTR_NAME = "checked";
    protected static final String SELECTED_ATTR_NAME = "selected";
    protected static final String DISABLED_ATTR_NAME = "disabled";
    protected static final String MULTIPLE_ATTR_NAME = "multiple";
    private AttributeDefinition discriminatorAttributeDefinition;
    protected AttributeDefinition idAttributeDefinition;
    protected AttributeDefinition typeAttributeDefinition;
    protected AttributeDefinition nameAttributeDefinition;
    protected AttributeDefinition valueAttributeDefinition;
    protected AttributeDefinition checkedAttributeDefinition;
    protected AttributeDefinition selectedAttributeDefinition;
    protected AttributeDefinition disabledAttributeDefinition;
    protected AttributeDefinition multipleAttributeDefinition;
    private final String discriminatorAttrName;
    private final String[] discriminatorAttrValues;
    private final boolean removeAttribute;

    protected abstract void doProcess(ITemplateContext iTemplateContext, IProcessableElementTag iProcessableElementTag, AttributeName attributeName, String str, IThymeleafBindStatus iThymeleafBindStatus, IElementTagStructureHandler iElementTagStructureHandler);

    public AbstractSpringFieldTagProcessor(String dialectPrefix, String elementName, String discriminatorAttrName, String[] discriminatorAttrValues, boolean removeAttribute) {
        super(TEMPLATE_MODE, dialectPrefix, elementName, false, ATTR_NAME, true, 1700, false);
        this.discriminatorAttrName = discriminatorAttrName;
        this.discriminatorAttrValues = discriminatorAttrValues;
        this.removeAttribute = removeAttribute;
    }

    @Override // org.thymeleaf.engine.IAttributeDefinitionsAware
    public void setAttributeDefinitions(AttributeDefinitions attributeDefinitions) {
        Validate.notNull(attributeDefinitions, "Attribute Definitions cannot be null");
        this.discriminatorAttributeDefinition = this.discriminatorAttrName != null ? attributeDefinitions.forName(TEMPLATE_MODE, this.discriminatorAttrName) : null;
        this.idAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, "id");
        this.typeAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, "type");
        this.nameAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, "name");
        this.valueAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, "value");
        this.checkedAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, CHECKED_ATTR_NAME);
        this.selectedAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, SELECTED_ATTR_NAME);
        this.disabledAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, "disabled");
        this.multipleAttributeDefinition = attributeDefinitions.forName(TEMPLATE_MODE, MULTIPLE_ATTR_NAME);
    }

    private boolean matchesDiscriminator(IProcessableElementTag tag) {
        if (this.discriminatorAttrName == null) {
            return true;
        }
        boolean hasDiscriminatorAttr = tag.hasAttribute(this.discriminatorAttributeDefinition.getAttributeName());
        if (this.discriminatorAttrValues == null || this.discriminatorAttrValues.length == 0) {
            return hasDiscriminatorAttr;
        }
        String discriminatorTagValue = hasDiscriminatorAttr ? tag.getAttributeValue(this.discriminatorAttributeDefinition.getAttributeName()) : null;
        for (int i = 0; i < this.discriminatorAttrValues.length; i++) {
            String discriminatorAttrValue = this.discriminatorAttrValues[i];
            if (discriminatorAttrValue == null) {
                if (!hasDiscriminatorAttr || discriminatorTagValue == null) {
                    return true;
                }
            } else if (discriminatorAttrValue.equals(discriminatorTagValue)) {
                return true;
            }
        }
        return false;
    }

    @Override // org.thymeleaf.processor.element.AbstractAttributeTagProcessor
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        if (!matchesDiscriminator(tag)) {
            return;
        }
        if (this.removeAttribute) {
            structureHandler.removeAttribute(attributeName);
        }
        IThymeleafBindStatus bindStatus = FieldUtils.getBindStatus(context, attributeValue);
        if (bindStatus == null) {
            throw new TemplateProcessingException("Cannot process attribute '" + attributeName + "': no associated BindStatus could be found for the intended form binding operations. This can be due to the lack of a proper management of the Spring RequestContext, which is usually done through the ThymeleafView or ThymeleafReactiveView");
        }
        structureHandler.setLocalVariable(SpringContextVariableNames.THYMELEAF_FIELD_BIND_STATUS, bindStatus);
        doProcess(context, tag, attributeName, attributeValue, bindStatus, structureHandler);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final String computeId(ITemplateContext context, IProcessableElementTag tag, String name, boolean sequence) {
        String id = tag.getAttributeValue(this.idAttributeDefinition.getAttributeName());
        if (!StringUtils.isEmptyOrWhitespace(id)) {
            if (org.springframework.util.StringUtils.hasText(id)) {
                return id;
            }
            return null;
        }
        String id2 = FieldUtils.idFromName(name);
        if (sequence) {
            Integer count = context.getIdentifierSequences().getAndIncrementIDSeq(id2);
            return id2 + count.toString();
        }
        return id2;
    }
}