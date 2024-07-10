package org.springframework.web.servlet.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.springframework.beans.BeansException;
import org.springframework.util.CollectionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/handler/SimpleUrlHandlerMapping.class */
public class SimpleUrlHandlerMapping extends AbstractUrlHandlerMapping {
    private final Map<String, Object> urlMap = new LinkedHashMap();

    public void setMappings(Properties mappings) {
        CollectionUtils.mergePropertiesIntoMap(mappings, this.urlMap);
    }

    public void setUrlMap(Map<String, ?> urlMap) {
        this.urlMap.putAll(urlMap);
    }

    public Map<String, ?> getUrlMap() {
        return this.urlMap;
    }

    @Override // org.springframework.web.servlet.handler.AbstractHandlerMapping, org.springframework.context.support.ApplicationObjectSupport
    public void initApplicationContext() throws BeansException {
        super.initApplicationContext();
        registerHandlers(this.urlMap);
    }

    protected void registerHandlers(Map<String, Object> urlMap) throws BeansException {
        if (urlMap.isEmpty()) {
            this.logger.trace("No patterns in " + formatMappingName());
            return;
        }
        urlMap.forEach(url, handler -> {
            if (!url.startsWith("/")) {
                url = "/" + url;
            }
            if (handler instanceof String) {
                handler = ((String) handler).trim();
            }
            registerHandler(url, handler);
        });
        if (this.logger.isDebugEnabled()) {
            List<String> patterns = new ArrayList<>();
            if (getRootHandler() != null) {
                patterns.add("/");
            }
            if (getDefaultHandler() != null) {
                patterns.add("/**");
            }
            patterns.addAll(getHandlerMap().keySet());
            this.logger.debug("Patterns " + patterns + " in " + formatMappingName());
        }
    }
}