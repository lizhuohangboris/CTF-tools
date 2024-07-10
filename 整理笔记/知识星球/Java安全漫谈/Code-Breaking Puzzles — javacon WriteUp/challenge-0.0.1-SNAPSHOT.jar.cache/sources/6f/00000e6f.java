package org.attoparser;

import org.thymeleaf.engine.XMLDeclaration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/ParsingXmlDeclarationMarkupUtil.class */
public final class ParsingXmlDeclarationMarkupUtil {
    private ParsingXmlDeclarationMarkupUtil() {
    }

    public static void parseXmlDeclaration(char[] buffer, int offset, int len, int line, int col, IXMLDeclarationHandler handler) throws ParseException {
        if (len < 7 || !isXmlDeclarationStart(buffer, offset, offset + len) || !isXmlDeclarationEnd(buffer, (offset + len) - 2, offset + len)) {
            throw new ParseException("Could not parse as a well-formed XML Declaration: \"" + new String(buffer, offset, len) + "\"", line, col);
        }
        int internalOffset = offset + 2;
        int internalLen = len - 4;
        int maxi = internalOffset + internalLen;
        int[] locator = {line, col + 2};
        int keywordLine = locator[0];
        int keywordCol = locator[1];
        int keywordEnd = ParsingMarkupUtil.findNextWhitespaceCharWildcard(buffer, internalOffset, maxi, false, locator);
        if (keywordEnd == -1) {
            throw new ParseException("XML Declaration must at least contain a \"version\" attribute: \"" + new String(buffer, offset, len) + "\"", line, col);
        }
        int keywordLen = keywordEnd - internalOffset;
        int contentOffset = ParsingMarkupUtil.findNextNonWhitespaceCharWildcard(buffer, keywordEnd, maxi, locator);
        if (contentOffset == -1) {
            throw new ParseException("XML Declaration must at least contain a \"version\" attribute: \"" + new String(buffer, offset, len) + "\"", line, col);
        }
        int contentLen = maxi - contentOffset;
        XmlDeclarationAttributeProcessor attHandling = new XmlDeclarationAttributeProcessor(offset, len, line, col);
        ParsingAttributeSequenceUtil.parseAttributeSequence(buffer, contentOffset, contentLen, locator[0], locator[1], attHandling);
        ParsingMarkupUtil.findNextStructureEndAvoidQuotes(buffer, contentOffset, maxi, locator);
        attHandling.finalChecks(locator, buffer);
        handler.handleXmlDeclaration(buffer, internalOffset, keywordLen, keywordLine, keywordCol, attHandling.versionOffset, attHandling.versionLen, attHandling.versionLine, attHandling.versionCol, attHandling.encodingOffset, attHandling.encodingLen, attHandling.encodingLine, attHandling.encodingCol, attHandling.standaloneOffset, attHandling.standaloneLen, attHandling.standaloneLine, attHandling.standaloneCol, offset, len, line, col);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isXmlDeclarationStart(char[] buffer, int offset, int maxi) {
        return maxi - offset > 5 && buffer[offset] == '<' && buffer[offset + 1] == '?' && buffer[offset + 2] == 'x' && buffer[offset + 3] == 'm' && buffer[offset + 4] == 'l' && (Character.isWhitespace(buffer[offset + 5]) || (maxi - offset > 6 && buffer[offset + 5] == '?' && buffer[offset + 6] == '>'));
    }

    static boolean isXmlDeclarationEnd(char[] buffer, int offset, int maxi) {
        return maxi - offset > 1 && buffer[offset] == '?' && buffer[offset + 1] == '>';
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/ParsingXmlDeclarationMarkupUtil$XmlDeclarationAttributeProcessor.class */
    private static class XmlDeclarationAttributeProcessor implements IAttributeSequenceHandler {
        private final int outerOffset;
        private final int outerLen;
        private final int outerLine;
        private final int outerCol;
        boolean versionPresent = false;
        int versionOffset = 0;
        int versionLen = 0;
        int versionLine = -1;
        int versionCol = -1;
        boolean encodingPresent = false;
        int encodingOffset = 0;
        int encodingLen = 0;
        int encodingLine = -1;
        int encodingCol = -1;
        boolean standalonePresent = false;
        int standaloneOffset = 0;
        int standaloneLen = 0;
        int standaloneLine = -1;
        int standaloneCol = -1;
        static final char[] VERSION = XMLDeclaration.ATTRIBUTE_NAME_VERSION.toCharArray();
        static final char[] ENCODING = XMLDeclaration.ATTRIBUTE_NAME_ENCODING.toCharArray();
        static final char[] STANDALONE = XMLDeclaration.ATTRIBUTE_NAME_STANDALONE.toCharArray();

        XmlDeclarationAttributeProcessor(int outerOffset, int outerLen, int outerLine, int outerCol) {
            this.outerOffset = outerOffset;
            this.outerLen = outerLen;
            this.outerLine = outerLine;
            this.outerCol = outerCol;
        }

        @Override // org.attoparser.IAttributeSequenceHandler
        public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) throws ParseException {
            if (charArrayEquals(buffer, nameOffset, nameLen, VERSION, 0, VERSION.length)) {
                if (this.versionPresent) {
                    throw new ParseException("XML Declaration can declare only one \"version\" attribute: \"" + new String(buffer, this.outerOffset, this.outerLen) + "\"", this.outerLine, this.outerCol);
                }
                if (this.encodingPresent || this.standalonePresent) {
                    throw new ParseException("XML Declaration must declare \"version\" as its first attribute: \"" + new String(buffer, this.outerOffset, this.outerLen) + "\"", this.outerLine, this.outerCol);
                }
                this.versionOffset = valueContentOffset;
                this.versionLen = valueContentLen;
                this.versionLine = valueLine;
                this.versionCol = valueCol;
                this.versionPresent = true;
            } else if (charArrayEquals(buffer, nameOffset, nameLen, ENCODING, 0, ENCODING.length)) {
                if (this.encodingPresent) {
                    throw new ParseException("XML Declaration can declare only one \"encoding\" attribute: \"" + new String(buffer, this.outerOffset, this.outerLen) + "\"", this.outerLine, this.outerCol);
                }
                if (!this.versionPresent) {
                    throw new ParseException("XML Declaration must declare \"encoding\" after \"version\": \"" + new String(buffer, this.outerOffset, this.outerLen) + "\"", this.outerLine, this.outerCol);
                }
                if (this.standalonePresent) {
                    throw new ParseException("XML Declaration must declare \"encoding\" before \"standalone\": \"" + new String(buffer, this.outerOffset, this.outerLen) + "\"", this.outerLine, this.outerCol);
                }
                this.encodingOffset = valueContentOffset;
                this.encodingLen = valueContentLen;
                this.encodingLine = valueLine;
                this.encodingCol = valueCol;
                this.encodingPresent = true;
            } else if (charArrayEquals(buffer, nameOffset, nameLen, STANDALONE, 0, STANDALONE.length)) {
                if (this.standalonePresent) {
                    throw new ParseException("XML Declaration can declare only one \"standalone\" attribute: \"" + new String(buffer, this.outerOffset, this.outerLen) + "\"", this.outerLine, this.outerCol);
                }
                this.standaloneOffset = valueContentOffset;
                this.standaloneLen = valueContentLen;
                this.standaloneLine = valueLine;
                this.standaloneCol = valueCol;
                this.standalonePresent = true;
            } else {
                throw new ParseException("XML Declaration does not allow attribute with name \"" + new String(buffer, nameOffset, nameLen) + "\". Only \"version\", \"encoding\" and \"standalone\" are allowed (in that order): \"" + new String(buffer, this.outerOffset, this.outerLen) + "\"", this.outerLine, this.outerCol);
            }
        }

        @Override // org.attoparser.IAttributeSequenceHandler
        public void handleInnerWhiteSpace(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        }

        public void finalChecks(int[] locator, char[] buffer) throws ParseException {
            if (!this.versionPresent) {
                throw new ParseException("Attribute \"version\" is required in XML Declaration: \"" + new String(buffer, this.outerOffset, this.outerLen) + "\"", this.outerLine, this.outerLine);
            }
            if (!this.standalonePresent) {
                this.standaloneLine = locator[0];
                this.standaloneCol = locator[1];
            }
            if (!this.encodingPresent) {
                if (!this.standalonePresent) {
                    this.encodingLine = locator[0];
                    this.encodingCol = locator[1];
                    return;
                }
                this.encodingLine = this.standaloneLine;
                this.encodingCol = this.standaloneCol;
            }
        }

        private static boolean charArrayEquals(char[] arr1, int arr1Offset, int arr1Len, char[] arr2, int arr2Offset, int arr2Len) {
            if (arr1Len != arr2Len) {
                return false;
            }
            for (int i = 0; i < arr1Len; i++) {
                if (arr1[arr1Offset + i] != arr2[arr2Offset + i]) {
                    return false;
                }
            }
            return true;
        }
    }
}