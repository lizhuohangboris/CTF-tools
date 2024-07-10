package org.thymeleaf.engine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IModel;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/ElementTagStructureHandler.class */
public final class ElementTagStructureHandler implements IElementTagStructureHandler {
    boolean setBodyText;
    CharSequence setBodyTextValue;
    boolean setBodyTextProcessable;
    boolean setBodyModel;
    IModel setBodyModelValue;
    boolean setBodyModelProcessable;
    boolean insertBeforeModel;
    IModel insertBeforeModelValue;
    boolean insertImmediatelyAfterModel;
    IModel insertImmediatelyAfterModelValue;
    boolean insertImmediatelyAfterModelProcessable;
    boolean replaceWithText;
    CharSequence replaceWithTextValue;
    boolean replaceWithTextProcessable;
    boolean replaceWithModel;
    IModel replaceWithModelValue;
    boolean replaceWithModelProcessable;
    boolean removeElement;
    boolean removeTags;
    boolean removeBody;
    boolean removeAllButFirstChild;
    boolean setLocalVariable;
    Map<String, Object> addedLocalVariables;
    boolean removeLocalVariable;
    Set<String> removedLocalVariableNames;
    boolean setAttribute;
    Object[][] setAttributeValues;
    int setAttributeValuesSize;
    boolean replaceAttribute;
    Object[][] replaceAttributeValues;
    int replaceAttributeValuesSize;
    boolean removeAttribute;
    Object[][] removeAttributeValues;
    int removeAttributeValuesSize;
    boolean setSelectionTarget;
    Object selectionTargetObject;
    boolean setInliner;
    IInliner setInlinerValue;
    boolean setTemplateData;
    TemplateData setTemplateDataValue;
    boolean iterateElement;
    String iterVariableName;
    String iterStatusVariableName;
    Object iteratedObject;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ElementTagStructureHandler() {
        reset();
    }

    @Override // org.thymeleaf.processor.element.IElementTagStructureHandler
    public void setBody(CharSequence text, boolean processable) {
        resetAllButVariablesOrAttributes();
        Validate.notNull(text, "Text cannot be null");
        this.setBodyText = true;
        this.setBodyTextValue = text;
        this.setBodyTextProcessable = processable;
    }

    @Override // org.thymeleaf.processor.element.IElementTagStructureHandler
    public void setBody(IModel model, boolean processable) {
        resetAllButVariablesOrAttributes();
        Validate.notNull(model, "Model cannot be null");
        this.setBodyModel = true;
        this.setBodyModelValue = model;
        this.setBodyModelProcessable = processable;
    }

    @Override // org.thymeleaf.processor.element.IElementTagStructureHandler
    public void insertBefore(IModel model) {
        resetAllButVariablesOrAttributes();
        Validate.notNull(model, "Model cannot be null");
        this.insertBeforeModel = true;
        this.insertBeforeModelValue = model;
    }

    @Override // org.thymeleaf.processor.element.IElementTagStructureHandler
    public void insertImmediatelyAfter(IModel model, boolean processable) {
        resetAllButVariablesOrAttributes();
        Validate.notNull(model, "Model cannot be null");
        this.insertImmediatelyAfterModel = true;
        this.insertImmediatelyAfterModelValue = model;
        this.insertImmediatelyAfterModelProcessable = processable;
    }

    @Override // org.thymeleaf.processor.element.IElementTagStructureHandler
    public void replaceWith(CharSequence text, boolean processable) {
        resetAllButVariablesOrAttributes();
        Validate.notNull(text, "Text cannot be null");
        this.replaceWithText = true;
        this.replaceWithTextValue = text;
        this.replaceWithTextProcessable = processable;
    }

    @Override // org.thymeleaf.processor.element.IElementTagStructureHandler
    public void replaceWith(IModel model, boolean processable) {
        resetAllButVariablesOrAttributes();
        Validate.notNull(model, "Model cannot be null");
        this.replaceWithModel = true;
        this.replaceWithModelValue = model;
        this.replaceWithModelProcessable = processable;
    }

    @Override // org.thymeleaf.processor.element.IElementTagStructureHandler
    public void removeElement() {
        resetAllButVariablesOrAttributes();
        this.removeElement = true;
    }

    @Override // org.thymeleaf.processor.element.IElementTagStructureHandler
    public void removeTags() {
        resetAllButVariablesOrAttributes();
        this.removeTags = true;
    }

    @Override // org.thymeleaf.processor.element.IElementTagStructureHandler
    public void removeBody() {
        resetAllButVariablesOrAttributes();
        this.removeBody = true;
    }

