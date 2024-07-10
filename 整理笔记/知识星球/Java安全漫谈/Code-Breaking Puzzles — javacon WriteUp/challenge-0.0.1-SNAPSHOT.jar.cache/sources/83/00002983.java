package org.thymeleaf.standard.processor;

import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardNonRemovableAttributeTagProcessor.class */
public final class StandardNonRemovableAttributeTagProcessor extends AbstractStandardAttributeModifierTagProcessor {
    public static final int PRECEDENCE = 1000;
    public static final String[] ATTR_NAMES = {"name", "type"};

    public StandardNonRemovableAttributeTagProcessor(String dialectPrefix, String attrName) {
        super(TemplateMode.HTML, dialectPrefix, attrName, 1000, false, false);
    }
}