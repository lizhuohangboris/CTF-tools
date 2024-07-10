package org.thymeleaf.cache;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.LoggingUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/cache/TemplateCacheKey.class */
public final class TemplateCacheKey implements Serializable {
    private static final long serialVersionUID = 45842555123291L;
    private final String ownerTemplate;
    private final String template;
    private final Set<String> templateSelectors;
    private final int lineOffset;
    private final int colOffset;
    private final TemplateMode templateMode;
    private final Map<String, Object> templateResolutionAttributes;
    private final int h;

    public TemplateCacheKey(String ownerTemplate, String template, Set<String> templateSelectors, int lineOffset, int colOffset, TemplateMode templateMode, Map<String, Object> templateResolutionAttributes) {
        Validate.notNull(template, "Template cannot be null");
        this.ownerTemplate = ownerTemplate;
        this.template = template;
        this.templateSelectors = templateSelectors;
        this.lineOffset = lineOffset;
        this.colOffset = colOffset;
        this.templateMode = templateMode;
        this.templateResolutionAttributes = templateResolutionAttributes;
        this.h = computeHashCode();
    }

    public String getOwnerTemplate() {
        return this.ownerTemplate;
    }

    public String getTemplate() {
        return this.template;
    }

    public Set<String> getTemplateSelectors() {
        return this.templateSelectors;
    }

    public int getLineOffset() {
        return this.lineOffset;
    }

    public int getColOffset() {
        return this.colOffset;
    }

    public TemplateMode getTemplateMode() {
        return this.templateMode;
    }

    public Map<String, Object> getTemplateResolutionAttributes() {
        return this.templateResolutionAttributes;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TemplateCacheKey)) {
            return false;
        }
        TemplateCacheKey that = (TemplateCacheKey) o;
        if (this.h != that.h || this.lineOffset != that.lineOffset || this.colOffset != that.colOffset) {
            return false;
        }
        if (this.ownerTemplate != null) {
            if (!this.ownerTemplate.equals(that.ownerTemplate)) {
                return false;
            }
        } else if (that.ownerTemplate != null) {
            return false;
        }
        if (!this.template.equals(that.template)) {
            return false;
        }
        if (this.templateSelectors != null) {
            if (!this.templateSelectors.equals(that.templateSelectors)) {
                return false;
            }
        } else if (that.templateSelectors != null) {
            return false;
        }
        if (this.templateMode != that.templateMode) {
            return false;
        }
        return this.templateResolutionAttributes == null ? that.templateResolutionAttributes == null : this.templateResolutionAttributes.equals(that.templateResolutionAttributes);
    }

    public int hashCode() {
        return this.h;
    }

    private int computeHashCode() {
        int result = this.ownerTemplate != null ? this.ownerTemplate.hashCode() : 0;
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * result) + this.template.hashCode())) + (this.templateSelectors != null ? this.templateSelectors.hashCode() : 0))) + this.lineOffset)) + this.colOffset)) + (this.templateMode != null ? this.templateMode.hashCode() : 0))) + (this.templateResolutionAttributes != null ? this.templateResolutionAttributes.hashCode() : 0);
    }

    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(LoggingUtils.loggifyTemplateName(this.template));
        if (this.ownerTemplate != null) {
            strBuilder.append('@');
            strBuilder.append('(');
            strBuilder.append(LoggingUtils.loggifyTemplateName(this.ownerTemplate));
            strBuilder.append(';');
            strBuilder.append(this.lineOffset);
            strBuilder.append(',');
            strBuilder.append(this.colOffset);
            strBuilder.append(')');
        }
        if (this.templateSelectors != null) {
            strBuilder.append("::");
            strBuilder.append(this.templateSelectors);
        }
        if (this.templateMode != null) {
            strBuilder.append(" @");
            strBuilder.append(this.templateMode);
        }
        if (this.templateResolutionAttributes != null) {
            strBuilder.append(" (");
            strBuilder.append(this.templateResolutionAttributes);
            strBuilder.append(")");
        }
        return strBuilder.toString();
    }
}