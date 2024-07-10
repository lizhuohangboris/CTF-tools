package org.springframework.scheduling.config;

import org.apache.coyote.http11.Constants;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/scheduling/config/ExecutorBeanDefinitionParser.class */
public class ExecutorBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
    @Override // org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
    protected String getBeanClassName(Element element) {
        return "org.springframework.scheduling.config.TaskExecutorFactoryBean";
    }

    @Override // org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String keepAliveSeconds = element.getAttribute(Constants.KEEPALIVE);
        if (StringUtils.hasText(keepAliveSeconds)) {
            builder.addPropertyValue("keepAliveSeconds", keepAliveSeconds);
        }
        String queueCapacity = element.getAttribute("queue-capacity");
        if (StringUtils.hasText(queueCapacity)) {
            builder.addPropertyValue("queueCapacity", queueCapacity);
        }
        configureRejectionPolicy(element, builder);
        String poolSize = element.getAttribute("pool-size");
        if (StringUtils.hasText(poolSize)) {
            builder.addPropertyValue("poolSize", poolSize);
        }
    }

    private void configureRejectionPolicy(Element element, BeanDefinitionBuilder builder) {
        String policyClassName;
        String rejectionPolicy = element.getAttribute("rejection-policy");
        if (!StringUtils.hasText(rejectionPolicy)) {
            return;
        }
        if (rejectionPolicy.equals("ABORT")) {
            policyClassName = "java.util.concurrent.ThreadPoolExecutor.AbortPolicy";
        } else {
            policyClassName = rejectionPolicy.equals("CALLER_RUNS") ? "java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy" : rejectionPolicy.equals("DISCARD") ? "java.util.concurrent.ThreadPoolExecutor.DiscardPolicy" : rejectionPolicy.equals("DISCARD_OLDEST") ? "java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy" : rejectionPolicy;
        }
        builder.addPropertyValue("rejectedExecutionHandler", new RootBeanDefinition(policyClassName));
    }
}