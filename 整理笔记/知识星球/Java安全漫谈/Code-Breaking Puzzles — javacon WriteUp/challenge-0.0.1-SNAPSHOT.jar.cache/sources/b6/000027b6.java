package org.thymeleaf.context;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.expression.IExpressionObjects;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/IExpressionContext.class */
public interface IExpressionContext extends IContext {
    IEngineConfiguration getConfiguration();

    IExpressionObjects getExpressionObjects();
}