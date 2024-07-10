package org.thymeleaf.templateparser.markup.decoupled;

import java.util.List;
import org.attoparser.AbstractChainedMarkupHandler;
import org.attoparser.IMarkupHandler;
import org.attoparser.ParseException;
import org.attoparser.select.ParseSelection;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/markup/decoupled/DecoupledTemplateLogicMarkupHandler.class */
public final class DecoupledTemplateLogicMarkupHandler extends AbstractChainedMarkupHandler {
    private static final int INJECTION_LEVEL = 0;
    private static final char[] INNER_WHITE_SPACE = " ".toCharArray();
    private final DecoupledTemplateLogic decoupledTemplateLogic;
    private final boolean injectAttributes;
    private ParseSelection parseSelection;
    private boolean lastWasInnerWhiteSpace;

    public DecoupledTemplateLogicMarkupHandler(DecoupledTemplateLogic decoupledTemplateLogic, IMarkupHandler handler) {
        super(handler);
        this.lastWasInnerWhiteSpace = false;
        Validate.notNull(decoupledTemplateLogic, "Decoupled Template Logic cannot be null");
        this.decoupledTemplateLogic = decoupledTemplateLogic;
        this.injectAttributes = this.decoupledTemplateLogic.hasInjectedAttributes();
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IMarkupHandler
    public void setParseSelection(ParseSelection selection) {
        this.parseSelection = selection;
        super.setParseSelection(selection);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementEnd(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        if (this.injectAttributes) {
            processInjectedAttributes(line, col);
        }
        super.handleStandaloneElementEnd(buffer, nameOffset, nameLen, minimized, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        if (this.injectAttributes) {
            processInjectedAttributes(line, col);
        }
        super.handleOpenElementEnd(buffer, nameOffset, nameLen, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleInnerWhiteSpace(char[] buffer, int offset, int len, int line, int col) throws ParseException {
        this.lastWasInnerWhiteSpace = true;
        super.handleInnerWhiteSpace(buffer, offset, len, line, col);
    }

    @Override // org.attoparser.AbstractChainedMarkupHandler, org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) throws ParseException {
        this.lastWasInnerWhiteSpace = false;
        super.handleAttribute(buffer, nameOffset, nameLen, nameLine, nameCol, operatorOffset, operatorLen, operatorLine, operatorCol, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen, valueLine, valueCol);
    }

    private void processInjectedAttributes(int line, int col) throws ParseException {
        String[] selectors;
        if (!this.parseSelection.isMatchingAny(0) || (selectors = this.parseSelection.getCurrentSelection(0)) == null || selectors.length == 0) {
            return;
        }
        for (String selector : selectors) {
            List<DecoupledInjectedAttribute> injectedAttributesForSelector = this.decoupledTemplateLogic.getInjectedAttributesForSelector(selector);
            if (injectedAttributesForSelector != null) {
                for (DecoupledInjectedAttribute injectedAttribute : injectedAttributesForSelector) {
                    if (!this.lastWasInnerWhiteSpace) {
                        super.handleInnerWhiteSpace(INNER_WHITE_SPACE, 0, 1, line, col);
                    }
                    super.handleAttribute(injectedAttribute.buffer, injectedAttribute.nameOffset, injectedAttribute.nameLen, line, col, injectedAttribute.operatorOffset, injectedAttribute.operatorLen, line, col, injectedAttribute.valueContentOffset, injectedAttribute.valueContentLen, injectedAttribute.valueOuterOffset, injectedAttribute.valueOuterLen, line, col);
                    this.lastWasInnerWhiteSpace = false;
                }
            }
        }
    }
}