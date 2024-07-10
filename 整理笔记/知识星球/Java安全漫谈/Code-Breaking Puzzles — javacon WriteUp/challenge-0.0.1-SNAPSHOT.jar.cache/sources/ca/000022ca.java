package org.springframework.scheduling.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/config/SchedulerBeanDefinitionParser.class */
public class SchedulerBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
    @Override // org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
    protected String getBeanClassName(Element element) {
        return "org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler";
    }

    @Override // org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        String poolSize = element.getAttribute("pool-size");
        if (StringUtils.hasText(poolSize)) {
            builder.addPropertyValue("poolSize", poolSize);
        }
    }
}