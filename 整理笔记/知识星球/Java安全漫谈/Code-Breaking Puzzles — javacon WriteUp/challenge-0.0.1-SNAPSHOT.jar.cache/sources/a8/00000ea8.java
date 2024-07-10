package org.attoparser.simple;

import java.util.Map;
import org.attoparser.ParseException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/simple/ISimpleMarkupHandler.class */
public interface ISimpleMarkupHandler {
    void handleDocumentStart(long j, int i, int i2) throws ParseException;

    void handleDocumentEnd(long j, long j2, int i, int i2) throws ParseException;

    void handleXmlDeclaration(String str, String str2, String str3, int i, int i2) throws ParseException;

    void handleDocType(String str, String str2, String str3, String str4, int i, int i2) throws ParseException;

    void handleCDATASection(char[] cArr, int i, int i2, int i3, int i4) throws ParseException;

    void handleComment(char[] cArr, int i, int i2, int i3, int i4) throws ParseException;

    void handleText(char[] cArr, int i, int i2, int i3, int i4) throws ParseException;

    void handleStandaloneElement(String str, Map<String, String> map, boolean z, int i, int i2) throws ParseException;

    void handleOpenElement(String str, Map<String, String> map, int i, int i2) throws ParseException;

    void handleAutoOpenElement(String str, Map<String, String> map, int i, int i2) throws ParseException;

    void handleCloseElement(String str, int i, int i2) throws ParseException;

    void handleAutoCloseElement(String str, int i, int i2) throws ParseException;

    void handleUnmatchedCloseElement(String str, int i, int i2) throws ParseException;

    void handleProcessingInstruction(String str, String str2, int i, int i2) throws ParseException;
}