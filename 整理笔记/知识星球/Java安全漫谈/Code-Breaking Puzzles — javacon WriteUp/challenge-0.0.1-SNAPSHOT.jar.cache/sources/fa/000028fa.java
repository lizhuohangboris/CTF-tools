package org.thymeleaf.spring5.view;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.spring5.ISpringTemplateEngine;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/view/ThymeleafViewResolver.class */
public class ThymeleafViewResolver extends AbstractCachingViewResolver implements Ordered {
    private static final Logger vrlogger = LoggerFactory.getLogger(ThymeleafViewResolver.class);
    public static final String REDIRECT_URL_PREFIX = "redirect:";
    public static final String FORWARD_URL_PREFIX = "forward:";
    private boolean redirectContextRelative = true;
    private boolean redirectHttp10Compatible = true;
    private boolean alwaysProcessRedirectAndForward = true;
    private boolean producePartialOutputWhileProcessing = true;
    private Class<? extends AbstractThymeleafView> viewClass = ThymeleafView.class;
    private String[] viewNames = null;
    private String[] excludedViewNames = null;
    private int order = Integer.MAX_VALUE;
    private final Map<String, Object> staticVariables = new LinkedHashMap(10);
    private String contentType = null;
    private boolean forceContentType = false;
    private String characterEncoding = null;
    private ISpringTemplateEngine templateEngine;

    public void setViewClass(Class<? extends AbstractThymeleafView> viewClass) {
        if (viewClass == null || !AbstractThymeleafView.class.isAssignableFrom(viewClass)) {
            throw new IllegalArgumentException("Given view class [" + (viewClass != null ? viewClass.getName() : null) + "] is not of type [" + AbstractThymeleafView.class.getName() + "]");
        }
        this.viewClass = viewClass;
    }

    protected Class<? extends AbstractThymeleafView> getViewClass() {
        return this.viewClass;
    }

    public ISpringTemplateEngine getTemplateEngine() {
        return this.templateEngine;
    }

