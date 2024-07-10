package org.thymeleaf.engine;

import java.io.IOException;
import java.io.Writer;
import org.thymeleaf.model.IModelVisitor;
import org.thymeleaf.model.ITemplateStart;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/TemplateStart.class */
public final class TemplateStart extends AbstractTemplateEvent implements ITemplateStart, IEngineTemplateEvent {
    static final TemplateStart TEMPLATE_START_INSTANCE = new TemplateStart();

    private TemplateStart() {
    }

    @Override // org.thymeleaf.model.ITemplateEvent
    public void accept(IModelVisitor visitor) {
        visitor.visit(this);
    }

    @Override // org.thymeleaf.model.ITemplateEvent
    public void write(Writer writer) throws IOException {
    }

    public static TemplateStart asEngineTemplateStart(ITemplateStart templateStart) {
        return TEMPLATE_START_INSTANCE;
    }

    @Override // org.thymeleaf.engine.IEngineTemplateEvent
    public void beHandled(ITemplateHandler handler) {
        handler.handleTemplateStart(this);
    }

    public final String toString() {
        return "";
    }
}