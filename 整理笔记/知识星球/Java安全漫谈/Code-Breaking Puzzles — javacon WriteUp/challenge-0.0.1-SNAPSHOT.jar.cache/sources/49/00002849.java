package org.thymeleaf.expression;

import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/expression/IExpressionObjects.class */
public interface IExpressionObjects {
    int size();

    boolean containsObject(String str);

    Set<String> getObjectNames();

    Object getObject(String str);
}