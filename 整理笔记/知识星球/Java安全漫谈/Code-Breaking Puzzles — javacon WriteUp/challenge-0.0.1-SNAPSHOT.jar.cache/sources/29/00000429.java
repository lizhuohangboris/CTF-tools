package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/introspect/POJOPropertiesCollector.class */
public class POJOPropertiesCollector {
    protected final MapperConfig<?> _config;
    protected final boolean _forSerialization;
    protected final boolean _stdBeanNaming;
    protected final JavaType _type;
    protected final AnnotatedClass _classDef;
    protected final VisibilityChecker<?> _visibilityChecker;
    protected final AnnotationIntrospector _annotationIntrospector;
    protected final boolean _useAnnotations;
    protected final String _mutatorPrefix;
    protected boolean _collected;
    protected LinkedHashMap<String, POJOPropertyBuilder> _properties;
    protected LinkedList<POJOPropertyBuilder> _creatorProperties;
    protected LinkedList<AnnotatedMember> _anyGetters;
    protected LinkedList<AnnotatedMethod> _anySetters;
    protected LinkedList<AnnotatedMember> _anySetterField;
    protected LinkedList<AnnotatedMember> _jsonValueAccessors;
    protected HashSet<String> _ignoredPropertyNames;
    protected LinkedHashMap<Object, AnnotatedMember> _injectables;

    /* JADX INFO: Access modifiers changed from: protected */
    public POJOPropertiesCollector(MapperConfig<?> config, boolean forSerialization, JavaType type, AnnotatedClass classDef, String mutatorPrefix) {
        this._config = config;
        this._stdBeanNaming = config.isEnabled(MapperFeature.USE_STD_BEAN_NAMING);
        this._forSerialization = forSerialization;
        this._type = type;
        this._classDef = classDef;
        this._mutatorPrefix = mutatorPrefix == null ? "set" : mutatorPrefix;
        if (config.isAnnotationProcessingEnabled()) {
            this._useAnnotations = true;
            this._annotationIntrospector = this._config.getAnnotationIntrospector();
        } else {
            this._useAnnotations = false;
            this._annotationIntrospector = AnnotationIntrospector.nopInstance();
        }
        this._visibilityChecker = this._config.getDefaultVisibilityChecker(type.getRawClass(), classDef);
    }

    public MapperConfig<?> getConfig() {
        return this._config;
    }

    public JavaType getType() {
        return this._type;
    }

    public AnnotatedClass getClassDef() {
        return this._classDef;
    }

    public AnnotationIntrospector getAnnotationIntrospector() {
        return this._annotationIntrospector;
    }

    public List<BeanPropertyDefinition> getProperties() {
        Map<String, POJOPropertyBuilder> props = getPropertyMap();
        return new ArrayList(props.values());
    }

    public Map<Object, AnnotatedMember> getInjectables() {
        if (!this._collected) {
            collectAll();
        }
        return this._injectables;
    }

    @Deprecated
    public AnnotatedMethod getJsonValueMethod() {
        AnnotatedMember m = getJsonValueAccessor();
        if (m instanceof AnnotatedMethod) {
            return (AnnotatedMethod) m;
        }
        return null;
    }

    public AnnotatedMember getJsonValueAccessor() {
        if (!this._collected) {
            collectAll();
        }
        if (this._jsonValueAccessors != null) {
            if (this._jsonValueAccessors.size() > 1) {
                reportProblem("Multiple 'as-value' properties defined (%s vs %s)", this._jsonValueAccessors.get(0), this._jsonValueAccessors.get(1));
            }
            return this._jsonValueAccessors.get(0);
        }
        return null;
    }

    public AnnotatedMember getAnyGetter() {
        if (!this._collected) {
            collectAll();
        }
        if (this._anyGetters != null) {
            if (this._anyGetters.size() > 1) {
                reportProblem("Multiple 'any-getters' defined (%s vs %s)", this._anyGetters.get(0), this._anyGetters.get(1));
            }
            return this._anyGetters.getFirst();
        }
        return null;
    }

    public AnnotatedMember getAnySetterField() {
        if (!this._collected) {
            collectAll();
        }
        if (this._anySetterField != null) {
            if (this._anySetterField.size() > 1) {
                reportProblem("Multiple 'any-setter' fields defined (%s vs %s)", this._anySetterField.get(0), this._anySetterField.get(1));
            }
            return this._anySetterField.getFirst();
        }
        return null;
    }

