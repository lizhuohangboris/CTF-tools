package org.unbescape.css;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/css/CssIdentifierEscapeUtil.class */
final class CssIdentifierEscapeUtil {
    private static final char ESCAPE_PREFIX = '\\';
    private static char[] HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
    private static int BACKSLASH_CHARS_LEN = 127;
    private static char BACKSLASH_CHARS_NO_ESCAPE = 0;
    private static char[] BACKSLASH_CHARS = new char[BACKSLASH_CHARS_LEN];
    private static final char ESCAPE_LEVELS_LEN = 161;
    private static final byte[] ESCAPE_LEVELS;

    static {
        Arrays.fill(BACKSLASH_CHARS, BACKSLASH_CHARS_NO_ESCAPE);
        BACKSLASH_CHARS[32] = ' ';
        BACKSLASH_CHARS[33] = '!';
        BACKSLASH_CHARS[34] = '\"';
        BACKSLASH_CHARS[35] = '#';
        BACKSLASH_CHARS[36] = '$';
        BACKSLASH_CHARS[37] = '%';
        BACKSLASH_CHARS[38] = '&';
        BACKSLASH_CHARS[39] = '\'';
        BACKSLASH_CHARS[40] = '(';
        BACKSLASH_CHARS[41] = ')';
        BACKSLASH_CHARS[42] = '*';
        BACKSLASH_CHARS[43] = '+';
        BACKSLASH_CHARS[44] = ',';
        BACKSLASH_CHARS[45] = '-';
        BACKSLASH_CHARS[46] = '.';
        BACKSLASH_CHARS[47] = '/';
        BACKSLASH_CHARS[59] = ';';
        BACKSLASH_CHARS[60] = '<';
        BACKSLASH_CHARS[61] = '=';
        BACKSLASH_CHARS[62] = '>';
        BACKSLASH_CHARS[63] = '?';
        BACKSLASH_CHARS[64] = '@';
        BACKSLASH_CHARS[91] = '[';
        BACKSLASH_CHARS[92] = '\\';
        BACKSLASH_CHARS[93] = ']';
        BACKSLASH_CHARS[94] = '^';
        BACKSLASH_CHARS[95] = '_';
        BACKSLASH_CHARS[96] = '`';
        BACKSLASH_CHARS[123] = '{';
        BACKSLASH_CHARS[124] = '|';
        BACKSLASH_CHARS[125] = '}';
        BACKSLASH_CHARS[126] = '~';
        ESCAPE_LEVELS = new byte[161];
        Arrays.fill(ESCAPE_LEVELS, (byte) 3);
        char c = 128;
        while (true) {
            char c2 = c;
            if (c2 >= 161) {
                break;
            }
            ESCAPE_LEVELS[c2] = 2;
            c = (char) (c2 + 1);
        }
        char c3 = 'A';
        while (true) {
            char c4 = c3;
            if (c4 > 'Z') {
                break;
            }
            ESCAPE_LEVELS[c4] = 4;
            c3 = (char) (c4 + 1);
        }
        char c5 = 'a';
        while (true) {
            char c6 = c5;
            if (c6 > 'z') {
                break;
            }
            ESCAPE_LEVELS[c6] = 4;
            c5 = (char) (c6 + 1);
        }
        char c7 = '0';
        while (true) {
            char c8 = c7;
            if (c8 > '9') {
                break;
            }
            ESCAPE_LEVELS[c8] = 4;
            c7 = (char) (c8 + 1);
        }
        ESCAPE_LEVELS[32] = 1;
        ESCAPE_LEVELS[33] = 1;
        ESCAPE_LEVELS[34] = 1;
        ESCAPE_LEVELS[35] = 1;
        ESCAPE_LEVELS[36] = 1;
        ESCAPE_LEVELS[37] = 1;
        ESCAPE_LEVELS[38] = 1;
        ESCAPE_LEVELS[39] = 1;
        ESCAPE_LEVELS[40] = 1;
        ESCAPE_LEVELS[41] = 1;
        ESCAPE_LEVELS[42] = 1;
        ESCAPE_LEVELS[43] = 1;
        ESCAPE_LEVELS[44] = 1;
        ESCAPE_LEVELS[45] = 1;
        ESCAPE_LEVELS[46] = 1;
        ESCAPE_LEVELS[47] = 1;
        ESCAPE_LEVELS[58] = 1;
        ESCAPE_LEVELS[59] = 1;
        ESCAPE_LEVELS[60] = 1;
        ESCAPE_LEVELS[61] = 1;
        ESCAPE_LEVELS[62] = 1;
        ESCAPE_LEVELS[63] = 1;
        ESCAPE_LEVELS[64] = 1;
        ESCAPE_LEVELS[91] = 1;
        ESCAPE_LEVELS[92] = 1;
        ESCAPE_LEVELS[93] = 1;
        ESCAPE_LEVELS[94] = 1;
        ESCAPE_LEVELS[95] = 1;
        ESCAPE_LEVELS[96] = 1;
        ESCAPE_LEVELS[123] = 1;
        ESCAPE_LEVELS[124] = 1;
        ESCAPE_LEVELS[125] = 1;
        ESCAPE_LEVELS[126] = 1;
        char c9 = 0;
        while (true) {
            char c10 = c9;
            if (c10 > 31) {
                break;
            }
            ESCAPE_LEVELS[c10] = 1;
            c9 = (char) (c10 + 1);
        }
        char c11 = 127;
        while (true) {
            char c12 = c11;
            if (c12 <= 159) {
                ESCAPE_LEVELS[c12] = 1;
                c11 = (char) (c12 + 1);
            } else {
                return;
            }
        }
    }

