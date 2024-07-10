package org.attoparser.select;

import java.util.Arrays;
import org.attoparser.IMarkupHandler;
import org.attoparser.ParseException;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/select/SelectorElementBuffer.class */
public final class SelectorElementBuffer {
    private static final int DEFAULT_ELEMENT_NAME_SIZE = 10;
    private static final int DEFAULT_ATTRIBUTES_SIZE = 8;
    private static final int DEFAULT_ATTRIBUTES_INC = 4;
    private static final int DEFAULT_ATTRIBUTE_BUFFER_SIZE = 40;
    private static final int DEFAULT_INNER_WHITE_SPACE_BUFFER_SIZE = 1;
    boolean standalone = false;
    boolean minimized = false;
    char[] elementName = new char[10];
    int elementNameLen = 0;
    int elementNameLine = 0;
    int elementNameCol = 0;
    int elementEndLine = 0;
    int elementEndCol = 0;
    int attributeCount = 0;
    char[][] attributeBuffers = new char[8];
    int[] attributeNameLens;
    int[] attributeOperatorLens;
    int[] attributeValueContentOffsets;
    int[] attributeValueContentLens;
    int[] attributeValueOuterLens;
    int[] attributeNameLines;
    int[] attributeNameCols;
    int[] attributeOperatorLines;
    int[] attributeOperatorCols;
    int[] attributeValueLines;
    int[] attributeValueCols;
    int elementInnerWhiteSpaceCount;
    char[][] elementInnerWhiteSpaceBuffers;
    int[] elementInnerWhiteSpaceLens;
    int[] elementInnerWhiteSpaceLines;
    int[] elementInnerWhiteSpaceCols;

    /* JADX WARN: Type inference failed for: r1v11, types: [char[], char[][]] */
    /* JADX WARN: Type inference failed for: r1v48, types: [char[], char[][]] */
    public SelectorElementBuffer() {
        Arrays.fill(this.attributeBuffers, (Object) null);
        this.attributeNameLens = new int[8];
        Arrays.fill(this.attributeNameLens, 0);
        this.attributeOperatorLens = new int[8];
        Arrays.fill(this.attributeOperatorLens, 0);
        this.attributeValueContentOffsets = new int[8];
        this.attributeValueContentLens = new int[8];
        Arrays.fill(this.attributeValueContentOffsets, 0);
        Arrays.fill(this.attributeValueContentLens, 0);
        this.attributeValueOuterLens = new int[8];
        Arrays.fill(this.attributeValueOuterLens, 0);
        this.attributeNameLines = new int[8];
        Arrays.fill(this.attributeNameLines, 0);
        this.attributeNameCols = new int[8];
        Arrays.fill(this.attributeNameCols, 0);
        this.attributeOperatorLines = new int[8];
        Arrays.fill(this.attributeOperatorLines, 0);
        this.attributeOperatorCols = new int[8];
        Arrays.fill(this.attributeOperatorCols, 0);
        this.attributeValueLines = new int[8];
        Arrays.fill(this.attributeValueLines, 0);
        this.attributeValueCols = new int[8];
        Arrays.fill(this.attributeValueCols, 0);
        this.elementInnerWhiteSpaceCount = 0;
        this.elementInnerWhiteSpaceBuffers = new char[9];
        Arrays.fill(this.elementInnerWhiteSpaceBuffers, (Object) null);
        this.elementInnerWhiteSpaceLens = new int[9];
        Arrays.fill(this.elementInnerWhiteSpaceLens, 0);
        this.elementInnerWhiteSpaceLines = new int[9];
        Arrays.fill(this.elementInnerWhiteSpaceLines, 0);
        this.elementInnerWhiteSpaceCols = new int[9];
        Arrays.fill(this.elementInnerWhiteSpaceCols, 0);
    }

    public void bufferElementStart(char[] buffer, int offset, int len, int line, int col, boolean standalone, boolean minimized) {
        if (len > this.elementName.length) {
            this.elementName = new char[len];
        }
        System.arraycopy(buffer, offset, this.elementName, 0, len);
        this.elementNameLen = len;
        this.elementNameLine = line;
        this.elementNameCol = col;
        this.elementEndLine = 0;
        this.elementEndCol = 0;
        this.standalone = standalone;
        this.minimized = minimized;
        this.attributeCount = 0;
        this.elementInnerWhiteSpaceCount = 0;
    }