    public AnnotatedMethod getAnySetterMethod() {
        if (!this._collected) {
            collectAll();
        }
        if (this._anySetters != null) {
            if (this._anySetters.size() > 1) {
                reportProblem("Multiple 'any-setter' methods defined (%s vs %s)", this._anySetters.get(0), this._anySetters.get(1));
            }
            return this._anySetters.getFirst();
        }
        return null;
    }

    public Set<String> getIgnoredPropertyNames() {
        return this._ignoredPropertyNames;
    }

    public ObjectIdInfo getObjectIdInfo() {
        ObjectIdInfo info = this._annotationIntrospector.findObjectIdInfo(this._classDef);
        if (info != null) {
            info = this._annotationIntrospector.findObjectReferenceInfo(this._classDef, info);
        }
        return info;
    }

    public Class<?> findPOJOBuilderClass() {
        return this._annotationIntrospector.findPOJOBuilder(this._classDef);
    }

    protected Map<String, POJOPropertyBuilder> getPropertyMap() {
        if (!this._collected) {
            collectAll();
        }
        return this._properties;
    }

    protected void collectAll() {
        LinkedHashMap<String, POJOPropertyBuilder> props = new LinkedHashMap<>();
        _addFields(props);
        _addMethods(props);
        if (!this._classDef.isNonStaticInnerClass()) {
            _addCreators(props);
        }
        _addInjectables(props);
        _removeUnwantedProperties(props);
        _removeUnwantedAccessor(props);
        _renameProperties(props);
        for (POJOPropertyBuilder property : props.values()) {
            property.mergeAnnotations(this._forSerialization);
        }
        PropertyNamingStrategy naming = _findNamingStrategy();
        if (naming != null) {
            _renameUsing(props, naming);
        }
        for (POJOPropertyBuilder property2 : props.values()) {
            property2.trimByVisibility();
        }
        if (this._config.isEnabled(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME)) {
            _renameWithWrappers(props);
        }
        _sortProperties(props);
        this._properties = props;
        this._collected = true;
    }

    protected void _addFields(Map<String, POJOPropertyBuilder> props) {
        PropertyName pn;
        AnnotationIntrospector ai = this._annotationIntrospector;
        boolean pruneFinalFields = (this._forSerialization || this._config.isEnabled(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS)) ? false : true;
        boolean transientAsIgnoral = this._config.isEnabled(MapperFeature.PROPAGATE_TRANSIENT_MARKER);
        for (AnnotatedField f : this._classDef.fields()) {
            String implName = ai.findImplicitPropertyName(f);
            if (Boolean.TRUE.equals(ai.hasAsValue(f))) {
                if (this._jsonValueAccessors == null) {
                    this._jsonValueAccessors = new LinkedList<>();
                }
                this._jsonValueAccessors.add(f);
            } else if (Boolean.TRUE.equals(ai.hasAnySetter(f))) {
                if (this._anySetterField == null) {
                    this._anySetterField = new LinkedList<>();
                }
                this._anySetterField.add(f);
            } else {
                if (implName == null) {
                    implName = f.getName();
                }
                if (this._forSerialization) {
                    pn = ai.findNameForSerialization(f);
                } else {
                    pn = ai.findNameForDeserialization(f);
                }
                boolean hasName = pn != null;
                boolean nameExplicit = hasName;
                if (nameExplicit && pn.isEmpty()) {
                    pn = _propNameFromSimple(implName);
                    nameExplicit = false;
                }
                boolean visible = pn != null;
                if (!visible) {
                    visible = this._visibilityChecker.isFieldVisible(f);
                }
                boolean ignored = ai.hasIgnoreMarker(f);
                if (f.isTransient() && !hasName) {
                    visible = false;
                    if (transientAsIgnoral) {
                        ignored = true;
                    }
                }
                if (!pruneFinalFields || pn != null || ignored || !Modifier.isFinal(f.getModifiers())) {
                    _property(props, implName).addField(f, pn, nameExplicit, visible, ignored);
                }
            }
        }
    }

    protected void _addCreators(Map<String, POJOPropertyBuilder> props) {
        if (!this._useAnnotations) {
            return;
        }
        for (AnnotatedConstructor ctor : this._classDef.getConstructors()) {
            if (this._creatorProperties == null) {
                this._creatorProperties = new LinkedList<>();
            }
            int len = ctor.getParameterCount();
            for (int i = 0; i < len; i++) {
                _addCreatorParam(props, ctor.getParameter(i));
            }
        }
        for (AnnotatedMethod factory : this._classDef.getFactoryMethods()) {
            if (this._creatorProperties == null) {
                this._creatorProperties = new LinkedList<>();
            }
            int len2 = factory.getParameterCount();
            for (int i2 = 0; i2 < len2; i2++) {
                _addCreatorParam(props, factory.getParameter(i2));
            }
        }
    }

