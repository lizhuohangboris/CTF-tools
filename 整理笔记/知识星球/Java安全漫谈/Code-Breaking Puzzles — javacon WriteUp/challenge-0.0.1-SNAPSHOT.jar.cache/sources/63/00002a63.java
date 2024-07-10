package org.unbescape.xml;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/xml/XmlEscapeUtil.class */
final class XmlEscapeUtil {
    private static final char REFERENCE_PREFIX = '&';
    private static final char REFERENCE_NUMERIC_PREFIX2 = '#';
    private static final char REFERENCE_HEXA_PREFIX3 = 'x';
    private static final char REFERENCE_SUFFIX = ';';
    private static final char[] REFERENCE_DECIMAL_PREFIX = "&#".toCharArray();
    private static final char[] REFERENCE_HEXA_PREFIX = "&#x".toCharArray();
    private static char[] HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
    private static char[] HEXA_CHARS_LOWER = "0123456789abcdef".toCharArray();

    private XmlEscapeUtil() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String escape(String text, XmlEscapeSymbols symbols, XmlEscapeType escapeType, XmlEscapeLevel escapeLevel) {
        int codepointIndex;
        if (text == null) {
            return null;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useCERs = escapeType.getUseCERs();
        boolean useHexa = escapeType.getUseHexa();
        StringBuilder strBuilder = null;
        int max = text.length();
        int readOffset = 0;
        int i = 0;
        while (i < max) {
            int codepoint = Character.codePointAt(text, i);
            boolean codepointValid = symbols.CODEPOINT_VALIDATOR.isValid(codepoint);
            if (codepoint > 159 || level >= symbols.ESCAPE_LEVELS[codepoint] || !codepointValid) {
                if (codepoint > 159 && level < symbols.ESCAPE_LEVELS[160] && codepointValid) {
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
                    if (codepointValid) {
                        if (useCERs && (codepointIndex = Arrays.binarySearch(symbols.SORTED_CODEPOINTS, codepoint)) >= 0) {
                            strBuilder.append(symbols.SORTED_CERS_BY_CODEPOINT[codepointIndex]);
                        } else {
                            if (useHexa) {
                                strBuilder.append(REFERENCE_HEXA_PREFIX);
                                strBuilder.append(Integer.toHexString(codepoint));
                            } else {
                                strBuilder.append(REFERENCE_DECIMAL_PREFIX);
                                strBuilder.append(String.valueOf(codepoint));
                            }
                            strBuilder.append(';');
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
    public static void escape(Reader reader, Writer writer, XmlEscapeSymbols symbols, XmlEscapeType escapeType, XmlEscapeLevel escapeLevel) throws IOException {
        int codepointIndex;
        if (reader == null) {
            return;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useCERs = escapeType.getUseCERs();
        boolean useHexa = escapeType.getUseHexa();
        int c2 = reader.read();
        while (c2 >= 0) {
            int c1 = c2;
            c2 = reader.read();
            int codepoint = codePointAt((char) c1, (char) c2);
            boolean codepointValid = symbols.CODEPOINT_VALIDATOR.isValid(codepoint);
            if (codepoint <= 159 && level < symbols.ESCAPE_LEVELS[codepoint] && codepointValid) {
                writer.write(c1);
            } else if (codepoint > 159 && level < symbols.ESCAPE_LEVELS[160] && codepointValid) {
                writer.write(c1);
                if (Character.charCount(codepoint) > 1) {
                    writer.write(c2);
                    c2 = reader.read();
                }
            } else {
                if (Character.charCount(codepoint) > 1) {
                    c2 = reader.read();
                }
                if (codepointValid) {
                    if (useCERs && (codepointIndex = Arrays.binarySearch(symbols.SORTED_CODEPOINTS, codepoint)) >= 0) {
                        writer.write(symbols.SORTED_CERS_BY_CODEPOINT[codepointIndex]);
                    } else {
                        if (useHexa) {
                            writer.write(REFERENCE_HEXA_PREFIX);
                            writer.write(Integer.toHexString(codepoint));
                        } else {
                            writer.write(REFERENCE_DECIMAL_PREFIX);
                            writer.write(String.valueOf(codepoint));
                        }
                        writer.write(59);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void escape(char[] text, int offset, int len, Writer writer, XmlEscapeSymbols symbols, XmlEscapeType escapeType, XmlEscapeLevel escapeLevel) throws IOException {
        int codepointIndex;
        if (text == null || text.length == 0) {
            return;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useCERs = escapeType.getUseCERs();
        boolean useHexa = escapeType.getUseHexa();
        int max = offset + len;
        int readOffset = offset;
        int i = offset;
        while (i < max) {
            int codepoint = Character.codePointAt(text, i);
            boolean codepointValid = symbols.CODEPOINT_VALIDATOR.isValid(codepoint);
            if (codepoint > 159 || level >= symbols.ESCAPE_LEVELS[codepoint] || !codepointValid) {
                if (codepoint > 159 && level < symbols.ESCAPE_LEVELS[160] && codepointValid) {
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
                    if (codepointValid) {
                        if (useCERs && (codepointIndex = Arrays.binarySearch(symbols.SORTED_CODEPOINTS, codepoint)) >= 0) {
                            writer.write(symbols.SORTED_CERS_BY_CODEPOINT[codepointIndex]);
                        } else {
                            if (useHexa) {
                                writer.write(REFERENCE_HEXA_PREFIX);
                                writer.write(Integer.toHexString(codepoint));
                            } else {
                                writer.write(REFERENCE_DECIMAL_PREFIX);
                                writer.write(String.valueOf(codepoint));
                            }
                            writer.write(59);
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
    public static String unescape(String text, XmlEscapeSymbols symbols) {
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
            if (c == '&' && i + 1 < max) {
                int codepoint = 0;
                if (c == '&') {
                    char c1 = text.charAt(i + 1);
                    if (c1 != ' ' && c1 != '\n' && c1 != '\t' && c1 != '\f' && c1 != '<' && c1 != '&') {
                        if (c1 == '#') {
                            if (i + 2 < max) {
                                char c2 = text.charAt(i + 2);
                                if (c2 == 'x' && i + 3 < max) {
                                    int f = i + 3;
                                    while (f < max && (((cf3 = text.charAt(f)) >= '0' && cf3 <= '9') || ((cf3 >= 'A' && cf3 <= 'F') || (cf3 >= 'a' && cf3 <= 'f')))) {
                                        f++;
                                    }
                                    if (f - (i + 3) > 0 && f < max && text.charAt(f) == ';') {
                                        int f2 = f + 1;
                                        codepoint = parseIntFromReference(text, i + 3, f2 - 1, 16);
                                        referenceOffset = f2 - 1;
                                    }
                                } else if (c2 >= '0' && c2 <= '9') {
                                    int f3 = i + 2;
                                    while (f3 < max && (cf2 = text.charAt(f3)) >= '0' && cf2 <= '9') {
                                        f3++;
                                    }
                                    if (f3 - (i + 2) > 0 && f3 < max && text.charAt(f3) == ';') {
                                        int f4 = f3 + 1;
                                        codepoint = parseIntFromReference(text, i + 2, f4 - 1, 10);
                                        referenceOffset = f4 - 1;
                                    }
                                }
                            }
                        } else {
                            int f5 = i + 1;
                            while (f5 < max && (((cf = text.charAt(f5)) >= 'a' && cf <= 'z') || ((cf >= 'A' && cf <= 'Z') || (cf >= '0' && cf <= '9')))) {
                                f5++;
                            }
                            if (f5 - (i + 1) > 0) {
                                if (f5 < max && text.charAt(f5) == ';') {
                                    f5++;
                                }
                                int ncrPosition = XmlEscapeSymbols.binarySearch(symbols.SORTED_CERS, text, i, f5);
                                if (ncrPosition >= 0) {
                                    codepoint = symbols.SORTED_CODEPOINTS_BY_CER[ncrPosition];
                                    referenceOffset = f5 - 1;
                                }
                            }
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
    public static void unescape(Reader reader, Writer writer, XmlEscapeSymbols symbols) throws IOException {
        if (reader == null) {
            return;
        }
        char[] escapes = new char[10];
        int c2 = reader.read();
        while (c2 >= 0) {
            int c1 = c2;
            c2 = reader.read();
            int escapei = 0;
            if (c1 != 38 || c2 < 0) {
                writer.write(c1);
            } else {
                int codepoint = 0;
                if (c1 == 38) {
                    if (c2 == 32 || c2 == 10 || c2 == 9 || c2 == 12 || c2 == 60 || c2 == 38) {
                        writer.write(c1);
                    } else if (c2 == 35) {
                        int c3 = reader.read();
                        if (c3 < 0) {
                            writer.write(c1);
                            writer.write(c2);
                            c2 = c3;
                        } else if (c3 == 120) {
                            int ce = reader.read();
                            while (ce >= 0 && ((ce >= 48 && ce <= 57) || ((ce >= 65 && ce <= 70) || (ce >= 97 && ce <= 102)))) {
                                if (escapei == escapes.length) {
                                    char[] newEscapes = new char[escapes.length + 4];
                                    System.arraycopy(escapes, 0, newEscapes, 0, escapes.length);
                                    escapes = newEscapes;
                                }
                                escapes[escapei] = (char) ce;
                                ce = reader.read();
                                escapei++;
                            }
                            if (escapei == 0) {
                                writer.write(c1);
                                writer.write(c2);
                                writer.write(c3);
                                c2 = ce;
                            } else if (ce != 59) {
                                writer.write(c1);
                                writer.write(c2);
                                writer.write(c3);
                                writer.write(escapes, 0, escapei);
                                char c = escapes[escapei - 1];
                                c2 = ce;
                            } else {
                                c2 = reader.read();
                                codepoint = parseIntFromReference(escapes, 0, escapei, 16);
                            }
                        } else if (c3 >= 48 && c3 <= 57) {
                            int ce2 = c3;
                            while (ce2 >= 0 && ce2 >= 48 && ce2 <= 57) {
                                if (escapei == escapes.length) {
                                    char[] newEscapes2 = new char[escapes.length + 4];
                                    System.arraycopy(escapes, 0, newEscapes2, 0, escapes.length);
                                    escapes = newEscapes2;
                                }
                                escapes[escapei] = (char) ce2;
                                ce2 = reader.read();
                                escapei++;
                            }
                            if (escapei == 0) {
                                writer.write(c1);
                                writer.write(c2);
                                c2 = c3;
                            } else if (ce2 != 59) {
                                writer.write(c1);
                                writer.write(c2);
                                writer.write(escapes, 0, escapei);
                                char c4 = escapes[escapei - 1];
                                c2 = ce2;
                            } else {
                                c2 = reader.read();
                                codepoint = parseIntFromReference(escapes, 0, escapei, 10);
                            }
                        } else {
                            writer.write(c1);
                            writer.write(c2);
                            c2 = c3;
                        }
                    } else {
                        int ce3 = c2;
                        while (ce3 >= 0 && ((ce3 >= 48 && ce3 <= 57) || ((ce3 >= 65 && ce3 <= 90) || (ce3 >= 97 && ce3 <= 122)))) {
                            if (escapei == escapes.length) {
                                char[] newEscapes3 = new char[escapes.length + 4];
                                System.arraycopy(escapes, 0, newEscapes3, 0, escapes.length);
                                escapes = newEscapes3;
                            }
                            escapes[escapei] = (char) ce3;
                            ce3 = reader.read();
                            escapei++;
                        }
                        if (escapei == 0) {
                            writer.write(c1);
                        } else {
                            if (escapei + 2 >= escapes.length) {
                                char[] newEscapes4 = new char[escapes.length + 4];
                                System.arraycopy(escapes, 0, newEscapes4, 0, escapes.length);
                                escapes = newEscapes4;
                            }
                            System.arraycopy(escapes, 0, escapes, 1, escapei);
                            escapes[0] = (char) c1;
                            int escapei2 = escapei + 1;
                            if (ce3 == 59) {
                                escapei2++;
                                escapes[escapei2] = (char) ce3;
                                ce3 = reader.read();
                            }
                            char c5 = escapes[escapei2 - 1];
                            c2 = ce3;
                            int ncrPosition = XmlEscapeSymbols.binarySearch(symbols.SORTED_CERS, escapes, 0, escapei2);
                            if (ncrPosition >= 0) {
                                codepoint = symbols.SORTED_CODEPOINTS_BY_CER[ncrPosition];
                            } else {
                                writer.write(escapes, 0, escapei2);
                            }
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
    public static void unescape(char[] text, int offset, int len, Writer writer, XmlEscapeSymbols symbols) throws IOException {
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
            if (c == '&' && i + 1 < max) {
                int codepoint = 0;
                if (c == '&') {
                    char c1 = text[i + 1];
                    if (c1 != ' ' && c1 != '\n' && c1 != '\t' && c1 != '\f' && c1 != '<' && c1 != '&') {
                        if (c1 == '#') {
                            if (i + 2 < max) {
                                char c2 = text[i + 2];
                                if (c2 == 'x' && i + 3 < max) {
                                    int f = i + 3;
                                    while (f < max && (((cf3 = text[f]) >= '0' && cf3 <= '9') || ((cf3 >= 'A' && cf3 <= 'F') || (cf3 >= 'a' && cf3 <= 'f')))) {
                                        f++;
                                    }
                                    if (f - (i + 3) > 0 && f < max && text[f] == ';') {
                                        int f2 = f + 1;
                                        codepoint = parseIntFromReference(text, i + 3, f2 - 1, 16);
                                        referenceOffset = f2 - 1;
                                    }
                                } else if (c2 >= '0' && c2 <= '9') {
                                    int f3 = i + 2;
                                    while (f3 < max && (cf2 = text[f3]) >= '0' && cf2 <= '9') {
                                        f3++;
                                    }
                                    if (f3 - (i + 2) > 0 && f3 < max && text[f3] == ';') {
                                        int f4 = f3 + 1;
                                        codepoint = parseIntFromReference(text, i + 2, f4 - 1, 10);
                                        referenceOffset = f4 - 1;
                                    }
                                }
                            }
                        } else {
                            int f5 = i + 1;
                            while (f5 < max && (((cf = text[f5]) >= 'a' && cf <= 'z') || ((cf >= 'A' && cf <= 'Z') || (cf >= '0' && cf <= '9')))) {
                                f5++;
                            }
                            if (f5 - (i + 1) > 0) {
                                if (f5 < max && text[f5] == ';') {
                                    f5++;
                                }
                                int ncrPosition = XmlEscapeSymbols.binarySearch(symbols.SORTED_CERS, text, i, f5);
                                if (ncrPosition >= 0) {
                                    codepoint = symbols.SORTED_CODEPOINTS_BY_CER[ncrPosition];
                                    referenceOffset = f5 - 1;
                                }
                            }
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