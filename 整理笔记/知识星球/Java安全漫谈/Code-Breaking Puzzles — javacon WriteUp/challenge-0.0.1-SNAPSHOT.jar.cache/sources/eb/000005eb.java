package javax.el;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/ListELResolver.class */
public class ListELResolver extends ELResolver {
    private final boolean readOnly;
    private static final Class<?> UNMODIFIABLE = Collections.unmodifiableList(new ArrayList()).getClass();

    public ListELResolver() {
        this.readOnly = false;
    }

    public ListELResolver(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override // javax.el.ELResolver
    public Class<?> getType(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base instanceof List) {
            context.setPropertyResolved(base, property);
            List<?> list = (List) base;
            int idx = coerce(property);
            if (idx < 0 || idx >= list.size()) {
                throw new PropertyNotFoundException(new ArrayIndexOutOfBoundsException(idx).getMessage());
            }
            return Object.class;
        }
        return null;
    }

    @Override // javax.el.ELResolver
    public Object getValue(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base instanceof List) {
            context.setPropertyResolved(base, property);
            List<?> list = (List) base;
            int idx = coerce(property);
            if (idx < 0 || idx >= list.size()) {
                return null;
            }
            return list.get(idx);
        }
        return null;
    }

    @Override // javax.el.ELResolver
    public void setValue(ELContext context, Object base, Object property, Object value) {
        Objects.requireNonNull(context);
        if (base instanceof List) {
            context.setPropertyResolved(base, property);
            List<Object> list = (List) base;
            if (this.readOnly) {
                throw new PropertyNotWritableException(Util.message(context, "resolverNotWriteable", base.getClass().getName()));
            }
            int idx = coerce(property);
            try {
                list.set(idx, value);
            } catch (IndexOutOfBoundsException e) {
                throw new PropertyNotFoundException(e);
            } catch (UnsupportedOperationException e2) {
                throw new PropertyNotWritableException(e2);
            }
        }
    }

    @Override // javax.el.ELResolver
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        int idx;
        Objects.requireNonNull(context);
        if (base instanceof List) {
            context.setPropertyResolved(base, property);
            List<?> list = (List) base;
            try {
                idx = coerce(property);
            } catch (IllegalArgumentException e) {
            }
            if (idx < 0 || idx >= list.size()) {
                throw new PropertyNotFoundException(new ArrayIndexOutOfBoundsException(idx).getMessage());
            }
            return this.readOnly || UNMODIFIABLE.equals(list.getClass());
        }
        return this.readOnly;
    }

    @Override // javax.el.ELResolver
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }

    @Override // javax.el.ELResolver
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base instanceof List) {
            return Integer.class;
        }
        return null;
    }

    private static final int coerce(Object property) {
        if (property instanceof Number) {
            return ((Number) property).intValue();
        }
        if (property instanceof Character) {
            return ((Character) property).charValue();
        }
        if (property instanceof Boolean) {
            return ((Boolean) property).booleanValue() ? 1 : 0;
        } else if (property instanceof String) {
            return Integer.parseInt((String) property);
        } else {
            throw new IllegalArgumentException(property != null ? property.toString() : BeanDefinitionParserDelegate.NULL_ELEMENT);
        }
    }
}