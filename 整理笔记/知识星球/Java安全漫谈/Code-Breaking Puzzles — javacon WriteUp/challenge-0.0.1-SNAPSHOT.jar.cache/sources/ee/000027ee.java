package org.thymeleaf.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.processor.element.IElementModelStructureHandler;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/ElementModelStructureHandler.class */
public final class ElementModelStructureHandler implements IElementModelStructureHandler {
    boolean setLocalVariable;
    Map<String, Object> addedLocalVariables;
    boolean removeLocalVariable;
    Set<String> removedLocalVariableNames;
    boolean setSelectionTarget;
    Object selectionTargetObject;
    boolean setInliner;
    IInliner setInlinerValue;
    boolean setTemplateData;
    TemplateData setTemplateDataValue;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ElementModelStructureHandler() {
        reset();
    }

    @Override // org.thymeleaf.processor.element.IElementModelStructureHandler
    public void removeLocalVariable(String name) {
        this.removeLocalVariable = true;
        if (this.removedLocalVariableNames == null) {
            this.removedLocalVariableNames = new HashSet(3);
        }
        this.removedLocalVariableNames.add(name);
    }

    @Override // org.thymeleaf.processor.element.IElementModelStructureHandler
    public void setLocalVariable(String name, Object value) {
        this.setLocalVariable = true;
        if (this.addedLocalVariables == null) {
            this.addedLocalVariables = new HashMap(3);
        }
        this.addedLocalVariables.put(name, value);
    }

    @Override // org.thymeleaf.processor.element.IElementModelStructureHandler
    public void setSelectionTarget(Object selectionTarget) {
        this.setSelectionTarget = true;
        this.selectionTargetObject = selectionTarget;
    }

    @Override // org.thymeleaf.processor.element.IElementModelStructureHandler
    public void setInliner(IInliner inliner) {
        this.setInliner = true;
        this.setInlinerValue = inliner;
    }

    @Override // org.thymeleaf.processor.element.IElementModelStructureHandler
    public void setTemplateData(TemplateData templateData) {
        this.setTemplateData = true;
        this.setTemplateDataValue = templateData;
    }

    @Override // org.thymeleaf.processor.element.IElementModelStructureHandler
    public void reset() {
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
}