package org.attoparser;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/IElementHandler.class */
public interface IElementHandler extends IAttributeSequenceHandler {
    void handleStandaloneElementStart(char[] cArr, int i, int i2, boolean z, int i3, int i4) throws ParseException;

    void handleStandaloneElementEnd(char[] cArr, int i, int i2, boolean z, int i3, int i4) throws ParseException;

    void handleOpenElementStart(char[] cArr, int i, int i2, int i3, int i4) throws ParseException;

    void handleOpenElementEnd(char[] cArr, int i, int i2, int i3, int i4) throws ParseException;

    void handleAutoOpenElementStart(char[] cArr, int i, int i2, int i3, int i4) throws ParseException;

    void handleAutoOpenElementEnd(char[] cArr, int i, int i2, int i3, int i4) throws ParseException;

    void handleCloseElementStart(char[] cArr, int i, int i2, int i3, int i4) throws ParseException;

    void handleCloseElementEnd(char[] cArr, int i, int i2, int i3, int i4) throws ParseException;

    void handleAutoCloseElementStart(char[] cArr, int i, int i2, int i3, int i4) throws ParseException;

    void handleAutoCloseElementEnd(char[] cArr, int i, int i2, int i3, int i4) throws ParseException;

    void handleUnmatchedCloseElementStart(char[] cArr, int i, int i2, int i3, int i4) throws ParseException;

    void handleUnmatchedCloseElementEnd(char[] cArr, int i, int i2, int i3, int i4) throws ParseException;
}