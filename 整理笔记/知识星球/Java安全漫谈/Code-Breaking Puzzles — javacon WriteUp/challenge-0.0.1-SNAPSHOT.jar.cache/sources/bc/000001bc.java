package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.pattern.DynamicConverter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/rolling/helper/IntegerTokenConverter.class */
public class IntegerTokenConverter extends DynamicConverter<Object> implements MonoTypedConverter {
    public static final String CONVERTER_KEY = "i";

    public String convert(int i) {
        return Integer.toString(i);
    }

    @Override // ch.qos.logback.core.pattern.Converter
    public String convert(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("Null argument forbidden");
        }
        if (o instanceof Integer) {
            Integer i = (Integer) o;
            return convert(i.intValue());
        }
        throw new IllegalArgumentException("Cannot convert " + o + " of type" + o.getClass().getName());
    }

    @Override // ch.qos.logback.core.rolling.helper.MonoTypedConverter
    public boolean isApplicable(Object o) {
        return o instanceof Integer;
    }
}