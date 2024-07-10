package org.thymeleaf.standard.processor;

import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardLangXmlLangTagProcessor.class */
public final class StandardLangXmlLangTagProcessor extends AbstractStandardDoubleAttributeModifierTagProcessor {
    public static final int PRECEDENCE = 990;
    public static final String ATTR_NAME = "lang-xmllang";
    public static final String TARGET_ATTR_NAME_ONE = "lang";
    public static final String TARGET_ATTR_NAME_TWO = "xml:lang";

    public StandardLangXmlLangTagProcessor(String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, ATTR_NAME, 990, "lang", "xml:lang", true);
    }
}