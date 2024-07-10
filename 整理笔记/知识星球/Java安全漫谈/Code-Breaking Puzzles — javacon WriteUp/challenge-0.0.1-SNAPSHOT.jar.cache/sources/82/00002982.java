package org.thymeleaf.standard.processor;

import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardMethodTagProcessor.class */
public final class StandardMethodTagProcessor extends AbstractStandardAttributeModifierTagProcessor {
    public static final int PRECEDENCE = 1000;
    public static final String ATTR_NAME = "method";

    public StandardMethodTagProcessor(String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, "method", 1000, true, false);
    }
}