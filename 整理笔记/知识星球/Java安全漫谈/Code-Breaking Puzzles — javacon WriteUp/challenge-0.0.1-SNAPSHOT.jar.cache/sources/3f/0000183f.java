package org.springframework.boot.autoconfigure.web;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.http.CacheControl;

@ConfigurationProperties(prefix = "spring.resources", ignoreUnknownFields = false)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/ResourceProperties.class */
public class ResourceProperties {
    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {"classpath:/META-INF/resources/", "classpath:/resources/", "classpath:/static/", "classpath:/public/"};
    private String[] staticLocations = CLASSPATH_RESOURCE_LOCATIONS;
    private boolean addMappings = true;
    private final Chain chain = new Chain();
    private final Cache cache = new Cache();

    public String[] getStaticLocations() {
        return this.staticLocations;
    }

    public void setStaticLocations(String[] staticLocations) {
        this.staticLocations = appendSlashIfNecessary(staticLocations);
    }

    private String[] appendSlashIfNecessary(String[] staticLocations) {
        String[] normalized = new String[staticLocations.length];
        for (int i = 0; i < staticLocations.length; i++) {
            String location = staticLocations[i];
            normalized[i] = location.endsWith("/") ? location : location + "/";
        }
        return normalized;
    }

    public boolean isAddMappings() {
        return this.addMappings;
    }

    public void setAddMappings(boolean addMappings) {
        this.addMappings = addMappings;
    }

    public Chain getChain() {
        return this.chain;
    }