    private CssIdentifierEscapeUtil() {
    }

    static char[] toCompactHexa(int codepoint, char next, int level) {
        boolean needTrailingSpace = level < 4 && ((next >= '0' && next <= '9') || ((next >= 'A' && next <= 'F') || (next >= 'a' && next <= 'f')));
        if (codepoint == 0) {
            return needTrailingSpace ? new char[]{'0', ' '} : new char[]{'0'};
        }
        char[] result = null;
        for (int div = 20; result == null && div >= 0; div -= 4) {
            if ((codepoint >>> div) % 16 > 0) {
                result = new char[(div / 4) + (needTrailingSpace ? 2 : 1)];
            }
        }
        int div2 = 0;
        for (int i = needTrailingSpace ? result.length - 2 : result.length - 1; i >= 0; i--) {
            result[i] = HEXA_CHARS_UPPER[(codepoint >>> div2) % 16];
            div2 += 4;
        }
        if (needTrailingSpace) {
            result[result.length - 1] = ' ';
        }
        return result;
    }

    static char[] toSixDigitHexa(int codepoint, char next, int level) {
        char[] result = {HEXA_CHARS_UPPER[(codepoint >>> 20) % 16], HEXA_CHARS_UPPER[(codepoint >>> 16) % 16], HEXA_CHARS_UPPER[(codepoint >>> 12) % 16], HEXA_CHARS_UPPER[(codepoint >>> 8) % 16], HEXA_CHARS_UPPER[(codepoint >>> 4) % 16], HEXA_CHARS_UPPER[codepoint % 16]};
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String escape(String text, CssIdentifierEscapeType escapeType, CssIdentifierEscapeLevel escapeLevel) {
        char escape;
        char c1;
        if (text == null) {
            return null;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useBackslashEscapes = escapeType.getUseBackslashEscapes();
        boolean useCompactHexa = escapeType.getUseCompactHexa();
        StringBuilder strBuilder = null;
        int max = text.length();
        int readOffset = 0;
        int i = 0;
        while (i < max) {
            int codepoint = Character.codePointAt(text, i);
            if ((codepoint > 159 || level >= ESCAPE_LEVELS[codepoint] || (i <= 0 && codepoint >= 48 && codepoint <= 57)) && ((codepoint != 45 || level >= 3 || (i <= 0 && i + 1 < max && ((c1 = text.charAt(i + 1)) == '-' || (c1 >= '0' && c1 <= '9')))) && (codepoint != 95 || level >= 3 || i <= 0))) {
                if (codepoint > 159 && level < ESCAPE_LEVELS[160]) {
                    if (Character.charCount(codepoint) > 1) {
                        i++;
                    }
                } else {
                    if (strBuilder == null) {
                        strBuilder = new StringBuilder(max + 20);
                    }
                    if (i - readOffset > 0) {
                        strBuilder.append((CharSequence) text, readOffset, i);
                    }
                    if (Character.charCount(codepoint) > 1) {
                        i++;
                    }
                    readOffset = i + 1;
                    if (useBackslashEscapes && codepoint < BACKSLASH_CHARS_LEN && (escape = BACKSLASH_CHARS[codepoint]) != BACKSLASH_CHARS_NO_ESCAPE) {
                        strBuilder.append('\\');
                        strBuilder.append(escape);
                    } else {
                        char next = i + 1 < max ? text.charAt(i + 1) : (char) 0;
                        if (useCompactHexa) {
                            strBuilder.append('\\');
                            strBuilder.append(toCompactHexa(codepoint, next, level));
                        } else {
                            strBuilder.append('\\');
                            strBuilder.append(toSixDigitHexa(codepoint, next, level));
                        }
                    }
                }
            }
            i++;
        }
        if (strBuilder == null) {
            return text;
        }
        if (max - readOffset > 0) {
            strBuilder.append((CharSequence) text, readOffset, max);
        }
        return strBuilder.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void escape(Reader reader, Writer writer, CssIdentifierEscapeType escapeType, CssIdentifierEscapeLevel escapeLevel) throws IOException {
        char escape;
        if (reader == null) {
            return;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useBackslashEscapes = escapeType.getUseBackslashEscapes();
        boolean useCompactHexa = escapeType.getUseCompactHexa();
        int c1 = -1;
        int c2 = reader.read();
        while (c2 >= 0) {
            int c0 = c1;
            c1 = c2;
            c2 = reader.read();
            int codepoint = codePointAt((char) c1, (char) c2);
            if (codepoint <= 159 && level < ESCAPE_LEVELS[codepoint] && (c0 >= 0 || codepoint < 48 || codepoint > 57)) {
                writer.write(c1);
            } else {
                if (codepoint == 45 && level < 3) {
                    if (c0 >= 0 || c2 < 0) {
                        writer.write(c1);
                    } else if (c2 != 45 && (c2 < 48 || c2 > 57)) {
                        writer.write(c1);
                    }
                }
                if (codepoint == 95 && level < 3 && c0 >= 0) {
                    writer.write(c1);
                } else if (codepoint > 159 && level < ESCAPE_LEVELS[160]) {
                    writer.write(c1);
                    if (Character.charCount(codepoint) > 1) {
                        writer.write(c2);
                        c1 = c2;
                        c2 = reader.read();
                    }
                } else {
                    if (Character.charCount(codepoint) > 1) {
                        c1 = c2;
                        c2 = reader.read();
                    }
                    if (useBackslashEscapes && codepoint < BACKSLASH_CHARS_LEN && (escape = BACKSLASH_CHARS[codepoint]) != BACKSLASH_CHARS_NO_ESCAPE) {
                        writer.write(92);
                        writer.write(escape);
                    } else {
                        char next = c2 >= 0 ? (char) c2 : (char) 0;
                        if (useCompactHexa) {
                            writer.write(92);
                            writer.write(toCompactHexa(codepoint, next, level));
                        } else {
                            writer.write(92);
                            writer.write(toSixDigitHexa(codepoint, next, level));
                        }
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void escape(char[] text, int offset, int len, Writer writer, CssIdentifierEscapeType escapeType, CssIdentifierEscapeLevel escapeLevel) throws IOException {
        char escape;
        char c1;
        if (text == null || text.length == 0) {
            return;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useBackslashEscapes = escapeType.getUseBackslashEscapes();
        boolean useCompactHexa = escapeType.getUseCompactHexa();
        int max = offset + len;
        int readOffset = offset;
        int i = offset;
        while (i < max) {
            int codepoint = Character.codePointAt(text, i);
            if ((codepoint > 159 || level >= ESCAPE_LEVELS[codepoint] || (i <= offset && codepoint >= 48 && codepoint <= 57)) && ((codepoint != 45 || level >= 3 || (i <= offset && i + 1 < max && ((c1 = text[i + 1]) == '-' || (c1 >= '0' && c1 <= '9')))) && (codepoint != 95 || level >= 3 || i <= offset))) {
                if (codepoint > 159 && level < ESCAPE_LEVELS[160]) {
                    if (Character.charCount(codepoint) > 1) {
                        i++;
                    }
                } else {
                    if (i - readOffset > 0) {
                        writer.write(text, readOffset, i - readOffset);
                    }
                    if (Character.charCount(codepoint) > 1) {
                        i++;
                    }
                    readOffset = i + 1;
                    if (useBackslashEscapes && codepoint < BACKSLASH_CHARS_LEN && (escape = BACKSLASH_CHARS[codepoint]) != BACKSLASH_CHARS_NO_ESCAPE) {
                        writer.write(92);
                        writer.write(escape);
                    } else {
                        char next = i + 1 < max ? text[i + 1] : (char) 0;
                        if (useCompactHexa) {
                            writer.write(92);
                            writer.write(toCompactHexa(codepoint, next, level));
                        } else {
                            writer.write(92);
                            writer.write(toSixDigitHexa(codepoint, next, level));
                        }
                    }
                }
            }
            i++;
        }
        if (max - readOffset > 0) {
            writer.write(text, readOffset, max - readOffset);
        }
    }

    private static int codePointAt(char c1, char c2) {
        if (Character.isHighSurrogate(c1) && c2 >= 0 && Character.isLowSurrogate(c2)) {
            return Character.toCodePoint(c1, c2);
        }
        return c1;
    }
}