package org.springframework.web.servlet.view.tiles3;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;
import javax.servlet.ServletContext;
import javax.servlet.jsp.JspFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tiles.TilesContainer;
import org.apache.tiles.TilesException;
import org.apache.tiles.definition.DefinitionsFactory;
import org.apache.tiles.definition.DefinitionsReader;
import org.apache.tiles.definition.dao.BaseLocaleUrlDefinitionDAO;
import org.apache.tiles.definition.dao.CachingLocaleUrlDefinitionDAO;
import org.apache.tiles.definition.digester.DigesterDefinitionsReader;
import org.apache.tiles.el.ELAttributeEvaluator;
import org.apache.tiles.el.ScopeELResolver;
import org.apache.tiles.el.TilesContextBeanELResolver;
import org.apache.tiles.el.TilesContextELResolver;
import org.apache.tiles.evaluator.AttributeEvaluator;
import org.apache.tiles.evaluator.AttributeEvaluatorFactory;
import org.apache.tiles.evaluator.BasicAttributeEvaluatorFactory;
import org.apache.tiles.evaluator.impl.DirectAttributeEvaluator;
import org.apache.tiles.extras.complete.CompleteAutoloadTilesContainerFactory;
import org.apache.tiles.extras.complete.CompleteAutoloadTilesInitializer;
import org.apache.tiles.factory.AbstractTilesContainerFactory;
import org.apache.tiles.factory.BasicTilesContainerFactory;
import org.apache.tiles.impl.mgmt.CachingTilesContainer;
import org.apache.tiles.locale.LocaleResolver;
import org.apache.tiles.preparer.factory.PreparerFactory;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationContextAware;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.startup.DefaultTilesInitializer;
import org.apache.tiles.startup.TilesInitializer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.DispatcherServlet;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/tiles3/TilesConfigurer.class */
public class TilesConfigurer implements ServletContextAware, InitializingBean, DisposableBean {
    private static final boolean tilesElPresent = ClassUtils.isPresent("org.apache.tiles.el.ELAttributeEvaluator", TilesConfigurer.class.getClassLoader());
    @Nullable
    private TilesInitializer tilesInitializer;
    @Nullable
    private String[] definitions;
    @Nullable
    private Class<? extends DefinitionsFactory> definitionsFactoryClass;
    @Nullable
    private Class<? extends PreparerFactory> preparerFactoryClass;
    @Nullable
    private ServletContext servletContext;
    protected final Log logger = LogFactory.getLog(getClass());
    private boolean checkRefresh = false;
    private boolean validateDefinitions = true;
    private boolean useMutableTilesContainer = false;

    public void setTilesInitializer(TilesInitializer tilesInitializer) {
        this.tilesInitializer = tilesInitializer;
    }

    public void setCompleteAutoload(boolean completeAutoload) {
        if (completeAutoload) {
            try {
                this.tilesInitializer = new SpringCompleteAutoloadTilesInitializer();
                return;
            } catch (Throwable ex) {
                throw new IllegalStateException("Tiles-Extras 3.0 not available", ex);
            }
        }
        this.tilesInitializer = null;
    }

    public void setDefinitions(String... definitions) {
        this.definitions = definitions;
    }

    public void setCheckRefresh(boolean checkRefresh) {
        this.checkRefresh = checkRefresh;
    }

    public void setValidateDefinitions(boolean validateDefinitions) {
        this.validateDefinitions = validateDefinitions;
    }

    public void setDefinitionsFactoryClass(Class<? extends DefinitionsFactory> definitionsFactoryClass) {
        this.definitionsFactoryClass = definitionsFactoryClass;
    }

    public void setPreparerFactoryClass(Class<? extends PreparerFactory> preparerFactoryClass) {
        this.preparerFactoryClass = preparerFactoryClass;
    }

    public void setUseMutableTilesContainer(boolean useMutableTilesContainer) {
        this.useMutableTilesContainer = useMutableTilesContainer;
    }

    @Override // org.springframework.web.context.ServletContextAware
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws TilesException {
        Assert.state(this.servletContext != null, "No ServletContext available");
        SpringWildcardServletTilesApplicationContext springWildcardServletTilesApplicationContext = new SpringWildcardServletTilesApplicationContext(this.servletContext);
        if (this.tilesInitializer == null) {
            this.tilesInitializer = new SpringTilesInitializer();
        }
        this.tilesInitializer.initialize(springWildcardServletTilesApplicationContext);
    }

