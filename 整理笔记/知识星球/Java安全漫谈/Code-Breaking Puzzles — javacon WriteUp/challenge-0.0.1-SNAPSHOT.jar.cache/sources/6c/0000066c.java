package javax.servlet.http;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/http/HttpServletMapping.class */
public interface HttpServletMapping {
    String getMatchValue();

    String getPattern();

    String getServletName();

    MappingMatch getMappingMatch();
}