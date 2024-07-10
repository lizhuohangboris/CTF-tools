package org.thymeleaf.engine;

import org.thymeleaf.model.IModel;
import org.thymeleaf.processor.cdatasection.ICDATASectionStructureHandler;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/CDATASectionStructureHandler.class */
public final class CDATASectionStructureHandler implements ICDATASectionStructureHandler {
    boolean setContent;
    CharSequence setContentValue;
    boolean replaceWithModel;
    IModel replaceWithModelValue;
    boolean replaceWithModelProcessable;
    boolean removeCDATASection;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CDATASectionStructureHandler() {
        reset();
    }

    @Override // org.thymeleaf.processor.cdatasection.ICDATASectionStructureHandler
    public void setContent(CharSequence content) {
        reset();
        Validate.notNull(content, "Content cannot be null");
        this.setContent = true;
        this.setContentValue = content;
    }

    @Override // org.thymeleaf.processor.cdatasection.ICDATASectionStructureHandler
    public void replaceWith(IModel model, boolean processable) {
        reset();
        Validate.notNull(model, "Model cannot be null");
        this.replaceWithModel = true;
        this.replaceWithModelValue = model;
        this.replaceWithModelProcessable = processable;
    }

    @Override // org.thymeleaf.processor.cdatasection.ICDATASectionStructureHandler
    public void removeCDATASection() {
        reset();
        this.removeCDATASection = true;
    }

    @Override // org.thymeleaf.processor.cdatasection.ICDATASectionStructureHandler
    public void reset() {
        this.setContent = false;
        this.setContentValue = null;
        this.replaceWithModel = false;
        this.replaceWithModelValue = null;
        this.replaceWithModelProcessable = false;
        this.removeCDATASection = false;
    }
}