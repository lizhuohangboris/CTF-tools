package org.attoparser.select;

import java.util.Arrays;
import java.util.List;
import org.attoparser.AbstractMarkupHandler;
import org.attoparser.IMarkupHandler;
import org.attoparser.ParseException;
import org.attoparser.ParseStatus;
import org.attoparser.config.ParseConfiguration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/select/NodeSelectorMarkupHandler.class */
public final class NodeSelectorMarkupHandler extends AbstractMarkupHandler {
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
    private final int[][] matchingMarkupLevelsPerSelector;
    private boolean someSelectorsMatch;
    private int markupLevel;
    private static final int MARKUP_BLOCKS_LEN = 10;
    private int[] markupBlocks;
    private int markupBlockIndex;

    public NodeSelectorMarkupHandler(IMarkupHandler selectedHandler, IMarkupHandler nonSelectedHandler, String selector) {
        this(selectedHandler, nonSelectedHandler, new String[]{selector}, (IMarkupSelectorReferenceResolver) null);
    }

    public NodeSelectorMarkupHandler(IMarkupHandler selectedHandler, IMarkupHandler nonSelectedHandler, String selector, IMarkupSelectorReferenceResolver referenceResolver) {
        this(selectedHandler, nonSelectedHandler, new String[]{selector}, referenceResolver);
    }

    public NodeSelectorMarkupHandler(IMarkupHandler selectedHandler, IMarkupHandler nonSelectedHandler, String[] selectors) {
        this(selectedHandler, nonSelectedHandler, selectors, (IMarkupSelectorReferenceResolver) null);
    }

