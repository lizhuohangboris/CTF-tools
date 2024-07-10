package org.thymeleaf.engine;

import java.io.IOException;
import java.io.Writer;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IModelVisitor;
import org.thymeleaf.templatemode.TemplateMode;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/CloseElementTag.class */
public final class CloseElementTag extends AbstractElementTag implements ICloseElementTag, IEngineTemplateEvent {
    final String trailingWhiteSpace;
    final boolean unmatched;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CloseElementTag(TemplateMode templateMode, ElementDefinition elementDefinition, String elementCompleteName, String trailingWhiteSpace, boolean synthetic, boolean unmatched) {
        super(templateMode, elementDefinition, elementCompleteName, synthetic);
        this.trailingWhiteSpace = trailingWhiteSpace;
        this.unmatched = unmatched;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CloseElementTag(TemplateMode templateMode, ElementDefinition elementDefinition, String elementCompleteName, String trailingWhiteSpace, boolean synthetic, boolean unmatched, String templateName, int line, int col) {
        super(templateMode, elementDefinition, elementCompleteName, synthetic, templateName, line, col);
        this.trailingWhiteSpace = trailingWhiteSpace;
        this.unmatched = unmatched;
    }

    @Override // org.thymeleaf.model.ICloseElementTag
    public boolean isUnmatched() {
        return this.unmatched;
    }

    @Override // org.thymeleaf.model.ITemplateEvent
    public void accept(IModelVisitor visitor) {
        visitor.visit(this);
    }

    @Override // org.thymeleaf.model.ITemplateEvent
    public void write(Writer writer) throws IOException {
        if (this.synthetic) {
            return;
        }
        if (this.templateMode.isText()) {
            writer.write("[/");
            writer.write(this.elementCompleteName);
            if (this.trailingWhiteSpace != null) {
                writer.write(this.trailingWhiteSpace);
            }
            writer.write("]");
            return;
        }
        writer.write("</");
        writer.write(this.elementCompleteName);
        if (this.trailingWhiteSpace != null) {
            writer.write(this.trailingWhiteSpace);
        }
        writer.write(62);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static CloseElementTag asEngineCloseElementTag(ICloseElementTag closeElementTag) {
        if (closeElementTag instanceof CloseElementTag) {
            return (CloseElementTag) closeElementTag;
        }
        return new CloseElementTag(closeElementTag.getTemplateMode(), closeElementTag.getElementDefinition(), closeElementTag.getElementCompleteName(), null, closeElementTag.isSynthetic(), closeElementTag.isUnmatched(), closeElementTag.getTemplateName(), closeElementTag.getLine(), closeElementTag.getCol());
    }

    @Override // org.thymeleaf.engine.IEngineTemplateEvent
    public void beHandled(ITemplateHandler handler) {
        handler.handleCloseElement(this);
    }
}