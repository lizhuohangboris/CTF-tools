package javax.el;

import java.beans.FeatureDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Objects;
import org.springframework.cglib.core.Constants;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/StaticFieldELResolver.class */
public class StaticFieldELResolver extends ELResolver {
    @Override // javax.el.ELResolver
    public Object getValue(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if ((base instanceof ELClass) && (property instanceof String)) {
            context.setPropertyResolved(base, property);
            Class<?> clazz = ((ELClass) base).getKlass();
            String name = (String) property;
            Exception exception = null;
            try {
                Field field = clazz.getField(name);
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
                    return field.get(null);
                }
            } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                exception = e;
            }
            String msg = Util.message(context, "staticFieldELResolver.notFound", name, clazz.getName());
            if (exception == null) {
                throw new PropertyNotFoundException(msg);
            }
            throw new PropertyNotFoundException(msg, exception);
        }
        return null;
    }

    @Override // javax.el.ELResolver
    public void setValue(ELContext context, Object base, Object property, Object value) {
        Objects.requireNonNull(context);
        if ((base instanceof ELClass) && (property instanceof String)) {
            Class<?> clazz = ((ELClass) base).getKlass();
            String name = (String) property;
            throw new PropertyNotWritableException(Util.message(context, "staticFieldELResolver.notWriteable", name, clazz.getName()));
        }
    }

    @Override // javax.el.ELResolver
    public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
        Objects.requireNonNull(context);
        if ((base instanceof ELClass) && (method instanceof String)) {
            context.setPropertyResolved(base, method);
            Class<?> clazz = ((ELClass) base).getKlass();
            String methodName = (String) method;
            if (Constants.CONSTRUCTOR_NAME.equals(methodName)) {
                Constructor<?> match = Util.findConstructor(clazz, paramTypes, params);
                Object[] parameters = Util.buildParameters(match.getParameterTypes(), match.isVarArgs(), params);
                try {
                    Object result = match.newInstance(parameters);
                    return result;
                } catch (InvocationTargetException e) {
                    Throwable cause = e.getCause();
                    Util.handleThrowable(cause);
                    throw new ELException(cause);
                } catch (ReflectiveOperationException e2) {
                    throw new ELException(e2);
                }
            }
            Method match2 = Util.findMethod(clazz, methodName, paramTypes, params);
            int modifiers = match2.getModifiers();
            if (!Modifier.isStatic(modifiers)) {
                throw new MethodNotFoundException(Util.message(context, "staticFieldELResolver.methodNotFound", methodName, clazz.getName()));
            }
            Object[] parameters2 = Util.buildParameters(match2.getParameterTypes(), match2.isVarArgs(), params);
            try {
                Object result2 = match2.invoke(null, parameters2);
                return result2;
            } catch (IllegalAccessException | IllegalArgumentException e3) {
                throw new ELException(e3);
            } catch (InvocationTargetException e4) {
                Throwable cause2 = e4.getCause();
                Util.handleThrowable(cause2);
                throw new ELException(cause2);
            }
        }
        return null;
    }

    @Override // javax.el.ELResolver
    public Class<?> getType(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if ((base instanceof ELClass) && (property instanceof String)) {
            context.setPropertyResolved(base, property);
            Class<?> clazz = ((ELClass) base).getKlass();
            String name = (String) property;
            Exception exception = null;
            try {
                Field field = clazz.getField(name);
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
                    return field.getType();
                }
            } catch (IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                exception = e;
            }
            String msg = Util.message(context, "staticFieldELResolver.notFound", name, clazz.getName());
            if (exception == null) {
                throw new PropertyNotFoundException(msg);
            }
            throw new PropertyNotFoundException(msg, exception);
        }
        return null;
    }

    @Override // javax.el.ELResolver
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if ((base instanceof ELClass) && (property instanceof String)) {
            context.setPropertyResolved(base, property);
            return true;
        }
        return true;
    }

    @Override // javax.el.ELResolver
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }

    @Override // javax.el.ELResolver
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return String.class;
    }
}