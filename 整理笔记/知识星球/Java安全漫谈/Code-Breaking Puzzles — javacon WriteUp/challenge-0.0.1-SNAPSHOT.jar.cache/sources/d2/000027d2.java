package org.thymeleaf.engine;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementProcessor;
import org.thymeleaf.processor.element.MatchingElementName;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.ProcessorComparators;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/AbstractProcessableElementTag.class */
public abstract class AbstractProcessableElementTag extends AbstractElementTag implements IProcessableElementTag {
    private static final IElementProcessor[] EMPTY_ASSOCIATED_PROCESSORS = new IElementProcessor[0];
    final Attributes attributes;
    private volatile IElementProcessor[] associatedProcessors;

    public abstract AbstractProcessableElementTag setAttribute(AttributeDefinitions attributeDefinitions, AttributeDefinition attributeDefinition, String str, String str2, AttributeValueQuotes attributeValueQuotes);

    public abstract AbstractProcessableElementTag replaceAttribute(AttributeDefinitions attributeDefinitions, AttributeName attributeName, AttributeDefinition attributeDefinition, String str, String str2, AttributeValueQuotes attributeValueQuotes);

    public abstract AbstractProcessableElementTag removeAttribute(String str, String str2);

    public abstract AbstractProcessableElementTag removeAttribute(String str);

    public abstract AbstractProcessableElementTag removeAttribute(AttributeName attributeName);

    public AbstractProcessableElementTag(TemplateMode templateMode, ElementDefinition elementDefinition, String elementCompleteName, Attributes attributes, boolean synthetic) {
        super(templateMode, elementDefinition, elementCompleteName, synthetic);
        this.associatedProcessors = null;
        this.attributes = attributes;
    }

    public AbstractProcessableElementTag(TemplateMode templateMode, ElementDefinition elementDefinition, String elementCompleteName, Attributes attributes, boolean synthetic, String templateName, int line, int col) {
        super(templateMode, elementDefinition, elementCompleteName, synthetic, templateName, line, col);
        this.associatedProcessors = null;
        this.attributes = attributes;
    }

    @Override // org.thymeleaf.model.IProcessableElementTag
    public final boolean hasAttribute(String completeName) {
        Validate.notNull(completeName, "Attribute name cannot be null");
        if (this.attributes == null) {
            return false;
        }
        return this.attributes.hasAttribute(this.templateMode, completeName);
    }

    @Override // org.thymeleaf.model.IProcessableElementTag
    public final boolean hasAttribute(String prefix, String name) {
        Validate.notNull(name, "Attribute name cannot be null");
        if (this.attributes == null) {
            return false;
        }
        return this.attributes.hasAttribute(this.templateMode, prefix, name);
    }

    @Override // org.thymeleaf.model.IProcessableElementTag
    public final boolean hasAttribute(AttributeName attributeName) {
        Validate.notNull(attributeName, "Attribute name cannot be null");
        if (this.attributes == null) {
            return false;
        }
        return this.attributes.hasAttribute(attributeName);
    }

    @Override // org.thymeleaf.model.IProcessableElementTag
    public final IAttribute getAttribute(String completeName) {
        Validate.notNull(completeName, "Attribute name cannot be null");
        if (this.attributes == null) {
            return null;
        }
        return this.attributes.getAttribute(this.templateMode, completeName);
    }

    @Override // org.thymeleaf.model.IProcessableElementTag
    public final IAttribute getAttribute(String prefix, String name) {
        Validate.notNull(name, "Attribute name cannot be null");
        if (this.attributes == null) {
            return null;
        }
        return this.attributes.getAttribute(this.templateMode, prefix, name);
    }

    @Override // org.thymeleaf.model.IProcessableElementTag
    public final IAttribute getAttribute(AttributeName attributeName) {
        Validate.notNull(attributeName, "Attribute name cannot be null");
        if (this.attributes == null) {
            return null;
        }
        return this.attributes.getAttribute(attributeName);
    }

    @Override // org.thymeleaf.model.IProcessableElementTag
    public final String getAttributeValue(String completeName) {
        Attribute attribute;
        Validate.notNull(completeName, "Attribute name cannot be null");
        if (this.attributes == null || (attribute = this.attributes.getAttribute(this.templateMode, completeName)) == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override // org.thymeleaf.model.IProcessableElementTag
    public final String getAttributeValue(String prefix, String name) {
        Attribute attribute;
        Validate.notNull(name, "Attribute name cannot be null");
        if (this.attributes == null || (attribute = this.attributes.getAttribute(this.templateMode, prefix, name)) == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override // org.thymeleaf.model.IProcessableElementTag
    public final String getAttributeValue(AttributeName attributeName) {
        Attribute attribute;
        Validate.notNull(attributeName, "Attribute name cannot be null");
        if (this.attributes == null || (attribute = this.attributes.getAttribute(attributeName)) == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override // org.thymeleaf.model.IProcessableElementTag
    public IAttribute[] getAllAttributes() {
        if (this.attributes == null) {
            return Attributes.EMPTY_ATTRIBUTE_ARRAY;
        }
        return this.attributes.getAllAttributes();
    }

    @Override // org.thymeleaf.model.IProcessableElementTag
    public Map<String, String> getAttributeMap() {
        if (this.attributes == null) {
            return Collections.emptyMap();
        }
        return this.attributes.getAttributeMap();
    }

    public IElementProcessor[] getAssociatedProcessors() {
        IElementProcessor[] p = this.associatedProcessors;
        if (p == null) {
            IElementProcessor[] computeProcessors = computeProcessors();
            p = computeProcessors;
            this.associatedProcessors = computeProcessors;
        }
        return p;
    }

    public boolean hasAssociatedProcessors() {
        return getAssociatedProcessors().length > 0;
    }

    private IElementProcessor[] computeProcessors() {
        int associatedProcessorCount = this.attributes != null ? this.attributes.getAssociatedProcessorCount() : 0;
        if (this.attributes == null || associatedProcessorCount == 0) {
            return this.elementDefinition.hasAssociatedProcessors ? this.elementDefinition.associatedProcessors : EMPTY_ASSOCIATED_PROCESSORS;
        }
        int elementProcessorCount = this.elementDefinition.hasAssociatedProcessors ? this.elementDefinition.associatedProcessors.length : 0;
        IElementProcessor[] processors = new IElementProcessor[elementProcessorCount + associatedProcessorCount];
        if (elementProcessorCount > 0) {
            System.arraycopy(this.elementDefinition.associatedProcessors, 0, processors, 0, elementProcessorCount);
        }
        int idx = elementProcessorCount;
        int n = this.attributes.attributes.length;
        while (true) {
            int i = n;
            n--;
            if (i == 0) {
                break;
            } else if (this.attributes.attributes[n].definition.hasAssociatedProcessors) {
                IElementProcessor[] attributeAssociatedProcessors = this.attributes.attributes[n].definition.associatedProcessors;
                for (int i2 = 0; i2 < attributeAssociatedProcessors.length; i2++) {
                    MatchingElementName matchingElementName = attributeAssociatedProcessors[i2].getMatchingElementName();
                    if (matchingElementName == null || matchingElementName.matches(this.elementDefinition.elementName)) {
                        int i3 = idx;
                        idx++;
                        processors[i3] = attributeAssociatedProcessors[i2];
                    }
                }
            }
        }
        if (idx < processors.length) {
            processors = (IElementProcessor[]) Arrays.copyOf(processors, idx);
        }
        if (processors.length > 1) {
            Arrays.sort(processors, ProcessorComparators.PROCESSOR_COMPARATOR);
        }
        return processors;
    }
}