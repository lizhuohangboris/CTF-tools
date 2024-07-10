package org.springframework.web.servlet.view.script;

import java.nio.charset.Charset;
import javax.script.ScriptEngine;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/script/ScriptTemplateConfig.class */
public interface ScriptTemplateConfig {
    @Nullable
    ScriptEngine getEngine();

    @Nullable
    String getEngineName();

    @Nullable
    Boolean isSharedEngine();

    @Nullable
    String[] getScripts();

    @Nullable
    String getRenderObject();

    @Nullable
    String getRenderFunction();

    @Nullable
    String getContentType();

    @Nullable
    Charset getCharset();

    @Nullable
    String getResourceLoaderPath();
}