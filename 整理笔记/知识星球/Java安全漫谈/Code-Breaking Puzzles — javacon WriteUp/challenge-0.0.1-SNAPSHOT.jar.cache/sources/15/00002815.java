package org.thymeleaf.engine;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IModelVisitor;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/StandaloneElementTag.class */
public final class StandaloneElementTag extends AbstractProcessableElementTag implements IStandaloneElementTag, IEngineTemplateEvent {
    final boolean minimized;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StandaloneElementTag(TemplateMode templateMode, ElementDefinition elementDefinition, String elementCompleteName, Attributes attributes, boolean synthetic, boolean minimized) {
        super(templateMode, elementDefinition, elementCompleteName, attributes, synthetic);
        Validate.isTrue(minimized || templateMode == TemplateMode.HTML, "Not-minimized standalone elements are only allowed in HTML template mode (is " + templateMode + ")");
        this.minimized = minimized;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public StandaloneElementTag(TemplateMode templateMode, ElementDefinition elementDefinition, String elementCompleteName, Attributes attributes, boolean synthetic, boolean minimized, String templateName, int line, int col) {
        super(templateMode, elementDefinition, elementCompleteName, attributes, synthetic, templateName, line, col);
        Validate.isTrue(minimized || templateMode == TemplateMode.HTML, "Not-minimized standalone elements are only allowed in HTML template mode (is " + templateMode + ")");
        this.minimized = minimized;
    }

    @Override // org.thymeleaf.model.IStandaloneElementTag
    public boolean isMinimized() {
        return this.minimized;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.thymeleaf.engine.AbstractProcessableElementTag
    public StandaloneElementTag setAttribute(AttributeDefinitions attributeDefinitions, AttributeDefinition attributeDefinition, String completeName, String value, AttributeValueQuotes valueQuotes) {
        Attributes oldAttributes = this.attributes != null ? this.attributes : Attributes.EMPTY_ATTRIBUTES;
        Attributes newAttributes = oldAttributes.setAttribute(attributeDefinitions, this.templateMode, attributeDefinition, completeName, value, valueQuotes);
        return new StandaloneElementTag(this.templateMode, this.elementDefinition, this.elementCompleteName, newAttributes, this.synthetic, this.minimized, this.templateName, this.line, this.col);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.thymeleaf.engine.AbstractProcessableElementTag
    public StandaloneElementTag replaceAttribute(AttributeDefinitions attributeDefinitions, AttributeName oldName, AttributeDefinition newAttributeDefinition, String completeNewName, String value, AttributeValueQuotes valueQuotes) {
        Attributes oldAttributes = this.attributes != null ? this.attributes : Attributes.EMPTY_ATTRIBUTES;
        Attributes newAttributes = oldAttributes.replaceAttribute(attributeDefinitions, this.templateMode, oldName, newAttributeDefinition, completeNewName, value, valueQuotes);
        return new StandaloneElementTag(this.templateMode, this.elementDefinition, this.elementCompleteName, newAttributes, this.synthetic, this.minimized, this.templateName, this.line, this.col);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.thymeleaf.engine.AbstractProcessableElementTag
    public StandaloneElementTag removeAttribute(String prefix, String name) {
        Attributes oldAttributes = this.attributes != null ? this.attributes : Attributes.EMPTY_ATTRIBUTES;
        Attributes newAttributes = oldAttributes.removeAttribute(this.templateMode, prefix, name);
        if (oldAttributes == newAttributes) {
            return this;
        }
        return new StandaloneElementTag(this.templateMode, this.elementDefinition, this.elementCompleteName, newAttributes, this.synthetic, this.minimized, this.templateName, this.line, this.col);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.thymeleaf.engine.AbstractProcessableElementTag
    public StandaloneElementTag removeAttribute(String completeName) {
        Attributes oldAttributes = this.attributes != null ? this.attributes : Attributes.EMPTY_ATTRIBUTES;
        Attributes newAttributes = oldAttributes.removeAttribute(this.templateMode, completeName);
        if (oldAttributes == newAttributes) {
            return this;
        }
        return new StandaloneElementTag(this.templateMode, this.elementDefinition, this.elementCompleteName, newAttributes, this.synthetic, this.minimized, this.templateName, this.line, this.col);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.thymeleaf.engine.AbstractProcessableElementTag
    public StandaloneElementTag removeAttribute(AttributeName attributeName) {
        Attributes oldAttributes = this.attributes != null ? this.attributes : Attributes.EMPTY_ATTRIBUTES;
        Attributes newAttributes = oldAttributes.removeAttribute(attributeName);
        if (oldAttributes == newAttributes) {
            return this;
        }
        return new StandaloneElementTag(this.templateMode, this.elementDefinition, this.elementCompleteName, newAttributes, this.synthetic, this.minimized, this.templateName, this.line, this.col);
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
            if (this.minimized) {
                writer.write("/]");
                return;
            } else {
                writer.write("]");
                return;
            }
        }
        writer.write(60);
        writer.write(this.elementCompleteName);
        if (this.attributes != null) {
            this.attributes.write(writer);
        }
        if (this.minimized) {
            writer.write("/>");
        } else {
            writer.write(62);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static StandaloneElementTag asEngineStandaloneElementTag(IStandaloneElementTag standaloneElementTag) {
        Attributes attributes;
        String[] newInnerWhiteSpaces;
        if (standaloneElementTag instanceof StandaloneElementTag) {
            return (StandaloneElementTag) standaloneElementTag;
        }
        IAttribute[] originalAttributeArray = standaloneElementTag.getAllAttributes();
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
        return new StandaloneElementTag(standaloneElementTag.getTemplateMode(), standaloneElementTag.getElementDefinition(), standaloneElementTag.getElementCompleteName(), attributes, standaloneElementTag.isSynthetic(), standaloneElementTag.isMinimized(), standaloneElementTag.getTemplateName(), standaloneElementTag.getLine(), standaloneElementTag.getCol());
    }

    @Override // org.thymeleaf.engine.IEngineTemplateEvent
    public void beHandled(ITemplateHandler handler) {
        handler.handleStandaloneElement(this);
    }
}