package org.hibernate.validator.internal.engine;

import java.time.Clock;
import javax.validation.ClockProvider;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/engine/DefaultClockProvider.class */
public class DefaultClockProvider implements ClockProvider {
    public static final DefaultClockProvider INSTANCE = new DefaultClockProvider();

    private DefaultClockProvider() {
    }

    @Override // javax.validation.ClockProvider
    public Clock getClock() {
        return Clock.systemDefaultZone();
    }
}