package org.attoparser.select;

import java.util.Arrays;
import java.util.List;
import org.attoparser.AbstractMarkupHandler;
import org.attoparser.IMarkupHandler;
import org.attoparser.ParseException;
import org.attoparser.ParseStatus;
import org.attoparser.config.ParseConfiguration;
import org.attoparser.discard.DiscardMarkupHandler;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/select/BlockSelectorMarkupHandler.class */
public final class BlockSelectorMarkupHandler extends AbstractMarkupHandler {
    private static final DiscardMarkupHandler DISCARD_MARKUP_HANDLER = new DiscardMarkupHandler();
    private final IMarkupHandler selectedHandler;
    private final IMarkupHandler nonSelectedHandler;
    private ParseSelection selection;
    private int selectionIndex;
    private final IMarkupSelectorReferenceResolver referenceResolver;
    private final SelectorElementBuffer elementBuffer;
    private IMarkupHandler documentStartEndHandler;
    private final int selectorsLen;
    private final String[] selectors;
    private final boolean[] selectorMatches;
    private final MarkupSelectorFilter[] selectorFilters;
    private boolean insideAllSelectorMatchingBlock;
    private boolean someSelectorsMatch;
    private int markupLevel;
    private int[] matchingMarkupLevelsPerSelector;
    private static final int MARKUP_BLOCKS_LEN = 10;
    private int[] markupBlocks;
    private int markupBlockIndex;

    public BlockSelectorMarkupHandler(IMarkupHandler selectedHandler, String selector) {
        this(selectedHandler, DISCARD_MARKUP_HANDLER, new String[]{selector}, (IMarkupSelectorReferenceResolver) null);
    }

    public BlockSelectorMarkupHandler(IMarkupHandler selectedHandler, String selector, IMarkupSelectorReferenceResolver referenceResolver) {
        this(selectedHandler, DISCARD_MARKUP_HANDLER, new String[]{selector}, referenceResolver);
    }

    public BlockSelectorMarkupHandler(IMarkupHandler selectedHandler, IMarkupHandler nonSelectedHandler, String selector) {
        this(selectedHandler, nonSelectedHandler, new String[]{selector}, (IMarkupSelectorReferenceResolver) null);
    }

    public BlockSelectorMarkupHandler(IMarkupHandler selectedHandler, IMarkupHandler nonSelectedHandler, String selector, IMarkupSelectorReferenceResolver referenceResolver) {
        this(selectedHandler, nonSelectedHandler, new String[]{selector}, referenceResolver);
    }

    public BlockSelectorMarkupHandler(IMarkupHandler selectedHandler, String[] selectors) {
        this(selectedHandler, DISCARD_MARKUP_HANDLER, selectors, (IMarkupSelectorReferenceResolver) null);
    }

    public BlockSelectorMarkupHandler(IMarkupHandler selectedHandler, String[] selectors, IMarkupSelectorReferenceResolver referenceResolver) {
        this(selectedHandler, DISCARD_MARKUP_HANDLER, selectors, referenceResolver);
    }

    public BlockSelectorMarkupHandler(IMarkupHandler selectedHandler, IMarkupHandler nonSelectedHandler, String[] selectors) {
        this(selectedHandler, nonSelectedHandler, selectors, (IMarkupSelectorReferenceResolver) null);
    }

    public BlockSelectorMarkupHandler(IMarkupHandler selectedHandler, IMarkupHandler nonSelectedHandler, String[] selectors, IMarkupSelectorReferenceResolver referenceResolver) {
        this.selectionIndex = -1;
        if (selectors == null || selectors.length == 0) {
            throw new IllegalArgumentException("Selector array cannot be null or empty");
        }
        for (String selector : selectors) {
            if (selector == null || selector.trim().length() == 0) {
                throw new IllegalArgumentException("Selector array contains at least one null or empty item, which is forbidden");
            }
        }
        this.selectedHandler = selectedHandler;
        this.nonSelectedHandler = nonSelectedHandler;
        this.documentStartEndHandler = this.selectedHandler;
        this.referenceResolver = referenceResolver;
        this.selectors = selectors;
        this.selectorsLen = selectors.length;
        this.selectorMatches = new boolean[this.selectors.length];
        Arrays.fill(this.selectorMatches, false);
        this.someSelectorsMatch = false;
        this.insideAllSelectorMatchingBlock = false;
        this.selectorFilters = new MarkupSelectorFilter[this.selectorsLen];
        this.elementBuffer = new SelectorElementBuffer();
        this.markupLevel = 0;
        this.matchingMarkupLevelsPerSelector = new int[this.selectorsLen];
        Arrays.fill(this.matchingMarkupLevelsPerSelector, Integer.MAX_VALUE);
        this.markupBlockIndex = 0;
        this.markupBlocks = new int[10];
        this.markupBlocks[this.markupLevel] = this.markupBlockIndex;
    }

