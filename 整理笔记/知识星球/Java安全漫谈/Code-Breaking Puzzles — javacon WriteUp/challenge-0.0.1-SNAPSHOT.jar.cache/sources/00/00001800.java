package org.springframework.boot.autoconfigure.session;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.boot.web.servlet.DispatcherType;

@ConfigurationProperties(prefix = "spring.session")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/SessionProperties.class */
public class SessionProperties {
    private StoreType storeType;
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration timeout;
    private Servlet servlet = new Servlet();
    private final ServerProperties serverProperties;

    public SessionProperties(ObjectProvider<ServerProperties> serverProperties) {
        this.serverProperties = serverProperties.getIfUnique();
    }

    @PostConstruct
    public void checkSessionTimeout() {
        if (this.timeout == null && this.serverProperties != null) {
            this.timeout = this.serverProperties.getServlet().getSession().getTimeout();
        }
    }

    public StoreType getStoreType() {
        return this.storeType;
    }

    public void setStoreType(StoreType storeType) {
        this.storeType = storeType;
    }

    public Duration getTimeout() {
        return this.timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public Servlet getServlet() {
        return this.servlet;
    }

    public void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/SessionProperties$Servlet.class */
    public static class Servlet {
        private int filterOrder = -2147483598;
        private Set<DispatcherType> filterDispatcherTypes = new HashSet(Arrays.asList(DispatcherType.ASYNC, DispatcherType.ERROR, DispatcherType.REQUEST));

        public int getFilterOrder() {
            return this.filterOrder;
        }

        public void setFilterOrder(int filterOrder) {
            this.filterOrder = filterOrder;
        }

        public Set<DispatcherType> getFilterDispatcherTypes() {
            return this.filterDispatcherTypes;
        }

        public void setFilterDispatcherTypes(Set<DispatcherType> filterDispatcherTypes) {
            this.filterDispatcherTypes = filterDispatcherTypes;
        }
    }
}