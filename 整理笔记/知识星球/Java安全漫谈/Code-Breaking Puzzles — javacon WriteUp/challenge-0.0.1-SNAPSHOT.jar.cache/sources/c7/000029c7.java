package org.thymeleaf.templateparser.text;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/text/CommentProcessorTextHandler.class */
final class CommentProcessorTextHandler extends AbstractChainedTextHandler {
    private final boolean standardDialectPresent;
    private boolean filterTexts;
    private char[] filteredTextBuffer;
    private int filteredTextSize;
    private int[] filteredTextLocator;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CommentProcessorTextHandler(boolean standardDialectPresent, ITextHandler handler) {
        super(handler);
        this.filterTexts = false;
        this.filteredTextBuffer = null;
        this.filteredTextSize = 0;
        this.filteredTextLocator = null;
        this.standardDialectPresent = standardDialectPresent;
    }

    @Override // org.thymeleaf.templateparser.text.AbstractChainedTextHandler, org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleDocumentEnd(long endTimeNanos, long totalTimeNanos, int line, int col) throws TextParseException {
        processFilteredTexts();
        super.handleDocumentEnd(endTimeNanos, totalTimeNanos, line, col);
    }

    @Override // org.thymeleaf.templateparser.text.AbstractChainedTextHandler, org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleComment(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col) throws TextParseException {
        processFilteredTexts();
        if (!isCommentProcessable(buffer, contentOffset, contentLen)) {
            super.handleText(buffer, outerOffset, outerLen, line, col);
            return;
        }
        int maxi = contentOffset + contentLen;
        if (TextParsingElementUtil.isOpenElementStart(buffer, contentOffset, maxi)) {
            if (TextParsingElementUtil.isElementEnd(buffer, maxi - 2, maxi, true)) {
                TextParsingElementUtil.parseStandaloneElement(buffer, contentOffset, contentLen, line, col + 2, getNext());
                return;
            } else if (TextParsingElementUtil.isElementEnd(buffer, maxi - 1, maxi, false)) {
                TextParsingElementUtil.parseOpenElement(buffer, contentOffset, contentLen, line, col + 2, getNext());
                return;
            }
        } else if (TextParsingElementUtil.isCloseElementStart(buffer, contentOffset, maxi) && TextParsingElementUtil.isElementEnd(buffer, maxi - 1, maxi, false)) {
            TextParsingElementUtil.parseCloseElement(buffer, contentOffset, contentLen, line, col + 2, getNext());
            return;
        }
        if (this.standardDialectPresent) {
            getNext().handleText(buffer, contentOffset, contentLen, line, col + 2);
            this.filterTexts = true;
            return;
        }
        getNext().handleText(buffer, outerOffset, outerLen, line, col);
    }

    private boolean isCommentProcessable(char[] buffer, int contentOffset, int contentLen) {
        int maxi = contentOffset + contentLen;
        if (contentLen < 3 || buffer[contentOffset] != '[' || buffer[maxi - 1] != ']') {
            return false;
        }
        if (contentLen >= 4 && buffer[contentOffset + 1] == '(' && buffer[maxi - 2] == ')') {
            return true;
        }
        if (contentLen >= 4 && buffer[contentOffset + 1] == '[' && buffer[maxi - 2] == ']') {
            return true;
        }
        if (TextParsingElementUtil.isOpenElementStart(buffer, contentOffset, maxi)) {
            return TextParsingElementUtil.isElementEnd(buffer, maxi - 1, maxi, false);
        }
        if (TextParsingElementUtil.isCloseElementStart(buffer, contentOffset, maxi)) {
            return TextParsingElementUtil.isElementEnd(buffer, maxi - 1, maxi, false);
        }
        return false;
    }

    @Override // org.thymeleaf.templateparser.text.AbstractChainedTextHandler, org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleText(char[] buffer, int offset, int len, int line, int col) throws TextParseException {
        if (this.filterTexts) {
            filterText(buffer, offset, len, line, col);
        } else {
            super.handleText(buffer, offset, len, line, col);
        }
    }

    private void filterText(char[] buffer, int offset, int len, int line, int col) {
        if (this.filteredTextBuffer == null) {
            this.filteredTextBuffer = new char[Math.max(256, len)];
            this.filteredTextSize = 0;
            this.filteredTextLocator = new int[2];
        } else if (this.filteredTextSize + len > this.filteredTextBuffer.length) {
            char[] newFilteredTextBuffer = new char[Math.max(this.filteredTextBuffer.length + 256, this.filteredTextSize + len)];
            System.arraycopy(this.filteredTextBuffer, 0, newFilteredTextBuffer, 0, this.filteredTextSize);
            this.filteredTextBuffer = newFilteredTextBuffer;
        }
        System.arraycopy(buffer, offset, this.filteredTextBuffer, this.filteredTextSize, len);
        this.filteredTextSize += len;
        this.filteredTextLocator[0] = line;
        this.filteredTextLocator[1] = col;
    }

    private void processFilteredTexts() throws TextParseException {
        if (!this.filterTexts) {
            return;
        }
        int filterOffset = computeFilterOffset(this.filteredTextBuffer, 0, this.filteredTextSize, this.filteredTextLocator);
        if (filterOffset < this.filteredTextSize) {
            super.handleText(this.filteredTextBuffer, filterOffset, this.filteredTextSize - filterOffset, this.filteredTextLocator[0], this.filteredTextLocator[1]);
        }
        this.filteredTextSize = 0;
        this.filterTexts = false;
    }

    @Override // org.thymeleaf.templateparser.text.AbstractChainedTextHandler, org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws TextParseException {
        processFilteredTexts();
        super.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
    }

    @Override // org.thymeleaf.templateparser.text.AbstractChainedTextHandler, org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws TextParseException {
        processFilteredTexts();
        super.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.thymeleaf.templateparser.text.AbstractChainedTextHandler, org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws TextParseException {
        processFilteredTexts();
        super.handleCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    private static int computeFilterOffset(char[] buffer, int offset, int maxi, int[] locator) {
        if (offset == maxi) {
            return 0;
        }
        char literalDelimiter = 0;
        int arrayLevel = 0;
        int objectLevel = 0;
        int i = offset;
        while (i < maxi) {
            int i2 = i;
            i++;
            char c = buffer[i2];
            if (literalDelimiter != 0) {
                if (c == literalDelimiter && buffer[i - 2] != '\\') {
                    literalDelimiter = 0;
                }
                ParsingLocatorUtil.countChar(locator, c);
            } else if (c == '\'' || c == '\"') {
                literalDelimiter = c;
                ParsingLocatorUtil.countChar(locator, c);
            } else if (c == '{') {
                objectLevel++;
                ParsingLocatorUtil.countChar(locator, c);
            } else if (objectLevel > 0 && c == '}') {
                objectLevel--;
                ParsingLocatorUtil.countChar(locator, c);
            } else if (c == '[') {
                arrayLevel++;
                ParsingLocatorUtil.countChar(locator, c);
            } else if (arrayLevel > 0 && c == ']') {
                arrayLevel--;
                ParsingLocatorUtil.countChar(locator, c);
            } else {
                if (arrayLevel == 0 && objectLevel == 0) {
                    if (c == '\n') {
                        return i - 1;
                    }
                    if (c == ';' || c == ',' || c == ')' || c == '}' || c == ']') {
                        return i - 1;
                    }
                    if (c == '/' && i < maxi && buffer[i] == '/') {
                        return i - 1;
                    }
                }
                ParsingLocatorUtil.countChar(locator, c);
            }
        }
        return maxi;
    }
}