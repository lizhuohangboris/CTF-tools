package org.springframework.boot;

import java.util.List;
import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/ApplicationArguments.class */
public interface ApplicationArguments {
    String[] getSourceArgs();

    Set<String> getOptionNames();

    boolean containsOption(String name);

    List<String> getOptionValues(String name);

    List<String> getNonOptionArgs();
}