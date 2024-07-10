package org.thymeleaf.standard.processor;

import org.thymeleaf.standard.processor.AbstractStandardMultipleAttributeModifierTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardAttrprependTagProcessor.class */
public final class StandardAttrprependTagProcessor extends AbstractStandardMultipleAttributeModifierTagProcessor {
    public static final int PRECEDENCE = 800;
    public static final String ATTR_NAME = "attrprepend";

    public StandardAttrprependTagProcessor(TemplateMode templateMode, String dialectPrefix) {
        super(templateMode, dialectPrefix, ATTR_NAME, 800, AbstractStandardMultipleAttributeModifierTagProcessor.ModificationType.PREPEND, true);
    }
}