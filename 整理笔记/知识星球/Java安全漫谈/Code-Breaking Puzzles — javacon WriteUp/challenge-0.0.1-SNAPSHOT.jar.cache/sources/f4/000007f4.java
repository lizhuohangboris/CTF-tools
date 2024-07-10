package org.apache.catalina.core;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletSecurityElement;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Wrapper;
import org.apache.catalina.util.ParameterMap;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/core/ApplicationServletRegistration.class */
public class ApplicationServletRegistration implements ServletRegistration.Dynamic {
    private static final StringManager sm = StringManager.getManager(Constants.Package);
    private final Wrapper wrapper;
    private final Context context;
    private ServletSecurityElement constraint;

    public ApplicationServletRegistration(Wrapper wrapper, Context context) {
        this.wrapper = wrapper;
        this.context = context;
    }

    @Override // javax.servlet.Registration
    public String getClassName() {
        return this.wrapper.getServletClass();
    }

    @Override // javax.servlet.Registration
    public String getInitParameter(String name) {
        return this.wrapper.findInitParameter(name);
    }

    @Override // javax.servlet.Registration
    public Map<String, String> getInitParameters() {
        ParameterMap<String, String> result = new ParameterMap<>();
        String[] parameterNames = this.wrapper.findInitParameters();
        for (String parameterName : parameterNames) {
            result.put(parameterName, this.wrapper.findInitParameter(parameterName));
        }
        result.setLocked(true);
        return result;
    }

    @Override // javax.servlet.Registration
    public String getName() {
        return this.wrapper.getName();
    }

    @Override // javax.servlet.Registration
    public boolean setInitParameter(String name, String value) {
        if (name == null || value == null) {
            throw new IllegalArgumentException(sm.getString("applicationFilterRegistration.nullInitParam", name, value));
        }
        if (getInitParameter(name) != null) {
            return false;
        }
        this.wrapper.addInitParameter(name, value);
        return true;
    }

    @Override // javax.servlet.Registration
    public Set<String> setInitParameters(Map<String, String> initParameters) {
        Set<String> conflicts = new HashSet<>();
        for (Map.Entry<String, String> entry : initParameters.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                throw new IllegalArgumentException(sm.getString("applicationFilterRegistration.nullInitParams", entry.getKey(), entry.getValue()));
            }
            if (getInitParameter(entry.getKey()) != null) {
                conflicts.add(entry.getKey());
            }
        }
        if (conflicts.isEmpty()) {
            for (Map.Entry<String, String> entry2 : initParameters.entrySet()) {
                setInitParameter(entry2.getKey(), entry2.getValue());
            }
        }
        return conflicts;
    }

    @Override // javax.servlet.Registration.Dynamic
    public void setAsyncSupported(boolean asyncSupported) {
        this.wrapper.setAsyncSupported(asyncSupported);
    }

    @Override // javax.servlet.ServletRegistration.Dynamic
    public void setLoadOnStartup(int loadOnStartup) {
        this.wrapper.setLoadOnStartup(loadOnStartup);
    }

    @Override // javax.servlet.ServletRegistration.Dynamic
    public void setMultipartConfig(MultipartConfigElement multipartConfig) {
        this.wrapper.setMultipartConfigElement(multipartConfig);
    }

    @Override // javax.servlet.ServletRegistration.Dynamic
    public void setRunAsRole(String roleName) {
        this.wrapper.setRunAs(roleName);
    }

    @Override // javax.servlet.ServletRegistration.Dynamic
    public Set<String> setServletSecurity(ServletSecurityElement constraint) {
        if (constraint == null) {
            throw new IllegalArgumentException(sm.getString("applicationServletRegistration.setServletSecurity.iae", getName(), this.context.getName()));
        }
        if (!this.context.getState().equals(LifecycleState.STARTING_PREP)) {
            throw new IllegalStateException(sm.getString("applicationServletRegistration.setServletSecurity.ise", getName(), this.context.getName()));
        }
        this.constraint = constraint;
        return this.context.addServletSecurity(this, constraint);
    }

    @Override // javax.servlet.ServletRegistration
    public Set<String> addMapping(String... urlPatterns) {
        if (urlPatterns == null) {
            return Collections.emptySet();
        }
        Set<String> conflicts = new HashSet<>();
        for (String urlPattern : urlPatterns) {
            String wrapperName = this.context.findServletMapping(urlPattern);
            if (wrapperName != null) {
                Wrapper wrapper = (Wrapper) this.context.findChild(wrapperName);
                if (wrapper.isOverridable()) {
                    this.context.removeServletMapping(urlPattern);
                } else {
                    conflicts.add(urlPattern);
                }
            }
        }
        if (!conflicts.isEmpty()) {
            return conflicts;
        }
        for (String urlPattern2 : urlPatterns) {
            this.context.addServletMappingDecoded(UDecoder.URLDecode(urlPattern2, StandardCharsets.UTF_8), this.wrapper.getName());
        }
        if (this.constraint != null) {
            this.context.addServletSecurity(this, this.constraint);
        }
        return Collections.emptySet();
    }

    @Override // javax.servlet.ServletRegistration
    public Collection<String> getMappings() {
        Set<String> result = new HashSet<>();
        String servletName = this.wrapper.getName();
        String[] urlPatterns = this.context.findServletMappings();
        for (String urlPattern : urlPatterns) {
            String name = this.context.findServletMapping(urlPattern);
            if (name.equals(servletName)) {
                result.add(urlPattern);
            }
        }
        return result;
    }

    @Override // javax.servlet.ServletRegistration
    public String getRunAsRole() {
        return this.wrapper.getRunAs();
    }
}