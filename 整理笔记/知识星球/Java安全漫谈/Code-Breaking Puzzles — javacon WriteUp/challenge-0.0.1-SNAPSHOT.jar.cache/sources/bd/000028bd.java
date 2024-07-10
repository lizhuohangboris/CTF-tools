package org.thymeleaf.spring5.context.webflux;

import java.beans.PropertyEditor;
import org.springframework.validation.Errors;
import org.springframework.web.reactive.result.view.BindStatus;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/context/webflux/SpringWebFluxThymeleafBindStatus.class */
class SpringWebFluxThymeleafBindStatus implements IThymeleafBindStatus {
    private final BindStatus bindStatus;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SpringWebFluxThymeleafBindStatus(BindStatus bindStatus) {
        Validate.notNull(bindStatus, "BindStatus cannot be null");
        this.bindStatus = bindStatus;
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafBindStatus
    public String getPath() {
        return this.bindStatus.getPath();
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafBindStatus
    public String getExpression() {
        return this.bindStatus.getExpression();
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafBindStatus
    public Object getValue() {
        return this.bindStatus.getValue();
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafBindStatus
    public Class<?> getValueType() {
        return this.bindStatus.getValueType();
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafBindStatus
    public Object getActualValue() {
        return this.bindStatus.getActualValue();
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafBindStatus
    public String getDisplayValue() {
        return this.bindStatus.getDisplayValue();
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafBindStatus
    public boolean isError() {
        return this.bindStatus.isError();
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafBindStatus
    public String[] getErrorCodes() {
        return this.bindStatus.getErrorCodes();
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafBindStatus
    public String getErrorCode() {
        return this.bindStatus.getErrorCode();
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafBindStatus
    public String[] getErrorMessages() {
        return this.bindStatus.getErrorMessages();
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafBindStatus
    public String getErrorMessage() {
        return this.bindStatus.getErrorMessage();
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafBindStatus
    public String getErrorMessagesAsString(String delimiter) {
        return this.bindStatus.getErrorMessagesAsString(delimiter);
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafBindStatus
    public Errors getErrors() {
        return this.bindStatus.getErrors();
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafBindStatus
    public PropertyEditor getEditor() {
        return this.bindStatus.getEditor();
    }

    @Override // org.thymeleaf.spring5.context.IThymeleafBindStatus
    public PropertyEditor findEditor(Class<?> valueClass) {
        return this.bindStatus.findEditor(valueClass);
    }

    public String toString() {
        return this.bindStatus.toString();
    }
}