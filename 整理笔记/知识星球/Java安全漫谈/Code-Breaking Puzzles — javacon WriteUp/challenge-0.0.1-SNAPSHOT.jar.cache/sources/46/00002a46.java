package org.unbescape.properties;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/properties/PropertiesEscape.class */
public final class PropertiesEscape {
    public static String escapePropertiesValueMinimal(String text) {
        return escapePropertiesValue(text, PropertiesValueEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static String escapePropertiesValue(String text) {
        return escapePropertiesValue(text, PropertiesValueEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static String escapePropertiesValue(String text, PropertiesValueEscapeLevel level) {
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        return PropertiesValueEscapeUtil.escape(text, level);
    }

    public static void escapePropertiesValueMinimal(String text, Writer writer) throws IOException {
        escapePropertiesValue(text, writer, PropertiesValueEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapePropertiesValue(String text, Writer writer) throws IOException {
        escapePropertiesValue(text, writer, PropertiesValueEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapePropertiesValue(String text, Writer writer, PropertiesValueEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        PropertiesValueEscapeUtil.escape(new InternalStringReader(text), writer, level);
    }

    public static void escapePropertiesValueMinimal(Reader reader, Writer writer) throws IOException {
        escapePropertiesValue(reader, writer, PropertiesValueEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapePropertiesValue(Reader reader, Writer writer) throws IOException {
        escapePropertiesValue(reader, writer, PropertiesValueEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapePropertiesValue(Reader reader, Writer writer, PropertiesValueEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        PropertiesValueEscapeUtil.escape(reader, writer, level);
    }

    public static void escapePropertiesValueMinimal(char[] text, int offset, int len, Writer writer) throws IOException {
        escapePropertiesValue(text, offset, len, writer, PropertiesValueEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapePropertiesValue(char[] text, int offset, int len, Writer writer) throws IOException {
        escapePropertiesValue(text, offset, len, writer, PropertiesValueEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapePropertiesValue(char[] text, int offset, int len, Writer writer, PropertiesValueEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
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
        PropertiesValueEscapeUtil.escape(text, offset, len, writer, level);
    }

    public static String escapePropertiesKeyMinimal(String text) {
        return escapePropertiesKey(text, PropertiesKeyEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static String escapePropertiesKey(String text) {
        return escapePropertiesKey(text, PropertiesKeyEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static String escapePropertiesKey(String text, PropertiesKeyEscapeLevel level) {
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        return PropertiesKeyEscapeUtil.escape(text, level);
    }

    public static void escapePropertiesKeyMinimal(String text, Writer writer) throws IOException {
        escapePropertiesKey(text, writer, PropertiesKeyEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapePropertiesKey(String text, Writer writer) throws IOException {
        escapePropertiesKey(text, writer, PropertiesKeyEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapePropertiesKey(String text, Writer writer, PropertiesKeyEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        PropertiesKeyEscapeUtil.escape(new InternalStringReader(text), writer, level);
    }

    public static void escapePropertiesKeyMinimal(Reader reader, Writer writer) throws IOException {
        escapePropertiesKey(reader, writer, PropertiesKeyEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapePropertiesKey(Reader reader, Writer writer) throws IOException {
        escapePropertiesKey(reader, writer, PropertiesKeyEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapePropertiesKey(Reader reader, Writer writer, PropertiesKeyEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        PropertiesKeyEscapeUtil.escape(reader, writer, level);
    }

    public static void escapePropertiesKeyMinimal(char[] text, int offset, int len, Writer writer) throws IOException {
        escapePropertiesKey(text, offset, len, writer, PropertiesKeyEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapePropertiesKey(char[] text, int offset, int len, Writer writer) throws IOException {
        escapePropertiesKey(text, offset, len, writer, PropertiesKeyEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapePropertiesKey(char[] text, int offset, int len, Writer writer, PropertiesKeyEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
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
        PropertiesKeyEscapeUtil.escape(text, offset, len, writer, level);
    }

    public static String unescapeProperties(String text) {
        if (text == null) {
            return null;
        }
        if (text.indexOf(92) < 0) {
            return text;
        }
        return PropertiesUnescapeUtil.unescape(text);
    }

    public static void unescapeProperties(String text, Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (text == null) {
            return;
        }
        if (text.indexOf(92) < 0) {
            writer.write(text);
        } else {
            PropertiesUnescapeUtil.unescape(new InternalStringReader(text), writer);
        }
    }

    public static void unescapeProperties(Reader reader, Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        PropertiesUnescapeUtil.unescape(reader, writer);
    }

    public static void unescapeProperties(char[] text, int offset, int len, Writer writer) throws IOException {
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
        PropertiesUnescapeUtil.unescape(text, offset, len, writer);
    }

    private PropertiesEscape() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/properties/PropertiesEscape$InternalStringReader.class */
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