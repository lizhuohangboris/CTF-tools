package org.unbescape.css;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/css/CssEscape.class */
public final class CssEscape {
    public static String escapeCssStringMinimal(String text) {
        return escapeCssString(text, CssStringEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA, CssStringEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static String escapeCssString(String text) {
        return escapeCssString(text, CssStringEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA, CssStringEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static String escapeCssString(String text, CssStringEscapeType type, CssStringEscapeLevel level) {
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        return CssStringEscapeUtil.escape(text, type, level);
    }

    public static void escapeCssStringMinimal(String text, Writer writer) throws IOException {
        escapeCssString(text, writer, CssStringEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA, CssStringEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapeCssString(String text, Writer writer) throws IOException {
        escapeCssString(text, writer, CssStringEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA, CssStringEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapeCssString(String text, Writer writer, CssStringEscapeType type, CssStringEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        CssStringEscapeUtil.escape(new InternalStringReader(text), writer, type, level);
    }

    public static void escapeCssStringMinimal(Reader reader, Writer writer) throws IOException {
        escapeCssString(reader, writer, CssStringEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA, CssStringEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapeCssString(Reader reader, Writer writer) throws IOException {
        escapeCssString(reader, writer, CssStringEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA, CssStringEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapeCssString(Reader reader, Writer writer, CssStringEscapeType type, CssStringEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        CssStringEscapeUtil.escape(reader, writer, type, level);
    }

    public static void escapeCssStringMinimal(char[] text, int offset, int len, Writer writer) throws IOException {
        escapeCssString(text, offset, len, writer, CssStringEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA, CssStringEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapeCssString(char[] text, int offset, int len, Writer writer) throws IOException {
        escapeCssString(text, offset, len, writer, CssStringEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA, CssStringEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapeCssString(char[] text, int offset, int len, Writer writer, CssStringEscapeType type, CssStringEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        int textLen = text == null ? 0 : text.length;
        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        if (len < 0 || offset + len > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        CssStringEscapeUtil.escape(text, offset, len, writer, type, level);
    }

    public static String escapeCssIdentifierMinimal(String text) {
        return escapeCssIdentifier(text, CssIdentifierEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA, CssIdentifierEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static String escapeCssIdentifier(String text) {
        return escapeCssIdentifier(text, CssIdentifierEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA, CssIdentifierEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static String escapeCssIdentifier(String text, CssIdentifierEscapeType type, CssIdentifierEscapeLevel level) {
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        return CssIdentifierEscapeUtil.escape(text, type, level);
    }

    public static void escapeCssIdentifierMinimal(String text, Writer writer) throws IOException {
        escapeCssIdentifier(text, writer, CssIdentifierEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA, CssIdentifierEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapeCssIdentifier(String text, Writer writer) throws IOException {
        escapeCssIdentifier(text, writer, CssIdentifierEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA, CssIdentifierEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapeCssIdentifier(String text, Writer writer, CssIdentifierEscapeType type, CssIdentifierEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        CssIdentifierEscapeUtil.escape(new InternalStringReader(text), writer, type, level);
    }

    public static void escapeCssIdentifierMinimal(Reader reader, Writer writer) throws IOException {
        escapeCssIdentifier(reader, writer, CssIdentifierEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA, CssIdentifierEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapeCssIdentifier(Reader reader, Writer writer) throws IOException {
        escapeCssIdentifier(reader, writer, CssIdentifierEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA, CssIdentifierEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapeCssIdentifier(Reader reader, Writer writer, CssIdentifierEscapeType type, CssIdentifierEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        CssIdentifierEscapeUtil.escape(reader, writer, type, level);
    }

    public static void escapeCssIdentifierMinimal(char[] text, int offset, int len, Writer writer) throws IOException {
        escapeCssIdentifier(text, offset, len, writer, CssIdentifierEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA, CssIdentifierEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapeCssIdentifier(char[] text, int offset, int len, Writer writer) throws IOException {
        escapeCssIdentifier(text, offset, len, writer, CssIdentifierEscapeType.BACKSLASH_ESCAPES_DEFAULT_TO_COMPACT_HEXA, CssIdentifierEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapeCssIdentifier(char[] text, int offset, int len, Writer writer, CssIdentifierEscapeType type, CssIdentifierEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        int textLen = text == null ? 0 : text.length;
        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        if (len < 0 || offset + len > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        CssIdentifierEscapeUtil.escape(text, offset, len, writer, type, level);
    }

    public static String unescapeCss(String text) {
        if (text == null) {
            return null;
        }
        if (text.indexOf(92) < 0) {
            return text;
        }
        return CssUnescapeUtil.unescape(text);
    }

    public static void unescapeCss(String text, Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (text == null) {
            return;
        }
        if (text.indexOf(92) < 0) {
            writer.write(text);
        } else {
            CssUnescapeUtil.unescape(new InternalStringReader(text), writer);
        }
    }

    public static void unescapeCss(Reader reader, Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        CssUnescapeUtil.unescape(reader, writer);
    }

    public static void unescapeCss(char[] text, int offset, int len, Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        int textLen = text == null ? 0 : text.length;
        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        if (len < 0 || offset + len > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        CssUnescapeUtil.unescape(text, offset, len, writer);
    }

    private CssEscape() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/css/CssEscape$InternalStringReader.class */
    public static final class InternalStringReader extends Reader {
        private String str;
        private int length;
        private int next = 0;

        public InternalStringReader(String s) {
            this.str = s;
            this.length = s.length();
        }

        @Override // java.io.Reader
        public int read() throws IOException {
            if (this.next >= this.length) {
                return -1;
            }
            String str = this.str;
            int i = this.next;
            this.next = i + 1;
            return str.charAt(i);
        }

        @Override // java.io.Reader
        public int read(char[] cbuf, int off, int len) throws IOException {
            if (off < 0 || off > cbuf.length || len < 0 || off + len > cbuf.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return 0;
            }
            if (this.next >= this.length) {
                return -1;
            }
            int n = Math.min(this.length - this.next, len);
            this.str.getChars(this.next, this.next + n, cbuf, off);
            this.next += n;
            return n;
        }

        @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this.str = null;
        }
    }
}