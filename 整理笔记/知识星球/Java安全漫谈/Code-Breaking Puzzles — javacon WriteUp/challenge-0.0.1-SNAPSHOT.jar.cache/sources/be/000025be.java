package org.springframework.web.servlet.handler;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/handler/AbstractDetectingUrlHandlerMapping.class */
public abstract class AbstractDetectingUrlHandlerMapping extends AbstractUrlHandlerMapping {
    private boolean detectHandlersInAncestorContexts = false;

    protected abstract String[] determineUrlsForHandler(String str);

    public void setDetectHandlersInAncestorContexts(boolean detectHandlersInAncestorContexts) {
        this.detectHandlersInAncestorContexts = detectHandlersInAncestorContexts;
    }

    @Override // org.springframework.web.servlet.handler.AbstractHandlerMapping, org.springframework.context.support.ApplicationObjectSupport
    public void initApplicationContext() throws ApplicationContextException {
        super.initApplicationContext();
        detectHandlers();
    }

    protected void detectHandlers() throws BeansException {
        String[] beanNamesForType;
        ApplicationContext applicationContext = obtainApplicationContext();
        if (this.detectHandlersInAncestorContexts) {
            beanNamesForType = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(applicationContext, Object.class);
        } else {
            beanNamesForType = applicationContext.getBeanNamesForType(Object.class);
        }
        String[] beanNames = beanNamesForType;
        for (String beanName : beanNames) {
            String[] urls = determineUrlsForHandler(beanName);
            if (!ObjectUtils.isEmpty((Object[]) urls)) {
                registerHandler(urls, beanName);
            }
        }
        if ((this.logger.isDebugEnabled() && !getHandlerMap().isEmpty()) || this.logger.isTraceEnabled()) {
            this.logger.debug("Detected " + getHandlerMap().size() + " mappings in " + formatMappingName());
        }
    }
}