package org.springframework.web.servlet.tags.form;

import javax.servlet.jsp.PageContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/tags/form/TagIdGenerator.class */
abstract class TagIdGenerator {
    private static final String PAGE_CONTEXT_ATTRIBUTE_PREFIX = TagIdGenerator.class.getName() + ".";

    TagIdGenerator() {
    }

    public static String nextId(String name, PageContext pageContext) {
        String attributeName = PAGE_CONTEXT_ATTRIBUTE_PREFIX + name;
        Integer currentCount = (Integer) pageContext.getAttribute(attributeName);
        Integer currentCount2 = Integer.valueOf(currentCount != null ? currentCount.intValue() + 1 : 1);
        pageContext.setAttribute(attributeName, currentCount2);
        return name + currentCount2;
    }
}