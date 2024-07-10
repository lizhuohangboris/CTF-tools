package org.springframework.boot.web.context;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/context/ServerPortInfoApplicationContextInitializer.class */
public class ServerPortInfoApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, ApplicationListener<WebServerInitializedEvent> {
    @Override // org.springframework.context.ApplicationContextInitializer
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext.addApplicationListener(this);
    }

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(WebServerInitializedEvent event) {
        String propertyName = "local." + getName(event.getApplicationContext()) + ".port";
        setPortProperty(event.getApplicationContext(), propertyName, event.getWebServer().getPort());
    }

    private String getName(WebServerApplicationContext context) {
        String name = context.getServerNamespace();
        return StringUtils.hasText(name) ? name : "server";
    }

    private void setPortProperty(ApplicationContext context, String propertyName, int port) {
        if (context instanceof ConfigurableApplicationContext) {
            setPortProperty(((ConfigurableApplicationContext) context).getEnvironment(), propertyName, port);
        }
        if (context.getParent() != null) {
            setPortProperty(context.getParent(), propertyName, port);
        }
    }

    private void setPortProperty(ConfigurableEnvironment environment, String propertyName, int port) {
        MutablePropertySources sources = environment.getPropertySources();
        PropertySource<?> source = sources.get("server.ports");
        if (source == null) {
            source = new MapPropertySource("server.ports", new HashMap());
            sources.addFirst(source);
        }
        ((Map) source.getSource()).put(propertyName, Integer.valueOf(port));
    }
}