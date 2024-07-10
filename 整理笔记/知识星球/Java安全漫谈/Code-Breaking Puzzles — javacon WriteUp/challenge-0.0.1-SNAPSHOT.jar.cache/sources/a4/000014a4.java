package org.springframework.beans.factory.xml;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.Conventions;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/xml/AbstractSimpleBeanDefinitionParser.class */
public abstract class AbstractSimpleBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
    @Override // org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
    public void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        NamedNodeMap attributes = element.getAttributes();
        for (int x = 0; x < attributes.getLength(); x++) {
            Attr attribute = (Attr) attributes.item(x);
            if (isEligibleAttribute(attribute, parserContext)) {
                String propertyName = extractPropertyName(attribute.getLocalName());
                Assert.state(StringUtils.hasText(propertyName), "Illegal property name returned from 'extractPropertyName(String)': cannot be null or empty.");
                builder.addPropertyValue(propertyName, attribute.getValue());
            }
        }
        postProcess(builder, element);
    }

    protected boolean isEligibleAttribute(Attr attribute, ParserContext parserContext) {
        String fullName = attribute.getName();
        return (fullName.equals("xmlns") || fullName.startsWith(StandardXmlNsTagProcessor.ATTR_NAME_PREFIX) || !isEligibleAttribute(parserContext.getDelegate().getLocalName(attribute))) ? false : true;
    }

    public boolean isEligibleAttribute(String attributeName) {
        return !"id".equals(attributeName);
    }

    protected String extractPropertyName(String attributeName) {
        return Conventions.attributeNameToPropertyName(attributeName);
    }

    protected void postProcess(BeanDefinitionBuilder beanDefinition, Element element) {
    }
}