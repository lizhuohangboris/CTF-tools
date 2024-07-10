package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.9.7.jar:com/fasterxml/jackson/datatype/jsr310/deser/InstantDeserializer.class */
public class InstantDeserializer<T extends Temporal> extends JSR310DateTimeDeserializerBase<T> {
    private static final long serialVersionUID = 1;
    private static final Pattern ISO8601_UTC_ZERO_OFFSET_SUFFIX_REGEX = Pattern.compile("\\+00:?(00)?$");
    public static final InstantDeserializer<Instant> INSTANT = new InstantDeserializer<>(Instant.class, DateTimeFormatter.ISO_INSTANT, Instant::from, a -> {
        return Instant.ofEpochMilli(a.value);
    }, a2 -> {
        return Instant.ofEpochSecond(a2.integer, a2.fraction);
    }, null, true);
    public static final InstantDeserializer<OffsetDateTime> OFFSET_DATE_TIME = new InstantDeserializer<>(OffsetDateTime.class, DateTimeFormatter.ISO_OFFSET_DATE_TIME, OffsetDateTime::from, a -> {
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(a.value), a.zoneId);
    }, a2 -> {
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(a2.integer, a2.fraction), a2.zoneId);
    }, d, z -> {
        return d.withOffsetSameInstant(z.getRules().getOffset(d.toLocalDateTime()));
    }, true);
    public static final InstantDeserializer<ZonedDateTime> ZONED_DATE_TIME = new InstantDeserializer<>(ZonedDateTime.class, DateTimeFormatter.ISO_ZONED_DATE_TIME, ZonedDateTime::from, a -> {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(a.value), a.zoneId);
    }, a2 -> {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(a2.integer, a2.fraction), a2.zoneId);
    }, (v0, v1) -> {
        return v0.withZoneSameInstant(v1);
    }, false);
    protected final Function<FromIntegerArguments, T> fromMilliseconds;
    protected final Function<FromDecimalArguments, T> fromNanoseconds;
    protected final Function<TemporalAccessor, T> parsedToValue;
    protected final BiFunction<T, ZoneId, T> adjust;
    protected final boolean replaceZeroOffsetAsZ;
    protected final Boolean _adjustToContextTZOverride;

    protected InstantDeserializer(Class<T> supportedType, DateTimeFormatter formatter, Function<TemporalAccessor, T> parsedToValue, Function<FromIntegerArguments, T> fromMilliseconds, Function<FromDecimalArguments, T> fromNanoseconds, BiFunction<T, ZoneId, T> adjust, boolean replaceZeroOffsetAsZ) {
        super(supportedType, formatter);
        this.parsedToValue = parsedToValue;
        this.fromMilliseconds = fromMilliseconds;
        this.fromNanoseconds = fromNanoseconds;
        this.adjust = adjust == null ? d, z -> {
            return d;
        } : adjust;
        this.replaceZeroOffsetAsZ = replaceZeroOffsetAsZ;
        this._adjustToContextTZOverride = null;
    }

    protected InstantDeserializer(InstantDeserializer<T> base, DateTimeFormatter f) {
        super(base.handledType(), f);
        this.parsedToValue = base.parsedToValue;
        this.fromMilliseconds = base.fromMilliseconds;
        this.fromNanoseconds = base.fromNanoseconds;
        this.adjust = base.adjust;
        this.replaceZeroOffsetAsZ = this._formatter == DateTimeFormatter.ISO_INSTANT;
        this._adjustToContextTZOverride = base._adjustToContextTZOverride;
    }

    protected InstantDeserializer(InstantDeserializer<T> base, Boolean adjustToContextTimezoneOverride) {
        super(base.handledType(), base._formatter);
        this.parsedToValue = base.parsedToValue;
        this.fromMilliseconds = base.fromMilliseconds;
        this.fromNanoseconds = base.fromNanoseconds;
        this.adjust = base.adjust;
        this.replaceZeroOffsetAsZ = base.replaceZeroOffsetAsZ;
        this._adjustToContextTZOverride = adjustToContextTimezoneOverride;
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase
    protected JsonDeserializer<T> withDateFormat(DateTimeFormatter dtf) {
        if (dtf == this._formatter) {
            return this;
        }
        return new InstantDeserializer(this, dtf);
    }

    @Override // com.fasterxml.jackson.databind.JsonDeserializer
    public T deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        T value;
        switch (parser.getCurrentTokenId()) {
            case 3:
                return (T) _deserializeFromArray(parser, context);
            case 4:
            case 5:
            case 9:
            case 10:
            case 11:
            default:
                return (T) _handleUnexpectedToken(context, parser, JsonToken.VALUE_STRING, JsonToken.VALUE_NUMBER_INT, JsonToken.VALUE_NUMBER_FLOAT);
            case 6:
                String string = parser.getText().trim();
                if (string.length() == 0) {
                    return null;
                }
                if (this._formatter == DateTimeFormatter.ISO_INSTANT || this._formatter == DateTimeFormatter.ISO_OFFSET_DATE_TIME || this._formatter == DateTimeFormatter.ISO_ZONED_DATE_TIME) {
                    int dots = _countPeriods(string);
                    if (dots >= 0) {
                        try {
                            if (dots == 0) {
                                return _fromLong(context, Long.parseLong(string));
                            }
                            if (dots == 1) {
                                return _fromDecimal(context, new BigDecimal(string));
                            }
                        } catch (NumberFormatException e) {
                        }
                    }
                    string = replaceZeroOffsetAsZIfNecessary(string);
                }
                try {
                    TemporalAccessor acc = this._formatter.parse(string);
                    T value2 = this.parsedToValue.apply(acc);
                    value = value2;
                    if (shouldAdjustToContextTimezone(context)) {
                        return this.adjust.apply(value2, getZone(context));
                    }
                } catch (DateTimeException e2) {
                    value = (Temporal) _handleDateTimeException(context, e2, string);
                }
                return (T) value;
            case 7:
                return _fromLong(context, parser.getLongValue());
            case 8:
                return _fromDecimal(context, parser.getDecimalValue());
            case 12:
                return (T) parser.getEmbeddedObject();
        }
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase, com.fasterxml.jackson.databind.deser.ContextualDeserializer
    public JsonDeserializer<T> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
        JsonFormat.Value val;
        InstantDeserializer<T> deserializer = (InstantDeserializer) super.createContextual(ctxt, property);
        if (deserializer != this && (val = findFormatOverrides(ctxt, property, handledType())) != null) {
            return new InstantDeserializer(deserializer, val.getFeature(JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE));
        }
        return this;
    }

    protected boolean shouldAdjustToContextTimezone(DeserializationContext context) {
        return this._adjustToContextTZOverride != null ? this._adjustToContextTZOverride.booleanValue() : context.isEnabled(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
    }

    protected int _countPeriods(String str) {
        int commas = 0;
        int end = str.length();
        for (int i = 0; i < end; i++) {
            int ch2 = str.charAt(i);
            if (ch2 < 48 || ch2 > 57) {
                if (ch2 == 46) {
                    commas++;
                } else {
                    return -1;
                }
            }
        }
        return commas;
    }

    protected T _fromLong(DeserializationContext context, long timestamp) {
        if (context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
            return this.fromNanoseconds.apply(new FromDecimalArguments(timestamp, 0, getZone(context)));
        }
        return this.fromMilliseconds.apply(new FromIntegerArguments(timestamp, getZone(context)));
    }

    protected T _fromDecimal(DeserializationContext context, BigDecimal value) {
        long seconds = value.longValue();
        int nanoseconds = DecimalUtils.extractNanosecondDecimal(value, seconds);
        return this.fromNanoseconds.apply(new FromDecimalArguments(seconds, nanoseconds, getZone(context)));
    }

    private ZoneId getZone(DeserializationContext context) {
        if (this._valueClass == Instant.class) {
            return null;
        }
        return context.getTimeZone().toZoneId();
    }

    private String replaceZeroOffsetAsZIfNecessary(String text) {
        if (this.replaceZeroOffsetAsZ) {
            return ISO8601_UTC_ZERO_OFFSET_SUFFIX_REGEX.matcher(text).replaceFirst("Z");
        }
        return text;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.9.7.jar:com/fasterxml/jackson/datatype/jsr310/deser/InstantDeserializer$FromIntegerArguments.class */
    public static class FromIntegerArguments {
        public final long value;
        public final ZoneId zoneId;

        private FromIntegerArguments(long value, ZoneId zoneId) {
            this.value = value;
            this.zoneId = zoneId;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.9.7.jar:com/fasterxml/jackson/datatype/jsr310/deser/InstantDeserializer$FromDecimalArguments.class */
    public static class FromDecimalArguments {
        public final long integer;
        public final int fraction;
        public final ZoneId zoneId;

        private FromDecimalArguments(long integer, int fraction, ZoneId zoneId) {
            this.integer = integer;
            this.fraction = fraction;
            this.zoneId = zoneId;
        }
    }
}