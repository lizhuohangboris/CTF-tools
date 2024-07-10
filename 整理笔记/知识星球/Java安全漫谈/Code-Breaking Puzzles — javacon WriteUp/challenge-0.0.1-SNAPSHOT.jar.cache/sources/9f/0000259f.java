package org.springframework.web.servlet.config;

import java.util.List;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerProperties;
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateProperties;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.ViewResolverComposite;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.springframework.web.servlet.view.groovy.GroovyMarkupViewResolver;
import org.springframework.web.servlet.view.script.ScriptTemplateViewResolver;
import org.springframework.web.servlet.view.tiles3.TilesViewResolver;
import org.w3c.dom.Element;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/config/ViewResolversBeanDefinitionParser.class */
public class ViewResolversBeanDefinitionParser implements BeanDefinitionParser {
    public static final String VIEW_RESOLVER_BEAN_NAME = "mvcViewResolver";

    @Override // org.springframework.beans.factory.xml.BeanDefinitionParser
    public BeanDefinition parse(Element element, ParserContext context) {
        RootBeanDefinition resolverBeanDef;
        Object source = context.extractSource(element);
        context.pushContainingComponent(new CompositeComponentDefinition(element.getTagName(), source));
        ManagedList<Object> resolvers = new ManagedList<>(4);
        resolvers.setSource(context.extractSource(element));
        String[] names = {"jsp", "tiles", "bean-name", "freemarker", "groovy", "script-template", "bean", "ref"};
        for (Element resolverElement : DomUtils.getChildElementsByTagName(element, names)) {
            String name = resolverElement.getLocalName();
            if ("bean".equals(name) || "ref".equals(name)) {
                resolvers.add(context.getDelegate().parsePropertySubElement(resolverElement, null));
            } else {
                if ("jsp".equals(name)) {
                    resolverBeanDef = new RootBeanDefinition(InternalResourceViewResolver.class);
                    resolverBeanDef.getPropertyValues().add("prefix", "/WEB-INF/");
                    resolverBeanDef.getPropertyValues().add("suffix", ".jsp");
                    addUrlBasedViewResolverProperties(resolverElement, resolverBeanDef);
                } else if ("tiles".equals(name)) {
                    resolverBeanDef = new RootBeanDefinition(TilesViewResolver.class);
                    addUrlBasedViewResolverProperties(resolverElement, resolverBeanDef);
                } else if ("freemarker".equals(name)) {
                    resolverBeanDef = new RootBeanDefinition(FreeMarkerViewResolver.class);
                    resolverBeanDef.getPropertyValues().add("suffix", FreeMarkerProperties.DEFAULT_SUFFIX);
                    addUrlBasedViewResolverProperties(resolverElement, resolverBeanDef);
                } else if ("groovy".equals(name)) {
                    resolverBeanDef = new RootBeanDefinition(GroovyMarkupViewResolver.class);
                    resolverBeanDef.getPropertyValues().add("suffix", GroovyTemplateProperties.DEFAULT_SUFFIX);
                    addUrlBasedViewResolverProperties(resolverElement, resolverBeanDef);
                } else if ("script-template".equals(name)) {
                    resolverBeanDef = new RootBeanDefinition(ScriptTemplateViewResolver.class);
                    addUrlBasedViewResolverProperties(resolverElement, resolverBeanDef);
                } else if ("bean-name".equals(name)) {
                    resolverBeanDef = new RootBeanDefinition(BeanNameViewResolver.class);
                } else {
                    throw new IllegalStateException("Unexpected element name: " + name);
                }
                resolverBeanDef.setSource(source);
                resolverBeanDef.setRole(2);
                resolvers.add(resolverBeanDef);
            }
        }
        RootBeanDefinition compositeResolverBeanDef = new RootBeanDefinition(ViewResolverComposite.class);
        compositeResolverBeanDef.setSource(source);
        compositeResolverBeanDef.setRole(2);
        String[] names2 = {"content-negotiation"};
        List<Element> contentNegotiationElements = DomUtils.getChildElementsByTagName(element, names2);
        if (contentNegotiationElements.isEmpty()) {
            compositeResolverBeanDef.getPropertyValues().add("viewResolvers", resolvers);
        } else if (contentNegotiationElements.size() == 1) {
            BeanDefinition beanDef = createContentNegotiatingViewResolver(contentNegotiationElements.get(0), context);
            beanDef.getPropertyValues().add("viewResolvers", resolvers);
            ManagedList<Object> list = new ManagedList<>(1);
            list.add(beanDef);
            compositeResolverBeanDef.getPropertyValues().add("order", Integer.MIN_VALUE);
            compositeResolverBeanDef.getPropertyValues().add("viewResolvers", list);
        } else {
            throw new IllegalArgumentException("Only one <content-negotiation> element is allowed.");
        }
        if (element.hasAttribute("order")) {
            compositeResolverBeanDef.getPropertyValues().add("order", element.getAttribute("order"));
        }
        context.getReaderContext().getRegistry().registerBeanDefinition(VIEW_RESOLVER_BEAN_NAME, compositeResolverBeanDef);
        context.registerComponent(new BeanComponentDefinition(compositeResolverBeanDef, VIEW_RESOLVER_BEAN_NAME));
        context.popAndRegisterContainingComponent();
        return null;
    }

    private void addUrlBasedViewResolverProperties(Element element, RootBeanDefinition beanDefinition) {
        if (element.hasAttribute("prefix")) {
            beanDefinition.getPropertyValues().add("prefix", element.getAttribute("prefix"));
        }
        if (element.hasAttribute("suffix")) {
            beanDefinition.getPropertyValues().add("suffix", element.getAttribute("suffix"));
        }
        if (element.hasAttribute("cache-views")) {
            beanDefinition.getPropertyValues().add("cache", element.getAttribute("cache-views"));
        }
        if (element.hasAttribute("view-class")) {
            beanDefinition.getPropertyValues().add("viewClass", element.getAttribute("view-class"));
        }
        if (element.hasAttribute("view-names")) {
            beanDefinition.getPropertyValues().add("viewNames", element.getAttribute("view-names"));
        }
    }

    private BeanDefinition createContentNegotiatingViewResolver(Element resolverElement, ParserContext context) {
        RootBeanDefinition beanDef = new RootBeanDefinition(ContentNegotiatingViewResolver.class);
        beanDef.setSource(context.extractSource(resolverElement));
        beanDef.setRole(2);
        MutablePropertyValues values = beanDef.getPropertyValues();
        List<Element> elements = DomUtils.getChildElementsByTagName(resolverElement, "default-views");
        if (!elements.isEmpty()) {
            ManagedList<Object> list = new ManagedList<>();
            for (Element element : DomUtils.getChildElementsByTagName(elements.get(0), "bean", "ref")) {
                list.add(context.getDelegate().parsePropertySubElement(element, null));
            }
            values.add("defaultViews", list);
        }
        if (resolverElement.hasAttribute("use-not-acceptable")) {
            values.add("useNotAcceptableStatusCode", resolverElement.getAttribute("use-not-acceptable"));
        }
        Object manager = MvcNamespaceUtils.getContentNegotiationManager(context);
        if (manager != null) {
            values.add("contentNegotiationManager", manager);
        }
        return beanDef;
    }
}