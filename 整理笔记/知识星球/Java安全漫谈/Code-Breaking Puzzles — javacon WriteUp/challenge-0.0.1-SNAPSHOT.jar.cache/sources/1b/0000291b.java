package org.thymeleaf.standard.expression;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Map;
import org.thymeleaf.engine.TemplateModel;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.FastStringWriter;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/Fragment.class */
public final class Fragment {
    public static final Fragment EMPTY_FRAGMENT = new Fragment();
    private final TemplateModel templateModel;
    private final Map<String, Object> parameters;
    private final boolean syntheticParameters;

    public Fragment(TemplateModel templateModel, Map<String, Object> parameters, boolean syntheticParameters) {
        Validate.notNull(templateModel, "Template model cannot be null");
        this.templateModel = templateModel;
        this.parameters = parameters != null ? Collections.unmodifiableMap(parameters) : null;
        this.syntheticParameters = this.parameters != null && this.parameters.size() > 0 && syntheticParameters;
    }

    private Fragment() {
        this.templateModel = null;
        this.parameters = null;
        this.syntheticParameters = false;
    }

    public TemplateModel getTemplateModel() {
        return this.templateModel;
    }

    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    public boolean hasSyntheticParameters() {
        return this.syntheticParameters;
    }

    public void write(Writer writer) throws IOException {
        if (this.templateModel != null) {
            this.templateModel.write(writer);
        }
    }

    public String toString() {
        Writer stringWriter = new FastStringWriter();
        try {
            write(stringWriter);
            return stringWriter.toString();
        } catch (IOException e) {
            throw new TemplateProcessingException("Exception while creating String representation of model entity", e);
        }
    }
}