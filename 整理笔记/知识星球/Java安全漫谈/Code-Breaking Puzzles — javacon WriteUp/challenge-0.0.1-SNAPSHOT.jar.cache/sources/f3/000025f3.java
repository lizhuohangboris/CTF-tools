package org.springframework.web.servlet.mvc;

import java.util.Enumeration;
import java.util.Properties;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.ModelAndView;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/ServletWrappingController.class */
public class ServletWrappingController extends AbstractController implements BeanNameAware, InitializingBean, DisposableBean {
    @Nullable
    private Class<? extends Servlet> servletClass;
    @Nullable
    private String servletName;
    private Properties initParameters;
    @Nullable
    private String beanName;
    @Nullable
    private Servlet servletInstance;

    public ServletWrappingController() {
        super(false);
        this.initParameters = new Properties();
    }

    public void setServletClass(Class<? extends Servlet> servletClass) {
        this.servletClass = servletClass;
    }

    public void setServletName(String servletName) {
        this.servletName = servletName;
    }

    public void setInitParameters(Properties initParameters) {
        this.initParameters = initParameters;
    }

    @Override // org.springframework.beans.factory.BeanNameAware
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        if (this.servletClass == null) {
            throw new IllegalArgumentException("'servletClass' is required");
        }
        if (this.servletName == null) {
            this.servletName = this.beanName;
        }
        this.servletInstance = (Servlet) ReflectionUtils.accessibleConstructor(this.servletClass, new Class[0]).newInstance(new Object[0]);
        this.servletInstance.init(new DelegatingServletConfig());
    }

    @Override // org.springframework.web.servlet.mvc.AbstractController
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Assert.state(this.servletInstance != null, "No Servlet instance");
        this.servletInstance.service(request, response);
        return null;
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        if (this.servletInstance != null) {
            this.servletInstance.destroy();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/ServletWrappingController$DelegatingServletConfig.class */
    private class DelegatingServletConfig implements ServletConfig {
        private DelegatingServletConfig() {
        }

        @Override // javax.servlet.ServletConfig
        @Nullable
        public String getServletName() {
            return ServletWrappingController.this.servletName;
        }

        @Override // javax.servlet.ServletConfig
        @Nullable
        public ServletContext getServletContext() {
            return ServletWrappingController.this.getServletContext();
        }

        @Override // javax.servlet.ServletConfig
        public String getInitParameter(String paramName) {
            return ServletWrappingController.this.initParameters.getProperty(paramName);
        }

        @Override // javax.servlet.ServletConfig
        public Enumeration<String> getInitParameterNames() {
            return ServletWrappingController.this.initParameters.keys();
        }
    }
}