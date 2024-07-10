package org.thymeleaf.templateresource;

import java.io.IOException;
import java.io.Reader;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateresource/ITemplateResource.class */
public interface ITemplateResource {
    String getDescription();

    String getBaseName();

    boolean exists();

    Reader reader() throws IOException;

    ITemplateResource relative(String str);
}