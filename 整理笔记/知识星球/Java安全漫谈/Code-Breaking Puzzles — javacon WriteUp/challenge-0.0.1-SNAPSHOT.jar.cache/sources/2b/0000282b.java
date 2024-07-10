package org.thymeleaf.engine;

import org.thymeleaf.model.IModel;
import org.thymeleaf.processor.text.ITextStructureHandler;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/TextStructureHandler.class */
public final class TextStructureHandler implements ITextStructureHandler {
    boolean setText;
    CharSequence setTextValue;
    boolean replaceWithModel;
    IModel replaceWithModelValue;
    boolean replaceWithModelProcessable;
    boolean removeText;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TextStructureHandler() {
        reset();
    }

    @Override // org.thymeleaf.processor.text.ITextStructureHandler
    public void setText(CharSequence text) {
        reset();
        Validate.notNull(text, "Text cannot be null");
        this.setText = true;
        this.setTextValue = text;
    }

    @Override // org.thymeleaf.processor.text.ITextStructureHandler
    public void replaceWith(IModel model, boolean processable) {
        reset();
        Validate.notNull(model, "Model cannot be null");
        this.replaceWithModel = true;
        this.replaceWithModelValue = model;
        this.replaceWithModelProcessable = processable;
    }

    @Override // org.thymeleaf.processor.text.ITextStructureHandler
    public void removeText() {
        reset();
        this.removeText = true;
    }

    @Override // org.thymeleaf.processor.text.ITextStructureHandler
    public void reset() {
        this.setText = false;
        this.setTextValue = null;
        this.replaceWithModel = false;
        this.replaceWithModelValue = null;
        this.replaceWithModelProcessable = false;
        this.removeText = false;
    }
}