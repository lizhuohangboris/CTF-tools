package org.thymeleaf.linkbuilder;

import java.util.Map;
import org.thymeleaf.context.IExpressionContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/linkbuilder/ILinkBuilder.class */
public interface ILinkBuilder {
    String getName();

    Integer getOrder();

    String buildLink(IExpressionContext iExpressionContext, String str, Map<String, Object> map);
}