package org.springframework.web.servlet.tags;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/tags/ThemeTag.class */
public class ThemeTag extends MessageTag {
    @Override // org.springframework.web.servlet.tags.MessageTag
    protected MessageSource getMessageSource() {
        return getRequestContext().getTheme().getMessageSource();
    }

    @Override // org.springframework.web.servlet.tags.MessageTag
    protected String getNoSuchMessageExceptionDescription(NoSuchMessageException ex) {
        return "Theme '" + getRequestContext().getTheme().getName() + "': " + ex.getMessage();
    }
}