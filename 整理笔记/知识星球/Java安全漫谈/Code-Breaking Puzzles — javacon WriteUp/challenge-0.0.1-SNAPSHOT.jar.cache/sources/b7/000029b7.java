package org.thymeleaf.templateparser.raw;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/raw/IRawHandler.class */
public interface IRawHandler {
    void handleDocumentStart(long j, int i, int i2) throws RawParseException;

    void handleDocumentEnd(long j, long j2, int i, int i2) throws RawParseException;

    void handleText(char[] cArr, int i, int i2, int i3, int i4) throws RawParseException;
}