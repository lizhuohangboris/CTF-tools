package org.thymeleaf.model;

import java.io.IOException;
import java.io.Writer;
import org.thymeleaf.engine.AttributeDefinition;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/model/IAttribute.class */
public interface IAttribute {
    String getAttributeCompleteName();

    AttributeDefinition getAttributeDefinition();

    String getOperator();

    String getValue();

    AttributeValueQuotes getValueQuotes();

    boolean hasLocation();

    String getTemplateName();

    int getLine();

    int getCol();

    void write(Writer writer) throws IOException;
}