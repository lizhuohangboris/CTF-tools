package com.fasterxml.jackson.datatype.jsr310.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-datatype-jsr310-2.9.7.jar:com/fasterxml/jackson/datatype/jsr310/ser/InstantSerializer.class */
public class InstantSerializer extends InstantSerializerBase<Instant> {
    private static final long serialVersionUID = 1;
    public static final InstantSerializer INSTANCE = new InstantSerializer();

    protected InstantSerializer() {
        super(Instant.class, (v0) -> {
            return v0.toEpochMilli();
        }, (v0) -> {
            return v0.getEpochSecond();
        }, (v0) -> {
            return v0.getNano();
        }, null);
    }

    protected InstantSerializer(InstantSerializer base, Boolean useTimestamp, DateTimeFormatter formatter) {
        this(base, useTimestamp, null, formatter);
    }

    protected InstantSerializer(InstantSerializer base, Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter formatter) {
        super(base, useTimestamp, useNanoseconds, formatter);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializerBase, com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    protected JSR310FormattedSerializerBase<Instant> withFormat(Boolean useTimestamp, DateTimeFormatter formatter, JsonFormat.Shape shape) {
        return new InstantSerializer(this, useTimestamp, formatter);
    }

    @Override // com.fasterxml.jackson.datatype.jsr310.ser.JSR310FormattedSerializerBase
    protected JSR310FormattedSerializerBase<?> withFeatures(Boolean writeZoneId, Boolean writeNanoseconds) {
        return new InstantSerializer(this, this._useTimestamp, writeNanoseconds, this._formatter);
    }
}