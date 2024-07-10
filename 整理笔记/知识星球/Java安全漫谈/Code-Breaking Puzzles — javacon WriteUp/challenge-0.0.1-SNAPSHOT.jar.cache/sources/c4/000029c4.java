package org.thymeleaf.templateparser.text;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/text/AbstractTextHandler.class */
public abstract class AbstractTextHandler implements ITextHandler {
    @Override // org.thymeleaf.templateparser.text.ITextHandler
    public void handleDocumentStart(long startTimeNanos, int line, int col) throws TextParseException {
    }

    @Override // org.thymeleaf.templateparser.text.ITextHandler
    public void handleDocumentEnd(long endTimeNanos, long totalTimeNanos, int line, int col) throws TextParseException {
    }

    @Override // org.thymeleaf.templateparser.text.ITextHandler
    public void handleText(char[] buffer, int offset, int len, int line, int col) throws TextParseException {
    }

    @Override // org.thymeleaf.templateparser.text.ITextHandler
    public void handleComment(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col) throws TextParseException {
    }

    @Override // org.thymeleaf.templateparser.text.ITextHandler
    public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws TextParseException {
    }

    @Override // org.thymeleaf.templateparser.text.ITextHandler
    public void handleStandaloneElementEnd(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws TextParseException {
    }

    @Override // org.thymeleaf.templateparser.text.ITextHandler
    public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws TextParseException {
    }

    @Override // org.thymeleaf.templateparser.text.ITextHandler
    public void handleOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws TextParseException {
    }

    @Override // org.thymeleaf.templateparser.text.ITextHandler
    public void handleCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws TextParseException {
    }

    @Override // org.thymeleaf.templateparser.text.ITextHandler
    public void handleCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws TextParseException {
    }

    @Override // org.thymeleaf.templateparser.text.ITextHandler
    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) throws TextParseException {
    }
}