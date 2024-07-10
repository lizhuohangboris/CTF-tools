package org.thymeleaf.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.inline.NoOpInliner;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/EngineContext.class */
public class EngineContext extends AbstractEngineContext implements IEngineContext {
    private static final int DEFAULT_ELEMENT_HIERARCHY_SIZE = 20;
    private static final int DEFAULT_LEVELS_SIZE = 10;
    private static final int DEFAULT_MAP_SIZE = 5;
    private int level;
    private int index;
    private int[] levels;
    private HashMap<String, Object>[] maps;
    private SelectionTarget[] selectionTargets;
    private IInliner[] inliners;
    private TemplateData[] templateDatas;
    private IProcessableElementTag[] elementTags;
    private SelectionTarget lastSelectionTarget;
    private IInliner lastInliner;
    private TemplateData lastTemplateData;
    private final List<TemplateData> templateStack;
    private static final Object NON_EXISTING = new Object() { // from class: org.thymeleaf.context.EngineContext.1
        public String toString() {
            return "(*removed*)";
        }
    };
    private static final Object NULL = new Object() { // from class: org.thymeleaf.context.EngineContext.2
        public String toString() {
            return BeanDefinitionParserDelegate.NULL_ELEMENT;
        }
    };

    public EngineContext(IEngineConfiguration configuration, TemplateData templateData, Map<String, Object> templateResolutionAttributes, Locale locale, Map<String, Object> variables) {
        super(configuration, templateResolutionAttributes, locale);
        this.level = 0;
        this.index = 0;
        this.lastSelectionTarget = null;
        this.lastInliner = null;
        this.lastTemplateData = null;
        this.levels = new int[10];
        this.maps = new HashMap[10];
        this.selectionTargets = new SelectionTarget[10];
        this.inliners = new IInliner[10];
        this.templateDatas = new TemplateData[10];
        this.elementTags = new IProcessableElementTag[20];
        Arrays.fill(this.levels, Integer.MAX_VALUE);
        Arrays.fill(this.maps, (Object) null);
        Arrays.fill(this.selectionTargets, (Object) null);
        Arrays.fill(this.inliners, (Object) null);
        Arrays.fill(this.templateDatas, (Object) null);
        Arrays.fill(this.elementTags, (Object) null);
        this.levels[0] = 0;
        this.templateDatas[0] = templateData;
        this.lastTemplateData = templateData;
        this.templateStack = new ArrayList(10);
        this.templateStack.add(templateData);
        if (variables != null) {
            setVariables(variables);
        }
    }

    @Override // org.thymeleaf.context.IContext
    public boolean containsVariable(String name) {
        Object value;
        int n = this.index + 1;
        while (true) {
            int i = n;
            n--;
            if (i != 0) {
                HashMap map = this.maps[n];
                if (map != null && map.size() > 0 && (value = map.get(name)) != null) {
                    return value != NON_EXISTING;
                }
            } else {
                return false;
            }
        }
    }

    @Override // org.thymeleaf.context.IContext
    public Object getVariable(String key) {
        Object value;
        int n = this.index + 1;
        while (true) {
            int i = n;
            n--;
            if (i != 0) {
                HashMap map = this.maps[n];
                if (map != null && map.size() > 0 && (value = map.get(key)) != null) {
                    if (value == NON_EXISTING || value == NULL) {
                        return null;
                    }
                    return resolveLazy(value);
                }
            } else {
                return null;
            }
        }
    }

