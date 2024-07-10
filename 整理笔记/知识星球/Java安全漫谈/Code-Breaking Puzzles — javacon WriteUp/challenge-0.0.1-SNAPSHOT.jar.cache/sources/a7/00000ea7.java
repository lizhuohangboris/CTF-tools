package org.attoparser.simple;

import java.util.Map;
import org.attoparser.ParseException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/simple/AbstractSimpleMarkupHandler.class */
public abstract class AbstractSimpleMarkupHandler implements ISimpleMarkupHandler {
    protected AbstractSimpleMarkupHandler() {
    }

    @Override // org.attoparser.simple.ISimpleMarkupHandler
    public void handleDocumentStart(long startTimeNanos, int line, int col) throws ParseException {
    }

    @Override // org.attoparser.simple.ISimpleMarkupHandler
    public void handleDocumentEnd(long endTimeNanos, long totalTimeNanos, int line, int col) throws ParseException {
    }

    @Override // org.attoparser.simple.ISimpleMarkupHandler
    public void handleXmlDeclaration(String version, String encoding, String standalone, int line, int col) throws ParseException {
    }

    @Override // org.attoparser.simple.ISimpleMarkupHandler
    public void handleDocType(String elementName, String publicId, String systemId, String internalSubset, int line, int col) throws ParseException {
    }

    @Override // org.attoparser.simple.ISimpleMarkupHandler
    public void handleCDATASection(char[] buffer, int offset, int len, int line, int col) throws ParseException {
    }

    @Override // org.attoparser.simple.ISimpleMarkupHandler
    public void handleComment(char[] buffer, int offset, int len, int line, int col) throws ParseException {
    }

    @Override // org.attoparser.simple.ISimpleMarkupHandler
    public void handleText(char[] buffer, int offset, int len, int line, int col) throws ParseException {
    }

    @Override // org.attoparser.simple.ISimpleMarkupHandler
    public void handleStandaloneElement(String elementName, Map<String, String> attributes, boolean minimized, int line, int col) throws ParseException {
    }

    @Override // org.attoparser.simple.ISimpleMarkupHandler
    public void handleOpenElement(String elementName, Map<String, String> attributes, int line, int col) throws ParseException {
    }

    @Override // org.attoparser.simple.ISimpleMarkupHandler
    public void handleAutoOpenElement(String elementName, Map<String, String> attributes, int line, int col) throws ParseException {
    }

    @Override // org.attoparser.simple.ISimpleMarkupHandler
    public void handleCloseElement(String elementName, int line, int col) throws ParseException {
    }

    @Override // org.attoparser.simple.ISimpleMarkupHandler
    public void handleAutoCloseElement(String elementName, int line, int col) throws ParseException {
    }

    @Override // org.attoparser.simple.ISimpleMarkupHandler
    public void handleUnmatchedCloseElement(String elementName, int line, int col) throws ParseException {
    }

    @Override // org.attoparser.simple.ISimpleMarkupHandler
    public void handleProcessingInstruction(String target, String content, int line, int col) throws ParseException {
    }
}