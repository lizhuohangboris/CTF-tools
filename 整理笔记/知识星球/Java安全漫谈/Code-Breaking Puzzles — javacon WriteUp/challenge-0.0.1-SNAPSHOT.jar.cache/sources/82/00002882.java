package org.thymeleaf.processor.cdatasection;

import org.thymeleaf.model.IModel;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/processor/cdatasection/ICDATASectionStructureHandler.class */
public interface ICDATASectionStructureHandler {
    void reset();

    void setContent(CharSequence charSequence);

    void replaceWith(IModel iModel, boolean z);

    void removeCDATASection();
}