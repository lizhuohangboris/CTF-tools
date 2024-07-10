package org.thymeleaf.engine;

import java.util.Arrays;
import java.util.Map;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IDocType;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IProcessingInstruction;
import org.thymeleaf.model.IStandaloneElementTag;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.model.IText;
import org.thymeleaf.model.IXMLDeclaration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/StandardModelFactory.class */
public class StandardModelFactory implements IModelFactory {
    private static final String[][] SYNTHETIC_INNER_WHITESPACES = {new String[0], Attributes.DEFAULT_WHITE_SPACE_ARRAY, new String[]{" ", " "}, new String[]{" ", " ", " "}, new String[]{" ", " ", " ", " "}, new String[]{" ", " ", " ", " ", " "}};
    private final IEngineConfiguration configuration;
    private final AttributeDefinitions attributeDefinitions;
    private final ElementDefinitions elementDefinitions;
    private final TemplateMode templateMode;

    public StandardModelFactory(IEngineConfiguration configuration, TemplateMode templateMode) {
        Validate.notNull(configuration, "Configuration cannot be null");
        Validate.notNull(configuration.getAttributeDefinitions(), "Attribute Definitions returned by Engine Configuration cannot be null");
        Validate.notNull(configuration.getElementDefinitions(), "Element Definitions returned by Engine Configuration cannot be null");
        Validate.notNull(templateMode, "Template Mode cannot be null");
        this.configuration = configuration;
        this.attributeDefinitions = this.configuration.getAttributeDefinitions();
        this.elementDefinitions = this.configuration.getElementDefinitions();
        this.templateMode = templateMode;
    }

    private void checkRestrictedEventForTextTemplateMode(String eventClass) {
        if (this.templateMode.isText()) {
            throw new TemplateProcessingException("Events of class " + eventClass + " cannot be created in a text-type template mode (" + this.templateMode + ")");
        }
    }

    @Override // org.thymeleaf.model.IModelFactory
    public IModel createModel() {
        return new Model(this.configuration, this.templateMode);
    }

    @Override // org.thymeleaf.model.IModelFactory
    public IModel createModel(ITemplateEvent event) {
        Model model = new Model(this.configuration, this.templateMode);
        model.add(event);
        return model;
    }

    @Override // org.thymeleaf.model.IModelFactory
    public IModel parse(TemplateData ownerTemplate, String template) {
        return this.configuration.getTemplateManager().parseString(ownerTemplate, template, 0, 0, this.templateMode, false);
    }

    @Override // org.thymeleaf.model.IModelFactory
    public ICDATASection createCDATASection(CharSequence content) {
        checkRestrictedEventForTextTemplateMode("CDATASection");
        return new CDATASection(content);
    }

    @Override // org.thymeleaf.model.IModelFactory
    public IComment createComment(CharSequence content) {
        checkRestrictedEventForTextTemplateMode("Comment");
        return new Comment(content);
    }

    @Override // org.thymeleaf.model.IModelFactory
    public IDocType createHTML5DocType() {
        checkRestrictedEventForTextTemplateMode("DocType");
        return new DocType(null, null);
    }

    @Override // org.thymeleaf.model.IModelFactory
    public IDocType createDocType(String publicId, String systemId) {
        checkRestrictedEventForTextTemplateMode("DocType");
        return new DocType(publicId, systemId);
    }

    @Override // org.thymeleaf.model.IModelFactory
    public IDocType createDocType(String keyword, String elementName, String publicId, String systemId, String internalSubset) {
        checkRestrictedEventForTextTemplateMode("DocType");
        return new DocType(keyword, elementName, publicId, systemId, internalSubset);
    }

    @Override // org.thymeleaf.model.IModelFactory
    public IProcessingInstruction createProcessingInstruction(String target, String content) {
        checkRestrictedEventForTextTemplateMode("ProcessingInstruction");
        return new ProcessingInstruction(target, content);
    }

    @Override // org.thymeleaf.model.IModelFactory
    public IText createText(CharSequence text) {
        return new Text(text);
    }

    @Override // org.thymeleaf.model.IModelFactory
    public IXMLDeclaration createXMLDeclaration(String version, String encoding, String standalone) {
        checkRestrictedEventForTextTemplateMode("XMLDeclaration");
        return new XMLDeclaration(XMLDeclaration.DEFAULT_KEYWORD, version, encoding, standalone);
    }

