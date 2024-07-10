package org.unbescape.html;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/html/HtmlEscape.class */
public final class HtmlEscape {
    public static String escapeHtml5(String text) {
        return escapeHtml(text, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static String escapeHtml5Xml(String text) {
        return escapeHtml(text, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, HtmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static String escapeHtml4(String text) {
        return escapeHtml(text, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static String escapeHtml4Xml(String text) {
        return escapeHtml(text, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, HtmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static String escapeHtml(String text, HtmlEscapeType type, HtmlEscapeLevel level) {
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        return HtmlEscapeUtil.escape(text, type, level);
    }

    public static void escapeHtml5(String text, Writer writer) throws IOException {
        escapeHtml(text, writer, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static void escapeHtml5Xml(String text, Writer writer) throws IOException {
        escapeHtml(text, writer, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, HtmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static void escapeHtml4(String text, Writer writer) throws IOException {
        escapeHtml(text, writer, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static void escapeHtml4Xml(String text, Writer writer) throws IOException {
        escapeHtml(text, writer, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, HtmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static void escapeHtml(String text, Writer writer, HtmlEscapeType type, HtmlEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        HtmlEscapeUtil.escape(new InternalStringReader(text), writer, type, level);
    }

    public static void escapeHtml5(Reader reader, Writer writer) throws IOException {
        escapeHtml(reader, writer, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static void escapeHtml5Xml(Reader reader, Writer writer) throws IOException {
        escapeHtml(reader, writer, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, HtmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static void escapeHtml4(Reader reader, Writer writer) throws IOException {
        escapeHtml(reader, writer, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static void escapeHtml4Xml(Reader reader, Writer writer) throws IOException {
        escapeHtml(reader, writer, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, HtmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static void escapeHtml(Reader reader, Writer writer, HtmlEscapeType type, HtmlEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        HtmlEscapeUtil.escape(reader, writer, type, level);
    }

    public static void escapeHtml5(char[] text, int offset, int len, Writer writer) throws IOException {
        escapeHtml(text, offset, len, writer, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static void escapeHtml5Xml(char[] text, int offset, int len, Writer writer) throws IOException {
        escapeHtml(text, offset, len, writer, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, HtmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static void escapeHtml4(char[] text, int offset, int len, Writer writer) throws IOException {
        escapeHtml(text, offset, len, writer, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, HtmlEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_MARKUP_SIGNIFICANT);
    }

    public static void escapeHtml4Xml(char[] text, int offset, int len, Writer writer) throws IOException {
        escapeHtml(text, offset, len, writer, HtmlEscapeType.HTML4_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, HtmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT);
    }

    public static void escapeHtml(char[] text, int offset, int len, Writer writer, HtmlEscapeType type, HtmlEscapeLevel level) throws IOException {
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
        HtmlEscapeUtil.escape(text, offset, len, writer, type, level);
    }

    public static String unescapeHtml(String text) {
        if (text == null) {
            return null;
        }
        if (text.indexOf(38) < 0) {
            return text;
        }
        return HtmlEscapeUtil.unescape(text);
    }

    public static void unescapeHtml(String text, Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (text == null) {
            return;
        }
        if (text.indexOf(38) < 0) {
            writer.write(text);
        } else {
            HtmlEscapeUtil.unescape(new InternalStringReader(text), writer);
        }
    }

    public static void unescapeHtml(Reader reader, Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        HtmlEscapeUtil.unescape(reader, writer);
    }

    public static void unescapeHtml(char[] text, int offset, int len, Writer writer) throws IOException {
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
        HtmlEscapeUtil.unescape(text, offset, len, writer);
    }

    private HtmlEscape() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/html/HtmlEscape$InternalStringReader.class */
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