    public void setTemplateEngine(ISpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public Map<String, Object> getStaticVariables() {
        return Collections.unmodifiableMap(this.staticVariables);
    }

    public void addStaticVariable(String name, Object value) {
        this.staticVariables.put(name, value);
    }

    public void setStaticVariables(Map<String, ?> variables) {
        if (variables != null) {
            for (Map.Entry<String, ?> entry : variables.entrySet()) {
                addStaticVariable(entry.getKey(), entry.getValue());
            }
        }
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return this.contentType;
    }

    public boolean getForceContentType() {
        return this.forceContentType;
    }

    public void setForceContentType(boolean forceContentType) {
        this.forceContentType = forceContentType;
    }

    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    public void setRedirectContextRelative(boolean redirectContextRelative) {
        this.redirectContextRelative = redirectContextRelative;
    }

    public boolean isRedirectContextRelative() {
        return this.redirectContextRelative;
    }

    public void setRedirectHttp10Compatible(boolean redirectHttp10Compatible) {
        this.redirectHttp10Compatible = redirectHttp10Compatible;
    }

    public boolean isRedirectHttp10Compatible() {
        return this.redirectHttp10Compatible;
    }

    public void setAlwaysProcessRedirectAndForward(boolean alwaysProcessRedirectAndForward) {
        this.alwaysProcessRedirectAndForward = alwaysProcessRedirectAndForward;
    }

    public boolean getAlwaysProcessRedirectAndForward() {
        return this.alwaysProcessRedirectAndForward;
    }

    public boolean getProducePartialOutputWhileProcessing() {
        return this.producePartialOutputWhileProcessing;
    }

    public void setProducePartialOutputWhileProcessing(boolean producePartialOutputWhileProcessing) {
        this.producePartialOutputWhileProcessing = producePartialOutputWhileProcessing;
    }

    public void setViewNames(String[] viewNames) {
        this.viewNames = viewNames;
    }

    public String[] getViewNames() {
        return this.viewNames;
    }

    public void setExcludedViewNames(String[] excludedViewNames) {
        this.excludedViewNames = excludedViewNames;
    }

    public String[] getExcludedViewNames() {
        return this.excludedViewNames;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean canHandle(String viewName, Locale locale) {
        String[] viewNamesToBeProcessed = getViewNames();
        String[] viewNamesNotToBeProcessed = getExcludedViewNames();
        return (viewNamesToBeProcessed == null || PatternMatchUtils.simpleMatch(viewNamesToBeProcessed, viewName)) && (viewNamesNotToBeProcessed == null || !PatternMatchUtils.simpleMatch(viewNamesNotToBeProcessed, viewName));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.web.servlet.view.AbstractCachingViewResolver
    public View createView(String viewName, Locale locale) throws Exception {
        if (!this.alwaysProcessRedirectAndForward && !canHandle(viewName, locale)) {
            vrlogger.trace("[THYMELEAF] View \"{}\" cannot be handled by ThymeleafViewResolver. Passing on to the next resolver in the chain.", viewName);
            return null;
        } else if (viewName.startsWith("redirect:")) {
            vrlogger.trace("[THYMELEAF] View \"{}\" is a redirect, and will not be handled directly by ThymeleafViewResolver.", viewName);
            String redirectUrl = viewName.substring("redirect:".length(), viewName.length());
            RedirectView view = new RedirectView(redirectUrl, isRedirectContextRelative(), isRedirectHttp10Compatible());
            return (View) getApplicationContext().getAutowireCapableBeanFactory().initializeBean(view, viewName);
        } else if (viewName.startsWith("forward:")) {
            vrlogger.trace("[THYMELEAF] View \"{}\" is a forward, and will not be handled directly by ThymeleafViewResolver.", viewName);
            String forwardUrl = viewName.substring("forward:".length(), viewName.length());
            return new InternalResourceView(forwardUrl);
        } else if (this.alwaysProcessRedirectAndForward && !canHandle(viewName, locale)) {
            vrlogger.trace("[THYMELEAF] View \"{}\" cannot be handled by ThymeleafViewResolver. Passing on to the next resolver in the chain.", viewName);
            return null;
        } else {
            vrlogger.trace("[THYMELEAF] View {} will be handled by ThymeleafViewResolver and a {} instance will be created for it", viewName, getViewClass().getSimpleName());
            return loadView(viewName, locale);
        }
    }

    @Override // org.springframework.web.servlet.view.AbstractCachingViewResolver
    protected View loadView(String viewName, Locale locale) throws Exception {
        AbstractThymeleafView view;
        BeanDefinition beanDefinition;
        AutowireCapableBeanFactory beanFactory = getApplicationContext().getAutowireCapableBeanFactory();
        boolean viewBeanExists = beanFactory.containsBean(viewName);
        Class<?> viewBeanType = viewBeanExists ? beanFactory.getType(viewName) : null;
        if (viewBeanExists && viewBeanType != null && AbstractThymeleafView.class.isAssignableFrom(viewBeanType)) {
            if (beanFactory instanceof ConfigurableListableBeanFactory) {
                beanDefinition = ((ConfigurableListableBeanFactory) beanFactory).getBeanDefinition(viewName);
            } else {
                beanDefinition = null;
            }
            BeanDefinition viewBeanDefinition = beanDefinition;
            if (viewBeanDefinition == null || !viewBeanDefinition.isPrototype()) {
                view = (AbstractThymeleafView) beanFactory.configureBean((AbstractThymeleafView) BeanUtils.instantiateClass(getViewClass()), viewName);
            } else {
                view = (AbstractThymeleafView) beanFactory.getBean(viewName);
            }
        } else {
            AbstractThymeleafView viewInstance = (AbstractThymeleafView) BeanUtils.instantiateClass(getViewClass());
            if (viewBeanExists && viewBeanType == null) {
                beanFactory.autowireBeanProperties(viewInstance, 0, false);
                beanFactory.applyBeanPropertyValues(viewInstance, viewName);
                view = (AbstractThymeleafView) beanFactory.initializeBean(viewInstance, viewName);
            } else {
                beanFactory.autowireBeanProperties(viewInstance, 0, false);
                view = (AbstractThymeleafView) beanFactory.initializeBean(viewInstance, viewName);
            }
        }
        view.setTemplateEngine(getTemplateEngine());
        view.setStaticVariables(getStaticVariables());
        if (view.getTemplateName() == null) {
            view.setTemplateName(viewName);
        }
        if (!view.isForceContentTypeSet()) {
            view.setForceContentType(getForceContentType());
        }
        if (!view.isContentTypeSet() && getContentType() != null) {
            view.setContentType(getContentType());
        }
        if (view.getLocale() == null && locale != null) {
            view.setLocale(locale);
        }
        if (view.getCharacterEncoding() == null && getCharacterEncoding() != null) {
            view.setCharacterEncoding(getCharacterEncoding());
        }
        if (!view.isProducePartialOutputWhileProcessingSet()) {
            view.setProducePartialOutputWhileProcessing(getProducePartialOutputWhileProcessing());
        }
        return view;
    }
}