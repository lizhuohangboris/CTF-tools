package javax.servlet.http;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/http/HttpUpgradeHandler.class */
public interface HttpUpgradeHandler {
    void init(WebConnection webConnection);

    void destroy();
}