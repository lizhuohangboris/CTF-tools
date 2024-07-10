package org.springframework.boot.autoconfigure.web;

import ch.qos.logback.core.CoreConstants;
import java.io.File;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.apache.catalina.valves.Constants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.boot.web.server.Compression;
import org.springframework.boot.web.server.Http2;
import org.springframework.boot.web.server.Ssl;
import org.springframework.boot.web.servlet.server.Jsp;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties(prefix = "server", ignoreUnknownFields = true)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/ServerProperties.class */
public class ServerProperties {
    private Integer port;
    private InetAddress address;
    private Boolean useForwardHeaders;
    private String serverHeader;
    private Duration connectionTimeout;
    @NestedConfigurationProperty
    private Ssl ssl;
    @NestedConfigurationProperty
    private final ErrorProperties error = new ErrorProperties();
    private DataSize maxHttpHeaderSize = DataSize.ofKilobytes(8);
    @NestedConfigurationProperty
    private final Compression compression = new Compression();
    @NestedConfigurationProperty
    private final Http2 http2 = new Http2();
    private final Servlet servlet = new Servlet();
    private final Tomcat tomcat = new Tomcat();
    private final Jetty jetty = new Jetty();
    private final Undertow undertow = new Undertow();

    public Integer getPort() {
        return this.port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public InetAddress getAddress() {
        return this.address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public Boolean isUseForwardHeaders() {
        return this.useForwardHeaders;
    }

    public void setUseForwardHeaders(Boolean useForwardHeaders) {
        this.useForwardHeaders = useForwardHeaders;
    }

    public String getServerHeader() {
        return this.serverHeader;
    }

    public void setServerHeader(String serverHeader) {
        this.serverHeader = serverHeader;
    }

    public DataSize getMaxHttpHeaderSize() {
        return this.maxHttpHeaderSize;
    }

    public void setMaxHttpHeaderSize(DataSize maxHttpHeaderSize) {
        this.maxHttpHeaderSize = maxHttpHeaderSize;
    }

    public Duration getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public void setConnectionTimeout(Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public ErrorProperties getError() {
        return this.error;
    }

    public Ssl getSsl() {
        return this.ssl;
    }

    public void setSsl(Ssl ssl) {
        this.ssl = ssl;
    }

    public Compression getCompression() {
        return this.compression;
    }

    public Http2 getHttp2() {
        return this.http2;
    }

    public Servlet getServlet() {
        return this.servlet;
    }

    public Tomcat getTomcat() {
        return this.tomcat;
    }

    public Jetty getJetty() {
        return this.jetty;
    }

    public Undertow getUndertow() {
        return this.undertow;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/ServerProperties$Servlet.class */
    public static class Servlet {
        private String contextPath;
        private final Map<String, String> contextParameters = new HashMap();
        private String applicationDisplayName = "application";
        @NestedConfigurationProperty
        private final Jsp jsp = new Jsp();
        @NestedConfigurationProperty
        private final Session session = new Session();

        public String getContextPath() {
            return this.contextPath;
        }

        public void setContextPath(String contextPath) {
            this.contextPath = cleanContextPath(contextPath);
        }

        private String cleanContextPath(String contextPath) {
            if (StringUtils.hasText(contextPath) && contextPath.endsWith("/")) {
                return contextPath.substring(0, contextPath.length() - 1);
            }
            return contextPath;
        }

        public String getApplicationDisplayName() {
            return this.applicationDisplayName;
        }

        public void setApplicationDisplayName(String displayName) {
            this.applicationDisplayName = displayName;
        }

        public Map<String, String> getContextParameters() {
            return this.contextParameters;
        }

        public Jsp getJsp() {
            return this.jsp;
        }

        public Session getSession() {
            return this.session;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/ServerProperties$Tomcat.class */
    public static class Tomcat {
        private String protocolHeader;
        private String remoteIpHeader;
        private File basedir;
        private Boolean useRelativeRedirects;
        private final Accesslog accesslog = new Accesslog();
        private String internalProxies = "10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|192\\.168\\.\\d{1,3}\\.\\d{1,3}|169\\.254\\.\\d{1,3}\\.\\d{1,3}|127\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|172\\.1[6-9]{1}\\.\\d{1,3}\\.\\d{1,3}|172\\.2[0-9]{1}\\.\\d{1,3}\\.\\d{1,3}|172\\.3[0-1]{1}\\.\\d{1,3}\\.\\d{1,3}|0:0:0:0:0:0:0:1|::1";
        private String protocolHeaderHttpsValue = "https";
        private String portHeader = "X-Forwarded-Port";
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration backgroundProcessorDelay = Duration.ofSeconds(10);
        private int maxThreads = 200;
        private int minSpareThreads = 10;
        private DataSize maxHttpPostSize = DataSize.ofMegabytes(2);
        private DataSize maxHttpHeaderSize = DataSize.ofBytes(0);
        private DataSize maxSwallowSize = DataSize.ofMegabytes(2);
        private Boolean redirectContextRoot = true;
        private Charset uriEncoding = StandardCharsets.UTF_8;
        private int maxConnections = 10000;
        private int acceptCount = 100;
        private List<String> additionalTldSkipPatterns = new ArrayList();
        private final Resource resource = new Resource();

        public int getMaxThreads() {
            return this.maxThreads;
        }

        public void setMaxThreads(int maxThreads) {
            this.maxThreads = maxThreads;
        }

        public int getMinSpareThreads() {
            return this.minSpareThreads;
        }

        public void setMinSpareThreads(int minSpareThreads) {
            this.minSpareThreads = minSpareThreads;
        }

        public DataSize getMaxHttpPostSize() {
            return this.maxHttpPostSize;
        }

        public void setMaxHttpPostSize(DataSize maxHttpPostSize) {
            this.maxHttpPostSize = maxHttpPostSize;
        }

        public Accesslog getAccesslog() {
            return this.accesslog;
        }

        public Duration getBackgroundProcessorDelay() {
            return this.backgroundProcessorDelay;
        }

        public void setBackgroundProcessorDelay(Duration backgroundProcessorDelay) {
            this.backgroundProcessorDelay = backgroundProcessorDelay;
        }

        public File getBasedir() {
            return this.basedir;
        }

        public void setBasedir(File basedir) {
            this.basedir = basedir;
        }

        public String getInternalProxies() {
            return this.internalProxies;
        }

        public void setInternalProxies(String internalProxies) {
            this.internalProxies = internalProxies;
        }

        public String getProtocolHeader() {
            return this.protocolHeader;
        }

        public void setProtocolHeader(String protocolHeader) {
            this.protocolHeader = protocolHeader;
        }

        public String getProtocolHeaderHttpsValue() {
            return this.protocolHeaderHttpsValue;
        }

        public void setProtocolHeaderHttpsValue(String protocolHeaderHttpsValue) {
            this.protocolHeaderHttpsValue = protocolHeaderHttpsValue;
        }

        public String getPortHeader() {
            return this.portHeader;
        }

        public void setPortHeader(String portHeader) {
            this.portHeader = portHeader;
        }

        public Boolean getRedirectContextRoot() {
            return this.redirectContextRoot;
        }

        public void setRedirectContextRoot(Boolean redirectContextRoot) {
            this.redirectContextRoot = redirectContextRoot;
        }

        public Boolean getUseRelativeRedirects() {
            return this.useRelativeRedirects;
        }

        public void setUseRelativeRedirects(Boolean useRelativeRedirects) {
            this.useRelativeRedirects = useRelativeRedirects;
        }

        public String getRemoteIpHeader() {
            return this.remoteIpHeader;
        }

        public void setRemoteIpHeader(String remoteIpHeader) {
            this.remoteIpHeader = remoteIpHeader;
        }

        public Charset getUriEncoding() {
            return this.uriEncoding;
        }

        public void setUriEncoding(Charset uriEncoding) {
            this.uriEncoding = uriEncoding;
        }

        public int getMaxConnections() {
            return this.maxConnections;
        }

        public void setMaxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
        }

        @DeprecatedConfigurationProperty(replacement = "server.max-http-header-size")
        @Deprecated
        public DataSize getMaxHttpHeaderSize() {
            return this.maxHttpHeaderSize;
        }

        @Deprecated
        public void setMaxHttpHeaderSize(DataSize maxHttpHeaderSize) {
            this.maxHttpHeaderSize = maxHttpHeaderSize;
        }

        public DataSize getMaxSwallowSize() {
            return this.maxSwallowSize;
        }

        public void setMaxSwallowSize(DataSize maxSwallowSize) {
            this.maxSwallowSize = maxSwallowSize;
        }

        public int getAcceptCount() {
            return this.acceptCount;
        }

        public void setAcceptCount(int acceptCount) {
            this.acceptCount = acceptCount;
        }

        public List<String> getAdditionalTldSkipPatterns() {
            return this.additionalTldSkipPatterns;
        }

        public void setAdditionalTldSkipPatterns(List<String> additionalTldSkipPatterns) {
            this.additionalTldSkipPatterns = additionalTldSkipPatterns;
        }

        public Resource getResource() {
            return this.resource;
        }

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/ServerProperties$Tomcat$Accesslog.class */
        public static class Accesslog {
            private boolean enabled = false;
            private String pattern = Constants.AccessLog.COMMON_ALIAS;
            private String directory = "logs";
            protected String prefix = "access_log";
            private String suffix = ".log";
            private boolean rotate = true;
            private boolean renameOnRotate = false;
            private String fileDateFormat = ".yyyy-MM-dd";
            private boolean requestAttributesEnabled = false;
            private boolean buffered = true;

            public boolean isEnabled() {
                return this.enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public String getPattern() {
                return this.pattern;
            }

            public void setPattern(String pattern) {
                this.pattern = pattern;
            }

            public String getDirectory() {
                return this.directory;
            }

            public void setDirectory(String directory) {
                this.directory = directory;
            }

            public String getPrefix() {
                return this.prefix;
            }

            public void setPrefix(String prefix) {
                this.prefix = prefix;
            }

            public String getSuffix() {
                return this.suffix;
            }

            public void setSuffix(String suffix) {
                this.suffix = suffix;
            }

            public boolean isRotate() {
                return this.rotate;
            }

            public void setRotate(boolean rotate) {
                this.rotate = rotate;
            }

            public boolean isRenameOnRotate() {
                return this.renameOnRotate;
            }

            public void setRenameOnRotate(boolean renameOnRotate) {
                this.renameOnRotate = renameOnRotate;
            }

            public String getFileDateFormat() {
                return this.fileDateFormat;
            }

            public void setFileDateFormat(String fileDateFormat) {
                this.fileDateFormat = fileDateFormat;
            }

            public boolean isRequestAttributesEnabled() {
                return this.requestAttributesEnabled;
            }

            public void setRequestAttributesEnabled(boolean requestAttributesEnabled) {
                this.requestAttributesEnabled = requestAttributesEnabled;
            }

            public boolean isBuffered() {
                return this.buffered;
            }

            public void setBuffered(boolean buffered) {
                this.buffered = buffered;
            }
        }

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/ServerProperties$Tomcat$Resource.class */
        public static class Resource {
            private boolean allowCaching = true;
            private Duration cacheTtl;

            public boolean isAllowCaching() {
                return this.allowCaching;
            }

            public void setAllowCaching(boolean allowCaching) {
                this.allowCaching = allowCaching;
            }

            public Duration getCacheTtl() {
                return this.cacheTtl;
            }

            public void setCacheTtl(Duration cacheTtl) {
                this.cacheTtl = cacheTtl;
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/ServerProperties$Jetty.class */
    public static class Jetty {
        private final Accesslog accesslog = new Accesslog();
        private DataSize maxHttpPostSize = DataSize.ofBytes(200000);
        private Integer acceptors = -1;
        private Integer selectors = -1;

        public Accesslog getAccesslog() {
            return this.accesslog;
        }

        public DataSize getMaxHttpPostSize() {
            return this.maxHttpPostSize;
        }

        public void setMaxHttpPostSize(DataSize maxHttpPostSize) {
            this.maxHttpPostSize = maxHttpPostSize;
        }

        public Integer getAcceptors() {
            return this.acceptors;
        }

        public void setAcceptors(Integer acceptors) {
            this.acceptors = acceptors;
        }

        public Integer getSelectors() {
            return this.selectors;
        }

        public void setSelectors(Integer selectors) {
            this.selectors = selectors;
        }

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/ServerProperties$Jetty$Accesslog.class */
        public static class Accesslog {
            private String filename;
            private String fileDateFormat;
            private boolean append;
            private boolean extendedFormat;
            private Locale locale;
            private boolean logCookies;
            private boolean logServer;
            private boolean logLatency;
            private boolean enabled = false;
            private int retentionPeriod = 31;
            private String dateFormat = CoreConstants.CLF_DATE_PATTERN;
            private TimeZone timeZone = TimeZone.getTimeZone("GMT");

            public boolean isEnabled() {
                return this.enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public String getFilename() {
                return this.filename;
            }

            public void setFilename(String filename) {
                this.filename = filename;
            }

            public String getFileDateFormat() {
                return this.fileDateFormat;
            }

            public void setFileDateFormat(String fileDateFormat) {
                this.fileDateFormat = fileDateFormat;
            }

            public int getRetentionPeriod() {
                return this.retentionPeriod;
            }

            public void setRetentionPeriod(int retentionPeriod) {
                this.retentionPeriod = retentionPeriod;
            }

            public boolean isAppend() {
                return this.append;
            }

            public void setAppend(boolean append) {
                this.append = append;
            }

            public boolean isExtendedFormat() {
                return this.extendedFormat;
            }

            public void setExtendedFormat(boolean extendedFormat) {
                this.extendedFormat = extendedFormat;
            }

            public String getDateFormat() {
                return this.dateFormat;
            }

            public void setDateFormat(String dateFormat) {
                this.dateFormat = dateFormat;
            }

            public Locale getLocale() {
                return this.locale;
            }

            public void setLocale(Locale locale) {
                this.locale = locale;
            }

            public TimeZone getTimeZone() {
                return this.timeZone;
            }

            public void setTimeZone(TimeZone timeZone) {
                this.timeZone = timeZone;
            }

            public boolean isLogCookies() {
                return this.logCookies;
            }

            public void setLogCookies(boolean logCookies) {
                this.logCookies = logCookies;
            }

            public boolean isLogServer() {
                return this.logServer;
            }

            public void setLogServer(boolean logServer) {
                this.logServer = logServer;
            }

            public boolean isLogLatency() {
                return this.logLatency;
            }

            public void setLogLatency(boolean logLatency) {
                this.logLatency = logLatency;
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/ServerProperties$Undertow.class */
    public static class Undertow {
        private DataSize bufferSize;
        private Integer ioThreads;
        private Integer workerThreads;
        private Boolean directBuffers;
        private DataSize maxHttpPostSize = DataSize.ofBytes(-1);
        private boolean eagerFilterInit = true;
        private final Accesslog accesslog = new Accesslog();

        public DataSize getMaxHttpPostSize() {
            return this.maxHttpPostSize;
        }

        public void setMaxHttpPostSize(DataSize maxHttpPostSize) {
            this.maxHttpPostSize = maxHttpPostSize;
        }

        public DataSize getBufferSize() {
            return this.bufferSize;
        }

        public void setBufferSize(DataSize bufferSize) {
            this.bufferSize = bufferSize;
        }

        public Integer getIoThreads() {
            return this.ioThreads;
        }

        public void setIoThreads(Integer ioThreads) {
            this.ioThreads = ioThreads;
        }

        public Integer getWorkerThreads() {
            return this.workerThreads;
        }

        public void setWorkerThreads(Integer workerThreads) {
            this.workerThreads = workerThreads;
        }

        public Boolean getDirectBuffers() {
            return this.directBuffers;
        }

        public void setDirectBuffers(Boolean directBuffers) {
            this.directBuffers = directBuffers;
        }

        public boolean isEagerFilterInit() {
            return this.eagerFilterInit;
        }

        public void setEagerFilterInit(boolean eagerFilterInit) {
            this.eagerFilterInit = eagerFilterInit;
        }

        public Accesslog getAccesslog() {
            return this.accesslog;
        }

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/ServerProperties$Undertow$Accesslog.class */
        public static class Accesslog {
            private boolean enabled = false;
            private String pattern = Constants.AccessLog.COMMON_ALIAS;
            protected String prefix = "access_log.";
            private String suffix = "log";
            private File dir = new File("logs");
            private boolean rotate = true;

            public boolean isEnabled() {
                return this.enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public String getPattern() {
                return this.pattern;
            }

            public void setPattern(String pattern) {
                this.pattern = pattern;
            }

            public String getPrefix() {
                return this.prefix;
            }

            public void setPrefix(String prefix) {
                this.prefix = prefix;
            }

            public String getSuffix() {
                return this.suffix;
            }

            public void setSuffix(String suffix) {
                this.suffix = suffix;
            }

            public File getDir() {
                return this.dir;
            }

            public void setDir(File dir) {
                this.dir = dir;
            }

            public boolean isRotate() {
                return this.rotate;
            }

            public void setRotate(boolean rotate) {
                this.rotate = rotate;
            }
        }
    }
}