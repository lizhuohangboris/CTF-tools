package org.unbescape.uri;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.unbescape.uri.UriEscapeUtil;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/uri/UriEscape.class */
public final class UriEscape {
    public static final String DEFAULT_ENCODING = "UTF-8";

    public static String escapeUriPath(String text) {
        return escapeUriPath(text, DEFAULT_ENCODING);
    }

    public static String escapeUriPath(String text, String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        return UriEscapeUtil.escape(text, UriEscapeUtil.UriEscapeType.PATH, encoding);
    }

    public static String escapeUriPathSegment(String text) {
        return escapeUriPathSegment(text, DEFAULT_ENCODING);
    }

    public static String escapeUriPathSegment(String text, String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        return UriEscapeUtil.escape(text, UriEscapeUtil.UriEscapeType.PATH_SEGMENT, encoding);
    }

    public static String escapeUriQueryParam(String text) {
        return escapeUriQueryParam(text, DEFAULT_ENCODING);
    }

    public static String escapeUriQueryParam(String text, String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        return UriEscapeUtil.escape(text, UriEscapeUtil.UriEscapeType.QUERY_PARAM, encoding);
    }

    public static String escapeUriFragmentId(String text) {
        return escapeUriFragmentId(text, DEFAULT_ENCODING);
    }

    public static String escapeUriFragmentId(String text, String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        return UriEscapeUtil.escape(text, UriEscapeUtil.UriEscapeType.FRAGMENT_ID, encoding);
    }

    public static void escapeUriPath(String text, Writer writer) throws IOException {
        escapeUriPath(text, writer, DEFAULT_ENCODING);
    }

    public static void escapeUriPath(String text, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.escape(new InternalStringReader(text), writer, UriEscapeUtil.UriEscapeType.PATH, encoding);
    }

    public static void escapeUriPathSegment(String text, Writer writer) throws IOException {
        escapeUriPathSegment(text, writer, DEFAULT_ENCODING);
    }

    public static void escapeUriPathSegment(String text, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.escape(new InternalStringReader(text), writer, UriEscapeUtil.UriEscapeType.PATH_SEGMENT, encoding);
    }

    public static void escapeUriQueryParam(String text, Writer writer) throws IOException {
        escapeUriQueryParam(text, writer, DEFAULT_ENCODING);
    }

    public static void escapeUriQueryParam(String text, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.escape(new InternalStringReader(text), writer, UriEscapeUtil.UriEscapeType.QUERY_PARAM, encoding);
    }

    public static void escapeUriFragmentId(String text, Writer writer) throws IOException {
        escapeUriFragmentId(text, writer, DEFAULT_ENCODING);
    }

    public static void escapeUriFragmentId(String text, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.escape(new InternalStringReader(text), writer, UriEscapeUtil.UriEscapeType.FRAGMENT_ID, encoding);
    }

    public static void escapeUriPath(Reader reader, Writer writer) throws IOException {
        escapeUriPath(reader, writer, DEFAULT_ENCODING);
    }

    public static void escapeUriPath(Reader reader, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.escape(reader, writer, UriEscapeUtil.UriEscapeType.PATH, encoding);
    }

    public static void escapeUriPathSegment(Reader reader, Writer writer) throws IOException {
        escapeUriPathSegment(reader, writer, DEFAULT_ENCODING);
    }

    public static void escapeUriPathSegment(Reader reader, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.escape(reader, writer, UriEscapeUtil.UriEscapeType.PATH_SEGMENT, encoding);
    }

    public static void escapeUriQueryParam(Reader reader, Writer writer) throws IOException {
        escapeUriQueryParam(reader, writer, DEFAULT_ENCODING);
    }

    public static void escapeUriQueryParam(Reader reader, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.escape(reader, writer, UriEscapeUtil.UriEscapeType.QUERY_PARAM, encoding);
    }

    public static void escapeUriFragmentId(Reader reader, Writer writer) throws IOException {
        escapeUriFragmentId(reader, writer, DEFAULT_ENCODING);
    }

    public static void escapeUriFragmentId(Reader reader, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.escape(reader, writer, UriEscapeUtil.UriEscapeType.FRAGMENT_ID, encoding);
    }

