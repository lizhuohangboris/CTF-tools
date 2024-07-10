package org.thymeleaf.processor.element;

import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.HTMLAttributeName;
import org.thymeleaf.engine.TextAttributeName;
import org.thymeleaf.engine.XMLAttributeName;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.TextUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/element/MatchingAttributeName.class */
public final class MatchingAttributeName {
    private final TemplateMode templateMode;
    private final AttributeName matchingAttributeName;
    private final String matchingAllAttributesWithPrefix;
    private final boolean matchingAllAttributes;

    public static MatchingAttributeName forAttributeName(TemplateMode templateMode, AttributeName matchingAttributeName) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        Validate.notNull(matchingAttributeName, "Matching attribute name cannot be null");
        if (templateMode == TemplateMode.HTML && !(matchingAttributeName instanceof HTMLAttributeName)) {
            throw new IllegalArgumentException("Attribute names for HTML template mode must be of class " + HTMLAttributeName.class.getName());
        }
        if (templateMode == TemplateMode.XML && !(matchingAttributeName instanceof XMLAttributeName)) {
            throw new IllegalArgumentException("Attribute names for XML template mode must be of class " + XMLAttributeName.class.getName());
        }
        if (templateMode.isText() && !(matchingAttributeName instanceof TextAttributeName)) {
            throw new IllegalArgumentException("Attribute names for any text template modes must be of class " + TextAttributeName.class.getName());
        }
        return new MatchingAttributeName(templateMode, matchingAttributeName, null, false);
    }

    public static MatchingAttributeName forAllAttributesWithPrefix(TemplateMode templateMode, String matchingAllAttributesWithPrefix) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        return new MatchingAttributeName(templateMode, null, matchingAllAttributesWithPrefix, false);
    }

    public static MatchingAttributeName forAllAttributes(TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        return new MatchingAttributeName(templateMode, null, null, true);
    }

    private MatchingAttributeName(TemplateMode templateMode, AttributeName matchingAttributeName, String matchingAllAttributesWithPrefix, boolean matchingAllAttributes) {
        this.templateMode = templateMode;
        this.matchingAttributeName = matchingAttributeName;
        this.matchingAllAttributesWithPrefix = matchingAllAttributesWithPrefix;
        this.matchingAllAttributes = matchingAllAttributes;
    }

    public TemplateMode getTemplateMode() {
        return this.templateMode;
    }

    public AttributeName getMatchingAttributeName() {
        return this.matchingAttributeName;
    }

    public String getMatchingAllAttributesWithPrefix() {
        return this.matchingAllAttributesWithPrefix;
    }

    public boolean isMatchingAllAttributes() {
        return this.matchingAllAttributes;
    }

    public boolean matches(AttributeName attributeName) {
        Validate.notNull(attributeName, "Attributes name cannot be null");
        if (this.matchingAttributeName == null) {
            if (this.templateMode == TemplateMode.HTML && !(attributeName instanceof HTMLAttributeName)) {
                return false;
            }
            if (this.templateMode == TemplateMode.XML && !(attributeName instanceof XMLAttributeName)) {
                return false;
            }
            if (this.templateMode.isText() && !(attributeName instanceof TextAttributeName)) {
                return false;
            }
            if (this.matchingAllAttributes) {
                return true;
            }
            if (this.matchingAllAttributesWithPrefix == null) {
                return attributeName.getPrefix() == null;
            }
            String attributeNamePrefix = attributeName.getPrefix();
            if (attributeNamePrefix == null) {
                return false;
            }
            return TextUtils.equals(this.templateMode.isCaseSensitive(), this.matchingAllAttributesWithPrefix, attributeNamePrefix);
        }
        return this.matchingAttributeName.equals(attributeName);
    }

    public String toString() {
        if (this.matchingAttributeName == null) {
            if (this.matchingAllAttributes) {
                return "*";
            }
            if (this.matchingAllAttributesWithPrefix == null) {
                return "[^:]*";
            }
            return this.matchingAllAttributesWithPrefix + ":*";
        }
        return this.matchingAttributeName.toString();
    }
}