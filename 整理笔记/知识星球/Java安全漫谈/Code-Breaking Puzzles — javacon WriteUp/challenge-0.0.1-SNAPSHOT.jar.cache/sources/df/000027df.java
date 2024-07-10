package org.thymeleaf.engine;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.FastStringWriter;
import org.thymeleaf.util.Validate;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/Attributes.class */
public final class Attributes {
    static final String DEFAULT_WHITE_SPACE = " ";
    static final String[] DEFAULT_WHITE_SPACE_ARRAY = {DEFAULT_WHITE_SPACE};
    static final Attributes EMPTY_ATTRIBUTES = new Attributes(null, null);
    static final Attribute[] EMPTY_ATTRIBUTE_ARRAY = new Attribute[0];
    final Attribute[] attributes;
    final String[] innerWhiteSpaces;
    private volatile int associatedProcessorCount = -1;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Attributes(Attribute[] attributes, String[] innerWhiteSpaces) {
        this.attributes = attributes;
        this.innerWhiteSpaces = innerWhiteSpaces;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getAssociatedProcessorCount() {
        int c = this.associatedProcessorCount;
        if (c < 0) {
            int computeAssociatedProcessorCount = computeAssociatedProcessorCount();
            c = computeAssociatedProcessorCount;
            this.associatedProcessorCount = computeAssociatedProcessorCount;
        }
        return c;
    }

    private int computeAssociatedProcessorCount() {
        if (this.attributes == null || this.attributes.length == 0) {
            return 0;
        }
        int count = 0;
        int n = this.attributes.length;
        while (true) {
            int i = n;
            n--;
            if (i != 0) {
                if (this.attributes[n].definition.hasAssociatedProcessors) {
                    count += this.attributes[n].definition.associatedProcessors.length;
                }
            } else {
                return count;
            }
        }
    }

    private int searchAttribute(TemplateMode templateMode, String completeName) {
        if (this.attributes == null || this.attributes.length == 0) {
            return -1;
        }
        int n = this.attributes.length;
        do {
            int i = n;
            n--;
            if (i == 0) {
                return searchAttribute(AttributeNames.forName(templateMode, completeName));
            }
        } while (!this.attributes[n].completeName.equals(completeName));
        return n;
    }

    private int searchAttribute(TemplateMode templateMode, String prefix, String name) {
        if (this.attributes == null || this.attributes.length == 0) {
            return -1;
        }
        if (prefix == null || prefix.length() == 0) {
            return searchAttribute(templateMode, name);
        }
        return searchAttribute(AttributeNames.forName(templateMode, prefix, name));
    }

    private int searchAttribute(AttributeName attributeName) {
        if (this.attributes == null || this.attributes.length == 0) {
            return -1;
        }
        int n = this.attributes.length;
        do {
            int i = n;
            n--;
            if (i == 0) {
                return -1;
            }
        } while (this.attributes[n].definition.attributeName != attributeName);
        return n;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasAttribute(TemplateMode templateMode, String completeName) {
        return searchAttribute(templateMode, completeName) >= 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasAttribute(TemplateMode templateMode, String prefix, String name) {
        return searchAttribute(templateMode, prefix, name) >= 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasAttribute(AttributeName attributeName) {
        return searchAttribute(attributeName) >= 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Attribute getAttribute(TemplateMode templateMode, String completeName) {
        int pos = searchAttribute(templateMode, completeName);
        if (pos < 0) {
            return null;
        }
        return this.attributes[pos];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Attribute getAttribute(TemplateMode templateMode, String prefix, String name) {
        int pos = searchAttribute(templateMode, prefix, name);
        if (pos < 0) {
            return null;
        }
        return this.attributes[pos];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Attribute getAttribute(AttributeName attributeName) {
        int pos = searchAttribute(attributeName);
        if (pos < 0) {
            return null;
        }
        return this.attributes[pos];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Attribute[] getAllAttributes() {
        if (this.attributes == null || this.attributes.length == 0) {
            return EMPTY_ATTRIBUTE_ARRAY;
        }
        return (Attribute[]) this.attributes.clone();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Map<String, String> getAttributeMap() {
        if (this.attributes == null || this.attributes.length == 0) {
            return Collections.emptyMap();
        }
        Map<String, String> attributeMap = new LinkedHashMap<>(this.attributes.length + 5);
        for (int i = 0; i < this.attributes.length; i++) {
            attributeMap.put(this.attributes[i].completeName, this.attributes[i].value);
        }
        return attributeMap;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Attributes setAttribute(AttributeDefinitions attributeDefinitions, TemplateMode templateMode, AttributeDefinition attributeDefinition, String completeName, String value, AttributeValueQuotes valueQuotes) {
        Attribute[] newAttributes;
        String[] newInnerWhiteSpaces;
        Validate.isTrue((value == null && templateMode == TemplateMode.XML) ? false : true, "Cannot set null-value attributes in XML template mode");
        Validate.isTrue((valueQuotes == AttributeValueQuotes.NONE && templateMode == TemplateMode.XML) ? false : true, "Cannot set unquoted attributes in XML template mode");
        int existingIdx = attributeDefinition != null ? searchAttribute(attributeDefinition.attributeName) : searchAttribute(templateMode, completeName);
        if (existingIdx >= 0) {
            Attribute[] newAttributes2 = (Attribute[]) this.attributes.clone();
            newAttributes2[existingIdx] = newAttributes2[existingIdx].modify(null, completeName, value, valueQuotes);
            return new Attributes(newAttributes2, this.innerWhiteSpaces);
        }
        AttributeDefinition newAttributeDefinition = attributeDefinition != null ? attributeDefinition : attributeDefinitions.forName(templateMode, completeName);
        Attribute newAttribute = new Attribute(newAttributeDefinition, completeName, null, value, valueQuotes, null, -1, -1);
        if (this.attributes != null) {
            newAttributes = new Attribute[this.attributes.length + 1];
            System.arraycopy(this.attributes, 0, newAttributes, 0, this.attributes.length);
            newAttributes[this.attributes.length] = newAttribute;
        } else {
            newAttributes = new Attribute[]{newAttribute};
        }
        if (this.innerWhiteSpaces != null) {
            newInnerWhiteSpaces = new String[this.innerWhiteSpaces.length + 1];
            System.arraycopy(this.innerWhiteSpaces, 0, newInnerWhiteSpaces, 0, this.innerWhiteSpaces.length);
            if (this.innerWhiteSpaces.length == (this.attributes != null ? this.attributes.length : 0)) {
                newInnerWhiteSpaces[this.innerWhiteSpaces.length] = DEFAULT_WHITE_SPACE;
            } else {
                newInnerWhiteSpaces[this.innerWhiteSpaces.length] = newInnerWhiteSpaces[this.innerWhiteSpaces.length - 1];
                newInnerWhiteSpaces[this.innerWhiteSpaces.length - 1] = DEFAULT_WHITE_SPACE;
            }
        } else {
            newInnerWhiteSpaces = DEFAULT_WHITE_SPACE_ARRAY;
        }
        return new Attributes(newAttributes, newInnerWhiteSpaces);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Attributes replaceAttribute(AttributeDefinitions attributeDefinitions, TemplateMode templateMode, AttributeName oldName, AttributeDefinition newAttributeDefinition, String newCompleteName, String value, AttributeValueQuotes valueQuotes) {
        Validate.isTrue((value == null && templateMode == TemplateMode.XML) ? false : true, "Cannot set null-value attributes in XML template mode");
        Validate.isTrue((valueQuotes == AttributeValueQuotes.NONE && templateMode == TemplateMode.XML) ? false : true, "Cannot set unquoted attributes in XML template mode");
        if (this.attributes == null) {
            return setAttribute(attributeDefinitions, templateMode, newAttributeDefinition, newCompleteName, value, valueQuotes);
        }
        int oldIdx = searchAttribute(oldName);
        if (oldIdx < 0) {
            return setAttribute(attributeDefinitions, templateMode, newAttributeDefinition, newCompleteName, value, valueQuotes);
        }
        int existingIdx = newAttributeDefinition != null ? searchAttribute(newAttributeDefinition.attributeName) : searchAttribute(templateMode, newCompleteName);
        if (existingIdx >= 0) {
            if (oldIdx == existingIdx) {
                return setAttribute(attributeDefinitions, templateMode, newAttributeDefinition, newCompleteName, value, valueQuotes);
            }
            Attribute[] newAttributes = new Attribute[this.attributes.length - 1];
            System.arraycopy(this.attributes, 0, newAttributes, 0, oldIdx);
            System.arraycopy(this.attributes, oldIdx + 1, newAttributes, oldIdx, newAttributes.length - oldIdx);
            int iwIdx = oldIdx + 1;
            if (oldIdx + 1 == this.attributes.length) {
                iwIdx = oldIdx;
            }
            String[] newInnerWhiteSpaces = new String[this.innerWhiteSpaces.length - 1];
            System.arraycopy(this.innerWhiteSpaces, 0, newInnerWhiteSpaces, 0, iwIdx);
            System.arraycopy(this.innerWhiteSpaces, iwIdx + 1, newInnerWhiteSpaces, iwIdx, newInnerWhiteSpaces.length - iwIdx);
            if (existingIdx > oldIdx) {
                existingIdx--;
            }
            newAttributes[existingIdx] = newAttributes[existingIdx].modify(null, newCompleteName, value, valueQuotes);
            return new Attributes(newAttributes, newInnerWhiteSpaces);
        }
        AttributeDefinition computedNewAttributeDefinition = newAttributeDefinition != null ? newAttributeDefinition : attributeDefinitions.forName(templateMode, newCompleteName);
        Attribute[] newAttributes2 = (Attribute[]) this.attributes.clone();
        newAttributes2[oldIdx] = newAttributes2[oldIdx].modify(computedNewAttributeDefinition, newCompleteName, value, valueQuotes);
        return new Attributes(newAttributes2, this.innerWhiteSpaces);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Attributes removeAttribute(TemplateMode templateMode, String prefix, String name) {
        if (this.attributes == null) {
            return this;
        }
        int attrIdx = searchAttribute(templateMode, prefix, name);
        if (attrIdx < 0) {
            return this;
        }
        return removeAttribute(attrIdx);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Attributes removeAttribute(TemplateMode templateMode, String completeName) {
        if (this.attributes == null) {
            return this;
        }
        int attrIdx = searchAttribute(templateMode, completeName);
        if (attrIdx < 0) {
            return this;
        }
        return removeAttribute(attrIdx);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Attributes removeAttribute(AttributeName attributeName) {
        if (this.attributes == null) {
            return this;
        }
        int attrIdx = searchAttribute(attributeName);
        if (attrIdx < 0) {
            return this;
        }
        return removeAttribute(attrIdx);
    }

    private Attributes removeAttribute(int attrIdx) {
        Attribute[] newAttributes;
        if (this.attributes.length == 1 && this.innerWhiteSpaces.length == 1) {
            return EMPTY_ATTRIBUTES;
        }
        if (this.attributes.length == 1) {
            newAttributes = null;
        } else {
            newAttributes = new Attribute[this.attributes.length - 1];
            System.arraycopy(this.attributes, 0, newAttributes, 0, attrIdx);
            System.arraycopy(this.attributes, attrIdx + 1, newAttributes, attrIdx, newAttributes.length - attrIdx);
        }
        int iwIdx = attrIdx + 1;
        if (attrIdx + 1 == this.attributes.length) {
            iwIdx = attrIdx;
        }
        String[] newInnerWhiteSpaces = new String[this.innerWhiteSpaces.length - 1];
        System.arraycopy(this.innerWhiteSpaces, 0, newInnerWhiteSpaces, 0, iwIdx);
        System.arraycopy(this.innerWhiteSpaces, iwIdx + 1, newInnerWhiteSpaces, iwIdx, newInnerWhiteSpaces.length - iwIdx);
        return new Attributes(newAttributes, newInnerWhiteSpaces);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void write(Writer writer) throws IOException {
        if (this.attributes == null) {
            if (this.innerWhiteSpaces != null) {
                writer.write(this.innerWhiteSpaces[0]);
                return;
            }
            return;
        }
        int i = 0;
        while (i < this.attributes.length) {
            writer.write(this.innerWhiteSpaces[i]);
            this.attributes[i].write(writer);
            i++;
        }
        if (i < this.innerWhiteSpaces.length) {
            writer.write(this.innerWhiteSpaces[i]);
        }
    }

    public String toString() {
        Writer stringWriter = new FastStringWriter();
        try {
            write(stringWriter);
            return stringWriter.toString();
        } catch (IOException e) {
            throw new TemplateProcessingException("Exception processing String form of ElementAttributes", e);
        }
    }
}