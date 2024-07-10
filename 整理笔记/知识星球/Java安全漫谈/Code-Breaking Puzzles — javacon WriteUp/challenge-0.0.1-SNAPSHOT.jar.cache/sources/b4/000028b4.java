package org.thymeleaf.spring5.context.webflux;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.AbstractEngineContext;
import org.thymeleaf.context.EngineContext;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.ILazyContextVariable;
import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.inline.IInliner;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.spring5.context.SpringContextUtils;
import org.thymeleaf.util.Validate;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/context/webflux/SpringWebFluxEngineContext.class */
public class SpringWebFluxEngineContext extends AbstractEngineContext implements IEngineContext, ISpringWebFluxContext {
    private static final String PARAM_VARIABLE_NAME = "param";
    private static final String SESSION_VARIABLE_NAME = "session";
    private final ServerHttpRequest request;
    private final ServerHttpResponse response;
    private final ServerWebExchange exchange;
    private final WebExchangeAttributesVariablesMap webExchangeAttributesVariablesMap;
    private final Map<String, Object> requestParametersVariablesMap;
    private final Map<String, Object> sessionAttributesVariablesMap;

    public SpringWebFluxEngineContext(IEngineConfiguration configuration, TemplateData templateData, Map<String, Object> templateResolutionAttributes, ServerWebExchange exchange, Locale locale, Map<String, Object> variables) {
        super(configuration, templateResolutionAttributes, locale);
        Validate.notNull(exchange, "Server Web Exchange cannot be null in web variables map");
        this.exchange = exchange;
        this.request = this.exchange.getRequest();
        this.response = this.exchange.getResponse();
        this.webExchangeAttributesVariablesMap = new WebExchangeAttributesVariablesMap(configuration, templateData, templateResolutionAttributes, this.exchange, locale, variables);
        this.requestParametersVariablesMap = new RequestParametersMap(this.request);
        this.sessionAttributesVariablesMap = new SessionAttributesMap(this.webExchangeAttributesVariablesMap);
    }

    @Override // org.thymeleaf.spring5.context.webflux.ISpringWebFluxContext
    public ServerHttpRequest getRequest() {
        return this.request;
    }

    @Override // org.thymeleaf.spring5.context.webflux.ISpringWebFluxContext
    public ServerHttpResponse getResponse() {
        return this.response;
    }

    @Override // org.thymeleaf.spring5.context.webflux.ISpringWebFluxContext
    public Mono<WebSession> getSession() {
        return this.exchange.getSession();
    }

    @Override // org.thymeleaf.spring5.context.webflux.ISpringWebFluxContext
    public ServerWebExchange getExchange() {
        return this.exchange;
    }

    @Override // org.thymeleaf.context.IContext
    public boolean containsVariable(String name) {
        return "session".equals(name) || PARAM_VARIABLE_NAME.equals(name) || this.webExchangeAttributesVariablesMap.containsVariable(name);
    }

    @Override // org.thymeleaf.context.IContext
    public Object getVariable(String key) {
        if ("session".equals(key)) {
            return this.sessionAttributesVariablesMap;
        }
        if (PARAM_VARIABLE_NAME.equals(key)) {
            return this.requestParametersVariablesMap;
        }
        return this.webExchangeAttributesVariablesMap.getVariable(key);
    }

