package org.unbescape.uri;

import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/uri/UriEscapeUtil.class */
final class UriEscapeUtil {
    private static final char ESCAPE_PREFIX = '%';
    private static char[] HEXA_CHARS_UPPER = "0123456789ABCDEF".toCharArray();
    private static char[] HEXA_CHARS_LOWER = "0123456789abcdef".toCharArray();

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/uri/UriEscapeUtil$UriEscapeType.class */
    enum UriEscapeType {
        PATH { // from class: org.unbescape.uri.UriEscapeUtil.UriEscapeType.1
            @Override // org.unbescape.uri.UriEscapeUtil.UriEscapeType
            public boolean isAllowed(int c) {
                return UriEscapeType.isPchar(c) || 47 == c;
            }
        },
        PATH_SEGMENT { // from class: org.unbescape.uri.UriEscapeUtil.UriEscapeType.2
            @Override // org.unbescape.uri.UriEscapeUtil.UriEscapeType
            public boolean isAllowed(int c) {
                return UriEscapeType.isPchar(c);
            }
        },
        QUERY_PARAM { // from class: org.unbescape.uri.UriEscapeUtil.UriEscapeType.3
            @Override // org.unbescape.uri.UriEscapeUtil.UriEscapeType
            public boolean isAllowed(int c) {
                if (61 == c || 38 == c || 43 == c || 35 == c) {
                    return false;
                }
                return UriEscapeType.isPchar(c) || 47 == c || 63 == c;
            }

            @Override // org.unbescape.uri.UriEscapeUtil.UriEscapeType
            public boolean canPlusEscapeWhitespace() {
                return true;
            }
        },
        FRAGMENT_ID { // from class: org.unbescape.uri.UriEscapeUtil.UriEscapeType.4
            @Override // org.unbescape.uri.UriEscapeUtil.UriEscapeType
            public boolean isAllowed(int c) {
                return UriEscapeType.isPchar(c) || 47 == c || 63 == c;
            }
        };

        public abstract boolean isAllowed(int i);

        public boolean canPlusEscapeWhitespace() {
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static boolean isPchar(int c) {
            return isUnreserved(c) || isSubDelim(c) || 58 == c || 64 == c;
        }

        private static boolean isUnreserved(int c) {
            return isAlpha(c) || isDigit(c) || 45 == c || 46 == c || 95 == c || 126 == c;
        }

        private static boolean isReserved(int c) {
            return isGenDelim(c) || isSubDelim(c);
        }

        private static boolean isSubDelim(int c) {
            return 33 == c || 36 == c || 38 == c || 39 == c || 40 == c || 41 == c || 42 == c || 43 == c || 44 == c || 59 == c || 61 == c;
        }

        private static boolean isGenDelim(int c) {
            return 58 == c || 47 == c || 63 == c || 35 == c || 91 == c || 93 == c || 64 == c;
        }

        static boolean isAlpha(int c) {
            return (c >= 65 && c <= 90) || (c >= 97 && c <= 122);
        }

        private static boolean isDigit(int c) {
            return c >= 48 && c <= 57;
        }
    }

    private UriEscapeUtil() {
    }

    static char[] printHexa(byte b) {
        char[] result = {HEXA_CHARS_UPPER[(b >> 4) & 15], HEXA_CHARS_UPPER[b & 15]};
        return result;
    }

