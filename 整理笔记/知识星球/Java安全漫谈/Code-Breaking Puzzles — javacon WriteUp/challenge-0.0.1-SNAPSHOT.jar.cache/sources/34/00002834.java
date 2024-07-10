package org.thymeleaf.engine;

import org.thymeleaf.model.IModel;
import org.thymeleaf.processor.xmldeclaration.IXMLDeclarationStructureHandler;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/XMLDeclarationStructureHandler.class */
public final class XMLDeclarationStructureHandler implements IXMLDeclarationStructureHandler {
    boolean setXMLDeclaration;
    String setXMLDeclarationKeyword;
    String setXMLDeclarationVersion;
    String setXMLDeclarationEncoding;
    String setXMLDeclarationStandalone;
    boolean replaceWithModel;
    IModel replaceWithModelValue;
    boolean replaceWithModelProcessable;
    boolean removeXMLDeclaration;

    /* JADX INFO: Access modifiers changed from: package-private */
    public XMLDeclarationStructureHandler() {
        reset();
    }

    @Override // org.thymeleaf.processor.xmldeclaration.IXMLDeclarationStructureHandler
    public void setXMLDeclaration(String keyword, String version, String encoding, String standalone) {
        reset();
        Validate.notNull(keyword, "Keyword cannot be null");
        this.setXMLDeclaration = true;
        this.setXMLDeclarationKeyword = keyword;
        this.setXMLDeclarationVersion = version;
        this.setXMLDeclarationEncoding = encoding;
        this.setXMLDeclarationStandalone = standalone;
    }

    @Override // org.thymeleaf.processor.xmldeclaration.IXMLDeclarationStructureHandler
    public void replaceWith(IModel model, boolean processable) {
        reset();
        Validate.notNull(model, "Model cannot be null");
        this.replaceWithModel = true;
        this.replaceWithModelValue = model;
        this.replaceWithModelProcessable = processable;
    }

    @Override // org.thymeleaf.processor.xmldeclaration.IXMLDeclarationStructureHandler
    public void removeXMLDeclaration() {
        reset();
        this.removeXMLDeclaration = true;
    }

    @Override // org.thymeleaf.processor.xmldeclaration.IXMLDeclarationStructureHandler
    public void reset() {
        this.setXMLDeclaration = false;
        this.setXMLDeclarationKeyword = null;
        this.setXMLDeclarationVersion = null;
        this.setXMLDeclarationEncoding = null;
        this.setXMLDeclarationStandalone = null;
        this.replaceWithModel = false;
        this.replaceWithModelValue = null;
        this.replaceWithModelProcessable = false;
        this.removeXMLDeclaration = false;
    }
}