package org.apache.catalina.loader;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/loader/ResourceEntry.class */
public class ResourceEntry {
    public long lastModified = -1;
    public volatile Class<?> loadedClass = null;
}