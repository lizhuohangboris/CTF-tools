package org.thymeleaf.model;

import java.io.IOException;
import java.io.Writer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/model/ITemplateEvent.class */
public interface ITemplateEvent {
    boolean hasLocation();

    String getTemplateName();

    int getLine();

    int getCol();

    void accept(IModelVisitor iModelVisitor);

    void write(Writer writer) throws IOException;
}