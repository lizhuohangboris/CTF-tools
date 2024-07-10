package javax.servlet;

import java.util.Collection;
import java.util.Set;
import javax.servlet.Registration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/ServletRegistration.class */
public interface ServletRegistration extends Registration {

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/ServletRegistration$Dynamic.class */
    public interface Dynamic extends ServletRegistration, Registration.Dynamic {
        void setLoadOnStartup(int i);

        Set<String> setServletSecurity(ServletSecurityElement servletSecurityElement);

        void setMultipartConfig(MultipartConfigElement multipartConfigElement);

        void setRunAsRole(String str);
    }

    Set<String> addMapping(String... strArr);

    Collection<String> getMappings();

    String getRunAsRole();
}