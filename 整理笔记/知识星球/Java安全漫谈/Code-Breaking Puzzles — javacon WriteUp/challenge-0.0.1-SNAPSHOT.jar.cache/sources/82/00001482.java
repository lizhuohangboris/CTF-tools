package org.springframework.beans.factory.support;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/support/DisposableBeanAdapter.class */
public class DisposableBeanAdapter implements DisposableBean, Runnable, Serializable {
    private static final String CLOSE_METHOD_NAME = "close";
    private static final String SHUTDOWN_METHOD_NAME = "shutdown";
    private static final Log logger = LogFactory.getLog(DisposableBeanAdapter.class);
    private final Object bean;
    private final String beanName;
    private final boolean invokeDisposableBean;
    private final boolean nonPublicAccessAllowed;
    @Nullable
    private final AccessControlContext acc;
    @Nullable
    private String destroyMethodName;
    @Nullable
    private transient Method destroyMethod;
    @Nullable
    private List<DestructionAwareBeanPostProcessor> beanPostProcessors;

    public DisposableBeanAdapter(Object bean, String beanName, RootBeanDefinition beanDefinition, List<BeanPostProcessor> postProcessors, @Nullable AccessControlContext acc) {
        Assert.notNull(bean, "Disposable bean must not be null");
        this.bean = bean;
        this.beanName = beanName;
        this.invokeDisposableBean = (this.bean instanceof DisposableBean) && !beanDefinition.isExternallyManagedDestroyMethod("destroy");
        this.nonPublicAccessAllowed = beanDefinition.isNonPublicAccessAllowed();
        this.acc = acc;
        String destroyMethodName = inferDestroyMethodIfNecessary(bean, beanDefinition);
        if (destroyMethodName != null && ((!this.invokeDisposableBean || !"destroy".equals(destroyMethodName)) && !beanDefinition.isExternallyManagedDestroyMethod(destroyMethodName))) {
            this.destroyMethodName = destroyMethodName;
            this.destroyMethod = determineDestroyMethod(destroyMethodName);
            if (this.destroyMethod == null) {
                if (beanDefinition.isEnforceDestroyMethod()) {
                    throw new BeanDefinitionValidationException("Could not find a destroy method named '" + destroyMethodName + "' on bean with name '" + beanName + "'");
                }
            } else {
                Class<?>[] paramTypes = this.destroyMethod.getParameterTypes();
                if (paramTypes.length > 1) {
                    throw new BeanDefinitionValidationException("Method '" + destroyMethodName + "' of bean '" + beanName + "' has more than one parameter - not supported as destroy method");
                }
                if (paramTypes.length == 1 && Boolean.TYPE != paramTypes[0]) {
                    throw new BeanDefinitionValidationException("Method '" + destroyMethodName + "' of bean '" + beanName + "' has a non-boolean parameter - not supported as destroy method");
                }
            }
        }
        this.beanPostProcessors = filterPostProcessors(postProcessors, bean);
    }

    public DisposableBeanAdapter(Object bean, List<BeanPostProcessor> postProcessors, AccessControlContext acc) {
        Assert.notNull(bean, "Disposable bean must not be null");
        this.bean = bean;
        this.beanName = bean.getClass().getName();
        this.invokeDisposableBean = this.bean instanceof DisposableBean;
        this.nonPublicAccessAllowed = true;
        this.acc = acc;
        this.beanPostProcessors = filterPostProcessors(postProcessors, bean);
    }

    private DisposableBeanAdapter(Object bean, String beanName, boolean invokeDisposableBean, boolean nonPublicAccessAllowed, @Nullable String destroyMethodName, @Nullable List<DestructionAwareBeanPostProcessor> postProcessors) {
        this.bean = bean;
        this.beanName = beanName;
        this.invokeDisposableBean = invokeDisposableBean;
        this.nonPublicAccessAllowed = nonPublicAccessAllowed;
        this.acc = null;
        this.destroyMethodName = destroyMethodName;
        this.beanPostProcessors = postProcessors;
    }

    @Nullable
    private String inferDestroyMethodIfNecessary(Object bean, RootBeanDefinition beanDefinition) {
        String destroyMethodName = beanDefinition.getDestroyMethodName();
        if (AbstractBeanDefinition.INFER_METHOD.equals(destroyMethodName) || (destroyMethodName == null && (bean instanceof AutoCloseable))) {
            if (!(bean instanceof DisposableBean)) {
                try {
                    return bean.getClass().getMethod("close", new Class[0]).getName();
                } catch (NoSuchMethodException e) {
                    try {
                        return bean.getClass().getMethod(SHUTDOWN_METHOD_NAME, new Class[0]).getName();
                    } catch (NoSuchMethodException e2) {
                        return null;
                    }
                }
            }
            return null;
        } else if (StringUtils.hasLength(destroyMethodName)) {
            return destroyMethodName;
        } else {
            return null;
        }
    }

    @Nullable
    private List<DestructionAwareBeanPostProcessor> filterPostProcessors(List<BeanPostProcessor> processors, Object bean) {
        List<DestructionAwareBeanPostProcessor> filteredPostProcessors = null;
        if (!CollectionUtils.isEmpty(processors)) {
            filteredPostProcessors = new ArrayList<>(processors.size());
            for (BeanPostProcessor processor : processors) {
                if (processor instanceof DestructionAwareBeanPostProcessor) {
                    DestructionAwareBeanPostProcessor dabpp = (DestructionAwareBeanPostProcessor) processor;
                    if (dabpp.requiresDestruction(bean)) {
                        filteredPostProcessors.add(dabpp);
                    }
                }
            }
        }
        return filteredPostProcessors;
    }

