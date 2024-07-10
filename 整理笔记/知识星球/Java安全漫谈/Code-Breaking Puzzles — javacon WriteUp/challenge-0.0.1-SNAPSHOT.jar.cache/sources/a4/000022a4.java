package org.springframework.scheduling.annotation;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.FixedDelayTask;
import org.springframework.scheduling.config.FixedRateTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/annotation/ScheduledAnnotationBeanPostProcessor.class */
public class ScheduledAnnotationBeanPostProcessor implements ScheduledTaskHolder, MergedBeanDefinitionPostProcessor, DestructionAwareBeanPostProcessor, Ordered, EmbeddedValueResolverAware, BeanNameAware, BeanFactoryAware, ApplicationContextAware, SmartInitializingSingleton, ApplicationListener<ContextRefreshedEvent>, DisposableBean {
    public static final String DEFAULT_TASK_SCHEDULER_BEAN_NAME = "taskScheduler";
    protected final Log logger;
    private final ScheduledTaskRegistrar registrar;
    @Nullable
    private Object scheduler;
    @Nullable
    private StringValueResolver embeddedValueResolver;
    @Nullable
    private String beanName;
    @Nullable
    private BeanFactory beanFactory;
    @Nullable
    private ApplicationContext applicationContext;
    private final Set<Class<?>> nonAnnotatedClasses;
    private final Map<Object, Set<ScheduledTask>> scheduledTasks;

