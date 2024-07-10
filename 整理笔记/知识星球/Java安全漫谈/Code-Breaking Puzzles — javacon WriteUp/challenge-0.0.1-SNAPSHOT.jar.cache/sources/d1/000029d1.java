package org.thymeleaf.templateparser.text;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/text/TextParser.class */
public final class TextParser {
    private final BufferPool pool;
    private final boolean processCommentsAndLiterals;
    private final boolean standardDialectPresent;

    public TextParser(int poolSize, int bufferSize, boolean processCommentsAndLiterals, boolean standardDialectPresent) {
        this.pool = new BufferPool(poolSize, bufferSize);
        this.processCommentsAndLiterals = processCommentsAndLiterals;
        this.standardDialectPresent = standardDialectPresent;
    }

    public void parse(String document, ITextHandler handler) throws TextParseException {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        parse(new StringReader(document), handler);
    }

    public void parse(Reader reader, ITextHandler handler) throws TextParseException {
        if (reader == null) {
            throw new IllegalArgumentException("Reader cannot be null");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }
        ITextHandler handlerChain = new EventProcessorTextHandler(handler);
        if (this.processCommentsAndLiterals) {
            handlerChain = new CommentProcessorTextHandler(this.standardDialectPresent, handlerChain);
        }
        parseDocument(reader, this.pool.poolBufferSize, handlerChain);
    }

