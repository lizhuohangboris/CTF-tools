package org.thymeleaf.templateparser.text;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/text/TextParsingUtil.class */
final class TextParsingUtil {
    private TextParsingUtil() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int findNextStructureEndAvoidQuotes(char[] text, int offset, int maxi, int[] locator) {
        boolean inQuotes = false;
        boolean inApos = false;
        int colIndex = offset;
        int i = offset;
        int n = maxi - offset;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c = text[i];
                if (c == '\n') {
                    colIndex = i;
                    locator[1] = 0;
                    locator[0] = locator[0] + 1;
                } else if (c == '\"' && !inApos) {
                    inQuotes = !inQuotes;
                } else if (c == '\'' && !inQuotes) {
                    inApos = !inApos;
                } else if (c == ']' && !inQuotes && !inApos) {
                    locator[1] = locator[1] + (i - colIndex);
                    return i;
                }
                i++;
            } else {
                locator[1] = locator[1] + (maxi - colIndex);
                return -1;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int findNextCommentBlockEnd(char[] text, int offset, int maxi, int[] locator) {
        int colIndex = offset;
        int i = offset;
        int n = maxi - offset;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c = text[i];
                if (c == '\n') {
                    colIndex = i;
                    locator[1] = 0;
                    locator[0] = locator[0] + 1;
                } else if (i > offset && c == '/' && text[i - 1] == '*') {
                    locator[1] = locator[1] + (i - colIndex);
                    return i;
                }
                i++;
            } else {
                locator[1] = locator[1] + (maxi - colIndex);
                return -1;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int findNextCommentLineEnd(char[] text, int offset, int maxi, int[] locator) {
        int i = offset;
        int n = maxi - offset;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c = text[i];
                if (c == '\n') {
                    locator[1] = locator[1] + (i - offset);
                    return i;
                }
                i++;
            } else {
                locator[1] = locator[1] + (maxi - offset);
                return -1;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int findNextLiteralEnd(char[] text, int offset, int maxi, int[] locator, char literalMarker) {
        int colIndex = offset;
        int i = offset;
        int n = maxi - offset;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c = text[i];
                if (c == '\n') {
                    colIndex = i;
                    locator[1] = 0;
                    locator[0] = locator[0] + 1;
                } else if (i > offset && c == literalMarker && isLiteralDelimiter(text, offset, i)) {
                    locator[1] = locator[1] + (i - colIndex);
                    return i;
                }
                i++;
            } else {
                locator[1] = locator[1] + (maxi - colIndex);
                return -1;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int findNextStructureStartOrLiteralMarker(char[] text, int offset, int maxi, int[] locator, boolean processCommentsAndLiterals) {
        int colIndex = offset;
        int i = offset;
        int n = maxi - offset;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c = text[i];
                if (c == '\n') {
                    colIndex = i;
                    locator[1] = 0;
                    locator[0] = locator[0] + 1;
                } else if (c == '[') {
                    locator[1] = locator[1] + (i - colIndex);
                    return i;
                } else if (!processCommentsAndLiterals) {
                    continue;
                } else if (c == '/') {
                    locator[1] = locator[1] + (i - colIndex);
                    return i;
                } else if ((c == '\'' || c == '\"' || c == '`') && isLiteralDelimiter(text, offset, i)) {
                    locator[1] = locator[1] + (i - colIndex);
                    return i;
                }
                i++;
            } else {
                locator[1] = locator[1] + (maxi - colIndex);
                return -1;
            }
        }
    }

    private static boolean isLiteralDelimiter(char[] text, int offset, int i) {
        int escapes = 0;
        int j = i - 1;
        while (j >= offset) {
            int i2 = j;
            j--;
            if (text[i2] != '\\') {
                break;
            }
            escapes++;
        }
        return escapes % 2 == 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int findNextWhitespaceCharWildcard(char[] text, int offset, int maxi, boolean avoidQuotes, int[] locator) {
        boolean inQuotes = false;
        boolean inApos = false;
        int i = offset;
        int n = maxi - offset;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c = text[i];
                if (avoidQuotes && !inApos && c == '\"') {
                    inQuotes = !inQuotes;
                } else if (avoidQuotes && !inQuotes && c == '\'') {
                    inApos = !inApos;
                } else if (!inQuotes && !inApos && (c == ' ' || c == '\n' || c == '\t' || c == '\r' || c == '\f' || c == 11 || c == 28 || c == 29 || c == 30 || c == 31 || (c > 127 && Character.isWhitespace(c)))) {
                    break;
                }
                ParsingLocatorUtil.countChar(locator, c);
                i++;
            } else {
                return -1;
            }
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int findNextNonWhitespaceCharWildcard(char[] text, int offset, int maxi, int[] locator) {
        int i = offset;
        int n = maxi - offset;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c = text[i];
                boolean isWhitespace = c == ' ' || c == '\n' || c == '\t' || c == '\r' || c == '\f' || c == 11 || c == 28 || c == 29 || c == 30 || c == 31 || (c > 127 && Character.isWhitespace(c));
                if (!isWhitespace) {
                    return i;
                }
                ParsingLocatorUtil.countChar(locator, c);
                i++;
            } else {
                return -1;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int findNextOperatorCharWildcard(char[] text, int offset, int maxi, int[] locator) {
        int i = offset;
        int n = maxi - offset;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c = text[i];
                if (c == '=' || c == ' ' || c == '\n' || c == '\t' || c == '\r' || c == '\f' || c == 11 || c == 28 || c == 29 || c == 30 || c == 31 || (c > 127 && Character.isWhitespace(c))) {
                    break;
                }
                ParsingLocatorUtil.countChar(locator, c);
                i++;
            } else {
                return -1;
            }
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int findNextNonOperatorCharWildcard(char[] text, int offset, int maxi, int[] locator) {
        int i = offset;
        int n = maxi - offset;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c = text[i];
                if (c == '=' || c == ' ' || c == '\n' || c == '\t' || c == '\r' || c == '\f' || c == 11 || c == 28 || c == 29 || c == 30 || c == 31 || (c > 127 && Character.isWhitespace(c))) {
                    ParsingLocatorUtil.countChar(locator, c);
                    i++;
                }
            } else {
                return -1;
            }
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int findNextAnyCharAvoidQuotesWildcard(char[] text, int offset, int maxi, int[] locator) {
        boolean inQuotes = false;
        boolean inApos = false;
        int i = offset;
        int n = maxi - offset;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c = text[i];
                if (!inApos && c == '\"') {
                    if (inQuotes) {
                        ParsingLocatorUtil.countChar(locator, c);
                        int i3 = i + 1;
                        if (i3 < maxi) {
                            return i3;
                        }
                        return -1;
                    }
                    inQuotes = true;
                } else if (!inQuotes && c == '\'') {
                    if (inApos) {
                        ParsingLocatorUtil.countChar(locator, c);
                        int i4 = i + 1;
                        if (i4 < maxi) {
                            return i4;
                        }
                        return -1;
                    }
                    inApos = true;
                } else if (!inQuotes && !inApos) {
                    return i;
                }
                ParsingLocatorUtil.countChar(locator, c);
                i++;
            } else {
                return -1;
            }
        }
    }
}