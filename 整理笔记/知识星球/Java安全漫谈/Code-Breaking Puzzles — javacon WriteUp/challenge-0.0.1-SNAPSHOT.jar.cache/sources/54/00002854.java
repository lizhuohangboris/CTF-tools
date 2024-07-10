package org.thymeleaf.extras.java8time.dialect;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.extras.java8time.expression.Temporals;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-extras-java8time-3.0.1.RELEASE.jar:org/thymeleaf/extras/java8time/dialect/Java8TimeExpressionFactory.class */
public class Java8TimeExpressionFactory implements IExpressionObjectFactory {
    private static final String TEMPORAL_EVALUATION_VARIABLE_NAME = "temporals";
    private static final Set<String> ALL_EXPRESSION_OBJECT_NAMES = Collections.unmodifiableSet(new HashSet(Arrays.asList(TEMPORAL_EVALUATION_VARIABLE_NAME)));

    @Override // org.thymeleaf.expression.IExpressionObjectFactory
    public Set<String> getAllExpressionObjectNames() {
        return ALL_EXPRESSION_OBJECT_NAMES;
    }

    @Override // org.thymeleaf.expression.IExpressionObjectFactory
    public Object buildObject(IExpressionContext context, String expressionObjectName) {
        if (TEMPORAL_EVALUATION_VARIABLE_NAME.equals(expressionObjectName)) {
            return new Temporals(context.getLocale());
        }
        return null;
    }

    @Override // org.thymeleaf.expression.IExpressionObjectFactory
    public boolean isCacheable(String expressionObjectName) {
        return expressionObjectName != null && TEMPORAL_EVALUATION_VARIABLE_NAME.equals(expressionObjectName);
    }
}