    @Override // org.thymeleaf.processor.element.IElementTagStructureHandler
    public void removeAllButFirstChild() {
        resetAllButVariablesOrAttributes();
        this.removeAllButFirstChild = true;
    }

    @Override // org.thymeleaf.processor.element.IElementTagStructureHandler
    public void removeLocalVariable(String name) {
        Validate.notNull(name, "Variable name cannot be null");
        this.removeLocalVariable = true;
        if (this.removedLocalVariableNames == null) {
            this.removedLocalVariableNames = new HashSet(3);
        }
        this.removedLocalVariableNames.add(name);
    }

    @Override // org.thymeleaf.processor.element.IElementTagStructureHandler
    public void setLocalVariable(String name, Object value) {
        Validate.notNull(name, "Variable name cannot be null");
        this.setLocalVariable = true;
        if (this.addedLocalVariables == null) {
            this.addedLocalVariables = new HashMap(3);
        }
        this.addedLocalVariables.put(name, value);
    }

    @Override // org.thymeleaf.processor.element.IElementTagStructureHandler
    public void setAttribute(String attributeName, String attributeValue) {
        Validate.notNull(attributeName, "Attribute name cannot be null");
        ensureSetAttributeSize();
        this.setAttribute = true;
        Object[] values = this.setAttributeValues[this.setAttributeValuesSize];
        values[0] = null;
        values[1] = attributeName;
        values[2] = attributeValue;
        values[3] = null;
        this.setAttributeValuesSize++;
    }

    @Override // org.thymeleaf.processor.element.IElementTagStructureHandler
    public void setAttribute(String attributeName, String attributeValue, AttributeValueQuotes attributeValueQuotes) {
        Validate.notNull(attributeName, "Attribute name cannot be null");
        ensureSetAttributeSize();
        this.setAttribute = true;
        Object[] values = this.setAttributeValues[this.setAttributeValuesSize];
        values[0] = null;
        values[1] = attributeName;
        values[2] = attributeValue;
        values[3] = attributeValueQuotes;
        this.setAttributeValuesSize++;
    }

    public void setAttribute(AttributeDefinition attributeDefinition, String attributeName, String attributeValue, AttributeValueQuotes attributeValueQuotes) {
        Validate.notNull(attributeDefinition, "Attribute definition cannot be null");
        Validate.notNull(attributeName, "Attribute name cannot be null");
        ensureSetAttributeSize();
        this.setAttribute = true;
        Object[] values = this.setAttributeValues[this.setAttributeValuesSize];
        values[0] = attributeDefinition;
        values[1] = attributeName;
        values[2] = attributeValue;
        values[3] = attributeValueQuotes;
        this.setAttributeValuesSize++;
    }

    /* JADX WARN: Type inference failed for: r1v11, types: [java.lang.Object[], java.lang.Object[][]] */
    private void ensureSetAttributeSize() {
        if (this.setAttributeValues == null) {
            this.setAttributeValues = new Object[3];
        }
        if (this.setAttributeValues.length == this.setAttributeValuesSize) {
            this.setAttributeValues = (Object[][]) Arrays.copyOf(this.setAttributeValues, this.setAttributeValues.length + 3);
        }
        if (this.setAttributeValues[this.setAttributeValuesSize] == null) {
            this.setAttributeValues[this.setAttributeValuesSize] = new Object[4];
        }
    }

    @Override // org.thymeleaf.processor.element.IElementTagStructureHandler
    public void replaceAttribute(AttributeName oldAttributeName, String attributeName, String attributeValue) {
        Validate.notNull(oldAttributeName, "Old attribute name cannot be null");
        Validate.notNull(attributeName, "Attribute name cannot be null");
        ensureReplaceAttributeSize();
        this.replaceAttribute = true;
        Object[] values = this.replaceAttributeValues[this.replaceAttributeValuesSize];
        values[0] = oldAttributeName;
        values[1] = null;
        values[2] = attributeName;
        values[3] = attributeValue;
        values[4] = null;
        this.replaceAttributeValuesSize++;
    }

    @Override // org.thymeleaf.processor.element.IElementTagStructureHandler
    public void replaceAttribute(AttributeName oldAttributeName, String attributeName, String attributeValue, AttributeValueQuotes attributeValueQuotes) {
        Validate.notNull(oldAttributeName, "Old attribute name cannot be null");
        Validate.notNull(attributeName, "Attribute name cannot be null");
        ensureReplaceAttributeSize();
        this.replaceAttribute = true;
        Object[] values = this.replaceAttributeValues[this.replaceAttributeValuesSize];
        values[0] = oldAttributeName;
        values[1] = null;
        values[2] = attributeName;
        values[3] = attributeValue;
        values[4] = attributeValueQuotes;
        this.replaceAttributeValuesSize++;
    }

