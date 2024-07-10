package org.apache.catalina;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/Globals.class */
public final class Globals {
    public static final String ALT_DD_ATTR = "org.apache.catalina.deploy.alt_dd";
    public static final String CERTIFICATES_ATTR = "javax.servlet.request.X509Certificate";
    public static final String CIPHER_SUITE_ATTR = "javax.servlet.request.cipher_suite";
    public static final String DISPATCHER_TYPE_ATTR = "org.apache.catalina.core.DISPATCHER_TYPE";
    public static final String DISPATCHER_REQUEST_PATH_ATTR = "org.apache.catalina.core.DISPATCHER_REQUEST_PATH";
    public static final String RESOURCES_ATTR = "org.apache.catalina.resources";
    public static final String CLASS_PATH_ATTR = "org.apache.catalina.jsp_classpath";
    public static final String KEY_SIZE_ATTR = "javax.servlet.request.key_size";
    public static final String SSL_SESSION_ID_ATTR = "javax.servlet.request.ssl_session_id";
    public static final String SSL_SESSION_MGR_ATTR = "javax.servlet.request.ssl_session_mgr";
    public static final String NAMED_DISPATCHER_ATTR = "org.apache.catalina.NAMED";
    @Deprecated
    public static final String SSI_FLAG_ATTR = "org.apache.catalina.ssi.SSIServlet";
    public static final String SUBJECT_ATTR = "javax.security.auth.subject";
    public static final String GSS_CREDENTIAL_ATTR = "org.apache.catalina.realm.GSS_CREDENTIAL";
    public static final String SENDFILE_SUPPORTED_ATTR = "org.apache.tomcat.sendfile.support";
    public static final String SENDFILE_FILENAME_ATTR = "org.apache.tomcat.sendfile.filename";
    public static final String SENDFILE_FILE_START_ATTR = "org.apache.tomcat.sendfile.start";
    public static final String SENDFILE_FILE_END_ATTR = "org.apache.tomcat.sendfile.end";
    public static final String REMOTE_ADDR_ATTRIBUTE = "org.apache.tomcat.remoteAddr";
    public static final String ASYNC_SUPPORTED_ATTR = "org.apache.catalina.ASYNC_SUPPORTED";
    public static final String PARAMETER_PARSE_FAILED_ATTR = "org.apache.catalina.parameter_parse_failed";
    public static final String PARAMETER_PARSE_FAILED_REASON_ATTR = "org.apache.catalina.parameter_parse_failed_reason";
    public static final boolean STRICT_SERVLET_COMPLIANCE = Boolean.parseBoolean(System.getProperty("org.apache.catalina.STRICT_SERVLET_COMPLIANCE", "false"));
    public static final boolean IS_SECURITY_ENABLED;
    public static final String DEFAULT_MBEAN_DOMAIN = "Catalina";
    public static final String CATALINA_HOME_PROP = "catalina.home";
    public static final String CATALINA_BASE_PROP = "catalina.base";
    public static final String JASPER_XML_VALIDATION_TLD_INIT_PARAM = "org.apache.jasper.XML_VALIDATE_TLD";
    public static final String JASPER_XML_BLOCK_EXTERNAL_INIT_PARAM = "org.apache.jasper.XML_BLOCK_EXTERNAL";
    public static final String CREDENTIAL_HANDLER = "org.apache.catalina.CredentialHandler";

    static {
        IS_SECURITY_ENABLED = System.getSecurityManager() != null;
    }
}