    public void setDocumentStartEndHandler(IMarkupHandler documentStartEndHandler) {
        if (documentStartEndHandler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }
        this.documentStartEndHandler = documentStartEndHandler;
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IMarkupHandler
    public void setParseConfiguration(ParseConfiguration parseConfiguration) {
        boolean html = ParseConfiguration.ParsingMode.HTML == parseConfiguration.getMode();
        for (int i = 0; i < this.selectorsLen; i++) {
            List<IMarkupSelectorItem> selectorItems = MarkupSelectorItems.forSelector(html, this.selectors[i], this.referenceResolver);
            this.selectorFilters[i] = new MarkupSelectorFilter(null, selectorItems.get(0));
            MarkupSelectorFilter last = this.selectorFilters[i];
            for (int j = 1; j < selectorItems.size(); j++) {
                last = new MarkupSelectorFilter(last, selectorItems.get(j));
            }
        }
        this.selectedHandler.setParseConfiguration(parseConfiguration);
        if (this.nonSelectedHandler != this.selectedHandler) {
            this.nonSelectedHandler.setParseConfiguration(parseConfiguration);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IMarkupHandler
    public void setParseStatus(ParseStatus status) {
        this.selectedHandler.setParseStatus(status);
        if (this.nonSelectedHandler != this.selectedHandler) {
            this.nonSelectedHandler.setParseStatus(status);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IMarkupHandler
    public void setParseSelection(ParseSelection selection) {
        if (this.selection == null) {
            this.selection = selection;
        }
        if (this.selectionIndex == -1) {
            this.selectionIndex = this.selection.subscribeLevel();
        }
        this.selectedHandler.setParseSelection(selection);
        if (this.nonSelectedHandler != this.selectedHandler) {
            this.nonSelectedHandler.setParseSelection(selection);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocumentHandler
    public void handleDocumentStart(long startTimeNanos, int line, int col) throws ParseException {
        this.selection.levels[this.selectionIndex].selectors = this.selectors;
        this.documentStartEndHandler.handleDocumentStart(startTimeNanos, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocumentHandler
    public void handleDocumentEnd(long endTimeNanos, long totalTimeNanos, int line, int col) throws ParseException {
        this.documentStartEndHandler.handleDocumentEnd(endTimeNanos, totalTimeNanos, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IXMLDeclarationHandler
    public void handleXmlDeclaration(char[] buffer, int keywordOffset, int keywordLen, int keywordLine, int keywordCol, int versionOffset, int versionLen, int versionLine, int versionCol, int encodingOffset, int encodingLen, int encodingLine, int encodingCol, int standaloneOffset, int standaloneLen, int standaloneLine, int standaloneCol, int outerOffset, int outerLen, int line, int col) throws ParseException {
        if (!this.insideAllSelectorMatchingBlock) {
            this.someSelectorsMatch = false;
            for (int i = 0; i < this.selectorsLen; i++) {
                if (this.matchingMarkupLevelsPerSelector[i] > this.markupLevel) {
                    this.selectorMatches[i] = this.selectorFilters[i].matchXmlDeclaration(true, this.markupLevel, this.markupBlocks[this.markupLevel]);
                    if (this.selectorMatches[i]) {
                        this.someSelectorsMatch = true;
                    }
                } else {
                    this.selectorMatches[i] = true;
                    this.someSelectorsMatch = true;
                }
            }
            if (this.someSelectorsMatch) {
                markCurrentSelection();
                this.selectedHandler.handleXmlDeclaration(buffer, keywordOffset, keywordLen, keywordLine, keywordCol, versionOffset, versionLen, versionLine, versionCol, encodingOffset, encodingLen, encodingLine, encodingCol, standaloneOffset, standaloneLen, standaloneLine, standaloneCol, outerOffset, outerLen, line, col);
                unmarkCurrentSelection();
                return;
            }
            unmarkCurrentSelection();
            this.nonSelectedHandler.handleXmlDeclaration(buffer, keywordOffset, keywordLen, keywordLine, keywordCol, versionOffset, versionLen, versionLine, versionCol, encodingOffset, encodingLen, encodingLine, encodingCol, standaloneOffset, standaloneLen, standaloneLine, standaloneCol, outerOffset, outerLen, line, col);
            return;
        }
        markCurrentSelection();
        this.selectedHandler.handleXmlDeclaration(buffer, keywordOffset, keywordLen, keywordLine, keywordCol, versionOffset, versionLen, versionLine, versionCol, encodingOffset, encodingLen, encodingLine, encodingCol, standaloneOffset, standaloneLen, standaloneLine, standaloneCol, outerOffset, outerLen, line, col);
        unmarkCurrentSelection();
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocTypeHandler
    public void handleDocType(char[] buffer, int keywordOffset, int keywordLen, int keywordLine, int keywordCol, int elementNameOffset, int elementNameLen, int elementNameLine, int elementNameCol, int typeOffset, int typeLen, int typeLine, int typeCol, int publicIdOffset, int publicIdLen, int publicIdLine, int publicIdCol, int systemIdOffset, int systemIdLen, int systemIdLine, int systemIdCol, int internalSubsetOffset, int internalSubsetLen, int internalSubsetLine, int internalSubsetCol, int outerOffset, int outerLen, int outerLine, int outerCol) throws ParseException {
        if (!this.insideAllSelectorMatchingBlock) {
            this.someSelectorsMatch = false;
            for (int i = 0; i < this.selectorsLen; i++) {
                if (this.matchingMarkupLevelsPerSelector[i] > this.markupLevel) {
                    this.selectorMatches[i] = this.selectorFilters[i].matchDocTypeClause(true, this.markupLevel, this.markupBlocks[this.markupLevel]);
                    if (this.selectorMatches[i]) {
                        this.someSelectorsMatch = true;
                    }
                } else {
                    this.selectorMatches[i] = true;
                    this.someSelectorsMatch = true;
                }
            }
            if (this.someSelectorsMatch) {
                markCurrentSelection();
                this.selectedHandler.handleDocType(buffer, keywordOffset, keywordLen, keywordLine, keywordCol, elementNameOffset, elementNameLen, elementNameLine, elementNameCol, typeOffset, typeLen, typeLine, typeCol, publicIdOffset, publicIdLen, publicIdLine, publicIdCol, systemIdOffset, systemIdLen, systemIdLine, systemIdCol, internalSubsetOffset, internalSubsetLen, internalSubsetLine, internalSubsetCol, outerOffset, outerLen, outerLine, outerCol);
                unmarkCurrentSelection();
                return;
            }
            unmarkCurrentSelection();
            this.nonSelectedHandler.handleDocType(buffer, keywordOffset, keywordLen, keywordLine, keywordCol, elementNameOffset, elementNameLen, elementNameLine, elementNameCol, typeOffset, typeLen, typeLine, typeCol, publicIdOffset, publicIdLen, publicIdLine, publicIdCol, systemIdOffset, systemIdLen, systemIdLine, systemIdCol, internalSubsetOffset, internalSubsetLen, internalSubsetLine, internalSubsetCol, outerOffset, outerLen, outerLine, outerCol);
            return;
        }
        markCurrentSelection();
        this.selectedHandler.handleDocType(buffer, keywordOffset, keywordLen, keywordLine, keywordCol, elementNameOffset, elementNameLen, elementNameLine, elementNameCol, typeOffset, typeLen, typeLine, typeCol, publicIdOffset, publicIdLen, publicIdLine, publicIdCol, systemIdOffset, systemIdLen, systemIdLine, systemIdCol, internalSubsetOffset, internalSubsetLen, internalSubsetLine, internalSubsetCol, outerOffset, outerLen, outerLine, outerCol);
        unmarkCurrentSelection();
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ICDATASectionHandler
    public void handleCDATASection(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col) throws ParseException {
        if (!this.insideAllSelectorMatchingBlock) {
            this.someSelectorsMatch = false;
            for (int i = 0; i < this.selectorsLen; i++) {
                if (this.matchingMarkupLevelsPerSelector[i] > this.markupLevel) {
                    this.selectorMatches[i] = this.selectorFilters[i].matchCDATASection(true, this.markupLevel, this.markupBlocks[this.markupLevel]);
                    if (this.selectorMatches[i]) {
                        this.someSelectorsMatch = true;
                    }
                } else {
                    this.selectorMatches[i] = true;
                    this.someSelectorsMatch = true;
                }
            }
            if (this.someSelectorsMatch) {
                markCurrentSelection();
                this.selectedHandler.handleCDATASection(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
                unmarkCurrentSelection();
                return;
            }
            unmarkCurrentSelection();
            this.nonSelectedHandler.handleCDATASection(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
            return;
        }
        markCurrentSelection();
        this.selectedHandler.handleCDATASection(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
        unmarkCurrentSelection();
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ITextHandler
    public void handleText(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        if (!this.insideAllSelectorMatchingBlock) {
            this.someSelectorsMatch = false;
            for (int i = 0; i < this.selectorsLen; i++) {
                if (this.matchingMarkupLevelsPerSelector[i] > this.markupLevel) {
                    this.selectorMatches[i] = this.selectorFilters[i].matchText(true, this.markupLevel, this.markupBlocks[this.markupLevel]);
                    if (this.selectorMatches[i]) {
                        this.someSelectorsMatch = true;
                    }
                } else {
                    this.selectorMatches[i] = true;
                    this.someSelectorsMatch = true;
                }
            }
            if (this.someSelectorsMatch) {
                markCurrentSelection();
                this.selectedHandler.handleText(buffer, offset, len, line, col);
                unmarkCurrentSelection();
                return;
            }
            unmarkCurrentSelection();
            this.nonSelectedHandler.handleText(buffer, offset, len, line, col);
            return;
        }
        markCurrentSelection();
        this.selectedHandler.handleText(buffer, offset, len, line, col);
        unmarkCurrentSelection();
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ICommentHandler
    public void handleComment(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col) throws ParseException {
        if (!this.insideAllSelectorMatchingBlock) {
            this.someSelectorsMatch = false;
            for (int i = 0; i < this.selectorsLen; i++) {
                if (this.matchingMarkupLevelsPerSelector[i] > this.markupLevel) {
                    this.selectorMatches[i] = this.selectorFilters[i].matchComment(true, this.markupLevel, this.markupBlocks[this.markupLevel]);
                    if (this.selectorMatches[i]) {
                        this.someSelectorsMatch = true;
                    }
                } else {
                    this.selectorMatches[i] = true;
                    this.someSelectorsMatch = true;
                }
            }
            if (this.someSelectorsMatch) {
                markCurrentSelection();
                this.selectedHandler.handleComment(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
                unmarkCurrentSelection();
                return;
            }
            unmarkCurrentSelection();
            this.nonSelectedHandler.handleComment(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
            return;
        }
        markCurrentSelection();
        this.selectedHandler.handleComment(buffer, contentOffset, contentLen, outerOffset, outerLen, line, col);
        unmarkCurrentSelection();
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) throws ParseException {
        if (!this.insideAllSelectorMatchingBlock) {
            this.elementBuffer.bufferAttribute(buffer, nameOffset, nameLen, nameLine, nameCol, operatorOffset, operatorLen, operatorLine, operatorCol, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);
        } else {
            this.selectedHandler.handleAttribute(buffer, nameOffset, nameLen, nameLine, nameCol, operatorOffset, operatorLen, operatorLine, operatorCol, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        if (!this.insideAllSelectorMatchingBlock) {
            this.elementBuffer.bufferElementStart(buffer, nameOffset, nameLen, line, col, true, minimized);
            return;
        }
        markCurrentSelection();
        this.selectedHandler.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementEnd(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        if (!this.insideAllSelectorMatchingBlock) {
            this.elementBuffer.bufferElementEnd(buffer, nameOffset, nameLen, line, col);
            this.someSelectorsMatch = false;
            for (int i = 0; i < this.selectorsLen; i++) {
                if (this.matchingMarkupLevelsPerSelector[i] > this.markupLevel) {
                    this.selectorMatches[i] = this.selectorFilters[i].matchStandaloneElement(true, this.markupLevel, this.markupBlocks[this.markupLevel], this.elementBuffer);
                    if (this.selectorMatches[i]) {
                        this.someSelectorsMatch = true;
                    }
                } else {
                    this.selectorMatches[i] = true;
                    this.someSelectorsMatch = true;
                }
            }
            if (this.someSelectorsMatch) {
                markCurrentSelection();
                this.elementBuffer.flushBuffer(this.selectedHandler, false);
                unmarkCurrentSelection();
                return;
            }
            unmarkCurrentSelection();
            this.elementBuffer.flushBuffer(this.nonSelectedHandler, false);
            return;
        }
        this.selectedHandler.handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col);
        unmarkCurrentSelection();
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        if (!this.insideAllSelectorMatchingBlock) {
            this.elementBuffer.bufferElementStart(buffer, nameOffset, nameLen, line, col, false, false);
            return;
        }
        markCurrentSelection();
        this.selectedHandler.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        if (!this.insideAllSelectorMatchingBlock) {
            this.elementBuffer.bufferElementEnd(buffer, nameOffset, nameLen, line, col);
            this.someSelectorsMatch = false;
            for (int i = 0; i < this.selectorsLen; i++) {
                if (this.matchingMarkupLevelsPerSelector[i] > this.markupLevel) {
                    this.selectorMatches[i] = this.selectorFilters[i].matchOpenElement(true, this.markupLevel, this.markupBlocks[this.markupLevel], this.elementBuffer);
                    if (this.selectorMatches[i]) {
                        this.someSelectorsMatch = true;
                        this.matchingMarkupLevelsPerSelector[i] = this.markupLevel;
                    }
                } else {
                    this.selectorMatches[i] = true;
                    this.someSelectorsMatch = true;
                }
            }
            if (this.someSelectorsMatch) {
                updateInsideAllSelectorMatchingBlockFlag();
                this.markupLevel++;
                checkSizeOfMarkupBlocksStructure(this.markupLevel);
                int[] iArr = this.markupBlocks;
                int i2 = this.markupLevel;
                int i3 = this.markupBlockIndex + 1;
                this.markupBlockIndex = i3;
                iArr[i2] = i3;
                markCurrentSelection();
                this.elementBuffer.flushBuffer(this.selectedHandler, false);
                unmarkCurrentSelection();
                return;
            }
            this.markupLevel++;
            checkSizeOfMarkupBlocksStructure(this.markupLevel);
            int[] iArr2 = this.markupBlocks;
            int i4 = this.markupLevel;
            int i5 = this.markupBlockIndex + 1;
            this.markupBlockIndex = i5;
            iArr2[i4] = i5;
            unmarkCurrentSelection();
            this.elementBuffer.flushBuffer(this.nonSelectedHandler, false);
            return;
        }
        this.markupLevel++;
        checkSizeOfMarkupBlocksStructure(this.markupLevel);
        int[] iArr3 = this.markupBlocks;
        int i6 = this.markupLevel;
        int i7 = this.markupBlockIndex + 1;
        this.markupBlockIndex = i7;
        iArr3[i6] = i7;
        this.selectedHandler.handleOpenElementEnd(buffer, nameOffset, nameLen, line, col);
        unmarkCurrentSelection();
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        if (!this.insideAllSelectorMatchingBlock) {
            this.elementBuffer.bufferElementStart(buffer, nameOffset, nameLen, line, col, false, false);
            return;
        }
        markCurrentSelection();
        this.selectedHandler.handleAutoOpenElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        if (!this.insideAllSelectorMatchingBlock) {
            this.elementBuffer.bufferElementEnd(buffer, nameOffset, nameLen, line, col);
            this.someSelectorsMatch = false;
            for (int i = 0; i < this.selectorsLen; i++) {
                if (this.matchingMarkupLevelsPerSelector[i] > this.markupLevel) {
                    this.selectorMatches[i] = this.selectorFilters[i].matchOpenElement(true, this.markupLevel, this.markupBlocks[this.markupLevel], this.elementBuffer);
                    if (this.selectorMatches[i]) {
                        this.someSelectorsMatch = true;
                        this.matchingMarkupLevelsPerSelector[i] = this.markupLevel;
                    }
                } else {
                    this.selectorMatches[i] = true;
                    this.someSelectorsMatch = true;
                }
            }
            if (this.someSelectorsMatch) {
                updateInsideAllSelectorMatchingBlockFlag();
                this.markupLevel++;
                checkSizeOfMarkupBlocksStructure(this.markupLevel);
                int[] iArr = this.markupBlocks;
                int i2 = this.markupLevel;
                int i3 = this.markupBlockIndex + 1;
                this.markupBlockIndex = i3;
                iArr[i2] = i3;
                markCurrentSelection();
                this.elementBuffer.flushBuffer(this.selectedHandler, true);
                unmarkCurrentSelection();
                return;
            }
            this.markupLevel++;
            checkSizeOfMarkupBlocksStructure(this.markupLevel);
            int[] iArr2 = this.markupBlocks;
            int i4 = this.markupLevel;
            int i5 = this.markupBlockIndex + 1;
            this.markupBlockIndex = i5;
            iArr2[i4] = i5;
            unmarkCurrentSelection();
            this.elementBuffer.flushBuffer(this.nonSelectedHandler, true);
            return;
        }
        this.markupLevel++;
        checkSizeOfMarkupBlocksStructure(this.markupLevel);
        int[] iArr3 = this.markupBlocks;
        int i6 = this.markupLevel;
        int i7 = this.markupBlockIndex + 1;
        this.markupBlockIndex = i7;
        iArr3[i6] = i7;
        this.selectedHandler.handleAutoOpenElementEnd(buffer, nameOffset, nameLen, line, col);
        unmarkCurrentSelection();
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.markupLevel--;
        for (int i = 0; i < this.selectorsLen; i++) {
            this.selectorFilters[i].removeMatchesForLevel(this.markupLevel);
        }
        if (!this.insideAllSelectorMatchingBlock) {
            this.someSelectorsMatch = false;
            for (int i2 = 0; i2 < this.selectorsLen; i2++) {
                this.selectorMatches[i2] = this.matchingMarkupLevelsPerSelector[i2] <= this.markupLevel;
                if (this.selectorMatches[i2]) {
                    this.someSelectorsMatch = true;
                }
            }
            if (this.someSelectorsMatch) {
                markCurrentSelection();
                this.selectedHandler.handleCloseElementStart(buffer, nameOffset, nameLen, line, col);
                return;
            }
            unmarkCurrentSelection();
            this.nonSelectedHandler.handleCloseElementStart(buffer, nameOffset, nameLen, line, col);
            return;
        }
        markCurrentSelection();
        this.selectedHandler.handleCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        if (!this.insideAllSelectorMatchingBlock) {
            this.someSelectorsMatch = false;
            for (int i = 0; i < this.selectorsLen; i++) {
                this.selectorMatches[i] = this.matchingMarkupLevelsPerSelector[i] <= this.markupLevel;
                if (this.selectorMatches[i]) {
                    this.someSelectorsMatch = true;
                }
            }
            for (int i2 = 0; i2 < this.selectorsLen; i2++) {
                if (this.matchingMarkupLevelsPerSelector[i2] == this.markupLevel) {
                    this.insideAllSelectorMatchingBlock = false;
                    this.matchingMarkupLevelsPerSelector[i2] = Integer.MAX_VALUE;
                }
            }
            if (this.someSelectorsMatch) {
                this.selectedHandler.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
                unmarkCurrentSelection();
                return;
            }
            unmarkCurrentSelection();
            this.nonSelectedHandler.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
            return;
        }
        for (int i3 = 0; i3 < this.selectorsLen; i3++) {
            if (this.matchingMarkupLevelsPerSelector[i3] == this.markupLevel) {
                this.insideAllSelectorMatchingBlock = false;
                this.matchingMarkupLevelsPerSelector[i3] = Integer.MAX_VALUE;
            }
        }
        this.selectedHandler.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
        unmarkCurrentSelection();
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.markupLevel--;
        for (int i = 0; i < this.selectorsLen; i++) {
            this.selectorFilters[i].removeMatchesForLevel(this.markupLevel);
        }
        if (!this.insideAllSelectorMatchingBlock) {
            this.someSelectorsMatch = false;
            for (int i2 = 0; i2 < this.selectorsLen; i2++) {
                this.selectorMatches[i2] = this.matchingMarkupLevelsPerSelector[i2] <= this.markupLevel;
                if (this.selectorMatches[i2]) {
                    this.someSelectorsMatch = true;
                }
            }
            if (this.someSelectorsMatch) {
                markCurrentSelection();
                this.selectedHandler.handleAutoCloseElementStart(buffer, nameOffset, nameLen, line, col);
                return;
            }
            unmarkCurrentSelection();
            this.nonSelectedHandler.handleAutoCloseElementStart(buffer, nameOffset, nameLen, line, col);
            return;
        }
        markCurrentSelection();
        this.selectedHandler.handleAutoCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        if (!this.insideAllSelectorMatchingBlock) {
            this.someSelectorsMatch = false;
            for (int i = 0; i < this.selectorsLen; i++) {
                this.selectorMatches[i] = this.matchingMarkupLevelsPerSelector[i] <= this.markupLevel;
                if (this.selectorMatches[i]) {
                    this.someSelectorsMatch = true;
                }
            }
            for (int i2 = 0; i2 < this.selectorsLen; i2++) {
                if (this.matchingMarkupLevelsPerSelector[i2] == this.markupLevel) {
                    this.insideAllSelectorMatchingBlock = false;
                    this.matchingMarkupLevelsPerSelector[i2] = Integer.MAX_VALUE;
                }
            }
            if (this.someSelectorsMatch) {
                this.selectedHandler.handleAutoCloseElementEnd(buffer, nameOffset, nameLen, line, col);
                unmarkCurrentSelection();
                return;
            }
            unmarkCurrentSelection();
            this.nonSelectedHandler.handleAutoCloseElementEnd(buffer, nameOffset, nameLen, line, col);
            return;
        }
        for (int i3 = 0; i3 < this.selectorsLen; i3++) {
            if (this.matchingMarkupLevelsPerSelector[i3] == this.markupLevel) {
                this.insideAllSelectorMatchingBlock = false;
                this.matchingMarkupLevelsPerSelector[i3] = Integer.MAX_VALUE;
            }
        }
        this.selectedHandler.handleAutoCloseElementEnd(buffer, nameOffset, nameLen, line, col);
        unmarkCurrentSelection();
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        if (!this.insideAllSelectorMatchingBlock) {
            this.someSelectorsMatch = false;
            for (int i = 0; i < this.selectorsLen; i++) {
                this.selectorMatches[i] = this.matchingMarkupLevelsPerSelector[i] <= this.markupLevel;
                if (this.selectorMatches[i]) {
                    this.someSelectorsMatch = true;
                }
            }
            if (this.someSelectorsMatch) {
                markCurrentSelection();
                this.selectedHandler.handleUnmatchedCloseElementStart(buffer, nameOffset, nameLen, line, col);
                return;
            }
            unmarkCurrentSelection();
            this.nonSelectedHandler.handleUnmatchedCloseElementStart(buffer, nameOffset, nameLen, line, col);
            return;
        }
        markCurrentSelection();
        this.selectedHandler.handleUnmatchedCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        if (!this.insideAllSelectorMatchingBlock) {
            this.someSelectorsMatch = false;
            for (int i = 0; i < this.selectorsLen; i++) {
                this.selectorMatches[i] = this.matchingMarkupLevelsPerSelector[i] <= this.markupLevel;
                if (this.selectorMatches[i]) {
                    this.someSelectorsMatch = true;
                }
            }
            if (this.someSelectorsMatch) {
                this.selectedHandler.handleUnmatchedCloseElementEnd(buffer, nameOffset, nameLen, line, col);
                unmarkCurrentSelection();
                return;
            }
            unmarkCurrentSelection();
            this.nonSelectedHandler.handleUnmatchedCloseElementEnd(buffer, nameOffset, nameLen, line, col);
            return;
        }
        this.selectedHandler.handleUnmatchedCloseElementEnd(buffer, nameOffset, nameLen, line, col);
        unmarkCurrentSelection();
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleInnerWhiteSpace(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        if (!this.insideAllSelectorMatchingBlock) {
            this.elementBuffer.bufferElementInnerWhiteSpace(buffer, offset, len, line, col);
        } else {
            this.selectedHandler.handleInnerWhiteSpace(buffer, offset, len, line, col);
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IProcessingInstructionHandler
    public void handleProcessingInstruction(char[] buffer, int targetOffset, int targetLen, int targetLine, int targetCol, int contentOffset, int contentLen, int contentLine, int contentCol, int outerOffset, int outerLen, int line, int col) throws ParseException {
        if (!this.insideAllSelectorMatchingBlock) {
            this.someSelectorsMatch = false;
            for (int i = 0; i < this.selectorsLen; i++) {
                if (this.matchingMarkupLevelsPerSelector[i] > this.markupLevel) {
                    this.selectorMatches[i] = this.selectorFilters[i].matchProcessingInstruction(true, this.markupLevel, this.markupBlocks[this.markupLevel]);
                    if (this.selectorMatches[i]) {
                        this.someSelectorsMatch = true;
                    }
                } else {
                    this.selectorMatches[i] = true;
                    this.someSelectorsMatch = true;
                }
            }
            if (this.someSelectorsMatch) {
                markCurrentSelection();
                this.selectedHandler.handleProcessingInstruction(buffer, targetOffset, targetLen, targetLine, targetCol, contentOffset, contentLen, contentLine, contentCol, outerOffset, outerLen, line, col);
                unmarkCurrentSelection();
                return;
            }
            unmarkCurrentSelection();
            this.nonSelectedHandler.handleProcessingInstruction(buffer, targetOffset, targetLen, targetLine, targetCol, contentOffset, contentLen, contentLine, contentCol, outerOffset, outerLen, line, col);
            return;
        }
        markCurrentSelection();
        this.selectedHandler.handleProcessingInstruction(buffer, targetOffset, targetLen, targetLine, targetCol, contentOffset, contentLen, contentLine, contentCol, outerOffset, outerLen, line, col);
        unmarkCurrentSelection();
    }

    private void markCurrentSelection() {
        this.selection.levels[this.selectionIndex].selection = this.selectorMatches;
    }

    private void unmarkCurrentSelection() {
        this.selection.levels[this.selectionIndex].selection = null;
    }

    private void checkSizeOfMarkupBlocksStructure(int markupLevel) {
        if (markupLevel >= this.markupBlocks.length) {
            int newLen = Math.max(markupLevel + 1, this.markupBlocks.length + 10);
            int[] newMarkupBlocks = new int[newLen];
            Arrays.fill(newMarkupBlocks, 0);
            System.arraycopy(this.markupBlocks, 0, newMarkupBlocks, 0, this.markupBlocks.length);
            this.markupBlocks = newMarkupBlocks;
        }
    }

    private void updateInsideAllSelectorMatchingBlockFlag() {
        for (int i = 0; i < this.selectorsLen; i++) {
            if (!this.selectorMatches[i]) {
                this.insideAllSelectorMatchingBlock = false;
                return;
            }
        }
        this.insideAllSelectorMatchingBlock = true;
    }
}