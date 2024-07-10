package org.attoparser;

import org.thymeleaf.engine.DocType;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/ParsingDocTypeMarkupUtil.class */
public final class ParsingDocTypeMarkupUtil {
    private static final char[] DOCTYPE_TYPE_PUBLIC_UPPER = DocType.DEFAULT_TYPE_PUBLIC.toCharArray();
    private static final char[] DOCTYPE_TYPE_PUBLIC_LOWER = "public".toCharArray();
    private static final char[] DOCTYPE_TYPE_SYSTEM_UPPER = DocType.DEFAULT_TYPE_SYSTEM.toCharArray();
    private static final char[] DOCTYPE_TYPE_SYSTEM_LOWER = "system".toCharArray();

    private ParsingDocTypeMarkupUtil() {
    }

    public static void parseDocType(char[] buffer, int offset, int len, int line, int col, IDocTypeHandler handler) throws ParseException {
        if (len < 10 || !isDocTypeStart(buffer, offset, offset + len) || !isDocTypeEnd(buffer, (offset + len) - 1, offset + len)) {
            throw new ParseException("Could not parse as a well-formed DOCTYPE clause: \"" + new String(buffer, offset, len) + "\"", line, col);
        }
        int contentOffset = offset + 2;
        int contentLen = len - 3;
        int internalSubsetLastChar = findInternalSubsetEndChar(buffer, contentOffset, contentLen);
        if (internalSubsetLastChar == -1) {
            doParseDetailedDocTypeWithInternalSubset(buffer, contentOffset, contentLen, offset, len, line, col, 0, 0, 0, 0, handler);
            return;
        }
        int maxi = contentOffset + contentLen;
        int[] locator = {line, col + 2};
        int internalSubsetStart = findInternalSubsetStartCharWildcard(buffer, contentOffset, maxi, locator);
        if (internalSubsetStart == -1) {
            throw new ParseException("Could not parse as a well-formed DOCTYPE clause: \"" + new String(buffer, offset, len) + "\"", line, col);
        }
        doParseDetailedDocTypeWithInternalSubset(buffer, contentOffset, internalSubsetStart - contentOffset, offset, len, line, col, internalSubsetStart + 1, (internalSubsetLastChar - internalSubsetStart) - 1, locator[0], locator[1], handler);
    }

