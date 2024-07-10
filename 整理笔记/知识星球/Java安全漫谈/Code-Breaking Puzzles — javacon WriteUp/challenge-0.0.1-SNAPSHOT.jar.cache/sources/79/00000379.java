package com.fasterxml.jackson.databind.deser.impl;

import ch.qos.logback.core.CoreConstants;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.std.StdValueInstantiator;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.introspect.AnnotationMap;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/impl/CreatorCollector.class */
public class CreatorCollector {
    protected static final int C_DEFAULT = 0;
    protected static final int C_STRING = 1;
    protected static final int C_INT = 2;
    protected static final int C_LONG = 3;
    protected static final int C_DOUBLE = 4;
    protected static final int C_BOOLEAN = 5;
    protected static final int C_DELEGATE = 6;
    protected static final int C_PROPS = 7;
    protected static final int C_ARRAY_DELEGATE = 8;
    protected static final String[] TYPE_DESCS = {"default", "from-String", "from-int", "from-long", "from-double", "from-boolean", "delegate", "property-based"};
    protected final BeanDescription _beanDesc;
    protected final boolean _canFixAccess;
    protected final boolean _forceAccess;
    protected final AnnotatedWithParams[] _creators = new AnnotatedWithParams[9];
    protected int _explicitCreators = 0;
    protected boolean _hasNonDefaultCreator = false;
    protected SettableBeanProperty[] _delegateArgs;
    protected SettableBeanProperty[] _arrayDelegateArgs;
    protected SettableBeanProperty[] _propertyBasedArgs;