    @Override // org.springframework.beans.factory.DisposableBean
    public void destroy() throws TilesException {
        if (this.tilesInitializer != null) {
            this.tilesInitializer.destroy();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/tiles3/TilesConfigurer$SpringTilesInitializer.class */
    private class SpringTilesInitializer extends DefaultTilesInitializer {
        private SpringTilesInitializer() {
        }

        protected AbstractTilesContainerFactory createContainerFactory(ApplicationContext context) {
            return new SpringTilesContainerFactory();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/tiles3/TilesConfigurer$SpringTilesContainerFactory.class */
    private class SpringTilesContainerFactory extends BasicTilesContainerFactory {
        private SpringTilesContainerFactory() {
        }

        protected TilesContainer createDecoratedContainer(TilesContainer originalContainer, ApplicationContext context) {
            return TilesConfigurer.this.useMutableTilesContainer ? new CachingTilesContainer(originalContainer) : originalContainer;
        }

        protected List<ApplicationResource> getSources(ApplicationContext applicationContext) {
            String[] strArr;
            if (TilesConfigurer.this.definitions != null) {
                List<ApplicationResource> result = new LinkedList<>();
                for (String definition : TilesConfigurer.this.definitions) {
                    Collection<? extends ApplicationResource> resources = applicationContext.getResources(definition);
                    if (resources != null) {
                        result.addAll(resources);
                    }
                }
                return result;
            }
            return super.getSources(applicationContext);
        }

        protected BaseLocaleUrlDefinitionDAO instantiateLocaleDefinitionDao(ApplicationContext applicationContext, LocaleResolver resolver) {
            CachingLocaleUrlDefinitionDAO instantiateLocaleDefinitionDao = super.instantiateLocaleDefinitionDao(applicationContext, resolver);
            if (TilesConfigurer.this.checkRefresh && (instantiateLocaleDefinitionDao instanceof CachingLocaleUrlDefinitionDAO)) {
                instantiateLocaleDefinitionDao.setCheckRefresh(true);
            }
            return instantiateLocaleDefinitionDao;
        }

        protected DefinitionsReader createDefinitionsReader(ApplicationContext context) {
            DigesterDefinitionsReader reader = super.createDefinitionsReader(context);
            reader.setValidating(TilesConfigurer.this.validateDefinitions);
            return reader;
        }

        protected DefinitionsFactory createDefinitionsFactory(ApplicationContext applicationContext, LocaleResolver resolver) {
            if (TilesConfigurer.this.definitionsFactoryClass != null) {
                ApplicationContextAware applicationContextAware = (DefinitionsFactory) BeanUtils.instantiateClass(TilesConfigurer.this.definitionsFactoryClass);
                if (applicationContextAware instanceof ApplicationContextAware) {
                    applicationContextAware.setApplicationContext(applicationContext);
                }
                BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(applicationContextAware);
                if (bw.isWritableProperty(DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME)) {
                    bw.setPropertyValue(DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME, resolver);
                }
                if (bw.isWritableProperty("definitionDAO")) {
                    bw.setPropertyValue("definitionDAO", createLocaleDefinitionDao(applicationContext, resolver));
                }
                return applicationContextAware;
            }
            return super.createDefinitionsFactory(applicationContext, resolver);
        }

        protected PreparerFactory createPreparerFactory(ApplicationContext context) {
            if (TilesConfigurer.this.preparerFactoryClass != null) {
                return (PreparerFactory) BeanUtils.instantiateClass(TilesConfigurer.this.preparerFactoryClass);
            }
            return super.createPreparerFactory(context);
        }

        protected LocaleResolver createLocaleResolver(ApplicationContext context) {
            return new SpringLocaleResolver();
        }

        protected AttributeEvaluatorFactory createAttributeEvaluatorFactory(ApplicationContext context, LocaleResolver resolver) {
            AttributeEvaluator evaluator;
            if (TilesConfigurer.tilesElPresent && JspFactory.getDefaultFactory() != null) {
                evaluator = new TilesElActivator().createEvaluator();
            } else {
                evaluator = new DirectAttributeEvaluator();
            }
            return new BasicAttributeEvaluatorFactory(evaluator);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/tiles3/TilesConfigurer$SpringCompleteAutoloadTilesInitializer.class */
    private static class SpringCompleteAutoloadTilesInitializer extends CompleteAutoloadTilesInitializer {
        private SpringCompleteAutoloadTilesInitializer() {
        }

        protected AbstractTilesContainerFactory createContainerFactory(ApplicationContext context) {
            return new SpringCompleteAutoloadTilesContainerFactory();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/tiles3/TilesConfigurer$SpringCompleteAutoloadTilesContainerFactory.class */
    private static class SpringCompleteAutoloadTilesContainerFactory extends CompleteAutoloadTilesContainerFactory {
        private SpringCompleteAutoloadTilesContainerFactory() {
        }

        protected LocaleResolver createLocaleResolver(ApplicationContext applicationContext) {
            return new SpringLocaleResolver();
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/tiles3/TilesConfigurer$TilesElActivator.class */
    private class TilesElActivator {
        private TilesElActivator() {
        }

        public AttributeEvaluator createEvaluator() {
            ELAttributeEvaluator evaluator = new ELAttributeEvaluator();
            evaluator.setExpressionFactory(JspFactory.getDefaultFactory().getJspApplicationContext(TilesConfigurer.this.servletContext).getExpressionFactory());
            evaluator.setResolver(new CompositeELResolverImpl());
            return evaluator;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/view/tiles3/TilesConfigurer$CompositeELResolverImpl.class */
    public static class CompositeELResolverImpl extends CompositeELResolver {
        public CompositeELResolverImpl() {
            add(new ScopeELResolver());
            add(new TilesContextELResolver(new TilesContextBeanELResolver()));
            add(new TilesContextBeanELResolver());
            add(new ArrayELResolver(false));
            add(new ListELResolver(false));
            add(new MapELResolver(false));
            add(new ResourceBundleELResolver());
            add(new BeanELResolver(false));
        }
    }
}