package org.unbescape.javascript;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import org.springframework.asm.Opcodes;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/javascript/JavaScriptEscapeUtil.class */
public final class JavaScriptEscapeUtil {
    private static final char ESCAPE_PREFIX = '\\';
    private static final char ESCAPE_XHEXA_PREFIX2 = 'x';
    private static final char ESCAPE_UHEXA_PREFIX2 = 'u';
    private static final char[] ESCAPE_XHEXA_PREFIX = "\\x".toCharArray();
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
        SEC_CHARS[0] = '0';
        SEC_CHARS[8] = 'b';
        SEC_CHARS[9] = 't';
        SEC_CHARS[10] = 'n';
        SEC_CHARS[12] = 'f';
        SEC_CHARS[13] = 'r';
        SEC_CHARS[34] = '\"';
        SEC_CHARS[39] = '\'';
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
        ESCAPE_LEVELS[0] = 1;
        ESCAPE_LEVELS[8] = 1;
        ESCAPE_LEVELS[9] = 1;
        ESCAPE_LEVELS[10] = 1;
        ESCAPE_LEVELS[12] = 1;
        ESCAPE_LEVELS[13] = 1;
        ESCAPE_LEVELS[34] = 1;
        ESCAPE_LEVELS[39] = 1;
        ESCAPE_LEVELS[92] = 1;
        ESCAPE_LEVELS[47] = 1;
        ESCAPE_LEVELS[38] = 1;
        char c9 = 1;
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

    private JavaScriptEscapeUtil() {
    }

    static char[] toXHexa(int codepoint) {
        char[] result = {HEXA_CHARS_UPPER[(codepoint >>> 4) % 16], HEXA_CHARS_UPPER[codepoint % 16]};
        return result;
    }

