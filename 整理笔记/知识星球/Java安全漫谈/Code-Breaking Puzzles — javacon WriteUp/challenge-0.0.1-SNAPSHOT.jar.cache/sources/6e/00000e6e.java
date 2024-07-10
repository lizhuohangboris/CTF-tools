package org.attoparser;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/ParsingProcessingInstructionUtil.class */
public final class ParsingProcessingInstructionUtil {
    private ParsingProcessingInstructionUtil() {
    }

    public static void parseProcessingInstruction(char[] buffer, int offset, int len, int line, int col, IProcessingInstructionHandler handler) throws ParseException {
        if (len < 4 || !isProcessingInstructionStart(buffer, offset, offset + len) || !isProcessingInstructionEnd(buffer, (offset + len) - 2, offset + len)) {
            throw new ParseException("Could not parse as a well-formed Processing Instruction: \"" + new String(buffer, offset, len) + "\"", line, col);
        }
        int contentOffset = offset + 2;
        int contentLen = len - 4;
        int maxi = contentOffset + contentLen;
        int[] locator = {line, col + 2};
        int targetEnd = ParsingMarkupUtil.findNextWhitespaceCharWildcard(buffer, contentOffset, maxi, false, locator);
        if (targetEnd == -1) {
            handler.handleProcessingInstruction(buffer, contentOffset, maxi - contentOffset, line, col + 2, 0, 0, locator[0], locator[1], offset, len, line, col);
            return;
        }
        int targetLen = targetEnd - contentOffset;
        int contentStart = ParsingMarkupUtil.findNextNonWhitespaceCharWildcard(buffer, targetEnd, maxi, locator);
        if (contentStart == -1) {
            handler.handleProcessingInstruction(buffer, contentOffset, targetLen, line, col + 2, 0, 0, locator[0], locator[1], offset, len, line, col);
        } else {
            handler.handleProcessingInstruction(buffer, contentOffset, targetLen, line, col + 2, contentStart, maxi - contentStart, locator[0], locator[1], offset, len, line, col);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isProcessingInstructionStart(char[] buffer, int offset, int maxi) {
        int len = maxi - offset;
        return len > 5 ? (buffer[offset] != '<' || buffer[offset + 1] != '?' || buffer[offset + 2] == ' ' || Character.isWhitespace(buffer[offset + 2]) || (buffer[offset + 2] == 'x' && buffer[offset + 3] == 'm' && buffer[offset + 4] == 'l' && Character.isWhitespace(buffer[offset + 5]))) ? false : true : len > 2 && buffer[offset] == '<' && buffer[offset + 1] == '?' && buffer[offset + 2] != ' ' && !Character.isWhitespace(buffer[offset + 2]);
    }

    static boolean isProcessingInstructionEnd(char[] buffer, int offset, int maxi) {
        return maxi - offset > 1 && buffer[offset] == '?' && buffer[offset + 1] == '>';
    }
}