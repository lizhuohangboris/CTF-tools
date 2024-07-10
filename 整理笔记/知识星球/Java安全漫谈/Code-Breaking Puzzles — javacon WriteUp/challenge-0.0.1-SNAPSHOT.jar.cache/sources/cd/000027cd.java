package org.thymeleaf.dialect;

import java.util.Set;
import org.thymeleaf.postprocessor.IPostProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/dialect/IPostProcessorDialect.class */
public interface IPostProcessorDialect extends IDialect {
    int getDialectPostProcessorPrecedence();

    Set<IPostProcessor> getPostProcessors();
}