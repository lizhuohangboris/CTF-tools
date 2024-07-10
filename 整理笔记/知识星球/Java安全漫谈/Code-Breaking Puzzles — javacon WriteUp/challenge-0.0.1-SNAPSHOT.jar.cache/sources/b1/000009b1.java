package org.apache.catalina.valves.rewrite;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/rewrite/RewriteMap.class */
public interface RewriteMap {
    String setParameters(String str);

    String lookup(String str);
}