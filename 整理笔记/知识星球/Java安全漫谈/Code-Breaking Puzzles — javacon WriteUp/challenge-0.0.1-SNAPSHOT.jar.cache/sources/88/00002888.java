package org.thymeleaf.processor.doctype;

import org.thymeleaf.model.IModel;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/doctype/IDocTypeStructureHandler.class */
public interface IDocTypeStructureHandler {
    void reset();

    void setDocType(String str, String str2, String str3, String str4, String str5);

    void replaceWith(IModel iModel, boolean z);

    void removeDocType();
}