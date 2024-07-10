package org.thymeleaf.dialect;

import java.util.Set;
import org.thymeleaf.preprocessor.IPreProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/dialect/IPreProcessorDialect.class */
public interface IPreProcessorDialect extends IDialect {
    int getDialectPreProcessorPrecedence();

    Set<IPreProcessor> getPreProcessors();
}