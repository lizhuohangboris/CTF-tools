package org.apache.catalina.core;

import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/StandardWrapperFacade.class */
public final class StandardWrapperFacade implements ServletConfig {
    private final ServletConfig config;
    private ServletContext context = null;

    public StandardWrapperFacade(StandardWrapper config) {
        this.config = config;
    }

    @Override // javax.servlet.ServletConfig
    public String getServletName() {
        return this.config.getServletName();
    }

    @Override // javax.servlet.ServletConfig
    public ServletContext getServletContext() {
        if (this.context == null) {
            this.context = this.config.getServletContext();
            if (this.context instanceof ApplicationContext) {
                this.context = ((ApplicationContext) this.context).getFacade();
            }
        }
        return this.context;
    }

    @Override // javax.servlet.ServletConfig
    public String getInitParameter(String name) {
        return this.config.getInitParameter(name);
    }

    @Override // javax.servlet.ServletConfig
    public Enumeration<String> getInitParameterNames() {
        return this.config.getInitParameterNames();
    }
}