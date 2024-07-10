package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardXmlNsTagProcessor.class */
public final class StandardXmlNsTagProcessor extends AbstractAttributeTagProcessor {
    public static final int PRECEDENCE = 1000;
    public static final String ATTR_NAME_PREFIX = "xmlns:";

    public StandardXmlNsTagProcessor(TemplateMode templateMode, String dialectPrefix) {
        super(templateMode, null, null, false, ATTR_NAME_PREFIX + dialectPrefix, false, 1000, true);
    }

    @Override // org.thymeleaf.processor.element.AbstractAttributeTagProcessor
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
    }
}