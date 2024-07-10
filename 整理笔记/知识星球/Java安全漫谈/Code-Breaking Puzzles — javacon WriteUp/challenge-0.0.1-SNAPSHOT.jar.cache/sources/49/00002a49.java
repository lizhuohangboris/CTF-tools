package org.unbescape.properties;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/properties/PropertiesKeyEscapeUtil.class */
final class PropertiesKeyEscapeUtil {
    private static final char ESCAPE_PREFIX = '\\';
    private static final char[] ESCAPE_UHEXA_PREFIX = "\\u".toCharArray();
    private static char[] HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
    private static int SEC_CHARS_LEN = 93;
    private static char SEC_CHARS_NO_SEC = '*';
    private static char[] SEC_CHARS = new char[SEC_CHARS_LEN];
    private static final char ESCAPE_LEVELS_LEN = 161;
    private static final byte[] ESCAPE_LEVELS;

    static {
        Arrays.fill(SEC_CHARS, SEC_CHARS_NO_SEC);
        SEC_CHARS[9] = 't';
        SEC_CHARS[10] = 'n';
        SEC_CHARS[12] = 'f';
        SEC_CHARS[13] = 'r';
        SEC_CHARS[32] = ' ';
        SEC_CHARS[58] = ':';
        SEC_CHARS[59] = '=';
        SEC_CHARS[92] = '\\';
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
        ESCAPE_LEVELS[9] = 1;
        ESCAPE_LEVELS[10] = 1;
        ESCAPE_LEVELS[12] = 1;
        ESCAPE_LEVELS[13] = 1;
        ESCAPE_LEVELS[32] = 1;
        ESCAPE_LEVELS[58] = 1;
        ESCAPE_LEVELS[59] = 1;
        ESCAPE_LEVELS[92] = 1;
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

    private PropertiesKeyEscapeUtil() {
    }

    static char[] toUHexa(int codepoint) {
        char[] result = {HEXA_CHARS_UPPER[(codepoint >>> 12) % 16], HEXA_CHARS_UPPER[(codepoint >>> 8) % 16], HEXA_CHARS_UPPER[(codepoint >>> 4) % 16], HEXA_CHARS_UPPER[codepoint % 16]};
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String escape(String text, PropertiesKeyEscapeLevel escapeLevel) {
        char sec;
        if (text == null) {
            return null;
        }
        int level = escapeLevel.getEscapeLevel();
        StringBuilder strBuilder = null;
        int max = text.length();
        int readOffset = 0;
        int i = 0;
        while (i < max) {
            int codepoint = Character.codePointAt(text, i);
            if (codepoint > 159 || level >= ESCAPE_LEVELS[codepoint]) {
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
                    if (codepoint < SEC_CHARS_LEN && (sec = SEC_CHARS[codepoint]) != SEC_CHARS_NO_SEC) {
                        strBuilder.append('\\');
                        strBuilder.append(sec);
                    } else if (Character.charCount(codepoint) > 1) {
                        char[] codepointChars = Character.toChars(codepoint);
                        strBuilder.append(ESCAPE_UHEXA_PREFIX);
                        strBuilder.append(toUHexa(codepointChars[0]));
                        strBuilder.append(ESCAPE_UHEXA_PREFIX);
                        strBuilder.append(toUHexa(codepointChars[1]));
                    } else {
                        strBuilder.append(ESCAPE_UHEXA_PREFIX);
                        strBuilder.append(toUHexa(codepoint));
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
    public static void escape(Reader reader, Writer writer, PropertiesKeyEscapeLevel escapeLevel) throws IOException {
        char sec;
        if (reader == null) {
            return;
        }
        int level = escapeLevel.getEscapeLevel();
        int c2 = reader.read();
        while (c2 >= 0) {
            int c1 = c2;
            c2 = reader.read();
            int codepoint = codePointAt((char) c1, (char) c2);
            if (codepoint <= 159 && level < ESCAPE_LEVELS[codepoint]) {
                writer.write(c1);
            } else if (codepoint > 159 && level < ESCAPE_LEVELS[160]) {
                writer.write(c1);
                if (Character.charCount(codepoint) > 1) {
                    writer.write(c2);
                    c2 = reader.read();
                }
            } else {
                if (Character.charCount(codepoint) > 1) {
                    c2 = reader.read();
                }
                if (codepoint < SEC_CHARS_LEN && (sec = SEC_CHARS[codepoint]) != SEC_CHARS_NO_SEC) {
                    writer.write(92);
                    writer.write(sec);
                } else if (Character.charCount(codepoint) > 1) {
                    char[] codepointChars = Character.toChars(codepoint);
                    writer.write(ESCAPE_UHEXA_PREFIX);
                    writer.write(toUHexa(codepointChars[0]));
                    writer.write(ESCAPE_UHEXA_PREFIX);
                    writer.write(toUHexa(codepointChars[1]));
                } else {
                    writer.write(ESCAPE_UHEXA_PREFIX);
                    writer.write(toUHexa(codepoint));
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void escape(char[] text, int offset, int len, Writer writer, PropertiesKeyEscapeLevel escapeLevel) throws IOException {
        char sec;
        if (text == null || text.length == 0) {
            return;
        }
        int level = escapeLevel.getEscapeLevel();
        int max = offset + len;
        int readOffset = offset;
        int i = offset;
        while (i < max) {
            int codepoint = Character.codePointAt(text, i);
            if (codepoint > 159 || level >= ESCAPE_LEVELS[codepoint]) {
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
                    if (codepoint < SEC_CHARS_LEN && (sec = SEC_CHARS[codepoint]) != SEC_CHARS_NO_SEC) {
                        writer.write(92);
                        writer.write(sec);
                    } else if (Character.charCount(codepoint) > 1) {
                        char[] codepointChars = Character.toChars(codepoint);
                        writer.write(ESCAPE_UHEXA_PREFIX);
                        writer.write(toUHexa(codepointChars[0]));
                        writer.write(ESCAPE_UHEXA_PREFIX);
                        writer.write(toUHexa(codepointChars[1]));
                    } else {
                        writer.write(ESCAPE_UHEXA_PREFIX);
                        writer.write(toUHexa(codepoint));
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