package org.springframework.boot.context.properties.bind;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/context/properties/bind/PlaceholdersResolver.class */
public interface PlaceholdersResolver {
    public static final PlaceholdersResolver NONE = value -> {
        return value;
    };

    Object resolvePlaceholders(Object value);
}