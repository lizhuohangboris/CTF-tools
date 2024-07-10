package org.springframework.boot.logging.logback;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.CoreConstants;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/logging/logback/WhitespaceThrowableProxyConverter.class */
public class WhitespaceThrowableProxyConverter extends ThrowableProxyConverter {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // ch.qos.logback.classic.pattern.ThrowableProxyConverter
    public String throwableProxyToString(IThrowableProxy tp) {
        return CoreConstants.LINE_SEPARATOR + super.throwableProxyToString(tp) + CoreConstants.LINE_SEPARATOR;
    }
}