    protected void _addCreatorParam(Map<String, POJOPropertyBuilder> props, AnnotatedParameter param) {
        JsonCreator.Mode creatorMode;
        String impl = this._annotationIntrospector.findImplicitPropertyName(param);
        if (impl == null) {
            impl = "";
        }
        PropertyName pn = this._annotationIntrospector.findNameForDeserialization(param);
        boolean expl = (pn == null || pn.isEmpty()) ? false : true;
        if (!expl) {
            if (impl.isEmpty() || (creatorMode = this._annotationIntrospector.findCreatorAnnotation(this._config, param.getOwner())) == null || creatorMode == JsonCreator.Mode.DISABLED) {
                return;
            }
            pn = PropertyName.construct(impl);
        }
        POJOPropertyBuilder prop = (expl && impl.isEmpty()) ? _property(props, pn) : _property(props, impl);
        prop.addCtor(param, pn, expl, true, false);
        this._creatorProperties.add(prop);
    }

    protected void _addMethods(Map<String, POJOPropertyBuilder> props) {
        AnnotationIntrospector ai = this._annotationIntrospector;
        for (AnnotatedMethod m : this._classDef.memberMethods()) {
            int argCount = m.getParameterCount();
            if (argCount == 0) {
                _addGetterMethod(props, m, ai);
            } else if (argCount == 1) {
                _addSetterMethod(props, m, ai);
            } else if (argCount == 2 && ai != null && Boolean.TRUE.equals(ai.hasAnySetter(m))) {
                if (this._anySetters == null) {
                    this._anySetters = new LinkedList<>();
                }
                this._anySetters.add(m);
            }
        }
    }

    protected void _addGetterMethod(Map<String, POJOPropertyBuilder> props, AnnotatedMethod m, AnnotationIntrospector ai) {
        String implName;
        boolean visible;
        if (!m.hasReturnType()) {
            return;
        }
        if (Boolean.TRUE.equals(ai.hasAnyGetter(m))) {
            if (this._anyGetters == null) {
                this._anyGetters = new LinkedList<>();
            }
            this._anyGetters.add(m);
        } else if (Boolean.TRUE.equals(ai.hasAsValue(m))) {
            if (this._jsonValueAccessors == null) {
                this._jsonValueAccessors = new LinkedList<>();
            }
            this._jsonValueAccessors.add(m);
        } else {
            PropertyName pn = ai.findNameForSerialization(m);
            boolean nameExplicit = pn != null;
            if (!nameExplicit) {
                implName = ai.findImplicitPropertyName(m);
                if (implName == null) {
                    implName = BeanUtil.okNameForRegularGetter(m, m.getName(), this._stdBeanNaming);
                }
                if (implName == null) {
                    implName = BeanUtil.okNameForIsGetter(m, m.getName(), this._stdBeanNaming);
                    if (implName == null) {
                        return;
                    }
                    visible = this._visibilityChecker.isIsGetterVisible(m);
                } else {
                    visible = this._visibilityChecker.isGetterVisible(m);
                }
            } else {
                implName = ai.findImplicitPropertyName(m);
                if (implName == null) {
                    implName = BeanUtil.okNameForGetter(m, this._stdBeanNaming);
                }
                if (implName == null) {
                    implName = m.getName();
                }
                if (pn.isEmpty()) {
                    pn = _propNameFromSimple(implName);
                    nameExplicit = false;
                }
                visible = true;
            }
            boolean ignore = ai.hasIgnoreMarker(m);
            _property(props, implName).addGetter(m, pn, nameExplicit, visible, ignore);
        }
    }

