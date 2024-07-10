package org.thymeleaf.templateparser.markup.decoupled;

import java.util.ArrayList;
import java.util.List;
import org.attoparser.AbstractMarkupHandler;
import org.attoparser.ParseException;
import org.springframework.beans.PropertyAccessor;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.TextUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/markup/decoupled/DecoupledTemplateLogicBuilderMarkupHandler.class */
public final class DecoupledTemplateLogicBuilderMarkupHandler extends AbstractMarkupHandler {
    public static final String TAG_NAME_ATTR = "attr";
    private final String templateName;
    private final TemplateMode templateMode;
    private final DecoupledTemplateLogic decoupledTemplateLogic;
    private boolean inLogicBody = false;
    private boolean inAttrTag = false;
    private Selector selector = new Selector();
    private List<DecoupledInjectedAttribute> currentInjectedAttributes = new ArrayList(8);
    public static final String TAG_NAME_LOGIC = "thlogic";
    private static final char[] TAG_NAME_LOGIC_CHARS = TAG_NAME_LOGIC.toCharArray();
    private static final char[] TAG_NAME_ATTR_CHARS = "attr".toCharArray();
    public static final String ATTRIBUTE_NAME_SEL = "sel";
    private static final char[] ATTRIBUTE_NAME_SEL_CHARS = ATTRIBUTE_NAME_SEL.toCharArray();

    public DecoupledTemplateLogicBuilderMarkupHandler(String templateName, TemplateMode templateMode) {
        Validate.notEmpty(templateName, "Template name cannot be null or empty");
        Validate.notNull(templateMode, "Template mode cannot be null");
        this.templateName = templateName;
        this.templateMode = templateMode;
        this.decoupledTemplateLogic = new DecoupledTemplateLogic();
    }