    public void replaceAttribute(AttributeName oldAttributeName, AttributeDefinition attributeDefinition, String attributeName, String attributeValue, AttributeValueQuotes attributeValueQuotes) {
        Validate.notNull(oldAttributeName, "Old attribute name cannot be null");
        Validate.notNull(attributeDefinition, "Attribute definition cannot be null");
        Validate.notNull(attributeName, "Attribute name cannot be null");
        ensureReplaceAttributeSize();
        this.replaceAttribute = true;
        Object[] values = this.replaceAttributeValues[this.replaceAttributeValuesSize];
        values[0] = oldAttributeName;
        values[1] = attributeDefinition;
        values[2] = attributeName;
        values[3] = attributeValue;
        values[4] = attributeValueQuotes;
        this.replaceAttributeValuesSize++;
    }

    /* JADX WARN: Type inference failed for: r1v11, types: [java.lang.Object[], java.lang.Object[][]] */
    private void ensureReplaceAttributeSize() {
        if (this.replaceAttributeValues == null) {
            this.replaceAttributeValues = new Object[3];
        }
        if (this.replaceAttributeValues.length == this.replaceAttributeValuesSize) {
            this.replaceAttributeValues = (Object[][]) Arrays.copyOf(this.replaceAttributeValues, this.replaceAttributeValues.length + 3);
        }
        if (this.replaceAttributeValues[this.replaceAttributeValuesSize] == null) {
            this.replaceAttributeValues[this.replaceAttributeValuesSize] = new Object[5];
        }
    }

    @Override // org.thymeleaf.processor.element.IElementTagStructureHandler
    public void removeAttribute(String attributeName) {
        Validate.notNull(attributeName, "Attribute name cannot be null");
        ensureRemoveAttributeSize();
        this.removeAttribute = true;
        Object[] values = this.removeAttributeValues[this.removeAttributeValuesSize];
        values[0] = attributeName;
        values[1] = null;
        this.removeAttributeValuesSize++;
    }

    @Override // org.thymeleaf.processor.element.IElementTagStructureHandler
    public void removeAttribute(String prefix, String name) {
        Validate.notNull(name, "Attribute name cannot be null");
        ensureRemoveAttributeSize();
        this.removeAttribute = true;
        Object[] values = this.removeAttributeValues[this.removeAttributeValuesSize];
        values[0] = prefix;
        values[1] = name;
        this.removeAttributeValuesSize++;
    }

    @Override // org.thymeleaf.processor.element.IElementTagStructureHandler
    public void removeAttribute(AttributeName attributeName) {
        Validate.notNull(attributeName, "Attribute name cannot be null");
        ensureRemoveAttributeSize();
        this.removeAttribute = true;
        Object[] values = this.removeAttributeValues[this.removeAttributeValuesSize];
        values[0] = attributeName;
        values[1] = null;
        this.removeAttributeValuesSize++;
    }

    /* JADX WARN: Type inference failed for: r1v11, types: [java.lang.Object[], java.lang.Object[][]] */
    private void ensureRemoveAttributeSize() {
        if (this.removeAttributeValues == null) {
            this.removeAttributeValues = new Object[3];
        }
        if (this.removeAttributeValues.length == this.removeAttributeValuesSize) {
            this.removeAttributeValues = (Object[][]) Arrays.copyOf(this.removeAttributeValues, this.removeAttributeValues.length + 3);
        }
        if (this.removeAttributeValues[this.removeAttributeValuesSize] == null) {
            this.removeAttributeValues[this.removeAttributeValuesSize] = new Object[2];
        }
    }

    @Override // org.thymeleaf.processor.element.IElementTagStructureHandler
    public void setSelectionTarget(Object selectionTarget) {
        this.setSelectionTarget = true;
        this.selectionTargetObject = selectionTarget;
    }

    @Override // org.thymeleaf.processor.element.IElementTagStructureHandler
    public void setInliner(IInliner inliner) {
        this.setInliner = true;
        this.setInlinerValue = inliner;
    }

    @Override // org.thymeleaf.processor.element.IElementTagStructureHandler
    public void setTemplateData(TemplateData templateData) {
        this.setTemplateData = true;
        this.setTemplateDataValue = templateData;
    }