    void parseDocument(Reader reader, int suggestedBufferSize, ITextHandler handler) throws TextParseException {
        long parsingStartTimeNanos = System.nanoTime();
        try {
            try {
                try {
                    TextParseStatus status = new TextParseStatus();
                    handler.handleDocumentStart(parsingStartTimeNanos, 1, 1);
                    int bufferSize = suggestedBufferSize;
                    char[] buffer = this.pool.allocateBuffer(bufferSize);
                    int bufferContentSize = reader.read(buffer);
                    boolean cont = bufferContentSize != -1;
                    status.offset = -1;
                    status.line = 1;
                    status.col = 1;
                    status.inStructure = false;
                    status.inCommentLine = false;
                    status.literalMarker = (char) 0;
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
                        if (status.inStructure && !status.inCommentLine) {
                            throw new TextParseException("Incomplete structure: \"" + new String(buffer, lastStart, lastLen) + "\"", status.line, status.col);
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
                } catch (TextParseException e2) {
                    throw e2;
                }
            } catch (Exception e3) {
                throw new TextParseException(e3);
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

    private void parseBuffer(char[] buffer, int offset, int len, ITextHandler handler, TextParseStatus status) throws TextParseException {
        int findNextStructureEndAvoidQuotes;
        int[] locator = {status.line, status.col};
        int currentLine = locator[0];
        int currentCol = locator[1];
        int maxi = offset + len;
        int i = offset;
        int current = i;
        boolean inOpenElement = false;
        boolean inCloseElement = false;
        boolean inCommentBlock = false;
        boolean inCommentLine = false;
        boolean inLiteral = false;
        int tagStart = i;
        int tagEnd = i;
        while (i < maxi) {
            if (!(inOpenElement || inCloseElement || inCommentBlock || inCommentLine || inLiteral)) {
                int pos = TextParsingUtil.findNextStructureStartOrLiteralMarker(buffer, i, maxi, locator, this.processCommentsAndLiterals);
                if (pos == -1) {
                    status.offset = current;
                    status.line = currentLine;
                    status.col = currentCol;
                    status.inStructure = false;
                    status.inCommentLine = false;
                    status.literalMarker = (char) 0;
                    return;
                }
                char c = buffer[pos];
                inOpenElement = TextParsingElementUtil.isOpenElementStart(buffer, pos, maxi);
                if (!inOpenElement) {
                    inCloseElement = TextParsingElementUtil.isCloseElementStart(buffer, pos, maxi);
                    if (!inCloseElement && this.processCommentsAndLiterals) {
                        inCommentBlock = TextParsingCommentUtil.isCommentBlockStart(buffer, pos, maxi);
                        if (!inCommentBlock) {
                            inCommentLine = TextParsingCommentUtil.isCommentLineStart(buffer, pos, maxi);
                            if (!inCommentLine) {
                                inLiteral = c == '\'' || c == '\"' || c == '`' || TextParsingLiteralUtil.isRegexLiteralStart(buffer, pos, maxi);
                                status.literalMarker = inLiteral ? c : (char) 0;
                            }
                        }
                    }
                }
                boolean inStructure = inOpenElement || inCloseElement || inCommentBlock || inCommentLine || inLiteral;
                if (inStructure && !inLiteral) {
                    tagStart = pos;
                }
                while (!inStructure) {
                    ParsingLocatorUtil.countChar(locator, c);
                    pos = TextParsingUtil.findNextStructureStartOrLiteralMarker(buffer, pos + 1, maxi, locator, this.processCommentsAndLiterals);
                    if (pos == -1) {
                        status.offset = current;
                        status.line = currentLine;
                        status.col = currentCol;
                        status.inStructure = false;
                        status.inCommentLine = false;
                        status.literalMarker = (char) 0;
                        return;
                    }
                    c = buffer[pos];
                    inOpenElement = TextParsingElementUtil.isOpenElementStart(buffer, pos, maxi);
                    if (!inOpenElement) {
                        inCloseElement = TextParsingElementUtil.isCloseElementStart(buffer, pos, maxi);
                        if (!inCloseElement && this.processCommentsAndLiterals) {
                            inCommentBlock = TextParsingCommentUtil.isCommentBlockStart(buffer, pos, maxi);
                            if (!inCommentBlock) {
                                inCommentLine = TextParsingCommentUtil.isCommentLineStart(buffer, pos, maxi);
                                if (!inCommentLine) {
                                    inLiteral = c == '\'' || c == '\"' || c == '`' || TextParsingLiteralUtil.isRegexLiteralStart(buffer, pos, maxi);
                                    status.literalMarker = inLiteral ? c : (char) 0;
                                }
                            }
                        }
                    }
                    inStructure = inOpenElement || inCloseElement || inCommentBlock || inCommentLine || inLiteral;
                    if (inStructure && !inLiteral) {
                        tagStart = pos;
                    }
                }
                if (tagStart > current) {
                    handler.handleText(buffer, current, tagStart - current, currentLine, currentCol);
                }
                if (tagStart == pos) {
                    current = tagStart;
                    currentLine = locator[0];
                    currentCol = locator[1];
                }
                i = pos;
            } else {
                if (inLiteral) {
                    findNextStructureEndAvoidQuotes = TextParsingUtil.findNextLiteralEnd(buffer, i, maxi, locator, status.literalMarker);
                } else if (inCommentBlock) {
                    findNextStructureEndAvoidQuotes = TextParsingUtil.findNextCommentBlockEnd(buffer, i, maxi, locator);
                } else if (inCommentLine) {
                    findNextStructureEndAvoidQuotes = TextParsingUtil.findNextCommentLineEnd(buffer, i, maxi, locator);
                } else {
                    findNextStructureEndAvoidQuotes = TextParsingUtil.findNextStructureEndAvoidQuotes(buffer, i, maxi, locator);
                }
                int pos2 = findNextStructureEndAvoidQuotes;
                if (pos2 < 0) {
                    status.offset = current;
                    status.line = currentLine;
                    status.col = currentCol;
                    status.inStructure = true;
                    status.inCommentLine = inCommentLine;
                    status.literalMarker = (char) 0;
                    return;
                }
                if (inOpenElement) {
                    tagEnd = pos2;
                    if (buffer[tagEnd - 1] == '/') {
                        TextParsingElementUtil.parseStandaloneElement(buffer, current, (tagEnd - current) + 1, currentLine, currentCol, handler);
                    } else {
                        TextParsingElementUtil.parseOpenElement(buffer, current, (tagEnd - current) + 1, currentLine, currentCol, handler);
                    }
                    inOpenElement = false;
                } else if (inCloseElement) {
                    tagEnd = pos2;
                    TextParsingElementUtil.parseCloseElement(buffer, current, (tagEnd - current) + 1, currentLine, currentCol, handler);
                    inCloseElement = false;
                } else if (inCommentBlock) {
                    tagEnd = pos2;
                    TextParsingCommentUtil.parseComment(buffer, current, (tagEnd - current) + 1, currentLine, currentCol, handler);
                    inCommentBlock = false;
                } else if (inCommentLine) {
                    tagEnd = pos2;
                    handler.handleText(buffer, current, (tagEnd - current) + 1, currentLine, currentCol);
                    inCommentLine = false;
                } else if (inLiteral) {
                    inLiteral = false;
                    status.literalMarker = (char) 0;
                } else {
                    throw new IllegalStateException("Illegal parsing state: structure is not of a recognized type");
                }
                ParsingLocatorUtil.countChar(locator, buffer[pos2]);
                if (tagEnd == pos2) {
                    current = tagEnd + 1;
                    currentLine = locator[0];
                    currentCol = locator[1];
                }
                i = pos2 + 1;
            }
        }
        status.offset = current;
        status.line = currentLine;
        status.col = currentCol;
        status.inStructure = false;
        status.inCommentLine = false;
        status.literalMarker = (char) 0;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/text/TextParser$BufferPool.class */
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