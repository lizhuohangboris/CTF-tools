package org.thymeleaf.dialect;

import java.util.Set;
import org.thymeleaf.processor.IProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/dialect/IProcessorDialect.class */
public interface IProcessorDialect extends IDialect {
    String getPrefix();

    int getDialectProcessorPrecedence();

    Set<IProcessor> getProcessors(String str);
}