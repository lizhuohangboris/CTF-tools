package org.thymeleaf.processor.xmldeclaration;

import org.thymeleaf.model.IModel;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/xmldeclaration/IXMLDeclarationStructureHandler.class */
public interface IXMLDeclarationStructureHandler {
    void reset();

    void setXMLDeclaration(String str, String str2, String str3, String str4);

    void replaceWith(IModel iModel, boolean z);

    void removeXMLDeclaration();
}