    protected void _addSetterMethod(Map<String, POJOPropertyBuilder> props, AnnotatedMethod m, AnnotationIntrospector ai) {
        String implName;
        boolean visible;
        PropertyName pn = ai == null ? null : ai.findNameForDeserialization(m);
        boolean nameExplicit = pn != null;
        if (!nameExplicit) {
            implName = ai == null ? null : ai.findImplicitPropertyName(m);
            if (implName == null) {
                implName = BeanUtil.okNameForMutator(m, this._mutatorPrefix, this._stdBeanNaming);
            }
            if (implName == null) {
                return;
            }
            visible = this._visibilityChecker.isSetterVisible(m);
        } else {
            implName = ai == null ? null : ai.findImplicitPropertyName(m);
            if (implName == null) {
                implName = BeanUtil.okNameForMutator(m, this._mutatorPrefix, this._stdBeanNaming);
            }
            if (implName == null) {
                implName = m.getName();
            }
            if (pn.isEmpty()) {
                pn = _propNameFromSimple(implName);
                nameExplicit = false;
            }
            visible = true;
        }
        boolean ignore = ai == null ? false : ai.hasIgnoreMarker(m);
        _property(props, implName).addSetter(m, pn, nameExplicit, visible, ignore);
    }

    protected void _addInjectables(Map<String, POJOPropertyBuilder> props) {
        AnnotationIntrospector ai = this._annotationIntrospector;
        for (AnnotatedField f : this._classDef.fields()) {
            _doAddInjectable(ai.findInjectableValue(f), f);
        }
        for (AnnotatedMethod m : this._classDef.memberMethods()) {
            if (m.getParameterCount() == 1) {
                _doAddInjectable(ai.findInjectableValue(m), m);
            }
        }
    }

    protected void _doAddInjectable(JacksonInject.Value injectable, AnnotatedMember m) {
        if (injectable == null) {
            return;
        }
        Object id = injectable.getId();
        if (this._injectables == null) {
            this._injectables = new LinkedHashMap<>();
        }
        AnnotatedMember prev = this._injectables.put(id, m);
        if (prev != null && prev.getClass() == m.getClass()) {
            String type = id.getClass().getName();
            throw new IllegalArgumentException("Duplicate injectable value with id '" + String.valueOf(id) + "' (of type " + type + ")");
        }
    }

    private PropertyName _propNameFromSimple(String simpleName) {
        return PropertyName.construct(simpleName, null);
    }

    protected void _removeUnwantedProperties(Map<String, POJOPropertyBuilder> props) {
        Iterator<POJOPropertyBuilder> it = props.values().iterator();
        while (it.hasNext()) {
            POJOPropertyBuilder prop = it.next();
            if (!prop.anyVisible()) {
                it.remove();
            } else if (prop.anyIgnorals()) {
                if (!prop.isExplicitlyIncluded()) {
                    it.remove();
                    _collectIgnorals(prop.getName());
                } else {
                    prop.removeIgnored();
                    if (!prop.couldDeserialize()) {
                        _collectIgnorals(prop.getName());
                    }
                }
            }
        }
    }

    protected void _removeUnwantedAccessor(Map<String, POJOPropertyBuilder> props) {
        boolean inferMutators = this._config.isEnabled(MapperFeature.INFER_PROPERTY_MUTATORS);
        for (POJOPropertyBuilder prop : props.values()) {
            JsonProperty.Access acc = prop.removeNonVisible(inferMutators);
            if (acc == JsonProperty.Access.READ_ONLY) {
                _collectIgnorals(prop.getName());
            }
        }
    }

    private void _collectIgnorals(String name) {
        if (!this._forSerialization) {
            if (this._ignoredPropertyNames == null) {
                this._ignoredPropertyNames = new HashSet<>();
            }
            this._ignoredPropertyNames.add(name);
        }
    }

    protected void _renameProperties(Map<String, POJOPropertyBuilder> props) {
        Iterator<Map.Entry<String, POJOPropertyBuilder>> it = props.entrySet().iterator();
        LinkedList<POJOPropertyBuilder> renamed = null;
        while (it.hasNext()) {
            Map.Entry<String, POJOPropertyBuilder> entry = it.next();
            POJOPropertyBuilder prop = entry.getValue();
            Collection<PropertyName> l = prop.findExplicitNames();
            if (!l.isEmpty()) {
                it.remove();
                if (renamed == null) {
                    renamed = new LinkedList<>();
                }
                if (l.size() == 1) {
                    PropertyName n = l.iterator().next();
                    renamed.add(prop.withName(n));
                } else {
                    renamed.addAll(prop.explode(l));
                }
            }
        }
        if (renamed != null) {
            Iterator i$ = renamed.iterator();
            while (i$.hasNext()) {
                POJOPropertyBuilder prop2 = i$.next();
                String name = prop2.getName();
                POJOPropertyBuilder old = props.get(name);
                if (old == null) {
                    props.put(name, prop2);
                } else {
                    old.addAll(prop2);
                }
                _updateCreatorProperty(prop2, this._creatorProperties);
                if (this._ignoredPropertyNames != null) {
                    this._ignoredPropertyNames.remove(name);
                }
            }
        }
    }

