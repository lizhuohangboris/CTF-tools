package org.attoparser;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import org.attoparser.config.ParseConfiguration;
import org.attoparser.select.ParseSelection;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/MarkupParser.class */
public final class MarkupParser implements IMarkupParser {
    public static final int DEFAULT_BUFFER_SIZE = 4096;
    public static final int DEFAULT_POOL_SIZE = 2;
    private final ParseConfiguration configuration;
    private final BufferPool pool;

    public MarkupParser(ParseConfiguration configuration) {
        this(configuration, 2, 4096);
    }

    public MarkupParser(ParseConfiguration configuration, int poolSize, int bufferSize) {
        this.configuration = configuration;
        this.pool = new BufferPool(poolSize, bufferSize);
    }

    @Override // org.attoparser.IMarkupParser
    public void parse(String document, IMarkupHandler handler) throws ParseException {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        parse(new StringReader(document), handler);
    }

    @Override // org.attoparser.IMarkupParser
    public void parse(char[] document, IMarkupHandler handler) throws ParseException {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        parse(document, 0, document.length, handler);
    }

    @Override // org.attoparser.IMarkupParser
    public void parse(char[] document, int offset, int len, IMarkupHandler handler) throws ParseException {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        if (offset < 0 || len < 0) {
            throw new IllegalArgumentException("Neither document offset (" + offset + ") nor document length (" + len + ") can be less than zero");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }
        IMarkupHandler markupHandler = new MarkupEventProcessorHandler(ParseConfiguration.ParsingMode.HTML.equals(this.configuration.getMode()) ? new HtmlMarkupHandler(handler) : handler);
        markupHandler.setParseConfiguration(this.configuration);
        ParseStatus status = new ParseStatus();
        markupHandler.setParseStatus(status);
        ParseSelection selection = new ParseSelection();
        markupHandler.setParseSelection(selection);
        parseDocument(document, offset, len, markupHandler, status);
    }

    @Override // org.attoparser.IMarkupParser
    public void parse(Reader reader, IMarkupHandler handler) throws ParseException {
        if (reader == null) {
            throw new IllegalArgumentException("Reader cannot be null");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }
        IMarkupHandler markupHandler = new MarkupEventProcessorHandler(ParseConfiguration.ParsingMode.HTML.equals(this.configuration.getMode()) ? new HtmlMarkupHandler(handler) : handler);
        markupHandler.setParseConfiguration(this.configuration);
        ParseStatus status = new ParseStatus();
        markupHandler.setParseStatus(status);
        ParseSelection selection = new ParseSelection();
        markupHandler.setParseSelection(selection);
        parseDocument(reader, this.pool.poolBufferSize, markupHandler, status);
    }

    void parseDocument(Reader reader, int suggestedBufferSize, IMarkupHandler handler, ParseStatus status) throws ParseException {
        long parsingStartTimeNanos = System.nanoTime();
        try {
            try {
                handler.handleDocumentStart(parsingStartTimeNanos, 1, 1);
                int bufferSize = suggestedBufferSize;
                char[] buffer = this.pool.allocateBuffer(bufferSize);
                int bufferContentSize = reader.read(buffer);
                boolean cont = bufferContentSize != -1;
                status.offset = -1;
                status.line = 1;
                status.col = 1;
                status.inStructure = false;
                status.parsingDisabled = true;
                status.parsingDisabledLimitSequence = null;
                status.autoCloseRequired = null;
                status.autoCloseLimits = null;
                while (cont) {
                    parseBuffer(buffer, 0, bufferContentSize, handler, status);
                    int readOffset = 0;
                    int readLen = bufferSize;
                    if (status.offset == 0) {
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
                        readOffset = bufferContentSize;
                        readLen = bufferSize - readOffset;
                    } else if (status.offset < bufferContentSize) {
                        System.arraycopy(buffer, status.offset, buffer, 0, bufferContentSize - status.offset);
                        readOffset = bufferContentSize - status.offset;
                        readLen = bufferSize - readOffset;
                        status.offset = 0;
                        bufferContentSize = readOffset;
                    }
                    int read = reader.read(buffer, readOffset, readLen);
                    if (read != -1) {
                        bufferContentSize = readOffset + read;
                    } else {
                        cont = false;
                    }
                }
                int lastLine = status.line;
                int lastCol = status.col;
                int lastStart = status.offset;
                int lastLen = bufferContentSize - lastStart;
                if (lastLen > 0) {
                    if (status.inStructure) {
                        throw new ParseException("Incomplete structure: \"" + new String(buffer, lastStart, lastLen) + "\"", status.line, status.col);
                    }
                    handler.handleText(buffer, lastStart, lastLen, status.line, status.col);
                    for (int i = lastStart; i < lastStart + lastLen; i++) {
                        char c = buffer[i];
                        if (c == '\n') {
                            lastLine++;
                            lastCol = 1;
                        } else {
                            lastCol++;
                        }
                    }
                }
                long parsingEndTimeNanos = System.nanoTime();
                handler.handleDocumentEnd(parsingEndTimeNanos, parsingEndTimeNanos - parsingStartTimeNanos, lastLine, lastCol);
                this.pool.releaseBuffer(buffer);
                try {
                    reader.close();
                } catch (Throwable th) {
                }
            } catch (ParseException e2) {
                throw e2;
            } catch (Exception e3) {
                throw new ParseException(e3);
            }
        } catch (Throwable th2) {
            this.pool.releaseBuffer(null);
            try {
                reader.close();
            } catch (Throwable th3) {
            }
            throw th2;
        }
    }

    void parseDocument(char[] buffer, int offset, int len, IMarkupHandler handler, ParseStatus status) throws ParseException {
        long parsingStartTimeNanos = System.nanoTime();
        try {
            handler.handleDocumentStart(parsingStartTimeNanos, 1, 1);
            status.offset = -1;
            status.line = 1;
            status.col = 1;
            status.inStructure = false;
            status.parsingDisabled = true;
            status.parsingDisabledLimitSequence = null;
            status.autoCloseRequired = null;
            status.autoCloseLimits = null;
            parseBuffer(buffer, offset, len, handler, status);
            int lastLine = status.line;
            int lastCol = status.col;
            int lastStart = status.offset;
            int lastLen = (offset + len) - lastStart;
            if (lastLen > 0) {
                if (status.inStructure) {
                    throw new ParseException("Incomplete structure: \"" + new String(buffer, lastStart, lastLen) + "\"", status.line, status.col);
                }
                handler.handleText(buffer, lastStart, lastLen, status.line, status.col);
                for (int i = lastStart; i < lastStart + lastLen; i++) {
                    char c = buffer[i];
                    if (c == '\n') {
                        lastLine++;
                        lastCol = 1;
                    } else {
                        lastCol++;
                    }
                }
            }
            long parsingEndTimeNanos = System.nanoTime();
            handler.handleDocumentEnd(parsingEndTimeNanos, parsingEndTimeNanos - parsingStartTimeNanos, lastLine, lastCol);
        } catch (ParseException e) {
            throw e;
        } catch (Exception e2) {
            throw new ParseException(e2);
        }
    }

    /* JADX WARN: Incorrect condition in loop: B:310:0x01e4 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void parseBuffer(char[] r8, int r9, int r10, org.attoparser.IMarkupHandler r11, org.attoparser.ParseStatus r12) throws org.attoparser.ParseException {
        /*
            Method dump skipped, instructions count: 1508
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.attoparser.MarkupParser.parseBuffer(char[], int, int, org.attoparser.IMarkupHandler, org.attoparser.ParseStatus):void");
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/MarkupParser$BufferPool.class */
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