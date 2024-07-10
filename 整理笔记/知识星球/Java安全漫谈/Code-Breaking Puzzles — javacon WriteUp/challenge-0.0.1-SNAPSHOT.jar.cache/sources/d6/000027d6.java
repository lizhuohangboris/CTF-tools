package org.thymeleaf.engine;

import java.io.IOException;
import java.io.Writer;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.util.FastStringWriter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/Attribute.class */
final class Attribute implements IAttribute {
    static final String DEFAULT_OPERATOR = "=";
    final AttributeDefinition definition;
    final String completeName;
    final String operator;
    final String value;
    final AttributeValueQuotes valueQuotes;
    final String templateName;
    final int line;
    final int col;
    private volatile IStandardExpression standardExpression = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Attribute(AttributeDefinition definition, String completeName, String operator, String value, AttributeValueQuotes valueQuotes, String templateName, int line, int col) {
        this.definition = definition;
        this.completeName = completeName;
        this.value = value;
        if (value == null) {
            this.operator = null;
        } else if (operator == null) {
            this.operator = DEFAULT_OPERATOR;
        } else {
            this.operator = operator;
        }
        if (value == null) {
            this.valueQuotes = null;
        } else if (valueQuotes == null) {
            this.valueQuotes = AttributeValueQuotes.DOUBLE;
        } else if (valueQuotes == AttributeValueQuotes.NONE && value.length() == 0) {
            this.valueQuotes = AttributeValueQuotes.DOUBLE;
        } else {
            this.valueQuotes = valueQuotes;
        }
        this.templateName = templateName;
        this.line = line;
        this.col = col;
    }

    @Override // org.thymeleaf.model.IAttribute
    public AttributeDefinition getAttributeDefinition() {
        return this.definition;
    }

    @Override // org.thymeleaf.model.IAttribute
    public String getAttributeCompleteName() {
        return this.completeName;
    }

    @Override // org.thymeleaf.model.IAttribute
    public String getOperator() {
        return this.operator;
    }

    @Override // org.thymeleaf.model.IAttribute
    public String getValue() {
        return this.value;
    }

    @Override // org.thymeleaf.model.IAttribute
    public AttributeValueQuotes getValueQuotes() {
        return this.valueQuotes;
    }

    @Override // org.thymeleaf.model.IAttribute
    public String getTemplateName() {
        return this.templateName;
    }

    @Override // org.thymeleaf.model.IAttribute
    public final boolean hasLocation() {
        return (this.templateName == null || this.line == -1 || this.col == -1) ? false : true;
    }

    @Override // org.thymeleaf.model.IAttribute
    public int getLine() {
        return this.line;
    }

    @Override // org.thymeleaf.model.IAttribute
    public int getCol() {
        return this.col;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public IStandardExpression getCachedStandardExpression() {
        return this.standardExpression;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCachedStandardExpression(IStandardExpression standardExpression) {
        this.standardExpression = standardExpression;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Attribute modify(AttributeDefinition definition, String completeName, String value, AttributeValueQuotes valueQuotes) {
        return new Attribute(definition == null ? this.definition : definition, completeName == null ? this.completeName : completeName, this.operator, value, valueQuotes == null ? this.valueQuotes : valueQuotes, this.templateName, this.line, this.col);
    }

    @Override // org.thymeleaf.model.IAttribute
    public void write(Writer writer) throws IOException {
        writer.write(this.completeName);
        if (this.value != null) {
            writer.write(this.operator);
            if (this.valueQuotes == null) {
                writer.write(this.value);
                return;
            }
            switch (this.valueQuotes) {
                case DOUBLE:
                    writer.write(34);
                    writer.write(this.value);
                    writer.write(34);
                    return;
                case SINGLE:
                    writer.write(39);
                    writer.write(this.value);
                    writer.write(39);
                    return;
                case NONE:
                    writer.write(this.value);
                    return;
                default:
                    return;
            }
        }
    }

    public String toString() {
        Writer stringWriter = new FastStringWriter();
        try {
            write(stringWriter);
            return stringWriter.toString();
        } catch (IOException e) {
            throw new TemplateProcessingException("Error computing attribute representation", e);
        }
    }
}