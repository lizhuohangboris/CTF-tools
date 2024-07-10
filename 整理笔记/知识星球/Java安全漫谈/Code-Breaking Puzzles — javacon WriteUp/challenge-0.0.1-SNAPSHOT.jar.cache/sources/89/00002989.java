package org.thymeleaf.standard.processor;

import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardSrcTagProcessor.class */
public final class StandardSrcTagProcessor extends AbstractStandardAttributeModifierTagProcessor {
    public static final int PRECEDENCE = 1000;
    public static final String ATTR_NAME = "src";

    public StandardSrcTagProcessor(String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, "src", 1000, false, true);
    }
}