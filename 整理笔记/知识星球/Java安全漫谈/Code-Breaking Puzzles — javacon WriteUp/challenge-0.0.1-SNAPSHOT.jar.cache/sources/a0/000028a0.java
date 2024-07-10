package org.thymeleaf.spring5;

import org.springframework.context.MessageSource;
import org.thymeleaf.ITemplateEngine;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/ISpringTemplateEngine.class */
public interface ISpringTemplateEngine extends ITemplateEngine {
    void setTemplateEngineMessageSource(MessageSource messageSource);
}