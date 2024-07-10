package org.apache.catalina.valves.rewrite;

import java.nio.charset.Charset;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/rewrite/Resolver.class */
public abstract class Resolver {
    public abstract String resolve(String str);

    public abstract String resolveSsl(String str);

    public abstract String resolveHttp(String str);

    public abstract boolean resolveResource(int i, String str);

    public abstract Charset getUriCharset();

    public String resolveEnv(String key) {
        return System.getProperty(key);
    }
}