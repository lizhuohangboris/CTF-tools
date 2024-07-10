package org.unbescape.java;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/java/JavaEscape.class */
public final class JavaEscape {
    public static String escapeJavaMinimal(String text) {
        return escapeJava(text, JavaEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static String escapeJava(String text) {
        return escapeJava(text, JavaEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static String escapeJava(String text, JavaEscapeLevel level) {
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        return JavaEscapeUtil.escape(text, level);
    }

    public static void escapeJavaMinimal(String text, Writer writer) throws IOException {
        escapeJava(text, writer, JavaEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapeJava(String text, Writer writer) throws IOException {
        escapeJava(text, writer, JavaEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapeJava(String text, Writer writer, JavaEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        JavaEscapeUtil.escape(new InternalStringReader(text), writer, level);
    }

    public static void escapeJavaMinimal(Reader reader, Writer writer) throws IOException {
        escapeJava(reader, writer, JavaEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapeJava(Reader reader, Writer writer) throws IOException {
        escapeJava(reader, writer, JavaEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapeJava(Reader reader, Writer writer, JavaEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        JavaEscapeUtil.escape(reader, writer, level);
    }

    public static void escapeJavaMinimal(char[] text, int offset, int len, Writer writer) throws IOException {
        escapeJava(text, offset, len, writer, JavaEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapeJava(char[] text, int offset, int len, Writer writer) throws IOException {
        escapeJava(text, offset, len, writer, JavaEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapeJava(char[] text, int offset, int len, Writer writer, JavaEscapeLevel level) throws IOException {
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
        JavaEscapeUtil.escape(text, offset, len, writer, level);
    }

    public static String unescapeJava(String text) {
        if (text == null) {
            return null;
        }
        if (text.indexOf(92) < 0) {
            return text;
        }
        return JavaEscapeUtil.unescape(text);
    }

    public static void unescapeJava(String text, Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (text == null) {
            return;
        }
        if (text.indexOf(92) < 0) {
            writer.write(text);
        } else {
            JavaEscapeUtil.unescape(new InternalStringReader(text), writer);
        }
    }

    public static void unescapeJava(Reader reader, Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        JavaEscapeUtil.unescape(reader, writer);
    }

    public static void unescapeJava(char[] text, int offset, int len, Writer writer) throws IOException {
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
        JavaEscapeUtil.unescape(text, offset, len, writer);
    }

    private JavaEscape() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/java/JavaEscape$InternalStringReader.class */
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