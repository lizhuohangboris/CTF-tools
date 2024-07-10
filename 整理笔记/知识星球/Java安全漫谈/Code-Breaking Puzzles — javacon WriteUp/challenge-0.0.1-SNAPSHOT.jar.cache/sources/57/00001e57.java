package org.springframework.core.env;

import java.util.function.Predicate;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/env/Profiles.class */
public interface Profiles {
    boolean matches(Predicate<String> predicate);

    static Profiles of(String... profiles) {
        return ProfilesParser.parse(profiles);
    }
}