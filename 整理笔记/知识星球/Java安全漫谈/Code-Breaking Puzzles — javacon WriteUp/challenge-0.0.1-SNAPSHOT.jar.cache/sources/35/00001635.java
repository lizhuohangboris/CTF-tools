package org.springframework.boot.autoconfigure.data.neo4j;

import org.neo4j.ogm.config.AutoIndexMode;
import org.neo4j.ogm.config.Configuration;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ClassUtils;

@ConfigurationProperties(prefix = "spring.data.neo4j")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/neo4j/Neo4jProperties.class */
public class Neo4jProperties implements ApplicationContextAware {
    static final String EMBEDDED_DRIVER = "org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver";
    static final String HTTP_DRIVER = "org.neo4j.ogm.drivers.http.driver.HttpDriver";
    static final String DEFAULT_BOLT_URI = "bolt://localhost:7687";
    static final String BOLT_DRIVER = "org.neo4j.ogm.drivers.bolt.driver.BoltDriver";
    private String uri;
    private String username;
    private String password;
    private Boolean openInView;
    private AutoIndexMode autoIndex = AutoIndexMode.NONE;
    private final Embedded embedded = new Embedded();
    private ClassLoader classLoader = Neo4jProperties.class.getClassLoader();

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AutoIndexMode getAutoIndex() {
        return this.autoIndex;
    }

    public void setAutoIndex(AutoIndexMode autoIndex) {
        this.autoIndex = autoIndex;
    }

    public Boolean getOpenInView() {
        return this.openInView;
    }

    public void setOpenInView(Boolean openInView) {
        this.openInView = openInView;
    }

    public Embedded getEmbedded() {
        return this.embedded;
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.classLoader = ctx.getClassLoader();
    }

    public Configuration createConfiguration() {
        Configuration.Builder builder = new Configuration.Builder();
        configure(builder);
        return builder.build();
    }

    private void configure(Configuration.Builder builder) {
        if (this.uri != null) {
            builder.uri(this.uri);
        } else {
            configureUriWithDefaults(builder);
        }
        if (this.username != null && this.password != null) {
            builder.credentials(this.username, this.password);
        }
        builder.autoIndex(getAutoIndex().getName());
    }

    private void configureUriWithDefaults(Configuration.Builder builder) {
        if (!getEmbedded().isEnabled() || !ClassUtils.isPresent(EMBEDDED_DRIVER, this.classLoader)) {
            builder.uri(DEFAULT_BOLT_URI);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/neo4j/Neo4jProperties$Embedded.class */
    public static class Embedded {
        private boolean enabled = true;

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}