    protected void _renameUsing(Map<String, POJOPropertyBuilder> propMap, PropertyNamingStrategy naming) {
        String simpleName;
        POJOPropertyBuilder[] props = (POJOPropertyBuilder[]) propMap.values().toArray(new POJOPropertyBuilder[propMap.size()]);
        propMap.clear();
        for (POJOPropertyBuilder prop : props) {
            PropertyName fullName = prop.getFullName();
            String rename = null;
            if (!prop.isExplicitlyNamed() || this._config.isEnabled(MapperFeature.ALLOW_EXPLICIT_PROPERTY_RENAMING)) {
                if (this._forSerialization) {
                    if (prop.hasGetter()) {
                        rename = naming.nameForGetterMethod(this._config, prop.getGetter(), fullName.getSimpleName());
                    } else if (prop.hasField()) {
                        rename = naming.nameForField(this._config, prop.getField(), fullName.getSimpleName());
                    }
                } else if (prop.hasSetter()) {
                    rename = naming.nameForSetterMethod(this._config, prop.getSetter(), fullName.getSimpleName());
                } else if (prop.hasConstructorParameter()) {
                    rename = naming.nameForConstructorParameter(this._config, prop.getConstructorParameter(), fullName.getSimpleName());
                } else if (prop.hasField()) {
                    rename = naming.nameForField(this._config, prop.getField(), fullName.getSimpleName());
                } else if (prop.hasGetter()) {
                    rename = naming.nameForGetterMethod(this._config, prop.getGetter(), fullName.getSimpleName());
                }
            }
            if (rename != null && !fullName.hasSimpleName(rename)) {
                prop = prop.withSimpleName(rename);
                simpleName = rename;
            } else {
                simpleName = fullName.getSimpleName();
            }
            POJOPropertyBuilder old = propMap.get(simpleName);
            if (old == null) {
                propMap.put(simpleName, prop);
            } else {
                old.addAll(prop);
            }
            _updateCreatorProperty(prop, this._creatorProperties);
        }
    }

