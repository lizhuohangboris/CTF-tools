package org.thymeleaf.engine;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IModelVisitor;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.templatemode.TemplateMode;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/OpenElementTag.class */
public final class OpenElementTag extends AbstractProcessableElementTag implements IOpenElementTag, IEngineTemplateEvent {
    /* JADX INFO: Access modifiers changed from: package-private */
    public OpenElementTag(TemplateMode templateMode, ElementDefinition elementDefinition, String elementCompleteName, Attributes attributes, boolean synthetic) {
        super(templateMode, elementDefinition, elementCompleteName, attributes, synthetic);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public OpenElementTag(TemplateMode templateMode, ElementDefinition elementDefinition, String elementCompleteName, Attributes attributes, boolean synthetic, String templateName, int line, int col) {
        super(templateMode, elementDefinition, elementCompleteName, attributes, synthetic, templateName, line, col);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.thymeleaf.engine.AbstractProcessableElementTag
    public OpenElementTag setAttribute(AttributeDefinitions attributeDefinitions, AttributeDefinition attributeDefinition, String completeName, String value, AttributeValueQuotes valueQuotes) {
        Attributes oldAttributes = this.attributes != null ? this.attributes : Attributes.EMPTY_ATTRIBUTES;
        Attributes newAttributes = oldAttributes.setAttribute(attributeDefinitions, this.templateMode, attributeDefinition, completeName, value, valueQuotes);
        return new OpenElementTag(this.templateMode, this.elementDefinition, this.elementCompleteName, newAttributes, this.synthetic, this.templateName, this.line, this.col);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.thymeleaf.engine.AbstractProcessableElementTag
    public OpenElementTag replaceAttribute(AttributeDefinitions attributeDefinitions, AttributeName oldName, AttributeDefinition newAttributeDefinition, String completeNewName, String value, AttributeValueQuotes valueQuotes) {
        Attributes oldAttributes = this.attributes != null ? this.attributes : Attributes.EMPTY_ATTRIBUTES;
        Attributes newAttributes = oldAttributes.replaceAttribute(attributeDefinitions, this.templateMode, oldName, newAttributeDefinition, completeNewName, value, valueQuotes);
        return new OpenElementTag(this.templateMode, this.elementDefinition, this.elementCompleteName, newAttributes, this.synthetic, this.templateName, this.line, this.col);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.thymeleaf.engine.AbstractProcessableElementTag
    public OpenElementTag removeAttribute(String prefix, String name) {
        Attributes oldAttributes = this.attributes != null ? this.attributes : Attributes.EMPTY_ATTRIBUTES;
        Attributes newAttributes = oldAttributes.removeAttribute(this.templateMode, prefix, name);
        if (oldAttributes == newAttributes) {
            return this;
        }
        return new OpenElementTag(this.templateMode, this.elementDefinition, this.elementCompleteName, newAttributes, this.synthetic, this.templateName, this.line, this.col);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.thymeleaf.engine.AbstractProcessableElementTag
    public OpenElementTag removeAttribute(String completeName) {
        Attributes oldAttributes = this.attributes != null ? this.attributes : Attributes.EMPTY_ATTRIBUTES;
        Attributes newAttributes = oldAttributes.removeAttribute(this.templateMode, completeName);
        if (oldAttributes == newAttributes) {
            return this;
        }
        return new OpenElementTag(this.templateMode, this.elementDefinition, this.elementCompleteName, newAttributes, this.synthetic, this.templateName, this.line, this.col);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.thymeleaf.engine.AbstractProcessableElementTag
    public OpenElementTag removeAttribute(AttributeName attributeName) {
        Attributes oldAttributes = this.attributes != null ? this.attributes : Attributes.EMPTY_ATTRIBUTES;
        Attributes newAttributes = oldAttributes.removeAttribute(attributeName);
        if (oldAttributes == newAttributes) {
            return this;
        }
        return new OpenElementTag(this.templateMode, this.elementDefinition, this.elementCompleteName, newAttributes, this.synthetic, this.templateName, this.line, this.col);
    }

    @Override // org.thymeleaf.model.ITemplateEvent
    public void accept(IModelVisitor visitor) {
        visitor.visit(this);
    }

    @Override // org.thymeleaf.model.ITemplateEvent
    public void write(Writer writer) throws IOException {
        if (this.synthetic) {
            return;
        }
        if (this.templateMode.isText()) {
            writer.write("[#");
            writer.write(this.elementCompleteName);
            if (this.attributes != null) {
                this.attributes.write(writer);
            }
            writer.write("]");
            return;
        }
        writer.write(60);
        writer.write(this.elementCompleteName);
        if (this.attributes != null) {
            this.attributes.write(writer);
        }
        writer.write(62);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static OpenElementTag asEngineOpenElementTag(IOpenElementTag openElementTag) {
        Attributes attributes;
        String[] newInnerWhiteSpaces;
        if (openElementTag instanceof OpenElementTag) {
            return (OpenElementTag) openElementTag;
        }
        IAttribute[] originalAttributeArray = openElementTag.getAllAttributes();
        if (originalAttributeArray == null || originalAttributeArray.length == 0) {
            attributes = null;
        } else {
            Attribute[] newAttributeArray = new Attribute[originalAttributeArray.length];
            for (int i = 0; i < originalAttributeArray.length; i++) {
                IAttribute originalAttribute = originalAttributeArray[i];
                newAttributeArray[i] = new Attribute(originalAttribute.getAttributeDefinition(), originalAttribute.getAttributeCompleteName(), originalAttribute.getOperator(), originalAttribute.getValue(), originalAttribute.getValueQuotes(), originalAttribute.getTemplateName(), originalAttribute.getLine(), originalAttribute.getCol());
            }
            if (newAttributeArray.length == 1) {
                newInnerWhiteSpaces = Attributes.DEFAULT_WHITE_SPACE_ARRAY;
            } else {
                newInnerWhiteSpaces = new String[newAttributeArray.length];
                Arrays.fill(newInnerWhiteSpaces, " ");
            }
            attributes = new Attributes(newAttributeArray, newInnerWhiteSpaces);
        }
        return new OpenElementTag(openElementTag.getTemplateMode(), openElementTag.getElementDefinition(), openElementTag.getElementCompleteName(), attributes, openElementTag.isSynthetic(), openElementTag.getTemplateName(), openElementTag.getLine(), openElementTag.getCol());
    }

    @Override // org.thymeleaf.engine.IEngineTemplateEvent
    public void beHandled(ITemplateHandler handler) {
        handler.handleOpenElement(this);
    }
}