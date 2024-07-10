package org.springframework.context.config;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/config/MBeanExportBeanDefinitionParser.class */
class MBeanExportBeanDefinitionParser extends AbstractBeanDefinitionParser {
    private static final String MBEAN_EXPORTER_BEAN_NAME = "mbeanExporter";
    private static final String DEFAULT_DOMAIN_ATTRIBUTE = "default-domain";
    private static final String SERVER_ATTRIBUTE = "server";
    private static final String REGISTRATION_ATTRIBUTE = "registration";
    private static final String REGISTRATION_IGNORE_EXISTING = "ignoreExisting";
    private static final String REGISTRATION_REPLACE_EXISTING = "replaceExisting";

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.beans.factory.xml.AbstractBeanDefinitionParser
    public String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) {
        return MBEAN_EXPORTER_BEAN_NAME;
    }

    @Override // org.springframework.beans.factory.xml.AbstractBeanDefinitionParser
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(AnnotationMBeanExporter.class);
        builder.setRole(2);
        builder.getRawBeanDefinition().setSource(parserContext.extractSource(element));
        String defaultDomain = element.getAttribute(DEFAULT_DOMAIN_ATTRIBUTE);
        if (StringUtils.hasText(defaultDomain)) {
            builder.addPropertyValue("defaultDomain", defaultDomain);
        }
        String serverBeanName = element.getAttribute(SERVER_ATTRIBUTE);
        if (StringUtils.hasText(serverBeanName)) {
            builder.addPropertyReference(SERVER_ATTRIBUTE, serverBeanName);
        } else {
            AbstractBeanDefinition specialServer = MBeanServerBeanDefinitionParser.findServerForSpecialEnvironment();
            if (specialServer != null) {
                builder.addPropertyValue(SERVER_ATTRIBUTE, specialServer);
            }
        }
        String registration = element.getAttribute(REGISTRATION_ATTRIBUTE);
        RegistrationPolicy registrationPolicy = RegistrationPolicy.FAIL_ON_EXISTING;
        if (REGISTRATION_IGNORE_EXISTING.equals(registration)) {
            registrationPolicy = RegistrationPolicy.IGNORE_EXISTING;
        } else if (REGISTRATION_REPLACE_EXISTING.equals(registration)) {
            registrationPolicy = RegistrationPolicy.REPLACE_EXISTING;
        }
        builder.addPropertyValue("registrationPolicy", registrationPolicy);
        return builder.getBeanDefinition();
    }
}