package org.springframework.web.servlet.view.freemarker;

import freemarker.ext.jsp.TaglibFactory;
import freemarker.template.Configuration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/freemarker/FreeMarkerConfig.class */
public interface FreeMarkerConfig {
    Configuration getConfiguration();

    TaglibFactory getTaglibFactory();
}