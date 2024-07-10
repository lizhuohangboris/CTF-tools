package org.springframework.core;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/DefaultParameterNameDiscoverer.class */
public class DefaultParameterNameDiscoverer extends PrioritizedParameterNameDiscoverer {
    public DefaultParameterNameDiscoverer() {
        if (!GraalDetector.inImageCode()) {
            if (KotlinDetector.isKotlinReflectPresent()) {
                addDiscoverer(new KotlinReflectionParameterNameDiscoverer());
            }
            addDiscoverer(new StandardReflectionParameterNameDiscoverer());
            addDiscoverer(new LocalVariableTableParameterNameDiscoverer());
        }
    }
}