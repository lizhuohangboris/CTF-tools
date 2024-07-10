package org.thymeleaf;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.ContentTypeUtils;
import org.thymeleaf.util.LoggingUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/TemplateSpec.class */
public final class TemplateSpec implements Serializable {
    private static final long serialVersionUID = 51214133;
    private final String template;
    private final Set<String> templateSelectors;
    private final TemplateMode templateMode;
    private final Map<String, Object> templateResolutionAttributes;
    private final String outputContentType;
    private final boolean outputSSE;

    public TemplateSpec(String template, TemplateMode templateMode) {
        this(template, null, templateMode, null, null);
    }

    public TemplateSpec(String template, String outputContentType) {
        this(template, null, null, outputContentType, null);
    }

    public TemplateSpec(String template, Map<String, Object> templateResolutionAttributes) {
        this(template, null, null, null, templateResolutionAttributes);
    }

    public TemplateSpec(String template, Set<String> templateSelectors, TemplateMode templateMode, Map<String, Object> templateResolutionAttributes) {
        this(template, templateSelectors, templateMode, null, templateResolutionAttributes);
    }

    public TemplateSpec(String template, Set<String> templateSelectors, String outputContentType, Map<String, Object> templateResolutionAttributes) {
        this(template, templateSelectors, null, outputContentType, templateResolutionAttributes);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TemplateSpec(String template, Set<String> templateSelectors, TemplateMode templateMode, String outputContentType, Map<String, Object> templateResolutionAttributes) {
        Validate.notNull(template, "Template cannot be null");
        Validate.isTrue(templateMode == null || outputContentType == null, "If template mode or output content type are specified, the other one cannot");
        this.template = template;
        if (templateSelectors != null && !templateSelectors.isEmpty()) {
            Validate.containsNoEmpties(templateSelectors, "If specified, the Template Selector set cannot contain any nulls or empties");
            if (templateSelectors.size() == 1) {
                this.templateSelectors = Collections.singleton(templateSelectors.iterator().next());
            } else {
                this.templateSelectors = Collections.unmodifiableSet(new TreeSet(templateSelectors));
            }
        } else {
            this.templateSelectors = null;
        }
        this.templateResolutionAttributes = (templateResolutionAttributes == null || templateResolutionAttributes.isEmpty()) ? null : Collections.unmodifiableMap(new HashMap(templateResolutionAttributes));
        this.outputContentType = outputContentType;
        TemplateMode computedTemplateMode = ContentTypeUtils.computeTemplateModeForContentType(this.outputContentType);
        if (computedTemplateMode != null) {
            this.templateMode = computedTemplateMode;
        } else {
            this.templateMode = templateMode;
        }
        this.outputSSE = ContentTypeUtils.isContentTypeSSE(this.outputContentType);
    }

    public String getTemplate() {
        return this.template;
    }

    public boolean hasTemplateSelectors() {
        return this.templateSelectors != null;
    }

    public Set<String> getTemplateSelectors() {
        return this.templateSelectors;
    }

    public boolean hasTemplateMode() {
        return this.templateMode != null;
    }

    public TemplateMode getTemplateMode() {
        return this.templateMode;
    }

    public boolean hasTemplateResolutionAttributes() {
        return this.templateResolutionAttributes != null;
    }

    public Map<String, Object> getTemplateResolutionAttributes() {
        return this.templateResolutionAttributes;
    }

    public String getOutputContentType() {
        return this.outputContentType;
    }

    public boolean isOutputSSE() {
        return this.outputSSE;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TemplateSpec)) {
            return false;
        }
        TemplateSpec that = (TemplateSpec) o;
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
        if (this.templateMode == that.templateMode && this.outputContentType.equals(that.outputContentType)) {
            return this.templateResolutionAttributes == null ? that.templateResolutionAttributes == null : this.templateResolutionAttributes.equals(that.templateResolutionAttributes);
        }
        return false;
    }

    public int hashCode() {
        int result = this.template.hashCode();
        return (31 * ((31 * ((31 * ((31 * result) + (this.templateSelectors != null ? this.templateSelectors.hashCode() : 0))) + (this.templateMode != null ? this.templateMode.hashCode() : 0))) + (this.outputContentType != null ? this.outputContentType.hashCode() : 0))) + (this.templateResolutionAttributes != null ? this.templateResolutionAttributes.hashCode() : 0);
    }

    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(LoggingUtils.loggifyTemplateName(this.template));
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
        if (this.outputContentType != null) {
            strBuilder.append(" [");
            strBuilder.append(this.outputContentType);
            strBuilder.append("]");
        }
        return strBuilder.toString();
    }
}