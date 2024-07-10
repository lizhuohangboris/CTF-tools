package org.apache.el.stream;

import java.beans.FeatureDescriptor;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.el.ELContext;
import javax.el.ELResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/stream/StreamELResolverImpl.class */
public class StreamELResolverImpl extends ELResolver {
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

    @Override // javax.el.ELResolver
    public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
        if ("stream".equals(method) && params.length == 0) {
            if (base.getClass().isArray()) {
                context.setPropertyResolved(true);
                return new Stream(new ArrayIterator(base));
            } else if (base instanceof Collection) {
                context.setPropertyResolved(true);
                Collection<Object> collection = (Collection) base;
                return new Stream(collection.iterator());
            } else {
                return null;
            }
        }
        return null;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/stream/StreamELResolverImpl$ArrayIterator.class */
    private static class ArrayIterator implements Iterator<Object> {
        private final Object base;
        private final int size;
        private int index = 0;

        public ArrayIterator(Object base) {
            this.base = base;
            this.size = Array.getLength(base);
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.size > this.index;
        }

        @Override // java.util.Iterator
        public Object next() {
            try {
                Object obj = this.base;
                int i = this.index;
                this.index = i + 1;
                return Array.get(obj, i);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}