    @Override // org.thymeleaf.processor.element.IElementTagStructureHandler
    public void iterateElement(String iterVariableName, String iterStatusVariableName, Object iteratedObject) {
        Validate.notEmpty(iterVariableName, "Iteration variable name cannot be null");
        resetAllButVariablesOrAttributes();
        this.iterateElement = true;
        this.iterVariableName = iterVariableName;
        this.iterStatusVariableName = iterStatusVariableName;
        this.iteratedObject = iteratedObject;
    }

    @Override // org.thymeleaf.processor.element.IElementTagStructureHandler
    public void reset() {
        resetAllButVariablesOrAttributes();
        this.setLocalVariable = false;
        if (this.addedLocalVariables != null && this.addedLocalVariables.size() > 0) {
            this.addedLocalVariables.clear();
        }
        this.removeLocalVariable = false;
        if (this.removedLocalVariableNames != null && this.removedLocalVariableNames.size() > 0) {
            this.removedLocalVariableNames.clear();
        }
        this.setSelectionTarget = false;
        this.selectionTargetObject = null;
        this.setInliner = false;
        this.setInlinerValue = null;
        this.setTemplateData = false;
        this.setTemplateDataValue = null;
        this.setAttribute = false;
        this.setAttributeValuesSize = 0;
        this.replaceAttribute = false;
        this.replaceAttributeValuesSize = 0;
        this.removeAttribute = false;
        this.removeAttributeValuesSize = 0;
    }

    private void resetAllButVariablesOrAttributes() {
        this.setBodyText = false;
        this.setBodyTextValue = null;
        this.setBodyTextProcessable = false;
        this.setBodyModel = false;
        this.setBodyModelValue = null;
        this.setBodyModelProcessable = false;
        this.insertBeforeModel = false;
        this.insertBeforeModelValue = null;
        this.insertImmediatelyAfterModel = false;
        this.insertImmediatelyAfterModelValue = null;
        this.insertImmediatelyAfterModelProcessable = false;
        this.replaceWithText = false;
        this.replaceWithTextValue = null;
        this.replaceWithTextProcessable = false;
        this.replaceWithModel = false;
        this.replaceWithModelValue = null;
        this.replaceWithModelProcessable = false;
        this.removeElement = false;
        this.removeTags = false;
        this.removeBody = false;
        this.removeAllButFirstChild = false;
        this.iterateElement = false;
        this.iterVariableName = null;
        this.iterStatusVariableName = null;
        this.iteratedObject = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void applyContextModifications(IEngineContext engineContext) {
        if (engineContext == null) {
            return;
        }
        if (this.setLocalVariable) {
            engineContext.setVariables(this.addedLocalVariables);
        }
        if (this.removeLocalVariable) {
            for (String variableName : this.removedLocalVariableNames) {
                engineContext.removeVariable(variableName);
            }
        }
        if (this.setSelectionTarget) {
            engineContext.setSelectionTarget(this.selectionTargetObject);
        }
        if (this.setInliner) {
            engineContext.setInliner(this.setInlinerValue);
        }
        if (this.setTemplateData) {
            engineContext.setTemplateData(this.setTemplateDataValue);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public <T extends AbstractProcessableElementTag> T applyAttributes(AttributeDefinitions attributeDefinitions, T tag) {
        AbstractProcessableElementTag removeAttribute;
        T ttag = tag;
        if (this.removeAttribute) {
            for (int i = 0; i < this.removeAttributeValuesSize; i++) {
                Object[] values = this.removeAttributeValues[i];
                if (values[1] != null) {
                    removeAttribute = ttag.removeAttribute((String) values[0], (String) values[1]);
                } else if (values[0] instanceof AttributeName) {
                    removeAttribute = ttag.removeAttribute((AttributeName) values[0]);
                } else {
                    removeAttribute = ttag.removeAttribute((String) values[0]);
                }
                ttag = removeAttribute;
            }
        }
        if (this.replaceAttribute) {
            for (int i2 = 0; i2 < this.replaceAttributeValuesSize; i2++) {
                Object[] values2 = this.replaceAttributeValues[i2];
                ttag = ttag.replaceAttribute(attributeDefinitions, (AttributeName) values2[0], (AttributeDefinition) values2[1], (String) values2[2], (String) values2[3], (AttributeValueQuotes) values2[4]);
            }
        }
        if (this.setAttribute) {
            for (int i3 = 0; i3 < this.setAttributeValuesSize; i3++) {
                Object[] values3 = this.setAttributeValues[i3];
                ttag = ttag.setAttribute(attributeDefinitions, (AttributeDefinition) values3[0], (String) values3[1], (String) values3[2], (AttributeValueQuotes) values3[3]);
            }
        }
        return ttag;
    }
}