package org.thymeleaf.model;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/model/IXMLDeclaration.class */
public interface IXMLDeclaration extends ITemplateEvent {
    String getKeyword();

    String getVersion();

    String getEncoding();

    String getStandalone();

    String getXmlDeclaration();
}