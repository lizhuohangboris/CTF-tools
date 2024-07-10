package org.springframework.boot.web.servlet;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.Registration;
import javax.servlet.Registration.Dynamic;
import javax.servlet.ServletContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.Conventions;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/servlet/DynamicRegistrationBean.class */
public abstract class DynamicRegistrationBean<D extends Registration.Dynamic> extends RegistrationBean {
    private static final Log logger = LogFactory.getLog(RegistrationBean.class);
    private String name;
    private boolean asyncSupported = true;
    private Map<String, String> initParameters = new LinkedHashMap();

    protected abstract D addRegistration(String description, ServletContext servletContext);

    public void setName(String name) {
        Assert.hasLength(name, "Name must not be empty");
        this.name = name;
    }

    public void setAsyncSupported(boolean asyncSupported) {
        this.asyncSupported = asyncSupported;
    }

    public boolean isAsyncSupported() {
        return this.asyncSupported;
    }

    public void setInitParameters(Map<String, String> initParameters) {
        Assert.notNull(initParameters, "InitParameters must not be null");
        this.initParameters = new LinkedHashMap(initParameters);
    }

    public Map<String, String> getInitParameters() {
        return this.initParameters;
    }

    public void addInitParameter(String name, String value) {
        Assert.notNull(name, "Name must not be null");
        this.initParameters.put(name, value);
    }

    @Override // org.springframework.boot.web.servlet.RegistrationBean
    protected final void register(String description, ServletContext servletContext) {
        D registration = addRegistration(description, servletContext);
        if (registration == null) {
            logger.info(StringUtils.capitalize(description) + " was not registered (possibly already registered?)");
        } else {
            configure(registration);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void configure(D registration) {
        registration.setAsyncSupported(this.asyncSupported);
        if (!this.initParameters.isEmpty()) {
            registration.setInitParameters(this.initParameters);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final String getOrDeduceName(Object value) {
        return this.name != null ? this.name : Conventions.getVariableName(value);
    }
}