    static char[] toUHexa(int codepoint) {
        char[] result = {HEXA_CHARS_UPPER[(codepoint >>> 12) % 16], HEXA_CHARS_UPPER[(codepoint >>> 8) % 16], HEXA_CHARS_UPPER[(codepoint >>> 4) % 16], HEXA_CHARS_UPPER[codepoint % 16]};
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String escape(String text, JavaScriptEscapeType escapeType, JavaScriptEscapeLevel escapeLevel) {
        char sec;
        if (text == null) {
            return null;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useSECs = escapeType.getUseSECs();
        boolean useXHexa = escapeType.getUseXHexa();
        StringBuilder strBuilder = null;
        int max = text.length();
        int readOffset = 0;
        int i = 0;
        while (i < max) {
            int codepoint = Character.codePointAt(text, i);
            if ((codepoint > 159 || level >= ESCAPE_LEVELS[codepoint]) && (codepoint != 47 || level >= 3 || (i != 0 && text.charAt(i - 1) == '<'))) {
                if (codepoint > 159 && level < ESCAPE_LEVELS[160] && codepoint != 8232 && codepoint != 8233) {
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
                    } else if (useXHexa && codepoint <= 255) {
                        strBuilder.append(ESCAPE_XHEXA_PREFIX);
                        strBuilder.append(toXHexa(codepoint));
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
    public static void escape(Reader reader, Writer writer, JavaScriptEscapeType escapeType, JavaScriptEscapeLevel escapeLevel) throws IOException {
        char sec;
        if (reader == null) {
            return;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useSECs = escapeType.getUseSECs();
        boolean useXHexa = escapeType.getUseXHexa();
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
            } else if (codepoint > 159 && level < ESCAPE_LEVELS[160] && codepoint != 8232 && codepoint != 8233) {
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
                } else if (useXHexa && codepoint <= 255) {
                    writer.write(ESCAPE_XHEXA_PREFIX);
                    writer.write(toXHexa(codepoint));
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
    public static void escape(char[] text, int offset, int len, Writer writer, JavaScriptEscapeType escapeType, JavaScriptEscapeLevel escapeLevel) throws IOException {
        char sec;
        if (text == null || text.length == 0) {
            return;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useSECs = escapeType.getUseSECs();
        boolean useXHexa = escapeType.getUseXHexa();
        int max = offset + len;
        int readOffset = offset;
        int i = offset;
        while (i < max) {
            int codepoint = Character.codePointAt(text, i);
            if ((codepoint > 159 || level >= ESCAPE_LEVELS[codepoint]) && (codepoint != 47 || level >= 3 || (i != 0 && text[i - 1] == '<'))) {
                if (codepoint > 159 && level < ESCAPE_LEVELS[160] && codepoint != 8232 && codepoint != 8233) {
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
                    } else if (useXHexa && codepoint <= 255) {
                        writer.write(ESCAPE_XHEXA_PREFIX);
                        writer.write(toXHexa(codepoint));
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

    static boolean isOctalEscape(String text, int start, int end) {
        char c1;
        if (start >= end || (c1 = text.charAt(start)) < '0' || c1 > '7') {
            return false;
        }
        if (start + 1 >= end) {
            return c1 != '0';
        }
        char c2 = text.charAt(start + 1);
        if (c2 < '0' || c2 > '7') {
            return c1 != '0';
        } else if (start + 2 >= end) {
            return (c1 == '0' && c2 == '0') ? false : true;
        } else {
            char c3 = text.charAt(start + 2);
            return (c3 < '0' || c3 > '7') ? (c1 == '0' && c2 == '0') ? false : true : (c1 == '0' && c2 == '0' && c3 == '0') ? false : true;
        }
    }

    static boolean isOctalEscape(char[] text, int start, int end) {
        char c1;
        if (start >= end || (c1 = text[start]) < '0' || c1 > '7') {
            return false;
        }
        if (start + 1 >= end) {
            return c1 != '0';
        }
        char c2 = text[start + 1];
        if (c2 < '0' || c2 > '7') {
            return c1 != '0';
        } else if (start + 2 >= end) {
            return (c1 == '0' && c2 == '0') ? false : true;
        } else {
            char c3 = text[start + 2];
            return (c3 < '0' || c3 > '7') ? (c1 == '0' && c2 == '0') ? false : true : (c1 == '0' && c2 == '0' && c3 == '0') ? false : true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public static String unescape(String text) {
        char cf;
        char cf2;
        char cf3;
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
                    int c1 = text.charAt(i + 1);
                    switch (c1) {
                        case 10:
                            codepoint = -2;
                            referenceOffset = i + 1;
                            break;
                        case 34:
                            codepoint = 34;
                            referenceOffset = i + 1;
                            break;
                        case 39:
                            codepoint = 39;
                            referenceOffset = i + 1;
                            break;
                        case 47:
                            codepoint = 47;
                            referenceOffset = i + 1;
                            break;
                        case 48:
                            if (!isOctalEscape(text, i + 1, max)) {
                                codepoint = 0;
                                referenceOffset = i + 1;
                                break;
                            }
                            break;
                        case 92:
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
                        case 116:
                            codepoint = 9;
                            referenceOffset = i + 1;
                            break;
                        case Opcodes.FNEG /* 118 */:
                            codepoint = 11;
                            referenceOffset = i + 1;
                            break;
                    }
                    if (codepoint == -1) {
                        if (c1 == 120) {
                            int f = i + 2;
                            while (f < i + 4 && f < max && (((cf3 = text.charAt(f)) >= '0' && cf3 <= '9') || ((cf3 >= 'A' && cf3 <= 'F') || (cf3 >= 'a' && cf3 <= 'f')))) {
                                f++;
                            }
                            if (f - (i + 2) < 2) {
                                i++;
                            } else {
                                codepoint = parseIntFromReference(text, i + 2, f, 16);
                                referenceOffset = f - 1;
                            }
                        } else if (c1 == 117) {
                            int f2 = i + 2;
                            while (f2 < i + 6 && f2 < max && (((cf2 = text.charAt(f2)) >= '0' && cf2 <= '9') || ((cf2 >= 'A' && cf2 <= 'F') || (cf2 >= 'a' && cf2 <= 'f')))) {
                                f2++;
                            }
                            if (f2 - (i + 2) < 4) {
                                i++;
                            } else {
                                codepoint = parseIntFromReference(text, i + 2, f2, 16);
                                referenceOffset = f2 - 1;
                            }
                        } else if (c1 >= 48 && c1 <= 55) {
                            int f3 = i + 2;
                            while (f3 < i + 4 && f3 < max && (cf = text.charAt(f3)) >= '0' && cf <= '7') {
                                f3++;
                            }
                            codepoint = parseIntFromReference(text, i + 1, f3, 8);
                            if (codepoint > 255) {
                                codepoint = parseIntFromReference(text, i + 1, f3 - 1, 8);
                                referenceOffset = f3 - 2;
                            } else {
                                referenceOffset = f3 - 1;
                            }
                        } else if (c1 == 56 || c1 == 57 || c1 == 13 || c1 == 8232 || c1 == 8233) {
                            i++;
                        } else {
                            codepoint = c1;
                            referenceOffset = i + 1;
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
                } else if (codepoint != -2) {
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
        char[] escapes = new char[4];
        int c2 = reader.read();
        while (c2 >= 0) {
            int c1 = c2;
            c2 = reader.read();
            int escapei = 0;
            if (c1 != 92 || c2 < 0) {
                writer.write(c1);
            } else {
                int codepoint = -1;
                if (c1 == 92) {
                    switch (c2) {
                        case 10:
                            codepoint = -2;
                            c1 = c2;
                            c2 = reader.read();
                            break;
                        case 34:
                            codepoint = 34;
                            c1 = c2;
                            c2 = reader.read();
                            break;
                        case 39:
                            codepoint = 39;
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
                        case Opcodes.FNEG /* 118 */:
                            codepoint = 11;
                            c1 = c2;
                            c2 = reader.read();
                            break;
                    }
                    if (codepoint == -1) {
                        if (c2 == 120) {
                            int escapei2 = 0;
                            int ce = reader.read();
                            while (ce >= 0 && escapei2 < 2 && ((ce >= 48 && ce <= 57) || ((ce >= 65 && ce <= 70) || (ce >= 97 && ce <= 102)))) {
                                escapes[escapei2] = (char) ce;
                                ce = reader.read();
                                escapei2++;
                            }
                            if (escapei2 < 2) {
                                writer.write(c1);
                                writer.write(c2);
                                for (int i = 0; i < escapei2; i++) {
                                    c2 = escapes[i];
                                    writer.write(c2);
                                }
                                c2 = ce;
                            } else {
                                char c = escapes[3];
                                c2 = ce;
                                codepoint = parseIntFromReference(escapes, 0, 2, 16);
                                escapei = 0;
                            }
                        } else if (c2 == 117) {
                            int escapei3 = 0;
                            int ce2 = reader.read();
                            while (ce2 >= 0 && escapei3 < 4 && ((ce2 >= 48 && ce2 <= 57) || ((ce2 >= 65 && ce2 <= 70) || (ce2 >= 97 && ce2 <= 102)))) {
                                escapes[escapei3] = (char) ce2;
                                ce2 = reader.read();
                                escapei3++;
                            }
                            if (escapei3 < 4) {
                                writer.write(c1);
                                writer.write(c2);
                                for (int i2 = 0; i2 < escapei3; i2++) {
                                    c2 = escapes[i2];
                                    writer.write(c2);
                                }
                                c2 = ce2;
                            } else {
                                char c3 = escapes[3];
                                c2 = ce2;
                                codepoint = parseIntFromReference(escapes, 0, 4, 16);
                                escapei = 0;
                            }
                        } else if (c2 >= 48 && c2 <= 55) {
                            int escapei4 = 0;
                            int ce3 = c2;
                            while (ce3 >= 0 && escapei4 < 3 && ce3 >= 48 && ce3 <= 55) {
                                escapes[escapei4] = (char) ce3;
                                ce3 = reader.read();
                                escapei4++;
                            }
                            char c4 = escapes[escapei4 - 1];
                            c2 = ce3;
                            codepoint = parseIntFromReference(escapes, 0, escapei4, 8);
                            if (codepoint > 255) {
                                codepoint = parseIntFromReference(escapes, 0, escapei4 - 1, 8);
                                System.arraycopy(escapes, escapei4 - 2, escapes, 0, 1);
                                escapei = 1;
                            } else if (codepoint == 0 && escapei4 > 1) {
                                System.arraycopy(escapes, 1, escapes, 0, escapei4 - 1);
                                escapei = escapei4 - 1;
                            } else {
                                escapei = 0;
                            }
                        } else if (c2 == 56 || c2 == 57 || c2 == 13 || c2 == 8232 || c2 == 8233) {
                            writer.write(c1);
                            writer.write(c2);
                            c2 = reader.read();
                        } else {
                            codepoint = c2;
                            c2 = reader.read();
                            escapei = 0;
                        }
                    }
                }
                if (codepoint > 65535) {
                    writer.write(Character.toChars(codepoint));
                } else if (codepoint != -2) {
                    writer.write((char) codepoint);
                }
                if (escapei > 0) {
                    writer.write(escapes, 0, escapei);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public static void unescape(char[] text, int offset, int len, Writer writer) throws IOException {
        char cf;
        char cf2;
        char cf3;
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
                        case '\n':
                            codepoint = -2;
                            referenceOffset = i + 1;
                            break;
                        case '\"':
                            codepoint = 34;
                            referenceOffset = i + 1;
                            break;
                        case '\'':
                            codepoint = 39;
                            referenceOffset = i + 1;
                            break;
                        case '/':
                            codepoint = 47;
                            referenceOffset = i + 1;
                            break;
                        case '0':
                            if (!isOctalEscape(text, i + 1, max)) {
                                codepoint = 0;
                                referenceOffset = i + 1;
                                break;
                            }
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
                        case Opcodes.FNEG /* 118 */:
                            codepoint = 11;
                            referenceOffset = i + 1;
                            break;
                    }
                    if (codepoint == -1) {
                        if (c1 == 'x') {
                            int f = i + 2;
                            while (f < i + 4 && f < max && (((cf3 = text[f]) >= '0' && cf3 <= '9') || ((cf3 >= 'A' && cf3 <= 'F') || (cf3 >= 'a' && cf3 <= 'f')))) {
                                f++;
                            }
                            if (f - (i + 2) < 2) {
                                i++;
                            } else {
                                codepoint = parseIntFromReference(text, i + 2, f, 16);
                                referenceOffset = f - 1;
                            }
                        } else if (c1 == 'u') {
                            int f2 = i + 2;
                            while (f2 < i + 6 && f2 < max && (((cf2 = text[f2]) >= '0' && cf2 <= '9') || ((cf2 >= 'A' && cf2 <= 'F') || (cf2 >= 'a' && cf2 <= 'f')))) {
                                f2++;
                            }
                            if (f2 - (i + 2) < 4) {
                                i++;
                            } else {
                                codepoint = parseIntFromReference(text, i + 2, f2, 16);
                                referenceOffset = f2 - 1;
                            }
                        } else if (c1 >= '0' && c1 <= '7') {
                            int f3 = i + 2;
                            while (f3 < i + 4 && f3 < max && (cf = text[f3]) >= '0' && cf <= '7') {
                                f3++;
                            }
                            codepoint = parseIntFromReference(text, i + 1, f3, 8);
                            if (codepoint > 255) {
                                codepoint = parseIntFromReference(text, i + 1, f3 - 1, 8);
                                referenceOffset = f3 - 2;
                            } else {
                                referenceOffset = f3 - 1;
                            }
                        } else if (c1 == '8' || c1 == '9' || c1 == '\r' || c1 == 8232 || c1 == 8233) {
                            i++;
                        } else {
                            codepoint = c1;
                            referenceOffset = i + 1;
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
                } else if (codepoint != -2) {
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