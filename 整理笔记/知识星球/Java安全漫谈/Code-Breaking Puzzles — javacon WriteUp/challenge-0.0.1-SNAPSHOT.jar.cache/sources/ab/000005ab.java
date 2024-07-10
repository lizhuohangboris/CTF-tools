package com.fasterxml.jackson.datatype.jsr310.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.9.7.jar:com/fasterxml/jackson/datatype/jsr310/ser/OffsetDateTimeSerializer.class */
public class OffsetDateTimeSerializer extends InstantSerializerBase<OffsetDateTime> {
    private static final long serialVersionUID = 1;
    public static final OffsetDateTimeSerializer INSTANCE = new OffsetDateTimeSerializer();

    protected OffsetDateTimeSerializer() {
        super(OffsetDateTime.class, dt -> {
            return dt.toInstant().toEpochMilli();
        }, (v0) -> {
            return v0.toEpochSecond();
        }, (v0) -> {
            return v0.getNano();
        }, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    protected OffsetDateTimeSerializer(OffsetDateTimeSerializer base, Boolean useTimestamp, DateTimeFormatter formatter) {
        this(base, useTimestamp, null, formatter);
    }

    protected OffsetDateTimeSerializer(OffsetDateTimeSerializer base, Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter formatter) {
        super(base, useTimestamp, useNanoseconds, formatter);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializerBase, com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    protected JSR310FormattedSerializerBase<?> withFormat(Boolean useTimestamp, DateTimeFormatter formatter, JsonFormat.Shape shape) {
        return new OffsetDateTimeSerializer(this, useTimestamp, formatter);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    protected JSR310FormattedSerializerBase<?> withFeatures(Boolean writeZoneId, Boolean writeNanoseconds) {
        return new OffsetDateTimeSerializer(this, this._useTimestamp, writeNanoseconds, this._formatter);
    }
}