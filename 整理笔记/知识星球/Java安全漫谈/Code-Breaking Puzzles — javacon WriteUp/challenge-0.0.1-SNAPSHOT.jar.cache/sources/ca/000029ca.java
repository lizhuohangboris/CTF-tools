package org.thymeleaf.templateparser.text;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/text/ITextHandler.class */
public interface ITextHandler {
    void handleDocumentStart(long j, int i, int i2) throws TextParseException;

    void handleDocumentEnd(long j, long j2, int i, int i2) throws TextParseException;

    void handleText(char[] cArr, int i, int i2, int i3, int i4) throws TextParseException;

    void handleComment(char[] cArr, int i, int i2, int i3, int i4, int i5, int i6) throws TextParseException;

    void handleStandaloneElementStart(char[] cArr, int i, int i2, boolean z, int i3, int i4) throws TextParseException;

    void handleStandaloneElementEnd(char[] cArr, int i, int i2, boolean z, int i3, int i4) throws TextParseException;

    void handleOpenElementStart(char[] cArr, int i, int i2, int i3, int i4) throws TextParseException;

    void handleOpenElementEnd(char[] cArr, int i, int i2, int i3, int i4) throws TextParseException;

    void handleCloseElementStart(char[] cArr, int i, int i2, int i3, int i4) throws TextParseException;

    void handleCloseElementEnd(char[] cArr, int i, int i2, int i3, int i4) throws TextParseException;

    void handleAttribute(char[] cArr, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14) throws TextParseException;
}