    /* JADX WARN: Type inference failed for: r0v55, types: [char[], char[][], java.lang.Object[], java.lang.Object] */
    public void bufferAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) {
        if (this.attributeCount >= this.attributeBuffers.length) {
            ?? r0 = new char[this.attributeCount + 4];
            Arrays.fill((Object[]) r0, (Object) null);
            System.arraycopy(this.attributeBuffers, 0, r0, 0, this.attributeCount);
            this.attributeBuffers = r0;
            int[] newAttributeNameLens = new int[this.attributeCount + 4];
            Arrays.fill(newAttributeNameLens, 0);
            System.arraycopy(this.attributeNameLens, 0, newAttributeNameLens, 0, this.attributeCount);
            this.attributeNameLens = newAttributeNameLens;
            int[] newAttributeOperatorLens = new int[this.attributeCount + 4];
            Arrays.fill(newAttributeOperatorLens, 0);
            System.arraycopy(this.attributeOperatorLens, 0, newAttributeOperatorLens, 0, this.attributeCount);
            this.attributeOperatorLens = newAttributeOperatorLens;
            int[] newAttributeValueContentOffsets = new int[this.attributeCount + 4];
            int[] newAttributeValueContentLens = new int[this.attributeCount + 4];
            Arrays.fill(newAttributeValueContentOffsets, 0);
            Arrays.fill(newAttributeValueContentLens, 0);
            System.arraycopy(this.attributeValueContentOffsets, 0, newAttributeValueContentOffsets, 0, this.attributeCount);
            System.arraycopy(this.attributeValueContentLens, 0, newAttributeValueContentLens, 0, this.attributeCount);
            this.attributeValueContentOffsets = newAttributeValueContentOffsets;
            this.attributeValueContentLens = newAttributeValueContentLens;
            int[] newAttributeValueOuterLens = new int[this.attributeCount + 4];
            Arrays.fill(newAttributeValueOuterLens, 0);
            System.arraycopy(this.attributeValueOuterLens, 0, newAttributeValueOuterLens, 0, this.attributeCount);
            this.attributeValueOuterLens = newAttributeValueOuterLens;
            int[] newAttributeNameLines = new int[this.attributeCount + 4];
            int[] newAttributeNameCols = new int[this.attributeCount + 4];
            System.arraycopy(this.attributeNameLines, 0, newAttributeNameLines, 0, this.attributeCount);
            System.arraycopy(this.attributeNameCols, 0, newAttributeNameCols, 0, this.attributeCount);
            this.attributeNameLines = newAttributeNameLines;
            this.attributeNameCols = newAttributeNameCols;
            int[] newAttributeOperatorLines = new int[this.attributeCount + 4];
            int[] newAttributeOperatorCols = new int[this.attributeCount + 4];
            System.arraycopy(this.attributeOperatorLines, 0, newAttributeOperatorLines, 0, this.attributeCount);
            System.arraycopy(this.attributeOperatorCols, 0, newAttributeOperatorCols, 0, this.attributeCount);
            this.attributeOperatorLines = newAttributeOperatorLines;
            this.attributeOperatorCols = newAttributeOperatorCols;
            int[] newAttributeValueLines = new int[this.attributeCount + 4];
            int[] newAttributeValueCols = new int[this.attributeCount + 4];
            System.arraycopy(this.attributeValueLines, 0, newAttributeValueLines, 0, this.attributeCount);
            System.arraycopy(this.attributeValueCols, 0, newAttributeValueCols, 0, this.attributeCount);
            this.attributeValueLines = newAttributeValueLines;
            this.attributeValueCols = newAttributeValueCols;
        }
        int requiredLen = nameLen + operatorLen + valueOuterLen;
        if (this.attributeBuffers[this.attributeCount] == null || this.attributeBuffers[this.attributeCount].length < requiredLen) {
            this.attributeBuffers[this.attributeCount] = new char[Math.max(requiredLen, 40)];
        }
        boolean isContinuous = nameOffset + nameLen == operatorOffset && operatorOffset + operatorLen == valueOuterOffset && valueOuterOffset <= valueContentOffset && valueOuterOffset + valueOuterLen >= valueContentOffset + valueContentLen;
        if (isContinuous) {
            System.arraycopy(buffer, nameOffset, this.attributeBuffers[this.attributeCount], 0, requiredLen);
        } else {
            System.arraycopy(buffer, nameOffset, this.attributeBuffers[this.attributeCount], 0, nameLen);
            System.arraycopy(buffer, operatorOffset, this.attributeBuffers[this.attributeCount], nameLen, operatorLen);
            System.arraycopy(buffer, valueOuterOffset, this.attributeBuffers[this.attributeCount], nameLen + operatorLen, valueOuterLen);
        }
        this.attributeNameLens[this.attributeCount] = nameLen;
        this.attributeOperatorLens[this.attributeCount] = operatorLen;
        this.attributeValueContentOffsets[this.attributeCount] = nameLen + operatorLen + (valueContentOffset - valueOuterOffset);
        this.attributeValueContentLens[this.attributeCount] = valueContentLen;
        this.attributeValueOuterLens[this.attributeCount] = valueOuterLen;
        this.attributeNameLines[this.attributeCount] = nameLine;
        this.attributeNameCols[this.attributeCount] = nameCol;
        this.attributeOperatorLines[this.attributeCount] = operatorLine;
        this.attributeOperatorCols[this.attributeCount] = operatorCol;
        this.attributeValueLines[this.attributeCount] = valueLine;
        this.attributeValueCols[this.attributeCount] = valueCol;
        this.attributeCount++;
    }

    public void bufferElementEnd(char[] buffer, int offset, int len, int line, int col) {
        this.elementEndLine = line;
        this.elementEndCol = col;
    }

    /* JADX WARN: Type inference failed for: r0v22, types: [char[], char[][], java.lang.Object[], java.lang.Object] */
    public void bufferElementInnerWhiteSpace(char[] buffer, int offset, int len, int line, int col) {
        if (this.elementInnerWhiteSpaceCount >= this.elementInnerWhiteSpaceBuffers.length) {
            ?? r0 = new char[this.elementInnerWhiteSpaceCount + 4];
            Arrays.fill((Object[]) r0, (Object) null);
            System.arraycopy(this.elementInnerWhiteSpaceBuffers, 0, r0, 0, this.elementInnerWhiteSpaceCount);
            this.elementInnerWhiteSpaceBuffers = r0;
            int[] newElementInnerWhiteSpaceLens = new int[this.elementInnerWhiteSpaceCount + 4];
            System.arraycopy(this.elementInnerWhiteSpaceLens, 0, newElementInnerWhiteSpaceLens, 0, this.elementInnerWhiteSpaceCount);
            this.elementInnerWhiteSpaceLens = newElementInnerWhiteSpaceLens;
            int[] newElementInnerWhiteSpaceLines = new int[this.elementInnerWhiteSpaceCount + 4];
            int[] newElementInnerWhiteSpaceCols = new int[this.elementInnerWhiteSpaceCount + 4];
            System.arraycopy(this.elementInnerWhiteSpaceLines, 0, newElementInnerWhiteSpaceLines, 0, this.elementInnerWhiteSpaceCount);
            System.arraycopy(this.elementInnerWhiteSpaceCols, 0, newElementInnerWhiteSpaceCols, 0, this.elementInnerWhiteSpaceCount);
            this.elementInnerWhiteSpaceLines = newElementInnerWhiteSpaceLines;
            this.elementInnerWhiteSpaceCols = newElementInnerWhiteSpaceCols;
        }
        if (this.elementInnerWhiteSpaceBuffers[this.elementInnerWhiteSpaceCount] == null || this.elementInnerWhiteSpaceBuffers[this.elementInnerWhiteSpaceCount].length < len) {
            this.elementInnerWhiteSpaceBuffers[this.elementInnerWhiteSpaceCount] = new char[Math.max(len, 1)];
        }
        System.arraycopy(buffer, offset, this.elementInnerWhiteSpaceBuffers[this.elementInnerWhiteSpaceCount], 0, len);
        this.elementInnerWhiteSpaceLens[this.elementInnerWhiteSpaceCount] = len;
        this.elementInnerWhiteSpaceLines[this.elementInnerWhiteSpaceCount] = line;
        this.elementInnerWhiteSpaceCols[this.elementInnerWhiteSpaceCount] = col;
        this.elementInnerWhiteSpaceCount++;
    }

    public void flushBuffer(IMarkupHandler handler, boolean autoOpen) throws ParseException {
        if (this.standalone) {
            handler.handleStandaloneElementStart(this.elementName, 0, this.elementNameLen, this.minimized, this.elementNameLine, this.elementNameCol);
        } else if (autoOpen) {
            handler.handleAutoOpenElementStart(this.elementName, 0, this.elementNameLen, this.elementNameLine, this.elementNameCol);
        } else {
            handler.handleOpenElementStart(this.elementName, 0, this.elementNameLen, this.elementNameLine, this.elementNameCol);
        }
        for (int i = 0; i < this.attributeCount; i++) {
            handler.handleInnerWhiteSpace(this.elementInnerWhiteSpaceBuffers[i], 0, this.elementInnerWhiteSpaceLens[i], this.elementInnerWhiteSpaceLines[i], this.elementInnerWhiteSpaceCols[i]);
            handler.handleAttribute(this.attributeBuffers[i], 0, this.attributeNameLens[i], this.attributeNameLines[i], this.attributeNameCols[i], this.attributeNameLens[i], this.attributeOperatorLens[i], this.attributeOperatorLines[i], this.attributeOperatorCols[i], this.attributeValueContentOffsets[i], this.attributeValueContentLens[i], this.attributeNameLens[i] + this.attributeOperatorLens[i], this.attributeValueOuterLens[i], this.attributeValueLines[i], this.attributeValueCols[i]);
        }
        if (this.elementInnerWhiteSpaceCount - this.attributeCount > 0) {
            for (int i2 = this.attributeCount; i2 < this.elementInnerWhiteSpaceCount; i2++) {
                handler.handleInnerWhiteSpace(this.elementInnerWhiteSpaceBuffers[i2], 0, this.elementInnerWhiteSpaceLens[i2], this.elementInnerWhiteSpaceLines[i2], this.elementInnerWhiteSpaceCols[i2]);
            }
        }
        if (this.standalone) {
            handler.handleStandaloneElementEnd(this.elementName, 0, this.elementNameLen, this.minimized, this.elementEndLine, this.elementEndCol);
        } else if (autoOpen) {
            handler.handleAutoOpenElementEnd(this.elementName, 0, this.elementNameLen, this.elementEndLine, this.elementEndCol);
        } else {
            handler.handleOpenElementEnd(this.elementName, 0, this.elementNameLen, this.elementEndLine, this.elementEndCol);
        }
    }
}