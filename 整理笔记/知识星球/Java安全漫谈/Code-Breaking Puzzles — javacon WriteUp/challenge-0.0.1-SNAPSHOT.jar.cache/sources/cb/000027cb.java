package org.thymeleaf.dialect;

import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/dialect/IExecutionAttributeDialect.class */
public interface IExecutionAttributeDialect extends IDialect {
    Map<String, Object> getExecutionAttributes();
}