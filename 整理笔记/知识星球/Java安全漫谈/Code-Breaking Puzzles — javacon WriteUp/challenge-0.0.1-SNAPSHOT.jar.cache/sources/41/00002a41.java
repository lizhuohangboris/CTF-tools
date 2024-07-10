package org.unbescape.json;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/json/JsonEscape.class */
public final class JsonEscape {
    public static String escapeJsonMinimal(String text) {
        return escapeJson(text, JsonEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_UHEXA, JsonEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static String escapeJson(String text) {
        return escapeJson(text, JsonEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_UHEXA, JsonEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static String escapeJson(String text, JsonEscapeType type, JsonEscapeLevel level) {
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        return JsonEscapeUtil.escape(text, type, level);
    }

    public static void escapeJsonMinimal(String text, Writer writer) throws IOException {
        escapeJson(text, writer, JsonEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_UHEXA, JsonEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapeJson(String text, Writer writer) throws IOException {
        escapeJson(text, writer, JsonEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_UHEXA, JsonEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapeJson(String text, Writer writer, JsonEscapeType type, JsonEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        JsonEscapeUtil.escape(new InternalStringReader(text), writer, type, level);
    }

    public static void escapeJsonMinimal(Reader reader, Writer writer) throws IOException {
        escapeJson(reader, writer, JsonEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_UHEXA, JsonEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapeJson(Reader reader, Writer writer) throws IOException {
        escapeJson(reader, writer, JsonEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_UHEXA, JsonEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapeJson(Reader reader, Writer writer, JsonEscapeType type, JsonEscapeLevel level) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("The 'type' argument cannot be null");
        }
        if (level == null) {
            throw new IllegalArgumentException("The 'level' argument cannot be null");
        }
        JsonEscapeUtil.escape(reader, writer, type, level);
    }

    public static void escapeJsonMinimal(char[] text, int offset, int len, Writer writer) throws IOException {
        escapeJson(text, offset, len, writer, JsonEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_UHEXA, JsonEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
    }

    public static void escapeJson(char[] text, int offset, int len, Writer writer) throws IOException {
        escapeJson(text, offset, len, writer, JsonEscapeType.SINGLE_ESCAPE_CHARS_DEFAULT_TO_UHEXA, JsonEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
    }

    public static void escapeJson(char[] text, int offset, int len, Writer writer, JsonEscapeType type, JsonEscapeLevel level) throws IOException {
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
        JsonEscapeUtil.escape(text, offset, len, writer, type, level);
    }

    public static String unescapeJson(String text) {
        if (text == null) {
            return null;
        }
        if (text.indexOf(92) < 0) {
            return text;
        }
        return JsonEscapeUtil.unescape(text);
    }

    public static void unescapeJson(String text, Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (text == null) {
            return;
        }
        if (text.indexOf(92) < 0) {
            writer.write(text);
        } else {
            JsonEscapeUtil.unescape(new InternalStringReader(text), writer);
        }
    }

    public static void unescapeJson(Reader reader, Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        JsonEscapeUtil.unescape(reader, writer);
    }

    public static void unescapeJson(char[] text, int offset, int len, Writer writer) throws IOException {
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
        JsonEscapeUtil.unescape(text, offset, len, writer);
    }

    private JsonEscape() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/json/JsonEscape$InternalStringReader.class */
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