    /* JADX WARN: Type inference failed for: r1v23, types: [int[], int[][]] */
    public NodeSelectorMarkupHandler(IMarkupHandler selectedHandler, IMarkupHandler nonSelectedHandler, String[] selectors, IMarkupSelectorReferenceResolver referenceResolver) {
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
        this.selectorFilters = new MarkupSelectorFilter[this.selectorsLen];
        this.elementBuffer = new SelectorElementBuffer();
        this.matchingMarkupLevelsPerSelector = new int[this.selectorsLen];
        Arrays.fill(this.matchingMarkupLevelsPerSelector, (Object) null);
        this.markupLevel = 0;
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
        boolean html = ParseConfiguration.ParsingMode.HTML.equals(parseConfiguration.getMode());
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
        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {
            this.selectorMatches[i] = this.selectorFilters[i].matchXmlDeclaration(false, this.markupLevel, this.markupBlocks[this.markupLevel]);
            if (this.selectorMatches[i]) {
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
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IDocTypeHandler
    public void handleDocType(char[] buffer, int keywordOffset, int keywordLen, int keywordLine, int keywordCol, int elementNameOffset, int elementNameLen, int elementNameLine, int elementNameCol, int typeOffset, int typeLen, int typeLine, int typeCol, int publicIdOffset, int publicIdLen, int publicIdLine, int publicIdCol, int systemIdOffset, int systemIdLen, int systemIdLine, int systemIdCol, int internalSubsetOffset, int internalSubsetLen, int internalSubsetLine, int internalSubsetCol, int outerOffset, int outerLen, int outerLine, int outerCol) throws ParseException {
        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {
            this.selectorMatches[i] = this.selectorFilters[i].matchDocTypeClause(false, this.markupLevel, this.markupBlocks[this.markupLevel]);
            if (this.selectorMatches[i]) {
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
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ICDATASectionHandler
    public void handleCDATASection(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col) throws ParseException {
        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {
            this.selectorMatches[i] = this.selectorFilters[i].matchCDATASection(false, this.markupLevel, this.markupBlocks[this.markupLevel]);
            if (this.selectorMatches[i]) {
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
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ITextHandler
    public void handleText(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {
            this.selectorMatches[i] = this.selectorFilters[i].matchText(false, this.markupLevel, this.markupBlocks[this.markupLevel]);
            if (this.selectorMatches[i]) {
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
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.ICommentHandler
    public void handleComment(char[] buffer, int contentOffset, int contentLen, int outerOffset, int outerLen, int line, int col) throws ParseException {
        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {
            this.selectorMatches[i] = this.selectorFilters[i].matchComment(false, this.markupLevel, this.markupBlocks[this.markupLevel]);
            if (this.selectorMatches[i]) {
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
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) throws ParseException {
        this.elementBuffer.bufferAttribute(buffer, nameOffset, nameLen, nameLine, nameCol, operatorOffset, operatorLen, operatorLine, operatorCol, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        this.elementBuffer.bufferElementStart(buffer, nameOffset, nameLen, line, col, true, minimized);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementEnd(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        this.elementBuffer.bufferElementEnd(buffer, nameOffset, nameLen, line, col);
        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {
            this.selectorMatches[i] = this.selectorFilters[i].matchStandaloneElement(false, this.markupLevel, this.markupBlocks[this.markupLevel], this.elementBuffer);
            if (this.selectorMatches[i]) {
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
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.elementBuffer.bufferElementStart(buffer, nameOffset, nameLen, line, col, false, false);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.elementBuffer.bufferElementEnd(buffer, nameOffset, nameLen, line, col);
        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {
            this.selectorMatches[i] = this.selectorFilters[i].matchOpenElement(false, this.markupLevel, this.markupBlocks[this.markupLevel], this.elementBuffer);
            if (this.selectorMatches[i]) {
                this.someSelectorsMatch = true;
                addMatchingMarkupLevel(i, this.markupLevel);
            }
        }
        this.markupLevel++;
        checkSizeOfMarkupBlocksStructure(this.markupLevel);
        int[] iArr = this.markupBlocks;
        int i2 = this.markupLevel;
        int i3 = this.markupBlockIndex + 1;
        this.markupBlockIndex = i3;
        iArr[i2] = i3;
        if (this.someSelectorsMatch) {
            markCurrentSelection();
            this.elementBuffer.flushBuffer(this.selectedHandler, false);
            unmarkCurrentSelection();
            return;
        }
        unmarkCurrentSelection();
        this.elementBuffer.flushBuffer(this.nonSelectedHandler, false);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.elementBuffer.bufferElementStart(buffer, nameOffset, nameLen, line, col, false, false);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.elementBuffer.bufferElementEnd(buffer, nameOffset, nameLen, line, col);
        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {
            this.selectorMatches[i] = this.selectorFilters[i].matchOpenElement(false, this.markupLevel, this.markupBlocks[this.markupLevel], this.elementBuffer);
            if (this.selectorMatches[i]) {
                this.someSelectorsMatch = true;
                addMatchingMarkupLevel(i, this.markupLevel);
            }
        }
        this.markupLevel++;
        checkSizeOfMarkupBlocksStructure(this.markupLevel);
        int[] iArr = this.markupBlocks;
        int i2 = this.markupLevel;
        int i3 = this.markupBlockIndex + 1;
        this.markupBlockIndex = i3;
        iArr[i2] = i3;
        if (this.someSelectorsMatch) {
            markCurrentSelection();
            this.elementBuffer.flushBuffer(this.selectedHandler, true);
            unmarkCurrentSelection();
            return;
        }
        unmarkCurrentSelection();
        this.elementBuffer.flushBuffer(this.nonSelectedHandler, true);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.markupLevel--;
        for (int i = 0; i < this.selectorsLen; i++) {
            this.selectorFilters[i].removeMatchesForLevel(this.markupLevel);
        }
        this.someSelectorsMatch = false;
        for (int i2 = 0; i2 < this.selectorsLen; i2++) {
            this.selectorMatches[i2] = isMatchingMarkupLevel(i2, this.markupLevel);
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
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {
            this.selectorMatches[i] = isMatchingMarkupLevel(i, this.markupLevel);
            if (this.selectorMatches[i]) {
                this.someSelectorsMatch = true;
                removeMatchingMarkupLevel(i, this.markupLevel);
            }
        }
        if (this.someSelectorsMatch) {
            this.selectedHandler.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
            unmarkCurrentSelection();
            return;
        }
        unmarkCurrentSelection();
        this.nonSelectedHandler.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.markupLevel--;
        for (int i = 0; i < this.selectorsLen; i++) {
            this.selectorFilters[i].removeMatchesForLevel(this.markupLevel);
        }
        this.someSelectorsMatch = false;
        for (int i2 = 0; i2 < this.selectorsLen; i2++) {
            this.selectorMatches[i2] = isMatchingMarkupLevel(i2, this.markupLevel);
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
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {
            this.selectorMatches[i] = isMatchingMarkupLevel(i, this.markupLevel);
            if (this.selectorMatches[i]) {
                this.someSelectorsMatch = true;
                removeMatchingMarkupLevel(i, this.markupLevel);
            }
        }
        if (this.someSelectorsMatch) {
            this.selectedHandler.handleAutoCloseElementEnd(buffer, nameOffset, nameLen, line, col);
            unmarkCurrentSelection();
            return;
        }
        unmarkCurrentSelection();
        this.nonSelectedHandler.handleAutoCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {
            this.selectorMatches[i] = isMatchingMarkupLevel(i, this.markupLevel);
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
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {
            this.selectorMatches[i] = isMatchingMarkupLevel(i, this.markupLevel);
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
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleInnerWhiteSpace(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        this.elementBuffer.bufferElementInnerWhiteSpace(buffer, offset, len, line, col);
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IProcessingInstructionHandler
    public void handleProcessingInstruction(char[] buffer, int targetOffset, int targetLen, int targetLine, int targetCol, int contentOffset, int contentLen, int contentLine, int contentCol, int outerOffset, int outerLen, int line, int col) throws ParseException {
        this.someSelectorsMatch = false;
        for (int i = 0; i < this.selectorsLen; i++) {
            this.selectorMatches[i] = this.selectorFilters[i].matchProcessingInstruction(false, this.markupLevel, this.markupBlocks[this.markupLevel]);
            if (this.selectorMatches[i]) {
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

    private void addMatchingMarkupLevel(int selector, int markupLevel) {
        if (this.matchingMarkupLevelsPerSelector[selector] == null) {
            this.matchingMarkupLevelsPerSelector[selector] = new int[2];
            Arrays.fill(this.matchingMarkupLevelsPerSelector[selector], Integer.MAX_VALUE);
        }
        for (int i = 0; i < this.matchingMarkupLevelsPerSelector[selector].length; i++) {
            if (this.matchingMarkupLevelsPerSelector[selector][i] == Integer.MAX_VALUE) {
                this.matchingMarkupLevelsPerSelector[selector][i] = markupLevel;
                return;
            }
        }
        int[] newMatchingMarkupLevelsPerSelector = new int[this.matchingMarkupLevelsPerSelector[selector].length + 2];
        Arrays.fill(newMatchingMarkupLevelsPerSelector, Integer.MAX_VALUE);
        System.arraycopy(this.matchingMarkupLevelsPerSelector[selector], 0, newMatchingMarkupLevelsPerSelector, 0, this.matchingMarkupLevelsPerSelector[selector].length);
        newMatchingMarkupLevelsPerSelector[this.matchingMarkupLevelsPerSelector[selector].length] = markupLevel;
        this.matchingMarkupLevelsPerSelector[selector] = newMatchingMarkupLevelsPerSelector;
    }

    private boolean isMatchingMarkupLevel(int selector, int markupLevel) {
        if (this.matchingMarkupLevelsPerSelector[selector] == null) {
            return false;
        }
        for (int i = 0; i < this.matchingMarkupLevelsPerSelector[selector].length; i++) {
            if (this.matchingMarkupLevelsPerSelector[selector][i] == markupLevel) {
                return true;
            }
        }
        return false;
    }

    private void removeMatchingMarkupLevel(int selector, int markupLevel) {
        for (int i = 0; i < this.matchingMarkupLevelsPerSelector[selector].length; i++) {
            if (this.matchingMarkupLevelsPerSelector[selector][i] == markupLevel) {
                this.matchingMarkupLevelsPerSelector[selector][i] = Integer.MAX_VALUE;
                return;
            }
        }
    }
}