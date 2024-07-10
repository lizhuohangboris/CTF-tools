package org.thymeleaf.engine;

import java.io.IOException;
import java.io.Writer;
import org.thymeleaf.model.IModelVisitor;
import org.thymeleaf.model.IProcessingInstruction;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/ProcessingInstruction.class */
public final class ProcessingInstruction extends AbstractTemplateEvent implements IProcessingInstruction, IEngineTemplateEvent {
    private final String target;
    private final String content;
    private final String processingInstruction;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ProcessingInstruction(String target, String content) {
        this.target = target;
        this.content = content;
        this.processingInstruction = computeProcessingInstruction();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ProcessingInstruction(String processingInstruction, String target, String content, String templateName, int line, int col) {
        super(templateName, line, col);
        this.target = target;
        this.content = content;
        this.processingInstruction = processingInstruction != null ? processingInstruction : computeProcessingInstruction();
    }

    @Override // org.thymeleaf.model.IProcessingInstruction
    public String getTarget() {
        return this.target;
    }

    @Override // org.thymeleaf.model.IProcessingInstruction
    public String getContent() {
        return this.content;
    }

    @Override // org.thymeleaf.model.IProcessingInstruction
    public String getProcessingInstruction() {
        return this.processingInstruction;
    }

    private String computeProcessingInstruction() {
        StringBuilder strBuilder = new StringBuilder(100);
        strBuilder.append("<?");
        strBuilder.append(this.target);
        if (this.content != null) {
            strBuilder.append(' ');
            strBuilder.append(this.content);
        }
        strBuilder.append("?>");
        return strBuilder.toString();
    }

    @Override // org.thymeleaf.model.ITemplateEvent
    public void accept(IModelVisitor visitor) {
        visitor.visit(this);
    }

    @Override // org.thymeleaf.model.ITemplateEvent
    public void write(Writer writer) throws IOException {
        writer.write(this.processingInstruction);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ProcessingInstruction asEngineProcessingInstruction(IProcessingInstruction processingInstruction) {
        if (processingInstruction instanceof ProcessingInstruction) {
            return (ProcessingInstruction) processingInstruction;
        }
        return new ProcessingInstruction(null, processingInstruction.getTarget(), processingInstruction.getContent(), processingInstruction.getTemplateName(), processingInstruction.getLine(), processingInstruction.getCol());
    }

    @Override // org.thymeleaf.engine.IEngineTemplateEvent
    public void beHandled(ITemplateHandler handler) {
        handler.handleProcessingInstruction(this);
    }

    public String toString() {
        return getProcessingInstruction();
    }
}