    public DecoupledTemplateLogic getDecoupledTemplateLogic() {
        return this.decoupledTemplateLogic;
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementStart(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        if (!this.inLogicBody || !TextUtils.equals(this.templateMode.isCaseSensitive(), buffer, nameOffset, nameLen, TAG_NAME_ATTR_CHARS, 0, TAG_NAME_ATTR_CHARS.length)) {
            return;
        }
        this.selector.increaseLevel();
        this.inAttrTag = true;
        this.currentInjectedAttributes.clear();
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleStandaloneElementEnd(char[] buffer, int nameOffset, int nameLen, boolean minimized, int line, int col) throws ParseException {
        if (!this.inLogicBody) {
            return;
        }
        if (this.inAttrTag && this.selector.isLevelEmpty()) {
            throw new TemplateInputException("Error while processing decoupled logic file: <attr> injection tag does not contain any \"sel\" selector attributes.", this.templateName, line, col);
        }
        String currentSelector = this.selector.getCurrentSelector();
        for (DecoupledInjectedAttribute injectedAttribute : this.currentInjectedAttributes) {
            this.decoupledTemplateLogic.addInjectedAttribute(currentSelector, injectedAttribute);
        }
        this.currentInjectedAttributes.clear();
        this.inAttrTag = false;
        this.selector.decreaseLevel();
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementStart(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        if (!this.inLogicBody) {
            if (TextUtils.equals(this.templateMode.isCaseSensitive(), buffer, nameOffset, nameLen, TAG_NAME_LOGIC_CHARS, 0, TAG_NAME_LOGIC_CHARS.length)) {
                this.inLogicBody = true;
            }
        } else if (!TextUtils.equals(this.templateMode.isCaseSensitive(), buffer, nameOffset, nameLen, TAG_NAME_ATTR_CHARS, 0, TAG_NAME_ATTR_CHARS.length)) {
        } else {
            this.selector.increaseLevel();
            this.inAttrTag = true;
            this.currentInjectedAttributes.clear();
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleOpenElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        if (!this.inLogicBody) {
            return;
        }
        if (this.inAttrTag && this.selector.isLevelEmpty()) {
            throw new TemplateInputException("Error while processing decoupled logic file: <attr> injection tag does not contain any \"sel\" selector attributes.", this.templateName, line, col);
        }
        String currentSelector = this.selector.getCurrentSelector();
        for (DecoupledInjectedAttribute injectedAttribute : this.currentInjectedAttributes) {
            this.decoupledTemplateLogic.addInjectedAttribute(currentSelector, injectedAttribute);
        }
        this.currentInjectedAttributes.clear();
        this.inAttrTag = false;
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        if (!this.inLogicBody) {
            return;
        }
        if (TextUtils.equals(this.templateMode.isCaseSensitive(), buffer, nameOffset, nameLen, TAG_NAME_LOGIC_CHARS, 0, TAG_NAME_LOGIC_CHARS.length)) {
            this.inLogicBody = false;
        } else if (TextUtils.equals(this.templateMode.isCaseSensitive(), buffer, nameOffset, nameLen, TAG_NAME_ATTR_CHARS, 0, TAG_NAME_ATTR_CHARS.length)) {
            this.selector.decreaseLevel();
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IElementHandler
    public void handleAutoCloseElementEnd(char[] buffer, int nameOffset, int nameLen, int line, int col) throws ParseException {
        if (!this.inLogicBody) {
            return;
        }
        if (TextUtils.equals(this.templateMode.isCaseSensitive(), buffer, nameOffset, nameLen, TAG_NAME_LOGIC_CHARS, 0, TAG_NAME_LOGIC_CHARS.length)) {
            this.inLogicBody = false;
        } else if (TextUtils.equals(this.templateMode.isCaseSensitive(), buffer, nameOffset, nameLen, TAG_NAME_ATTR_CHARS, 0, TAG_NAME_ATTR_CHARS.length)) {
            this.selector.decreaseLevel();
        }
    }

    @Override // org.attoparser.AbstractMarkupHandler, org.attoparser.IAttributeSequenceHandler
    public void handleAttribute(char[] buffer, int nameOffset, int nameLen, int nameLine, int nameCol, int operatorOffset, int operatorLen, int operatorLine, int operatorCol, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen, int valueLine, int valueCol) throws ParseException {
        if (!this.inAttrTag) {
            return;
        }
        if (TextUtils.equals(this.templateMode.isCaseSensitive(), buffer, nameOffset, nameLen, ATTRIBUTE_NAME_SEL_CHARS, 0, ATTRIBUTE_NAME_SEL_CHARS.length)) {
            if (!this.selector.isLevelEmpty()) {
                throw new TemplateInputException("Error while processing decoupled logic file: selector (\"sel\") attribute found more than once in attr injection tag", this.templateName, nameLine, nameCol);
            }
            this.selector.setSelector(new String(buffer, valueContentOffset, valueContentLen));
            return;
        }
        DecoupledInjectedAttribute injectedAttribute = DecoupledInjectedAttribute.createAttribute(buffer, nameOffset, nameLen, operatorOffset, operatorLen, valueContentOffset, valueContentLen, valueOuterOffset, valueOuterLen);
        this.currentInjectedAttributes.add(injectedAttribute);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/markup/decoupled/DecoupledTemplateLogicBuilderMarkupHandler$Selector.class */
    private static final class Selector {
        private int level = -1;
        private List<String> selectorLevels = new ArrayList(5);
        private String currentSelector = null;

        Selector() {
        }

        void increaseLevel() {
            this.level++;
        }

        void decreaseLevel() {
            if (this.level < 0) {
                throw new IndexOutOfBoundsException("Cannot decrease level when the selector is clean");
            }
            if (this.selectorLevels.size() > this.level) {
                this.selectorLevels.remove(this.level);
            }
            this.level--;
        }

        void setSelector(String selector) {
            this.selectorLevels.add(selector.charAt(0) == '/' ? selector : "//" + selector);
            this.currentSelector = null;
        }

        boolean isLevelEmpty() {
            return this.selectorLevels.size() <= this.level;
        }

        String getCurrentSelector() {
            if (this.currentSelector == null) {
                this.currentSelector = StringUtils.join(this.selectorLevels, "");
            }
            return this.currentSelector;
        }

        public String toString() {
            return PropertyAccessor.PROPERTY_KEY_PREFIX + this.level + "]" + this.selectorLevels.toString();
        }
    }
}