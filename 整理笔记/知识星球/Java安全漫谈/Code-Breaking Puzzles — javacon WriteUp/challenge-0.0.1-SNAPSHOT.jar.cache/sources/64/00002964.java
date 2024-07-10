package org.thymeleaf.standard.processor;

import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardAltTitleTagProcessor.class */
public final class StandardAltTitleTagProcessor extends AbstractStandardDoubleAttributeModifierTagProcessor {
    public static final int PRECEDENCE = 990;
    public static final String ATTR_NAME = "alt-title";
    public static final String TARGET_ATTR_NAME_ONE = "alt";
    public static final String TARGET_ATTR_NAME_TWO = "title";

    public StandardAltTitleTagProcessor(String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, ATTR_NAME, 990, "alt", "title", true);
    }
}