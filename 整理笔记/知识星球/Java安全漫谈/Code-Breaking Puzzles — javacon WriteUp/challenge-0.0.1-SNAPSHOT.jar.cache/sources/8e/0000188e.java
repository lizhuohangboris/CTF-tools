package org.springframework.boot.autoconfigure.web.servlet;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.core.Ordered;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/web/servlet/ServletWebServerFactoryCustomizer.class */
public class ServletWebServerFactoryCustomizer implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>, Ordered {
    private final ServerProperties serverProperties;

    public ServletWebServerFactoryCustomizer(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return 0;
    }

    @Override // org.springframework.boot.web.server.WebServerFactoryCustomizer
    public void customize(ConfigurableServletWebServerFactory factory) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        ServerProperties serverProperties = this.serverProperties;
        serverProperties.getClass();
        PropertyMapper.Source from = map.from(this::getPort);
        factory.getClass();
        from.to((v1) -> {
            r1.setPort(v1);
        });
        ServerProperties serverProperties2 = this.serverProperties;
        serverProperties2.getClass();
        PropertyMapper.Source from2 = map.from(this::getAddress);
        factory.getClass();
        from2.to(this::setAddress);
        ServerProperties.Servlet servlet = this.serverProperties.getServlet();
        servlet.getClass();
        PropertyMapper.Source from3 = map.from(this::getContextPath);
        factory.getClass();
        from3.to(this::setContextPath);
        ServerProperties.Servlet servlet2 = this.serverProperties.getServlet();
        servlet2.getClass();
        PropertyMapper.Source from4 = map.from(this::getApplicationDisplayName);
        factory.getClass();
        from4.to(this::setDisplayName);
        ServerProperties.Servlet servlet3 = this.serverProperties.getServlet();
        servlet3.getClass();
        PropertyMapper.Source from5 = map.from(this::getSession);
        factory.getClass();
        from5.to(this::setSession);
        ServerProperties serverProperties3 = this.serverProperties;
        serverProperties3.getClass();
        PropertyMapper.Source from6 = map.from(this::getSsl);
        factory.getClass();
        from6.to(this::setSsl);
        ServerProperties.Servlet servlet4 = this.serverProperties.getServlet();
        servlet4.getClass();
        PropertyMapper.Source from7 = map.from(this::getJsp);
        factory.getClass();
        from7.to(this::setJsp);
        ServerProperties serverProperties4 = this.serverProperties;
        serverProperties4.getClass();
        PropertyMapper.Source from8 = map.from(this::getCompression);
        factory.getClass();
        from8.to(this::setCompression);
        ServerProperties serverProperties5 = this.serverProperties;
        serverProperties5.getClass();
        PropertyMapper.Source from9 = map.from(this::getHttp2);
        factory.getClass();
        from9.to(this::setHttp2);
        ServerProperties serverProperties6 = this.serverProperties;
        serverProperties6.getClass();
        PropertyMapper.Source from10 = map.from(this::getServerHeader);
        factory.getClass();
        from10.to(this::setServerHeader);
        ServerProperties.Servlet servlet5 = this.serverProperties.getServlet();
        servlet5.getClass();
        PropertyMapper.Source from11 = map.from(this::getContextParameters);
        factory.getClass();
        from11.to(this::setInitParameters);
    }
}