    @Override // org.thymeleaf.context.IContext
    public Set<String> getVariableNames() {
        return this.webExchangeAttributesVariablesMap.getVariableNames();
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void setVariable(String name, Object value) {
        if ("session".equals(name) || PARAM_VARIABLE_NAME.equals(name)) {
            throw new IllegalArgumentException("Cannot set variable called '" + name + "' into web variables map: such name is a reserved word");
        }
        this.webExchangeAttributesVariablesMap.setVariable(name, value);
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
            if (r0 == 0) goto L62
            r0 = r7
            java.lang.Object r0 = r0.next()
            java.lang.String r0 = (java.lang.String) r0
            r8 = r0
            java.lang.String r0 = "session"
            r1 = r8
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L3f
            java.lang.String r0 = "param"
            r1 = r8
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L5f
        L3f:
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
        L5f:
            goto L1a
        L62:
            r0 = r5
            org.thymeleaf.spring5.context.webflux.SpringWebFluxEngineContext$WebExchangeAttributesVariablesMap r0 = r0.webExchangeAttributesVariablesMap
            r1 = r6
            r0.setVariables(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.thymeleaf.spring5.context.webflux.SpringWebFluxEngineContext.setVariables(java.util.Map):void");
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void removeVariable(String name) {
        if ("session".equals(name) || PARAM_VARIABLE_NAME.equals(name)) {
            throw new IllegalArgumentException("Cannot remove variable called '" + name + "' in web variables map: such name is a reserved word");
        }
        this.webExchangeAttributesVariablesMap.removeVariable(name);
    }

    @Override // org.thymeleaf.context.IEngineContext
    public boolean isVariableLocal(String name) {
        return this.webExchangeAttributesVariablesMap.isVariableLocal(name);
    }

    @Override // org.thymeleaf.context.ITemplateContext
    public boolean hasSelectionTarget() {
        return this.webExchangeAttributesVariablesMap.hasSelectionTarget();
    }

    @Override // org.thymeleaf.context.ITemplateContext
    public Object getSelectionTarget() {
        return this.webExchangeAttributesVariablesMap.getSelectionTarget();
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void setSelectionTarget(Object selectionTarget) {
        this.webExchangeAttributesVariablesMap.setSelectionTarget(selectionTarget);
    }

    @Override // org.thymeleaf.context.ITemplateContext
    public IInliner getInliner() {
        return this.webExchangeAttributesVariablesMap.getInliner();
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void setInliner(IInliner inliner) {
        this.webExchangeAttributesVariablesMap.setInliner(inliner);
    }

    @Override // org.thymeleaf.context.ITemplateContext
    public TemplateData getTemplateData() {
        return this.webExchangeAttributesVariablesMap.getTemplateData();
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void setTemplateData(TemplateData templateData) {
        this.webExchangeAttributesVariablesMap.setTemplateData(templateData);
    }

    @Override // org.thymeleaf.context.ITemplateContext
    public List<TemplateData> getTemplateStack() {
        return this.webExchangeAttributesVariablesMap.getTemplateStack();
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void setElementTag(IProcessableElementTag elementTag) {
        this.webExchangeAttributesVariablesMap.setElementTag(elementTag);
    }

    @Override // org.thymeleaf.context.ITemplateContext
    public List<IProcessableElementTag> getElementStack() {
        return this.webExchangeAttributesVariablesMap.getElementStack();
    }

    @Override // org.thymeleaf.context.IEngineContext
    public List<IProcessableElementTag> getElementStackAbove(int contextLevel) {
        return this.webExchangeAttributesVariablesMap.getElementStackAbove(contextLevel);
    }

    @Override // org.thymeleaf.context.IEngineContext
    public int level() {
        return this.webExchangeAttributesVariablesMap.level();
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void increaseLevel() {
        this.webExchangeAttributesVariablesMap.increaseLevel();
    }

    @Override // org.thymeleaf.context.IEngineContext
    public void decreaseLevel() {
        this.webExchangeAttributesVariablesMap.decreaseLevel();
    }

    public String getStringRepresentationByLevel() {
        return this.webExchangeAttributesVariablesMap.getStringRepresentationByLevel();
    }

    public String toString() {
        return this.webExchangeAttributesVariablesMap.toString();
    }

    static Object resolveLazy(Object variable) {
        if (variable != null && (variable instanceof ILazyContextVariable)) {
            return ((ILazyContextVariable) variable).getValue();
        }
        return variable;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/context/webflux/SpringWebFluxEngineContext$SessionAttributesMap.class */
    private static final class SessionAttributesMap extends NoOpMapImpl {
        private final WebExchangeAttributesVariablesMap attrVars;
        private WebSession session = null;

        SessionAttributesMap(WebExchangeAttributesVariablesMap attrVars) {
            this.attrVars = attrVars;
        }

        private WebSession getSession() {
            if (this.session == null) {
                this.session = (WebSession) this.attrVars.getVariable(SpringContextUtils.WEB_SESSION_ATTRIBUTE_NAME);
            }
            return this.session;
        }

        @Override // org.thymeleaf.spring5.context.webflux.SpringWebFluxEngineContext.NoOpMapImpl, java.util.Map
        public int size() {
            WebSession webSession = getSession();
            if (webSession == null) {
                return 0;
            }
            return webSession.getAttributes().size();
        }

        @Override // org.thymeleaf.spring5.context.webflux.SpringWebFluxEngineContext.NoOpMapImpl, java.util.Map
        public boolean isEmpty() {
            WebSession webSession = getSession();
            if (webSession == null) {
                return true;
            }
            return webSession.getAttributes().isEmpty();
        }

        @Override // org.thymeleaf.spring5.context.webflux.SpringWebFluxEngineContext.NoOpMapImpl, java.util.Map
        public boolean containsKey(Object key) {
            return true;
        }

        @Override // org.thymeleaf.spring5.context.webflux.SpringWebFluxEngineContext.NoOpMapImpl, java.util.Map
        public boolean containsValue(Object value) {
            throw new UnsupportedOperationException("Map does not support #containsValue()");
        }

        @Override // org.thymeleaf.spring5.context.webflux.SpringWebFluxEngineContext.NoOpMapImpl, java.util.Map
        public Object get(Object key) {
            WebSession webSession = getSession();
            if (webSession == null) {
                return null;
            }
            return SpringWebFluxEngineContext.resolveLazy(webSession.getAttributes().get(key != null ? key.toString() : null));
        }

        @Override // org.thymeleaf.spring5.context.webflux.SpringWebFluxEngineContext.NoOpMapImpl, java.util.Map
        public Set<String> keySet() {
            WebSession webSession = getSession();
            if (webSession == null) {
                return Collections.emptySet();
            }
            return webSession.getAttributes().keySet();
        }

        @Override // org.thymeleaf.spring5.context.webflux.SpringWebFluxEngineContext.NoOpMapImpl, java.util.Map
        public Collection<Object> values() {
            WebSession webSession = getSession();
            if (webSession == null) {
                return Collections.emptyList();
            }
            return webSession.getAttributes().values();
        }

        @Override // org.thymeleaf.spring5.context.webflux.SpringWebFluxEngineContext.NoOpMapImpl, java.util.Map
        public Set<Map.Entry<String, Object>> entrySet() {
            WebSession webSession = getSession();
            if (webSession == null) {
                return Collections.emptySet();
            }
            return webSession.getAttributes().entrySet();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/context/webflux/SpringWebFluxEngineContext$RequestParametersMap.class */
    private static final class RequestParametersMap extends NoOpMapImpl {
        private final ServerHttpRequest request;

        RequestParametersMap(ServerHttpRequest request) {
            this.request = request;
        }

        @Override // org.thymeleaf.spring5.context.webflux.SpringWebFluxEngineContext.NoOpMapImpl, java.util.Map
        public int size() {
            return this.request.getQueryParams().size();
        }

        @Override // org.thymeleaf.spring5.context.webflux.SpringWebFluxEngineContext.NoOpMapImpl, java.util.Map
        public boolean isEmpty() {
            return this.request.getQueryParams().isEmpty();
        }

        @Override // org.thymeleaf.spring5.context.webflux.SpringWebFluxEngineContext.NoOpMapImpl, java.util.Map
        public boolean containsKey(Object key) {
            return true;
        }

        @Override // org.thymeleaf.spring5.context.webflux.SpringWebFluxEngineContext.NoOpMapImpl, java.util.Map
        public boolean containsValue(Object value) {
            throw new UnsupportedOperationException("Map does not support #containsValue()");
        }

        @Override // org.thymeleaf.spring5.context.webflux.SpringWebFluxEngineContext.NoOpMapImpl, java.util.Map
        public Object get(Object key) {
            List<String> parameterValues = (List) this.request.getQueryParams().get(key != null ? key.toString() : null);
            if (parameterValues == null) {
                return null;
            }
            return new RequestParameterValues(parameterValues);
        }

        @Override // org.thymeleaf.spring5.context.webflux.SpringWebFluxEngineContext.NoOpMapImpl, java.util.Map
        public Set<String> keySet() {
            return this.request.getQueryParams().keySet();
        }

        @Override // org.thymeleaf.spring5.context.webflux.SpringWebFluxEngineContext.NoOpMapImpl, java.util.Map
        public Collection<Object> values() {
            return this.request.getQueryParams().values();
        }

        @Override // org.thymeleaf.spring5.context.webflux.SpringWebFluxEngineContext.NoOpMapImpl, java.util.Map
        public Set<Map.Entry<String, Object>> entrySet() {
            return this.request.getQueryParams().entrySet();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/context/webflux/SpringWebFluxEngineContext$WebExchangeAttributesVariablesMap.class */
    public static final class WebExchangeAttributesVariablesMap extends EngineContext {
        private final ServerWebExchange exchange;

        WebExchangeAttributesVariablesMap(IEngineConfiguration configuration, TemplateData templateData, Map<String, Object> templateResolutionAttributes, ServerWebExchange exchange, Locale locale, Map<String, Object> variables) {
            super(configuration, templateData, templateResolutionAttributes, locale, variables);
            this.exchange = exchange;
        }

        @Override // org.thymeleaf.context.EngineContext, org.thymeleaf.context.IContext
        public boolean containsVariable(String name) {
            if (super.containsVariable(name)) {
                return true;
            }
            return this.exchange.getAttributes().containsKey(name);
        }

        @Override // org.thymeleaf.context.EngineContext, org.thymeleaf.context.IContext
        public Object getVariable(String key) {
            Object value = super.getVariable(key);
            if (value != null) {
                return value;
            }
            return this.exchange.getAttributes().get(key);
        }

        @Override // org.thymeleaf.context.EngineContext, org.thymeleaf.context.IContext
        public Set<String> getVariableNames() {
            Set<String> variableNames = super.getVariableNames();
            variableNames.addAll(this.exchange.getAttributes().keySet());
            return variableNames;
        }

        @Override // org.thymeleaf.context.EngineContext
        public String getStringRepresentationByLevel() {
            StringBuilder strBuilder = new StringBuilder(super.getStringRepresentationByLevel());
            strBuilder.append("[[EXCHANGE: " + this.exchange.getAttributes() + "]]");
            return strBuilder.toString();
        }

        @Override // org.thymeleaf.context.EngineContext
        public String toString() {
            StringBuilder strBuilder = new StringBuilder(super.toString());
            strBuilder.append("[[EXCHANGE: " + this.exchange.getAttributes() + "]]");
            return strBuilder.toString();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/context/webflux/SpringWebFluxEngineContext$NoOpMapImpl.class */
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

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/context/webflux/SpringWebFluxEngineContext$NoOpMapImpl$MapEntry.class */
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

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/context/webflux/SpringWebFluxEngineContext$RequestParameterValues.class */
    private static final class RequestParameterValues extends AbstractList<String> {
        private final List<String> parameterValues;

        RequestParameterValues(List<String> parameterValues) {
            this.parameterValues = parameterValues;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public int size() {
            return this.parameterValues.size();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public Object[] toArray() {
            return this.parameterValues.toArray();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public <T> T[] toArray(T[] arr) {
            return (T[]) this.parameterValues.toArray(arr);
        }

        @Override // java.util.AbstractList, java.util.List
        public String get(int index) {
            return this.parameterValues.get(index);
        }

        @Override // java.util.AbstractList, java.util.List
        public int indexOf(Object obj) {
            return this.parameterValues.indexOf(obj);
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
        public boolean contains(Object obj) {
            return this.parameterValues.contains(obj);
        }

        @Override // java.util.AbstractCollection
        public String toString() {
            int size = this.parameterValues.size();
            if (size == 0) {
                return "";
            }
            if (size == 1) {
                return this.parameterValues.get(0);
            }
            return this.parameterValues.toString();
        }
    }
}