package org.unbescape.json;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import org.springframework.asm.Opcodes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/json/JsonEscapeUtil.class */
final class JsonEscapeUtil {
    private static final char ESCAPE_PREFIX = '\\';
    private static final char ESCAPE_UHEXA_PREFIX2 = 'u';
    private static final char[] ESCAPE_UHEXA_PREFIX = "\\u".toCharArray();
    private static char[] HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
    private static char[] HEXA_CHARS_LOWER = "0123456789abcdef".toCharArray();
    private static int SEC_CHARS_LEN = 93;
    private static char SEC_CHARS_NO_SEC = '*';
    private static char[] SEC_CHARS = new char[SEC_CHARS_LEN];
    private static final char ESCAPE_LEVELS_LEN = 161;
    private static final byte[] ESCAPE_LEVELS;

    static {
        Arrays.fill(SEC_CHARS, SEC_CHARS_NO_SEC);
        SEC_CHARS[8] = 'b';
        SEC_CHARS[9] = 't';
        SEC_CHARS[10] = 'n';
        SEC_CHARS[12] = 'f';
        SEC_CHARS[13] = 'r';
        SEC_CHARS[34] = '\"';
        SEC_CHARS[92] = '\\';
        SEC_CHARS[47] = '/';
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
        ESCAPE_LEVELS[8] = 1;
        ESCAPE_LEVELS[9] = 1;
        ESCAPE_LEVELS[10] = 1;
        ESCAPE_LEVELS[12] = 1;
        ESCAPE_LEVELS[13] = 1;
        ESCAPE_LEVELS[34] = 1;
        ESCAPE_LEVELS[92] = 1;
        ESCAPE_LEVELS[47] = 1;
        ESCAPE_LEVELS[38] = 1;
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

    private JsonEscapeUtil() {
    }

    static char[] toUHexa(int codepoint) {
        char[] result = {HEXA_CHARS_UPPER[(codepoint >>> 12) % 16], HEXA_CHARS_UPPER[(codepoint >>> 8) % 16], HEXA_CHARS_UPPER[(codepoint >>> 4) % 16], HEXA_CHARS_UPPER[codepoint % 16]};
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String escape(String text, JsonEscapeType escapeType, JsonEscapeLevel escapeLevel) {
        char sec;
        if (text == null) {
            return null;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useSECs = escapeType.getUseSECs();
        StringBuilder strBuilder = null;
        int max = text.length();
        int readOffset = 0;
        int i = 0;
        while (i < max) {
            int codepoint = Character.codePointAt(text, i);
            if ((codepoint > 159 || level >= ESCAPE_LEVELS[codepoint]) && (codepoint != 47 || level >= 3 || (i != 0 && text.charAt(i - 1) == '<'))) {
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
                    if (useSECs && codepoint < SEC_CHARS_LEN && (sec = SEC_CHARS[codepoint]) != SEC_CHARS_NO_SEC) {
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
    public static void escape(Reader reader, Writer writer, JsonEscapeType escapeType, JsonEscapeLevel escapeLevel) throws IOException {
        char sec;
        if (reader == null) {
            return;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useSECs = escapeType.getUseSECs();
        int c1 = -1;
        int c2 = reader.read();
        while (c2 >= 0) {
            int c0 = c1;
            c1 = c2;
            c2 = reader.read();
            int codepoint = codePointAt((char) c1, (char) c2);
            if (codepoint <= 159 && level < ESCAPE_LEVELS[codepoint]) {
                writer.write(c1);
            } else if (codepoint == 47 && level < 3 && c0 != 60) {
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
                if (useSECs && codepoint < SEC_CHARS_LEN && (sec = SEC_CHARS[codepoint]) != SEC_CHARS_NO_SEC) {
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
    public static void escape(char[] text, int offset, int len, Writer writer, JsonEscapeType escapeType, JsonEscapeLevel escapeLevel) throws IOException {
        char sec;
        if (text == null || text.length == 0) {
            return;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useSECs = escapeType.getUseSECs();
        int max = offset + len;
        int readOffset = offset;
        int i = offset;
        while (i < max) {
            int codepoint = Character.codePointAt(text, i);
            if ((codepoint > 159 || level >= ESCAPE_LEVELS[codepoint]) && (codepoint != 47 || level >= 3 || (i != offset && text[i - 1] == '<'))) {
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
                    if (useSECs && codepoint < SEC_CHARS_LEN && (sec = SEC_CHARS[codepoint]) != SEC_CHARS_NO_SEC) {
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

    static int parseIntFromReference(String text, int start, int end, int radix) {
        int result = 0;
        for (int i = start; i < end; i++) {
            char c = text.charAt(i);
            int n = -1;
            for (int j = 0; j < HEXA_CHARS_UPPER.length; j++) {
                if (c == HEXA_CHARS_UPPER[j] || c == HEXA_CHARS_LOWER[j]) {
                    n = j;
                    break;
                }
            }
            result = (radix * result) + n;
        }
        return result;
    }

    static int parseIntFromReference(char[] text, int start, int end, int radix) {
        int result = 0;
        for (int i = start; i < end; i++) {
            char c = text[i];
            int n = -1;
            for (int j = 0; j < HEXA_CHARS_UPPER.length; j++) {
                if (c == HEXA_CHARS_UPPER[j] || c == HEXA_CHARS_LOWER[j]) {
                    n = j;
                    break;
                }
            }
            result = (radix * result) + n;
        }
        return result;
    }

    static int parseIntFromReference(int[] text, int start, int end, int radix) {
        int result = 0;
        for (int i = start; i < end; i++) {
            char c = (char) text[i];
            int n = -1;
            for (int j = 0; j < HEXA_CHARS_UPPER.length; j++) {
                if (c == HEXA_CHARS_UPPER[j] || c == HEXA_CHARS_LOWER[j]) {
                    n = j;
                    break;
                }
            }
            result = (radix * result) + n;
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String unescape(String text) {
        char cf;
        if (text == null) {
            return null;
        }
        StringBuilder strBuilder = null;
        int max = text.length();
        int readOffset = 0;
        int referenceOffset = 0;
        int i = 0;
        while (i < max) {
            char c = text.charAt(i);
            if (c == '\\' && i + 1 < max) {
                int codepoint = -1;
                if (c == '\\') {
                    char c1 = text.charAt(i + 1);
                    switch (c1) {
                        case '\"':
                            codepoint = 34;
                            referenceOffset = i + 1;
                            break;
                        case '/':
                            codepoint = 47;
                            referenceOffset = i + 1;
                            break;
                        case '\\':
                            codepoint = 92;
                            referenceOffset = i + 1;
                            break;
                        case Opcodes.FADD /* 98 */:
                            codepoint = 8;
                            referenceOffset = i + 1;
                            break;
                        case Opcodes.FSUB /* 102 */:
                            codepoint = 12;
                            referenceOffset = i + 1;
                            break;
                        case Opcodes.FDIV /* 110 */:
                            codepoint = 10;
                            referenceOffset = i + 1;
                            break;
                        case Opcodes.FREM /* 114 */:
                            codepoint = 13;
                            referenceOffset = i + 1;
                            break;
                        case 't':
                            codepoint = 9;
                            referenceOffset = i + 1;
                            break;
                    }
                    if (codepoint == -1) {
                        if (c1 == 'u') {
                            int f = i + 2;
                            while (f < i + 6 && f < max && (((cf = text.charAt(f)) >= '0' && cf <= '9') || ((cf >= 'A' && cf <= 'F') || (cf >= 'a' && cf <= 'f')))) {
                                f++;
                            }
                            if (f - (i + 2) < 4) {
                                i++;
                            } else {
                                codepoint = parseIntFromReference(text, i + 2, f, 16);
                                referenceOffset = f - 1;
                            }
                        } else {
                            i++;
                        }
                    }
                }
                if (strBuilder == null) {
                    strBuilder = new StringBuilder(max + 5);
                }
                if (i - readOffset > 0) {
                    strBuilder.append((CharSequence) text, readOffset, i);
                }
                i = referenceOffset;
                readOffset = i + 1;
                if (codepoint > 65535) {
                    strBuilder.append(Character.toChars(codepoint));
                } else {
                    strBuilder.append((char) codepoint);
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
    public static void unescape(Reader reader, Writer writer) throws IOException {
        if (reader == null) {
            return;
        }
        int[] escapes = new int[4];
        int c2 = reader.read();
        while (c2 >= 0) {
            int c1 = c2;
            c2 = reader.read();
            if (c1 != 92 || c2 < 0) {
                writer.write(c1);
            } else {
                int codepoint = -1;
                if (c1 == 92) {
                    switch (c2) {
                        case 34:
                            codepoint = 34;
                            c1 = c2;
                            c2 = reader.read();
                            break;
                        case 47:
                            codepoint = 47;
                            c1 = c2;
                            c2 = reader.read();
                            break;
                        case 92:
                            codepoint = 92;
                            c1 = c2;
                            c2 = reader.read();
                            break;
                        case Opcodes.FADD /* 98 */:
                            codepoint = 8;
                            c1 = c2;
                            c2 = reader.read();
                            break;
                        case Opcodes.FSUB /* 102 */:
                            codepoint = 12;
                            c1 = c2;
                            c2 = reader.read();
                            break;
                        case Opcodes.FDIV /* 110 */:
                            codepoint = 10;
                            c1 = c2;
                            c2 = reader.read();
                            break;
                        case Opcodes.FREM /* 114 */:
                            codepoint = 13;
                            c1 = c2;
                            c2 = reader.read();
                            break;
                        case 116:
                            codepoint = 9;
                            c1 = c2;
                            c2 = reader.read();
                            break;
                    }
                    if (codepoint == -1) {
                        if (c2 == 117) {
                            int escapei = 0;
                            int ce = reader.read();
                            while (ce >= 0 && escapei < 4 && ((ce >= 48 && ce <= 57) || ((ce >= 65 && ce <= 70) || (ce >= 97 && ce <= 102)))) {
                                escapes[escapei] = ce;
                                ce = reader.read();
                                escapei++;
                            }
                            if (escapei < 4) {
                                writer.write(c1);
                                writer.write(c2);
                                for (int i = 0; i < escapei; i++) {
                                    c2 = escapes[i];
                                    writer.write(c2);
                                }
                                c2 = ce;
                            } else {
                                int i2 = escapes[3];
                                c2 = ce;
                                codepoint = parseIntFromReference(escapes, 0, 4, 16);
                            }
                        } else {
                            writer.write(c1);
                            writer.write(c2);
                            c2 = reader.read();
                        }
                    }
                }
                if (codepoint > 65535) {
                    writer.write(Character.toChars(codepoint));
                } else {
                    writer.write((char) codepoint);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void unescape(char[] text, int offset, int len, Writer writer) throws IOException {
        char cf;
        if (text == null) {
            return;
        }
        int max = offset + len;
        int readOffset = offset;
        int referenceOffset = offset;
        int i = offset;
        while (i < max) {
            char c = text[i];
            if (c == '\\' && i + 1 < max) {
                int codepoint = -1;
                if (c == '\\') {
                    char c1 = text[i + 1];
                    switch (c1) {
                        case '\"':
                            codepoint = 34;
                            referenceOffset = i + 1;
                            break;
                        case '/':
                            codepoint = 47;
                            referenceOffset = i + 1;
                            break;
                        case '\\':
                            codepoint = 92;
                            referenceOffset = i + 1;
                            break;
                        case Opcodes.FADD /* 98 */:
                            codepoint = 8;
                            referenceOffset = i + 1;
                            break;
                        case Opcodes.FSUB /* 102 */:
                            codepoint = 12;
                            referenceOffset = i + 1;
                            break;
                        case Opcodes.FDIV /* 110 */:
                            codepoint = 10;
                            referenceOffset = i + 1;
                            break;
                        case Opcodes.FREM /* 114 */:
                            codepoint = 13;
                            referenceOffset = i + 1;
                            break;
                        case 't':
                            codepoint = 9;
                            referenceOffset = i + 1;
                            break;
                    }
                    if (codepoint == -1) {
                        if (c1 == 'u') {
                            int f = i + 2;
                            while (f < i + 6 && f < max && (((cf = text[f]) >= '0' && cf <= '9') || ((cf >= 'A' && cf <= 'F') || (cf >= 'a' && cf <= 'f')))) {
                                f++;
                            }
                            if (f - (i + 2) < 4) {
                                i++;
                            } else {
                                codepoint = parseIntFromReference(text, i + 2, f, 16);
                                referenceOffset = f - 1;
                            }
                        } else {
                            i++;
                        }
                    }
                }
                if (i - readOffset > 0) {
                    writer.write(text, readOffset, i - readOffset);
                }
                i = referenceOffset;
                readOffset = i + 1;
                if (codepoint > 65535) {
                    writer.write(Character.toChars(codepoint));
                } else {
                    writer.write((char) codepoint);
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