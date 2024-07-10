package org.thymeleaf.model;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/model/IDocType.class */
public interface IDocType extends ITemplateEvent {
    String getKeyword();

    String getElementName();

    String getType();

    String getPublicId();

    String getSystemId();

    String getInternalSubset();

    String getDocType();
}