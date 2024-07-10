package org.springframework.boot.autoconfigure.cassandra;

import com.datastax.driver.core.Cluster;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cassandra/ClusterBuilderCustomizer.class */
public interface ClusterBuilderCustomizer {
    void customize(Cluster.Builder clusterBuilder);
}