    private static void doParseDetailedDocTypeWithInternalSubset(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col, int internalSubsetOffset, int internalSubsetLen, int internalSubsetLine, int internalSubsetCol, IDocTypeHandler handler) throws ParseException {
        int maxi = contentOffset + contentLen;
        int[] locator = {line, col + 2};
        int keywordEnd = ParsingMarkupUtil.findNextWhitespaceCharWildcard(buffer, contentOffset, maxi, false, locator);
        if (keywordEnd == -1) {
            handler.handleDocType(buffer, contentOffset, maxi - contentOffset, line, col + 2, 0, 0, locator[0], locator[1], 0, 0, locator[0], locator[1], 0, 0, locator[0], locator[1], 0, 0, locator[0], locator[1], internalSubsetOffset, internalSubsetLen, Math.max(locator[0], internalSubsetLine), Math.max(locator[1], internalSubsetCol), outerOffset, outerLen, line, col);
            return;
        }
        int keywordLen = keywordEnd - contentOffset;
        int keywordCol = col + 2;
        int currentDocTypeLine = locator[0];
        int currentDocTypeCol = locator[1];
        int elementNameStart = ParsingMarkupUtil.findNextNonWhitespaceCharWildcard(buffer, keywordEnd, maxi, locator);
        if (elementNameStart == -1) {
            handler.handleDocType(buffer, contentOffset, keywordLen, line, keywordCol, 0, 0, currentDocTypeLine, currentDocTypeCol, 0, 0, currentDocTypeLine, currentDocTypeCol, 0, 0, currentDocTypeLine, currentDocTypeCol, 0, 0, currentDocTypeLine, currentDocTypeCol, internalSubsetOffset, internalSubsetLen, Math.max(currentDocTypeLine, internalSubsetLine), Math.max(currentDocTypeCol, internalSubsetCol), outerOffset, outerLen, line, col);
            return;
        }
        int currentDocTypeLine2 = locator[0];
        int currentDocTypeCol2 = locator[1];
        int elementNameEnd = ParsingMarkupUtil.findNextWhitespaceCharWildcard(buffer, elementNameStart, maxi, false, locator);
        if (elementNameEnd == -1) {
            handler.handleDocType(buffer, contentOffset, keywordLen, line, keywordCol, elementNameStart, maxi - elementNameStart, currentDocTypeLine2, currentDocTypeCol2, 0, 0, locator[0], locator[1], 0, 0, locator[0], locator[1], 0, 0, locator[0], locator[1], internalSubsetOffset, internalSubsetLen, Math.max(locator[0], internalSubsetLine), Math.max(locator[1], internalSubsetCol), outerOffset, outerLen, line, col);
            return;
        }
        int elementNameLen = elementNameEnd - elementNameStart;
        int i = locator[0];
        int i2 = locator[1];
        int typeStart = ParsingMarkupUtil.findNextNonWhitespaceCharWildcard(buffer, elementNameEnd, maxi, locator);
        if (typeStart == -1) {
            handler.handleDocType(buffer, contentOffset, keywordLen, line, keywordCol, elementNameStart, elementNameLen, currentDocTypeLine2, currentDocTypeCol2, 0, 0, locator[0], locator[1], 0, 0, locator[0], locator[1], 0, 0, locator[0], locator[1], internalSubsetOffset, internalSubsetLen, Math.max(locator[0], internalSubsetLine), Math.max(locator[1], internalSubsetCol), outerOffset, outerLen, line, col);
            return;
        }
        int currentDocTypeLine3 = locator[0];
        int currentDocTypeCol3 = locator[1];
        int typeEnd = ParsingMarkupUtil.findNextWhitespaceCharWildcard(buffer, typeStart, maxi, true, locator);
        if (typeEnd == -1) {
            throw new ParseException("Could not parse as a well-formed DOCTYPE clause \"" + new String(buffer, outerOffset, outerLen) + "\": If a type is specified (PUBLIC or SYSTEM), at least a public or a system ID has to be specified", line, col);
        }
        int typeLen = typeEnd - typeStart;
        if (!isValidDocTypeType(buffer, typeStart, typeLen)) {
            throw new ParseException("Could not parse as a well-formed DOCTYPE clause \"" + new String(buffer, outerOffset, outerLen) + "\": DOCTYPE type must be either \"PUBLIC\" or \"SYSTEM\"", line, col);
        }
        boolean isTypePublic = buffer[typeStart] == DOCTYPE_TYPE_PUBLIC_UPPER[0] || buffer[typeStart] == DOCTYPE_TYPE_PUBLIC_LOWER[0];
        int i3 = locator[0];
        int i4 = locator[1];
        int spec1Start = ParsingMarkupUtil.findNextNonWhitespaceCharWildcard(buffer, typeEnd, maxi, locator);
        if (spec1Start == -1) {
            throw new ParseException("Could not parse as a well-formed DOCTYPE clause \"" + new String(buffer, outerOffset, outerLen) + "\": If a type is specified (PUBLIC or SYSTEM), at least a public or a system ID has to be specified", line, col);
        }
        int currentDocTypeLine4 = locator[0];
        int currentDocTypeCol4 = locator[1];
        int spec1End = ParsingMarkupUtil.findNextWhitespaceCharWildcard(buffer, spec1Start, maxi, true, locator);
        if (spec1End == -1) {
            if (!isValidDocTypeSpec(buffer, spec1Start, maxi - spec1Start)) {
                throw new ParseException("Could not parse as a well-formed DOCTYPE clause \"" + new String(buffer, outerOffset, outerLen) + "\": Public and Systen IDs must be surrounded by quotes (\")", line, col);
            }
            if (isTypePublic) {
                handler.handleDocType(buffer, contentOffset, keywordLen, line, keywordCol, elementNameStart, elementNameLen, currentDocTypeLine2, currentDocTypeCol2, typeStart, typeLen, currentDocTypeLine3, currentDocTypeCol3, spec1Start + 1, maxi - (spec1Start + 2), currentDocTypeLine4, currentDocTypeCol4, 0, 0, locator[0], locator[1], internalSubsetOffset, internalSubsetLen, Math.max(locator[0], internalSubsetLine), Math.max(locator[1], internalSubsetCol), outerOffset, outerLen, line, col);
                return;
            } else {
                handler.handleDocType(buffer, contentOffset, keywordLen, line, keywordCol, elementNameStart, elementNameLen, currentDocTypeLine2, currentDocTypeCol2, typeStart, typeLen, currentDocTypeLine3, currentDocTypeCol3, 0, 0, currentDocTypeLine4, currentDocTypeCol4, spec1Start + 1, maxi - (spec1Start + 2), currentDocTypeLine4, currentDocTypeCol4, internalSubsetOffset, internalSubsetLen, Math.max(locator[0], internalSubsetLine), Math.max(locator[1], internalSubsetCol), outerOffset, outerLen, line, col);
                return;
            }
        }
        int spec1Len = spec1End - spec1Start;
        if (!isValidDocTypeSpec(buffer, spec1Start, spec1Len)) {
            throw new ParseException("Could not parse as a well-formed DOCTYPE clause \"" + new String(buffer, outerOffset, outerLen) + "\": Public and Systen IDs must be surrounded by quotes (\")", line, col);
        }
        int i5 = locator[0];
        int i6 = locator[1];
        int spec2Start = ParsingMarkupUtil.findNextNonWhitespaceCharWildcard(buffer, spec1End, maxi, locator);
        if (spec2Start == -1) {
            if (isTypePublic) {
                handler.handleDocType(buffer, contentOffset, keywordLen, line, keywordCol, elementNameStart, elementNameLen, currentDocTypeLine2, currentDocTypeCol2, typeStart, typeLen, currentDocTypeLine3, currentDocTypeCol3, spec1Start + 1, spec1Len - 2, currentDocTypeLine4, currentDocTypeCol4, 0, 0, locator[0], locator[1], internalSubsetOffset, internalSubsetLen, Math.max(locator[0], internalSubsetLine), Math.max(locator[1], internalSubsetCol), outerOffset, outerLen, line, col);
                return;
            } else {
                handler.handleDocType(buffer, contentOffset, keywordLen, line, keywordCol, elementNameStart, elementNameLen, currentDocTypeLine2, currentDocTypeCol2, typeStart, typeLen, currentDocTypeLine3, currentDocTypeCol3, 0, 0, currentDocTypeLine4, currentDocTypeCol4, spec1Start + 1, spec1Len - 2, currentDocTypeLine4, currentDocTypeCol4, internalSubsetOffset, internalSubsetLen, Math.max(locator[0], internalSubsetLine), Math.max(locator[1], internalSubsetCol), outerOffset, outerLen, line, col);
                return;
            }
        }
        int currentDocTypeLine5 = locator[0];
        int currentDocTypeCol5 = locator[1];
        int spec2End = ParsingMarkupUtil.findNextWhitespaceCharWildcard(buffer, spec2Start, maxi, true, locator);
        if (spec2End == -1) {
            if (!isValidDocTypeSpec(buffer, spec2Start, maxi - spec2Start)) {
                throw new ParseException("Could not parse as a well-formed DOCTYPE clause \"" + new String(buffer, outerOffset, outerLen) + "\": Public and Systen IDs must be surrounded by quotes (\")", line, col);
            }
            if (!isTypePublic) {
                throw new ParseException("Could not parse as a well-formed DOCTYPE clause \"" + new String(buffer, outerOffset, outerLen) + "\": type SYSTEM only allows specifying one element (a system ID)", line, col);
            }
            handler.handleDocType(buffer, contentOffset, keywordLen, line, keywordCol, elementNameStart, elementNameLen, currentDocTypeLine2, currentDocTypeCol2, typeStart, typeLen, currentDocTypeLine3, currentDocTypeCol3, spec1Start + 1, spec1Len - 2, currentDocTypeLine4, currentDocTypeCol4, spec2Start + 1, maxi - (spec2Start + 2), currentDocTypeLine5, currentDocTypeCol5, internalSubsetOffset, internalSubsetLen, Math.max(locator[0], internalSubsetLine), Math.max(locator[1], internalSubsetCol), outerOffset, outerLen, line, col);
            return;
        }
        int spec2Len = spec2End - spec2Start;
        if (!isValidDocTypeSpec(buffer, spec2Start, spec2Len)) {
            throw new ParseException("Could not parse as a well-formed DOCTYPE clause \"" + new String(buffer, outerOffset, outerLen) + "\": Public and Systen IDs must be surrounded by quotes (\")", line, col);
        }
        if (!isTypePublic) {
            throw new ParseException("Could not parse as a well-formed DOCTYPE clause \"" + new String(buffer, outerOffset, outerLen) + "\": type SYSTEM only allows specifying one element (a system ID)", line, col);
        }
        int i7 = locator[0];
        int i8 = locator[1];
        int clauseEndStart = ParsingMarkupUtil.findNextNonWhitespaceCharWildcard(buffer, spec2End, maxi, locator);
        if (clauseEndStart != -1) {
            throw new ParseException("Could not parse as a well-formed DOCTYPE clause \"" + new String(buffer, outerOffset, outerLen) + "\": More elements found than allowed", line, col);
        }
        handler.handleDocType(buffer, contentOffset, keywordLen, line, keywordCol, elementNameStart, elementNameLen, currentDocTypeLine2, currentDocTypeCol2, typeStart, typeLen, currentDocTypeLine3, currentDocTypeCol3, spec1Start + 1, spec1Len - 2, currentDocTypeLine4, currentDocTypeCol4, spec2Start + 1, spec2Len - 2, currentDocTypeLine5, currentDocTypeCol5, internalSubsetOffset, internalSubsetLen, Math.max(locator[0], internalSubsetLine), Math.max(locator[1], internalSubsetCol), outerOffset, outerLen, line, col);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isDocTypeStart(char[] buffer, int offset, int maxi) {
        return maxi - offset > 9 && buffer[offset] == '<' && buffer[offset + 1] == '!' && (buffer[offset + 2] == 'D' || buffer[offset + 2] == 'd') && ((buffer[offset + 3] == 'O' || buffer[offset + 3] == 'o') && ((buffer[offset + 4] == 'C' || buffer[offset + 4] == 'c') && ((buffer[offset + 5] == 'T' || buffer[offset + 5] == 't') && ((buffer[offset + 6] == 'Y' || buffer[offset + 6] == 'y') && ((buffer[offset + 7] == 'P' || buffer[offset + 7] == 'p') && ((buffer[offset + 8] == 'E' || buffer[offset + 8] == 'e') && (Character.isWhitespace(buffer[offset + 9]) || buffer[offset + 9] == '>')))))));
    }

    static boolean isDocTypeEnd(char[] buffer, int offset, int maxi) {
        return maxi - offset > 0 && buffer[offset] == '>';
    }

    private static boolean isValidDocTypeType(char[] buffer, int offset, int len) {
        if (len != 6) {
            return false;
        }
        if (buffer[offset] == DOCTYPE_TYPE_PUBLIC_UPPER[0] || buffer[offset] == DOCTYPE_TYPE_PUBLIC_LOWER[0]) {
            for (int i = 1; i < 6; i++) {
                if (buffer[offset + i] != DOCTYPE_TYPE_PUBLIC_UPPER[i] && buffer[offset + i] != DOCTYPE_TYPE_PUBLIC_LOWER[i]) {
                    return false;
                }
            }
            return true;
        } else if (buffer[offset] == DOCTYPE_TYPE_SYSTEM_UPPER[0] || buffer[offset] == DOCTYPE_TYPE_SYSTEM_LOWER[0]) {
            for (int i2 = 1; i2 < 6; i2++) {
                if (buffer[offset + i2] != DOCTYPE_TYPE_SYSTEM_UPPER[i2] && buffer[offset + i2] != DOCTYPE_TYPE_SYSTEM_LOWER[i2]) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private static boolean isValidDocTypeSpec(char[] buffer, int offset, int len) {
        return len >= 2 && ((buffer[offset] == '\"' && buffer[(offset + len) - 1] == '\"') || (buffer[offset] == '\'' && buffer[(offset + len) - 1] == '\''));
    }

    private static int findInternalSubsetEndChar(char[] buffer, int offset, int len) {
        int maxi = offset + len;
        for (int i = maxi - 1; i > offset; i--) {
            char c = buffer[i];
            if (!Character.isWhitespace(c)) {
                if (c == ']') {
                    return i;
                } else {
                    return -1;
                }
            }
        }
        return -1;
    }

    private static int findInternalSubsetStartCharWildcard(char[] text, int offset, int maxi, int[] locator) {
        boolean inQuotes = false;
        boolean inApos = false;
        for (int i = offset; i < maxi; i++) {
            char c = text[i];
            if (!inApos && c == '\"') {
                inQuotes = !inQuotes;
            } else if (!inQuotes && c == '\'') {
                inApos = !inApos;
            } else if (!inQuotes && !inApos && c == '[') {
                return i;
            }
            ParsingLocatorUtil.countChar(locator, c);
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int findNextDocTypeStructureEnd(char[] text, int offset, int maxi, int[] locator) {
        boolean inQuotes = false;
        boolean inApos = false;
        int bracketLevel = 0;
        for (int i = offset; i < maxi; i++) {
            char c = text[i];
            if (!inApos && c == '\"') {
                inQuotes = !inQuotes;
            } else if (!inQuotes && c == '\'') {
                inApos = !inApos;
            } else if (!inQuotes && !inApos && c == '[') {
                bracketLevel++;
            } else if (!inQuotes && !inApos && c == ']') {
                bracketLevel--;
            } else if (!inQuotes && !inApos && bracketLevel == 0 && c == '>') {
                return i;
            }
            ParsingLocatorUtil.countChar(locator, c);
        }
        if (bracketLevel != 0) {
            return -2;
        }
        return -1;
    }
}