package org.thymeleaf.standard.inline;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/inline/IInlinePreProcessorHandler.class */
public interface IInlinePreProcessorHandler {
    void handleText(char[] cArr, int i, int i2, int i3, int i4);

    void handleStandaloneElementStart(char[] cArr, int i, int i2, boolean z, int i3, int i4);

    void handleStandaloneElementEnd(char[] cArr, int i, int i2, boolean z, int i3, int i4);

    void handleOpenElementStart(char[] cArr, int i, int i2, int i3, int i4);

    void handleOpenElementEnd(char[] cArr, int i, int i2, int i3, int i4);

    void handleAutoOpenElementStart(char[] cArr, int i, int i2, int i3, int i4);

    void handleAutoOpenElementEnd(char[] cArr, int i, int i2, int i3, int i4);

    void handleCloseElementStart(char[] cArr, int i, int i2, int i3, int i4);

    void handleCloseElementEnd(char[] cArr, int i, int i2, int i3, int i4);

    void handleAutoCloseElementStart(char[] cArr, int i, int i2, int i3, int i4);

    void handleAutoCloseElementEnd(char[] cArr, int i, int i2, int i3, int i4);

    void handleAttribute(char[] cArr, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14);
}