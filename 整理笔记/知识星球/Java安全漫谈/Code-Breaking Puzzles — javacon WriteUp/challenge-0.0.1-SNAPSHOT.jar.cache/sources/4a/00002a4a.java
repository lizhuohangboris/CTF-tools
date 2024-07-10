package org.unbescape.properties;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.springframework.asm.Opcodes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/properties/PropertiesUnescapeUtil.class */
final class PropertiesUnescapeUtil {
    private static final char ESCAPE_PREFIX = '\\';
    private static final char ESCAPE_UHEXA_PREFIX2 = 'u';
    private static char[] HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
    private static char[] HEXA_CHARS_LOWER = "0123456789abcdef".toCharArray();

    private PropertiesUnescapeUtil() {
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

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
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
                    int c1 = text.charAt(i + 1);
                    switch (c1) {
                        case 92:
                            codepoint = 92;
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
                    }
                    if (codepoint == -1) {
                        if (c1 == 117) {
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
        char[] escapes = new char[4];
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
                        case 92:
                            codepoint = 92;
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
                                escapes[escapei] = (char) ce;
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
                                char c = escapes[3];
                                c2 = ce;
                                codepoint = parseIntFromReference(escapes, 0, 4, 16);
                            }
                        } else {
                            codepoint = c2;
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
    /* JADX WARN: Multi-variable type inference failed */
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
                        case '\\':
                            codepoint = 92;
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
}