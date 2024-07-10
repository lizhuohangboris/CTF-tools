package org.apache.catalina;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/WebResourceRoot.class */
public interface WebResourceRoot extends Lifecycle {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/WebResourceRoot$ResourceSetType.class */
    public enum ResourceSetType {
        PRE,
        RESOURCE_JAR,
        POST,
        CLASSES_JAR
    }

    WebResource getResource(String str);

    WebResource[] getResources(String str);

    WebResource getClassLoaderResource(String str);

    WebResource[] getClassLoaderResources(String str);

    String[] list(String str);

    Set<String> listWebAppPaths(String str);

    WebResource[] listResources(String str);

    boolean mkdir(String str);

    boolean write(String str, InputStream inputStream, boolean z);

    void createWebResourceSet(ResourceSetType resourceSetType, String str, URL url, String str2);

    void createWebResourceSet(ResourceSetType resourceSetType, String str, String str2, String str3, String str4);

    void addPreResources(WebResourceSet webResourceSet);

    WebResourceSet[] getPreResources();

    void addJarResources(WebResourceSet webResourceSet);

    WebResourceSet[] getJarResources();

    void addPostResources(WebResourceSet webResourceSet);

    WebResourceSet[] getPostResources();

    Context getContext();

    void setContext(Context context);

    void setAllowLinking(boolean z);

    boolean getAllowLinking();

    void setCachingAllowed(boolean z);

    boolean isCachingAllowed();

    void setCacheTtl(long j);

    long getCacheTtl();

    void setCacheMaxSize(long j);

    long getCacheMaxSize();

    void setCacheObjectMaxSize(int i);

    int getCacheObjectMaxSize();

    void setTrackLockedFiles(boolean z);

    boolean getTrackLockedFiles();

    void backgroundProcess();

    void registerTrackedResource(TrackedWebResource trackedWebResource);

    void deregisterTrackedResource(TrackedWebResource trackedWebResource);

    List<URL> getBaseUrls();

    void gc();
}