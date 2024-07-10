package org.thymeleaf.standard.processor;

import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardActionTagProcessor.class */
public final class StandardActionTagProcessor extends AbstractStandardAttributeModifierTagProcessor {
    public static final int PRECEDENCE = 1000;
    public static final String ATTR_NAME = "action";

    public StandardActionTagProcessor(String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, "action", 1000, false, false);
    }
}