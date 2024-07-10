package org.springframework.boot.autoconfigure.web.servlet;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.validation.DefaultMessageCodesResolver;

@ConfigurationProperties(prefix = "spring.mvc")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcProperties.class */
public class WebMvcProperties {
    private DefaultMessageCodesResolver.Format messageCodesResolverFormat;
    private Locale locale;
    private String dateFormat;
    private LocaleResolver localeResolver = LocaleResolver.ACCEPT_HEADER;
    private boolean dispatchTraceRequest = false;
    private boolean dispatchOptionsRequest = true;
    private boolean ignoreDefaultModelOnRedirect = true;
    private boolean throwExceptionIfNoHandlerFound = false;
    private boolean logResolvedException = false;
    private String staticPathPattern = "/**";
    private final Async async = new Async();
    private final Servlet servlet = new Servlet();
    private final View view = new View();
    private final Contentnegotiation contentnegotiation = new Contentnegotiation();
    private final Pathmatch pathmatch = new Pathmatch();

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcProperties$LocaleResolver.class */
    public enum LocaleResolver {
        FIXED,
        ACCEPT_HEADER
    }

    public DefaultMessageCodesResolver.Format getMessageCodesResolverFormat() {
        return this.messageCodesResolverFormat;
    }

    public void setMessageCodesResolverFormat(DefaultMessageCodesResolver.Format messageCodesResolverFormat) {
        this.messageCodesResolverFormat = messageCodesResolverFormat;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public LocaleResolver getLocaleResolver() {
        return this.localeResolver;
    }

    public void setLocaleResolver(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    public String getDateFormat() {
        return this.dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public boolean isIgnoreDefaultModelOnRedirect() {
        return this.ignoreDefaultModelOnRedirect;
    }

    public void setIgnoreDefaultModelOnRedirect(boolean ignoreDefaultModelOnRedirect) {
        this.ignoreDefaultModelOnRedirect = ignoreDefaultModelOnRedirect;
    }

    public boolean isThrowExceptionIfNoHandlerFound() {
        return this.throwExceptionIfNoHandlerFound;
    }

    public void setThrowExceptionIfNoHandlerFound(boolean throwExceptionIfNoHandlerFound) {
        this.throwExceptionIfNoHandlerFound = throwExceptionIfNoHandlerFound;
    }

    public boolean isLogResolvedException() {
        return this.logResolvedException;
    }

    public void setLogResolvedException(boolean logResolvedException) {
        this.logResolvedException = logResolvedException;
    }

    public boolean isDispatchOptionsRequest() {
        return this.dispatchOptionsRequest;
    }

    public void setDispatchOptionsRequest(boolean dispatchOptionsRequest) {
        this.dispatchOptionsRequest = dispatchOptionsRequest;
    }

    public boolean isDispatchTraceRequest() {
        return this.dispatchTraceRequest;
    }

    public void setDispatchTraceRequest(boolean dispatchTraceRequest) {
        this.dispatchTraceRequest = dispatchTraceRequest;
    }

    public String getStaticPathPattern() {
        return this.staticPathPattern;
    }

    public void setStaticPathPattern(String staticPathPattern) {
        this.staticPathPattern = staticPathPattern;
    }

    public Async getAsync() {
        return this.async;
    }

    public Servlet getServlet() {
        return this.servlet;
    }

    public View getView() {
        return this.view;
    }

    public Contentnegotiation getContentnegotiation() {
        return this.contentnegotiation;
    }

    public Pathmatch getPathmatch() {
        return this.pathmatch;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcProperties$Async.class */
    public static class Async {
        private Duration requestTimeout;

        public Duration getRequestTimeout() {
            return this.requestTimeout;
        }

        public void setRequestTimeout(Duration requestTimeout) {
            this.requestTimeout = requestTimeout;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcProperties$Servlet.class */
    public static class Servlet {
        private String path = "/";
        private int loadOnStartup = -1;

        public String getPath() {
            return this.path;
        }

        public void setPath(String path) {
            Assert.notNull(path, "Path must not be null");
            Assert.isTrue(!path.contains("*"), "Path must not contain wildcards");
            this.path = path;
        }

        public int getLoadOnStartup() {
            return this.loadOnStartup;
        }

        public void setLoadOnStartup(int loadOnStartup) {
            this.loadOnStartup = loadOnStartup;
        }

        public String getServletMapping() {
            if (this.path.equals("") || this.path.equals("/")) {
                return "/";
            }
            if (this.path.endsWith("/")) {
                return this.path + "*";
            }
            return this.path + "/*";
        }

        public String getPath(String path) {
            String prefix = getServletPrefix();
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            return prefix + path;
        }

        public String getServletPrefix() {
            String result = this.path;
            int index = result.indexOf(42);
            if (index != -1) {
                result = result.substring(0, index);
            }
            if (result.endsWith("/")) {
                result = result.substring(0, result.length() - 1);
            }
            return result;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcProperties$View.class */
    public static class View {
        private String prefix;
        private String suffix;

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
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcProperties$Contentnegotiation.class */
    public static class Contentnegotiation {
        private boolean favorPathExtension = false;
        private boolean favorParameter = false;
        private Map<String, MediaType> mediaTypes = new LinkedHashMap();
        private String parameterName;

        public boolean isFavorPathExtension() {
            return this.favorPathExtension;
        }

        public void setFavorPathExtension(boolean favorPathExtension) {
            this.favorPathExtension = favorPathExtension;
        }

        public boolean isFavorParameter() {
            return this.favorParameter;
        }

        public void setFavorParameter(boolean favorParameter) {
            this.favorParameter = favorParameter;
        }

        public Map<String, MediaType> getMediaTypes() {
            return this.mediaTypes;
        }

        public void setMediaTypes(Map<String, MediaType> mediaTypes) {
            this.mediaTypes = mediaTypes;
        }

        public String getParameterName() {
            return this.parameterName;
        }

        public void setParameterName(String parameterName) {
            this.parameterName = parameterName;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/servlet/WebMvcProperties$Pathmatch.class */
    public static class Pathmatch {
        private boolean useSuffixPattern = false;
        private boolean useRegisteredSuffixPattern = false;

        public boolean isUseSuffixPattern() {
            return this.useSuffixPattern;
        }

        public void setUseSuffixPattern(boolean useSuffixPattern) {
            this.useSuffixPattern = useSuffixPattern;
        }

        public boolean isUseRegisteredSuffixPattern() {
            return this.useRegisteredSuffixPattern;
        }

        public void setUseRegisteredSuffixPattern(boolean useRegisteredSuffixPattern) {
            this.useRegisteredSuffixPattern = useRegisteredSuffixPattern;
        }
    }
}