    public ScheduledAnnotationBeanPostProcessor() {
        this.logger = LogFactory.getLog(getClass());
        this.nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap(64));
        this.scheduledTasks = new IdentityHashMap(16);
        this.registrar = new ScheduledTaskRegistrar();
    }

    public ScheduledAnnotationBeanPostProcessor(ScheduledTaskRegistrar registrar) {
        this.logger = LogFactory.getLog(getClass());
        this.nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap(64));
        this.scheduledTasks = new IdentityHashMap(16);
        Assert.notNull(registrar, "ScheduledTaskRegistrar is required");
        this.registrar = registrar;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    public void setScheduler(Object scheduler) {
        this.scheduler = scheduler;
    }

    @Override // org.springframework.context.EmbeddedValueResolverAware
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.embeddedValueResolver = resolver;
    }

    @Override // org.springframework.beans.factory.BeanNameAware
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        if (this.beanFactory == null) {
            this.beanFactory = applicationContext;
        }
    }

    @Override // org.springframework.beans.factory.SmartInitializingSingleton
    public void afterSingletonsInstantiated() {
        this.nonAnnotatedClasses.clear();
        if (this.applicationContext == null) {
            finishRegistration();
        }
    }

    @Override // org.springframework.context.ApplicationListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext() == this.applicationContext) {
            finishRegistration();
        }
    }

    private void finishRegistration() {
        if (this.scheduler != null) {
            this.registrar.setScheduler(this.scheduler);
        }
        if (this.beanFactory instanceof ListableBeanFactory) {
            Map<String, SchedulingConfigurer> beans = ((ListableBeanFactory) this.beanFactory).getBeansOfType(SchedulingConfigurer.class);
            List<SchedulingConfigurer> configurers = new ArrayList<>(beans.values());
            AnnotationAwareOrderComparator.sort(configurers);
            for (SchedulingConfigurer configurer : configurers) {
                configurer.configureTasks(this.registrar);
            }
        }
        if (this.registrar.hasTasks() && this.registrar.getScheduler() == null) {
            Assert.state(this.beanFactory != null, "BeanFactory must be set to find scheduler by type");
            try {
                this.registrar.setTaskScheduler((TaskScheduler) resolveSchedulerBean(this.beanFactory, TaskScheduler.class, false));
            } catch (NoUniqueBeanDefinitionException ex) {
                this.logger.trace("Could not find unique TaskScheduler bean", ex);
                try {
                    this.registrar.setTaskScheduler((TaskScheduler) resolveSchedulerBean(this.beanFactory, TaskScheduler.class, true));
                } catch (NoSuchBeanDefinitionException e) {
                    if (this.logger.isInfoEnabled()) {
                        this.logger.info("More than one TaskScheduler bean exists within the context, and none is named 'taskScheduler'. Mark one of them as primary or name it 'taskScheduler' (possibly as an alias); or implement the SchedulingConfigurer interface and call ScheduledTaskRegistrar#setScheduler explicitly within the configureTasks() callback: " + ex.getBeanNamesFound());
                    }
                }
            } catch (NoSuchBeanDefinitionException ex2) {
                this.logger.trace("Could not find default TaskScheduler bean", ex2);
                try {
                    this.registrar.setScheduler(resolveSchedulerBean(this.beanFactory, ScheduledExecutorService.class, false));
                } catch (NoUniqueBeanDefinitionException ex22) {
                    this.logger.trace("Could not find unique ScheduledExecutorService bean", ex22);
                    try {
                        this.registrar.setScheduler(resolveSchedulerBean(this.beanFactory, ScheduledExecutorService.class, true));
                    } catch (NoSuchBeanDefinitionException e2) {
                        if (this.logger.isInfoEnabled()) {
                            this.logger.info("More than one ScheduledExecutorService bean exists within the context, and none is named 'taskScheduler'. Mark one of them as primary or name it 'taskScheduler' (possibly as an alias); or implement the SchedulingConfigurer interface and call ScheduledTaskRegistrar#setScheduler explicitly within the configureTasks() callback: " + ex22.getBeanNamesFound());
                        }
                    }
                } catch (NoSuchBeanDefinitionException ex23) {
                    this.logger.trace("Could not find default ScheduledExecutorService bean", ex23);
                    this.logger.info("No TaskScheduler/ScheduledExecutorService bean found for scheduled processing");
                }
            }
        }
        this.registrar.afterPropertiesSet();
    }

    private <T> T resolveSchedulerBean(BeanFactory beanFactory, Class<T> schedulerType, boolean byName) {
        if (byName) {
            T scheduler = (T) beanFactory.getBean(DEFAULT_TASK_SCHEDULER_BEAN_NAME, schedulerType);
            if (this.beanName != null && (this.beanFactory instanceof ConfigurableBeanFactory)) {
                ((ConfigurableBeanFactory) this.beanFactory).registerDependentBean(DEFAULT_TASK_SCHEDULER_BEAN_NAME, this.beanName);
            }
            return scheduler;
        } else if (beanFactory instanceof AutowireCapableBeanFactory) {
            NamedBeanHolder<T> holder = ((AutowireCapableBeanFactory) beanFactory).resolveNamedBean(schedulerType);
            if (this.beanName != null && (beanFactory instanceof ConfigurableBeanFactory)) {
                ((ConfigurableBeanFactory) beanFactory).registerDependentBean(holder.getBeanName(), this.beanName);
            }
            return holder.getBeanInstance();
        } else {
            return (T) beanFactory.getBean(schedulerType);
        }
    }

    @Override // org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override // org.springframework.beans.factory.config.BeanPostProcessor
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if ((bean instanceof AopInfrastructureBean) || (bean instanceof TaskScheduler) || (bean instanceof ScheduledExecutorService)) {
            return bean;
        }
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        if (!this.nonAnnotatedClasses.contains(targetClass)) {
            Map<Method, Set<Scheduled>> annotatedMethods = MethodIntrospector.selectMethods(targetClass, method -> {
                Set<Scheduled> scheduledMethods = AnnotatedElementUtils.getMergedRepeatableAnnotations(method, Scheduled.class, Schedules.class);
                if (scheduledMethods.isEmpty()) {
                    return null;
                }
                return scheduledMethods;
            });
            if (annotatedMethods.isEmpty()) {
                this.nonAnnotatedClasses.add(targetClass);
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("No @Scheduled annotations found on bean class: " + targetClass);
                }
            } else {
                annotatedMethods.forEach(method2, scheduledMethods -> {
                    scheduledMethods.forEach(scheduled -> {
                        processScheduled(scheduled, method2, bean);
                    });
                });
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace(annotatedMethods.size() + " @Scheduled methods processed on bean '" + beanName + "': " + annotatedMethods);
                }
            }
        }
        return bean;
    }

    protected void processScheduled(Scheduled scheduled, Method method, Object bean) {
        TimeZone timeZone;
        try {
            Runnable runnable = createRunnable(bean, method);
            boolean processedSchedule = false;
            Set<ScheduledTask> tasks = new LinkedHashSet<>(4);
            long initialDelay = scheduled.initialDelay();
            String initialDelayString = scheduled.initialDelayString();
            if (StringUtils.hasText(initialDelayString)) {
                Assert.isTrue(initialDelay < 0, "Specify 'initialDelay' or 'initialDelayString', not both");
                if (this.embeddedValueResolver != null) {
                    initialDelayString = this.embeddedValueResolver.resolveStringValue(initialDelayString);
                }
                if (StringUtils.hasLength(initialDelayString)) {
                    try {
                        initialDelay = parseDelayAsLong(initialDelayString);
                    } catch (RuntimeException e) {
                        throw new IllegalArgumentException("Invalid initialDelayString value \"" + initialDelayString + "\" - cannot parse into long");
                    }
                }
            }
            String cron = scheduled.cron();
            if (StringUtils.hasText(cron)) {
                String zone = scheduled.zone();
                if (this.embeddedValueResolver != null) {
                    cron = this.embeddedValueResolver.resolveStringValue(cron);
                    zone = this.embeddedValueResolver.resolveStringValue(zone);
                }
                if (StringUtils.hasLength(cron)) {
                    Assert.isTrue(initialDelay == -1, "'initialDelay' not supported for cron triggers");
                    processedSchedule = true;
                    if (!"-".equals(cron)) {
                        if (StringUtils.hasText(zone)) {
                            timeZone = StringUtils.parseTimeZoneString(zone);
                        } else {
                            timeZone = TimeZone.getDefault();
                        }
                        tasks.add(this.registrar.scheduleCronTask(new CronTask(runnable, new CronTrigger(cron, timeZone))));
                    }
                }
            }
            if (initialDelay < 0) {
                initialDelay = 0;
            }
            long fixedDelay = scheduled.fixedDelay();
            if (fixedDelay >= 0) {
                Assert.isTrue(!processedSchedule, "Exactly one of the 'cron', 'fixedDelay(String)', or 'fixedRate(String)' attributes is required");
                processedSchedule = true;
                tasks.add(this.registrar.scheduleFixedDelayTask(new FixedDelayTask(runnable, fixedDelay, initialDelay)));
            }
            String fixedDelayString = scheduled.fixedDelayString();
            if (StringUtils.hasText(fixedDelayString)) {
                if (this.embeddedValueResolver != null) {
                    fixedDelayString = this.embeddedValueResolver.resolveStringValue(fixedDelayString);
                }
                if (StringUtils.hasLength(fixedDelayString)) {
                    Assert.isTrue(!processedSchedule, "Exactly one of the 'cron', 'fixedDelay(String)', or 'fixedRate(String)' attributes is required");
                    processedSchedule = true;
                    try {
                        tasks.add(this.registrar.scheduleFixedDelayTask(new FixedDelayTask(runnable, parseDelayAsLong(fixedDelayString), initialDelay)));
                    } catch (RuntimeException e2) {
                        throw new IllegalArgumentException("Invalid fixedDelayString value \"" + fixedDelayString + "\" - cannot parse into long");
                    }
                }
            }
            long fixedRate = scheduled.fixedRate();
            if (fixedRate >= 0) {
                Assert.isTrue(!processedSchedule, "Exactly one of the 'cron', 'fixedDelay(String)', or 'fixedRate(String)' attributes is required");
                processedSchedule = true;
                tasks.add(this.registrar.scheduleFixedRateTask(new FixedRateTask(runnable, fixedRate, initialDelay)));
            }
            String fixedRateString = scheduled.fixedRateString();
            if (StringUtils.hasText(fixedRateString)) {
                if (this.embeddedValueResolver != null) {
                    fixedRateString = this.embeddedValueResolver.resolveStringValue(fixedRateString);
                }
                if (StringUtils.hasLength(fixedRateString)) {
                    Assert.isTrue(!processedSchedule, "Exactly one of the 'cron', 'fixedDelay(String)', or 'fixedRate(String)' attributes is required");
                    processedSchedule = true;
                    try {
                        tasks.add(this.registrar.scheduleFixedRateTask(new FixedRateTask(runnable, parseDelayAsLong(fixedRateString), initialDelay)));
                    } catch (RuntimeException e3) {
                        throw new IllegalArgumentException("Invalid fixedRateString value \"" + fixedRateString + "\" - cannot parse into long");
                    }
                }
            }
            Assert.isTrue(processedSchedule, "Exactly one of the 'cron', 'fixedDelay(String)', or 'fixedRate(String)' attributes is required");
            synchronized (this.scheduledTasks) {
                Set<ScheduledTask> regTasks = this.scheduledTasks.computeIfAbsent(bean, key -> {
                    return new LinkedHashSet(4);
                });
                regTasks.addAll(tasks);
            }
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("Encountered invalid @Scheduled method '" + method.getName() + "': " + ex.getMessage());
        }
    }

    protected Runnable createRunnable(Object target, Method method) {
        Assert.isTrue(method.getParameterCount() == 0, "Only no-arg methods may be annotated with @Scheduled");
        Method invocableMethod = AopUtils.selectInvocableMethod(method, target.getClass());
        return new ScheduledMethodRunnable(target, invocableMethod);
    }

    private static long parseDelayAsLong(String value) throws RuntimeException {
        if (value.length() > 1 && (isP(value.charAt(0)) || isP(value.charAt(1)))) {
            return Duration.parse(value).toMillis();
        }
        return Long.parseLong(value);
    }

    private static boolean isP(char ch2) {
        return ch2 == 'P' || ch2 == 'p';
    }

    @Override // org.springframework.scheduling.config.ScheduledTaskHolder
    public Set<ScheduledTask> getScheduledTasks() {
        Set<ScheduledTask> result = new LinkedHashSet<>();
        synchronized (this.scheduledTasks) {
            Collection<Set<ScheduledTask>> allTasks = this.scheduledTasks.values();
            for (Set<ScheduledTask> tasks : allTasks) {
                result.addAll(tasks);
            }
        }
        result.addAll(this.registrar.getScheduledTasks());
        return result;
    }

    @Override // org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor
    public void postProcessBeforeDestruction(Object bean, String beanName) {
        Set<ScheduledTask> tasks;
        synchronized (this.scheduledTasks) {
            tasks = this.scheduledTasks.remove(bean);
        }
        if (tasks != null) {
            for (ScheduledTask task : tasks) {
                task.cancel();
            }
        }
    }

    @Override // org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor
    public boolean requiresDestruction(Object bean) {
        boolean containsKey;
        synchronized (this.scheduledTasks) {
            containsKey = this.scheduledTasks.containsKey(bean);
        }
        return containsKey;
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() {
        synchronized (this.scheduledTasks) {
            Collection<Set<ScheduledTask>> allTasks = this.scheduledTasks.values();
            for (Set<ScheduledTask> tasks : allTasks) {
                for (ScheduledTask task : tasks) {
                    task.cancel();
                }
            }
            this.scheduledTasks.clear();
        }
        this.registrar.destroy();
    }
}