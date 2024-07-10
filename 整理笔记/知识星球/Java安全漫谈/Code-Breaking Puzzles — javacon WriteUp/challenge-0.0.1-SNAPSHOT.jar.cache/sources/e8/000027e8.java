package org.thymeleaf.engine;

import org.thymeleaf.model.IModel;
import org.thymeleaf.processor.doctype.IDocTypeStructureHandler;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/DocTypeStructureHandler.class */
public final class DocTypeStructureHandler implements IDocTypeStructureHandler {
    boolean setDocType;
    String setDocTypeKeyword;
    String setDocTypeElementName;
    String setDocTypePublicId;
    String setDocTypeSystemId;
    String setDocTypeInternalSubset;
    boolean replaceWithModel;
    IModel replaceWithModelValue;
    boolean replaceWithModelProcessable;
    boolean removeDocType;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DocTypeStructureHandler() {
        reset();
    }

    @Override // org.thymeleaf.processor.doctype.IDocTypeStructureHandler
    public void setDocType(String keyword, String elementName, String publicId, String systemId, String internalSubset) {
        reset();
        Validate.notNull(keyword, "Keyword cannot be null");
        Validate.notNull(elementName, "Element name cannot be null");
        this.setDocType = true;
        this.setDocTypeKeyword = keyword;
        this.setDocTypeElementName = elementName;
        this.setDocTypePublicId = publicId;
        this.setDocTypeSystemId = systemId;
        this.setDocTypeInternalSubset = internalSubset;
    }

    @Override // org.thymeleaf.processor.doctype.IDocTypeStructureHandler
    public void replaceWith(IModel model, boolean processable) {
        reset();
        Validate.notNull(model, "Model cannot be null");
        this.replaceWithModel = true;
        this.replaceWithModelValue = model;
        this.replaceWithModelProcessable = processable;
    }

    @Override // org.thymeleaf.processor.doctype.IDocTypeStructureHandler
    public void removeDocType() {
        reset();
        this.removeDocType = true;
    }

    @Override // org.thymeleaf.processor.doctype.IDocTypeStructureHandler
    public void reset() {
        this.setDocType = false;
        this.setDocTypeKeyword = null;
        this.setDocTypeElementName = null;
        this.setDocTypePublicId = null;
        this.setDocTypeSystemId = null;
        this.setDocTypeInternalSubset = null;
        this.replaceWithModel = false;
        this.replaceWithModelValue = null;
        this.replaceWithModelProcessable = false;
        this.removeDocType = false;
    }
}