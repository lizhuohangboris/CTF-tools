package org.thymeleaf.linkbuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/linkbuilder/AbstractLinkBuilder.class */
public abstract class AbstractLinkBuilder implements ILinkBuilder {
    private static final Logger logger = LoggerFactory.getLogger(AbstractLinkBuilder.class);
    private String name = getClass().getName();
    private Integer order = null;

    @Override // org.thymeleaf.linkbuilder.ILinkBuilder
    public final String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override // org.thymeleaf.linkbuilder.ILinkBuilder
    public final Integer getOrder() {
        return this.order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}