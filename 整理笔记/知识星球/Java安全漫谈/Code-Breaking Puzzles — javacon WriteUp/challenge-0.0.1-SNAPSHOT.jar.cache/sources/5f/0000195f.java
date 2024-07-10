package org.springframework.boot.convert;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.util.unit.DataUnit;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/convert/DataSizeUnit.class */
public @interface DataSizeUnit {
    DataUnit value();
}