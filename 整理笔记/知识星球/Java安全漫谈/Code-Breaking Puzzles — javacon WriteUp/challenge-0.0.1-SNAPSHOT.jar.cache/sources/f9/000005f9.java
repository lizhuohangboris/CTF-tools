package javax.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/TypeConverter.class */
public abstract class TypeConverter extends ELResolver {
    @Override // javax.el.ELResolver
    public abstract Object convertToType(ELContext eLContext, Object obj, Class<?> cls);

    @Override // javax.el.ELResolver
    public Object getValue(ELContext context, Object base, Object property) {
        return null;
    }

    @Override // javax.el.ELResolver
    public Class<?> getType(ELContext context, Object base, Object property) {
        return null;
    }

    @Override // javax.el.ELResolver
    public void setValue(ELContext context, Object base, Object property, Object value) {
    }

    @Override // javax.el.ELResolver
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        return false;
    }

    @Override // javax.el.ELResolver
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }

    @Override // javax.el.ELResolver
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return null;
    }
}