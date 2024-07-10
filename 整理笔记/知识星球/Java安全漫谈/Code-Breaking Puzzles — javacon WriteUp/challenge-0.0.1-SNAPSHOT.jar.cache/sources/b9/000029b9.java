package org.thymeleaf.templateparser.raw;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/raw/RawParser.class */
final class RawParser {
    private final BufferPool pool;

    /* JADX INFO: Access modifiers changed from: package-private */
    public RawParser(int poolSize, int bufferSize) {
        this.pool = new BufferPool(poolSize, bufferSize);
    }

    public void parse(String document, IRawHandler handler) throws RawParseException {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        parse(new StringReader(document), handler);
    }

    public void parse(Reader reader, IRawHandler handler) throws RawParseException {
        if (reader == null) {
            throw new IllegalArgumentException("Reader cannot be null");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }
        parseDocument(reader, this.pool.poolBufferSize, handler);
    }

    void parseDocument(Reader reader, int suggestedBufferSize, IRawHandler handler) throws RawParseException {
        long parsingStartTimeNanos = System.nanoTime();
        char[] buffer = null;
        try {
            try {
                try {
                    handler.handleDocumentStart(parsingStartTimeNanos, 1, 1);
                    int bufferSize = suggestedBufferSize;
                    buffer = this.pool.allocateBuffer(bufferSize);
                    int bufferContentSize = reader.read(buffer);
                    boolean cont = bufferContentSize != -1;
                    while (cont) {
                        if (bufferContentSize == bufferSize) {
                            char[] newBuffer = null;
                            try {
                                bufferSize *= 2;
                                newBuffer = this.pool.allocateBuffer(bufferSize);
                                System.arraycopy(buffer, 0, newBuffer, 0, bufferContentSize);
                                this.pool.releaseBuffer(buffer);
                                buffer = newBuffer;
                            } catch (Exception e) {
                                this.pool.releaseBuffer(newBuffer);
                            }
                        }
                        int read = reader.read(buffer, bufferContentSize, bufferSize - bufferContentSize);
                        if (read != -1) {
                            bufferContentSize += read;
                        } else {
                            cont = false;
                        }
                    }
                    handler.handleText(buffer, 0, bufferContentSize, 1, 1);
                    int[] lastLineCol = computeLastLineCol(buffer, bufferContentSize);
                    long parsingEndTimeNanos = System.nanoTime();
                    handler.handleDocumentEnd(parsingEndTimeNanos, parsingEndTimeNanos - parsingStartTimeNanos, lastLineCol[0], lastLineCol[1]);
                    this.pool.releaseBuffer(buffer);
                    try {
                        reader.close();
                    } catch (Throwable th) {
                    }
                } catch (RawParseException e2) {
                    throw e2;
                }
            } catch (Exception e3) {
                throw new RawParseException(e3);
            }
        } catch (Throwable th2) {
            this.pool.releaseBuffer(buffer);
            try {
                reader.close();
            } catch (Throwable th3) {
            }
            throw th2;
        }
    }

    private static int[] computeLastLineCol(char[] buffer, int bufferContentSize) {
        if (bufferContentSize == 0) {
            return new int[]{1, 1};
        }
        int line = 1;
        int lastLineFeed = 0;
        int n = bufferContentSize;
        int i = 0;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c = buffer[i];
                if (c == '\n') {
                    line++;
                    lastLineFeed = i;
                }
                i++;
            } else {
                int col = bufferContentSize - lastLineFeed;
                return new int[]{line, col};
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/raw/RawParser$BufferPool.class */
    public static final class BufferPool {
        private final char[][] pool;
        private final boolean[] allocated;
        private final int poolBufferSize;

        /* JADX WARN: Type inference failed for: r1v1, types: [char[], char[][]] */
        private BufferPool(int poolSize, int poolBufferSize) {
            this.pool = new char[poolSize];
            this.allocated = new boolean[poolSize];
            this.poolBufferSize = poolBufferSize;
            for (int i = 0; i < this.pool.length; i++) {
                this.pool[i] = new char[this.poolBufferSize];
            }
            Arrays.fill(this.allocated, false);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized char[] allocateBuffer(int bufferSize) {
            if (bufferSize != this.poolBufferSize) {
                return new char[bufferSize];
            }
            for (int i = 0; i < this.pool.length; i++) {
                if (!this.allocated[i]) {
                    this.allocated[i] = true;
                    return this.pool[i];
                }
            }
            return new char[bufferSize];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized void releaseBuffer(char[] buffer) {
            if (buffer == null || buffer.length != this.poolBufferSize) {
                return;
            }
            for (int i = 0; i < this.pool.length; i++) {
                if (this.pool[i] == buffer) {
                    this.allocated[i] = false;
                    return;
                }
            }
        }
    }
}