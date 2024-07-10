package org.thymeleaf;

import java.io.Writer;
import java.util.Set;
import org.thymeleaf.context.IContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/ITemplateEngine.class */
public interface ITemplateEngine {
    IEngineConfiguration getConfiguration();

    String process(String str, IContext iContext);

    String process(String str, Set<String> set, IContext iContext);

    String process(TemplateSpec templateSpec, IContext iContext);

    void process(String str, IContext iContext, Writer writer);

    void process(String str, Set<String> set, IContext iContext, Writer writer);

    void process(TemplateSpec templateSpec, IContext iContext, Writer writer);

    IThrottledTemplateProcessor processThrottled(String str, IContext iContext);

    IThrottledTemplateProcessor processThrottled(String str, Set<String> set, IContext iContext);

    IThrottledTemplateProcessor processThrottled(TemplateSpec templateSpec, IContext iContext);
}