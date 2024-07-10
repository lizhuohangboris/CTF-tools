package org.thymeleaf.standard.processor;

import org.thymeleaf.standard.processor.AbstractStandardMultipleAttributeModifierTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardAttrappendTagProcessor.class */
public final class StandardAttrappendTagProcessor extends AbstractStandardMultipleAttributeModifierTagProcessor {
    public static final int PRECEDENCE = 900;
    public static final String ATTR_NAME = "attrappend";

    public StandardAttrappendTagProcessor(TemplateMode templateMode, String dialectPrefix) {
        super(templateMode, dialectPrefix, ATTR_NAME, 900, AbstractStandardMultipleAttributeModifierTagProcessor.ModificationType.APPEND, true);
    }
}