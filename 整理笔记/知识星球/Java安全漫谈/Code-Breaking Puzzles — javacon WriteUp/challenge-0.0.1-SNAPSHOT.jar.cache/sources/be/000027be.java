package org.thymeleaf.context;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.PropertyAccessor;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.inline.NoOpInliner;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/WebEngineContext.class */
public class WebEngineContext extends AbstractEngineContext implements IEngineContext, IWebContext {
    private static final String PARAM_VARIABLE_NAME = "param";
    private static final String SESSION_VARIABLE_NAME = "session";
    private static final String APPLICATION_VARIABLE_NAME = "application";
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final HttpSession session;
    private final ServletContext servletContext;
    private final RequestAttributesVariablesMap requestAttributesVariablesMap;
    private final Map<String, Object> requestParametersVariablesMap;
    private final Map<String, Object> sessionAttributesVariablesMap;
    private final Map<String, Object> applicationAttributesVariablesMap;

    public WebEngineContext(IEngineConfiguration configuration, TemplateData templateData, Map<String, Object> templateResolutionAttributes, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, Locale locale, Map<String, Object> variables) {
        super(configuration, templateResolutionAttributes, locale);
        Validate.notNull(request, "Request cannot be null in web variables map");
        Validate.notNull(response, "Response cannot be null in web variables map");
        Validate.notNull(servletContext, "Servlet Context cannot be null in web variables map");
        this.request = request;
        this.response = response;
        this.session = request.getSession(false);
        this.servletContext = servletContext;
        this.requestAttributesVariablesMap = new RequestAttributesVariablesMap(configuration, templateData, templateResolutionAttributes, this.request, locale, variables);
        this.requestParametersVariablesMap = new RequestParametersMap(this.request);
        this.applicationAttributesVariablesMap = new ServletContextAttributesMap(this.servletContext);
        this.sessionAttributesVariablesMap = new SessionAttributesMap(this.session);
    }

    @Override // org.thymeleaf.context.IWebContext
    public HttpServletRequest getRequest() {
        return this.request;
    }

    @Override // org.thymeleaf.context.IWebContext
    public HttpServletResponse getResponse() {
        return this.response;
    }

    @Override // org.thymeleaf.context.IWebContext
    public HttpSession getSession() {
        return this.session;
    }

    @Override // org.thymeleaf.context.IWebContext
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    @Override // org.thymeleaf.context.IContext
    public boolean containsVariable(String name) {
        return "session".equals(name) ? this.sessionAttributesVariablesMap != null : PARAM_VARIABLE_NAME.equals(name) || "application".equals(name) || this.requestAttributesVariablesMap.containsVariable(name);
    }

    @Override // org.thymeleaf.context.IContext
    public Object getVariable(String key) {
        if ("session".equals(key)) {
            return this.sessionAttributesVariablesMap;
        }
        if (PARAM_VARIABLE_NAME.equals(key)) {
            return this.requestParametersVariablesMap;
        }
        if ("application".equals(key)) {
            return this.applicationAttributesVariablesMap;
        }
        return this.requestAttributesVariablesMap.getVariable(key);
    }

