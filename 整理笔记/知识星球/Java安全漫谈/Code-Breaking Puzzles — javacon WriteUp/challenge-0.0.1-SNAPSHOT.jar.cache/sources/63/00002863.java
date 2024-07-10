package org.thymeleaf.messageresolver;

import org.thymeleaf.context.ITemplateContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/messageresolver/IMessageResolver.class */
public interface IMessageResolver {
    String getName();

    Integer getOrder();

    String resolveMessage(ITemplateContext iTemplateContext, Class<?> cls, String str, Object[] objArr);

    String createAbsentMessageRepresentation(ITemplateContext iTemplateContext, Class<?> cls, String str, Object[] objArr);
}