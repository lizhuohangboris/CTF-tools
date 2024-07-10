package org.springframework.context.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.lang.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/support/PostProcessorRegistrationDelegate.class */
public final class PostProcessorRegistrationDelegate {
    private PostProcessorRegistrationDelegate() {
    }

    public static void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {
        Set<String> processedBeans = new HashSet<>();
        if (beanFactory instanceof BeanDefinitionRegistry) {
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
            List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
            List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();
            for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
                if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
                    BeanDefinitionRegistryPostProcessor registryProcessor = (BeanDefinitionRegistryPostProcessor) postProcessor;
                    registryProcessor.postProcessBeanDefinitionRegistry(registry);
                    registryProcessors.add(registryProcessor);
                } else {
                    regularPostProcessors.add(postProcessor);
                }
            }
            ArrayList arrayList = new ArrayList();
            String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
            for (String ppName : postProcessorNames) {
                if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                    arrayList.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                    processedBeans.add(ppName);
                }
            }
            sortPostProcessors(arrayList, beanFactory);
            registryProcessors.addAll(arrayList);
            invokeBeanDefinitionRegistryPostProcessors(arrayList, registry);
            arrayList.clear();
            String[] postProcessorNames2 = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
            for (String ppName2 : postProcessorNames2) {
                if (!processedBeans.contains(ppName2) && beanFactory.isTypeMatch(ppName2, Ordered.class)) {
                    arrayList.add(beanFactory.getBean(ppName2, BeanDefinitionRegistryPostProcessor.class));
                    processedBeans.add(ppName2);
                }
            }
            sortPostProcessors(arrayList, beanFactory);
            registryProcessors.addAll(arrayList);
            invokeBeanDefinitionRegistryPostProcessors(arrayList, registry);
            arrayList.clear();
            boolean reiterate = true;
            while (reiterate) {
                reiterate = false;
                String[] postProcessorNames3 = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
                for (String ppName3 : postProcessorNames3) {
                    if (!processedBeans.contains(ppName3)) {
                        arrayList.add(beanFactory.getBean(ppName3, BeanDefinitionRegistryPostProcessor.class));
                        processedBeans.add(ppName3);
                        reiterate = true;
                    }
                }
                sortPostProcessors(arrayList, beanFactory);
                registryProcessors.addAll(arrayList);
                invokeBeanDefinitionRegistryPostProcessors(arrayList, registry);
                arrayList.clear();
            }
            invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
            invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
        } else {
            invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
        }
        String[] postProcessorNames4 = beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);
        ArrayList arrayList2 = new ArrayList();
        List<String> orderedPostProcessorNames = new ArrayList<>();
        List<String> nonOrderedPostProcessorNames = new ArrayList<>();
        for (String ppName4 : postProcessorNames4) {
            if (!processedBeans.contains(ppName4)) {
                if (beanFactory.isTypeMatch(ppName4, PriorityOrdered.class)) {
                    arrayList2.add(beanFactory.getBean(ppName4, BeanFactoryPostProcessor.class));
                } else if (beanFactory.isTypeMatch(ppName4, Ordered.class)) {
                    orderedPostProcessorNames.add(ppName4);
                } else {
                    nonOrderedPostProcessorNames.add(ppName4);
                }
            }
        }
        sortPostProcessors(arrayList2, beanFactory);
        invokeBeanFactoryPostProcessors(arrayList2, beanFactory);
        ArrayList arrayList3 = new ArrayList();
        for (String postProcessorName : orderedPostProcessorNames) {
            arrayList3.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
        }
        sortPostProcessors(arrayList3, beanFactory);
        invokeBeanFactoryPostProcessors(arrayList3, beanFactory);
        ArrayList arrayList4 = new ArrayList();
        for (String postProcessorName2 : nonOrderedPostProcessorNames) {
            arrayList4.add(beanFactory.getBean(postProcessorName2, BeanFactoryPostProcessor.class));
        }
        invokeBeanFactoryPostProcessors(arrayList4, beanFactory);
        beanFactory.clearMetadataCache();
    }

    public static void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {
        String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);
        int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
        beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));
        List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
        List<BeanPostProcessor> internalPostProcessors = new ArrayList<>();
        List<String> orderedPostProcessorNames = new ArrayList<>();
        List<String> nonOrderedPostProcessorNames = new ArrayList<>();
        for (String ppName : postProcessorNames) {
            if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                BeanPostProcessor pp = (BeanPostProcessor) beanFactory.getBean(ppName, BeanPostProcessor.class);
                priorityOrderedPostProcessors.add(pp);
                if (pp instanceof MergedBeanDefinitionPostProcessor) {
                    internalPostProcessors.add(pp);
                }
            } else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
                orderedPostProcessorNames.add(ppName);
            } else {
                nonOrderedPostProcessorNames.add(ppName);
            }
        }
        sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
        registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);
        List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>();
        for (String ppName2 : orderedPostProcessorNames) {
            BeanPostProcessor pp2 = (BeanPostProcessor) beanFactory.getBean(ppName2, BeanPostProcessor.class);
            orderedPostProcessors.add(pp2);
            if (pp2 instanceof MergedBeanDefinitionPostProcessor) {
                internalPostProcessors.add(pp2);
            }
        }
        sortPostProcessors(orderedPostProcessors, beanFactory);
        registerBeanPostProcessors(beanFactory, orderedPostProcessors);
        List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
        for (String ppName3 : nonOrderedPostProcessorNames) {
            BeanPostProcessor pp3 = (BeanPostProcessor) beanFactory.getBean(ppName3, BeanPostProcessor.class);
            nonOrderedPostProcessors.add(pp3);
            if (pp3 instanceof MergedBeanDefinitionPostProcessor) {
                internalPostProcessors.add(pp3);
            }
        }
        registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);
        sortPostProcessors(internalPostProcessors, beanFactory);
        registerBeanPostProcessors(beanFactory, internalPostProcessors);
        beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
    }

    private static void sortPostProcessors(List<?> postProcessors, ConfigurableListableBeanFactory beanFactory) {
        Comparator<Object> comparatorToUse = null;
        if (beanFactory instanceof DefaultListableBeanFactory) {
            comparatorToUse = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
        }
        if (comparatorToUse == null) {
            comparatorToUse = OrderComparator.INSTANCE;
        }
        postProcessors.sort(comparatorToUse);
    }

    private static void invokeBeanDefinitionRegistryPostProcessors(Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {
        for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
            postProcessor.postProcessBeanDefinitionRegistry(registry);
        }
    }

    private static void invokeBeanFactoryPostProcessors(Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {
        for (BeanFactoryPostProcessor postProcessor : postProcessors) {
            postProcessor.postProcessBeanFactory(beanFactory);
        }
    }

    private static void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory, List<BeanPostProcessor> postProcessors) {
        for (BeanPostProcessor postProcessor : postProcessors) {
            beanFactory.addBeanPostProcessor(postProcessor);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/support/PostProcessorRegistrationDelegate$BeanPostProcessorChecker.class */
    public static final class BeanPostProcessorChecker implements BeanPostProcessor {
        private static final Log logger = LogFactory.getLog(BeanPostProcessorChecker.class);
        private final ConfigurableListableBeanFactory beanFactory;
        private final int beanPostProcessorTargetCount;

        public BeanPostProcessorChecker(ConfigurableListableBeanFactory beanFactory, int beanPostProcessorTargetCount) {
            this.beanFactory = beanFactory;
            this.beanPostProcessorTargetCount = beanPostProcessorTargetCount;
        }

        @Override // org.springframework.beans.factory.config.BeanPostProcessor
        public Object postProcessBeforeInitialization(Object bean, String beanName) {
            return bean;
        }

        @Override // org.springframework.beans.factory.config.BeanPostProcessor
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            if (!(bean instanceof BeanPostProcessor) && !isInfrastructureBean(beanName) && this.beanFactory.getBeanPostProcessorCount() < this.beanPostProcessorTargetCount && logger.isInfoEnabled()) {
                logger.info("Bean '" + beanName + "' of type [" + bean.getClass().getName() + "] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)");
            }
            return bean;
        }

        private boolean isInfrastructureBean(@Nullable String beanName) {
            if (beanName != null && this.beanFactory.containsBeanDefinition(beanName)) {
                BeanDefinition bd = this.beanFactory.getBeanDefinition(beanName);
                return bd.getRole() == 2;
            }
            return false;
        }
    }
}