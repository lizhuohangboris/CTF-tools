package org.thymeleaf.standard.processor;

import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardXmlSpaceTagProcessor.class */
public final class StandardXmlSpaceTagProcessor extends AbstractStandardAttributeModifierTagProcessor {
    public static final int PRECEDENCE = 1000;
    public static final String ATTR_NAME = "xmlspace";
    public static final String TARGET_ATTR_NAME = "xml:space";

    public StandardXmlSpaceTagProcessor(String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, ATTR_NAME, TARGET_ATTR_NAME, 1000, true, false);
    }
}