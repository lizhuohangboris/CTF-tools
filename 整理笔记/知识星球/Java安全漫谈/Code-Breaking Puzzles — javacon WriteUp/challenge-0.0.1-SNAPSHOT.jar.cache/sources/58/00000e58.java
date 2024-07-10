package org.attoparser;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/IDocumentHandler.class */
public interface IDocumentHandler {
    void handleDocumentStart(long j, int i, int i2) throws ParseException;

    void handleDocumentEnd(long j, long j2, int i, int i2) throws ParseException;
}