    public CreatorCollector(BeanDescription beanDesc, MapperConfig<?> config) {
        this._beanDesc = beanDesc;
        this._canFixAccess = config.canOverrideAccessModifiers();
        this._forceAccess = config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS);
    }

    public ValueInstantiator constructValueInstantiator(DeserializationContext ctxt) throws JsonMappingException {
        DeserializationConfig config = ctxt.getConfig();
        JavaType delegateType = _computeDelegateType(ctxt, this._creators[6], this._delegateArgs);
        JavaType arrayDelegateType = _computeDelegateType(ctxt, this._creators[8], this._arrayDelegateArgs);
        JavaType type = this._beanDesc.getType();
        AnnotatedWithParams defaultCtor = StdTypeConstructor.tryToOptimize(this._creators[0]);
        StdValueInstantiator inst = new StdValueInstantiator(config, type);
        inst.configureFromObjectSettings(defaultCtor, this._creators[6], delegateType, this._delegateArgs, this._creators[7], this._propertyBasedArgs);
        inst.configureFromArraySettings(this._creators[8], arrayDelegateType, this._arrayDelegateArgs);
        inst.configureFromStringCreator(this._creators[1]);
        inst.configureFromIntCreator(this._creators[2]);
        inst.configureFromLongCreator(this._creators[3]);
        inst.configureFromDoubleCreator(this._creators[4]);
        inst.configureFromBooleanCreator(this._creators[5]);
        return inst;
    }

    public void setDefaultCreator(AnnotatedWithParams creator) {
        this._creators[0] = (AnnotatedWithParams) _fixAccess(creator);
    }

    public void addStringCreator(AnnotatedWithParams creator, boolean explicit) {
        verifyNonDup(creator, 1, explicit);
    }

    public void addIntCreator(AnnotatedWithParams creator, boolean explicit) {
        verifyNonDup(creator, 2, explicit);
    }

    public void addLongCreator(AnnotatedWithParams creator, boolean explicit) {
        verifyNonDup(creator, 3, explicit);
    }

    public void addDoubleCreator(AnnotatedWithParams creator, boolean explicit) {
        verifyNonDup(creator, 4, explicit);
    }

    public void addBooleanCreator(AnnotatedWithParams creator, boolean explicit) {
        verifyNonDup(creator, 5, explicit);
    }

    public void addDelegatingCreator(AnnotatedWithParams creator, boolean explicit, SettableBeanProperty[] injectables, int delegateeIndex) {
        if (creator.getParameterType(delegateeIndex).isCollectionLikeType()) {
            if (verifyNonDup(creator, 8, explicit)) {
                this._arrayDelegateArgs = injectables;
            }
        } else if (verifyNonDup(creator, 6, explicit)) {
            this._delegateArgs = injectables;
        }
    }

    public void addPropertyCreator(AnnotatedWithParams creator, boolean explicit, SettableBeanProperty[] properties) {
        Integer old;
        if (verifyNonDup(creator, 7, explicit)) {
            if (properties.length > 1) {
                HashMap<String, Integer> names = new HashMap<>();
                int len = properties.length;
                for (int i = 0; i < len; i++) {
                    String name = properties[i].getName();
                    if ((!name.isEmpty() || properties[i].getInjectableValueId() == null) && (old = names.put(name, Integer.valueOf(i))) != null) {
                        throw new IllegalArgumentException(String.format("Duplicate creator property \"%s\" (index %s vs %d) for type %s ", name, old, Integer.valueOf(i), ClassUtil.nameOf(this._beanDesc.getBeanClass())));
                    }
                }
            }
            this._propertyBasedArgs = properties;
        }
    }

    public boolean hasDefaultCreator() {
        return this._creators[0] != null;
    }

    public boolean hasDelegatingCreator() {
        return this._creators[6] != null;
    }

    public boolean hasPropertyBasedCreator() {
        return this._creators[7] != null;
    }

    private JavaType _computeDelegateType(DeserializationContext ctxt, AnnotatedWithParams creator, SettableBeanProperty[] delegateArgs) throws JsonMappingException {
        if (!this._hasNonDefaultCreator || creator == null) {
            return null;
        }
        int ix = 0;
        if (delegateArgs != null) {
            int i = 0;
            int len = delegateArgs.length;
            while (true) {
                if (i >= len) {
                    break;
                } else if (delegateArgs[i] != null) {
                    i++;
                } else {
                    ix = i;
                    break;
                }
            }
        }
        DeserializationConfig config = ctxt.getConfig();
        JavaType baseType = creator.getParameterType(ix);
        AnnotationIntrospector intr = config.getAnnotationIntrospector();
        if (intr != null) {
            AnnotatedParameter delegate = creator.getParameter(ix);
            Object deserDef = intr.findDeserializer(delegate);
            if (deserDef != null) {
                JsonDeserializer<Object> deser = ctxt.deserializerInstance(delegate, deserDef);
                baseType = baseType.withValueHandler(deser);
            } else {
                baseType = intr.refineDeserializationType(config, delegate, baseType);
            }
        }
        return baseType;
    }

    private <T extends AnnotatedMember> T _fixAccess(T member) {
        if (member != null && this._canFixAccess) {
            ClassUtil.checkAndFixAccess((Member) member.getAnnotated(), this._forceAccess);
        }
        return member;
    }

    protected boolean verifyNonDup(AnnotatedWithParams newOne, int typeIndex, boolean explicit) {
        boolean verify;
        int mask = 1 << typeIndex;
        this._hasNonDefaultCreator = true;
        AnnotatedWithParams oldOne = this._creators[typeIndex];
        if (oldOne != null) {
            if ((this._explicitCreators & mask) != 0) {
                if (!explicit) {
                    return false;
                }
                verify = true;
            } else {
                verify = !explicit;
            }
            if (verify && oldOne.getClass() == newOne.getClass()) {
                Class<?> oldType = oldOne.getRawParameterType(0);
                Class<?> newType = newOne.getRawParameterType(0);
                if (oldType == newType) {
                    if (_isEnumValueOf(newOne)) {
                        return false;
                    }
                    if (!_isEnumValueOf(oldOne)) {
                        Object[] objArr = new Object[4];
                        objArr[0] = TYPE_DESCS[typeIndex];
                        objArr[1] = explicit ? "explicitly marked" : "implicitly discovered";
                        objArr[2] = oldOne;
                        objArr[3] = newOne;
                        throw new IllegalArgumentException(String.format("Conflicting %s creators: already had %s creator %s, encountered another: %s", objArr));
                    }
                } else if (newType.isAssignableFrom(oldType)) {
                    return false;
                }
            }
        }
        if (explicit) {
            this._explicitCreators |= mask;
        }
        this._creators[typeIndex] = (AnnotatedWithParams) _fixAccess(newOne);
        return true;
    }

    protected boolean _isEnumValueOf(AnnotatedWithParams creator) {
        return creator.getDeclaringClass().isEnum() && CoreConstants.VALUE_OF.equals(creator.getName());
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/impl/CreatorCollector$StdTypeConstructor.class */
    protected static final class StdTypeConstructor extends AnnotatedWithParams implements Serializable {
        private static final long serialVersionUID = 1;
        public static final int TYPE_ARRAY_LIST = 1;
        public static final int TYPE_HASH_MAP = 2;
        public static final int TYPE_LINKED_HASH_MAP = 3;
        private final AnnotatedWithParams _base;
        private final int _type;

        public StdTypeConstructor(AnnotatedWithParams base, int t) {
            super(base, null);
            this._base = base;
            this._type = t;
        }

        public static AnnotatedWithParams tryToOptimize(AnnotatedWithParams src) {
            if (src != null) {
                Class<?> rawType = src.getDeclaringClass();
                if (rawType == List.class || rawType == ArrayList.class) {
                    return new StdTypeConstructor(src, 1);
                }
                if (rawType == LinkedHashMap.class) {
                    return new StdTypeConstructor(src, 3);
                }
                if (rawType == HashMap.class) {
                    return new StdTypeConstructor(src, 2);
                }
            }
            return src;
        }

        protected final Object _construct() {
            switch (this._type) {
                case 1:
                    return new ArrayList();
                case 2:
                    return new HashMap();
                case 3:
                    return new LinkedHashMap();
                default:
                    throw new IllegalStateException("Unknown type " + this._type);
            }
        }

        @Override // com.fasterxml.jackson.databind.introspect.AnnotatedWithParams
        public int getParameterCount() {
            return this._base.getParameterCount();
        }

        @Override // com.fasterxml.jackson.databind.introspect.AnnotatedWithParams
        public Class<?> getRawParameterType(int index) {
            return this._base.getRawParameterType(index);
        }

        @Override // com.fasterxml.jackson.databind.introspect.AnnotatedWithParams
        public JavaType getParameterType(int index) {
            return this._base.getParameterType(index);
        }

        @Override // com.fasterxml.jackson.databind.introspect.AnnotatedWithParams
        @Deprecated
        public Type getGenericParameterType(int index) {
            return this._base.getGenericParameterType(index);
        }

        @Override // com.fasterxml.jackson.databind.introspect.AnnotatedWithParams
        public Object call() throws Exception {
            return _construct();
        }

        @Override // com.fasterxml.jackson.databind.introspect.AnnotatedWithParams
        public Object call(Object[] args) throws Exception {
            return _construct();
        }

        @Override // com.fasterxml.jackson.databind.introspect.AnnotatedWithParams
        public Object call1(Object arg) throws Exception {
            return _construct();
        }

        @Override // com.fasterxml.jackson.databind.introspect.AnnotatedMember
        public Class<?> getDeclaringClass() {
            return this._base.getDeclaringClass();
        }

        @Override // com.fasterxml.jackson.databind.introspect.AnnotatedMember
        public Member getMember() {
            return this._base.getMember();
        }

        @Override // com.fasterxml.jackson.databind.introspect.AnnotatedMember
        public void setValue(Object pojo, Object value) throws UnsupportedOperationException, IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        @Override // com.fasterxml.jackson.databind.introspect.AnnotatedMember
        public Object getValue(Object pojo) throws UnsupportedOperationException, IllegalArgumentException {
            throw new UnsupportedOperationException();
        }

        @Override // com.fasterxml.jackson.databind.introspect.AnnotatedMember
        public Annotated withAnnotations(AnnotationMap fallback) {
            throw new UnsupportedOperationException();
        }

        @Override // com.fasterxml.jackson.databind.introspect.Annotated
        public AnnotatedElement getAnnotated() {
            return this._base.getAnnotated();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.fasterxml.jackson.databind.introspect.Annotated
        public int getModifiers() {
            return this._base.getMember().getModifiers();
        }

        @Override // com.fasterxml.jackson.databind.introspect.Annotated
        public String getName() {
            return this._base.getName();
        }

        @Override // com.fasterxml.jackson.databind.introspect.Annotated
        public JavaType getType() {
            return this._base.getType();
        }

        @Override // com.fasterxml.jackson.databind.introspect.Annotated
        public Class<?> getRawType() {
            return this._base.getRawType();
        }

        @Override // com.fasterxml.jackson.databind.introspect.Annotated
        public boolean equals(Object o) {
            return o == this;
        }

        @Override // com.fasterxml.jackson.databind.introspect.Annotated
        public int hashCode() {
            return this._base.hashCode();
        }

        @Override // com.fasterxml.jackson.databind.introspect.Annotated
        public String toString() {
            return this._base.toString();
        }
    }
}