    public static void escapeUriPath(char[] text, int offset, int len, Writer writer) throws IOException {
        escapeUriPath(text, offset, len, writer, DEFAULT_ENCODING);
    }

    public static void escapeUriPath(char[] text, int offset, int len, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        int textLen = text == null ? 0 : text.length;
        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        if (len < 0 || offset + len > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        UriEscapeUtil.escape(text, offset, len, writer, UriEscapeUtil.UriEscapeType.PATH, encoding);
    }

    public static void escapeUriPathSegment(char[] text, int offset, int len, Writer writer) throws IOException {
        escapeUriPathSegment(text, offset, len, writer, DEFAULT_ENCODING);
    }

    public static void escapeUriPathSegment(char[] text, int offset, int len, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        int textLen = text == null ? 0 : text.length;
        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        if (len < 0 || offset + len > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        UriEscapeUtil.escape(text, offset, len, writer, UriEscapeUtil.UriEscapeType.PATH_SEGMENT, encoding);
    }

    public static void escapeUriQueryParam(char[] text, int offset, int len, Writer writer) throws IOException {
        escapeUriQueryParam(text, offset, len, writer, DEFAULT_ENCODING);
    }

    public static void escapeUriQueryParam(char[] text, int offset, int len, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        int textLen = text == null ? 0 : text.length;
        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        if (len < 0 || offset + len > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        UriEscapeUtil.escape(text, offset, len, writer, UriEscapeUtil.UriEscapeType.QUERY_PARAM, encoding);
    }

    public static void escapeUriFragmentId(char[] text, int offset, int len, Writer writer) throws IOException {
        escapeUriFragmentId(text, offset, len, writer, DEFAULT_ENCODING);
    }

    public static void escapeUriFragmentId(char[] text, int offset, int len, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        int textLen = text == null ? 0 : text.length;
        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        if (len < 0 || offset + len > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        UriEscapeUtil.escape(text, offset, len, writer, UriEscapeUtil.UriEscapeType.FRAGMENT_ID, encoding);
    }

    public static String unescapeUriPath(String text) {
        return unescapeUriPath(text, DEFAULT_ENCODING);
    }

    public static String unescapeUriPath(String text, String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        return UriEscapeUtil.unescape(text, UriEscapeUtil.UriEscapeType.PATH, encoding);
    }

    public static String unescapeUriPathSegment(String text) {
        return unescapeUriPathSegment(text, DEFAULT_ENCODING);
    }

    public static String unescapeUriPathSegment(String text, String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        return UriEscapeUtil.unescape(text, UriEscapeUtil.UriEscapeType.PATH_SEGMENT, encoding);
    }

    public static String unescapeUriQueryParam(String text) {
        return unescapeUriQueryParam(text, DEFAULT_ENCODING);
    }

    public static String unescapeUriQueryParam(String text, String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        return UriEscapeUtil.unescape(text, UriEscapeUtil.UriEscapeType.QUERY_PARAM, encoding);
    }

    public static String unescapeUriFragmentId(String text) {
        return unescapeUriFragmentId(text, DEFAULT_ENCODING);
    }

    public static String unescapeUriFragmentId(String text, String encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        return UriEscapeUtil.unescape(text, UriEscapeUtil.UriEscapeType.FRAGMENT_ID, encoding);
    }

    public static void unescapeUriPath(String text, Writer writer) throws IOException {
        unescapeUriPath(text, writer, DEFAULT_ENCODING);
    }

    public static void unescapeUriPath(String text, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.unescape(new InternalStringReader(text), writer, UriEscapeUtil.UriEscapeType.PATH, encoding);
    }

    public static void unescapeUriPathSegment(String text, Writer writer) throws IOException {
        unescapeUriPathSegment(text, writer, DEFAULT_ENCODING);
    }

    public static void unescapeUriPathSegment(String text, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.unescape(new InternalStringReader(text), writer, UriEscapeUtil.UriEscapeType.PATH_SEGMENT, encoding);
    }

    public static void unescapeUriQueryParam(String text, Writer writer) throws IOException {
        unescapeUriQueryParam(text, writer, DEFAULT_ENCODING);
    }

    public static void unescapeUriQueryParam(String text, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.unescape(new InternalStringReader(text), writer, UriEscapeUtil.UriEscapeType.QUERY_PARAM, encoding);
    }

    public static void unescapeUriFragmentId(String text, Writer writer) throws IOException {
        unescapeUriFragmentId(text, writer, DEFAULT_ENCODING);
    }

    public static void unescapeUriFragmentId(String text, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.unescape(new InternalStringReader(text), writer, UriEscapeUtil.UriEscapeType.FRAGMENT_ID, encoding);
    }

    public static void unescapeUriPath(Reader reader, Writer writer) throws IOException {
        unescapeUriPath(reader, writer, DEFAULT_ENCODING);
    }

    public static void unescapeUriPath(Reader reader, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.unescape(reader, writer, UriEscapeUtil.UriEscapeType.PATH, encoding);
    }

    public static void unescapeUriPathSegment(Reader reader, Writer writer) throws IOException {
        unescapeUriPathSegment(reader, writer, DEFAULT_ENCODING);
    }

    public static void unescapeUriPathSegment(Reader reader, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.unescape(reader, writer, UriEscapeUtil.UriEscapeType.PATH_SEGMENT, encoding);
    }

    public static void unescapeUriQueryParam(Reader reader, Writer writer) throws IOException {
        unescapeUriQueryParam(reader, writer, DEFAULT_ENCODING);
    }

    public static void unescapeUriQueryParam(Reader reader, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.unescape(reader, writer, UriEscapeUtil.UriEscapeType.QUERY_PARAM, encoding);
    }

    public static void unescapeUriFragmentId(Reader reader, Writer writer) throws IOException {
        unescapeUriFragmentId(reader, writer, DEFAULT_ENCODING);
    }

    public static void unescapeUriFragmentId(Reader reader, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        UriEscapeUtil.unescape(reader, writer, UriEscapeUtil.UriEscapeType.FRAGMENT_ID, encoding);
    }

    public static void unescapeUriPath(char[] text, int offset, int len, Writer writer) throws IOException {
        unescapeUriPath(text, offset, len, writer, DEFAULT_ENCODING);
    }

    public static void unescapeUriPath(char[] text, int offset, int len, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        int textLen = text == null ? 0 : text.length;
        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        if (len < 0 || offset + len > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        UriEscapeUtil.unescape(text, offset, len, writer, UriEscapeUtil.UriEscapeType.PATH, encoding);
    }

    public static void unescapeUriPathSegment(char[] text, int offset, int len, Writer writer) throws IOException {
        unescapeUriPathSegment(text, offset, len, writer, DEFAULT_ENCODING);
    }

    public static void unescapeUriPathSegment(char[] text, int offset, int len, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        int textLen = text == null ? 0 : text.length;
        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        if (len < 0 || offset + len > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        UriEscapeUtil.unescape(text, offset, len, writer, UriEscapeUtil.UriEscapeType.PATH_SEGMENT, encoding);
    }

    public static void unescapeUriQueryParam(char[] text, int offset, int len, Writer writer) throws IOException {
        unescapeUriQueryParam(text, offset, len, writer, DEFAULT_ENCODING);
    }

    public static void unescapeUriQueryParam(char[] text, int offset, int len, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        int textLen = text == null ? 0 : text.length;
        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        if (len < 0 || offset + len > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        UriEscapeUtil.unescape(text, offset, len, writer, UriEscapeUtil.UriEscapeType.QUERY_PARAM, encoding);
    }

    public static void unescapeUriFragmentId(char[] text, int offset, int len, Writer writer) throws IOException {
        unescapeUriFragmentId(text, offset, len, writer, DEFAULT_ENCODING);
    }

    public static void unescapeUriFragmentId(char[] text, int offset, int len, Writer writer, String encoding) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("Argument 'writer' cannot be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument 'encoding' cannot be null");
        }
        int textLen = text == null ? 0 : text.length;
        if (offset < 0 || offset > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        if (len < 0 || offset + len > textLen) {
            throw new IllegalArgumentException("Invalid (offset, len). offset=" + offset + ", len=" + len + ", text.length=" + textLen);
        }
        UriEscapeUtil.unescape(text, offset, len, writer, UriEscapeUtil.UriEscapeType.FRAGMENT_ID, encoding);
    }

    private UriEscape() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/unbescape-1.1.6.RELEASE.jar:org/unbescape/uri/UriEscape$InternalStringReader.class */
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