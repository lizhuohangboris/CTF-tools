package org.attoparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.attoparser.config.ParseConfiguration;
import org.attoparser.util.TextUtil;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/MarkupEventProcessorHandler.class */
public final class MarkupEventProcessorHandler extends AbstractChainedMarkupHandler {
    private static final int DEFAULT_STACK_LEN = 10;
    private static final int DEFAULT_ATTRIBUTE_NAMES_LEN = 3;
    private ParseStatus status;
    private boolean useStack;
    private boolean autoOpen;
    private boolean autoClose;
    private boolean requireBalancedElements;
    private boolean requireNoUnmatchedCloseElements;
    private ParseConfiguration.PrologParseConfiguration prologParseConfiguration;
    private ParseConfiguration.UniqueRootElementPresence uniqueRootElementPresence;
    private boolean caseSensitive;
    private boolean requireWellFormedAttributeValues;
    private boolean requireUniqueAttributesInElement;
    private boolean validateProlog;
    private boolean prologPresenceForbidden;
    private boolean xmlDeclarationPresenceForbidden;
    private boolean doctypePresenceForbidden;
    private StructureNamesRepository structureNamesRepository;
    private char[][] elementStack;
    private int elementStackSize;
    private boolean validPrologXmlDeclarationRead;
    private boolean validPrologDocTypeRead;
    private boolean elementRead;
    private char[] rootElementName;
    private char[][] currentElementAttributeNames;
    private int currentElementAttributeNamesSize;
    private boolean closeElementIsMatched;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MarkupEventProcessorHandler(IMarkupHandler handler) {
        super(handler);
        this.validPrologXmlDeclarationRead = false;
        this.validPrologDocTypeRead = false;
        this.elementRead = false;
        this.rootElementName = null;
        this.currentElementAttributeNames = null;
        this.currentElementAttributeNamesSize = 0;
        this.closeElementIsMatched = true;
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IMarkupHandler
    public void setParseStatus(ParseStatus status) {
        this.status = status;
        super.setParseStatus(status);
    }

    /* JADX WARN: Type inference failed for: r1v46, types: [char[], char[][]] */
    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IMarkupHandler
    public void setParseConfiguration(ParseConfiguration parseConfiguration) {
        this.caseSensitive = parseConfiguration.isCaseSensitive();
        this.useStack = ParseConfiguration.ElementBalancing.NO_BALANCING != parseConfiguration.getElementBalancing() || parseConfiguration.isUniqueAttributesInElementRequired() || parseConfiguration.isNoUnmatchedCloseElementsRequired() || ParseConfiguration.UniqueRootElementPresence.NOT_VALIDATED != parseConfiguration.getUniqueRootElementPresence();
        this.autoOpen = ParseConfiguration.ElementBalancing.AUTO_OPEN_CLOSE == parseConfiguration.getElementBalancing();
        this.autoClose = ParseConfiguration.ElementBalancing.AUTO_OPEN_CLOSE == parseConfiguration.getElementBalancing() || ParseConfiguration.ElementBalancing.AUTO_CLOSE == parseConfiguration.getElementBalancing();
        this.requireBalancedElements = ParseConfiguration.ElementBalancing.REQUIRE_BALANCED == parseConfiguration.getElementBalancing();
        this.requireNoUnmatchedCloseElements = this.requireBalancedElements || parseConfiguration.isNoUnmatchedCloseElementsRequired();
        this.prologParseConfiguration = parseConfiguration.getPrologParseConfiguration();
        this.prologParseConfiguration.validateConfiguration();
        this.uniqueRootElementPresence = parseConfiguration.getUniqueRootElementPresence();
        this.requireWellFormedAttributeValues = parseConfiguration.isXmlWellFormedAttributeValuesRequired();
        this.requireUniqueAttributesInElement = parseConfiguration.isUniqueAttributesInElementRequired();
        this.validateProlog = this.prologParseConfiguration.isValidateProlog();
        this.prologPresenceForbidden = this.prologParseConfiguration.getPrologPresence().isForbidden();
        this.xmlDeclarationPresenceForbidden = this.prologParseConfiguration.getXmlDeclarationPresence().isRequired();
        this.doctypePresenceForbidden = this.prologParseConfiguration.getDoctypePresence().isRequired();
        if (this.useStack) {
            this.elementStack = new char[10];
            this.elementStackSize = 0;
            this.structureNamesRepository = new StructureNamesRepository();
        } else {
            this.elementStack = null;
            this.elementStackSize = 0;
            this.structureNamesRepository = null;
        }
        super.setParseConfiguration(parseConfiguration);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IDocumentHandler
    public void handleDocumentEnd(long endTimeNanos, long totalTimeNanos, int line, int col) throws ParseException {
        if (this.requireBalancedElements && this.elementStackSize > 0) {
            char[] popped = popFromStack();
            throw new ParseException("Malformed markup: element \"" + new String(popped, 0, popped.length) + "\" is never closed (no closing tag at the end of document)");
        } else if (!this.elementRead && ((this.validPrologDocTypeRead && this.uniqueRootElementPresence.isDependsOnPrologDoctype()) || this.uniqueRootElementPresence.isRequiredAlways())) {
            throw new ParseException("Malformed markup: no root element present");
        } else {
            if (this.useStack) {
                cleanStack(line, col);
            }
            getNext().handleDocumentEnd(endTimeNanos, totalTimeNanos, line, col);
        }
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IXMLDeclarationHandler
    public void handleXmlDeclaration(char[] buffer, int keywordOffset, int keywordLen, int keywordLine, int keywordCol, int versionOffset, int versionLen, int versionLine, int versionCol, int encodingOffset, int encodingLen, int encodingLine, int encodingCol, int standaloneOffset, int standaloneLen, int standaloneLine, int standaloneCol, int outerOffset, int outerLen, int line, int col) throws ParseException {
        if (this.validateProlog && (this.prologPresenceForbidden || this.xmlDeclarationPresenceForbidden)) {
            throw new ParseException("An XML Declaration has been found, but it wasn't allowed", line, col);
        }
        if (this.validateProlog) {
            if (this.validPrologXmlDeclarationRead) {
                throw new ParseException("Malformed markup: Only one XML Declaration can appear in document", line, col);
            }
            if (this.validPrologDocTypeRead) {
                throw new ParseException("Malformed markup: XML Declaration must appear before DOCTYPE", line, col);
            }
            if (this.elementRead) {
                throw new ParseException("Malformed markup: XML Declaration must appear before any elements in document", line, col);
            }
        }
        if (this.validateProlog) {
            this.validPrologXmlDeclarationRead = true;
        }
        getNext().handleXmlDeclaration(buffer, keywordOffset, keywordLen, keywordLine, keywordCol, versionOffset, versionLen, versionLine, versionCol, encodingOffset, encodingLen, encodingLine, encodingCol, standaloneOffset, standaloneLen, standaloneLine, standaloneCol, outerOffset, outerLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        if (this.useStack) {
            if (this.elementStackSize == 0) {
                checkValidRootElement(buffer, nameOffset, nameLen, line, col);
            }
            if (this.requireUniqueAttributesInElement) {
                this.currentElementAttributeNames = null;
                this.currentElementAttributeNamesSize = 0;
            }
        }
        this.status.autoOpenCloseDone = false;
        this.status.autoOpenParents = null;
        this.status.autoOpenLimits = null;
        this.status.autoCloseRequired = null;
        this.status.autoCloseLimits = null;
        this.status.avoidStacking = true;
        getNext().handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
        if (this.useStack) {
            if (this.status.autoOpenParents != null || this.status.autoCloseRequired != null) {
                if (this.status.autoCloseRequired != null) {
                    autoClose(this.status.autoCloseRequired, this.status.autoCloseLimits, line, col);
                }
                if (this.status.autoOpenParents != null) {
                    autoOpen(this.status.autoOpenParents, this.status.autoOpenLimits, line, col);
                }
                this.status.autoOpenCloseDone = true;
                getNext().handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
            }
            if (!this.status.avoidStacking) {
                pushToStack(buffer, nameOffset, nameLen);
            }
        } else if (this.status.autoOpenParents != null || this.status.autoCloseRequired != null) {
            this.status.autoOpenCloseDone = true;
            getNext().handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
        }
        this.status.autoOpenCloseDone = true;
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementEnd(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        this.elementRead = true;
        getNext().handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        if (this.useStack) {
            if (this.elementStackSize == 0) {
                checkValidRootElement(buffer, nameOffset, nameLen, line, col);
            }
            if (this.requireUniqueAttributesInElement) {
                this.currentElementAttributeNames = null;
                this.currentElementAttributeNamesSize = 0;
            }
        }
        this.status.autoOpenCloseDone = false;
        this.status.autoOpenParents = null;
        this.status.autoOpenLimits = null;
        this.status.autoCloseRequired = null;
        this.status.autoCloseLimits = null;
        this.status.avoidStacking = false;
        getNext().handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
        if (this.useStack) {
            if (this.status.autoOpenParents != null || this.status.autoCloseRequired != null) {
                if (this.status.autoCloseRequired != null) {
                    autoClose(this.status.autoCloseRequired, this.status.autoCloseLimits, line, col);
                }
                if (this.status.autoOpenParents != null) {
                    autoOpen(this.status.autoOpenParents, this.status.autoOpenLimits, line, col);
                }
                this.status.autoOpenCloseDone = true;
                getNext().handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
            }
            if (!this.status.avoidStacking) {
                pushToStack(buffer, nameOffset, nameLen);
            }
        } else if (this.status.autoOpenParents != null || this.status.autoCloseRequired != null) {
            this.status.autoOpenCloseDone = true;
            getNext().handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
        }
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.elementRead = true;
        getNext().handleOpenElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        throw new IllegalStateException("handleAutoOpenElementStart should never be called on MarkupEventProcessor, as these events should originate in this class");
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        throw new IllegalStateException("handleAutoOpenElementEnd should never be called on MarkupEventProcessor, as these events should originate in this class");
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        if (this.useStack) {
            this.closeElementIsMatched = checkStackForElement(buffer, nameOffset, nameLen, line, col);
            if (this.requireUniqueAttributesInElement) {
                this.currentElementAttributeNames = null;
                this.currentElementAttributeNamesSize = 0;
            }
            if (this.closeElementIsMatched) {
                getNext().handleCloseElementStart(buffer, nameOffset, nameLen, line, col);
                return;
            } else {
                getNext().handleUnmatchedCloseElementStart(buffer, nameOffset, nameLen, line, col);
                return;
            }
        }
        getNext().handleCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        this.elementRead = true;
        if (this.useStack && !this.closeElementIsMatched) {
            getNext().handleUnmatchedCloseElementEnd(buffer, nameOffset, nameLen, line, col);
        } else {
            getNext().handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
        }
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        throw new IllegalStateException("handleAutoCloseElementStart should never be called on MarkupEventProcessor, as these events should originate in this class");
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        throw new IllegalStateException("handleAutoCloseElementEnd should never be called on MarkupEventProcessor, as these events should originate in this class");
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        throw new IllegalStateException("handleUnmatchedCloseElementStart should never be called on MarkupEventProcessor, as these events should originate in this class");
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleUnmatchedCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        throw new IllegalStateException("handleUnmatchedCloseElementEnd should never be called on MarkupEventProcessor, as these events should originate in this class");
    }

    /* JADX WARN: Type inference failed for: r0v26, types: [char[], char[][], java.lang.Object] */
    /* JADX WARN: Type inference failed for: r1v22, types: [char[], char[][]] */
    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) throws ParseException {
        if (this.useStack && this.requireUniqueAttributesInElement) {
            if (this.currentElementAttributeNames == null) {
                this.currentElementAttributeNames = new char[3];
            }
            for (int i = 0; i < this.currentElementAttributeNamesSize; i++) {
                if (TextUtil.equals(this.caseSensitive, this.currentElementAttributeNames[i], 0, this.currentElementAttributeNames[i].length, buffer, nameOffset, nameLen)) {
                    throw new ParseException("Malformed markup: Attribute \"" + new String(buffer, nameOffset, nameLen) + "\" appears more than once in element", nameLine, nameCol);
                }
            }
            if (this.currentElementAttributeNamesSize == this.currentElementAttributeNames.length) {
                ?? r0 = new char[this.currentElementAttributeNames.length + 3];
                System.arraycopy(this.currentElementAttributeNames, 0, r0, 0, this.currentElementAttributeNames.length);
                this.currentElementAttributeNames = r0;
            }
            this.currentElementAttributeNames[this.currentElementAttributeNamesSize] = this.structureNamesRepository.getStructureName(buffer, nameOffset, nameLen);
            this.currentElementAttributeNamesSize++;
        }
        if (this.requireWellFormedAttributeValues) {
            if (operatorLen == 0) {
                throw new ParseException("Malformed markup: Attribute \"" + new String(buffer, nameOffset, nameLen) + "\" must include an equals (=) sign and a value surrounded by quotes", operatorLine, operatorCol);
            }
            if (valueOuterLen == 0 || valueOuterLen == valueContentLen) {
                throw new ParseException("Malformed markup: Value for attribute \"" + new String(buffer, nameOffset, nameLen) + "\" must be surrounded by quotes", valueLine, valueCol);
            }
        }
        getNext().handleAttribute(buffer, nameOffset, nameLen, nameLine, nameCol, operatorOffset, operatorLen, operatorLine, operatorCol, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IDocTypeHandler
    public void handleDocType(char[] buffer, int keywordOffset, int keywordLen, int keywordLine, int keywordCol, int elementNameOffset, int elementNameLen, int elementNameLine, int elementNameCol, int typeOffset, int typeLen, int typeLine, int typeCol, int publicIdOffset, int publicIdLen, int publicIdLine, int publicIdCol, int systemIdOffset, int systemIdLen, int systemIdLine, int systemIdCol, int internalSubsetOffset, int internalSubsetLen, int internalSubsetLine, int internalSubsetCol, int outerOffset, int outerLen, int outerLine, int outerCol) throws ParseException {
        if (this.validateProlog) {
            if (this.prologPresenceForbidden || this.doctypePresenceForbidden) {
                throw new ParseException("A DOCTYPE clause has been found, but it wasn't allowed", outerLine, outerCol);
            }
            if (this.validPrologDocTypeRead) {
                throw new ParseException("Malformed markup: Only one DOCTYPE clause can appear in document", outerLine, outerCol);
            }
            if (this.elementRead) {
                throw new ParseException("Malformed markup: DOCTYPE must appear before any elements in document", outerLine, outerCol);
            }
            if (this.prologParseConfiguration.isRequireDoctypeKeywordsUpperCase()) {
                if (keywordLen > 0) {
                    int maxi = keywordOffset + keywordLen;
                    for (int i = keywordOffset; i < maxi; i++) {
                        if (Character.isLowerCase(buffer[i])) {
                            throw new ParseException("Malformed markup: DOCTYPE requires upper-case keywords (\"" + new String(buffer, keywordOffset, keywordLen) + "\" was found)", outerLine, outerCol);
                        }
                    }
                }
                if (typeLen > 0) {
                    int maxi2 = typeOffset + typeLen;
                    for (int i2 = typeOffset; i2 < maxi2; i2++) {
                        if (Character.isLowerCase(buffer[i2])) {
                            throw new ParseException("Malformed markup: DOCTYPE requires upper-case keywords (\"" + new String(buffer, typeOffset, typeLen) + "\" was found)", outerLine, outerCol);
                        }
                    }
                }
            }
        }
        if (this.useStack) {
            this.rootElementName = this.structureNamesRepository.getStructureName(buffer, elementNameOffset, elementNameLen);
        }
        if (this.validateProlog) {
            this.validPrologDocTypeRead = true;
        }
        getNext().handleDocType(buffer, keywordOffset, keywordLen, keywordLine, keywordCol, elementNameOffset, elementNameLen, elementNameLine, elementNameCol, typeOffset, typeLen, typeLine, typeCol, publicIdOffset, publicIdLen, publicIdLine, publicIdCol, systemIdOffset, systemIdLen, systemIdLine, systemIdCol, internalSubsetOffset, internalSubsetLen, internalSubsetLine, internalSubsetCol, outerOffset, outerLen, outerLine, outerCol);
    }

    private void checkValidRootElement(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        if (!this.validateProlog) {
            if (this.elementRead && this.uniqueRootElementPresence.isRequiredAlways()) {
                throw new ParseException("Malformed markup: Only one root element is allowed", line, col);
            }
        } else if (this.validPrologDocTypeRead) {
            if (this.elementRead) {
                throw new ParseException("Malformed markup: Only one root element (with name \"" + new String(this.rootElementName) + "\" is allowed", line, col);
            }
            if (!TextUtil.equals(this.caseSensitive, this.rootElementName, 0, this.rootElementName.length, buffer, offset, len)) {
                throw new ParseException("Malformed markup: Root element should be \"" + new String(this.rootElementName) + "\", but \"" + new String(buffer, offset, len) + "\" has been found", line, col);
            }
        }
    }

    private boolean checkStackForElement(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        int peekDelta = 0;
        char[] peekFromStack = peekFromStack(0);
        while (true) {
            char[] peek = peekFromStack;
            if (peek != null) {
                if (TextUtil.equals(this.caseSensitive, peek, 0, peek.length, buffer, offset, len)) {
                    for (int i = 0; i < peekDelta; i++) {
                        char[] peek2 = popFromStack();
                        if (this.autoClose) {
                            getNext().handleAutoCloseElementStart(peek2, 0, peek2.length, line, col);
                            getNext().handleAutoCloseElementEnd(peek2, 0, peek2.length, line, col);
                        } else {
                            throw new ParseException("Malformed markup: element \"" + new String(peek2, 0, peek2.length) + "\" is never closed", line, col);
                        }
                    }
                    popFromStack();
                    return true;
                } else if (this.requireBalancedElements) {
                    throw new ParseException("Malformed markup: element \"" + new String(peek, 0, peek.length) + "\" is never closed", line, col);
                } else {
                    peekDelta++;
                    peekFromStack = peekFromStack(peekDelta);
                }
            } else if (this.requireNoUnmatchedCloseElements) {
                throw new ParseException("Malformed markup: closing element \"" + new String(buffer, offset, len) + "\" is never open", line, col);
            } else {
                return false;
            }
        }
    }

    private void cleanStack(int line, int col) throws ParseException {
        if (this.elementStackSize > 0) {
            char[] popFromStack = popFromStack();
            while (true) {
                char[] popped = popFromStack;
                if (popped != null) {
                    if (this.autoClose) {
                        getNext().handleAutoCloseElementStart(popped, 0, popped.length, line, col);
                        getNext().handleAutoCloseElementEnd(popped, 0, popped.length, line, col);
                        popFromStack = popFromStack();
                    } else {
                        throw new ParseException("Malformed markup: element \"" + new String(popped, 0, popped.length) + "\" is never closed", line, col);
                    }
                } else {
                    return;
                }
            }
        }
    }

    private void autoClose(char[][] autoCloseElements, char[][] autoCloseLimits, int line, int col) throws ParseException {
        int peekDelta = 0;
        int unstackCount = 0;
        char[] peek = peekFromStack(0);
        while (peek != null) {
            if (autoCloseLimits != null) {
                int i = 0;
                int n = autoCloseLimits.length;
                while (true) {
                    int i2 = n;
                    n--;
                    if (i2 == 0) {
                        break;
                    } else if (TextUtil.equals(this.caseSensitive, autoCloseLimits[i], peek)) {
                        peek = null;
                        break;
                    } else {
                        i++;
                    }
                }
            }
            if (peek != null) {
                int i3 = 0;
                int n2 = autoCloseElements.length;
                while (true) {
                    int i4 = n2;
                    n2--;
                    if (i4 == 0) {
                        break;
                    } else if (TextUtil.equals(this.caseSensitive, autoCloseElements[i3], peek)) {
                        unstackCount = peekDelta + 1;
                        break;
                    } else {
                        i3++;
                    }
                }
                peekDelta++;
                peek = peekFromStack(peekDelta);
            }
        }
        int n3 = unstackCount;
        while (true) {
            int i5 = n3;
            n3--;
            if (i5 != 0) {
                char[] peek2 = popFromStack();
                if (this.requireBalancedElements) {
                    throw new ParseException("Malformed markup: element \"" + new String(peek2, 0, peek2.length) + "\" is not closed where it should be", line, col);
                }
                if (this.autoClose) {
                    getNext().handleAutoCloseElementStart(peek2, 0, peek2.length, line, col);
                    getNext().handleAutoCloseElementEnd(peek2, 0, peek2.length, line, col);
                }
            } else {
                return;
            }
        }
    }

    private void autoOpen(char[][] autoOpenParents, char[][] autoOpenLimits, int line, int col) throws ParseException {
        if (!this.autoOpen) {
            return;
        }
        int parentInsertCount = 0;
        if (autoOpenLimits == null) {
            if (this.elementStackSize >= autoOpenParents.length) {
                return;
            }
            char[] peek = peekFromStack(0);
            if (peek == null) {
                parentInsertCount = autoOpenParents.length;
            } else {
                int n = autoOpenParents.length;
                while (true) {
                    if (peek == null) {
                        break;
                    }
                    int i = n;
                    n--;
                    if (i != 0) {
                        if (TextUtil.equals(this.caseSensitive, autoOpenParents[n], peek)) {
                            parentInsertCount = (autoOpenParents.length - n) - 1;
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            if (parentInsertCount == 0) {
                return;
            }
        } else {
            char[] peek2 = peekFromStack(0);
            if (peek2 != null) {
                int i2 = 0;
                int n2 = autoOpenLimits.length;
                while (true) {
                    int i3 = n2;
                    n2--;
                    if (i3 == 0) {
                        break;
                    } else if (TextUtil.equals(this.caseSensitive, autoOpenLimits[i2], peek2)) {
                        return;
                    } else {
                        i2++;
                    }
                }
            }
            parentInsertCount = autoOpenParents.length;
        }
        int n3 = parentInsertCount;
        int i4 = autoOpenParents.length - parentInsertCount;
        while (true) {
            int i5 = n3;
            n3--;
            if (i5 != 0) {
                getNext().handleAutoOpenElementStart(autoOpenParents[i4], 0, autoOpenParents[i4].length, line, col);
                getNext().handleAutoOpenElementEnd(autoOpenParents[i4], 0, autoOpenParents[i4].length, line, col);
                pushToStack(autoOpenParents[i4], 0, autoOpenParents[i4].length);
                i4++;
            } else {
                return;
            }
        }
    }

    private void pushToStack(char[] buffer, int offset, int len) {
        if (this.elementStackSize == this.elementStack.length) {
            growStack();
        }
        this.elementStack[this.elementStackSize] = this.structureNamesRepository.getStructureName(buffer, offset, len);
        this.elementStackSize++;
    }

    private char[] peekFromStack(int delta) {
        if (this.elementStackSize <= delta) {
            return null;
        }
        return this.elementStack[(this.elementStackSize - 1) - delta];
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
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/MarkupEventProcessorHandler$StructureNamesRepository.class */
    public static final class StructureNamesRepository {
        private static final int REPOSITORY_INITIAL_LEN = 100;
        private static final int REPOSITORY_INITIAL_INC = 20;
        private char[][] repository = new char[100];
        private int repositorySize = 0;

        /* JADX WARN: Type inference failed for: r1v1, types: [char[], char[][]] */
        StructureNamesRepository() {
        }

        char[] getStructureName(char[] text, int offset, int len) {
            int index = TextUtil.binarySearch(true, this.repository, 0, this.repositorySize, text, offset, len);
            if (index >= 0) {
                return this.repository[index];
            }
            return storeStructureName(index, text, offset, len);
        }

        /* JADX WARN: Type inference failed for: r0v17, types: [char[], char[][], java.lang.Object[], java.lang.Object] */
        private char[] storeStructureName(int index, char[] text, int offset, int len) {
            if (this.repositorySize == this.repository.length) {
                ?? r0 = new char[this.repository.length + 20];
                Arrays.fill((Object[]) r0, (Object) null);
                System.arraycopy(this.repository, 0, r0, 0, this.repositorySize);
                this.repository = r0;
            }
            int insertionIndex = (index + 1) * (-1);
            char[] structureName = StandardNamesRepository.getStructureName(text, offset, len);
            System.arraycopy(this.repository, insertionIndex, this.repository, insertionIndex + 1, this.repositorySize - insertionIndex);
            this.repository[insertionIndex] = structureName;
            this.repositorySize++;
            return structureName;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/MarkupEventProcessorHandler$StandardNamesRepository.class */
    public static final class StandardNamesRepository {
        private static final char[][] REPOSITORY;

        /* JADX WARN: Type inference failed for: r0v16, types: [char[], char[][]] */
        static {
            List<String> names = new ArrayList<>(150);
            names.addAll(HtmlNames.ALL_STANDARD_ELEMENT_NAMES);
            for (String name : HtmlNames.ALL_STANDARD_ELEMENT_NAMES) {
                names.add(name.toUpperCase());
            }
            names.addAll(HtmlNames.ALL_STANDARD_ATTRIBUTE_NAMES);
            for (String name2 : HtmlNames.ALL_STANDARD_ATTRIBUTE_NAMES) {
                names.add(name2.toUpperCase());
            }
            Collections.sort(names);
            REPOSITORY = new char[names.size()];
            for (int i = 0; i < names.size(); i++) {
                String name3 = names.get(i);
                REPOSITORY[i] = name3.toCharArray();
            }
        }

        static char[] getStructureName(char[] text, int offset, int len) {
            int index = TextUtil.binarySearch(true, REPOSITORY, text, offset, len);
            if (index < 0) {
                char[] structureName = new char[len];
                System.arraycopy(text, offset, structureName, 0, len);
                return structureName;
            }
            return REPOSITORY[index];
        }

        private StandardNamesRepository() {
        }
    }
}