package org.thymeleaf.engine;

import java.io.IOException;
import java.io.Writer;
import org.thymeleaf.model.IModelVisitor;
import org.thymeleaf.model.IText;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/Text.class */
public final class Text extends AbstractTextualTemplateEvent implements IText {
    /* JADX INFO: Access modifiers changed from: package-private */
    public Text(CharSequence text) {
        super(text);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Text(CharSequence text, String templateName, int line, int col) {
        super(text, templateName, line, col);
    }

    @Override // org.thymeleaf.model.IText
    public String getText() {
        return getContentText();
    }

    @Override // java.lang.CharSequence
    public int length() {
        return getContentLength();
    }

    @Override // java.lang.CharSequence
    public char charAt(int index) {
        return charAtContent(index);
    }

    @Override // java.lang.CharSequence
    public CharSequence subSequence(int start, int end) {
        return contentSubSequence(start, end);
    }

    @Override // org.thymeleaf.model.ITemplateEvent
    public void accept(IModelVisitor visitor) {
        visitor.visit(this);
    }

    @Override // org.thymeleaf.model.ITemplateEvent
    public void write(Writer writer) throws IOException {
        writeContent(writer);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Text asEngineText(IText text) {
        if (text instanceof Text) {
            return (Text) text;
        }
        return new Text(text.getText(), text.getTemplateName(), text.getLine(), text.getCol());
    }

    @Override // org.thymeleaf.engine.IEngineTemplateEvent
    public void beHandled(ITemplateHandler handler) {
        handler.handleText(this);
    }

    @Override // org.thymeleaf.engine.AbstractTextualTemplateEvent, java.lang.CharSequence
    public String toString() {
        return getText();
    }
}