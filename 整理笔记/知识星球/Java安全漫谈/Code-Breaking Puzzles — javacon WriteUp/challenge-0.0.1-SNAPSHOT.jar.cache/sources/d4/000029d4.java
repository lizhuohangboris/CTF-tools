package org.thymeleaf.templateparser.text;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/text/TextParsingAttributeSequenceUtil.class */
final class TextParsingAttributeSequenceUtil {
    private TextParsingAttributeSequenceUtil() {
    }

    public static void parseAttributeSequence(char[] buffer, int offset, int len, int line, int col, ITextHandler handler) throws TextParseException {
        int findNextWhitespaceCharWildcard;
        int maxi = offset + len;
        int[] locator = {line, col};
        int i = offset;
        int current = i;
        while (i < maxi) {
            int wsEnd = TextParsingUtil.findNextNonWhitespaceCharWildcard(buffer, i, maxi, locator);
            if (wsEnd == -1) {
                i = maxi;
            } else {
                if (wsEnd > current) {
                    i = wsEnd;
                    current = i;
                }
                int currentArtifactLine = locator[0];
                int currentArtifactCol = locator[1];
                int attributeNameEnd = TextParsingUtil.findNextOperatorCharWildcard(buffer, i, maxi, locator);
                if (attributeNameEnd == -1) {
                    handler.handleAttribute(buffer, current, maxi - current, currentArtifactLine, currentArtifactCol, 0, 0, locator[0], locator[1], 0, 0, 0, 0, locator[0], locator[1]);
                    i = maxi;
                } else if (attributeNameEnd <= current) {
                    throw new TextParseException("Bad attribute name in sequence \"" + new String(buffer, offset, len) + "\": attribute names cannot start with an equals sign", currentArtifactLine, currentArtifactCol);
                } else {
                    int attributeNameOffset = current;
                    int attributeNameLen = attributeNameEnd - current;
                    current = attributeNameEnd;
                    int currentArtifactLine2 = locator[0];
                    int currentArtifactCol2 = locator[1];
                    int operatorEnd = TextParsingUtil.findNextNonOperatorCharWildcard(buffer, attributeNameEnd, maxi, locator);
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
                            handler.handleAttribute(buffer, attributeNameOffset, attributeNameLen, currentArtifactLine, currentArtifactCol, current, maxi - current, currentArtifactLine2, currentArtifactCol2, 0, 0, 0, 0, locator[0], locator[1]);
                        } else {
                            handler.handleAttribute(buffer, attributeNameOffset, attributeNameLen, currentArtifactLine, currentArtifactCol, 0, 0, currentArtifactLine2, currentArtifactCol2, 0, 0, 0, 0, currentArtifactLine2, currentArtifactCol2);
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
                            handler.handleAttribute(buffer, attributeNameOffset, attributeNameLen, currentArtifactLine, currentArtifactCol, 0, 0, currentArtifactLine2, currentArtifactCol2, 0, 0, 0, 0, currentArtifactLine2, currentArtifactCol2);
                            i = operatorEnd;
                            current = i;
                        } else {
                            int operatorLen = operatorEnd - current;
                            current = operatorEnd;
                            int currentArtifactLine3 = locator[0];
                            int currentArtifactCol3 = locator[1];
                            boolean attributeEndsWithQuotes = operatorEnd < maxi && (buffer[current] == '\"' || buffer[current] == '\'');
                            if (attributeEndsWithQuotes) {
                                findNextWhitespaceCharWildcard = TextParsingUtil.findNextAnyCharAvoidQuotesWildcard(buffer, operatorEnd, maxi, locator);
                            } else {
                                findNextWhitespaceCharWildcard = TextParsingUtil.findNextWhitespaceCharWildcard(buffer, operatorEnd, maxi, false, locator);
                            }
                            int valueEnd = findNextWhitespaceCharWildcard;
                            if (valueEnd == -1) {
                                int valueContentOffset = current;
                                int valueContentLen = maxi - current;
                                if (isValueSurroundedByCommas(buffer, current, maxi - current)) {
                                    valueContentOffset++;
                                    valueContentLen -= 2;
                                }
                                handler.handleAttribute(buffer, attributeNameOffset, attributeNameLen, currentArtifactLine, currentArtifactCol, current, operatorLen, currentArtifactLine2, currentArtifactCol2, valueContentOffset, valueContentLen, current, maxi - current, currentArtifactLine3, currentArtifactCol3);
                                i = maxi;
                            } else {
                                int valueOuterLen = valueEnd - current;
                                int valueContentOffset2 = current;
                                int valueContentLen2 = valueOuterLen;
                                if (isValueSurroundedByCommas(buffer, current, valueOuterLen)) {
                                    valueContentOffset2 = current + 1;
                                    valueContentLen2 = valueOuterLen - 2;
                                }
                                handler.handleAttribute(buffer, attributeNameOffset, attributeNameLen, currentArtifactLine, currentArtifactCol, current, operatorLen, currentArtifactLine2, currentArtifactCol2, valueContentOffset2, valueContentLen2, current, valueOuterLen, currentArtifactLine3, currentArtifactCol3);
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