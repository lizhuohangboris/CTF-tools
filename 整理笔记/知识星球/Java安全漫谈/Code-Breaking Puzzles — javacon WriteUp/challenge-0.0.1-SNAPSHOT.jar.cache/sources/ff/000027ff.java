package org.thymeleaf.engine;

import org.thymeleaf.model.ITemplateEvent;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/IEngineTemplateEvent.class */
interface IEngineTemplateEvent extends ITemplateEvent {
    void beHandled(ITemplateHandler iTemplateHandler);
}