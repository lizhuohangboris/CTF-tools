package org.thymeleaf.standard.processor;

import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardAssertTagProcessor.class */
public final class StandardAssertTagProcessor extends AbstractStandardAssertionTagProcessor {
    public static final int PRECEDENCE = 1550;
    public static final String ATTR_NAME = "assert";

    public StandardAssertTagProcessor(TemplateMode templateMode, String dialectPrefix) {
        super(templateMode, dialectPrefix, ATTR_NAME, PRECEDENCE);
    }
}