    @Override // org.thymeleaf.context.IContext
    public Set<String> getVariableNames() {
        Set<String> variableNames = new HashSet<>();
        int n = this.index + 1;
        int i = 0;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                if (this.maps[i] != null) {
                    for (Map.Entry<String, Object> mapEntry : this.maps[i].entrySet()) {
                        if (mapEntry.getValue() == NON_EXISTING) {
                            variableNames.remove(mapEntry.getKey());
                        } else {
                            variableNames.add(mapEntry.getKey());
                        }
                    }
                }
                i++;
            } else {
                return variableNames;
            }
        }
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void setVariable(String name, Object value) {
        ensureLevelInitialized(5);
        if (value == NON_EXISTING && this.level == 0) {
            this.maps[this.index].remove(name);
        } else if (value == null) {
            this.maps[this.index].put(name, NULL);
        } else {
            this.maps[this.index].put(name, value);
        }
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void setVariables(Map<String, Object> variables) {
        if (variables == null || variables.isEmpty()) {
            return;
        }
        ensureLevelInitialized(Math.max(5, variables.size() + 2));
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            Object value = entry.getValue();
            if (value == null) {
                this.maps[this.index].put(entry.getKey(), NULL);
            } else {
                this.maps[this.index].put(entry.getKey(), value);
            }
        }
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void removeVariable(String name) {
        if (containsVariable(name)) {
            setVariable(name, NON_EXISTING);
        }
    }

    @Override // org.thymeleaf.context.IEngineContext
    public boolean isVariableLocal(String name) {
        int n = this.index + 1;
        while (true) {
            int i = n;
            n--;
            if (i > 1) {
                if (this.maps[n] != null && this.maps[n].containsKey(name)) {
                    Object result = this.maps[n].get(name);
                    if (result == NON_EXISTING) {
                        return false;
                    }
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    @Override // org.thymeleaf.context.ITemplateContext
    public boolean hasSelectionTarget() {
        if (this.lastSelectionTarget != null) {
            return true;
        }
        int n = this.index + 1;
        do {
            int i = n;
            n--;
            if (i == 0) {
                return false;
            }
        } while (this.selectionTargets[n] == null);
        return true;
    }

    @Override // org.thymeleaf.context.ITemplateContext
    public Object getSelectionTarget() {
        if (this.lastSelectionTarget != null) {
            return this.lastSelectionTarget.selectionTarget;
        }
        int n = this.index + 1;
        do {
            int i = n;
            n--;
            if (i == 0) {
                return null;
            }
        } while (this.selectionTargets[n] == null);
        this.lastSelectionTarget = this.selectionTargets[n];
        return this.lastSelectionTarget.selectionTarget;
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void setSelectionTarget(Object selectionTarget) {
        ensureLevelInitialized(-1);
        this.lastSelectionTarget = new SelectionTarget(selectionTarget);
        this.selectionTargets[this.index] = this.lastSelectionTarget;
    }

    @Override // org.thymeleaf.context.ITemplateContext
    public IInliner getInliner() {
        if (this.lastInliner != null) {
            if (this.lastInliner == NoOpInliner.INSTANCE) {
                return null;
            }
            return this.lastInliner;
        }
        int n = this.index + 1;
        do {
            int i = n;
            n--;
            if (i == 0) {
                return null;
            }
        } while (this.inliners[n] == null);
        this.lastInliner = this.inliners[n];
        if (this.lastInliner == NoOpInliner.INSTANCE) {
            return null;
        }
        return this.lastInliner;
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void setInliner(IInliner inliner) {
        ensureLevelInitialized(-1);
        this.lastInliner = inliner == null ? NoOpInliner.INSTANCE : inliner;
        this.inliners[this.index] = this.lastInliner;
    }

    @Override // org.thymeleaf.context.ITemplateContext
    public TemplateData getTemplateData() {
        if (this.lastTemplateData != null) {
            return this.lastTemplateData;
        }
        int n = this.index + 1;
        do {
            int i = n;
            n--;
            if (i == 0) {
                return null;
            }
        } while (this.templateDatas[n] == null);
        this.lastTemplateData = this.templateDatas[n];
        return this.lastTemplateData;
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void setTemplateData(TemplateData templateData) {
        Validate.notNull(templateData, "Template Data cannot be null");
        ensureLevelInitialized(-1);
        this.lastTemplateData = templateData;
        this.templateDatas[this.index] = this.lastTemplateData;
        this.templateStack.clear();
    }

    @Override // org.thymeleaf.context.ITemplateContext
    public List<TemplateData> getTemplateStack() {
        if (!this.templateStack.isEmpty()) {
            return Collections.unmodifiableList(new ArrayList(this.templateStack));
        }
        for (int i = 0; i <= this.index; i++) {
            if (this.templateDatas[i] != null) {
                this.templateStack.add(this.templateDatas[i]);
            }
        }
        return Collections.unmodifiableList(new ArrayList(this.templateStack));
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void setElementTag(IProcessableElementTag elementTag) {
        if (this.elementTags.length <= this.level) {
            this.elementTags = (IProcessableElementTag[]) Arrays.copyOf(this.elementTags, Math.max(this.level, this.elementTags.length + 20));
        }
        this.elementTags[this.level] = elementTag;
    }

    @Override // org.thymeleaf.context.ITemplateContext
    public List<IProcessableElementTag> getElementStack() {
        List<IProcessableElementTag> elementStack = new ArrayList<>(this.level);
        for (int i = 0; i <= this.level && i < this.elementTags.length; i++) {
            if (this.elementTags[i] != null) {
                elementStack.add(this.elementTags[i]);
            }
        }
        return Collections.unmodifiableList(elementStack);
    }

    @Override // org.thymeleaf.context.IEngineContext
    public List<IProcessableElementTag> getElementStackAbove(int contextLevel) {
        List<IProcessableElementTag> elementStack = new ArrayList<>(this.level);
        for (int i = contextLevel + 1; i <= this.level && i < this.elementTags.length; i++) {
            if (this.elementTags[i] != null) {
                elementStack.add(this.elementTags[i]);
            }
        }
        return Collections.unmodifiableList(elementStack);
    }

    private void ensureLevelInitialized(int requiredSize) {
        if (this.levels[this.index] != this.level) {
            this.index++;
            if (this.levels.length == this.index) {
                this.levels = Arrays.copyOf(this.levels, this.levels.length + 10);
                Arrays.fill(this.levels, this.index, this.levels.length, Integer.MAX_VALUE);
                this.maps = (HashMap[]) Arrays.copyOf(this.maps, this.maps.length + 10);
                this.selectionTargets = (SelectionTarget[]) Arrays.copyOf(this.selectionTargets, this.selectionTargets.length + 10);
                this.inliners = (IInliner[]) Arrays.copyOf(this.inliners, this.inliners.length + 10);
                this.templateDatas = (TemplateData[]) Arrays.copyOf(this.templateDatas, this.templateDatas.length + 10);
            }
            this.levels[this.index] = this.level;
        }
        if (requiredSize >= 0 && this.maps[this.index] == null) {
            this.maps[this.index] = new HashMap<>(requiredSize, 1.0f);
        }
    }

    @Override // org.thymeleaf.context.IEngineContext
    public int level() {
        return this.level;
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void increaseLevel() {
        this.level++;
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void decreaseLevel() {
        Validate.isTrue(this.level > 0, "Cannot decrease variable map level below 0");
        if (this.levels[this.index] == this.level) {
            this.levels[this.index] = Integer.MAX_VALUE;
            if (this.maps[this.index] != null) {
                this.maps[this.index].clear();
            }
            this.selectionTargets[this.index] = null;
            this.inliners[this.index] = null;
            this.templateDatas[this.index] = null;
            this.index--;
            this.lastSelectionTarget = null;
            this.lastInliner = null;
            this.lastTemplateData = null;
            this.templateStack.clear();
        }
        if (this.level < this.elementTags.length) {
            this.elementTags[this.level] = null;
        }
        this.level--;
    }

    public String getStringRepresentationByLevel() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append('{');
        int n = this.index + 1;
        while (true) {
            int i = n;
            n--;
            if (i != 0) {
                Map<String, Object> levelVars = new LinkedHashMap<>();
                if (this.maps[n] != null) {
                    List<String> entryNames = new ArrayList<>(this.maps[n].keySet());
                    Collections.sort(entryNames);
                    for (String name : entryNames) {
                        Object value = this.maps[n].get(name);
                        if (value == NON_EXISTING) {
                            int n2 = n;
                            while (true) {
                                int i2 = n2;
                                n2--;
                                if (i2 == 0) {
                                    break;
                                } else if (this.maps[n2] != null && this.maps[n2].containsKey(name)) {
                                    if (this.maps[n2].get(name) != NON_EXISTING) {
                                        levelVars.put(name, value);
                                    }
                                }
                            }
                        } else {
                            levelVars.put(name, value);
                        }
                    }
                }
                if (n == 0 || !levelVars.isEmpty() || this.selectionTargets[n] != null || this.inliners[n] != null || this.templateDatas[n] != null) {
                    if (strBuilder.length() > 1) {
                        strBuilder.append(',');
                    }
                    strBuilder.append(this.levels[n]).append(":");
                    if (!levelVars.isEmpty() || n == 0) {
                        strBuilder.append(levelVars);
                    }
                    if (this.selectionTargets[n] != null) {
                        strBuilder.append("<").append(this.selectionTargets[n].selectionTarget).append(">");
                    }
                    if (this.inliners[n] != null) {
                        strBuilder.append(PropertyAccessor.PROPERTY_KEY_PREFIX).append(this.inliners[n].getName()).append("]");
                    }
                    if (this.templateDatas[n] != null) {
                        strBuilder.append("(").append(this.templateDatas[n].getTemplate()).append(")");
                    }
                }
            } else {
                strBuilder.append("}[");
                strBuilder.append(this.level);
                strBuilder.append(']');
                return strBuilder.toString();
            }
        }
    }

    public String toString() {
        Map<String, Object> equivalentMap = new LinkedHashMap<>();
        int n = this.index + 1;
        int i = 0;
        while (true) {
            int i2 = n;
            n--;
            if (i2 == 0) {
                break;
            }
            if (this.maps[i] != null) {
                List<String> entryNames = new ArrayList<>(this.maps[i].keySet());
                Collections.sort(entryNames);
                for (String name : entryNames) {
                    Object value = this.maps[i].get(name);
                    if (value == NON_EXISTING) {
                        equivalentMap.remove(name);
                    } else {
                        equivalentMap.put(name, value);
                    }
                }
            }
            i++;
        }
        String textInliningStr = getInliner() != null ? PropertyAccessor.PROPERTY_KEY_PREFIX + getInliner().getName() + "]" : "";
        String templateDataStr = "(" + getTemplateData().getTemplate() + ")";
        return equivalentMap.toString() + (hasSelectionTarget() ? "<" + getSelectionTarget() + ">" : "") + textInliningStr + templateDataStr;
    }

    private static Object resolveLazy(Object variable) {
        if (variable != null && (variable instanceof ILazyContextVariable)) {
            return ((ILazyContextVariable) variable).getValue();
        }
        return variable;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/EngineContext$SelectionTarget.class */
    public static final class SelectionTarget {
        final Object selectionTarget;

        SelectionTarget(Object selectionTarget) {
            this.selectionTarget = selectionTarget;
        }
    }
}