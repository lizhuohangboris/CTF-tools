package javax.servlet;

import java.util.Collection;
import java.util.EnumSet;
import javax.servlet.Registration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/FilterRegistration.class */
public interface FilterRegistration extends Registration {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/FilterRegistration$Dynamic.class */
    public interface Dynamic extends FilterRegistration, Registration.Dynamic {
    }

    void addMappingForServletNames(EnumSet<DispatcherType> enumSet, boolean z, String... strArr);

    Collection<String> getServletNameMappings();

    void addMappingForUrlPatterns(EnumSet<DispatcherType> enumSet, boolean z, String... strArr);

    Collection<String> getUrlPatternMappings();
}