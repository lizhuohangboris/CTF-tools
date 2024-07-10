package org.springframework.boot.autoconfigure.ldap;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

@ConfigurationProperties(prefix = "spring.ldap")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/ldap/LdapProperties.class */
public class LdapProperties {
    private static final int DEFAULT_PORT = 389;
    private String[] urls;
    private String base;
    private String username;
    private String password;
    private boolean anonymousReadOnly;
    private final Map<String, String> baseEnvironment = new HashMap();

    public String[] getUrls() {
        return this.urls;
    }

    public void setUrls(String[] urls) {
        this.urls = urls;
    }

    public String getBase() {
        return this.base;
    }

    public void setBase(String base) {
        this.base = base;
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

    public boolean getAnonymousReadOnly() {
        return this.anonymousReadOnly;
    }

    public void setAnonymousReadOnly(boolean anonymousReadOnly) {
        this.anonymousReadOnly = anonymousReadOnly;
    }

    public Map<String, String> getBaseEnvironment() {
        return this.baseEnvironment;
    }

    public String[] determineUrls(Environment environment) {
        return ObjectUtils.isEmpty((Object[]) this.urls) ? new String[]{"ldap://localhost:" + determinePort(environment)} : this.urls;
    }

    private int determinePort(Environment environment) {
        Assert.notNull(environment, "Environment must not be null");
        String localPort = environment.getProperty("local.ldap.port");
        if (localPort != null) {
            return Integer.valueOf(localPort).intValue();
        }
        return DEFAULT_PORT;
    }
}