    protected void _renameWithWrappers(Map<String, POJOPropertyBuilder> props) {
        PropertyName wrapperName;
        Iterator<Map.Entry<String, POJOPropertyBuilder>> it = props.entrySet().iterator();
        LinkedList<POJOPropertyBuilder> renamed = null;
        while (it.hasNext()) {
            Map.Entry<String, POJOPropertyBuilder> entry = it.next();
            POJOPropertyBuilder prop = entry.getValue();
            AnnotatedMember member = prop.getPrimaryMember();
            if (member != null && (wrapperName = this._annotationIntrospector.findWrapperName(member)) != null && wrapperName.hasSimpleName() && !wrapperName.equals(prop.getFullName())) {
                if (renamed == null) {
                    renamed = new LinkedList<>();
                }
                renamed.add(prop.withName(wrapperName));
                it.remove();
            }
        }
        if (renamed != null) {
            Iterator i$ = renamed.iterator();
            while (i$.hasNext()) {
                POJOPropertyBuilder prop2 = i$.next();
                String name = prop2.getName();
                POJOPropertyBuilder old = props.get(name);
                if (old == null) {
                    props.put(name, prop2);
                } else {
                    old.addAll(prop2);
                }
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void _sortProperties(Map<String, POJOPropertyBuilder> props) {
        boolean sort;
        Map<? extends String, ? extends POJOPropertyBuilder> all;
        Collection<POJOPropertyBuilder> cr;
        AnnotationIntrospector intr = this._annotationIntrospector;
        Boolean alpha = intr.findSerializationSortAlphabetically(this._classDef);
        if (alpha == null) {
            sort = this._config.shouldSortPropertiesAlphabetically();
        } else {
            sort = alpha.booleanValue();
        }
        String[] propertyOrder = intr.findSerializationPropertyOrder(this._classDef);
        if (!sort && this._creatorProperties == null && propertyOrder == null) {
            return;
        }
        int size = props.size();
        if (sort) {
            all = new TreeMap<>();
        } else {
            all = new LinkedHashMap<>(size + size);
        }
        for (POJOPropertyBuilder prop : props.values()) {
            all.put(prop.getName(), prop);
        }
        Map<String, POJOPropertyBuilder> ordered = new LinkedHashMap<>(size + size);
        if (propertyOrder != null) {
            int len$ = propertyOrder.length;
            for (int i$ = 0; i$ < len$; i$++) {
                String name = propertyOrder[i$];
                POJOPropertyBuilder w = all.get(name);
                if (w == null) {
                    Iterator i$2 = props.values().iterator();
                    while (true) {
                        if (!i$2.hasNext()) {
                            break;
                        }
                        POJOPropertyBuilder prop2 = i$2.next();
                        if (name.equals(prop2.getInternalName())) {
                            w = prop2;
                            name = prop2.getName();
                            break;
                        }
                    }
                }
                if (w != null) {
                    ordered.put(name, w);
                }
            }
        }
        if (this._creatorProperties != null) {
            if (sort) {
                TreeMap<String, POJOPropertyBuilder> sorted = new TreeMap<>();
                Iterator i$3 = this._creatorProperties.iterator();
                while (i$3.hasNext()) {
                    POJOPropertyBuilder prop3 = i$3.next();
                    sorted.put(prop3.getName(), prop3);
                }
                cr = sorted.values();
            } else {
                cr = this._creatorProperties;
            }
            for (POJOPropertyBuilder prop4 : cr) {
                String name2 = prop4.getName();
                if (all.containsKey(name2)) {
                    ordered.put(name2, prop4);
                }
            }
        }
        ordered.putAll(all);
        props.clear();
        props.putAll(ordered);
    }

    protected void reportProblem(String msg, Object... args) {
        if (args.length > 0) {
            msg = String.format(msg, args);
        }
        throw new IllegalArgumentException("Problem with definition of " + this._classDef + ": " + msg);
    }

    protected POJOPropertyBuilder _property(Map<String, POJOPropertyBuilder> props, PropertyName name) {
        String simpleName = name.getSimpleName();
        POJOPropertyBuilder prop = props.get(simpleName);
        if (prop == null) {
            prop = new POJOPropertyBuilder(this._config, this._annotationIntrospector, this._forSerialization, name);
            props.put(simpleName, prop);
        }
        return prop;
    }

    protected POJOPropertyBuilder _property(Map<String, POJOPropertyBuilder> props, String implName) {
        POJOPropertyBuilder prop = props.get(implName);
        if (prop == null) {
            prop = new POJOPropertyBuilder(this._config, this._annotationIntrospector, this._forSerialization, PropertyName.construct(implName));
            props.put(implName, prop);
        }
        return prop;
    }

    private PropertyNamingStrategy _findNamingStrategy() {
        PropertyNamingStrategy pns;
        Object namingDef = this._annotationIntrospector.findNamingStrategy(this._classDef);
        if (namingDef == null) {
            return this._config.getPropertyNamingStrategy();
        }
        if (namingDef instanceof PropertyNamingStrategy) {
            return (PropertyNamingStrategy) namingDef;
        }
        if (!(namingDef instanceof Class)) {
            throw new IllegalStateException("AnnotationIntrospector returned PropertyNamingStrategy definition of type " + namingDef.getClass().getName() + "; expected type PropertyNamingStrategy or Class<PropertyNamingStrategy> instead");
        }
        Class<?> namingClass = (Class) namingDef;
        if (namingClass == PropertyNamingStrategy.class) {
            return null;
        }
        if (!PropertyNamingStrategy.class.isAssignableFrom(namingClass)) {
            throw new IllegalStateException("AnnotationIntrospector returned Class " + namingClass.getName() + "; expected Class<PropertyNamingStrategy>");
        }
        HandlerInstantiator hi = this._config.getHandlerInstantiator();
        if (hi != null && (pns = hi.namingStrategyInstance(this._config, this._classDef, namingClass)) != null) {
            return pns;
        }
        return (PropertyNamingStrategy) ClassUtil.createInstance(namingClass, this._config.canOverrideAccessModifiers());
    }

    protected void _updateCreatorProperty(POJOPropertyBuilder prop, List<POJOPropertyBuilder> creatorProperties) {
        if (creatorProperties != null) {
            String intName = prop.getInternalName();
            int len = creatorProperties.size();
            for (int i = 0; i < len; i++) {
                if (creatorProperties.get(i).getInternalName().equals(intName)) {
                    creatorProperties.set(i, prop);
                    return;
                }
            }
        }
    }
}