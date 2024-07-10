package org.thymeleaf.model;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/model/IProcessingInstruction.class */
public interface IProcessingInstruction extends ITemplateEvent {
    String getTarget();

    String getContent();

    String getProcessingInstruction();
}