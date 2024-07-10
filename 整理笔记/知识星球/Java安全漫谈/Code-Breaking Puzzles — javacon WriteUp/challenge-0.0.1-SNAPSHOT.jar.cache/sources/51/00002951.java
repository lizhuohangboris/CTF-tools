package org.thymeleaf.standard.inline;

import java.util.Arrays;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.engine.AttributeNames;
import org.thymeleaf.engine.ElementNames;
import org.thymeleaf.standard.processor.StandardBlockTagProcessor;
import org.thymeleaf.standard.processor.StandardUtextTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EscapedAttributeUtils;
import org.thymeleaf.util.TextUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/inline/OutputExpressionInlinePreProcessorHandler.class */
public final class OutputExpressionInlinePreProcessorHandler implements IInlinePreProcessorHandler {
    private static final int DEFAULT_LEVELS_SIZE = 2;
    private final IInlinePreProcessorHandler next;
    private final String standardDialectPrefix;
    private final String[] inlineAttributeNames;
    private final char[] blockElementName;
    private final String escapedTextAttributeName;
    private final String unescapedTextAttributeName;
    private int execLevel;
    private TemplateMode[] inlineTemplateModes = new TemplateMode[2];
    private int[] inlineExecLevels = new int[2];
    private int inlineIndex;
    private char[] attributeBuffer;

    public OutputExpressionInlinePreProcessorHandler(IEngineConfiguration configuration, TemplateMode templateMode, String standardDialectPrefix, IInlinePreProcessorHandler handler) {
        this.next = handler;
        this.standardDialectPrefix = standardDialectPrefix;
        this.inlineAttributeNames = AttributeNames.forName(templateMode, this.standardDialectPrefix, "inline").getCompleteAttributeNames();
        this.blockElementName = ElementNames.forName(templateMode, this.standardDialectPrefix, StandardBlockTagProcessor.ELEMENT_NAME).getCompleteElementNames()[0].toCharArray();
        this.escapedTextAttributeName = AttributeNames.forName(templateMode, this.standardDialectPrefix, "text").getCompleteAttributeNames()[0];
        this.unescapedTextAttributeName = AttributeNames.forName(templateMode, this.standardDialectPrefix, StandardUtextTagProcessor.ATTR_NAME).getCompleteAttributeNames()[0];
        Arrays.fill(this.inlineTemplateModes, (Object) null);
        Arrays.fill(this.inlineExecLevels, -1);
        this.inlineIndex = 0;
        this.execLevel = 0;
        this.inlineTemplateModes[this.inlineIndex] = templateMode;
        this.inlineExecLevels[this.inlineIndex] = this.execLevel;
        this.attributeBuffer = null;
    }

    @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
    public void handleText(char[] buffer, int offset, int len, int line, int col) {
        if (this.inlineTemplateModes[this.inlineIndex] != this.inlineTemplateModes[0]) {
            this.next.handleText(buffer, offset, len, line, col);
        } else if (!mightNeedInlining(buffer, offset, len)) {
            this.next.handleText(buffer, offset, len, line, col);
        } else {
            performInlining(buffer, offset, len, line, col);
        }
    }