    static byte parseHexa(char c1, char c2) {
        byte result = 0;
        for (int j = 0; j < HEXA_CHARS_UPPER.length; j++) {
            if (c1 == HEXA_CHARS_UPPER[j] || c1 == HEXA_CHARS_LOWER[j]) {
                result = (byte) (0 + (j << 4));
                break;
            }
        }
        for (int j2 = 0; j2 < HEXA_CHARS_UPPER.length; j2++) {
            if (c2 == HEXA_CHARS_UPPER[j2] || c2 == HEXA_CHARS_LOWER[j2]) {
                result = (byte) (result + j2);
                break;
            }
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String escape(String text, UriEscapeType escapeType, String encoding) {
        if (text == null) {
            return null;
        }
        StringBuilder strBuilder = null;
        int max = text.length();
        int readOffset = 0;
        int i = 0;
        while (i < max) {
            int codepoint = Character.codePointAt(text, i);
            if (!UriEscapeType.isAlpha(codepoint) && !escapeType.isAllowed(codepoint)) {
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
                try {
                    byte[] charAsBytes = new String(Character.toChars(codepoint)).getBytes(encoding);
                    for (byte b : charAsBytes) {
                        strBuilder.append('%');
                        strBuilder.append(printHexa(b));
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalArgumentException("Exception while escaping URI: Bad encoding '" + encoding + "'", e);
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
    public static void escape(Reader reader, Writer writer, UriEscapeType escapeType, String encoding) throws IOException {
        if (reader == null) {
            return;
        }
        int c2 = reader.read();
        while (c2 >= 0) {
            int c1 = c2;
            c2 = reader.read();
            int codepoint = codePointAt((char) c1, (char) c2);
            if (UriEscapeType.isAlpha(codepoint)) {
                writer.write(c1);
            } else if (escapeType.isAllowed(codepoint)) {
                writer.write(c1);
            } else {
                if (Character.charCount(codepoint) > 1) {
                    c2 = reader.read();
                }
                try {
                    byte[] charAsBytes = new String(Character.toChars(codepoint)).getBytes(encoding);
                    for (byte b : charAsBytes) {
                        writer.write(37);
                        writer.write(printHexa(b));
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalArgumentException("Exception while escaping URI: Bad encoding '" + encoding + "'", e);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void escape(char[] text, int offset, int len, Writer writer, UriEscapeType escapeType, String encoding) throws IOException {
        if (text == null || text.length == 0) {
            return;
        }
        int max = offset + len;
        int readOffset = offset;
        int i = offset;
        while (i < max) {
            int codepoint = Character.codePointAt(text, i);
            if (!UriEscapeType.isAlpha(codepoint) && !escapeType.isAllowed(codepoint)) {
                if (i - readOffset > 0) {
                    writer.write(text, readOffset, i - readOffset);
                }
                if (Character.charCount(codepoint) > 1) {
                    i++;
                }
                readOffset = i + 1;
                try {
                    byte[] charAsBytes = new String(Character.toChars(codepoint)).getBytes(encoding);
                    for (byte b : charAsBytes) {
                        writer.write(37);
                        writer.write(printHexa(b));
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalArgumentException("Exception while escaping URI: Bad encoding '" + encoding + "'", e);
                }
            }
            i++;
        }
        if (max - readOffset > 0) {
            writer.write(text, readOffset, max - readOffset);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String unescape(String text, UriEscapeType escapeType, String encoding) {
        if (text == null) {
            return null;
        }
        StringBuilder strBuilder = null;
        int max = text.length();
        int readOffset = 0;
        int i = 0;
        while (i < max) {
            char c = text.charAt(i);
            if (c == '%' || (c == '+' && escapeType.canPlusEscapeWhitespace())) {
                if (strBuilder == null) {
                    strBuilder = new StringBuilder(max + 5);
                }
                if (i - readOffset > 0) {
                    strBuilder.append((CharSequence) text, readOffset, i);
                }
                if (c == '+') {
                    strBuilder.append(' ');
                    readOffset = i + 1;
                } else {
                    byte[] bytes = new byte[(max - i) / 3];
                    char aheadC = c;
                    int pos = 0;
                    while (i + 2 < max && aheadC == '%') {
                        int i2 = pos;
                        pos++;
                        bytes[i2] = parseHexa(text.charAt(i + 1), text.charAt(i + 2));
                        i += 3;
                        if (i < max) {
                            aheadC = text.charAt(i);
                        }
                    }
                    if (i < max && aheadC == '%') {
                        throw new IllegalArgumentException("Incomplete escaping sequence in input");
                    }
                    try {
                        strBuilder.append(new String(bytes, 0, pos, encoding));
                        readOffset = i;
                    } catch (UnsupportedEncodingException e) {
                        throw new IllegalArgumentException("Exception while escaping URI: Bad encoding '" + encoding + "'", e);
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
    public static void unescape(Reader reader, Writer writer, UriEscapeType escapeType, String encoding) throws IOException {
        if (reader == null) {
            return;
        }
        byte[] escapes = new byte[4];
        int c2 = reader.read();
        while (c2 >= 0) {
            int c1 = c2;
            c2 = reader.read();
            if ((c1 != 37 || c2 < 0) && (c1 != 43 || !escapeType.canPlusEscapeWhitespace())) {
                writer.write(c1);
            } else if (c1 == 43) {
                writer.write(32);
            } else {
                int pos = 0;
                int ce0 = c1;
                int ce1 = c2;
                int read = reader.read();
                while (true) {
                    int ce2 = read;
                    if (ce0 != 37 || ce1 < 0 || ce2 < 0) {
                        break;
                    }
                    if (pos == escapes.length) {
                        byte[] newEscapes = new byte[escapes.length + 4];
                        System.arraycopy(escapes, 0, newEscapes, 0, escapes.length);
                        escapes = newEscapes;
                    }
                    int i = pos;
                    pos++;
                    escapes[i] = parseHexa((char) ce1, (char) ce2);
                    ce0 = reader.read();
                    ce1 = ce0 < 0 ? ce0 : ce0 != 37 ? 0 : reader.read();
                    read = ce1 < 0 ? ce1 : ce0 != 37 ? 0 : reader.read();
                }
                if (ce0 == 37) {
                    throw new IllegalArgumentException("Incomplete escaping sequence in input");
                }
                c2 = ce0;
                try {
                    writer.write(new String(escapes, 0, pos, encoding));
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalArgumentException("Exception while escaping URI: Bad encoding '" + encoding + "'", e);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void unescape(char[] text, int offset, int len, Writer writer, UriEscapeType escapeType, String encoding) throws IOException {
        if (text == null) {
            return;
        }
        int max = offset + len;
        int readOffset = offset;
        int i = offset;
        while (i < max) {
            char c = text[i];
            if (c == '%' || (c == '+' && escapeType.canPlusEscapeWhitespace())) {
                if (i - readOffset > 0) {
                    writer.write(text, readOffset, i - readOffset);
                }
                if (c == '+') {
                    writer.write(32);
                    readOffset = i + 1;
                } else {
                    byte[] bytes = new byte[(max - i) / 3];
                    char aheadC = c;
                    int pos = 0;
                    while (i + 2 < max && aheadC == '%') {
                        int i2 = pos;
                        pos++;
                        bytes[i2] = parseHexa(text[i + 1], text[i + 2]);
                        i += 3;
                        if (i < max) {
                            aheadC = text[i];
                        }
                    }
                    if (i < max && aheadC == '%') {
                        throw new IllegalArgumentException("Incomplete escaping sequence in input");
                    }
                    try {
                        writer.write(new String(bytes, 0, pos, encoding));
                        readOffset = i;
                    } catch (UnsupportedEncodingException e) {
                        throw new IllegalArgumentException("Exception while escaping URI: Bad encoding '" + encoding + "'", e);
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