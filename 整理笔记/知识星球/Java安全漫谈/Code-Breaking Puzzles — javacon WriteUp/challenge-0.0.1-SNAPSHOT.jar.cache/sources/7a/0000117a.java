package org.hibernate.validator.internal.util.classhierarchy;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/classhierarchy/Filters.class */
public class Filters {
    private static final Filter PROXY_FILTER = new WeldProxyFilter();
    private static final Filter INTERFACES_FILTER = new InterfacesFilter();

    private Filters() {
    }

    public static Filter excludeInterfaces() {
        return INTERFACES_FILTER;
    }

    public static Filter excludeProxies() {
        return PROXY_FILTER;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/classhierarchy/Filters$InterfacesFilter.class */
    private static class InterfacesFilter implements Filter {
        private InterfacesFilter() {
        }

        @Override // org.hibernate.validator.internal.util.classhierarchy.Filter
        public boolean accepts(Class<?> clazz) {
            return !clazz.isInterface();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/util/classhierarchy/Filters$WeldProxyFilter.class */
    private static class WeldProxyFilter implements Filter {
        private static final String WELD_PROXY_INTERFACE_NAME = "org.jboss.weld.bean.proxy.ProxyObject";

        private WeldProxyFilter() {
        }

        @Override // org.hibernate.validator.internal.util.classhierarchy.Filter
        public boolean accepts(Class<?> clazz) {
            return !isWeldProxy(clazz);
        }

        private boolean isWeldProxy(Class<?> clazz) {
            Class<?>[] interfaces;
            for (Class<?> implementedInterface : clazz.getInterfaces()) {
                if (implementedInterface.getName().equals(WELD_PROXY_INTERFACE_NAME)) {
                    return true;
                }
            }
            return false;
        }
    }
}