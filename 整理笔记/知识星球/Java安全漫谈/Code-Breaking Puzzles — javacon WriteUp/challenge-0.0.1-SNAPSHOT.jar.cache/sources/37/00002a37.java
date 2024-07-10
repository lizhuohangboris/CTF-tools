package org.unbescape.html;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import javax.servlet.http.HttpServletResponse;
import org.springframework.asm.Opcodes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/html/HtmlEscapeUtil.class */
final class HtmlEscapeUtil {
    private static final char REFERENCE_PREFIX = '&';
    private static final char REFERENCE_NUMERIC_PREFIX2 = '#';
    private static final char REFERENCE_HEXA_PREFIX3_UPPER = 'X';
    private static final char REFERENCE_HEXA_PREFIX3_LOWER = 'x';
    private static final char REFERENCE_SUFFIX = ';';
    private static final char[] REFERENCE_DECIMAL_PREFIX = "&#".toCharArray();
    private static final char[] REFERENCE_HEXA_PREFIX = "&#x".toCharArray();
    private static char[] HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
    private static char[] HEXA_CHARS_LOWER = "0123456789abcdef".toCharArray();

    private HtmlEscapeUtil() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String escape(String text, HtmlEscapeType escapeType, HtmlEscapeLevel escapeLevel) {
        Short ncrIndex;
        if (text == null) {
            return null;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useHtml5 = escapeType.getUseHtml5();
        boolean useNCRs = escapeType.getUseNCRs();
        boolean useHexa = escapeType.getUseHexa();
        HtmlEscapeSymbols symbols = useHtml5 ? HtmlEscapeSymbols.HTML5_SYMBOLS : HtmlEscapeSymbols.HTML4_SYMBOLS;
        StringBuilder strBuilder = null;
        int max = text.length();
        int readOffset = 0;
        int i = 0;
        while (i < max) {
            char c = text.charAt(i);
            if ((c > 127 || level >= symbols.ESCAPE_LEVELS[c]) && (c <= 127 || level >= symbols.ESCAPE_LEVELS[128])) {
                int codepoint = Character.codePointAt(text, i);
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
                if (useNCRs) {
                    if (codepoint < 12287) {
                        short ncrIndex2 = symbols.NCRS_BY_CODEPOINT[codepoint];
                        if (ncrIndex2 != 0) {
                            strBuilder.append(symbols.SORTED_NCRS[ncrIndex2]);
                        }
                    } else if (symbols.NCRS_BY_CODEPOINT_OVERFLOW != null && (ncrIndex = symbols.NCRS_BY_CODEPOINT_OVERFLOW.get(Integer.valueOf(codepoint))) != null) {
                        strBuilder.append(symbols.SORTED_NCRS[ncrIndex.shortValue()]);
                    }
                }
                if (useHexa) {
                    strBuilder.append(REFERENCE_HEXA_PREFIX);
                    strBuilder.append(Integer.toHexString(codepoint));
                } else {
                    strBuilder.append(REFERENCE_DECIMAL_PREFIX);
                    strBuilder.append(String.valueOf(codepoint));
                }
                strBuilder.append(';');
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
    public static void escape(Reader reader, Writer writer, HtmlEscapeType escapeType, HtmlEscapeLevel escapeLevel) throws IOException {
        Short ncrIndex;
        if (reader == null) {
            return;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useHtml5 = escapeType.getUseHtml5();
        boolean useNCRs = escapeType.getUseNCRs();
        boolean useHexa = escapeType.getUseHexa();
        HtmlEscapeSymbols symbols = useHtml5 ? HtmlEscapeSymbols.HTML5_SYMBOLS : HtmlEscapeSymbols.HTML4_SYMBOLS;
        int c2 = reader.read();
        while (c2 >= 0) {
            int c1 = c2;
            c2 = reader.read();
            if (c1 <= 127 && level < symbols.ESCAPE_LEVELS[c1]) {
                writer.write(c1);
            } else if (c1 > 127 && level < symbols.ESCAPE_LEVELS[128]) {
                writer.write(c1);
            } else {
                int codepoint = codePointAt((char) c1, (char) c2);
                if (Character.charCount(codepoint) > 1) {
                    c2 = reader.read();
                }
                if (useNCRs) {
                    if (codepoint < 12287) {
                        short ncrIndex2 = symbols.NCRS_BY_CODEPOINT[codepoint];
                        if (ncrIndex2 != 0) {
                            writer.write(symbols.SORTED_NCRS[ncrIndex2]);
                        }
                    } else if (symbols.NCRS_BY_CODEPOINT_OVERFLOW != null && (ncrIndex = symbols.NCRS_BY_CODEPOINT_OVERFLOW.get(Integer.valueOf(codepoint))) != null) {
                        writer.write(symbols.SORTED_NCRS[ncrIndex.shortValue()]);
                    }
                }
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

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void escape(char[] text, int offset, int len, Writer writer, HtmlEscapeType escapeType, HtmlEscapeLevel escapeLevel) throws IOException {
        Short ncrIndex;
        if (text == null || text.length == 0) {
            return;
        }
        int level = escapeLevel.getEscapeLevel();
        boolean useHtml5 = escapeType.getUseHtml5();
        boolean useNCRs = escapeType.getUseNCRs();
        boolean useHexa = escapeType.getUseHexa();
        HtmlEscapeSymbols symbols = useHtml5 ? HtmlEscapeSymbols.HTML5_SYMBOLS : HtmlEscapeSymbols.HTML4_SYMBOLS;
        int max = offset + len;
        int readOffset = offset;
        int i = offset;
        while (i < max) {
            char c = text[i];
            if ((c > 127 || level >= symbols.ESCAPE_LEVELS[c]) && (c <= 127 || level >= symbols.ESCAPE_LEVELS[127 + 1])) {
                int codepoint = Character.codePointAt(text, i);
                if (i - readOffset > 0) {
                    writer.write(text, readOffset, i - readOffset);
                }
                if (Character.charCount(codepoint) > 1) {
                    i++;
                }
                readOffset = i + 1;
                if (useNCRs) {
                    if (codepoint < 12287) {
                        short ncrIndex2 = symbols.NCRS_BY_CODEPOINT[codepoint];
                        if (ncrIndex2 != 0) {
                            writer.write(symbols.SORTED_NCRS[ncrIndex2]);
                        }
                    } else if (symbols.NCRS_BY_CODEPOINT_OVERFLOW != null && (ncrIndex = symbols.NCRS_BY_CODEPOINT_OVERFLOW.get(Integer.valueOf(codepoint))) != null) {
                        writer.write(symbols.SORTED_NCRS[ncrIndex.shortValue()]);
                    }
                }
                if (useHexa) {
                    writer.write(REFERENCE_HEXA_PREFIX);
                    writer.write(Integer.toHexString(codepoint));
                } else {
                    writer.write(REFERENCE_DECIMAL_PREFIX);
                    writer.write(String.valueOf(codepoint));
                }
                writer.write(59);
            }
            i++;
        }
        if (max - readOffset > 0) {
            writer.write(text, readOffset, max - readOffset);
        }
    }

    static int translateIllFormedCodepoint(int codepoint) {
        switch (codepoint) {
            case 0:
                return 65533;
            case 128:
                return 8364;
            case 130:
                return 8218;
            case Opcodes.LXOR /* 131 */:
                return HttpServletResponse.SC_PAYMENT_REQUIRED;
            case 132:
                return 8222;
            case Opcodes.I2L /* 133 */:
                return 8230;
            case Opcodes.I2F /* 134 */:
                return 8224;
            case Opcodes.I2D /* 135 */:
                return 8225;
            case 136:
                return 710;
            case Opcodes.L2F /* 137 */:
                return 8240;
            case Opcodes.L2D /* 138 */:
                return 352;
            case Opcodes.F2I /* 139 */:
                return 8249;
            case Opcodes.F2L /* 140 */:
                return 338;
            case Opcodes.D2I /* 142 */:
                return 381;
            case Opcodes.I2B /* 145 */:
                return 8216;
            case Opcodes.I2C /* 146 */:
                return 8217;
            case Opcodes.I2S /* 147 */:
                return 8220;
            case Opcodes.LCMP /* 148 */:
                return 8221;
            case Opcodes.FCMPL /* 149 */:
                return 8226;
            case 150:
                return 8211;
            case Opcodes.DCMPL /* 151 */:
                return 8212;
            case 152:
                return 732;
            case 153:
                return 8482;
            case 154:
                return 353;
            case 155:
                return 8250;
            case 156:
                return 339;
            case 158:
                return 382;
            case Opcodes.IF_ICMPEQ /* 159 */:
                return 376;
            default:
                if ((codepoint >= 55296 && codepoint <= 57343) || codepoint > 1114111) {
                    return 65533;
                }
                return codepoint;
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
            int result2 = result * radix;
            if (result2 < 0) {
                return 65533;
            }
            result = result2 + n;
            if (result < 0) {
                return 65533;
            }
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
            int result2 = result * radix;
            if (result2 < 0) {
                return 65533;
            }
            result = result2 + n;
            if (result < 0) {
                return 65533;
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String unescape(String text) {
        char cf;
        char cf2;
        char cf3;
        if (text == null) {
            return null;
        }
        HtmlEscapeSymbols symbols = HtmlEscapeSymbols.HTML5_SYMBOLS;
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
                                if ((c2 == 'x' || c2 == 'X') && i + 3 < max) {
                                    int f = i + 3;
                                    while (f < max && (((cf2 = text.charAt(f)) >= '0' && cf2 <= '9') || ((cf2 >= 'A' && cf2 <= 'F') || (cf2 >= 'a' && cf2 <= 'f')))) {
                                        f++;
                                    }
                                    if (f - (i + 3) > 0) {
                                        int codepoint2 = parseIntFromReference(text, i + 3, f, 16);
                                        referenceOffset = f - 1;
                                        if (f < max && text.charAt(f) == ';') {
                                            referenceOffset++;
                                        }
                                        codepoint = translateIllFormedCodepoint(codepoint2);
                                    }
                                } else if (c2 >= '0' && c2 <= '9') {
                                    int f2 = i + 2;
                                    while (f2 < max && (cf3 = text.charAt(f2)) >= '0' && cf3 <= '9') {
                                        f2++;
                                    }
                                    if (f2 - (i + 2) > 0) {
                                        int codepoint3 = parseIntFromReference(text, i + 2, f2, 10);
                                        referenceOffset = f2 - 1;
                                        if (f2 < max && text.charAt(f2) == ';') {
                                            referenceOffset++;
                                        }
                                        codepoint = translateIllFormedCodepoint(codepoint3);
                                    }
                                }
                            }
                        } else {
                            int f3 = i + 1;
                            while (f3 < max && (((cf = text.charAt(f3)) >= 'a' && cf <= 'z') || ((cf >= 'A' && cf <= 'Z') || (cf >= '0' && cf <= '9')))) {
                                f3++;
                            }
                            if (f3 - (i + 1) > 0) {
                                if (f3 < max && text.charAt(f3) == ';') {
                                    f3++;
                                }
                                int ncrPosition = HtmlEscapeSymbols.binarySearch(symbols.SORTED_NCRS, text, i, f3);
                                if (ncrPosition >= 0) {
                                    codepoint = symbols.SORTED_CODEPOINTS[ncrPosition];
                                } else if (ncrPosition == Integer.MIN_VALUE) {
                                    continue;
                                } else if (ncrPosition < -10) {
                                    int partialIndex = (-1) * (ncrPosition + 10);
                                    char[] partialMatch = symbols.SORTED_NCRS[partialIndex];
                                    codepoint = symbols.SORTED_CODEPOINTS[partialIndex];
                                    f3 -= (f3 - i) - partialMatch.length;
                                } else {
                                    throw new RuntimeException("Invalid unescape codepoint after search: " + ncrPosition);
                                }
                                referenceOffset = f3 - 1;
                            } else {
                                continue;
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
                } else if (codepoint < 0) {
                    int[] codepoints = symbols.DOUBLE_CODEPOINTS[((-1) * codepoint) - 1];
                    if (codepoints[0] > 65535) {
                        strBuilder.append(Character.toChars(codepoints[0]));
                    } else {
                        strBuilder.append((char) codepoints[0]);
                    }
                    if (codepoints[1] > 65535) {
                        strBuilder.append(Character.toChars(codepoints[1]));
                    } else {
                        strBuilder.append((char) codepoints[1]);
                    }
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
        HtmlEscapeSymbols symbols = HtmlEscapeSymbols.HTML5_SYMBOLS;
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
                        } else if (c3 == 120 || c3 == 88) {
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
                            } else {
                                char c = escapes[escapei - 1];
                                c2 = ce;
                                int codepoint2 = parseIntFromReference(escapes, 0, escapei, 16);
                                if (c2 == 59) {
                                    c2 = reader.read();
                                }
                                codepoint = translateIllFormedCodepoint(codepoint2);
                                escapei = 0;
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
                            } else {
                                char c4 = escapes[escapei - 1];
                                c2 = ce2;
                                int codepoint3 = parseIntFromReference(escapes, 0, escapei, 10);
                                if (c2 == 59) {
                                    c2 = reader.read();
                                }
                                codepoint = translateIllFormedCodepoint(codepoint3);
                                escapei = 0;
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
                            int ncrPosition = HtmlEscapeSymbols.binarySearch(symbols.SORTED_NCRS, escapes, 0, escapei2);
                            if (ncrPosition >= 0) {
                                codepoint = symbols.SORTED_CODEPOINTS[ncrPosition];
                                escapei = 0;
                            } else if (ncrPosition == Integer.MIN_VALUE) {
                                writer.write(escapes, 0, escapei2);
                            } else if (ncrPosition < -10) {
                                int partialIndex = (-1) * (ncrPosition + 10);
                                char[] partialMatch = symbols.SORTED_NCRS[partialIndex];
                                codepoint = symbols.SORTED_CODEPOINTS[partialIndex];
                                System.arraycopy(escapes, partialMatch.length, escapes, 0, escapei2 - partialMatch.length);
                                escapei = escapei2 - partialMatch.length;
                            } else {
                                throw new RuntimeException("Invalid unescape codepoint after search: " + ncrPosition);
                            }
                        }
                    }
                }
                if (codepoint > 65535) {
                    writer.write(Character.toChars(codepoint));
                } else if (codepoint < 0) {
                    int[] codepoints = symbols.DOUBLE_CODEPOINTS[((-1) * codepoint) - 1];
                    if (codepoints[0] > 65535) {
                        writer.write(Character.toChars(codepoints[0]));
                    } else {
                        writer.write((char) codepoints[0]);
                    }
                    if (codepoints[1] > 65535) {
                        writer.write(Character.toChars(codepoints[1]));
                    } else {
                        writer.write((char) codepoints[1]);
                    }
                } else {
                    writer.write((char) codepoint);
                }
                if (escapei > 0) {
                    writer.write(escapes, 0, escapei);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void unescape(char[] text, int offset, int len, Writer writer) throws IOException {
        char cf;
        char cf2;
        char cf3;
        if (text == null) {
            return;
        }
        HtmlEscapeSymbols symbols = HtmlEscapeSymbols.HTML5_SYMBOLS;
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
                                if ((c2 == 'x' || c2 == 'X') && i + 3 < max) {
                                    int f = i + 3;
                                    while (f < max && (((cf2 = text[f]) >= '0' && cf2 <= '9') || ((cf2 >= 'A' && cf2 <= 'F') || (cf2 >= 'a' && cf2 <= 'f')))) {
                                        f++;
                                    }
                                    if (f - (i + 3) > 0) {
                                        int codepoint2 = parseIntFromReference(text, i + 3, f, 16);
                                        referenceOffset = f - 1;
                                        if (f < max && text[f] == ';') {
                                            referenceOffset++;
                                        }
                                        codepoint = translateIllFormedCodepoint(codepoint2);
                                    }
                                } else if (c2 >= '0' && c2 <= '9') {
                                    int f2 = i + 2;
                                    while (f2 < max && (cf3 = text[f2]) >= '0' && cf3 <= '9') {
                                        f2++;
                                    }
                                    if (f2 - (i + 2) > 0) {
                                        int codepoint3 = parseIntFromReference(text, i + 2, f2, 10);
                                        referenceOffset = f2 - 1;
                                        if (f2 < max && text[f2] == ';') {
                                            referenceOffset++;
                                        }
                                        codepoint = translateIllFormedCodepoint(codepoint3);
                                    }
                                }
                            }
                        } else {
                            int f3 = i + 1;
                            while (f3 < max && (((cf = text[f3]) >= 'a' && cf <= 'z') || ((cf >= 'A' && cf <= 'Z') || (cf >= '0' && cf <= '9')))) {
                                f3++;
                            }
                            if (f3 - (i + 1) > 0) {
                                if (f3 < max && text[f3] == ';') {
                                    f3++;
                                }
                                int ncrPosition = HtmlEscapeSymbols.binarySearch(symbols.SORTED_NCRS, text, i, f3);
                                if (ncrPosition >= 0) {
                                    codepoint = symbols.SORTED_CODEPOINTS[ncrPosition];
                                } else if (ncrPosition == Integer.MIN_VALUE) {
                                    continue;
                                } else if (ncrPosition < -10) {
                                    int partialIndex = (-1) * (ncrPosition + 10);
                                    char[] partialMatch = symbols.SORTED_NCRS[partialIndex];
                                    codepoint = symbols.SORTED_CODEPOINTS[partialIndex];
                                    f3 -= (f3 - i) - partialMatch.length;
                                } else {
                                    throw new RuntimeException("Invalid unescape codepoint after search: " + ncrPosition);
                                }
                                referenceOffset = f3 - 1;
                            } else {
                                continue;
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
                } else if (codepoint < 0) {
                    int[] codepoints = symbols.DOUBLE_CODEPOINTS[((-1) * codepoint) - 1];
                    if (codepoints[0] > 65535) {
                        writer.write(Character.toChars(codepoints[0]));
                    } else {
                        writer.write((char) codepoints[0]);
                    }
                    if (codepoints[1] > 65535) {
                        writer.write(Character.toChars(codepoints[1]));
                    } else {
                        writer.write((char) codepoints[1]);
                    }
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