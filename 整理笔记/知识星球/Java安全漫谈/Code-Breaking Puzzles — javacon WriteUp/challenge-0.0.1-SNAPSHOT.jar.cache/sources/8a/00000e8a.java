package org.attoparser.minimize;

import org.apache.naming.EjbRef;
import org.attoparser.AbstractChainedMarkupHandler;
import org.attoparser.IMarkupHandler;
import org.attoparser.ParseException;
import org.attoparser.config.ParseConfiguration;
import org.attoparser.util.TextUtil;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.validation.DefaultBindingErrorProcessor;
import org.springframework.web.servlet.tags.form.AbstractHtmlInputElementTag;
import org.thymeleaf.engine.DocType;
import org.thymeleaf.spring5.processor.SpringInputGeneralFieldTagProcessor;
import org.thymeleaf.spring5.processor.SpringOptionInSelectFieldTagProcessor;
import org.thymeleaf.standard.processor.StandardRemoveTagProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/minimize/MinimizeHtmlMarkupHandler.class */
public final class MinimizeHtmlMarkupHandler extends AbstractChainedMarkupHandler {
    private static final String[] BLOCK_ELEMENTS = {"address", "article", "aside", "audio", "base", "blockquote", StandardRemoveTagProcessor.VALUE_BODY, "canvas", "caption", "col", "colgroup", "dd", "div", "dl", "dt", "fieldset", "figcaption", "figure", "footer", "form", "h1", "h2", "h3", "h4", "h5", "h6", "head", "header", "hgroup", "hr", DocType.DEFAULT_ELEMENT_NAME, "li", EjbRef.LINK, BeanDefinitionParserDelegate.META_ELEMENT, "noscript", "ol", SpringOptionInSelectFieldTagProcessor.OPTION_TAG_NAME, "output", "p", "pre", "script", "section", "style", "table", "tbody", "td", "tfoot", "th", "thead", "title", "tr", "ul", "video"};
    private static final String[] PREFORMATTED_ELEMENTS = {"pre", "script", "style", "textarea"};
    private static final String[] BOOLEAN_ATTRIBUTE_NAMES = {"allowfullscreen", "async", "autofocus", "autoplay", "checked", "compact", "controls", "declare", "default", "defaultchecked", "defaultmuted", "defaultselected", "defer", "disabled", "draggable", "enabled", "formnovalidate", SpringInputGeneralFieldTagProcessor.HIDDEN_INPUT_TYPE_ATTR_VALUE, "indeterminate", "inert", "ismap", "itemscope", "loop", "multiple", "muted", "nohref", "noresize", "noshade", "novalidate", "nowrap", "open", "pauseonexit", AbstractHtmlInputElementTag.READONLY_ATTRIBUTE, DefaultBindingErrorProcessor.MISSING_FIELD_ERROR_CODE, "reversed", "scoped", "seamless", "selected", "sortable", "spellcheck", "translate", "truespeed", "typemustmatch", "visible"};
    private static final char[] SIZE_ONE_WHITE_SPACE = {' '};
    private static final char[] ATTRIBUTE_OPERATOR = {'='};
    private final MinimizeMode minimizeMode;
    private char[] internalBuffer;
    private boolean lastTextEndedInWhiteSpace;
    private boolean lastOpenElementWasBlock;
    private boolean lastClosedElementWasBlock;
    private boolean lastVisibleEventWasElement;
    private boolean pendingInterBlockElementWhiteSpace;
    private boolean inPreformattedElement;
    private int pendingEventLine;
    private int pendingEventCol;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/minimize/MinimizeHtmlMarkupHandler$MinimizeMode.class */
    public enum MinimizeMode {
        ONLY_WHITE_SPACE(false, false, false, false),
        COMPLETE(true, true, true, true);
        
        private boolean removeComments;
        private boolean unquoteAttributes;
        private boolean unminimizeStandalones;
        private boolean minimizeBooleanAttributes;

        MinimizeMode(boolean removeComments, boolean unquoteAttributes, boolean unminimizeStandalones, boolean minimizeBooleanAttributes) {
            this.removeComments = removeComments;
            this.unquoteAttributes = unquoteAttributes;
            this.unminimizeStandalones = unminimizeStandalones;
            this.minimizeBooleanAttributes = minimizeBooleanAttributes;
        }
    }

