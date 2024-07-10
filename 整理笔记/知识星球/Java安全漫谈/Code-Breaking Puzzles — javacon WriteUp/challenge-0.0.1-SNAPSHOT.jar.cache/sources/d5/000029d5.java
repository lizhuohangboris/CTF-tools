package org.thymeleaf.templateparser.text;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/text/TextParsingCommentUtil.class */
final class TextParsingCommentUtil {
    private TextParsingCommentUtil() {
    }

    public static void parseComment(char[] buffer, int offset, int len, int line, int col, ITextHandler handler) throws TextParseException {
        if (len < 4 || !isCommentBlockStart(buffer, offset, offset + len) || !isCommentBlockEnd(buffer, (offset + len) - 2, offset + len)) {
            throw new TextParseException("Could not parse as a well-formed Comment: \"" + new String(buffer, offset, len) + "\"", line, col);
        }
        int contentOffset = offset + 2;
        int contentLen = len - 4;
        handler.handleComment(buffer, contentOffset, contentLen, offset, len, line, col);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isCommentBlockStart(char[] buffer, int offset, int maxi) {
        return maxi - offset > 1 && buffer[offset] == '/' && buffer[offset + 1] == '*';
    }

    static boolean isCommentBlockEnd(char[] buffer, int offset, int maxi) {
        return maxi - offset > 1 && buffer[offset] == '*' && buffer[offset + 1] == '/';
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isCommentLineStart(char[] buffer, int offset, int maxi) {
        return maxi - offset > 1 && buffer[offset] == '/' && buffer[offset + 1] == '/';
    }
}