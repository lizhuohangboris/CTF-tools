package org.springframework.boot.autoconfigure.security;

import ch.qos.logback.classic.ClassicConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.DispatcherType;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "spring.security")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/SecurityProperties.class */
public class SecurityProperties {
    public static final int BASIC_AUTH_ORDER = 2147483642;
    public static final int IGNORED_ORDER = Integer.MIN_VALUE;
    public static final int DEFAULT_FILTER_ORDER = -100;
    private final Filter filter = new Filter();
    private User user = new User();

    public User getUser() {
        return this.user;
    }

    public Filter getFilter() {
        return this.filter;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/SecurityProperties$Filter.class */
    public static class Filter {
        private int order = -100;
        private Set<DispatcherType> dispatcherTypes = new HashSet(Arrays.asList(DispatcherType.ASYNC, DispatcherType.ERROR, DispatcherType.REQUEST));

        public int getOrder() {
            return this.order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public Set<DispatcherType> getDispatcherTypes() {
            return this.dispatcherTypes;
        }

        public void setDispatcherTypes(Set<DispatcherType> dispatcherTypes) {
            this.dispatcherTypes = dispatcherTypes;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/security/SecurityProperties$User.class */
    public static class User {
        private String name = ClassicConstants.USER_MDC_KEY;
        private String password = UUID.randomUUID().toString();
        private List<String> roles = new ArrayList();
        private boolean passwordGenerated = true;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return this.password;
        }

        public void setPassword(String password) {
            if (!StringUtils.hasLength(password)) {
                return;
            }
            this.passwordGenerated = false;
            this.password = password;
        }

        public List<String> getRoles() {
            return this.roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = new ArrayList(roles);
        }

        public boolean isPasswordGenerated() {
            return this.passwordGenerated;
        }
    }
}