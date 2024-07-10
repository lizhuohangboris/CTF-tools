package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.lang.annotation.Annotation;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/CreatorProperty.class */
public class CreatorProperty extends SettableBeanProperty {
    private static final long serialVersionUID = 1;
    protected final AnnotatedParameter _annotated;
    protected final Object _injectableValueId;
    protected SettableBeanProperty _fallbackSetter;
    protected final int _creatorIndex;
    protected boolean _ignorable;

    public CreatorProperty(PropertyName name, JavaType type, PropertyName wrapperName, TypeDeserializer typeDeser, Annotations contextAnnotations, AnnotatedParameter param, int index, Object injectableValueId, PropertyMetadata metadata) {
        super(name, type, wrapperName, typeDeser, contextAnnotations, metadata);
        this._annotated = param;
        this._creatorIndex = index;
        this._injectableValueId = injectableValueId;
        this._fallbackSetter = null;
    }

    protected CreatorProperty(CreatorProperty src, PropertyName newName) {
        super(src, newName);
        this._annotated = src._annotated;
        this._injectableValueId = src._injectableValueId;
        this._fallbackSetter = src._fallbackSetter;
        this._creatorIndex = src._creatorIndex;
        this._ignorable = src._ignorable;
    }

    protected CreatorProperty(CreatorProperty src, JsonDeserializer<?> deser, NullValueProvider nva) {
        super(src, deser, nva);
        this._annotated = src._annotated;
        this._injectableValueId = src._injectableValueId;
        this._fallbackSetter = src._fallbackSetter;
        this._creatorIndex = src._creatorIndex;
        this._ignorable = src._ignorable;
    }

    @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
    public SettableBeanProperty withName(PropertyName newName) {
        return new CreatorProperty(this, newName);
    }

    @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
    public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser) {
        if (this._valueDeserializer == deser) {
            return this;
        }
        return new CreatorProperty(this, deser, this._nullProvider);
    }

    @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
    public SettableBeanProperty withNullProvider(NullValueProvider nva) {
        return new CreatorProperty(this, this._valueDeserializer, nva);
    }

    @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
    public void fixAccess(DeserializationConfig config) {
        if (this._fallbackSetter != null) {
            this._fallbackSetter.fixAccess(config);
        }
    }

    public void setFallbackSetter(SettableBeanProperty fallbackSetter) {
        this._fallbackSetter = fallbackSetter;
    }

    @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
    public void markAsIgnorable() {
        this._ignorable = true;
    }

    @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
    public boolean isIgnorable() {
        return this._ignorable;
    }

    public Object findInjectableValue(DeserializationContext context, Object beanInstance) throws JsonMappingException {
        if (this._injectableValueId == null) {
            context.reportBadDefinition(ClassUtil.classOf(beanInstance), String.format("Property '%s' (type %s) has no injectable value id configured", getName(), getClass().getName()));
        }
        return context.findInjectableValue(this._injectableValueId, this, beanInstance);
    }

    public void inject(DeserializationContext context, Object beanInstance) throws IOException {
        set(beanInstance, findInjectableValue(context, beanInstance));
    }

    @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty, com.fasterxml.jackson.databind.BeanProperty
    public <A extends Annotation> A getAnnotation(Class<A> acls) {
        if (this._annotated == null) {
            return null;
        }
        return (A) this._annotated.getAnnotation(acls);
    }

    @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty, com.fasterxml.jackson.databind.BeanProperty
    public AnnotatedMember getMember() {
        return this._annotated;
    }

    @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
    public int getCreatorIndex() {
        return this._creatorIndex;
    }

    @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
    public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
        _verifySetter();
        this._fallbackSetter.set(instance, deserialize(p, ctxt));
    }

    @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
    public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
        _verifySetter();
        return this._fallbackSetter.setAndReturn(instance, deserialize(p, ctxt));
    }

    @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
    public void set(Object instance, Object value) throws IOException {
        _verifySetter();
        this._fallbackSetter.set(instance, value);
    }

    @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
    public Object setAndReturn(Object instance, Object value) throws IOException {
        _verifySetter();
        return this._fallbackSetter.setAndReturn(instance, value);
    }

    @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
    public Object getInjectableValueId() {
        return this._injectableValueId;
    }

    @Override // com.fasterxml.jackson.databind.deser.SettableBeanProperty
    public String toString() {
        return "[creator property, name '" + getName() + "'; inject id '" + this._injectableValueId + "']";
    }

    private final void _verifySetter() throws IOException {
        if (this._fallbackSetter == null) {
            _reportMissingSetter(null, null);
        }
    }

    private void _reportMissingSetter(JsonParser p, DeserializationContext ctxt) throws IOException {
        String msg = "No fallback setter/field defined for creator property '" + getName() + "'";
        if (ctxt != null) {
            ctxt.reportBadDefinition(getType(), msg);
            return;
        }
        throw InvalidDefinitionException.from(p, msg, getType());
    }
}