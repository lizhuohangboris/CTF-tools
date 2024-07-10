package org.attoparser;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/IAttributeSequenceHandler.class */
public interface IAttributeSequenceHandler {
    void handleAttribute(char[] cArr, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14) throws ParseException;

    void handleInnerWhiteSpace(char[] cArr, int i, int i2, int i3, int i4) throws ParseException;
}