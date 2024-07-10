package org.thymeleaf.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.model.IModel;
import org.thymeleaf.processor.templateboundaries.ITemplateBoundariesStructureHandler;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/TemplateBoundariesStructureHandler.class */
public final class TemplateBoundariesStructureHandler implements ITemplateBoundariesStructureHandler {
    boolean insertText;
    String insertTextValue;
    boolean insertTextProcessable;
    boolean insertModel;
    IModel insertModelValue;
    boolean insertModelProcessable;
    boolean setLocalVariable;
    Map<String, Object> addedLocalVariables;
    boolean removeLocalVariable;
    Set<String> removedLocalVariableNames;
    boolean setSelectionTarget;
    Object selectionTargetObject;
    boolean setInliner;
    IInliner setInlinerValue;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TemplateBoundariesStructureHandler() {
        reset();
    }

    @Override // org.thymeleaf.processor.templateboundaries.ITemplateBoundariesStructureHandler
    public void insert(String text, boolean processable) {
        resetAllButLocalVariables();
        Validate.notNull(text, "Text cannot be null");
        this.insertText = true;
        this.insertTextValue = text;
        this.insertTextProcessable = processable;
    }

    @Override // org.thymeleaf.processor.templateboundaries.ITemplateBoundariesStructureHandler
    public void insert(IModel model, boolean processable) {
        resetAllButLocalVariables();
        Validate.notNull(model, "Model cannot be null");
        this.insertModel = true;
        this.insertModelValue = model;
        this.insertModelProcessable = processable;
    }

    @Override // org.thymeleaf.processor.templateboundaries.ITemplateBoundariesStructureHandler
    public void removeLocalVariable(String name) {
        this.removeLocalVariable = true;
        if (this.removedLocalVariableNames == null) {
            this.removedLocalVariableNames = new HashSet(3);
        }
        this.removedLocalVariableNames.add(name);
    }

    @Override // org.thymeleaf.processor.templateboundaries.ITemplateBoundariesStructureHandler
    public void setLocalVariable(String name, Object value) {
        this.setLocalVariable = true;
        if (this.addedLocalVariables == null) {
            this.addedLocalVariables = new HashMap(3);
        }
        this.addedLocalVariables.put(name, value);
    }

    @Override // org.thymeleaf.processor.templateboundaries.ITemplateBoundariesStructureHandler
    public void setSelectionTarget(Object selectionTarget) {
        this.setSelectionTarget = true;
        this.selectionTargetObject = selectionTarget;
    }

    @Override // org.thymeleaf.processor.templateboundaries.ITemplateBoundariesStructureHandler
    public void setInliner(IInliner inliner) {
        this.setInliner = true;
        this.setInlinerValue = inliner;
    }

    @Override // org.thymeleaf.processor.templateboundaries.ITemplateBoundariesStructureHandler
    public void reset() {
        resetAllButLocalVariables();
        this.setLocalVariable = false;
        if (this.addedLocalVariables != null) {
            this.addedLocalVariables.clear();
        }
        this.removeLocalVariable = false;
        if (this.removedLocalVariableNames != null) {
            this.removedLocalVariableNames.clear();
        }
        this.setSelectionTarget = false;
        this.selectionTargetObject = null;
        this.setInliner = false;
        this.setInlinerValue = null;
    }

    private void resetAllButLocalVariables() {
        this.insertText = false;
        this.insertTextValue = null;
        this.insertTextProcessable = false;
        this.insertModel = false;
        this.insertModelValue = null;
        this.insertModelProcessable = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void applyContextModifications(IEngineContext engineContext) {
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
    }
}