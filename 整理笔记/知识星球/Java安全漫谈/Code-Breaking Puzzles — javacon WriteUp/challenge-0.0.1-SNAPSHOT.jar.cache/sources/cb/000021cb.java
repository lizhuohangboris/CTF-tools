package org.springframework.jmx.export.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.jmx.support.MetricType;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/export/annotation/ManagedMetric.class */
public @interface ManagedMetric {
    String category() default "";

    int currencyTimeLimit() default -1;

    String description() default "";

    String displayName() default "";

    MetricType metricType() default MetricType.GAUGE;

    int persistPeriod() default -1;

    String persistPolicy() default "";

    String unit() default "";
}