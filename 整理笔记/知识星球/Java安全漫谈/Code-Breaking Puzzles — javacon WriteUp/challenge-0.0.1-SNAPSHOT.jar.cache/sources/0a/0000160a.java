package org.springframework.boot.autoconfigure.data.couchbase;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.cluster.ClusterInfo;
import com.couchbase.client.java.env.CouchbaseEnvironment;
import org.springframework.data.couchbase.config.CouchbaseConfigurer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/couchbase/SpringBootCouchbaseConfigurer.class */
public class SpringBootCouchbaseConfigurer implements CouchbaseConfigurer {
    private final CouchbaseEnvironment env;
    private final Cluster cluster;
    private final ClusterInfo clusterInfo;
    private final Bucket bucket;

    public SpringBootCouchbaseConfigurer(CouchbaseEnvironment env, Cluster cluster, ClusterInfo clusterInfo, Bucket bucket) {
        this.env = env;
        this.cluster = cluster;
        this.clusterInfo = clusterInfo;
        this.bucket = bucket;
    }

    public CouchbaseEnvironment couchbaseEnvironment() throws Exception {
        return this.env;
    }

    public Cluster couchbaseCluster() throws Exception {
        return this.cluster;
    }

    public ClusterInfo couchbaseClusterInfo() throws Exception {
        return this.clusterInfo;
    }

    public Bucket couchbaseClient() throws Exception {
        return this.bucket;
    }
}