package org.springframework.context.annotation;

import org.springframework.beans.factory.parsing.Problem;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.core.type.MethodMetadata;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/BeanMethod.class */
public final class BeanMethod extends ConfigurationMethod {
    public BeanMethod(MethodMetadata metadata, ConfigurationClass configurationClass) {
        super(metadata, configurationClass);
    }

    @Override // org.springframework.context.annotation.ConfigurationMethod
    public void validate(ProblemReporter problemReporter) {
        if (!getMetadata().isStatic() && this.configurationClass.getMetadata().isAnnotated(Configuration.class.getName()) && !getMetadata().isOverridable()) {
            problemReporter.error(new NonOverridableMethodError());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/annotation/BeanMethod$NonOverridableMethodError.class */
    public class NonOverridableMethodError extends Problem {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public NonOverridableMethodError() {
            super(String.format("@Bean method '%s' must not be private or final; change the method's modifiers to continue", r8.getMetadata().getMethodName()), r8.getResourceLocation());
            BeanMethod.this = r8;
        }
    }
}