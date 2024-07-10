package org.thymeleaf.spring5.expression;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.exceptions.TemplateProcessingException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/expression/SpringStandardExpressions.class */
public final class SpringStandardExpressions {
    public static final String ENABLE_SPRING_EL_COMPILER_ATTRIBUTE_NAME = "EnableSpringELCompiler";

    private SpringStandardExpressions() {
    }

    public static boolean isSpringELCompilerEnabled(IEngineConfiguration configuration) {
        Object enableSpringELCompiler = configuration.getExecutionAttributes().get(ENABLE_SPRING_EL_COMPILER_ATTRIBUTE_NAME);
        if (enableSpringELCompiler == null) {
            return false;
        }
        if (!(enableSpringELCompiler instanceof Boolean)) {
            throw new TemplateProcessingException("A value for the \"EnableSpringELCompiler\" execution attribute has been specified, but it is not of the required type Boolean. (" + enableSpringELCompiler.getClass().getName() + ")");
        }
        return ((Boolean) enableSpringELCompiler).booleanValue();
    }
}