    @Override // org.thymeleaf.model.IModelFactory
    public IStandaloneElementTag createStandaloneElementTag(String elementName) {
        return createStandaloneElementTag(elementName, false, true);
    }

    @Override // org.thymeleaf.model.IModelFactory
    public IStandaloneElementTag createStandaloneElementTag(String elementName, String attributeName, String attributeValue) {
        return createStandaloneElementTag(elementName, attributeName, attributeValue, false, true);
    }

    @Override // org.thymeleaf.model.IModelFactory
    public IStandaloneElementTag createStandaloneElementTag(String elementName, boolean synthetic, boolean minimized) {
        ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementName);
        return new StandaloneElementTag(this.templateMode, elementDefinition, elementName, null, synthetic, minimized);
    }

    @Override // org.thymeleaf.model.IModelFactory
    public IStandaloneElementTag createStandaloneElementTag(String elementName, String attributeName, String attributeValue, boolean synthetic, boolean minimized) {
        ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementName);
        Attributes attributes = buildAttributes(new Attribute[]{buildAttribute(attributeName, attributeValue, null)});
        return new StandaloneElementTag(this.templateMode, elementDefinition, elementName, attributes, synthetic, minimized);
    }

    @Override // org.thymeleaf.model.IModelFactory
    public IStandaloneElementTag createStandaloneElementTag(String elementName, Map<String, String> attributes, AttributeValueQuotes attributeValueQuotes, boolean synthetic, boolean minimized) {
        ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementName);
        Attributes attributesObj = buildAttributes(buildAttributeArray(attributes, attributeValueQuotes));
        return new StandaloneElementTag(this.templateMode, elementDefinition, elementName, attributesObj, synthetic, minimized);
    }

    @Override // org.thymeleaf.model.IModelFactory
    public IOpenElementTag createOpenElementTag(String elementName) {
        return createOpenElementTag(elementName, false);
    }

    @Override // org.thymeleaf.model.IModelFactory
    public IOpenElementTag createOpenElementTag(String elementName, String attributeName, String attributeValue) {
        return createOpenElementTag(elementName, attributeName, attributeValue, false);
    }

    @Override // org.thymeleaf.model.IModelFactory
    public IOpenElementTag createOpenElementTag(String elementName, boolean synthetic) {
        ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementName);
        return new OpenElementTag(this.templateMode, elementDefinition, elementName, null, synthetic);
    }

    @Override // org.thymeleaf.model.IModelFactory
    public IOpenElementTag createOpenElementTag(String elementName, String attributeName, String attributeValue, boolean synthetic) {
        ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementName);
        Attributes attributes = buildAttributes(new Attribute[]{buildAttribute(attributeName, attributeValue, null)});
        return new OpenElementTag(this.templateMode, elementDefinition, elementName, attributes, synthetic);
    }

    @Override // org.thymeleaf.model.IModelFactory
    public IOpenElementTag createOpenElementTag(String elementName, Map<String, String> attributes, AttributeValueQuotes attributeValueQuotes, boolean synthetic) {
        ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementName);
        Attributes attributesObj = buildAttributes(buildAttributeArray(attributes, attributeValueQuotes));
        return new OpenElementTag(this.templateMode, elementDefinition, elementName, attributesObj, synthetic);
    }

    @Override // org.thymeleaf.model.IModelFactory
    public <T extends IProcessableElementTag> T setAttribute(T tag, String attributeName, String attributeValue) {
        if (tag == null) {
            return null;
        }
        if (tag instanceof IOpenElementTag) {
            return setAttribute((IOpenElementTag) tag, attributeName, attributeValue);
        }
        if (tag instanceof IStandaloneElementTag) {
            return setAttribute((IStandaloneElementTag) tag, attributeName, attributeValue);
        }
        throw new TemplateProcessingException("Unknown type of processable element tag: " + tag.getClass().getName());
    }

    @Override // org.thymeleaf.model.IModelFactory
    public <T extends IProcessableElementTag> T setAttribute(T tag, String attributeName, String attributeValue, AttributeValueQuotes attributeValueQuotes) {
        if (tag == null) {
            return null;
        }
        if (tag instanceof IOpenElementTag) {
            return setAttribute((IOpenElementTag) tag, attributeName, attributeValue, attributeValueQuotes);
        }
        if (tag instanceof IStandaloneElementTag) {
            return setAttribute((IStandaloneElementTag) tag, attributeName, attributeValue, attributeValueQuotes);
        }
        throw new TemplateProcessingException("Unknown type of processable element tag: " + tag.getClass().getName());
    }

    @Override // org.thymeleaf.model.IModelFactory
    public <T extends IProcessableElementTag> T replaceAttribute(T tag, AttributeName oldAttributeName, String attributeName, String attributeValue) {
        if (tag == null) {
            return null;
        }
        if (tag instanceof IOpenElementTag) {
            return replaceAttribute((IOpenElementTag) tag, oldAttributeName, attributeName, attributeValue);
        }
        if (tag instanceof IStandaloneElementTag) {
            return replaceAttribute((IStandaloneElementTag) tag, oldAttributeName, attributeName, attributeValue);
        }
        throw new TemplateProcessingException("Unknown type of processable element tag: " + tag.getClass().getName());
    }

    @Override // org.thymeleaf.model.IModelFactory
    public <T extends IProcessableElementTag> T replaceAttribute(T tag, AttributeName oldAttributeName, String attributeName, String attributeValue, AttributeValueQuotes attributeValueQuotes) {
        if (tag == null) {
            return null;
        }
        if (tag instanceof IOpenElementTag) {
            return replaceAttribute((IOpenElementTag) tag, oldAttributeName, attributeName, attributeValue, attributeValueQuotes);
        }
        if (tag instanceof IStandaloneElementTag) {
            return replaceAttribute((IStandaloneElementTag) tag, oldAttributeName, attributeName, attributeValue, attributeValueQuotes);
        }
        throw new TemplateProcessingException("Unknown type of processable element tag: " + tag.getClass().getName());
    }

    @Override // org.thymeleaf.model.IModelFactory
    public <T extends IProcessableElementTag> T removeAttribute(T tag, String attributeName) {
        if (tag == null) {
            return null;
        }
        if (tag instanceof IOpenElementTag) {
            return removeAttribute((IOpenElementTag) tag, attributeName);
        }
        if (tag instanceof IStandaloneElementTag) {
            return removeAttribute((IStandaloneElementTag) tag, attributeName);
        }
        throw new TemplateProcessingException("Unknown type of processable element tag: " + tag.getClass().getName());
    }

    @Override // org.thymeleaf.model.IModelFactory
    public <T extends IProcessableElementTag> T removeAttribute(T tag, String prefix, String name) {
        if (tag == null) {
            return null;
        }
        if (tag instanceof IOpenElementTag) {
            return removeAttribute((IOpenElementTag) tag, prefix, name);
        }
        if (tag instanceof IStandaloneElementTag) {
            return removeAttribute((IStandaloneElementTag) tag, prefix, name);
        }
        throw new TemplateProcessingException("Unknown type of processable element tag: " + tag.getClass().getName());
    }

    @Override // org.thymeleaf.model.IModelFactory
    public <T extends IProcessableElementTag> T removeAttribute(T tag, AttributeName attributeName) {
        if (tag == null) {
            return null;
        }
        if (tag instanceof IOpenElementTag) {
            return removeAttribute((IOpenElementTag) tag, attributeName);
        }
        if (tag instanceof IStandaloneElementTag) {
            return removeAttribute((IStandaloneElementTag) tag, attributeName);
        }
        throw new TemplateProcessingException("Unknown type of processable element tag: " + tag.getClass().getName());
    }

    private IStandaloneElementTag setAttribute(IStandaloneElementTag standaloneElementTag, String attributeName, String attributeValue) {
        if (!(standaloneElementTag instanceof StandaloneElementTag)) {
            return setAttribute((IStandaloneElementTag) StandaloneElementTag.asEngineStandaloneElementTag(standaloneElementTag), attributeName, attributeValue);
        }
        return ((StandaloneElementTag) standaloneElementTag).setAttribute(this.attributeDefinitions, (AttributeDefinition) null, attributeName, attributeValue, (AttributeValueQuotes) null);
    }

    private IStandaloneElementTag setAttribute(IStandaloneElementTag standaloneElementTag, String attributeName, String attributeValue, AttributeValueQuotes attributeValueQuotes) {
        if (!(standaloneElementTag instanceof StandaloneElementTag)) {
            return setAttribute((IStandaloneElementTag) StandaloneElementTag.asEngineStandaloneElementTag(standaloneElementTag), attributeName, attributeValue, attributeValueQuotes);
        }
        return ((StandaloneElementTag) standaloneElementTag).setAttribute(this.attributeDefinitions, (AttributeDefinition) null, attributeName, attributeValue, attributeValueQuotes);
    }

    private IStandaloneElementTag replaceAttribute(IStandaloneElementTag standaloneElementTag, AttributeName oldAttributeName, String attributeName, String attributeValue) {
        if (!(standaloneElementTag instanceof StandaloneElementTag)) {
            return replaceAttribute((IStandaloneElementTag) StandaloneElementTag.asEngineStandaloneElementTag(standaloneElementTag), oldAttributeName, attributeName, attributeValue);
        }
        return ((StandaloneElementTag) standaloneElementTag).replaceAttribute(this.attributeDefinitions, oldAttributeName, (AttributeDefinition) null, attributeName, attributeValue, (AttributeValueQuotes) null);
    }

    private IStandaloneElementTag replaceAttribute(IStandaloneElementTag standaloneElementTag, AttributeName oldAttributeName, String attributeName, String attributeValue, AttributeValueQuotes attributeValueQuotes) {
        if (!(standaloneElementTag instanceof StandaloneElementTag)) {
            return replaceAttribute((IStandaloneElementTag) StandaloneElementTag.asEngineStandaloneElementTag(standaloneElementTag), oldAttributeName, attributeName, attributeValue, attributeValueQuotes);
        }
        return ((StandaloneElementTag) standaloneElementTag).replaceAttribute(this.attributeDefinitions, oldAttributeName, (AttributeDefinition) null, attributeName, attributeValue, attributeValueQuotes);
    }

    private IStandaloneElementTag removeAttribute(IStandaloneElementTag standaloneElementTag, String attributeName) {
        if (!(standaloneElementTag instanceof StandaloneElementTag)) {
            return removeAttribute((IStandaloneElementTag) StandaloneElementTag.asEngineStandaloneElementTag(standaloneElementTag), attributeName);
        }
        return ((StandaloneElementTag) standaloneElementTag).removeAttribute(attributeName);
    }

    private IStandaloneElementTag removeAttribute(IStandaloneElementTag standaloneElementTag, String prefix, String name) {
        if (!(standaloneElementTag instanceof StandaloneElementTag)) {
            return removeAttribute((IStandaloneElementTag) StandaloneElementTag.asEngineStandaloneElementTag(standaloneElementTag), prefix, name);
        }
        return ((StandaloneElementTag) standaloneElementTag).removeAttribute(prefix, name);
    }

    private IStandaloneElementTag removeAttribute(IStandaloneElementTag standaloneElementTag, AttributeName attributeName) {
        if (!(standaloneElementTag instanceof StandaloneElementTag)) {
            return removeAttribute((IStandaloneElementTag) StandaloneElementTag.asEngineStandaloneElementTag(standaloneElementTag), attributeName);
        }
        return ((StandaloneElementTag) standaloneElementTag).removeAttribute(attributeName);
    }

    private IOpenElementTag setAttribute(IOpenElementTag openElementTag, String attributeName, String attributeValue) {
        if (!(openElementTag instanceof OpenElementTag)) {
            return setAttribute((IOpenElementTag) OpenElementTag.asEngineOpenElementTag(openElementTag), attributeName, attributeValue);
        }
        return ((OpenElementTag) openElementTag).setAttribute(this.attributeDefinitions, (AttributeDefinition) null, attributeName, attributeValue, (AttributeValueQuotes) null);
    }

    private IOpenElementTag setAttribute(IOpenElementTag openElementTag, String attributeName, String attributeValue, AttributeValueQuotes attributeValueQuotes) {
        if (!(openElementTag instanceof OpenElementTag)) {
            return setAttribute((IOpenElementTag) OpenElementTag.asEngineOpenElementTag(openElementTag), attributeName, attributeValue, attributeValueQuotes);
        }
        return ((OpenElementTag) openElementTag).setAttribute(this.attributeDefinitions, (AttributeDefinition) null, attributeName, attributeValue, attributeValueQuotes);
    }

    private IOpenElementTag replaceAttribute(IOpenElementTag openElementTag, AttributeName oldAttributeName, String attributeName, String attributeValue) {
        if (!(openElementTag instanceof OpenElementTag)) {
            return replaceAttribute((IOpenElementTag) OpenElementTag.asEngineOpenElementTag(openElementTag), oldAttributeName, attributeName, attributeValue);
        }
        return ((OpenElementTag) openElementTag).replaceAttribute(this.attributeDefinitions, oldAttributeName, (AttributeDefinition) null, attributeName, attributeValue, (AttributeValueQuotes) null);
    }

    private IOpenElementTag replaceAttribute(IOpenElementTag openElementTag, AttributeName oldAttributeName, String attributeName, String attributeValue, AttributeValueQuotes attributeValueQuotes) {
        if (!(openElementTag instanceof OpenElementTag)) {
            return replaceAttribute((IOpenElementTag) OpenElementTag.asEngineOpenElementTag(openElementTag), oldAttributeName, attributeName, attributeValue, attributeValueQuotes);
        }
        return ((OpenElementTag) openElementTag).replaceAttribute(this.attributeDefinitions, oldAttributeName, (AttributeDefinition) null, attributeName, attributeValue, attributeValueQuotes);
    }

    private IOpenElementTag removeAttribute(IOpenElementTag openElementTag, String attributeName) {
        if (!(openElementTag instanceof OpenElementTag)) {
            return removeAttribute((IOpenElementTag) OpenElementTag.asEngineOpenElementTag(openElementTag), attributeName);
        }
        return ((OpenElementTag) openElementTag).removeAttribute(attributeName);
    }

    private IOpenElementTag removeAttribute(IOpenElementTag openElementTag, String prefix, String name) {
        if (!(openElementTag instanceof OpenElementTag)) {
            return removeAttribute((IOpenElementTag) OpenElementTag.asEngineOpenElementTag(openElementTag), prefix, name);
        }
        return ((OpenElementTag) openElementTag).removeAttribute(prefix, name);
    }

    private IOpenElementTag removeAttribute(IOpenElementTag openElementTag, AttributeName attributeName) {
        if (!(openElementTag instanceof OpenElementTag)) {
            return removeAttribute((IOpenElementTag) OpenElementTag.asEngineOpenElementTag(openElementTag), attributeName);
        }
        return ((OpenElementTag) openElementTag).removeAttribute(attributeName);
    }

    @Override // org.thymeleaf.model.IModelFactory
    public ICloseElementTag createCloseElementTag(String elementName) {
        return createCloseElementTag(elementName, false, false);
    }

    @Override // org.thymeleaf.model.IModelFactory
    public ICloseElementTag createCloseElementTag(String elementName, boolean synthetic, boolean unmatched) {
        ElementDefinition elementDefinition = this.elementDefinitions.forName(this.templateMode, elementName);
        return new CloseElementTag(this.templateMode, elementDefinition, elementName, null, synthetic, unmatched);
    }

    private Attribute buildAttribute(String name, String value, AttributeValueQuotes quotes) {
        AttributeDefinition attributeDefinition = this.attributeDefinitions.forName(this.templateMode, name);
        return new Attribute(attributeDefinition, name, "=", value, quotes, null, -1, -1);
    }

    private Attribute[] buildAttributeArray(Map<String, String> attributes, AttributeValueQuotes quotes) {
        if (attributes == null || attributes.size() == 0) {
            return Attributes.EMPTY_ATTRIBUTE_ARRAY;
        }
        int i = 0;
        Attribute[] newAttributes = new Attribute[attributes.size()];
        for (Map.Entry<String, String> attributesEntry : attributes.entrySet()) {
            int i2 = i;
            i++;
            newAttributes[i2] = buildAttribute(attributesEntry.getKey(), attributesEntry.getValue(), quotes);
        }
        return newAttributes;
    }

    private Attributes buildAttributes(Attribute[] attributeArray) {
        String[] innerWhiteSpaces;
        if (attributeArray == null || attributeArray.length == 0) {
            return Attributes.EMPTY_ATTRIBUTES;
        }
        if (attributeArray.length < SYNTHETIC_INNER_WHITESPACES.length) {
            innerWhiteSpaces = SYNTHETIC_INNER_WHITESPACES[attributeArray.length];
        } else {
            innerWhiteSpaces = new String[attributeArray.length];
            Arrays.fill(innerWhiteSpaces, " ");
        }
        return new Attributes(attributeArray, innerWhiteSpaces);
    }
}