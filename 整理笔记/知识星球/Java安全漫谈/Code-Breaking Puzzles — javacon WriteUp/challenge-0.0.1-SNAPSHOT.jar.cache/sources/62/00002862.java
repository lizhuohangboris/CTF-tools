package org.thymeleaf.messageresolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/messageresolver/AbstractMessageResolver.class */
public abstract class AbstractMessageResolver implements IMessageResolver {
    private static final Logger logger = LoggerFactory.getLogger(AbstractMessageResolver.class);
    private String name = getClass().getName();
    private Integer order = null;

    @Override // org.thymeleaf.messageresolver.IMessageResolver
    public final String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override // org.thymeleaf.messageresolver.IMessageResolver
    public final Integer getOrder() {
        return this.order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}