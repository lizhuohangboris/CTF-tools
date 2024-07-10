package org.unbescape.css;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.apache.coyote.http11.Constants;
import org.apache.el.parser.ELParserConstants;
import org.springframework.asm.Opcodes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/css/CssUnescapeUtil.class */
final class CssUnescapeUtil {
    private static final char ESCAPE_PREFIX = '\\';
    private static char[] HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
    private static char[] HEXA_CHARS_LOWER = "0123456789abcdef".toCharArray();

    private CssUnescapeUtil() {
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
                        case 10:
                            codepoint = -2;
                            referenceOffset = i + 1;
                            break;
                        case 32:
                        case 33:
                        case 34:
                        case 35:
                        case 36:
                        case 37:
                        case 38:
                        case 39:
                        case 40:
                        case 41:
                        case 42:
                        case ELParserConstants.EMPTY /* 43 */:
                        case 44:
                        case 45:
                        case 46:
                        case 47:
                        case 58:
                        case 59:
                        case ELParserConstants.DIGIT /* 60 */:
                        case 61:
                        case 62:
                        case Constants.QUESTION /* 63 */:
                        case 64:
                        case 91:
                        case 92:
                        case 93:
                        case Opcodes.DUP2_X2 /* 94 */:
                        case Opcodes.SWAP /* 95 */:
                        case 96:
                        case 123:
                        case 124:
                        case 125:
                        case 126:
                            codepoint = c1;
                            referenceOffset = i + 1;
                            break;
                    }
                    if (codepoint == -1) {
                        if ((c1 >= 48 && c1 <= 57) || ((c1 >= 65 && c1 <= 70) || (c1 >= 97 && c1 <= 102))) {
                            int f = i + 2;
                            while (f < i + 7 && f < max && (((cf = text.charAt(f)) >= '0' && cf <= '9') || ((cf >= 'A' && cf <= 'F') || (cf >= 'a' && cf <= 'f')))) {
                                f++;
                            }
                            codepoint = parseIntFromReference(text, i + 1, f, 16);
                            referenceOffset = f - 1;
                            if (f < max && text.charAt(f) == ' ') {
                                referenceOffset++;
                            }
                        } else if (c1 == 13 || c1 == 12) {
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
        char[] escapes = new char[6];
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
                        case 10:
                            codepoint = -2;
                            c1 = c2;
                            c2 = reader.read();
                            break;
                        case 32:
                        case 33:
                        case 34:
                        case 35:
                        case 36:
                        case 37:
                        case 38:
                        case 39:
                        case 40:
                        case 41:
                        case 42:
                        case ELParserConstants.EMPTY /* 43 */:
                        case 44:
                        case 45:
                        case 46:
                        case 47:
                        case 58:
                        case 59:
                        case ELParserConstants.DIGIT /* 60 */:
                        case 61:
                        case 62:
                        case Constants.QUESTION /* 63 */:
                        case 64:
                        case 91:
                        case 92:
                        case 93:
                        case Opcodes.DUP2_X2 /* 94 */:
                        case Opcodes.SWAP /* 95 */:
                        case 96:
                        case 123:
                        case 124:
                        case 125:
                        case 126:
                            codepoint = c2;
                            c1 = c2;
                            c2 = reader.read();
                            break;
                    }
                    if (codepoint == -1) {
                        if ((c2 >= 48 && c2 <= 57) || ((c2 >= 65 && c2 <= 70) || (c2 >= 97 && c2 <= 102))) {
                            int escapei = 0;
                            int ce = c2;
                            while (ce >= 0 && escapei < 6 && ((ce >= 48 && ce <= 57) || ((ce >= 65 && ce <= 70) || (ce >= 97 && ce <= 102)))) {
                                escapes[escapei] = (char) ce;
                                ce = reader.read();
                                escapei++;
                            }
                            char c = escapes[5];
                            c2 = ce;
                            codepoint = parseIntFromReference(escapes, 0, escapei, 16);
                            if (c2 == 32) {
                                c2 = reader.read();
                            }
                        } else if (c2 == 13 || c2 == 12) {
                            writer.write(c1);
                        } else {
                            codepoint = c2;
                            c2 = reader.read();
                        }
                    }
                }
                if (codepoint > 65535) {
                    writer.write(Character.toChars(codepoint));
                } else if (codepoint != -2) {
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
                        case '\n':
                            codepoint = -2;
                            referenceOffset = i + 1;
                            break;
                        case ' ':
                        case '!':
                        case '\"':
                        case '#':
                        case '$':
                        case '%':
                        case '&':
                        case '\'':
                        case '(':
                        case ')':
                        case '*':
                        case ELParserConstants.EMPTY /* 43 */:
                        case ',':
                        case '-':
                        case '.':
                        case '/':
                        case ':':
                        case ';':
                        case ELParserConstants.DIGIT /* 60 */:
                        case '=':
                        case '>':
                        case Constants.QUESTION /* 63 */:
                        case '@':
                        case '[':
                        case '\\':
                        case ']':
                        case Opcodes.DUP2_X2 /* 94 */:
                        case Opcodes.SWAP /* 95 */:
                        case '`':
                        case '{':
                        case '|':
                        case '}':
                        case '~':
                            codepoint = c1;
                            referenceOffset = i + 1;
                            break;
                    }
                    if (codepoint == -1) {
                        if ((c1 >= '0' && c1 <= '9') || ((c1 >= 'A' && c1 <= 'F') || (c1 >= 'a' && c1 <= 'f'))) {
                            int f = i + 2;
                            while (f < i + 7 && f < max && (((cf = text[f]) >= '0' && cf <= '9') || ((cf >= 'A' && cf <= 'F') || (cf >= 'a' && cf <= 'f')))) {
                                f++;
                            }
                            codepoint = parseIntFromReference(text, i + 1, f, 16);
                            referenceOffset = f - 1;
                            if (f < max && text[f] == ' ') {
                                referenceOffset++;
                            }
                        } else if (c1 == '\r' || c1 == '\f') {
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
}