    @Override // org.thymeleaf.context.IContext
    public Set<String> getVariableNames() {
        return this.requestAttributesVariablesMap.getVariableNames();
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void setVariable(String name, Object value) {
        if ("session".equals(name) || PARAM_VARIABLE_NAME.equals(name) || "application".equals(name)) {
            throw new IllegalArgumentException("Cannot set variable called '" + name + "' into web variables map: such name is a reserved word");
        }
        this.requestAttributesVariablesMap.setVariable(name, value);
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0023  */
    @Override // org.thymeleaf.context.IEngineContext
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void setVariables(java.util.Map<java.lang.String, java.lang.Object> r6) {
        /*
            r5 = this;
            r0 = r6
            if (r0 == 0) goto Ld
            r0 = r6
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto Le
        Ld:
            return
        Le:
            r0 = r6
            java.util.Set r0 = r0.keySet()
            java.util.Iterator r0 = r0.iterator()
            r7 = r0
        L1a:
            r0 = r7
            boolean r0 = r0.hasNext()
            if (r0 == 0) goto L6b
            r0 = r7
            java.lang.Object r0 = r0.next()
            java.lang.String r0 = (java.lang.String) r0
            r8 = r0
            java.lang.String r0 = "session"
            r1 = r8
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L48
            java.lang.String r0 = "param"
            r1 = r8
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L48
            java.lang.String r0 = "application"
            r1 = r8
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L68
        L48:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            r1 = r0
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r3 = r2
            r3.<init>()
            java.lang.String r3 = "Cannot set variable called '"
            java.lang.StringBuilder r2 = r2.append(r3)
            r3 = r8
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r3 = "' into web variables map: such name is a reserved word"
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            throw r0
        L68:
            goto L1a
        L6b:
            r0 = r5
            org.thymeleaf.context.WebEngineContext$RequestAttributesVariablesMap r0 = r0.requestAttributesVariablesMap
            r1 = r6
            r0.setVariables(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.thymeleaf.context.WebEngineContext.setVariables(java.util.Map):void");
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void removeVariable(String name) {
        if ("session".equals(name) || PARAM_VARIABLE_NAME.equals(name) || "application".equals(name)) {
            throw new IllegalArgumentException("Cannot remove variable called '" + name + "' in web variables map: such name is a reserved word");
        }
        this.requestAttributesVariablesMap.removeVariable(name);
    }

    @Override // org.thymeleaf.context.IEngineContext
    public boolean isVariableLocal(String name) {
        return this.requestAttributesVariablesMap.isVariableLocal(name);
    }

    @Override // org.thymeleaf.context.ITemplateContext
    public boolean hasSelectionTarget() {
        return this.requestAttributesVariablesMap.hasSelectionTarget();
    }

    @Override // org.thymeleaf.context.ITemplateContext
    public Object getSelectionTarget() {
        return this.requestAttributesVariablesMap.getSelectionTarget();
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void setSelectionTarget(Object selectionTarget) {
        this.requestAttributesVariablesMap.setSelectionTarget(selectionTarget);
    }

    @Override // org.thymeleaf.context.ITemplateContext
    public IInliner getInliner() {
        return this.requestAttributesVariablesMap.getInliner();
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void setInliner(IInliner inliner) {
        this.requestAttributesVariablesMap.setInliner(inliner);
    }

    @Override // org.thymeleaf.context.ITemplateContext
    public TemplateData getTemplateData() {
        return this.requestAttributesVariablesMap.getTemplateData();
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void setTemplateData(TemplateData templateData) {
        this.requestAttributesVariablesMap.setTemplateData(templateData);
    }

    @Override // org.thymeleaf.context.ITemplateContext
    public List<TemplateData> getTemplateStack() {
        return this.requestAttributesVariablesMap.getTemplateStack();
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void setElementTag(IProcessableElementTag elementTag) {
        this.requestAttributesVariablesMap.setElementTag(elementTag);
    }

    @Override // org.thymeleaf.context.ITemplateContext
    public List<IProcessableElementTag> getElementStack() {
        return this.requestAttributesVariablesMap.getElementStack();
    }

    @Override // org.thymeleaf.context.IEngineContext
    public List<IProcessableElementTag> getElementStackAbove(int contextLevel) {
        return this.requestAttributesVariablesMap.getElementStackAbove(contextLevel);
    }

    @Override // org.thymeleaf.context.IEngineContext
    public int level() {
        return this.requestAttributesVariablesMap.level();
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void increaseLevel() {
        this.requestAttributesVariablesMap.increaseLevel();
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void decreaseLevel() {
        this.requestAttributesVariablesMap.decreaseLevel();
    }

    public String getStringRepresentationByLevel() {
        return this.requestAttributesVariablesMap.getStringRepresentationByLevel();
    }

    public String toString() {
        return this.requestAttributesVariablesMap.toString();
    }

    static Object resolveLazy(Object variable) {
        if (variable != null && (variable instanceof ILazyContextVariable)) {
            return ((ILazyContextVariable) variable).getValue();
        }
        return variable;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/WebEngineContext$SessionAttributesMap.class */
    private static final class SessionAttributesMap extends NoOpMapImpl {
        private final HttpSession session;

        SessionAttributesMap(HttpSession session) {
            this.session = session;
        }

        @Override // org.thymeleaf.context.WebEngineContext.NoOpMapImpl, java.util.Map
        public int size() {
            if (this.session == null) {
                return 0;
            }
            int size = 0;
            Enumeration<String> attributeNames = this.session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                attributeNames.nextElement();
                size++;
            }
            return size;
        }

        @Override // org.thymeleaf.context.WebEngineContext.NoOpMapImpl, java.util.Map
        public boolean isEmpty() {
            if (this.session == null) {
                return true;
            }
            Enumeration<String> attributeNames = this.session.getAttributeNames();
            return !attributeNames.hasMoreElements();
        }

        @Override // org.thymeleaf.context.WebEngineContext.NoOpMapImpl, java.util.Map
        public boolean containsKey(Object key) {
            return true;
        }

        @Override // org.thymeleaf.context.WebEngineContext.NoOpMapImpl, java.util.Map
        public boolean containsValue(Object value) {
            throw new UnsupportedOperationException("Map does not support #containsValue()");
        }

        @Override // org.thymeleaf.context.WebEngineContext.NoOpMapImpl, java.util.Map
        public Object get(Object key) {
            if (this.session == null) {
                return null;
            }
            return WebEngineContext.resolveLazy(this.session.getAttribute(key != null ? key.toString() : null));
        }

        @Override // org.thymeleaf.context.WebEngineContext.NoOpMapImpl, java.util.Map
        public Set<String> keySet() {
            if (this.session == null) {
                return Collections.emptySet();
            }
            Set<String> keySet = new LinkedHashSet<>(5);
            Enumeration<String> attributeNames = this.session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                keySet.add(attributeNames.nextElement());
            }
            return keySet;
        }

        @Override // org.thymeleaf.context.WebEngineContext.NoOpMapImpl, java.util.Map
        public Collection<Object> values() {
            if (this.session == null) {
                return Collections.emptySet();
            }
            List<Object> values = new ArrayList<>(5);
            Enumeration<String> attributeNames = this.session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                values.add(this.session.getAttribute(attributeNames.nextElement()));
            }
            return values;
        }

        @Override // org.thymeleaf.context.WebEngineContext.NoOpMapImpl, java.util.Map
        public Set<Map.Entry<String, Object>> entrySet() {
            if (this.session == null) {
                return Collections.emptySet();
            }
            Set<Map.Entry<String, Object>> entrySet = new LinkedHashSet<>(5);
            Enumeration<String> attributeNames = this.session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String key = attributeNames.nextElement();
                Object value = this.session.getAttribute(key);
                entrySet.add(new NoOpMapImpl.MapEntry(key, value));
            }
            return entrySet;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/WebEngineContext$ServletContextAttributesMap.class */
    private static final class ServletContextAttributesMap extends NoOpMapImpl {
        private final ServletContext servletContext;

        ServletContextAttributesMap(ServletContext servletContext) {
            this.servletContext = servletContext;
        }

        @Override // org.thymeleaf.context.WebEngineContext.NoOpMapImpl, java.util.Map
        public int size() {
            int size = 0;
            Enumeration<String> attributeNames = this.servletContext.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                attributeNames.nextElement();
                size++;
            }
            return size;
        }

        @Override // org.thymeleaf.context.WebEngineContext.NoOpMapImpl, java.util.Map
        public boolean isEmpty() {
            Enumeration<String> attributeNames = this.servletContext.getAttributeNames();
            return !attributeNames.hasMoreElements();
        }

        @Override // org.thymeleaf.context.WebEngineContext.NoOpMapImpl, java.util.Map
        public boolean containsKey(Object key) {
            return true;
        }

        @Override // org.thymeleaf.context.WebEngineContext.NoOpMapImpl, java.util.Map
        public boolean containsValue(Object value) {
            throw new UnsupportedOperationException("Map does not support #containsValue()");
        }

        @Override // org.thymeleaf.context.WebEngineContext.NoOpMapImpl, java.util.Map
        public Object get(Object key) {
            return WebEngineContext.resolveLazy(this.servletContext.getAttribute(key != null ? key.toString() : null));
        }

        @Override // org.thymeleaf.context.WebEngineContext.NoOpMapImpl, java.util.Map
        public Set<String> keySet() {
            Set<String> keySet = new LinkedHashSet<>(5);
            Enumeration<String> attributeNames = this.servletContext.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                keySet.add(attributeNames.nextElement());
            }
            return keySet;
        }