    public Cache getCache() {
        return this.cache;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/ResourceProperties$Chain.class */
    public static class Chain {
        private Boolean enabled;
        private boolean cache = true;
        private boolean htmlApplicationCache = false;
        private boolean compressed = false;
        private final Strategy strategy = new Strategy();

        public Boolean getEnabled() {
            return getEnabled(getStrategy().getFixed().isEnabled(), getStrategy().getContent().isEnabled(), this.enabled);
        }

        public void setEnabled(boolean enabled) {
            this.enabled = Boolean.valueOf(enabled);
        }

        public boolean isCache() {
            return this.cache;
        }

        public void setCache(boolean cache) {
            this.cache = cache;
        }

        public Strategy getStrategy() {
            return this.strategy;
        }

        public boolean isHtmlApplicationCache() {
            return this.htmlApplicationCache;
        }

        public void setHtmlApplicationCache(boolean htmlApplicationCache) {
            this.htmlApplicationCache = htmlApplicationCache;
        }

        public boolean isCompressed() {
            return this.compressed;
        }

        public void setCompressed(boolean compressed) {
            this.compressed = compressed;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static Boolean getEnabled(boolean fixedEnabled, boolean contentEnabled, Boolean chainEnabled) {
            return (fixedEnabled || contentEnabled) ? Boolean.TRUE : chainEnabled;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/ResourceProperties$Strategy.class */
    public static class Strategy {
        private final Fixed fixed = new Fixed();
        private final Content content = new Content();

        public Fixed getFixed() {
            return this.fixed;
        }

        public Content getContent() {
            return this.content;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/ResourceProperties$Content.class */
    public static class Content {
        private boolean enabled;
        private String[] paths = {"/**"};

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String[] getPaths() {
            return this.paths;
        }

        public void setPaths(String[] paths) {
            this.paths = paths;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/ResourceProperties$Fixed.class */
    public static class Fixed {
        private boolean enabled;
        private String[] paths = {"/**"};
        private String version;

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String[] getPaths() {
            return this.paths;
        }

        public void setPaths(String[] paths) {
            this.paths = paths;
        }

        public String getVersion() {
            return this.version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/ResourceProperties$Cache.class */
    public static class Cache {
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration period;
        private final Cachecontrol cachecontrol = new Cachecontrol();

        public Duration getPeriod() {
            return this.period;
        }

        public void setPeriod(Duration period) {
            this.period = period;
        }

        public Cachecontrol getCachecontrol() {
            return this.cachecontrol;
        }

        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/ResourceProperties$Cache$Cachecontrol.class */
        public static class Cachecontrol {
            @DurationUnit(ChronoUnit.SECONDS)
            private Duration maxAge;
            private Boolean noCache;
            private Boolean noStore;
            private Boolean mustRevalidate;
            private Boolean noTransform;
            private Boolean cachePublic;
            private Boolean cachePrivate;
            private Boolean proxyRevalidate;
            @DurationUnit(ChronoUnit.SECONDS)
            private Duration staleWhileRevalidate;
            @DurationUnit(ChronoUnit.SECONDS)
            private Duration staleIfError;
            @DurationUnit(ChronoUnit.SECONDS)
            private Duration sMaxAge;

            public Duration getMaxAge() {
                return this.maxAge;
            }

            public void setMaxAge(Duration maxAge) {
                this.maxAge = maxAge;
            }

            public Boolean getNoCache() {
                return this.noCache;
            }

            public void setNoCache(Boolean noCache) {
                this.noCache = noCache;
            }

            public Boolean getNoStore() {
                return this.noStore;
            }

            public void setNoStore(Boolean noStore) {
                this.noStore = noStore;
            }

            public Boolean getMustRevalidate() {
                return this.mustRevalidate;
            }

            public void setMustRevalidate(Boolean mustRevalidate) {
                this.mustRevalidate = mustRevalidate;
            }

            public Boolean getNoTransform() {
                return this.noTransform;
            }

            public void setNoTransform(Boolean noTransform) {
                this.noTransform = noTransform;
            }

            public Boolean getCachePublic() {
                return this.cachePublic;
            }

            public void setCachePublic(Boolean cachePublic) {
                this.cachePublic = cachePublic;
            }

            public Boolean getCachePrivate() {
                return this.cachePrivate;
            }

            public void setCachePrivate(Boolean cachePrivate) {
                this.cachePrivate = cachePrivate;
            }

            public Boolean getProxyRevalidate() {
                return this.proxyRevalidate;
            }

            public void setProxyRevalidate(Boolean proxyRevalidate) {
                this.proxyRevalidate = proxyRevalidate;
            }

            public Duration getStaleWhileRevalidate() {
                return this.staleWhileRevalidate;
            }

            public void setStaleWhileRevalidate(Duration staleWhileRevalidate) {
                this.staleWhileRevalidate = staleWhileRevalidate;
            }

            public Duration getStaleIfError() {
                return this.staleIfError;
            }

            public void setStaleIfError(Duration staleIfError) {
                this.staleIfError = staleIfError;
            }

            public Duration getSMaxAge() {
                return this.sMaxAge;
            }

            public void setSMaxAge(Duration sMaxAge) {
                this.sMaxAge = sMaxAge;
            }

            public CacheControl toHttpCacheControl() {
                PropertyMapper map = PropertyMapper.get();
                CacheControl control = createCacheControl();
                PropertyMapper.Source whenTrue = map.from(this::getMustRevalidate).whenTrue();
                control.getClass();
                whenTrue.toCall(this::mustRevalidate);
                PropertyMapper.Source whenTrue2 = map.from(this::getNoTransform).whenTrue();
                control.getClass();
                whenTrue2.toCall(this::noTransform);
                PropertyMapper.Source whenTrue3 = map.from(this::getCachePublic).whenTrue();
                control.getClass();
                whenTrue3.toCall(this::cachePublic);
                PropertyMapper.Source whenTrue4 = map.from(this::getCachePrivate).whenTrue();
                control.getClass();
                whenTrue4.toCall(this::cachePrivate);
                PropertyMapper.Source whenTrue5 = map.from(this::getProxyRevalidate).whenTrue();
                control.getClass();
                whenTrue5.toCall(this::proxyRevalidate);
                map.from(this::getStaleWhileRevalidate).whenNonNull().to(duration -> {
                    control.staleWhileRevalidate(duration.getSeconds(), TimeUnit.SECONDS);
                });
                map.from(this::getStaleIfError).whenNonNull().to(duration2 -> {
                    control.staleIfError(duration2.getSeconds(), TimeUnit.SECONDS);
                });
                map.from(this::getSMaxAge).whenNonNull().to(duration3 -> {
                    control.sMaxAge(duration3.getSeconds(), TimeUnit.SECONDS);
                });
                return control;
            }

            private CacheControl createCacheControl() {
                if (Boolean.TRUE.equals(this.noStore)) {
                    return CacheControl.noStore();
                }
                if (Boolean.TRUE.equals(this.noCache)) {
                    return CacheControl.noCache();
                }
                if (this.maxAge != null) {
                    return CacheControl.maxAge(this.maxAge.getSeconds(), TimeUnit.SECONDS);
                }
                return CacheControl.empty();
            }
        }
    }
}