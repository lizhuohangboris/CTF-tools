package org.thymeleaf.standard.processor;

import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardObjectTagProcessor.class */
public final class StandardObjectTagProcessor extends AbstractStandardTargetSelectionTagProcessor {
    public static final int PRECEDENCE = 500;
    public static final String ATTR_NAME = "object";

    public StandardObjectTagProcessor(TemplateMode templateMode, String dialectPrefix) {
        super(templateMode, dialectPrefix, "object", 500);
    }
}