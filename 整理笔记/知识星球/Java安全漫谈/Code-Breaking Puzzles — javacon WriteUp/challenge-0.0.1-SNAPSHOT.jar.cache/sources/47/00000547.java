package com.fasterxml.jackson.databind.util;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/util/EnumResolver.class */
public class EnumResolver implements Serializable {
    private static final long serialVersionUID = 1;
    protected final Class<Enum<?>> _enumClass;
    protected final Enum<?>[] _enums;
    protected final HashMap<String, Enum<?>> _enumsById;
    protected final Enum<?> _defaultValue;

    protected EnumResolver(Class<Enum<?>> enumClass, Enum<?>[] enums, HashMap<String, Enum<?>> map, Enum<?> defaultValue) {
        this._enumClass = enumClass;
        this._enums = enums;
        this._enumsById = map;
        this._defaultValue = defaultValue;
    }

    public static EnumResolver constructFor(Class<Enum<?>> enumCls, AnnotationIntrospector ai) {
        Enum<?>[] enumValues = enumCls.getEnumConstants();
        if (enumValues == null) {
            throw new IllegalArgumentException("No enum constants for class " + enumCls.getName());
        }
        String[] names = ai.findEnumValues(enumCls, enumValues, new String[enumValues.length]);
        HashMap<String, Enum<?>> map = new HashMap<>();
        int len = enumValues.length;
        for (int i = 0; i < len; i++) {
            String name = names[i];
            if (name == null) {
                name = enumValues[i].name();
            }
            map.put(name, enumValues[i]);
        }
        Enum<?> defaultEnum = ai.findDefaultEnumValue(enumCls);
        return new EnumResolver(enumCls, enumValues, map, defaultEnum);
    }

    @Deprecated
    public static EnumResolver constructUsingToString(Class<Enum<?>> enumCls) {
        return constructUsingToString(enumCls, null);
    }

    public static EnumResolver constructUsingToString(Class<Enum<?>> enumCls, AnnotationIntrospector ai) {
        Enum<?>[] enumValues = enumCls.getEnumConstants();
        HashMap<String, Enum<?>> map = new HashMap<>();
        int i = enumValues.length;
        while (true) {
            i--;
            if (i < 0) {
                break;
            }
            Enum<?> e = enumValues[i];
            map.put(e.toString(), e);
        }
        Enum<?> defaultEnum = ai == null ? null : ai.findDefaultEnumValue(enumCls);
        return new EnumResolver(enumCls, enumValues, map, defaultEnum);
    }

    public static EnumResolver constructUsingMethod(Class<Enum<?>> enumCls, AnnotatedMember accessor, AnnotationIntrospector ai) {
        Enum<?>[] enumValues = enumCls.getEnumConstants();
        HashMap<String, Enum<?>> map = new HashMap<>();
        int i = enumValues.length;
        while (true) {
            i--;
            if (i < 0) {
                break;
            }
            Enum<?> en = enumValues[i];
            try {
                Object o = accessor.getValue(en);
                if (o != null) {
                    map.put(o.toString(), en);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to access @JsonValue of Enum value " + en + ": " + e.getMessage());
            }
        }
        Enum<?> defaultEnum = ai != null ? ai.findDefaultEnumValue(enumCls) : null;
        return new EnumResolver(enumCls, enumValues, map, defaultEnum);
    }

    public static EnumResolver constructUnsafe(Class<?> rawEnumCls, AnnotationIntrospector ai) {
        return constructFor(rawEnumCls, ai);
    }

    public static EnumResolver constructUnsafeUsingToString(Class<?> rawEnumCls, AnnotationIntrospector ai) {
        return constructUsingToString(rawEnumCls, ai);
    }

    public static EnumResolver constructUnsafeUsingMethod(Class<?> rawEnumCls, AnnotatedMember accessor, AnnotationIntrospector ai) {
        return constructUsingMethod(rawEnumCls, accessor, ai);
    }

    public CompactStringObjectMap constructLookup() {
        return CompactStringObjectMap.construct(this._enumsById);
    }

    public Enum<?> findEnum(String key) {
        return this._enumsById.get(key);
    }

    public Enum<?> getEnum(int index) {
        if (index < 0 || index >= this._enums.length) {
            return null;
        }
        return this._enums[index];
    }

    public Enum<?> getDefaultValue() {
        return this._defaultValue;
    }

    public Enum<?>[] getRawEnums() {
        return this._enums;
    }

    public List<Enum<?>> getEnums() {
        ArrayList<Enum<?>> enums = new ArrayList<>(this._enums.length);
        Enum<?>[] arr$ = this._enums;
        for (Enum<?> e : arr$) {
            enums.add(e);
        }
        return enums;
    }

    public Collection<String> getEnumIds() {
        return this._enumsById.keySet();
    }

    public Class<Enum<?>> getEnumClass() {
        return this._enumClass;
    }

    public int lastValidIndex() {
        return this._enums.length - 1;
    }
}