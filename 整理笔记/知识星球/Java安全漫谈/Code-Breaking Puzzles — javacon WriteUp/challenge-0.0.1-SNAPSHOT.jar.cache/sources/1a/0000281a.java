package org.thymeleaf.engine;

import java.io.IOException;
import java.io.Writer;
import org.thymeleaf.model.IModelVisitor;
import org.thymeleaf.model.ITemplateEnd;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/TemplateEnd.class */
public final class TemplateEnd extends AbstractTemplateEvent implements ITemplateEnd, IEngineTemplateEvent {
    static final TemplateEnd TEMPLATE_END_INSTANCE = new TemplateEnd();

    private TemplateEnd() {
    }

    @Override // org.thymeleaf.model.ITemplateEvent
    public void accept(IModelVisitor visitor) {
        visitor.visit(this);
    }

    @Override // org.thymeleaf.model.ITemplateEvent
    public void write(Writer writer) throws IOException {
    }

    public static TemplateEnd asEngineTemplateEnd(ITemplateEnd templateEnd) {
        return TEMPLATE_END_INSTANCE;
    }

    @Override // org.thymeleaf.engine.IEngineTemplateEvent
    public void beHandled(ITemplateHandler handler) {
        handler.handleTemplateEnd(this);
    }

    public final String toString() {
        return "";
    }
}