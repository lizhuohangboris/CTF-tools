package org.thymeleaf.expression;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/expression/ExecutionInfo.class */
public final class ExecutionInfo {
    private final ITemplateContext context;
    private final Calendar now;

    public ExecutionInfo(ITemplateContext context) {
        this.context = context;
        this.now = Calendar.getInstance(context.getLocale());
    }

    public String getTemplateName() {
        return this.context.getTemplateData().getTemplate();
    }

    public TemplateMode getTemplateMode() {
        return this.context.getTemplateData().getTemplateMode();
    }

    public String getProcessedTemplateName() {
        return this.context.getTemplateStack().get(0).getTemplate();
    }

    public TemplateMode getProcessedTemplateMode() {
        return this.context.getTemplateStack().get(0).getTemplateMode();
    }

    public List<String> getTemplateNames() {
        List<TemplateData> templateStack = this.context.getTemplateStack();
        List<String> templateNameStack = new ArrayList<>(templateStack.size());
        for (TemplateData templateData : templateStack) {
            templateNameStack.add(templateData.getTemplate());
        }
        return templateNameStack;
    }

    public List<TemplateMode> getTemplateModes() {
        List<TemplateData> templateStack = this.context.getTemplateStack();
        List<TemplateMode> templateModeStack = new ArrayList<>(templateStack.size());
        for (TemplateData templateData : templateStack) {
            templateModeStack.add(templateData.getTemplateMode());
        }
        return templateModeStack;
    }

    public List<TemplateData> getTemplateStack() {
        return this.context.getTemplateStack();
    }

    public Calendar getNow() {
        return this.now;
    }
}