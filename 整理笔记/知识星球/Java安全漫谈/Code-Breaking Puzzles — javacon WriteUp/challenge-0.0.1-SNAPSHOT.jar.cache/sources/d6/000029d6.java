package org.thymeleaf.templateparser.text;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/text/TextParsingElementUtil.class */
final class TextParsingElementUtil {
    private TextParsingElementUtil() {
    }

    public static void parseStandaloneElement(char[] buffer, int offset, int len, int line, int col, ITextHandler handler) throws TextParseException {
        if (len < 4 || !isOpenElementStart(buffer, offset, offset + len) || !isElementEnd(buffer, (offset + len) - 2, offset + len, true)) {
            throw new TextParseException("Could not parse as a well-formed standalone element: \"" + new String(buffer, offset, len) + "\"", line, col);
        }
        int contentOffset = offset + 2;
        int contentLen = len - 4;
        int maxi = contentOffset + contentLen;
        int[] locator = {line, col + 2};
        int elementNameEnd = TextParsingUtil.findNextWhitespaceCharWildcard(buffer, contentOffset, maxi, true, locator);
        if (elementNameEnd == -1) {
            handler.handleStandaloneElementStart(buffer, contentOffset, contentLen, true, line, col);
            handler.handleStandaloneElementEnd(buffer, contentOffset, contentLen, true, locator[0], locator[1]);
            return;
        }
        handler.handleStandaloneElementStart(buffer, contentOffset, elementNameEnd - contentOffset, true, line, col);
        TextParsingAttributeSequenceUtil.parseAttributeSequence(buffer, elementNameEnd, maxi - elementNameEnd, locator[0], locator[1], handler);
        TextParsingUtil.findNextStructureEndAvoidQuotes(buffer, elementNameEnd, maxi, locator);
        handler.handleStandaloneElementEnd(buffer, contentOffset, elementNameEnd - contentOffset, true, locator[0], locator[1]);
    }

    public static void parseOpenElement(char[] buffer, int offset, int len, int line, int col, ITextHandler handler) throws TextParseException {
        if (len < 3 || !isOpenElementStart(buffer, offset, offset + len) || !isElementEnd(buffer, (offset + len) - 1, offset + len, false)) {
            throw new TextParseException("Could not parse as a well-formed open element: \"" + new String(buffer, offset, len) + "\"", line, col);
        }
        int contentOffset = offset + 2;
        int contentLen = len - 3;
        int maxi = contentOffset + contentLen;
        int[] locator = {line, col + 2};
        int elementNameEnd = TextParsingUtil.findNextWhitespaceCharWildcard(buffer, contentOffset, maxi, true, locator);
        if (elementNameEnd == -1) {
            handler.handleOpenElementStart(buffer, contentOffset, contentLen, line, col);
            handler.handleOpenElementEnd(buffer, contentOffset, contentLen, locator[0], locator[1]);
            return;
        }
        handler.handleOpenElementStart(buffer, contentOffset, elementNameEnd - contentOffset, line, col);
        TextParsingAttributeSequenceUtil.parseAttributeSequence(buffer, elementNameEnd, maxi - elementNameEnd, locator[0], locator[1], handler);
        TextParsingUtil.findNextStructureEndAvoidQuotes(buffer, elementNameEnd, maxi, locator);
        handler.handleOpenElementEnd(buffer, contentOffset, elementNameEnd - contentOffset, locator[0], locator[1]);
    }

    public static void parseCloseElement(char[] buffer, int offset, int len, int line, int col, ITextHandler handler) throws TextParseException {
        if (len < 3 || !isCloseElementStart(buffer, offset, offset + len) || !isElementEnd(buffer, (offset + len) - 1, offset + len, false)) {
            throw new TextParseException("Could not parse as a well-formed close element: \"" + new String(buffer, offset, len) + "\"", line, col);
        }
        int contentOffset = offset + 2;
        int contentLen = len - 3;
        int maxi = contentOffset + contentLen;
        int[] locator = {line, col + 2};
        int elementNameEnd = TextParsingUtil.findNextWhitespaceCharWildcard(buffer, contentOffset, maxi, true, locator);
        if (elementNameEnd == -1) {
            handler.handleCloseElementStart(buffer, contentOffset, contentLen, line, col);
            handler.handleCloseElementEnd(buffer, contentOffset, contentLen, locator[0], locator[1]);
            return;
        }
        handler.handleCloseElementStart(buffer, contentOffset, elementNameEnd - contentOffset, line, col);
        int wsEnd = TextParsingUtil.findNextNonWhitespaceCharWildcard(buffer, elementNameEnd, maxi, locator);
        if (wsEnd != -1) {
            throw new TextParseException("Could not parse as a well-formed closing element \"" + new String(buffer, offset, len) + "\": No attributes are allowed here", line, col);
        }
        handler.handleCloseElementEnd(buffer, contentOffset, elementNameEnd - contentOffset, locator[0], locator[1]);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isOpenElementStart(char[] buffer, int offset, int maxi) {
        int len = maxi - offset;
        return len > 2 && buffer[offset] == '[' && buffer[offset + 1] == '#' && isElementNameOrEnd(buffer, offset + 2, maxi);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isCloseElementStart(char[] buffer, int offset, int maxi) {
        int len = maxi - offset;
        return len > 2 && buffer[offset] == '[' && buffer[offset + 1] == '/' && isElementNameOrEnd(buffer, offset + 2, maxi);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isElementEnd(char[] buffer, int offset, int maxi, boolean minimized) {
        int len = maxi - offset;
        if (len < 1) {
            return false;
        }
        return minimized ? len >= 2 && buffer[offset] == '/' && buffer[offset + 1] == ']' : buffer[offset] == ']';
    }

    private static boolean isElementNameOrEnd(char[] buffer, int offset, int maxi) {
        if (Character.isWhitespace(buffer[offset])) {
            return true;
        }
        int len = maxi - offset;
        if (len > 1 && buffer[offset] == '/') {
            return isElementEnd(buffer, offset, maxi, true);
        }
        if (len <= 0 || buffer[offset] != ']') {
            return (len <= 0 || buffer[offset] == '-' || buffer[offset] == '!' || buffer[offset] == '/' || buffer[offset] == '?' || buffer[offset] == '[' || buffer[offset] == '{' || Character.isWhitespace(buffer[offset])) ? false : true;
        }
        return isElementEnd(buffer, offset, maxi, false);
    }
}