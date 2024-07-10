package com.fasterxml.jackson.databind.introspect;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/introspect/ConcreteBeanPropertyBase.class */
public abstract class ConcreteBeanPropertyBase implements BeanProperty, Serializable {
    private static final long serialVersionUID = 1;
    protected final PropertyMetadata _metadata;
    protected transient JsonFormat.Value _propertyFormat;
    protected transient List<PropertyName> _aliases;

    public ConcreteBeanPropertyBase(PropertyMetadata md) {
        this._metadata = md == null ? PropertyMetadata.STD_REQUIRED_OR_OPTIONAL : md;
    }

    public ConcreteBeanPropertyBase(ConcreteBeanPropertyBase src) {
        this._metadata = src._metadata;
        this._propertyFormat = src._propertyFormat;
    }

    @Override // com.fasterxml.jackson.databind.BeanProperty
    public boolean isRequired() {
        return this._metadata.isRequired();
    }

    @Override // com.fasterxml.jackson.databind.BeanProperty
    public PropertyMetadata getMetadata() {
        return this._metadata;
    }

    @Override // com.fasterxml.jackson.databind.BeanProperty
    public boolean isVirtual() {
        return false;
    }

    @Override // com.fasterxml.jackson.databind.BeanProperty
    @Deprecated
    public final JsonFormat.Value findFormatOverrides(AnnotationIntrospector intr) {
        AnnotatedMember member;
        JsonFormat.Value f = null;
        if (intr != null && (member = getMember()) != null) {
            f = intr.findFormat(member);
        }
        if (f == null) {
            f = EMPTY_FORMAT;
        }
        return f;
    }

    @Override // com.fasterxml.jackson.databind.BeanProperty
    public JsonFormat.Value findPropertyFormat(MapperConfig<?> config, Class<?> baseType) {
        AnnotatedMember member;
        JsonFormat.Value v = this._propertyFormat;
        if (v == null) {
            JsonFormat.Value v1 = config.getDefaultPropertyFormat(baseType);
            JsonFormat.Value v2 = null;
            AnnotationIntrospector intr = config.getAnnotationIntrospector();
            if (intr != null && (member = getMember()) != null) {
                v2 = intr.findFormat(member);
            }
            if (v1 == null) {
                v = v2 == null ? EMPTY_FORMAT : v2;
            } else {
                v = v2 == null ? v1 : v1.withOverrides(v2);
            }
            this._propertyFormat = v;
        }
        return v;
    }

    @Override // com.fasterxml.jackson.databind.BeanProperty
    public JsonInclude.Value findPropertyInclusion(MapperConfig<?> config, Class<?> baseType) {
        AnnotationIntrospector intr = config.getAnnotationIntrospector();
        AnnotatedMember member = getMember();
        if (member == null) {
            JsonInclude.Value def = config.getDefaultPropertyInclusion(baseType);
            return def;
        }
        JsonInclude.Value v0 = config.getDefaultInclusion(baseType, member.getRawType());
        if (intr == null) {
            return v0;
        }
        JsonInclude.Value v = intr.findPropertyInclusion(member);
        if (v0 == null) {
            return v;
        }
        return v0.withOverrides(v);
    }

    @Override // com.fasterxml.jackson.databind.BeanProperty
    public List<PropertyName> findAliases(MapperConfig<?> config) {
        List<PropertyName> aliases = this._aliases;
        if (aliases == null) {
            AnnotationIntrospector intr = config.getAnnotationIntrospector();
            if (intr != null) {
                aliases = intr.findPropertyAliases(getMember());
            }
            if (aliases == null) {
                aliases = Collections.emptyList();
            }
            this._aliases = aliases;
        }
        return aliases;
    }
}