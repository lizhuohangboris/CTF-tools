package org.attoparser;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/ParsingElementMarkupUtil.class */
public final class ParsingElementMarkupUtil {
    private ParsingElementMarkupUtil() {
    }

    public static void parseStandaloneElement(char[] buffer, int offset, int len, int line, int col, IMarkupHandler markupHandler) throws ParseException {
        if (len < 4 || !isOpenElementStart(buffer, offset, offset + len) || !isElementEnd(buffer, (offset + len) - 2, offset + len, true)) {
            throw new ParseException("Could not parse as a well-formed standalone element: \"" + new String(buffer, offset, len) + "\"", line, col);
        }
        int contentOffset = offset + 1;
        int contentLen = len - 3;
        int maxi = contentOffset + contentLen;
        int[] locator = {line, col + 1};
        int elementNameEnd = ParsingMarkupUtil.findNextWhitespaceCharWildcard(buffer, contentOffset, maxi, true, locator);
        if (elementNameEnd == -1) {
            markupHandler.handleStandaloneElementStart(buffer, contentOffset, contentLen, true, line, col);
            markupHandler.handleStandaloneElementEnd(buffer, contentOffset, contentLen, true, locator[0], locator[1]);
            return;
        }
        markupHandler.handleStandaloneElementStart(buffer, contentOffset, elementNameEnd - contentOffset, true, line, col);
        ParsingAttributeSequenceUtil.parseAttributeSequence(buffer, elementNameEnd, maxi - elementNameEnd, locator[0], locator[1], markupHandler);
        ParsingMarkupUtil.findNextStructureEndAvoidQuotes(buffer, elementNameEnd, maxi, locator);
        markupHandler.handleStandaloneElementEnd(buffer, contentOffset, elementNameEnd - contentOffset, true, locator[0], locator[1]);
    }

    public static void parseOpenElement(char[] buffer, int offset, int len, int line, int col, IMarkupHandler markupHandler) throws ParseException {
        if (len < 3 || !isOpenElementStart(buffer, offset, offset + len) || !isElementEnd(buffer, (offset + len) - 1, offset + len, false)) {
            throw new ParseException("Could not parse as a well-formed open element: \"" + new String(buffer, offset, len) + "\"", line, col);
        }
        int contentOffset = offset + 1;
        int contentLen = len - 2;
        int maxi = contentOffset + contentLen;
        int[] locator = {line, col + 1};
        int elementNameEnd = ParsingMarkupUtil.findNextWhitespaceCharWildcard(buffer, contentOffset, maxi, true, locator);
        if (elementNameEnd == -1) {
            markupHandler.handleOpenElementStart(buffer, contentOffset, contentLen, line, col);
            markupHandler.handleOpenElementEnd(buffer, contentOffset, contentLen, locator[0], locator[1]);
            return;
        }
        markupHandler.handleOpenElementStart(buffer, contentOffset, elementNameEnd - contentOffset, line, col);
        ParsingAttributeSequenceUtil.parseAttributeSequence(buffer, elementNameEnd, maxi - elementNameEnd, locator[0], locator[1], markupHandler);
        ParsingMarkupUtil.findNextStructureEndAvoidQuotes(buffer, elementNameEnd, maxi, locator);
        markupHandler.handleOpenElementEnd(buffer, contentOffset, elementNameEnd - contentOffset, locator[0], locator[1]);
    }

    public static void parseCloseElement(char[] buffer, int offset, int len, int line, int col, IMarkupHandler markupHandler) throws ParseException {
        if (len < 4 || !isCloseElementStart(buffer, offset, offset + len) || !isElementEnd(buffer, (offset + len) - 1, offset + len, false)) {
            throw new ParseException("Could not parse as a well-formed close element: \"" + new String(buffer, offset, len) + "\"", line, col);
        }
        int contentOffset = offset + 2;
        int contentLen = len - 3;
        int maxi = contentOffset + contentLen;
        int[] locator = {line, col + 2};
        int elementNameEnd = ParsingMarkupUtil.findNextWhitespaceCharWildcard(buffer, contentOffset, maxi, true, locator);
        if (elementNameEnd == -1) {
            markupHandler.handleCloseElementStart(buffer, contentOffset, contentLen, line, col);
            markupHandler.handleCloseElementEnd(buffer, contentOffset, contentLen, locator[0], locator[1]);
            return;
        }
        markupHandler.handleCloseElementStart(buffer, contentOffset, elementNameEnd - contentOffset, line, col);
        int currentArtifactLine = locator[0];
        int currentArtifactCol = locator[1];
        int wsEnd = ParsingMarkupUtil.findNextNonWhitespaceCharWildcard(buffer, elementNameEnd, maxi, locator);
        if (wsEnd != -1) {
            throw new ParseException("Could not parse as a well-formed closing element \"</" + new String(buffer, contentOffset, contentLen) + ">\": No attributes are allowed here", line, col);
        }
        markupHandler.handleInnerWhiteSpace(buffer, elementNameEnd, maxi - elementNameEnd, currentArtifactLine, currentArtifactCol);
        markupHandler.handleCloseElementEnd(buffer, contentOffset, elementNameEnd - contentOffset, locator[0], locator[1]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isOpenElementStart(char[] buffer, int offset, int maxi) {
        int len = maxi - offset;
        return len > 1 && buffer[offset] == '<' && isElementName(buffer, offset + 1, maxi);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isCloseElementStart(char[] buffer, int offset, int maxi) {
        int len = maxi - offset;
        return len > 2 && buffer[offset] == '<' && buffer[offset + 1] == '/' && isElementName(buffer, offset + 2, maxi);
    }

    static boolean isElementEnd(char[] buffer, int offset, int maxi, boolean minimized) {
        int len = maxi - offset;
        if (len < 1) {
            return false;
        }
        return minimized ? len >= 2 && buffer[offset] == '/' && buffer[offset + 1] == '>' : buffer[offset] == '>';
    }

    private static boolean isElementName(char[] buffer, int offset, int maxi) {
        int len = maxi - offset;
        return (len <= 1 || buffer[offset] != '!') ? (len <= 0 || buffer[offset] == '-' || buffer[offset] == '!' || buffer[offset] == '/' || buffer[offset] == '?' || buffer[offset] == '[' || Character.isWhitespace(buffer[offset])) ? false : true : len > 8 ? (buffer[offset + 1] == '-' || buffer[offset + 1] == '!' || buffer[offset + 1] == '/' || buffer[offset + 1] == '?' || buffer[offset + 1] == '[' || ((buffer[offset + 1] == 'D' || buffer[offset + 1] == 'd') && ((buffer[offset + 2] == 'O' || buffer[offset + 2] == 'o') && ((buffer[offset + 3] == 'C' || buffer[offset + 3] == 'c') && ((buffer[offset + 4] == 'T' || buffer[offset + 4] == 't') && ((buffer[offset + 5] == 'Y' || buffer[offset + 5] == 'y') && ((buffer[offset + 6] == 'P' || buffer[offset + 6] == 'p') && ((buffer[offset + 7] == 'E' || buffer[offset + 7] == 'e') && (Character.isWhitespace(buffer[offset + 8]) || buffer[offset + 8] == '>'))))))))) ? false : true : (buffer[offset + 1] == '-' || buffer[offset + 1] == '!' || buffer[offset + 1] == '/' || buffer[offset + 1] == '?' || buffer[offset + 1] == '[' || Character.isWhitespace(buffer[offset + 1])) ? false : true;
    }
}