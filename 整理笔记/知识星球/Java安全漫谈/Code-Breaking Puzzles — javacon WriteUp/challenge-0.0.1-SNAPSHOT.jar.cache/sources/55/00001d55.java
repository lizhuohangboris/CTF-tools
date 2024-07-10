package org.springframework.context.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.Lifecycle;
import org.springframework.context.LifecycleProcessor;
import org.springframework.context.Phased;
import org.springframework.context.SmartLifecycle;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.backoff.ExponentialBackOff;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/support/DefaultLifecycleProcessor.class */
public class DefaultLifecycleProcessor implements LifecycleProcessor, BeanFactoryAware {
    private final Log logger = LogFactory.getLog(getClass());
    private volatile long timeoutPerShutdownPhase = ExponentialBackOff.DEFAULT_MAX_INTERVAL;
    private volatile boolean running;
    @Nullable
    private volatile ConfigurableListableBeanFactory beanFactory;

    public void setTimeoutPerShutdownPhase(long timeoutPerShutdownPhase) {
        this.timeoutPerShutdownPhase = timeoutPerShutdownPhase;
    }

    @Override // org.springframework.beans.factory.BeanFactoryAware
    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException("DefaultLifecycleProcessor requires a ConfigurableListableBeanFactory: " + beanFactory);
        }
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    private ConfigurableListableBeanFactory getBeanFactory() {
        ConfigurableListableBeanFactory beanFactory = this.beanFactory;
        Assert.state(beanFactory != null, "No BeanFactory available");
        return beanFactory;
    }

    @Override // org.springframework.context.Lifecycle
    public void start() {
        startBeans(false);
        this.running = true;
    }

    @Override // org.springframework.context.Lifecycle
    public void stop() {
        stopBeans();
        this.running = false;
    }

    @Override // org.springframework.context.LifecycleProcessor
    public void onRefresh() {
        startBeans(true);
        this.running = true;
    }

    @Override // org.springframework.context.LifecycleProcessor
    public void onClose() {
        stopBeans();
        this.running = false;
    }

    @Override // org.springframework.context.Lifecycle
    public boolean isRunning() {
        return this.running;
    }

    private void startBeans(boolean autoStartupOnly) {
        Map<String, Lifecycle> lifecycleBeans = getLifecycleBeans();
        Map<Integer, LifecycleGroup> phases = new HashMap<>();
        lifecycleBeans.forEach(beanName, bean -> {
            if (!autoStartupOnly || ((bean instanceof SmartLifecycle) && ((SmartLifecycle) bean).isAutoStartup())) {
                int phase = getPhase(bean);
                LifecycleGroup group = (LifecycleGroup) phases.get(Integer.valueOf(phase));
                if (group == null) {
                    group = new LifecycleGroup(phase, this.timeoutPerShutdownPhase, lifecycleBeans, autoStartupOnly);
                    phases.put(Integer.valueOf(phase), group);
                }
                group.add(beanName, bean);
            }
        });
        if (!phases.isEmpty()) {
            List<Integer> keys = new ArrayList<>(phases.keySet());
            Collections.sort(keys);
            for (Integer key : keys) {
                phases.get(key).start();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doStart(Map<String, ? extends Lifecycle> lifecycleBeans, String beanName, boolean autoStartupOnly) {
        Lifecycle bean = lifecycleBeans.remove(beanName);
        if (bean != null && bean != this) {
            String[] dependenciesForBean = getBeanFactory().getDependenciesForBean(beanName);
            for (String dependency : dependenciesForBean) {
                doStart(lifecycleBeans, dependency, autoStartupOnly);
            }
            if (!bean.isRunning()) {
                if (!autoStartupOnly || !(bean instanceof SmartLifecycle) || ((SmartLifecycle) bean).isAutoStartup()) {
                    if (this.logger.isTraceEnabled()) {
                        this.logger.trace("Starting bean '" + beanName + "' of type [" + bean.getClass().getName() + "]");
                    }
                    try {
                        bean.start();
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Successfully started bean '" + beanName + "'");
                        }
                    } catch (Throwable ex) {
                        throw new ApplicationContextException("Failed to start bean '" + beanName + "'", ex);
                    }
                }
            }
        }
    }

    private void stopBeans() {
        Map<String, Lifecycle> lifecycleBeans = getLifecycleBeans();
        Map<Integer, LifecycleGroup> phases = new HashMap<>();
        lifecycleBeans.forEach(beanName, bean -> {
            int shutdownPhase = getPhase(bean);
            LifecycleGroup group = (LifecycleGroup) phases.get(Integer.valueOf(shutdownPhase));
            if (group == null) {
                group = new LifecycleGroup(shutdownPhase, this.timeoutPerShutdownPhase, lifecycleBeans, false);
                phases.put(Integer.valueOf(shutdownPhase), group);
            }
            group.add(beanName, bean);
        });
        if (!phases.isEmpty()) {
            List<Integer> keys = new ArrayList<>(phases.keySet());
            keys.sort(Collections.reverseOrder());
            for (Integer key : keys) {
                phases.get(key).stop();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doStop(Map<String, ? extends Lifecycle> lifecycleBeans, String beanName, CountDownLatch latch, Set<String> countDownBeanNames) {
        Lifecycle bean = lifecycleBeans.remove(beanName);
        if (bean != null) {
            String[] dependentBeans = getBeanFactory().getDependentBeans(beanName);
            for (String dependentBean : dependentBeans) {
                doStop(lifecycleBeans, dependentBean, latch, countDownBeanNames);
            }
            try {
                if (bean.isRunning()) {
                    if (bean instanceof SmartLifecycle) {
                        if (this.logger.isTraceEnabled()) {
                            this.logger.trace("Asking bean '" + beanName + "' of type [" + bean.getClass().getName() + "] to stop");
                        }
                        countDownBeanNames.add(beanName);
                        ((SmartLifecycle) bean).stop(() -> {
                            latch.countDown();
                            countDownBeanNames.remove(beanName);
                            if (this.logger.isDebugEnabled()) {
                                this.logger.debug("Bean '" + beanName + "' completed its stop procedure");
                            }
                        });
                    } else {
                        if (this.logger.isTraceEnabled()) {
                            this.logger.trace("Stopping bean '" + beanName + "' of type [" + bean.getClass().getName() + "]");
                        }
                        bean.stop();
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Successfully stopped bean '" + beanName + "'");
                        }
                    }
                } else if (bean instanceof SmartLifecycle) {
                    latch.countDown();
                }
            } catch (Throwable ex) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn("Failed to stop bean '" + beanName + "'", ex);
                }
            }
        }
    }

    protected Map<String, Lifecycle> getLifecycleBeans() {
        Object bean;
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        Map<String, Lifecycle> beans = new LinkedHashMap<>();
        String[] beanNames = beanFactory.getBeanNamesForType(Lifecycle.class, false, false);
        for (String beanName : beanNames) {
            String beanNameToRegister = BeanFactoryUtils.transformedBeanName(beanName);
            boolean isFactoryBean = beanFactory.isFactoryBean(beanNameToRegister);
            String beanNameToCheck = isFactoryBean ? BeanFactory.FACTORY_BEAN_PREFIX + beanName : beanName;
            if (((beanFactory.containsSingleton(beanNameToRegister) && (!isFactoryBean || matchesBeanType(Lifecycle.class, beanNameToCheck, beanFactory))) || matchesBeanType(SmartLifecycle.class, beanNameToCheck, beanFactory)) && (bean = beanFactory.getBean(beanNameToCheck)) != this && (bean instanceof Lifecycle)) {
                beans.put(beanNameToRegister, (Lifecycle) bean);
            }
        }
        return beans;
    }

    private boolean matchesBeanType(Class<?> targetType, String beanName, BeanFactory beanFactory) {
        Class<?> beanType = beanFactory.getType(beanName);
        return beanType != null && targetType.isAssignableFrom(beanType);
    }

    protected int getPhase(Lifecycle bean) {
        if (bean instanceof Phased) {
            return ((Phased) bean).getPhase();
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/support/DefaultLifecycleProcessor$LifecycleGroup.class */
    public class LifecycleGroup {
        private final int phase;
        private final long timeout;
        private final Map<String, ? extends Lifecycle> lifecycleBeans;
        private final boolean autoStartupOnly;
        private final List<LifecycleGroupMember> members = new ArrayList();
        private int smartMemberCount;

        public LifecycleGroup(int phase, long timeout, Map<String, ? extends Lifecycle> lifecycleBeans, boolean autoStartupOnly) {
            this.phase = phase;
            this.timeout = timeout;
            this.lifecycleBeans = lifecycleBeans;
            this.autoStartupOnly = autoStartupOnly;
        }

        public void add(String name, Lifecycle bean) {
            this.members.add(new LifecycleGroupMember(name, bean));
            if (bean instanceof SmartLifecycle) {
                this.smartMemberCount++;
            }
        }

        public void start() {
            if (!this.members.isEmpty()) {
                if (DefaultLifecycleProcessor.this.logger.isDebugEnabled()) {
                    DefaultLifecycleProcessor.this.logger.debug("Starting beans in phase " + this.phase);
                }
                Collections.sort(this.members);
                for (LifecycleGroupMember member : this.members) {
                    DefaultLifecycleProcessor.this.doStart(this.lifecycleBeans, member.name, this.autoStartupOnly);
                }
            }
        }

        public void stop() {
            if (!this.members.isEmpty()) {
                if (DefaultLifecycleProcessor.this.logger.isDebugEnabled()) {
                    DefaultLifecycleProcessor.this.logger.debug("Stopping beans in phase " + this.phase);
                }
                this.members.sort(Collections.reverseOrder());
                CountDownLatch latch = new CountDownLatch(this.smartMemberCount);
                Set<String> countDownBeanNames = Collections.synchronizedSet(new LinkedHashSet());
                Set<String> lifecycleBeanNames = new HashSet<>(this.lifecycleBeans.keySet());
                for (LifecycleGroupMember member : this.members) {
                    if (lifecycleBeanNames.contains(member.name)) {
                        DefaultLifecycleProcessor.this.doStop(this.lifecycleBeans, member.name, latch, countDownBeanNames);
                    } else if (member.bean instanceof SmartLifecycle) {
                        latch.countDown();
                    }
                }
                try {
                    latch.await(this.timeout, TimeUnit.MILLISECONDS);
                    if (latch.getCount() > 0 && !countDownBeanNames.isEmpty() && DefaultLifecycleProcessor.this.logger.isInfoEnabled()) {
                        DefaultLifecycleProcessor.this.logger.info("Failed to shut down " + countDownBeanNames.size() + " bean" + (countDownBeanNames.size() > 1 ? "s" : "") + " with phase value " + this.phase + " within timeout of " + this.timeout + ": " + countDownBeanNames);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/support/DefaultLifecycleProcessor$LifecycleGroupMember.class */
    public class LifecycleGroupMember implements Comparable<LifecycleGroupMember> {
        private final String name;
        private final Lifecycle bean;

        LifecycleGroupMember(String name, Lifecycle bean) {
            this.name = name;
            this.bean = bean;
        }

        @Override // java.lang.Comparable
        public int compareTo(LifecycleGroupMember other) {
            int thisPhase = DefaultLifecycleProcessor.this.getPhase(this.bean);
            int otherPhase = DefaultLifecycleProcessor.this.getPhase(other.bean);
            return Integer.compare(thisPhase, otherPhase);
        }
    }
}