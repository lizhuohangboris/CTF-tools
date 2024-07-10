package javax.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/ELResolver.class */
public abstract class ELResolver {
    public static final String TYPE = "type";
    public static final String RESOLVABLE_AT_DESIGN_TIME = "resolvableAtDesignTime";

    public abstract Object getValue(ELContext eLContext, Object obj, Object obj2);

    public abstract Class<?> getType(ELContext eLContext, Object obj, Object obj2);

    public abstract void setValue(ELContext eLContext, Object obj, Object obj2, Object obj3);

    public abstract boolean isReadOnly(ELContext eLContext, Object obj, Object obj2);

    public abstract Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext eLContext, Object obj);

    public abstract Class<?> getCommonPropertyType(ELContext eLContext, Object obj);

    public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
        return null;
    }

    public Object convertToType(ELContext context, Object obj, Class<?> type) {
        context.setPropertyResolved(false);
        return null;
    }
}