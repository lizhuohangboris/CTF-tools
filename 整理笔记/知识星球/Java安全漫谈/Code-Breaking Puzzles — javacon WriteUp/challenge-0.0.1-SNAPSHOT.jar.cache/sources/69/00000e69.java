package org.attoparser;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/ParsingCommentMarkupUtil.class */
public final class ParsingCommentMarkupUtil {
    private ParsingCommentMarkupUtil() {
    }

    public static void parseComment(char[] buffer, int offset, int len, int line, int col, ICommentHandler handler) throws ParseException {
        if (len < 7 || !isCommentStart(buffer, offset, offset + len) || !isCommentEnd(buffer, (offset + len) - 3, offset + len)) {
            throw new ParseException("Could not parse as a well-formed Comment: \"" + new String(buffer, offset, len) + "\"", line, col);
        }
        int contentOffset = offset + 4;
        int contentLen = len - 7;
        handler.handleComment(buffer, contentOffset, contentLen, offset, len, line, col);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isCommentStart(char[] buffer, int offset, int maxi) {
        return maxi - offset > 3 && buffer[offset] == '<' && buffer[offset + 1] == '!' && buffer[offset + 2] == '-' && buffer[offset + 3] == '-';
    }

    static boolean isCommentEnd(char[] buffer, int offset, int maxi) {
        return maxi - offset > 2 && buffer[offset] == '-' && buffer[offset + 1] == '-' && buffer[offset + 2] == '>';
    }
}