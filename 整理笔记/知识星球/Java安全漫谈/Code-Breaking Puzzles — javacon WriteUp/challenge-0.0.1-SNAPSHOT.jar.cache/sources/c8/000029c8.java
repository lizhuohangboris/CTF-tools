package org.thymeleaf.templateparser.text;

import java.util.Arrays;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.TextUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/text/EventProcessorTextHandler.class */
final class EventProcessorTextHandler extends AbstractChainedTextHandler {
    private static final int DEFAULT_STACK_LEN = 10;
    private static final int DEFAULT_ATTRIBUTE_NAMES_LEN = 3;
    private StructureNamesRepository structureNamesRepository;
    private char[][] elementStack;
    private int elementStackSize;
    private char[][] currentElementAttributeNames;
    private int currentElementAttributeNamesSize;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Type inference failed for: r1v4, types: [char[], char[][]] */
    public EventProcessorTextHandler(ITextHandler handler) {
        super(handler);
        this.currentElementAttributeNames = null;
        this.currentElementAttributeNamesSize = 0;
        this.elementStack = new char[10];
        this.elementStackSize = 0;
        this.structureNamesRepository = new StructureNamesRepository();
    }

    @Override // org.thymeleaf.templateparser.text.AbstractChainedTextHandler, org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleDocumentEnd(long endTimeNanos, long totalTimeNanos, int line, int col) throws TextParseException {
        if (this.elementStackSize > 0) {
            char[] popped = popFromStack();
            throw new TextParseException("Malformed template: element \"" + new String(popped, 0, popped.length) + "\" is never closed (no closing tag at the end of document)");
        } else {
            super.handleDocumentEnd(endTimeNanos, totalTimeNanos, line, col);
        }
    }

    @Override // org.thymeleaf.templateparser.text.AbstractChainedTextHandler, org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws TextParseException {
        this.currentElementAttributeNames = null;
        this.currentElementAttributeNamesSize = 0;
        super.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
    }

    @Override // org.thymeleaf.templateparser.text.AbstractChainedTextHandler, org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws TextParseException {
        this.currentElementAttributeNames = null;
        this.currentElementAttributeNamesSize = 0;
        super.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
        pushToStack(buffer, nameOffset, nameLen);
    }

    @Override // org.thymeleaf.templateparser.text.AbstractChainedTextHandler, org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws TextParseException {
        if (!checkStackForElement(buffer, nameOffset, nameLen, line, col)) {
            throw new TextParseException("Malformed text: element \"" + new String(buffer, nameOffset, nameLen) + "\" is never closed", line, col);
        }
        this.currentElementAttributeNames = null;
        this.currentElementAttributeNamesSize = 0;
        super.handleCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    /* JADX WARN: Type inference failed for: r0v14, types: [char[], char[][], java.lang.Object] */
    /* JADX WARN: Type inference failed for: r1v19, types: [char[], char[][]] */
    @Override // org.thymeleaf.templateparser.text.AbstractChainedTextHandler, org.thymeleaf.templateparser.text.AbstractTextHandler, org.thymeleaf.templateparser.text.ITextHandler
    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) throws TextParseException {
        if (this.currentElementAttributeNames == null) {
            this.currentElementAttributeNames = new char[3];
        }
        for (int i = 0; i < this.currentElementAttributeNamesSize; i++) {
            if (TextUtils.equals(TemplateMode.TEXT.isCaseSensitive(), this.currentElementAttributeNames[i], 0, this.currentElementAttributeNames[i].length, buffer, nameOffset, nameLen)) {
                throw new TextParseException("Malformed text: Attribute \"" + new String(buffer, nameOffset, nameLen) + "\" appears more than once in element", nameLine, nameCol);
            }
        }
        if (this.currentElementAttributeNamesSize == this.currentElementAttributeNames.length) {
            ?? r0 = new char[this.currentElementAttributeNames.length + 3];
            System.arraycopy(this.currentElementAttributeNames, 0, r0, 0, this.currentElementAttributeNames.length);
            this.currentElementAttributeNames = r0;
        }
        this.currentElementAttributeNames[this.currentElementAttributeNamesSize] = this.structureNamesRepository.getStructureName(buffer, nameOffset, nameLen);
        this.currentElementAttributeNamesSize++;
        super.handleAttribute(buffer, nameOffset, nameLen, nameLine, nameCol, operatorOffset, operatorLen, operatorLine, operatorCol, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);
    }

    private boolean checkStackForElement(char[] buffer, int offset, int len, int line, int col) throws TextParseException {
        char[] peek = peekFromStack();
        if (peek != null) {
            if (TextUtils.equals(TemplateMode.TEXT.isCaseSensitive(), peek, 0, peek.length, buffer, offset, len)) {
                popFromStack();
                return true;
            }
            throw new TextParseException("Malformed template: " + (peek.length > 0 ? "element \"" + new String(peek, 0, peek.length) + "\"" : "unnamed element") + " is never closed", line, col);
        }
        throw new TextParseException("Malformed template: unnamed closing element is never opened", line, col);
    }

    private void pushToStack(char[] buffer, int offset, int len) {
        if (this.elementStackSize == this.elementStack.length) {
            growStack();
        }
        this.elementStack[this.elementStackSize] = this.structureNamesRepository.getStructureName(buffer, offset, len);
        this.elementStackSize++;
    }

    private char[] peekFromStack() {
        if (this.elementStackSize == 0) {
            return null;
        }
        return this.elementStack[this.elementStackSize - 1];
    }

    private char[] popFromStack() {
        if (this.elementStackSize == 0) {
            return null;
        }
        char[] popped = this.elementStack[this.elementStackSize - 1];
        this.elementStack[this.elementStackSize - 1] = null;
        this.elementStackSize--;
        return popped;
    }

    /* JADX WARN: Type inference failed for: r0v5, types: [char[], char[][], java.lang.Object] */
    private void growStack() {
        int newStackLen = this.elementStack.length + 10;
        ?? r0 = new char[newStackLen];
        System.arraycopy(this.elementStack, 0, r0, 0, this.elementStack.length);
        this.elementStack = r0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/text/EventProcessorTextHandler$StructureNamesRepository.class */
    public static final class StructureNamesRepository {
        private static final int REPOSITORY_INITIAL_LEN = 20;
        private static final int REPOSITORY_INITIAL_INC = 5;
        private char[][] repository = new char[20];
        private int repositorySize = 0;

        /* JADX WARN: Type inference failed for: r1v1, types: [char[], char[][]] */
        StructureNamesRepository() {
        }

        char[] getStructureName(char[] text, int offset, int len) {
            int index = TextUtils.binarySearch(true, this.repository, 0, this.repositorySize, text, offset, len);
            if (index >= 0) {
                return this.repository[index];
            }
            return storeStructureName(index, text, offset, len);
        }

        /* JADX WARN: Type inference failed for: r0v18, types: [char[], char[][], java.lang.Object[], java.lang.Object] */
        private char[] storeStructureName(int index, char[] text, int offset, int len) {
            if (this.repositorySize == this.repository.length) {
                ?? r0 = new char[this.repository.length + 5];
                Arrays.fill((Object[]) r0, (Object) null);
                System.arraycopy(this.repository, 0, r0, 0, this.repositorySize);
                this.repository = r0;
            }
            int insertionIndex = (index + 1) * (-1);
            char[] structureName = new char[len];
            System.arraycopy(text, offset, structureName, 0, len);
            System.arraycopy(this.repository, insertionIndex, this.repository, insertionIndex + 1, this.repositorySize - insertionIndex);
            this.repository[insertionIndex] = structureName;
            this.repositorySize++;
            return structureName;
        }
    }
}