        @Override // org.thymeleaf.context.WebEngineContext.NoOpMapImpl, java.util.Map
        public Collection<Object> values() {
            List<Object> values = new ArrayList<>(5);
            Enumeration<String> attributeNames = this.servletContext.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                values.add(this.servletContext.getAttribute(attributeNames.nextElement()));
            }
            return values;
        }

        @Override // org.thymeleaf.context.WebEngineContext.NoOpMapImpl, java.util.Map
        public Set<Map.Entry<String, Object>> entrySet() {
            Set<Map.Entry<String, Object>> entrySet = new LinkedHashSet<>(5);
            Enumeration<String> attributeNames = this.servletContext.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String key = attributeNames.nextElement();
                Object value = this.servletContext.getAttribute(key);
                entrySet.add(new NoOpMapImpl.MapEntry(key, value));
            }
            return entrySet;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/WebEngineContext$RequestParametersMap.class */
    private static final class RequestParametersMap extends NoOpMapImpl {
        private final HttpServletRequest request;

        RequestParametersMap(HttpServletRequest request) {
            this.request = request;
        }

        @Override // org.thymeleaf.context.WebEngineContext.NoOpMapImpl, java.util.Map
        public int size() {
            return this.request.getParameterMap().size();
        }

        @Override // org.thymeleaf.context.WebEngineContext.NoOpMapImpl, java.util.Map
        public boolean isEmpty() {
            return this.request.getParameterMap().isEmpty();
        }

        @Override // org.thymeleaf.context.WebEngineContext.NoOpMapImpl, java.util.Map
        public boolean containsKey(Object key) {
            return true;
        }

        @Override // org.thymeleaf.context.WebEngineContext.NoOpMapImpl, java.util.Map
        public boolean containsValue(Object value) {
            throw new UnsupportedOperationException("Map does not support #containsValue()");
        }

        @Override // org.thymeleaf.context.WebEngineContext.NoOpMapImpl, java.util.Map
        public Object get(Object key) {
            String[] parameterValues = this.request.getParameterValues(key != null ? key.toString() : null);
            if (parameterValues == null) {
                return null;
            }
            return new RequestParameterValues(parameterValues);
        }

        @Override // org.thymeleaf.context.WebEngineContext.NoOpMapImpl, java.util.Map
        public Set<String> keySet() {
            return this.request.getParameterMap().keySet();
        }

        @Override // org.thymeleaf.context.WebEngineContext.NoOpMapImpl, java.util.Map
        public Collection<Object> values() {
            return this.request.getParameterMap().values();
        }

        @Override // org.thymeleaf.context.WebEngineContext.NoOpMapImpl, java.util.Map
        public Set<Map.Entry<String, Object>> entrySet() {
            return this.request.getParameterMap().entrySet();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/WebEngineContext$RequestAttributesVariablesMap.class */
    private static final class RequestAttributesVariablesMap extends AbstractEngineContext implements IEngineContext {
        private static final int DEFAULT_ELEMENT_HIERARCHY_SIZE = 20;
        private static final int DEFAULT_LEVELS_SIZE = 10;
        private static final int DEFAULT_LEVELARRAYS_SIZE = 5;
        private final HttpServletRequest request;
        private int level;
        private int index;
        private int[] levels;
        private String[][] names;
        private Object[][] oldValues;
        private Object[][] newValues;
        private int[] levelSizes;
        private SelectionTarget[] selectionTargets;
        private IInliner[] inliners;
        private TemplateData[] templateDatas;
        private IProcessableElementTag[] elementTags;
        private SelectionTarget lastSelectionTarget;
        private IInliner lastInliner;
        private TemplateData lastTemplateData;
        private final List<TemplateData> templateStack;

        /* JADX WARN: Type inference failed for: r1v10, types: [java.lang.String[], java.lang.String[][]] */
        /* JADX WARN: Type inference failed for: r1v12, types: [java.lang.Object[], java.lang.Object[][]] */
        /* JADX WARN: Type inference failed for: r1v14, types: [java.lang.Object[], java.lang.Object[][]] */
        RequestAttributesVariablesMap(IEngineConfiguration configuration, TemplateData templateData, Map<String, Object> templateResolutionAttributes, HttpServletRequest request, Locale locale, Map<String, Object> variables) {
            super(configuration, templateResolutionAttributes, locale);
            this.level = 0;
            this.index = 0;
            this.lastSelectionTarget = null;
            this.lastInliner = null;
            this.lastTemplateData = null;
            this.request = request;
            this.levels = new int[10];
            this.names = new String[10];
            this.oldValues = new Object[10];
            this.newValues = new Object[10];
            this.levelSizes = new int[10];
            this.selectionTargets = new SelectionTarget[10];
            this.inliners = new IInliner[10];
            this.templateDatas = new TemplateData[10];
            this.elementTags = new IProcessableElementTag[20];
            Arrays.fill(this.levels, Integer.MAX_VALUE);
            Arrays.fill(this.names, (Object) null);
            Arrays.fill(this.oldValues, (Object) null);
            Arrays.fill(this.newValues, (Object) null);
            Arrays.fill(this.levelSizes, 0);
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
            return this.request.getAttribute(name) != null;
        }

        @Override // org.thymeleaf.context.IContext
        public Object getVariable(String key) {
            return WebEngineContext.resolveLazy(this.request.getAttribute(key));
        }

        @Override // org.thymeleaf.context.IContext
        public Set<String> getVariableNames() {
            Set<String> variableNames = new HashSet<>(10);
            Enumeration<String> attributeNamesEnum = this.request.getAttributeNames();
            while (attributeNamesEnum.hasMoreElements()) {
                variableNames.add(attributeNamesEnum.nextElement());
            }
            return variableNames;
        }

        private int searchNameInIndex(String name, int idx) {
            int n = this.levelSizes[idx];
            if (name == null) {
                do {
                    int i = n;
                    n--;
                    if (i == 0) {
                        return -1;
                    }
                } while (this.names[idx][n] != null);
                return n;
            }
            do {
                int i2 = n;
                n--;
                if (i2 == 0) {
                    return -1;
                }
            } while (!name.equals(this.names[idx][n]));
            return n;
        }

        @Override // org.thymeleaf.context.IEngineContext
        public void setVariable(String name, Object value) {
            ensureLevelInitialized(true);
            if (this.level > 0) {
                int levelIndex = searchNameInIndex(name, this.index);
                if (levelIndex >= 0) {
                    this.newValues[this.index][levelIndex] = value;
                } else {
                    if (this.names[this.index].length == this.levelSizes[this.index]) {
                        this.names[this.index] = (String[]) Arrays.copyOf(this.names[this.index], this.names[this.index].length + 5);
                        this.newValues[this.index] = Arrays.copyOf(this.newValues[this.index], this.newValues[this.index].length + 5);
                        this.oldValues[this.index] = Arrays.copyOf(this.oldValues[this.index], this.oldValues[this.index].length + 5);
                    }
                    int levelIndex2 = this.levelSizes[this.index];
                    this.names[this.index][levelIndex2] = name;
                    this.oldValues[this.index][levelIndex2] = this.request.getAttribute(name);
                    this.newValues[this.index][levelIndex2] = value;
                    int[] iArr = this.levelSizes;
                    int i = this.index;
                    iArr[i] = iArr[i] + 1;
                }
            }
            this.request.setAttribute(name, value);
        }

        @Override // org.thymeleaf.context.IEngineContext
        public void setVariables(Map<String, Object> variables) {
            if (variables == null || variables.isEmpty()) {
                return;
            }
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                setVariable(entry.getKey(), entry.getValue());
            }
        }

        @Override // org.thymeleaf.context.IEngineContext
        public void removeVariable(String name) {
            setVariable(name, null);
        }

        @Override // org.thymeleaf.context.IEngineContext
        public boolean isVariableLocal(String name) {
            int idx;
            if (this.level == 0) {
                return false;
            }
            int n = this.index + 1;
            do {
                int i = n;
                n--;
                if (i > 1) {
                    idx = searchNameInIndex(name, n);
                } else {
                    return false;
                }
            } while (idx < 0);
            return this.newValues[n][idx] != null;
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
            ensureLevelInitialized(false);
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
            ensureLevelInitialized(false);
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
            ensureLevelInitialized(false);
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

        private void ensureLevelInitialized(boolean initVariables) {
            if (this.levels[this.index] != this.level) {
                this.index++;
                if (this.levels.length == this.index) {
                    this.levels = Arrays.copyOf(this.levels, this.levels.length + 10);
                    Arrays.fill(this.levels, this.index, this.levels.length, Integer.MAX_VALUE);
                    this.names = (String[][]) Arrays.copyOf(this.names, this.names.length + 10);
                    this.newValues = (Object[][]) Arrays.copyOf(this.newValues, this.newValues.length + 10);
                    this.oldValues = (Object[][]) Arrays.copyOf(this.oldValues, this.oldValues.length + 10);
                    this.levelSizes = Arrays.copyOf(this.levelSizes, this.levelSizes.length + 10);
                    this.selectionTargets = (SelectionTarget[]) Arrays.copyOf(this.selectionTargets, this.selectionTargets.length + 10);
                    this.inliners = (IInliner[]) Arrays.copyOf(this.inliners, this.inliners.length + 10);
                    this.templateDatas = (TemplateData[]) Arrays.copyOf(this.templateDatas, this.templateDatas.length + 10);
                }
                this.levels[this.index] = this.level;
            }
            if (this.level > 0 && initVariables && this.names[this.index] == null) {
                this.names[this.index] = new String[5];
                Arrays.fill(this.names[this.index], (Object) null);
                this.newValues[this.index] = new Object[5];
                Arrays.fill(this.newValues[this.index], (Object) null);
                this.oldValues[this.index] = new Object[5];
                Arrays.fill(this.oldValues[this.index], (Object) null);
                this.levelSizes[this.index] = 0;
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
                if (this.names[this.index] != null && this.levelSizes[this.index] > 0) {
                    int n = this.levelSizes[this.index];
                    while (true) {
                        int i = n;
                        n--;
                        if (i == 0) {
                            break;
                        }
                        String name = this.names[this.index][n];
                        Object newValue = this.newValues[this.index][n];
                        Object oldValue = this.oldValues[this.index][n];
                        Object currentValue = this.request.getAttribute(name);
                        if (newValue == currentValue) {
                            this.request.setAttribute(name, oldValue);
                        }
                    }
                    this.levelSizes[this.index] = 0;
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
            Object oldValue;
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append('{');
            Map<String, Object> oldValuesSum = new LinkedHashMap<>();
            int n = this.index + 1;
            while (true) {
                int i = n;
                n--;
                if (i == 1) {
                    break;
                }
                Map<String, Object> levelVars = new LinkedHashMap<>();
                if (this.names[n] != null && this.levelSizes[n] > 0) {
                    for (int i2 = 0; i2 < this.levelSizes[n]; i2++) {
                        String name = this.names[n][i2];
                        Object newValue = this.newValues[n][i2];
                        Object oldValue2 = this.oldValues[n][i2];
                        if (newValue != oldValue2) {
                            if (!oldValuesSum.containsKey(name)) {
                                if (newValue != this.request.getAttribute(name)) {
                                }
                                levelVars.put(name, newValue);
                                oldValuesSum.put(name, oldValue2);
                            } else {
                                if (newValue != oldValuesSum.get(name)) {
                                }
                                levelVars.put(name, newValue);
                                oldValuesSum.put(name, oldValue2);
                            }
                        }
                    }
                }
                if (!levelVars.isEmpty() || this.selectionTargets[n] != null || this.inliners[n] != null) {
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
            }
            Map<String, Object> requestAttributes = new LinkedHashMap<>();
            Enumeration<String> attrNames = this.request.getAttributeNames();
            while (attrNames.hasMoreElements()) {
                String name2 = attrNames.nextElement();
                if (oldValuesSum.containsKey(name2)) {
                    if (oldValuesSum.get(name2) != null) {
                        requestAttributes.put(name2, oldValuesSum.get(name2));
                    }
                    oldValuesSum.remove(name2);
                } else {
                    requestAttributes.put(name2, this.request.getAttribute(name2));
                }
            }
            for (Map.Entry<String, Object> oldValuesSumEntry : oldValuesSum.entrySet()) {
                String name3 = oldValuesSumEntry.getKey();
                if (!requestAttributes.containsKey(name3) && (oldValue = oldValuesSumEntry.getValue()) != null) {
                    requestAttributes.put(name3, oldValue);
                }
            }
            if (strBuilder.length() > 1) {
                strBuilder.append(',');
            }
            strBuilder.append(this.levels[n]).append(":");
            strBuilder.append(requestAttributes.toString());
            if (this.selectionTargets[0] != null) {
                strBuilder.append("<").append(this.selectionTargets[0].selectionTarget).append(">");
            }
            if (this.inliners[0] != null) {
                strBuilder.append(PropertyAccessor.PROPERTY_KEY_PREFIX).append(this.inliners[0].getName()).append("]");
            }
            if (this.templateDatas[0] != null) {
                strBuilder.append("(").append(this.templateDatas[0].getTemplate()).append(")");
            }
            strBuilder.append("}[");
            strBuilder.append(this.level);
            strBuilder.append(']');
            return strBuilder.toString();
        }

        public String toString() {
            Map<String, Object> equivalentMap = new LinkedHashMap<>();
            Enumeration<String> attributeNamesEnum = this.request.getAttributeNames();
            while (attributeNamesEnum.hasMoreElements()) {
                String name = attributeNamesEnum.nextElement();
                equivalentMap.put(name, this.request.getAttribute(name));
            }
            String textInliningStr = getInliner() != null ? PropertyAccessor.PROPERTY_KEY_PREFIX + getInliner().getName() + "]" : "";
            String templateDataStr = "(" + getTemplateData().getTemplate() + ")";
            return equivalentMap.toString() + (hasSelectionTarget() ? "<" + getSelectionTarget() + ">" : "") + textInliningStr + templateDataStr;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/WebEngineContext$RequestAttributesVariablesMap$SelectionTarget.class */
        public static final class SelectionTarget {
            final Object selectionTarget;

            SelectionTarget(Object selectionTarget) {
                this.selectionTarget = selectionTarget;
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/WebEngineContext$NoOpMapImpl.class */
    private static abstract class NoOpMapImpl implements Map<String, Object> {
        protected NoOpMapImpl() {
        }

        @Override // java.util.Map
        public int size() {
            return 0;
        }

        @Override // java.util.Map
        public boolean isEmpty() {
            return true;
        }

        @Override // java.util.Map
        public boolean containsKey(Object key) {
            return false;
        }

        @Override // java.util.Map
        public boolean containsValue(Object value) {
            return false;
        }

        @Override // java.util.Map
        public Object get(Object key) {
            return null;
        }

        @Override // java.util.Map
        public Object put(String key, Object value) {
            throw new UnsupportedOperationException("Cannot add new entry: map is immutable");
        }

        @Override // java.util.Map
        public Object remove(Object key) {
            throw new UnsupportedOperationException("Cannot remove entry: map is immutable");
        }

        @Override // java.util.Map
        public void putAll(Map<? extends String, ? extends Object> m) {
            throw new UnsupportedOperationException("Cannot add new entry: map is immutable");
        }

        @Override // java.util.Map
        public void clear() {
            throw new UnsupportedOperationException("Cannot clear: map is immutable");
        }

        @Override // java.util.Map
        public Set<String> keySet() {
            return Collections.emptySet();
        }

        @Override // java.util.Map
        public Collection<Object> values() {
            return Collections.emptyList();
        }

        @Override // java.util.Map
        public Set<Map.Entry<String, Object>> entrySet() {
            return Collections.emptySet();
        }

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/WebEngineContext$NoOpMapImpl$MapEntry.class */
        static final class MapEntry implements Map.Entry<String, Object> {
            private final String key;
            private final Object value;

            MapEntry(String key, Object value) {
                this.key = key;
                this.value = value;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.Map.Entry
            public String getKey() {
                return this.key;
            }

            @Override // java.util.Map.Entry
            public Object getValue() {
                return this.value;
            }

            @Override // java.util.Map.Entry
            public Object setValue(Object value) {
                throw new UnsupportedOperationException("Cannot set value: map is immutable");
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/WebEngineContext$RequestParameterValues.class */
    private static final class RequestParameterValues extends AbstractList<String> {
        private final String[] parameterValues;
        public final int length;

        RequestParameterValues(String[] parameterValues) {
            this.parameterValues = parameterValues;
            this.length = this.parameterValues.length;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.length;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public Object[] toArray() {
            return (Object[]) this.parameterValues.clone();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public <T> T[] toArray(T[] arr) {
            if (arr.length < this.length) {
                T[] copy = (T[]) ((Object[]) Array.newInstance(arr.getClass().getComponentType(), this.length));
                System.arraycopy(this.parameterValues, 0, copy, 0, this.length);
                return copy;
            }
            System.arraycopy(this.parameterValues, 0, arr, 0, this.length);
            if (arr.length > this.length) {
                arr[this.length] = null;
            }
            return arr;
        }

        @Override // java.util.AbstractList, java.util.List
        public String get(int index) {
            return this.parameterValues[index];
        }

        @Override // java.util.AbstractList, java.util.List
        public int indexOf(Object obj) {
            String[] a = this.parameterValues;
            if (obj == null) {
                for (int i = 0; i < a.length; i++) {
                    if (a[i] == null) {
                        return i;
                    }
                }
                return -1;
            }
            for (int i2 = 0; i2 < a.length; i2++) {
                if (obj.equals(a[i2])) {
                    return i2;
                }
            }
            return -1;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public boolean contains(Object obj) {
            return indexOf(obj) != -1;
        }

        @Override // java.util.AbstractCollection
        public String toString() {
            if (this.length == 0) {
                return "";
            }
            if (this.length == 1) {
                return this.parameterValues[0];
            }
            return super.toString();
        }
    }
}