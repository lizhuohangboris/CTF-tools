package org.attoparser;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/ParsingAttributeSequenceUtil.class */
public final class ParsingAttributeSequenceUtil {
    private ParsingAttributeSequenceUtil() {
    }

    public static void parseAttributeSequence(char[] buffer, int offset, int len, int line, int col, IAttributeSequenceHandler handler) throws ParseException {
        int findNextWhitespaceCharWildcard;
        int maxi = offset + len;
        int[] locator = {line, col};
        int i = offset;
        int current = i;
        while (i < maxi) {
            int currentArtifactLine = locator[0];
            int currentArtifactCol = locator[1];
            int wsEnd = ParsingMarkupUtil.findNextNonWhitespaceCharWildcard(buffer, i, maxi, locator);
            if (wsEnd == -1) {
                handler.handleInnerWhiteSpace(buffer, current, maxi - current, currentArtifactLine, currentArtifactCol);
                i = maxi;
            } else {
                if (wsEnd > current) {
                    int wsOffset = current;
                    int wsLen = wsEnd - current;
                    handler.handleInnerWhiteSpace(buffer, wsOffset, wsLen, currentArtifactLine, currentArtifactCol);
                    i = wsEnd;
                    current = i;
                }
                int currentArtifactLine2 = locator[0];
                int currentArtifactCol2 = locator[1];
                int attributeNameEnd = ParsingMarkupUtil.findNextOperatorCharWildcard(buffer, i, maxi, locator);
                if (attributeNameEnd == -1) {
                    handler.handleAttribute(buffer, current, maxi - current, currentArtifactLine2, currentArtifactCol2, 0, 0, locator[0], locator[1], 0, 0, 0, 0, locator[0], locator[1]);
                    i = maxi;
                } else if (attributeNameEnd <= current) {
                    throw new ParseException("Bad attribute name in sequence \"" + new String(buffer, offset, len) + "\": attribute names cannot start with an equals sign", currentArtifactLine2, currentArtifactCol2);
                } else {
                    int attributeNameOffset = current;
                    int attributeNameLen = attributeNameEnd - current;
                    current = attributeNameEnd;
                    int currentArtifactLine3 = locator[0];
                    int currentArtifactCol3 = locator[1];
                    int operatorEnd = ParsingMarkupUtil.findNextNonOperatorCharWildcard(buffer, attributeNameEnd, maxi, locator);
                    if (operatorEnd == -1) {
                        boolean equalsPresent = false;
                        int j = attributeNameEnd;
                        while (true) {
                            if (j >= maxi) {
                                break;
                            } else if (buffer[j] != '=') {
                                j++;
                            } else {
                                equalsPresent = true;
                                break;
                            }
                        }
                        if (equalsPresent) {
                            handler.handleAttribute(buffer, attributeNameOffset, attributeNameLen, currentArtifactLine2, currentArtifactCol2, current, maxi - current, currentArtifactLine3, currentArtifactCol3, 0, 0, 0, 0, locator[0], locator[1]);
                        } else {
                            handler.handleAttribute(buffer, attributeNameOffset, attributeNameLen, currentArtifactLine2, currentArtifactCol2, 0, 0, currentArtifactLine3, currentArtifactCol3, 0, 0, 0, 0, currentArtifactLine3, currentArtifactCol3);
                            handler.handleInnerWhiteSpace(buffer, current, maxi - current, currentArtifactLine3, currentArtifactCol3);
                        }
                        i = maxi;
                    } else {
                        boolean equalsPresent2 = false;
                        int j2 = current;
                        while (true) {
                            if (j2 >= operatorEnd) {
                                break;
                            } else if (buffer[j2] != '=') {
                                j2++;
                            } else {
                                equalsPresent2 = true;
                                break;
                            }
                        }
                        if (!equalsPresent2) {
                            handler.handleAttribute(buffer, attributeNameOffset, attributeNameLen, currentArtifactLine2, currentArtifactCol2, 0, 0, currentArtifactLine3, currentArtifactCol3, 0, 0, 0, 0, currentArtifactLine3, currentArtifactCol3);
                            handler.handleInnerWhiteSpace(buffer, current, operatorEnd - current, currentArtifactLine3, currentArtifactCol3);
                            i = operatorEnd;
                            current = i;
                        } else {
                            int operatorLen = operatorEnd - current;
                            current = operatorEnd;
                            int currentArtifactLine4 = locator[0];
                            int currentArtifactCol4 = locator[1];
                            boolean attributeEndsWithQuotes = operatorEnd < maxi && (buffer[current] == '\"' || buffer[current] == '\'');
                            if (attributeEndsWithQuotes) {
                                findNextWhitespaceCharWildcard = ParsingMarkupUtil.findNextAnyCharAvoidQuotesWildcard(buffer, operatorEnd, maxi, locator);
                            } else {
                                findNextWhitespaceCharWildcard = ParsingMarkupUtil.findNextWhitespaceCharWildcard(buffer, operatorEnd, maxi, false, locator);
                            }
                            int valueEnd = findNextWhitespaceCharWildcard;
                            if (valueEnd == -1) {
                                int valueContentOffset = current;
                                int valueContentLen = maxi - current;
                                if (isValueSurroundedByCommas(buffer, current, maxi - current)) {
                                    valueContentOffset++;
                                    valueContentLen -= 2;
                                }
                                handler.handleAttribute(buffer, attributeNameOffset, attributeNameLen, currentArtifactLine2, currentArtifactCol2, current, operatorLen, currentArtifactLine3, currentArtifactCol3, valueContentOffset, valueContentLen, current, maxi - current, currentArtifactLine4, currentArtifactCol4);
                                i = maxi;
                            } else {
                                int valueOuterLen = valueEnd - current;
                                int valueContentOffset2 = current;
                                int valueContentLen2 = valueOuterLen;
                                if (isValueSurroundedByCommas(buffer, current, valueOuterLen)) {
                                    valueContentOffset2 = current + 1;
                                    valueContentLen2 = valueOuterLen - 2;
                                }
                                handler.handleAttribute(buffer, attributeNameOffset, attributeNameLen, currentArtifactLine2, currentArtifactCol2, current, operatorLen, currentArtifactLine3, currentArtifactCol3, valueContentOffset2, valueContentLen2, current, valueOuterLen, currentArtifactLine4, currentArtifactCol4);
                                i = valueEnd;
                                current = i;
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean isValueSurroundedByCommas(char[] buffer, int offset, int len) {
        return len >= 2 && ((buffer[offset] == '\"' && buffer[(offset + len) - 1] == '\"') || (buffer[offset] == '\'' && buffer[(offset + len) - 1] == '\''));
    }
}