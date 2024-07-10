package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/DateDeserializers.class */
public class DateDeserializers {
    private static final HashSet<String> _classNames = new HashSet<>();

    static {
        Class<?>[] numberTypes = {Calendar.class, GregorianCalendar.class, Date.class, java.util.Date.class, Timestamp.class};
        for (Class<?> cls : numberTypes) {
            _classNames.add(cls.getName());
        }
    }

    public static JsonDeserializer<?> find(Class<?> rawType, String clsName) {
        if (_classNames.contains(clsName)) {
            if (rawType == Calendar.class) {
                return new CalendarDeserializer();
            }
            if (rawType == java.util.Date.class) {
                return DateDeserializer.instance;
            }
            if (rawType == Date.class) {
                return new SqlDateDeserializer();
            }
            if (rawType == Timestamp.class) {
                return new TimestampDeserializer();
            }
            if (rawType == GregorianCalendar.class) {
                return new CalendarDeserializer(GregorianCalendar.class);
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/DateDeserializers$DateBasedDeserializer.class */
    public static abstract class DateBasedDeserializer<T> extends StdScalarDeserializer<T> implements ContextualDeserializer {
        protected final DateFormat _customFormat;
        protected final String _formatString;

        protected abstract DateBasedDeserializer<T> withDateFormat(DateFormat dateFormat, String str);

        protected DateBasedDeserializer(Class<?> clz) {
            super(clz);
            this._customFormat = null;
            this._formatString = null;
        }

        protected DateBasedDeserializer(DateBasedDeserializer<T> base, DateFormat format, String formatStr) {
            super(base._valueClass);
            this._customFormat = format;
            this._formatString = formatStr;
        }

        public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
            DateFormat df;
            DateFormat df2;
            JsonFormat.Value format = findFormatOverrides(ctxt, property, handledType());
            if (format != null) {
                TimeZone tz = format.getTimeZone();
                Boolean lenient = format.getLenient();
                if (format.hasPattern()) {
                    String pattern = format.getPattern();
                    Locale loc = format.hasLocale() ? format.getLocale() : ctxt.getLocale();
                    SimpleDateFormat df3 = new SimpleDateFormat(pattern, loc);
                    if (tz == null) {
                        tz = ctxt.getTimeZone();
                    }
                    df3.setTimeZone(tz);
                    if (lenient != null) {
                        df3.setLenient(lenient.booleanValue());
                    }
                    return withDateFormat(df3, pattern);
                } else if (tz != null) {
                    DateFormat df4 = ctxt.getConfig().getDateFormat();
                    if (df4.getClass() == StdDateFormat.class) {
                        Locale loc2 = format.hasLocale() ? format.getLocale() : ctxt.getLocale();
                        StdDateFormat std = ((StdDateFormat) df4).withTimeZone(tz).withLocale(loc2);
                        if (lenient != null) {
                            std = std.withLenient(lenient);
                        }
                        df2 = std;
                    } else {
                        df2 = (DateFormat) df4.clone();
                        df2.setTimeZone(tz);
                        if (lenient != null) {
                            df2.setLenient(lenient.booleanValue());
                        }
                    }
                    return withDateFormat(df2, this._formatString);
                } else if (lenient != null) {
                    DateFormat df5 = ctxt.getConfig().getDateFormat();
                    String pattern2 = this._formatString;
                    if (df5.getClass() == StdDateFormat.class) {
                        StdDateFormat std2 = ((StdDateFormat) df5).withLenient(lenient);
                        df = std2;
                        pattern2 = std2.toPattern();
                    } else {
                        df = (DateFormat) df5.clone();
                        df.setLenient(lenient.booleanValue());
                        if (df instanceof SimpleDateFormat) {
                            ((SimpleDateFormat) df).toPattern();
                        }
                    }
                    if (pattern2 == null) {
                        pattern2 = "[unknown]";
                    }
                    return withDateFormat(df, pattern2);
                }
            }
            return this;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.fasterxml.jackson.databind.deser.std.StdDeserializer
        public java.util.Date _parseDate(JsonParser p, DeserializationContext ctxt) throws IOException {
            java.util.Date parse;
            if (this._customFormat != null && p.hasToken(JsonToken.VALUE_STRING)) {
                String str = p.getText().trim();
                if (str.length() == 0) {
                    return (java.util.Date) getEmptyValue(ctxt);
                }
                synchronized (this._customFormat) {
                    try {
                        parse = this._customFormat.parse(str);
                    } catch (ParseException e) {
                        return (java.util.Date) ctxt.handleWeirdStringValue(handledType(), str, "expected format \"%s\"", this._formatString);
                    }
                }
                return parse;
            }
            return super._parseDate(p, ctxt);
        }
    }

    @JacksonStdImpl
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/DateDeserializers$CalendarDeserializer.class */
    public static class CalendarDeserializer extends DateBasedDeserializer<Calendar> {
        protected final Constructor<Calendar> _defaultCtor;

        @Override // com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateBasedDeserializer, com.fasterxml.jackson.databind.deser.ContextualDeserializer
        public /* bridge */ /* synthetic */ JsonDeserializer createContextual(DeserializationContext x0, BeanProperty x1) throws JsonMappingException {
            return super.createContextual(x0, x1);
        }

        public CalendarDeserializer() {
            super(Calendar.class);
            this._defaultCtor = null;
        }

        public CalendarDeserializer(Class<? extends Calendar> cc) {
            super(cc);
            this._defaultCtor = ClassUtil.findConstructor(cc, false);
        }

        public CalendarDeserializer(CalendarDeserializer src, DateFormat df, String formatString) {
            super(src, df, formatString);
            this._defaultCtor = src._defaultCtor;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateBasedDeserializer
        /* renamed from: withDateFormat */
        public DateBasedDeserializer<Calendar> withDateFormat2(DateFormat df, String formatString) {
            return new CalendarDeserializer(this, df, formatString);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Calendar deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            java.util.Date d = _parseDate(p, ctxt);
            if (d == null) {
                return null;
            }
            if (this._defaultCtor == null) {
                return ctxt.constructCalendar(d);
            }
            try {
                Calendar c = this._defaultCtor.newInstance(new Object[0]);
                c.setTimeInMillis(d.getTime());
                TimeZone tz = ctxt.getTimeZone();
                if (tz != null) {
                    c.setTimeZone(tz);
                }
                return c;
            } catch (Exception e) {
                return (Calendar) ctxt.handleInstantiationProblem(handledType(), d, e);
            }
        }
    }

    @JacksonStdImpl
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/DateDeserializers$DateDeserializer.class */
    public static class DateDeserializer extends DateBasedDeserializer<java.util.Date> {
        public static final DateDeserializer instance = new DateDeserializer();

        @Override // com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateBasedDeserializer, com.fasterxml.jackson.databind.deser.ContextualDeserializer
        public /* bridge */ /* synthetic */ JsonDeserializer createContextual(DeserializationContext x0, BeanProperty x1) throws JsonMappingException {
            return super.createContextual(x0, x1);
        }

        public DateDeserializer() {
            super(java.util.Date.class);
        }

        public DateDeserializer(DateDeserializer base, DateFormat df, String formatString) {
            super(base, df, formatString);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateBasedDeserializer
        /* renamed from: withDateFormat */
        public DateBasedDeserializer<java.util.Date> withDateFormat2(DateFormat df, String formatString) {
            return new DateDeserializer(this, df, formatString);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public java.util.Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return _parseDate(p, ctxt);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/DateDeserializers$SqlDateDeserializer.class */
    public static class SqlDateDeserializer extends DateBasedDeserializer<Date> {
        @Override // com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateBasedDeserializer, com.fasterxml.jackson.databind.deser.ContextualDeserializer
        public /* bridge */ /* synthetic */ JsonDeserializer createContextual(DeserializationContext x0, BeanProperty x1) throws JsonMappingException {
            return super.createContextual(x0, x1);
        }

        public SqlDateDeserializer() {
            super(Date.class);
        }

        public SqlDateDeserializer(SqlDateDeserializer src, DateFormat df, String formatString) {
            super(src, df, formatString);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateBasedDeserializer
        /* renamed from: withDateFormat */
        public DateBasedDeserializer<Date> withDateFormat2(DateFormat df, String formatString) {
            return new SqlDateDeserializer(this, df, formatString);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            java.util.Date d = _parseDate(p, ctxt);
            if (d == null) {
                return null;
            }
            return new Date(d.getTime());
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-databind-2.9.7.jar:com/fasterxml/jackson/databind/deser/std/DateDeserializers$TimestampDeserializer.class */
    public static class TimestampDeserializer extends DateBasedDeserializer<Timestamp> {
        @Override // com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateBasedDeserializer, com.fasterxml.jackson.databind.deser.ContextualDeserializer
        public /* bridge */ /* synthetic */ JsonDeserializer createContextual(DeserializationContext x0, BeanProperty x1) throws JsonMappingException {
            return super.createContextual(x0, x1);
        }

        public TimestampDeserializer() {
            super(Timestamp.class);
        }

        public TimestampDeserializer(TimestampDeserializer src, DateFormat df, String formatString) {
            super(src, df, formatString);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateBasedDeserializer
        /* renamed from: withDateFormat */
        public DateBasedDeserializer<Timestamp> withDateFormat2(DateFormat df, String formatString) {
            return new TimestampDeserializer(this, df, formatString);
        }

        @Override // com.fasterxml.jackson.databind.JsonDeserializer
        public Timestamp deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            java.util.Date d = _parseDate(p, ctxt);
            if (d == null) {
                return null;
            }
            return new Timestamp(d.getTime());
        }
    }
}