    @Override // java.lang.Runnable
    public void run() {
        destroy();
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        Method methodToCall;
        if (!CollectionUtils.isEmpty(this.beanPostProcessors)) {
            for (DestructionAwareBeanPostProcessor processor : this.beanPostProcessors) {
                processor.postProcessBeforeDestruction(this.bean, this.beanName);
            }
        }
        if (this.invokeDisposableBean) {
            if (logger.isTraceEnabled()) {
                logger.trace("Invoking destroy() on bean with name '" + this.beanName + "'");
            }
            try {
                if (System.getSecurityManager() != null) {
                    AccessController.doPrivileged(() -> {
                        ((DisposableBean) this.bean).destroy();
                        return null;
                    }, this.acc);
                } else {
                    ((DisposableBean) this.bean).destroy();
                }
            } catch (Throwable ex) {
                String msg = "Invocation of destroy method failed on bean with name '" + this.beanName + "'";
                if (logger.isDebugEnabled()) {
                    logger.info(msg, ex);
                } else {
                    logger.info(msg + ": " + ex);
                }
            }
        }
        if (this.destroyMethod != null) {
            invokeCustomDestroyMethod(this.destroyMethod);
        } else if (this.destroyMethodName != null && (methodToCall = determineDestroyMethod(this.destroyMethodName)) != null) {
            invokeCustomDestroyMethod(methodToCall);
        }
    }

    @Nullable
    private Method determineDestroyMethod(String name) {
        try {
            if (System.getSecurityManager() != null) {
                return (Method) AccessController.doPrivileged(() -> {
                    return findDestroyMethod(name);
                });
            }
            return findDestroyMethod(name);
        } catch (IllegalArgumentException ex) {
            throw new BeanDefinitionValidationException("Could not find unique destroy method on bean with name '" + this.beanName + ": " + ex.getMessage());
        }
    }

    @Nullable
    private Method findDestroyMethod(String name) {
        if (this.nonPublicAccessAllowed) {
            return BeanUtils.findMethodWithMinimalParameters(this.bean.getClass(), name);
        }
        return BeanUtils.findMethodWithMinimalParameters(this.bean.getClass().getMethods(), name);
    }

    private void invokeCustomDestroyMethod(Method destroyMethod) {
        Class<?>[] paramTypes = destroyMethod.getParameterTypes();
        Object[] args = new Object[paramTypes.length];
        if (paramTypes.length == 1) {
            args[0] = Boolean.TRUE;
        }
        if (logger.isTraceEnabled()) {
            logger.trace("Invoking destroy method '" + this.destroyMethodName + "' on bean with name '" + this.beanName + "'");
        }
        try {
            if (System.getSecurityManager() != null) {
                AccessController.doPrivileged(() -> {
                    ReflectionUtils.makeAccessible(destroyMethod);
                    return null;
                });
                try {
                    AccessController.doPrivileged(() -> {
                        return destroyMethod.invoke(this.bean, args);
                    }, this.acc);
                } catch (PrivilegedActionException pax) {
                    throw ((InvocationTargetException) pax.getException());
                }
            } else {
                ReflectionUtils.makeAccessible(destroyMethod);
                destroyMethod.invoke(this.bean, args);
            }
        } catch (InvocationTargetException ex) {
            String msg = "Destroy method '" + this.destroyMethodName + "' on bean with name '" + this.beanName + "' threw an exception";
            if (logger.isDebugEnabled()) {
                logger.info(msg, ex.getTargetException());
            } else {
                logger.info(msg + ": " + ex.getTargetException());
            }
        } catch (Throwable ex2) {
            logger.info("Failed to invoke destroy method '" + this.destroyMethodName + "' on bean with name '" + this.beanName + "'", ex2);
        }
    }

    protected Object writeReplace() {
        List<DestructionAwareBeanPostProcessor> serializablePostProcessors = null;
        if (this.beanPostProcessors != null) {
            serializablePostProcessors = new ArrayList<>();
            for (DestructionAwareBeanPostProcessor postProcessor : this.beanPostProcessors) {
                if (postProcessor instanceof Serializable) {
                    serializablePostProcessors.add(postProcessor);
                }
            }
        }
        return new DisposableBeanAdapter(this.bean, this.beanName, this.invokeDisposableBean, this.nonPublicAccessAllowed, this.destroyMethodName, serializablePostProcessors);
    }

    public static boolean hasDestroyMethod(Object bean, RootBeanDefinition beanDefinition) {
        if ((bean instanceof DisposableBean) || (bean instanceof AutoCloseable)) {
            return true;
        }
        String destroyMethodName = beanDefinition.getDestroyMethodName();
        if (AbstractBeanDefinition.INFER_METHOD.equals(destroyMethodName)) {
            return ClassUtils.hasMethod(bean.getClass(), "close", new Class[0]) || ClassUtils.hasMethod(bean.getClass(), SHUTDOWN_METHOD_NAME, new Class[0]);
        }
        return StringUtils.hasLength(destroyMethodName);
    }

    public static boolean hasApplicableProcessors(Object bean, List<BeanPostProcessor> postProcessors) {
        if (!CollectionUtils.isEmpty(postProcessors)) {
            for (BeanPostProcessor processor : postProcessors) {
                if (processor instanceof DestructionAwareBeanPostProcessor) {
                    DestructionAwareBeanPostProcessor dabpp = (DestructionAwareBeanPostProcessor) processor;
                    if (dabpp.requiresDestruction(bean)) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }
}