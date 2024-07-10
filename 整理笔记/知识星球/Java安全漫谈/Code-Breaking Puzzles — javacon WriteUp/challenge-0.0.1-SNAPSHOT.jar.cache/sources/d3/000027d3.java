package org.thymeleaf.engine;

import org.thymeleaf.model.ITemplateEvent;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/AbstractTemplateEvent.class */
abstract class AbstractTemplateEvent implements ITemplateEvent {
    final String templateName;
    final int line;
    final int col;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractTemplateEvent() {
        this(null, -1, -1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractTemplateEvent(String templateName, int line, int col) {
        this.templateName = templateName;
        this.line = line;
        this.col = col;
    }

    AbstractTemplateEvent(AbstractTemplateEvent original) {
        this.templateName = original.templateName;
        this.line = original.line;
        this.col = original.col;
    }

    @Override // org.thymeleaf.model.ITemplateEvent
    public final boolean hasLocation() {
        return (this.templateName == null || this.line == -1 || this.col == -1) ? false : true;
    }

    @Override // org.thymeleaf.model.ITemplateEvent
    public final String getTemplateName() {
        return this.templateName;
    }

    @Override // org.thymeleaf.model.ITemplateEvent
    public final int getLine() {
        return this.line;
    }

    @Override // org.thymeleaf.model.ITemplateEvent
    public final int getCol() {
        return this.col;
    }
}