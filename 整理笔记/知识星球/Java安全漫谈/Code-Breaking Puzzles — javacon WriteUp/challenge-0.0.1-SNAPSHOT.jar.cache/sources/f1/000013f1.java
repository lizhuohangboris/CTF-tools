package org.springframework.beans.factory.config;

import java.beans.PropertyEditor;
import java.security.AccessControlContext;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/config/ConfigurableBeanFactory.class */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {
    public static final String SCOPE_SINGLETON = "singleton";
    public static final String SCOPE_PROTOTYPE = "prototype";

    void setParentBeanFactory(BeanFactory beanFactory) throws IllegalStateException;

    void setBeanClassLoader(@Nullable ClassLoader classLoader);

    @Nullable
    ClassLoader getBeanClassLoader();

    void setTempClassLoader(@Nullable ClassLoader classLoader);

    @Nullable
    ClassLoader getTempClassLoader();

    void setCacheBeanMetadata(boolean z);

    boolean isCacheBeanMetadata();

    void setBeanExpressionResolver(@Nullable BeanExpressionResolver beanExpressionResolver);

    @Nullable
    BeanExpressionResolver getBeanExpressionResolver();

    void setConversionService(@Nullable ConversionService conversionService);

    @Nullable
    ConversionService getConversionService();

    void addPropertyEditorRegistrar(PropertyEditorRegistrar propertyEditorRegistrar);

    void registerCustomEditor(Class<?> cls, Class<? extends PropertyEditor> cls2);

    void copyRegisteredEditorsTo(PropertyEditorRegistry propertyEditorRegistry);

    void setTypeConverter(TypeConverter typeConverter);

    TypeConverter getTypeConverter();

    void addEmbeddedValueResolver(StringValueResolver stringValueResolver);

    boolean hasEmbeddedValueResolver();

    @Nullable
    String resolveEmbeddedValue(String str);

    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    int getBeanPostProcessorCount();

    void registerScope(String str, Scope scope);

    String[] getRegisteredScopeNames();

    @Nullable
    Scope getRegisteredScope(String str);

    AccessControlContext getAccessControlContext();

    void copyConfigurationFrom(ConfigurableBeanFactory configurableBeanFactory);

    void registerAlias(String str, String str2) throws BeanDefinitionStoreException;

    void resolveAliases(StringValueResolver stringValueResolver);

    BeanDefinition getMergedBeanDefinition(String str) throws NoSuchBeanDefinitionException;

    boolean isFactoryBean(String str) throws NoSuchBeanDefinitionException;

    void setCurrentlyInCreation(String str, boolean z);

    boolean isCurrentlyInCreation(String str);

    void registerDependentBean(String str, String str2);

    String[] getDependentBeans(String str);

    String[] getDependenciesForBean(String str);

    void destroyBean(String str, Object obj);

    void destroyScopedBean(String str);

    void destroySingletons();
}