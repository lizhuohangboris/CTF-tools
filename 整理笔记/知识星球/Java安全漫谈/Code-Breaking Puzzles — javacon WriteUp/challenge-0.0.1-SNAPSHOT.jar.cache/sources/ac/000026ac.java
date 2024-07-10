package org.springframework.web.servlet.tags;

import javax.servlet.jsp.JspTagException;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/tags/ArgumentAware.class */
public interface ArgumentAware {
    void addArgument(@Nullable Object obj) throws JspTagException;
}