package org.thymeleaf.extras.java8time.dialect;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-extras-java8time-3.0.1.RELEASE.jar:org/thymeleaf/extras/java8time/dialect/Java8TimeDialect.class */
public class Java8TimeDialect extends AbstractDialect implements IExpressionObjectDialect {
    private final IExpressionObjectFactory JAVA8_TIME_EXPRESSION_OBJECTS_FACTORY;

    public Java8TimeDialect() {
        super("java8time");
        this.JAVA8_TIME_EXPRESSION_OBJECTS_FACTORY = new Java8TimeExpressionFactory();
    }

    @Override // org.thymeleaf.dialect.IExpressionObjectDialect
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return this.JAVA8_TIME_EXPRESSION_OBJECTS_FACTORY;
    }
}