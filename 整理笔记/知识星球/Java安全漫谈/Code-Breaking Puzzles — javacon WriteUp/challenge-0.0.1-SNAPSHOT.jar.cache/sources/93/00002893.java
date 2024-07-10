package org.thymeleaf.processor.element;

import org.thymeleaf.engine.ElementName;
import org.thymeleaf.engine.HTMLElementName;
import org.thymeleaf.engine.TextElementName;
import org.thymeleaf.engine.XMLElementName;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.TextUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/element/MatchingElementName.class */
public final class MatchingElementName {
    private final TemplateMode templateMode;
    private final ElementName matchingElementName;
    private final String matchingAllElementsWithPrefix;
    private final boolean matchingAllElements;

    public static MatchingElementName forElementName(TemplateMode templateMode, ElementName matchingElementName) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        Validate.notNull(matchingElementName, "Matching element name cannot be null");
        if (templateMode == TemplateMode.HTML && !(matchingElementName instanceof HTMLElementName)) {
            throw new IllegalArgumentException("Element names for HTML template mode must be of class " + HTMLElementName.class.getName());
        }
        if (templateMode == TemplateMode.XML && !(matchingElementName instanceof XMLElementName)) {
            throw new IllegalArgumentException("Element names for XML template mode must be of class " + XMLElementName.class.getName());
        }
        if (templateMode.isText() && !(matchingElementName instanceof TextElementName)) {
            throw new IllegalArgumentException("Element names for any text template modes must be of class " + TextElementName.class.getName());
        }
        return new MatchingElementName(templateMode, matchingElementName, null, false);
    }

    public static MatchingElementName forAllElementsWithPrefix(TemplateMode templateMode, String matchingAllElementsWithPrefix) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        return new MatchingElementName(templateMode, null, matchingAllElementsWithPrefix, false);
    }

    public static MatchingElementName forAllElements(TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        return new MatchingElementName(templateMode, null, null, true);
    }

    private MatchingElementName(TemplateMode templateMode, ElementName matchingElementName, String matchingAllElementsWithPrefix, boolean matchingAllElements) {
        this.templateMode = templateMode;
        this.matchingElementName = matchingElementName;
        this.matchingAllElementsWithPrefix = matchingAllElementsWithPrefix;
        this.matchingAllElements = matchingAllElements;
    }

    public TemplateMode getTemplateMode() {
        return this.templateMode;
    }

    public ElementName getMatchingElementName() {
        return this.matchingElementName;
    }

    public String getMatchingAllElementsWithPrefix() {
        return this.matchingAllElementsWithPrefix;
    }

    public boolean isMatchingAllElements() {
        return this.matchingAllElements;
    }

    public boolean matches(ElementName elementName) {
        Validate.notNull(elementName, "Element name cannot be null");
        if (this.matchingElementName == null) {
            if (this.templateMode == TemplateMode.HTML && !(elementName instanceof HTMLElementName)) {
                return false;
            }
            if (this.templateMode == TemplateMode.XML && !(elementName instanceof XMLElementName)) {
                return false;
            }
            if (this.templateMode.isText() && !(elementName instanceof TextElementName)) {
                return false;
            }
            if (this.matchingAllElements) {
                return true;
            }
            if (this.matchingAllElementsWithPrefix == null) {
                return elementName.getPrefix() == null;
            }
            String elementNamePrefix = elementName.getPrefix();
            if (elementNamePrefix == null) {
                return false;
            }
            return TextUtils.equals(this.templateMode.isCaseSensitive(), this.matchingAllElementsWithPrefix, elementNamePrefix);
        }
        return this.matchingElementName.equals(elementName);
    }

    public String toString() {
        if (this.matchingElementName == null) {
            if (this.matchingAllElements) {
                return "*";
            }
            if (this.matchingAllElementsWithPrefix == null) {
                return "[^:]*";
            }
            return this.matchingAllElementsWithPrefix + ":*";
        }
        return this.matchingElementName.toString();
    }
}