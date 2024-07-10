package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.impl.BeanAsArrayBuilderDeserializer;
import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/BuilderBasedDeserializer.class */
public class BuilderBasedDeserializer extends BeanDeserializerBase {
    private static final long serialVersionUID = 1;
    protected final AnnotatedMethod _buildMethod;
    protected final JavaType _targetType;

    public BuilderBasedDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, JavaType targetType, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, Set<String> ignorableProps, boolean ignoreAllUnknown, boolean hasViews) {
        super(builder, beanDesc, properties, backRefs, ignorableProps, ignoreAllUnknown, hasViews);
        this._targetType = targetType;
        this._buildMethod = builder.getBuildMethod();
        if (this._objectIdReader != null) {
            throw new IllegalArgumentException("Cannot use Object Id with Builder-based deserialization (type " + beanDesc.getType() + ")");
        }
    }

    @Deprecated
    public BuilderBasedDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, Set<String> ignorableProps, boolean ignoreAllUnknown, boolean hasViews) {
        this(builder, beanDesc, beanDesc.getType(), properties, backRefs, ignorableProps, ignoreAllUnknown, hasViews);
    }

    protected BuilderBasedDeserializer(BuilderBasedDeserializer src) {
        this(src, src._ignoreAllUnknown);
    }

    protected BuilderBasedDeserializer(BuilderBasedDeserializer src, boolean ignoreAllUnknown) {
        super(src, ignoreAllUnknown);
        this._buildMethod = src._buildMethod;
        this._targetType = src._targetType;
    }

    protected BuilderBasedDeserializer(BuilderBasedDeserializer src, NameTransformer unwrapper) {
        super(src, unwrapper);
        this._buildMethod = src._buildMethod;
        this._targetType = src._targetType;
    }

    public BuilderBasedDeserializer(BuilderBasedDeserializer src, ObjectIdReader oir) {
        super(src, oir);
        this._buildMethod = src._buildMethod;
        this._targetType = src._targetType;
    }

    public BuilderBasedDeserializer(BuilderBasedDeserializer src, Set<String> ignorableProps) {
        super(src, ignorableProps);
        this._buildMethod = src._buildMethod;
        this._targetType = src._targetType;
    }

    public BuilderBasedDeserializer(BuilderBasedDeserializer src, BeanPropertyMap props) {
        super(src, props);
        this._buildMethod = src._buildMethod;
        this._targetType = src._targetType;
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase, com.fasterxml.jackson.databind.JsonDeserializer
    public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper) {
        return new BuilderBasedDeserializer(this, unwrapper);
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    public BeanDeserializerBase withObjectIdReader(ObjectIdReader oir) {
        return new BuilderBasedDeserializer(this, oir);
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    public BeanDeserializerBase withIgnorableProperties(Set<String> ignorableProps) {
        return new BuilderBasedDeserializer(this, ignorableProps);
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    public BeanDeserializerBase withBeanProperties(BeanPropertyMap props) {
        return new BuilderBasedDeserializer(this, props);
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    protected BeanDeserializerBase asArrayDeserializer() {
        SettableBeanProperty[] props = this._beanProperties.getPropertiesInInsertionOrder();
        return new BeanAsArrayBuilderDeserializer(this, this._targetType, props, this._buildMethod);
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase, com.fasterxml.jackson.databind.JsonDeserializer
    public Boolean supportsUpdate(DeserializationConfig config) {
        return Boolean.FALSE;
    }

    protected Object finishBuild(DeserializationContext ctxt, Object builder) throws IOException {
        if (null == this._buildMethod) {
            return builder;
        }
        try {
            return this._buildMethod.getMember().invoke(builder, null);
        } catch (Exception e) {
            return wrapInstantiationProblem(e, ctxt);
        }
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.isExpectedStartObjectToken()) {
            JsonToken t = p.nextToken();
            if (this._vanillaProcessing) {
                return finishBuild(ctxt, vanillaDeserialize(p, ctxt, t));
            }
            Object builder = deserializeFromObject(p, ctxt);
            return finishBuild(ctxt, builder);
        }
        switch (p.getCurrentTokenId()) {
            case 2:
            case 5:
                return finishBuild(ctxt, deserializeFromObject(p, ctxt));
            case 3:
                return finishBuild(ctxt, deserializeFromArray(p, ctxt));
            case 4:
            case 11:
            default:
                return ctxt.handleUnexpectedToken(handledType(), p);
            case 6:
                return finishBuild(ctxt, deserializeFromString(p, ctxt));
            case 7:
                return finishBuild(ctxt, deserializeFromNumber(p, ctxt));
            case 8:
                return finishBuild(ctxt, deserializeFromDouble(p, ctxt));
            case 9:
            case 10:
                return finishBuild(ctxt, deserializeFromBoolean(p, ctxt));
            case 12:
                return p.getEmbeddedObject();
        }
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public Object deserialize(JsonParser p, DeserializationContext ctxt, Object value) throws IOException {
        JavaType valueType = this._targetType;
        Class<?> builderRawType = handledType();
        Class<?> instRawType = value.getClass();
        if (builderRawType.isAssignableFrom(instRawType)) {
            return ctxt.reportBadDefinition(valueType, String.format("Deserialization of %s by passing existing Builder (%s) instance not supported", valueType, builderRawType.getName()));
        }
        return ctxt.reportBadDefinition(valueType, String.format("Deserialization of %s by passing existing instance (of %s) not supported", valueType, instRawType.getName()));
    }

    private final Object vanillaDeserialize(JsonParser p, DeserializationContext ctxt, JsonToken t) throws IOException {
        Object bean = this._valueInstantiator.createUsingDefault(ctxt);
        while (p.getCurrentToken() == JsonToken.FIELD_NAME) {
            String propName = p.getCurrentName();
            p.nextToken();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                try {
                    bean = prop.deserializeSetAndReturn(p, ctxt, bean);
                } catch (Exception e) {
                    wrapAndThrow(e, bean, propName, ctxt);
                }
            } else {
                handleUnknownVanilla(p, ctxt, bean, propName);
            }
            p.nextToken();
        }
        return bean;
    }

    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
        Class<?> view;
        if (this._nonStandardCreation) {
            if (this._unwrappedPropertyHandler != null) {
                return deserializeWithUnwrapped(p, ctxt);
            }
            if (this._externalTypeIdHandler != null) {
                return deserializeWithExternalTypeId(p, ctxt);
            }
            return deserializeFromObjectUsingNonDefault(p, ctxt);
        }
        Object bean = this._valueInstantiator.createUsingDefault(ctxt);
        if (this._injectables != null) {
            injectValues(ctxt, bean);
        }
        if (this._needViewProcesing && (view = ctxt.getActiveView()) != null) {
            return deserializeWithView(p, ctxt, bean, view);
        }
        while (p.getCurrentToken() == JsonToken.FIELD_NAME) {
            String propName = p.getCurrentName();
            p.nextToken();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                try {
                    bean = prop.deserializeSetAndReturn(p, ctxt, bean);
                } catch (Exception e) {
                    wrapAndThrow(e, bean, propName, ctxt);
                }
            } else {
                handleUnknownVanilla(p, ctxt, bean, propName);
            }
            p.nextToken();
        }
        return bean;
    }

    /* JADX WARN: Incorrect condition in loop: B:8:0x0030 */
    @Override // com.fasterxml.jackson.databind.deser.BeanDeserializerBase
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected java.lang.Object _deserializeUsingPropertyBased(com.fasterxml.jackson.core.JsonParser r8, com.fasterxml.jackson.databind.DeserializationContext r9) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 421
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.deser.BuilderBasedDeserializer._deserializeUsingPropertyBased(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext):java.lang.Object");
    }

    protected final Object _deserialize(JsonParser p, DeserializationContext ctxt, Object builder) throws IOException {
        Class<?> view;
        if (this._injectables != null) {
            injectValues(ctxt, builder);
        }
        if (this._unwrappedPropertyHandler != null) {
            if (p.hasToken(JsonToken.START_OBJECT)) {
                p.nextToken();
            }
            TokenBuffer tokens = new TokenBuffer(p, ctxt);
            tokens.writeStartObject();
            return deserializeWithUnwrapped(p, ctxt, builder, tokens);
        } else if (this._externalTypeIdHandler != null) {
            return deserializeWithExternalTypeId(p, ctxt, builder);
        } else {
            if (this._needViewProcesing && (view = ctxt.getActiveView()) != null) {
                return deserializeWithView(p, ctxt, builder, view);
            }
            JsonToken t = p.getCurrentToken();
            if (t == JsonToken.START_OBJECT) {
                t = p.nextToken();
            }
            while (t == JsonToken.FIELD_NAME) {
                String propName = p.getCurrentName();
                p.nextToken();
                SettableBeanProperty prop = this._beanProperties.find(propName);
                if (prop != null) {
                    try {
                        builder = prop.deserializeSetAndReturn(p, ctxt, builder);
                    } catch (Exception e) {
                        wrapAndThrow(e, builder, propName, ctxt);
                    }
                } else {
                    handleUnknownVanilla(p, ctxt, handledType(), propName);
                }
                t = p.nextToken();
            }
            return builder;
        }
    }

    /* JADX WARN: Incorrect condition in loop: B:4:0x000b */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected final java.lang.Object deserializeWithView(com.fasterxml.jackson.core.JsonParser r7, com.fasterxml.jackson.databind.DeserializationContext r8, java.lang.Object r9, java.lang.Class<?> r10) throws java.io.IOException {
        /*
            r6 = this;
            r0 = r7
            com.fasterxml.jackson.core.JsonToken r0 = r0.getCurrentToken()
            r11 = r0
        L6:
            r0 = r11
            com.fasterxml.jackson.core.JsonToken r1 = com.fasterxml.jackson.core.JsonToken.FIELD_NAME
            if (r0 != r1) goto L68
            r0 = r7
            java.lang.String r0 = r0.getCurrentName()
            r12 = r0
            r0 = r7
            com.fasterxml.jackson.core.JsonToken r0 = r0.nextToken()
            r0 = r6
            com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap r0 = r0._beanProperties
            r1 = r12
            com.fasterxml.jackson.databind.deser.SettableBeanProperty r0 = r0.find(r1)
            r13 = r0
            r0 = r13
            if (r0 == 0) goto L56
            r0 = r13
            r1 = r10
            boolean r0 = r0.visibleInView(r1)
            if (r0 != 0) goto L3b
            r0 = r7
            com.fasterxml.jackson.core.JsonParser r0 = r0.skipChildren()
            goto L5f
        L3b:
            r0 = r13
            r1 = r7
            r2 = r8
            r3 = r9
            java.lang.Object r0 = r0.deserializeSetAndReturn(r1, r2, r3)     // Catch: java.lang.Exception -> L47
            r9 = r0
            goto L5f
        L47:
            r14 = move-exception
            r0 = r6
            r1 = r14
            r2 = r9
            r3 = r12
            r4 = r8
            r0.wrapAndThrow(r1, r2, r3, r4)
            goto L5f
        L56:
            r0 = r6
            r1 = r7
            r2 = r8
            r3 = r9
            r4 = r12
            r0.handleUnknownVanilla(r1, r2, r3, r4)
        L5f:
            r0 = r7
            com.fasterxml.jackson.core.JsonToken r0 = r0.nextToken()
            r11 = r0
            goto L6
        L68:
            r0 = r9
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.deser.BuilderBasedDeserializer.deserializeWithView(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext, java.lang.Object, java.lang.Class):java.lang.Object");
    }

    protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (this._delegateDeserializer != null) {
            return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
        }
        if (this._propertyBasedCreator != null) {
            return deserializeUsingPropertyBasedWithUnwrapped(p, ctxt);
        }
        TokenBuffer tokens = new TokenBuffer(p, ctxt);
        tokens.writeStartObject();
        Object bean = this._valueInstantiator.createUsingDefault(ctxt);
        if (this._injectables != null) {
            injectValues(ctxt, bean);
        }
        Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
        while (p.getCurrentToken() == JsonToken.FIELD_NAME) {
            String propName = p.getCurrentName();
            p.nextToken();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                if (activeView != null && !prop.visibleInView(activeView)) {
                    p.skipChildren();
                } else {
                    try {
                        bean = prop.deserializeSetAndReturn(p, ctxt, bean);
                    } catch (Exception e) {
                        wrapAndThrow(e, bean, propName, ctxt);
                    }
                }
            } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                handleIgnoredProperty(p, ctxt, bean, propName);
            } else {
                tokens.writeFieldName(propName);
                tokens.copyCurrentStructure(p);
                if (this._anySetter != null) {
                    try {
                        this._anySetter.deserializeAndSet(p, ctxt, bean, propName);
                    } catch (Exception e2) {
                        wrapAndThrow(e2, bean, propName, ctxt);
                    }
                }
            }
            p.nextToken();
        }
        tokens.writeEndObject();
        return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
    }

    /* JADX WARN: Incorrect condition in loop: B:4:0x002f */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected java.lang.Object deserializeUsingPropertyBasedWithUnwrapped(com.fasterxml.jackson.core.JsonParser r8, com.fasterxml.jackson.databind.DeserializationContext r9) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 344
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.deser.BuilderBasedDeserializer.deserializeUsingPropertyBasedWithUnwrapped(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext):java.lang.Object");
    }

    /* JADX WARN: Incorrect condition in loop: B:8:0x001c */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected java.lang.Object deserializeWithUnwrapped(com.fasterxml.jackson.core.JsonParser r7, com.fasterxml.jackson.databind.DeserializationContext r8, java.lang.Object r9, com.fasterxml.jackson.databind.util.TokenBuffer r10) throws java.io.IOException {
        /*
            r6 = this;
            r0 = r6
            boolean r0 = r0._needViewProcesing
            if (r0 == 0) goto Le
            r0 = r8
            java.lang.Class r0 = r0.getActiveView()
            goto Lf
        Le:
            r0 = 0
        Lf:
            r11 = r0
            r0 = r7
            com.fasterxml.jackson.core.JsonToken r0 = r0.getCurrentToken()
            r12 = r0
        L17:
            r0 = r12
            com.fasterxml.jackson.core.JsonToken r1 = com.fasterxml.jackson.core.JsonToken.FIELD_NAME
            if (r0 != r1) goto Lb6
            r0 = r7
            java.lang.String r0 = r0.getCurrentName()
            r13 = r0
            r0 = r6
            com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap r0 = r0._beanProperties
            r1 = r13
            com.fasterxml.jackson.databind.deser.SettableBeanProperty r0 = r0.find(r1)
            r14 = r0
            r0 = r7
            com.fasterxml.jackson.core.JsonToken r0 = r0.nextToken()
            r0 = r14
            if (r0 == 0) goto L6c
            r0 = r11
            if (r0 == 0) goto L51
            r0 = r14
            r1 = r11
            boolean r0 = r0.visibleInView(r1)
            if (r0 != 0) goto L51
            r0 = r7
            com.fasterxml.jackson.core.JsonParser r0 = r0.skipChildren()
            goto Lad
        L51:
            r0 = r14
            r1 = r7
            r2 = r8
            r3 = r9
            java.lang.Object r0 = r0.deserializeSetAndReturn(r1, r2, r3)     // Catch: java.lang.Exception -> L5d
            r9 = r0
            goto Lad
        L5d:
            r15 = move-exception
            r0 = r6
            r1 = r15
            r2 = r9
            r3 = r13
            r4 = r8
            r0.wrapAndThrow(r1, r2, r3, r4)
            goto Lad
        L6c:
            r0 = r6
            java.util.Set<java.lang.String> r0 = r0._ignorableProps
            if (r0 == 0) goto L8d
            r0 = r6
            java.util.Set<java.lang.String> r0 = r0._ignorableProps
            r1 = r13
            boolean r0 = r0.contains(r1)
            if (r0 == 0) goto L8d
            r0 = r6
            r1 = r7
            r2 = r8
            r3 = r9
            r4 = r13
            r0.handleIgnoredProperty(r1, r2, r3, r4)
            goto Lad
        L8d:
            r0 = r10
            r1 = r13
            r0.writeFieldName(r1)
            r0 = r10
            r1 = r7
            r0.copyCurrentStructure(r1)
            r0 = r6
            com.fasterxml.jackson.databind.deser.SettableAnyProperty r0 = r0._anySetter
            if (r0 == 0) goto Lad
            r0 = r6
            com.fasterxml.jackson.databind.deser.SettableAnyProperty r0 = r0._anySetter
            r1 = r7
            r2 = r8
            r3 = r9
            r4 = r13
            r0.deserializeAndSet(r1, r2, r3, r4)
        Lad:
            r0 = r7
            com.fasterxml.jackson.core.JsonToken r0 = r0.nextToken()
            r12 = r0
            goto L17
        Lb6:
            r0 = r10
            r0.writeEndObject()
            r0 = r6
            com.fasterxml.jackson.databind.deser.impl.UnwrappedPropertyHandler r0 = r0._unwrappedPropertyHandler
            r1 = r7
            r2 = r8
            r3 = r9
            r4 = r10
            java.lang.Object r0 = r0.processUnwrapped(r1, r2, r3, r4)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.deser.BuilderBasedDeserializer.deserializeWithUnwrapped(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext, java.lang.Object, com.fasterxml.jackson.databind.util.TokenBuffer):java.lang.Object");
    }

    protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (this._propertyBasedCreator != null) {
            return deserializeUsingPropertyBasedWithExternalTypeId(p, ctxt);
        }
        return deserializeWithExternalTypeId(p, ctxt, this._valueInstantiator.createUsingDefault(ctxt));
    }

    /* JADX WARN: Incorrect condition in loop: B:8:0x0025 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected java.lang.Object deserializeWithExternalTypeId(com.fasterxml.jackson.core.JsonParser r7, com.fasterxml.jackson.databind.DeserializationContext r8, java.lang.Object r9) throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 250
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.fasterxml.jackson.databind.deser.BuilderBasedDeserializer.deserializeWithExternalTypeId(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext, java.lang.Object):java.lang.Object");
    }

    protected Object deserializeUsingPropertyBasedWithExternalTypeId(JsonParser p, DeserializationContext ctxt) throws IOException {
        JavaType t = this._targetType;
        return ctxt.reportBadDefinition(t, String.format("Deserialization (of %s) with Builder, External type id, @JsonCreator not yet implemented", t));
    }
}