    public MinimizeHtmlMarkupHandler(MinimizeMode minimizeMode, IMarkupHandler next) {
        super(next);
        this.internalBuffer = new char[30];
        this.lastTextEndedInWhiteSpace = false;
        this.lastOpenElementWasBlock = false;
        this.lastClosedElementWasBlock = false;
        this.lastVisibleEventWasElement = false;
        this.pendingInterBlockElementWhiteSpace = false;
        this.inPreformattedElement = false;
        this.pendingEventLine = 1;
        this.pendingEventCol = 1;
        if (minimizeMode == null) {
            throw new IllegalArgumentException("Minimize mode cannot be null");
        }
        this.minimizeMode = minimizeMode;
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IMarkupHandler
    public void setParseConfiguration(ParseConfiguration parseConfiguration) {
        if (!ParseConfiguration.ParsingMode.HTML.equals(parseConfiguration.getMode())) {
            throw new IllegalArgumentException("The " + getClass().getName() + " handler can only be used when parsing in HTML mode. Current parsing mode is " + parseConfiguration.getMode());
        }
        super.setParseConfiguration(parseConfiguration);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IDocumentHandler
    public void handleDocumentStart(long startTimeNanos, int line, int col) throws ParseException {
        getNext().handleDocumentStart(startTimeNanos, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IDocumentHandler
    public void handleDocumentEnd(long endTimeNanos, long totalTimeNanos, int line, int col) throws ParseException {
        getNext().handleDocumentEnd(endTimeNanos, totalTimeNanos, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.ITextHandler
    public void handleText(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        if (len == 0) {
            return;
        }
        flushPendingInterBlockElementWhiteSpace(false);
        if (this.inPreformattedElement) {
            this.lastTextEndedInWhiteSpace = false;
            this.lastVisibleEventWasElement = false;
            getNext().handleText(buffer, offset, len, line, col);
            return;
        }
        boolean wasWhiteSpace = this.lastTextEndedInWhiteSpace;
        boolean shouldCompress = false;
        boolean isAllWhiteSpace = true;
        int i = offset;
        int n = len;
        while (true) {
            if (shouldCompress && !isAllWhiteSpace) {
                break;
            }
            int i2 = n;
            n--;
            if (i2 == 0) {
                break;
            }
            if (isWhitespace(buffer[i])) {
                if (wasWhiteSpace || buffer[i] != ' ') {
                    shouldCompress = true;
                }
                wasWhiteSpace = true;
            } else {
                wasWhiteSpace = false;
                isAllWhiteSpace = false;
            }
            i++;
        }
        if (!shouldCompress) {
            this.lastTextEndedInWhiteSpace = isWhitespace(buffer[(offset + len) - 1]);
            if (this.lastVisibleEventWasElement && isAllWhiteSpace) {
                this.pendingInterBlockElementWhiteSpace = true;
                this.pendingEventLine = line;
                this.pendingEventCol = col;
                this.lastVisibleEventWasElement = false;
                return;
            }
            this.lastVisibleEventWasElement = false;
            getNext().handleText(buffer, offset, len, line, col);
            return;
        }
        if (this.internalBuffer.length < len) {
            this.internalBuffer = new char[len];
        }
        boolean wasWhiteSpace2 = this.lastTextEndedInWhiteSpace;
        int internalBufferSize = 0;
        int i3 = offset;
        int n2 = len;
        while (true) {
            int i4 = n2;
            n2--;
            if (i4 == 0) {
                break;
            }
            int i5 = i3;
            i3++;
            char c = buffer[i5];
            if (isWhitespace(c)) {
                if (!wasWhiteSpace2) {
                    wasWhiteSpace2 = true;
                    int i6 = internalBufferSize;
                    internalBufferSize++;
                    this.internalBuffer[i6] = ' ';
                }
            } else {
                wasWhiteSpace2 = false;
                int i7 = internalBufferSize;
                internalBufferSize++;
                this.internalBuffer[i7] = c;
            }
        }
        if (internalBufferSize > 0) {
            this.lastTextEndedInWhiteSpace = wasWhiteSpace2;
            if (this.lastVisibleEventWasElement && isAllWhiteSpace) {
                this.pendingInterBlockElementWhiteSpace = true;
                this.pendingEventLine = line;
                this.pendingEventCol = col;
                this.lastVisibleEventWasElement = false;
                return;
            }
            this.lastVisibleEventWasElement = false;
            getNext().handleText(this.internalBuffer, 0, internalBufferSize, line, col);
        }
    }

    private void flushPendingInterBlockElementWhiteSpace(boolean ignore) throws ParseException {
        if (this.pendingInterBlockElementWhiteSpace) {
            this.pendingInterBlockElementWhiteSpace = false;
            if (!ignore) {
                getNext().handleText(SIZE_ONE_WHITE_SPACE, 0, 1, this.pendingEventLine, this.pendingEventCol);
            }
        }
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.ICommentHandler
    public void handleComment(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col) throws ParseException {
        if (!this.minimizeMode.removeComments) {
            flushPendingInterBlockElementWhiteSpace(false);
            this.lastVisibleEventWasElement = false;
            this.lastTextEndedInWhiteSpace = false;
            getNext().handleComment(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
        }
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.ICDATASectionHandler
    public void handleCDATASection(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col) throws ParseException {
        flushPendingInterBlockElementWhiteSpace(false);
        this.lastVisibleEventWasElement = false;
        this.lastTextEndedInWhiteSpace = false;
        getNext().handleCDATASection(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        this.lastTextEndedInWhiteSpace = false;
        boolean ignorePendingWhiteSpace = (this.lastClosedElementWasBlock || this.lastOpenElementWasBlock) && isBlockElement(buffer, nameOffset, nameLen);
        flushPendingInterBlockElementWhiteSpace(ignorePendingWhiteSpace);
        if (this.minimizeMode.unminimizeStandalones) {
            getNext().handleStandaloneElementStart(buffer, nameOffset, nameLen, false, line, col);
        } else {
            getNext().handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
        }
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementEnd(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        this.lastTextEndedInWhiteSpace = false;
        this.lastClosedElementWasBlock = isBlockElement(buffer, nameOffset, nameLen);
        this.lastOpenElementWasBlock = false;
        this.lastVisibleEventWasElement = true;
        if (this.minimizeMode.unminimizeStandalones) {
            getNext().handleStandaloneElementEnd(buffer, nameOffset, nameLen, false, line, col);
        } else {
            getNext().handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col);
        }
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.lastTextEndedInWhiteSpace = false;
        boolean ignorePendingWhiteSpace = (this.lastClosedElementWasBlock || this.lastOpenElementWasBlock) && isBlockElement(buffer, nameOffset, nameLen);
        flushPendingInterBlockElementWhiteSpace(ignorePendingWhiteSpace);
        if (isPreformattedElement(buffer, nameOffset, nameLen)) {
            this.inPreformattedElement = true;
        }
        getNext().handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.lastTextEndedInWhiteSpace = false;
        this.lastOpenElementWasBlock = isBlockElement(buffer, nameOffset, nameLen);
        this.lastClosedElementWasBlock = false;
        this.lastVisibleEventWasElement = true;
        getNext().handleOpenElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.lastTextEndedInWhiteSpace = false;
        boolean ignorePendingWhiteSpace = (this.lastClosedElementWasBlock || this.lastOpenElementWasBlock) && isBlockElement(buffer, nameOffset, nameLen);
        flushPendingInterBlockElementWhiteSpace(ignorePendingWhiteSpace);
        if (isPreformattedElement(buffer, nameOffset, nameLen)) {
            this.inPreformattedElement = true;
        }
        getNext().handleAutoOpenElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.lastTextEndedInWhiteSpace = false;
        this.lastOpenElementWasBlock = isBlockElement(buffer, nameOffset, nameLen);
        this.lastClosedElementWasBlock = false;
        this.lastVisibleEventWasElement = true;
        getNext().handleAutoOpenElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.lastTextEndedInWhiteSpace = false;
        boolean ignorePendingWhiteSpace = this.lastClosedElementWasBlock && isBlockElement(buffer, nameOffset, nameLen);
        flushPendingInterBlockElementWhiteSpace(ignorePendingWhiteSpace);
        if (isPreformattedElement(buffer, nameOffset, nameLen)) {
            this.inPreformattedElement = false;
        }
        getNext().handleCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.lastTextEndedInWhiteSpace = false;
        this.lastClosedElementWasBlock = isBlockElement(buffer, nameOffset, nameLen);
        this.lastOpenElementWasBlock = false;
        this.lastVisibleEventWasElement = true;
        getNext().handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.lastTextEndedInWhiteSpace = false;
        boolean ignorePendingWhiteSpace = this.lastClosedElementWasBlock && isBlockElement(buffer, nameOffset, nameLen);
        flushPendingInterBlockElementWhiteSpace(ignorePendingWhiteSpace);
        getNext().handleAutoCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.lastTextEndedInWhiteSpace = false;
        this.lastClosedElementWasBlock = isBlockElement(buffer, nameOffset, nameLen);
        this.lastOpenElementWasBlock = false;
        this.lastVisibleEventWasElement = true;
        getNext().handleAutoCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.lastTextEndedInWhiteSpace = false;
        boolean ignorePendingWhiteSpace = this.lastClosedElementWasBlock && isBlockElement(buffer, nameOffset, nameLen);
        flushPendingInterBlockElementWhiteSpace(ignorePendingWhiteSpace);
        getNext().handleUnmatchedCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.lastTextEndedInWhiteSpace = false;
        this.lastClosedElementWasBlock = isBlockElement(buffer, nameOffset, nameLen);
        this.lastOpenElementWasBlock = false;
        this.lastVisibleEventWasElement = true;
        getNext().handleUnmatchedCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) throws ParseException {
        getNext().handleInnerWhiteSpace(SIZE_ONE_WHITE_SPACE, 0, SIZE_ONE_WHITE_SPACE.length, this.pendingEventLine, this.pendingEventCol);
        boolean isMinimizableBooleanAttribute = this.minimizeMode.minimizeBooleanAttributes && isBooleanAttribute(buffer, nameOffset, nameLen) && TextUtil.equals(false, buffer, nameOffset, nameLen, buffer, valueContentOffset, valueContentLen);
        if (!isMinimizableBooleanAttribute) {
            boolean canRemoveAttributeQuotes = this.minimizeMode.unquoteAttributes && canAttributeValueBeUnquoted(buffer, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen);
            if (operatorLen <= 1 && !canRemoveAttributeQuotes) {
                getNext().handleAttribute(buffer, nameOffset, nameLen, nameLine, nameCol, operatorOffset, operatorLen, operatorLine, operatorCol, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);
                return;
            }
            int requiredLen = nameLen + 1 + valueOuterLen;
            if (this.internalBuffer.length < requiredLen) {
                this.internalBuffer = new char[requiredLen];
            }
            System.arraycopy(buffer, nameOffset, this.internalBuffer, 0, nameLen);
            System.arraycopy(ATTRIBUTE_OPERATOR, 0, this.internalBuffer, nameLen, ATTRIBUTE_OPERATOR.length);
            if (canRemoveAttributeQuotes) {
                System.arraycopy(buffer, valueContentOffset, this.internalBuffer, nameLen + ATTRIBUTE_OPERATOR.length, valueContentLen);
                getNext().handleAttribute(this.internalBuffer, 0, nameLen, nameLine, nameCol, nameLen, ATTRIBUTE_OPERATOR.length, operatorLine, operatorCol, nameLen + ATTRIBUTE_OPERATOR.length, valueContentLen, nameLen + ATTRIBUTE_OPERATOR.length, valueContentLen, valueLine, valueCol);
                return;
            }
            System.arraycopy(buffer, valueOuterOffset, this.internalBuffer, nameLen + ATTRIBUTE_OPERATOR.length, valueOuterLen);
            getNext().handleAttribute(this.internalBuffer, 0, nameLen, nameLine, nameCol, nameLen, ATTRIBUTE_OPERATOR.length, operatorLine, operatorCol, nameLen + ATTRIBUTE_OPERATOR.length + (valueOuterOffset - valueContentOffset), valueContentLen, nameLen + ATTRIBUTE_OPERATOR.length, valueOuterLen, valueLine, valueCol);
            return;
        }
        getNext().handleAttribute(buffer, nameOffset, nameLen, nameLine, nameCol, 0, 0, operatorLine, operatorCol, 0, 0, 0, 0, operatorLen, operatorCol);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleInnerWhiteSpace(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        this.pendingEventLine = line;
        this.pendingEventCol = col;
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IDocTypeHandler
    public void handleDocType(char[] buffer, int keywordOffset, int keywordLen, int keywordLine, int keywordCol, int elementNameOffset, int elementNameLen, int elementNameLine, int elementNameCol, int typeOffset, int typeLen, int typeLine, int typeCol, int publicIdOffset, int publicIdLen, int publicIdLine, int publicIdCol, int systemIdOffset, int systemIdLen, int systemIdLine, int systemIdCol, int internalSubsetOffset, int internalSubsetLen, int internalSubsetLine, int internalSubsetCol, int outerOffset, int outerLen, int outerLine, int outerCol) throws ParseException {
        flushPendingInterBlockElementWhiteSpace(false);
        this.lastVisibleEventWasElement = false;
        this.lastTextEndedInWhiteSpace = false;
        getNext().handleDocType(buffer, keywordOffset, keywordLen, keywordLine, keywordCol, elementNameOffset, elementNameLen, elementNameLine, elementNameCol, typeOffset, typeLen, typeLine, typeCol, publicIdOffset, publicIdLen, publicIdLine, publicIdCol, systemIdOffset, systemIdLen, systemIdLine, systemIdCol, internalSubsetOffset, internalSubsetLen, internalSubsetLine, internalSubsetCol, outerOffset, outerLen, outerLine, outerCol);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IXMLDeclarationHandler
    public void handleXmlDeclaration(char[] buffer, int keywordOffset, int keywordLen, int keywordLine, int keywordCol, int versionOffset, int versionLen, int versionLine, int versionCol, int encodingOffset, int encodingLen, int encodingLine, int encodingCol, int standaloneOffset, int standaloneLen, int standaloneLine, int standaloneCol, int outerOffset, int outerLen, int line, int col) throws ParseException {
        flushPendingInterBlockElementWhiteSpace(false);
        this.lastVisibleEventWasElement = false;
        this.lastTextEndedInWhiteSpace = false;
        getNext().handleXmlDeclaration(buffer, keywordOffset, keywordLen, keywordLine, keywordCol, versionOffset, versionLen, versionLine, versionCol, encodingOffset, encodingLen, encodingLine, encodingCol, standaloneOffset, standaloneLen, standaloneLine, standaloneCol, outerOffset, outerLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IProcessingInstructionHandler
    public void handleProcessingInstruction(char[] buffer, int targetOffset, int targetLen, int targetLine, int targetCol, int contentOffset, int contentLen, int contentLine, int contentCol, int outerOffset, int outerLen, int line, int col) throws ParseException {
        flushPendingInterBlockElementWhiteSpace(false);
        this.lastVisibleEventWasElement = false;
        this.lastTextEndedInWhiteSpace = false;
        getNext().handleProcessingInstruction(buffer, targetOffset, targetLen, targetLine, targetCol, contentOffset, contentLen, contentLine, contentCol, outerOffset, outerLen, line, col);
    }

    private static boolean canAttributeValueBeUnquoted(char[] buffer, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen) {
        if (valueContentLen == 0 || valueOuterLen == valueContentLen) {
            return false;
        }
        int i = valueContentOffset;
        int n = valueContentLen;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c = buffer[i];
                if ((c < 'a' || c > 'z') && ((c < 'A' || c > 'Z') && (c < '0' || c > '9'))) {
                    return false;
                }
                i++;
            } else {
                return true;
            }
        }
    }

    private static boolean isWhitespace(char c) {
        return c == ' ' || c == '\n' || c == '\t' || c == '\r' || c == '\f' || c == 11 || c == 28 || c == 29 || c == 30 || c == 31 || (c > 127 && Character.isWhitespace(c));
    }

    private static boolean isBlockElement(char[] buffer, int nameOffset, int nameLen) {
        return TextUtil.binarySearch(false, (CharSequence[]) BLOCK_ELEMENTS, buffer, nameOffset, nameLen) >= 0;
    }

    private static boolean isPreformattedElement(char[] buffer, int nameOffset, int nameLen) {
        int i = 0;
        int n = PREFORMATTED_ELEMENTS.length;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                if (TextUtil.compareTo(false, (CharSequence) PREFORMATTED_ELEMENTS[i], 0, PREFORMATTED_ELEMENTS[i].length(), buffer, nameOffset, nameLen) == 0) {
                    return true;
                }
                i++;
            } else {
                return false;
            }
        }
    }

    private static boolean isBooleanAttribute(char[] buffer, int nameOffset, int nameLen) {
        return TextUtil.binarySearch(false, (CharSequence[]) BOOLEAN_ATTRIBUTE_NAMES, buffer, nameOffset, nameLen) >= 0;
    }
}