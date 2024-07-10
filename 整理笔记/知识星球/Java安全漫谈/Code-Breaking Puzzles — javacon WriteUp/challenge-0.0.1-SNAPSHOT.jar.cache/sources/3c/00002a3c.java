package org.unbescape.javascript;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/javascript/JavaScriptEscape.class */
public final class JavaScriptEscape {
    public static String escapeJavaScriptMinimal(String text) {
        return escapeJavaScript(text, JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA, JavaScriptEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static String escapeJavaScript(String text) {
        return escapeJavaScript(text, JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA, JavaScriptEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static String escapeJavaScript(String text, JavaScriptEscapeType type, JavaScriptEscapeLevel level) {
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        return JavaScriptEscapeUtil.escape(text, type, level);
    }

    public static void escapeJavaScriptMinimal(String text, Writer writer) throws IOException {
        escapeJavaScript(text, writer, JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA, JavaScriptEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapeJavaScript(String text, Writer writer) throws IOException {
        escapeJavaScript(text, writer, JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA, JavaScriptEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapeJavaScript(String text, Writer writer, JavaScriptEscapeType type, JavaScriptEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        JavaScriptEscapeUtil.escape(new InternalStringReader(text), writer, type, level);
    }

    public static void escapeJavaScriptMinimal(Reader reader, Writer writer) throws IOException {
        escapeJavaScript(reader, writer, JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA, JavaScriptEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapeJavaScript(Reader reader, Writer writer) throws IOException {
        escapeJavaScript(reader, writer, JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA, JavaScriptEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapeJavaScript(Reader reader, Writer writer, JavaScriptEscapeType type, JavaScriptEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        JavaScriptEscapeUtil.escape(reader, writer, type, level);
    }

    public static void escapeJavaScriptMinimal(char[] text, int offset, int len, Writer writer) throws IOException {
        escapeJavaScript(text, offset, len, writer, JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA, JavaScriptEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapeJavaScript(char[] text, int offset, int len, Writer writer) throws IOException {
        escapeJavaScript(text, offset, len, writer, JavaScriptEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_XHEXA_AND_UHEXA, JavaScriptEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapeJavaScript(char[] text, int offset, int len, Writer writer, JavaScriptEscapeType type, JavaScriptEscapeLevel level) throws IOException {
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
        JavaScriptEscapeUtil.escape(text, offset, len, writer, type, level);
    }

    public static String unescapeJavaScript(String text) {
        if (text == null) {
            return null;
        }
        if (text.indexOf(92) < 0) {
            return text;
        }
        return JavaScriptEscapeUtil.unescape(text);
    }

    public static void unescapeJavaScript(String text, Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (text == null) {
            return;
        }
        if (text.indexOf(92) < 0) {
            writer.write(text);
        } else {
            JavaScriptEscapeUtil.unescape(new InternalStringReader(text), writer);
        }
    }

    public static void unescapeJavaScript(Reader reader, Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        JavaScriptEscapeUtil.unescape(reader, writer);
    }

    public static void unescapeJavaScript(char[] text, int offset, int len, Writer writer) throws IOException {
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
        JavaScriptEscapeUtil.unescape(text, offset, len, writer);
    }

    private JavaScriptEscape() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/javascript/JavaScriptEscape$InternalStringReader.class */
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