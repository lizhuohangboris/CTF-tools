package org.thymeleaf.standard.processor;

import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardReplaceTagProcessor.class */
public final class StandardReplaceTagProcessor extends AbstractStandardFragmentInsertionTagProcessor {
    public static final int PRECEDENCE = 100;
    public static final String ATTR_NAME = "replace";

    public StandardReplaceTagProcessor(TemplateMode templateMode, String dialectPrefix) {
        super(templateMode, dialectPrefix, "replace", 100, true);
    }
}