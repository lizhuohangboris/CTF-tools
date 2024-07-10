package org.thymeleaf.templateparser.text;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/text/TextParsingLiteralUtil.class */
final class TextParsingLiteralUtil {
    private TextParsingLiteralUtil() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isRegexLiteralStart(char[] buffer, int offset, int maxi) {
        if (offset == 0 || buffer[offset] != '/' || TextParsingCommentUtil.isCommentBlockStart(buffer, offset, maxi) || TextParsingCommentUtil.isCommentLineStart(buffer, offset, maxi)) {
            return false;
        }
        for (int i = offset - 1; i >= 0; i--) {
            char c = buffer[i];
            if (!Character.isWhitespace(c)) {
                return c == '(' || c == '=' || c == ',';
            }
        }
        return false;
    }
}