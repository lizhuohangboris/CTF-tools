package org.springframework.web.jsf.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;
import javax.faces.context.FacesContext;
import org.springframework.lang.Nullable;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/jsf/el/SpringBeanFacesELResolver.class */
public class SpringBeanFacesELResolver extends ELResolver {
    @Override // javax.el.ELResolver
    @Nullable
    public Object getValue(ELContext elContext, @Nullable Object base, Object property) throws ELException {
        if (base == null) {
            String beanName = property.toString();
            WebApplicationContext wac = getWebApplicationContext(elContext);
            if (wac.containsBean(beanName)) {
                elContext.setPropertyResolved(true);
                return wac.getBean(beanName);
            }
            return null;
        }
        return null;
    }

    @Override // javax.el.ELResolver
    @Nullable
    public Class<?> getType(ELContext elContext, @Nullable Object base, Object property) throws ELException {
        if (base == null) {
            String beanName = property.toString();
            WebApplicationContext wac = getWebApplicationContext(elContext);
            if (wac.containsBean(beanName)) {
                elContext.setPropertyResolved(true);
                return wac.getType(beanName);
            }
            return null;
        }
        return null;
    }

    @Override // javax.el.ELResolver
    public void setValue(ELContext elContext, @Nullable Object base, Object property, Object value) throws ELException {
        if (base == null) {
            String beanName = property.toString();
            WebApplicationContext wac = getWebApplicationContext(elContext);
            if (wac.containsBean(beanName)) {
                if (value == wac.getBean(beanName)) {
                    elContext.setPropertyResolved(true);
                    return;
                }
                throw new PropertyNotWritableException("Variable '" + beanName + "' refers to a Spring bean which by definition is not writable");
            }
        }
    }

    @Override // javax.el.ELResolver
    public boolean isReadOnly(ELContext elContext, @Nullable Object base, Object property) throws ELException {
        if (base == null) {
            String beanName = property.toString();
            WebApplicationContext wac = getWebApplicationContext(elContext);
            if (wac.containsBean(beanName)) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Override // javax.el.ELResolver
    @Nullable
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext elContext, @Nullable Object base) {
        return null;
    }

    @Override // javax.el.ELResolver
    public Class<?> getCommonPropertyType(ELContext elContext, @Nullable Object base) {
        return Object.class;
    }

    protected WebApplicationContext getWebApplicationContext(ELContext elContext) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return FacesContextUtils.getRequiredWebApplicationContext(facesContext);
    }
}