    @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
    public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) {
        increaseExecLevel();
        this.next.handleStandaloneElementStart(buffer, nameOffset, nameLen, minimized, line, col);
    }

    @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
    public void handleStandaloneElementEnd(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) {
        decreaseExecLevel();
        this.next.handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col);
    }

    @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
    public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) {
        increaseExecLevel();
        this.next.handleOpenElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
    public void handleOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) {
        this.next.handleOpenElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
    public void handleAutoOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) {
        increaseExecLevel();
        this.next.handleAutoOpenElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
    public void handleAutoOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) {
        this.next.handleAutoOpenElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
    public void handleCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) {
        this.next.handleCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
    public void handleCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) {
        decreaseExecLevel();
        this.next.handleCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
    public void handleAutoCloseElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) {
        this.next.handleAutoCloseElementStart(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
    public void handleAutoCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) {
        decreaseExecLevel();
        this.next.handleAutoCloseElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.thymeleaf.standard.inline.IInlinePreProcessorHandler
    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) {
        if (isInlineAttribute(buffer, nameOffset, nameLen)) {
            String inlineModeAttributeValue = EscapedAttributeUtils.unescapeAttribute(this.inlineTemplateModes[0], new String(buffer, valueContentOffset, valueContentLen));
            TemplateMode inlineTemplateMode = computeAssociatedTemplateMode(inlineModeAttributeValue);
            setInlineTemplateMode(inlineTemplateMode);
        }
        this.next.handleAttribute(buffer, nameOffset, nameLen, nameLine, nameCol, operatorOffset, operatorLen, operatorLine, operatorCol, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);
    }

    private void increaseExecLevel() {
        this.execLevel++;
    }

    private void decreaseExecLevel() {
        if (this.inlineExecLevels[this.inlineIndex] == this.execLevel) {
            this.inlineTemplateModes[this.inlineIndex] = null;
            this.inlineExecLevels[this.inlineIndex] = -1;
            this.inlineIndex--;
        }
        this.execLevel--;
    }

    private boolean isInlineAttribute(char[] buffer, int nameOffset, int nameLen) {
        String[] strArr;
        boolean caseSensitive = this.inlineTemplateModes[0].isCaseSensitive();
        for (String inlineAttributeName : this.inlineAttributeNames) {
            if (TextUtils.equals(caseSensitive, inlineAttributeName, 0, inlineAttributeName.length(), buffer, nameOffset, nameLen)) {
                return true;
            }
        }
        return false;
    }

    private void setInlineTemplateMode(TemplateMode templateMode) {
        if (this.inlineExecLevels[this.inlineIndex] != this.execLevel) {
            this.inlineIndex++;
        }
        if (this.inlineIndex >= this.inlineTemplateModes.length) {
            this.inlineTemplateModes = (TemplateMode[]) Arrays.copyOf(this.inlineTemplateModes, this.inlineTemplateModes.length + 2);
            int oldInlineExecLevelsLen = this.inlineExecLevels.length;
            this.inlineExecLevels = Arrays.copyOf(this.inlineExecLevels, this.inlineExecLevels.length + 2);
            Arrays.fill(this.inlineExecLevels, oldInlineExecLevelsLen, this.inlineExecLevels.length, -1);
        }
        this.inlineTemplateModes[this.inlineIndex] = templateMode;
        this.inlineExecLevels[this.inlineIndex] = this.execLevel;
    }

    private static TemplateMode computeAssociatedTemplateMode(String inlineModeAttributeValue) {
        StandardInlineMode inlineMode = StandardInlineMode.parse(inlineModeAttributeValue);
        if (inlineMode == null) {
            return null;
        }
        switch (inlineMode) {
            case NONE:
                return null;
            case HTML:
                return TemplateMode.HTML;
            case XML:
                return TemplateMode.XML;
            case TEXT:
                return TemplateMode.TEXT;
            case JAVASCRIPT:
                return TemplateMode.JAVASCRIPT;
            case CSS:
                return TemplateMode.CSS;
            default:
                throw new IllegalArgumentException("Unrecognized inline mode: " + inlineMode);
        }
    }

    private static boolean mightNeedInlining(char[] buffer, int offset, int len) {
        char c;
        int n = len;
        int i = offset;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                if (buffer[i] == '[' && n > 0 && ((c = buffer[i + 1]) == '[' || c == '(')) {
                    return true;
                }
                i++;
            } else {
                return false;
            }
        }
    }

    private void performInlining(char[] text, int offset, int len, int line, int col) {
        int[] locator = {line, col};
        int i = offset;
        int current = i;
        int maxi = offset + len;
        int currentLine = -1;
        int currentCol = -1;
        char innerClosingChar = 0;
        boolean inExpression = false;
        while (i < maxi) {
            currentLine = locator[0];
            currentCol = locator[1];
            if (!inExpression) {
                int expStart = findNextStructureStart(text, i, maxi, locator);
                if (expStart == -1) {
                    this.next.handleText(text, current, maxi - current, currentLine, currentCol);
                    return;
                }
                inExpression = true;
                if (expStart > current) {
                    this.next.handleText(text, current, expStart - current, currentLine, currentCol);
                }
                innerClosingChar = text[expStart + 1] == '[' ? ']' : ')';
                current = expStart;
                i = current + 2;
            } else {
                int expEnd = findNextStructureEndAvoidQuotes(text, i, maxi, innerClosingChar, locator);
                if (expEnd < 0) {
                    this.next.handleText(text, current, maxi - current, currentLine, currentCol);
                    return;
                }
                String textAttributeName = text[current + 1] == '[' ? this.escapedTextAttributeName : this.unescapedTextAttributeName;
                int textAttributeNameLen = textAttributeName.length();
                int textAttributeValueLen = expEnd - (current + 2);
                prepareAttributeBuffer(textAttributeName, text, current + 2, textAttributeValueLen);
                this.next.handleOpenElementStart(this.blockElementName, 0, this.blockElementName.length, currentLine, currentCol + 2);
                this.next.handleAttribute(this.attributeBuffer, 0, textAttributeNameLen, currentLine, currentCol + 2, textAttributeNameLen, 1, currentLine, currentCol + 2, textAttributeNameLen + 2, textAttributeValueLen, textAttributeNameLen + 1, textAttributeValueLen + 2, currentLine, currentCol + 2);
                this.next.handleOpenElementEnd(this.blockElementName, 0, this.blockElementName.length, currentLine, currentCol + 2);
                this.next.handleCloseElementStart(this.blockElementName, 0, this.blockElementName.length, currentLine, currentCol + 2);
                this.next.handleCloseElementEnd(this.blockElementName, 0, this.blockElementName.length, currentLine, currentCol + 2);
                countChar(locator, text[expEnd]);
                countChar(locator, text[expEnd + 1]);
                inExpression = false;
                current = expEnd + 2;
                i = current;
            }
        }
        if (inExpression) {
            this.next.handleText(text, current, maxi - current, currentLine, currentCol);
        }
    }

    private static void countChar(int[] locator, char c) {
        if (c == '\n') {
            locator[0] = locator[0] + 1;
            locator[1] = 1;
            return;
        }
        locator[1] = locator[1] + 1;
    }

    private static int findNextStructureStart(char[] text, int offset, int maxi, int[] locator) {
        char c;
        int colIndex = offset;
        int i = offset;
        int n = maxi - offset;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c2 = text[i];
                if (c2 == '\n') {
                    colIndex = i;
                    locator[1] = 0;
                    locator[0] = locator[0] + 1;
                } else if (c2 == '[' && n > 0 && ((c = text[i + 1]) == '[' || c == '(')) {
                    break;
                }
                i++;
            } else {
                locator[1] = locator[1] + (maxi - colIndex);
                return -1;
            }
        }
        locator[1] = locator[1] + (i - colIndex);
        return i;
    }

    private static int findNextStructureEndAvoidQuotes(char[] text, int offset, int maxi, char innerClosingChar, int[] locator) {
        boolean inQuotes = false;
        boolean inApos = false;
        int colIndex = offset;
        int i = offset;
        int n = maxi - offset;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c = text[i];
                if (c == '\n') {
                    colIndex = i;
                    locator[1] = 0;
                    locator[0] = locator[0] + 1;
                } else if (c == '\"' && !inApos) {
                    inQuotes = !inQuotes;
                } else if (c == '\'' && !inQuotes) {
                    inApos = !inApos;
                } else if (c == innerClosingChar && !inQuotes && !inApos && n > 0 && text[i + 1] == ']') {
                    locator[1] = locator[1] + (i - colIndex);
                    return i;
                }
                i++;
            } else {
                locator[1] = locator[1] + (maxi - colIndex);
                return -1;
            }
        }
    }

    private void prepareAttributeBuffer(String attributeName, char[] valueText, int valueOffset, int valueLen) {
        int attributeNameLen = attributeName.length();
        int requiredLen = attributeNameLen + 2 + valueLen + 1;
        if (this.attributeBuffer == null || this.attributeBuffer.length < requiredLen) {
            this.attributeBuffer = new char[Math.max(requiredLen, 30)];
        }
        attributeName.getChars(0, attributeNameLen, this.attributeBuffer, 0);
        this.attributeBuffer[attributeNameLen] = '=';
        this.attributeBuffer[attributeNameLen + 1] = '\"';
        System.arraycopy(valueText, valueOffset, this.attributeBuffer, attributeNameLen + 2, valueLen);
        this.attributeBuffer[requiredLen - 1] = '\"';
    }
}