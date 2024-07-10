package javax.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/CompositeELResolver.class */
public class CompositeELResolver extends ELResolver {
    private static final Class<?> SCOPED_ATTRIBUTE_EL_RESOLVER;
    private int size = 0;
    private ELResolver[] resolvers = new ELResolver[8];

    static {
        Class<?> clazz = null;
        try {
            clazz = Class.forName("javax.servlet.jsp.el.ScopedAttributeELResolver");
        } catch (ClassNotFoundException e) {
        }
        SCOPED_ATTRIBUTE_EL_RESOLVER = clazz;
    }

    public void add(ELResolver elResolver) {
        Objects.requireNonNull(elResolver);
        if (this.size >= this.resolvers.length) {
            ELResolver[] nr = new ELResolver[this.size * 2];
            System.arraycopy(this.resolvers, 0, nr, 0, this.size);
            this.resolvers = nr;
        }
        ELResolver[] eLResolverArr = this.resolvers;
        int i = this.size;
        this.size = i + 1;
        eLResolverArr[i] = elResolver;
    }

    @Override // javax.el.ELResolver
    public Object getValue(ELContext context, Object base, Object property) {
        context.setPropertyResolved(false);
        int sz = this.size;
        for (int i = 0; i < sz; i++) {
            Object result = this.resolvers[i].getValue(context, base, property);
            if (context.isPropertyResolved()) {
                return result;
            }
        }
        return null;
    }

    @Override // javax.el.ELResolver
    public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
        context.setPropertyResolved(false);
        int sz = this.size;
        for (int i = 0; i < sz; i++) {
            Object obj = this.resolvers[i].invoke(context, base, method, paramTypes, params);
            if (context.isPropertyResolved()) {
                return obj;
            }
        }
        return null;
    }

    @Override // javax.el.ELResolver
    public Class<?> getType(ELContext context, Object base, Object property) {
        Object value;
        context.setPropertyResolved(false);
        int sz = this.size;
        for (int i = 0; i < sz; i++) {
            Class<?> type = this.resolvers[i].getType(context, base, property);
            if (context.isPropertyResolved()) {
                if (SCOPED_ATTRIBUTE_EL_RESOLVER != null && SCOPED_ATTRIBUTE_EL_RESOLVER.isAssignableFrom(this.resolvers[i].getClass()) && (value = this.resolvers[i].getValue(context, base, property)) != null) {
                    return value.getClass();
                } else {
                    return type;
                }
            }
        }
        return null;
    }

    @Override // javax.el.ELResolver
    public void setValue(ELContext context, Object base, Object property, Object value) {
        context.setPropertyResolved(false);
        int sz = this.size;
        for (int i = 0; i < sz; i++) {
            this.resolvers[i].setValue(context, base, property, value);
            if (context.isPropertyResolved()) {
                return;
            }
        }
    }

    @Override // javax.el.ELResolver
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        context.setPropertyResolved(false);
        int sz = this.size;
        for (int i = 0; i < sz; i++) {
            boolean readOnly = this.resolvers[i].isReadOnly(context, base, property);
            if (context.isPropertyResolved()) {
                return readOnly;
            }
        }
        return false;
    }

    @Override // javax.el.ELResolver
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return new FeatureIterator(context, base, this.resolvers, this.size);
    }

    @Override // javax.el.ELResolver
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        Class<?> commonType = null;
        int sz = this.size;
        for (int i = 0; i < sz; i++) {
            Class<?> type = this.resolvers[i].getCommonPropertyType(context, base);
            if (type != null && (commonType == null || commonType.isAssignableFrom(type))) {
                commonType = type;
            }
        }
        return commonType;
    }

    @Override // javax.el.ELResolver
    public Object convertToType(ELContext context, Object obj, Class<?> type) {
        context.setPropertyResolved(false);
        int sz = this.size;
        for (int i = 0; i < sz; i++) {
            Object result = this.resolvers[i].convertToType(context, obj, type);
            if (context.isPropertyResolved()) {
                return result;
            }
        }
        return null;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/CompositeELResolver$FeatureIterator.class */
    private static final class FeatureIterator implements Iterator<FeatureDescriptor> {
        private final ELContext context;
        private final Object base;
        private final ELResolver[] resolvers;
        private final int size;
        private Iterator<FeatureDescriptor> itr;
        private int idx = 0;
        private FeatureDescriptor next;

        public FeatureIterator(ELContext context, Object base, ELResolver[] resolvers, int size) {
            this.context = context;
            this.base = base;
            this.resolvers = resolvers;
            this.size = size;
            guaranteeIterator();
        }

        private void guaranteeIterator() {
            while (this.itr == null && this.idx < this.size) {
                this.itr = this.resolvers[this.idx].getFeatureDescriptors(this.context, this.base);
                this.idx++;
            }
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            if (this.next != null) {
                return true;
            }
            if (this.itr != null) {
                while (this.next == null && this.itr.hasNext()) {
                    this.next = this.itr.next();
                }
                if (this.next == null) {
                    this.itr = null;
                    guaranteeIterator();
                }
                return hasNext();
            }
            return false;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public FeatureDescriptor next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            FeatureDescriptor result = this.next;
            this.next = null;
            return result;
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}