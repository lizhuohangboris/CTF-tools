package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/parser/Host.class */
public class Host {
    private Host() {
    }

    public static int parse(MessageBytes mb) {
        return parse(new MessageBytesReader(mb));
    }

    public static int parse(String string) {
        return parse(new StringReader(string));
    }

    private static int parse(Reader reader) {
        try {
            reader.mark(1);
            int first = reader.read();
            reader.reset();
            if (HttpParser.isAlpha(first)) {
                return HttpParser.readHostDomainName(reader);
            }
            if (HttpParser.isNumeric(first)) {
                return HttpParser.readHostIPv4(reader, false);
            }
            if (91 == first) {
                return HttpParser.readHostIPv6(reader);
            }
            throw new IllegalArgumentException();
        } catch (IOException ioe) {
            throw new IllegalArgumentException(ioe);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/parser/Host$MessageBytesReader.class */
    private static class MessageBytesReader extends Reader {
        private final byte[] bytes;
        private final int end;
        private int pos;
        private int mark;

        public MessageBytesReader(MessageBytes mb) {
            ByteChunk bc = mb.getByteChunk();
            this.bytes = bc.getBytes();
            this.pos = bc.getOffset();
            this.end = bc.getEnd();
        }

        @Override // java.io.Reader
        public int read(char[] cbuf, int off, int len) throws IOException {
            for (int i = off; i < off + len; i++) {
                byte[] bArr = this.bytes;
                int i2 = this.pos;
                this.pos = i2 + 1;
                cbuf[i] = (char) bArr[i2];
            }
            return len;
        }

        @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
        }

        @Override // java.io.Reader
        public int read() throws IOException {
            if (this.pos < this.end) {
                byte[] bArr = this.bytes;
                int i = this.pos;
                this.pos = i + 1;
                return bArr[i];
            }
            return -1;
        }

        @Override // java.io.Reader
        public boolean markSupported() {
            return true;
        }

        @Override // java.io.Reader
        public void mark(int readAheadLimit) throws IOException {
            this.mark = this.pos;
        }

        @Override // java.io.Reader
        public void reset() throws IOException {
            this.pos = this.mark;
        }
    }
}