package org.apache.catalina.filters;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/filters/Constants.class */
public final class Constants {
    public static final String CSRF_NONCE_SESSION_ATTR_NAME = "org.apache.catalina.filters.CSRF_NONCE";
    public static final String CSRF_NONCE_REQUEST_PARAM = "org.apache.catalina.filters.CSRF_NONCE";
    public static final String METHOD_GET = "GET";
    public static final String CSRF_REST_NONCE_HEADER_NAME = "X-CSRF-Token";
    public static final String CSRF_REST_NONCE_HEADER_FETCH_VALUE = "Fetch";
    public static final String CSRF_REST_NONCE_HEADER_REQUIRED_VALUE = "Required";
    public static final String CSRF_REST_NONCE_SESSION_ATTR_NAME = "org.apache.catalina.filters.CSRF_REST_NONCE";
}