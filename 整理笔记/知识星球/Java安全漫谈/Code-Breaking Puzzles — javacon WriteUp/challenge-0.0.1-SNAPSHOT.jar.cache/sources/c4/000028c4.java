package org.thymeleaf.spring5.expression;

import java.util.List;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.spring5.util.DetailedError;
import org.thymeleaf.spring5.util.FieldUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/expression/Fields.class */
public final class Fields {
    private final IExpressionContext context;

    public boolean hasAnyErrors() {
        return FieldUtils.hasAnyErrors(this.context);
    }

    public boolean hasErrors() {
        return FieldUtils.hasAnyErrors(this.context);
    }

    public boolean hasErrors(String field) {
        return FieldUtils.hasErrors(this.context, field);
    }

    public boolean hasGlobalErrors() {
        return FieldUtils.hasGlobalErrors(this.context);
    }

    public List<String> allErrors() {
        return FieldUtils.errors(this.context);
    }

    public List<String> errors() {
        return FieldUtils.errors(this.context);
    }

    public List<String> errors(String field) {
        return FieldUtils.errors(this.context, field);
    }

    public List<String> globalErrors() {
        return FieldUtils.globalErrors(this.context);
    }

    public String idFromName(String fieldName) {
        return FieldUtils.idFromName(fieldName);
    }

    public List<DetailedError> allDetailedErrors() {
        return FieldUtils.detailedErrors(this.context);
    }

    public List<DetailedError> detailedErrors() {
        return FieldUtils.detailedErrors(this.context);
    }

    public List<DetailedError> detailedErrors(String field) {
        return FieldUtils.detailedErrors(this.context, field);
    }

    public List<DetailedError> globalDetailedErrors() {
        return FieldUtils.globalDetailedErrors(this.context);
    }

    public Fields(IExpressionContext context) {
        this.context = context;
    }
}