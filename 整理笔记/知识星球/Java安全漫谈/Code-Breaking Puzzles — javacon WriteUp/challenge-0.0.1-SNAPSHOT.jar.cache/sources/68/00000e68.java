package org.attoparser;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/ParsingCDATASectionMarkupUtil.class */
public final class ParsingCDATASectionMarkupUtil {
    private ParsingCDATASectionMarkupUtil() {
    }

    public static void parseCDATASection(char[] buffer, int offset, int len, int line, int col, ICDATASectionHandler handler) throws ParseException {
        if (len < 12 || !isCDATASectionStart(buffer, offset, offset + len) || !isCDATASectionEnd(buffer, (offset + len) - 3, offset + len)) {
            throw new ParseException("Could not parse as a well-formed CDATA Section: \"" + new String(buffer, offset, len) + "\"", line, col);
        }
        int contentOffset = offset + 9;
        int contentLen = len - 12;
        handler.handleCDATASection(buffer, contentOffset, contentLen, offset, len, line, col);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isCDATASectionStart(char[] buffer, int offset, int maxi) {
        return maxi - offset > 8 && buffer[offset] == '<' && buffer[offset + 1] == '!' && buffer[offset + 2] == '[' && (buffer[offset + 3] == 'C' || buffer[offset + 3] == 'c') && ((buffer[offset + 4] == 'D' || buffer[offset + 4] == 'd') && ((buffer[offset + 5] == 'A' || buffer[offset + 5] == 'a') && ((buffer[offset + 6] == 'T' || buffer[offset + 6] == 't') && ((buffer[offset + 7] == 'A' || buffer[offset + 7] == 'a') && buffer[offset + 8] == '['))));
    }

    static boolean isCDATASectionEnd(char[] buffer, int offset, int maxi) {
        return maxi - offset > 2 && buffer[offset] == ']' && buffer[offset + 1] == ']' && buffer[offset + 2] == '>';
    }
}