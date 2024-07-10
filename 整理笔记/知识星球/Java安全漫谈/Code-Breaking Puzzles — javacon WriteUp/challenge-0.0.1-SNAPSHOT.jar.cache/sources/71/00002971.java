package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardFragmentTagProcessor.class */
public final class StandardFragmentTagProcessor extends AbstractElementTagProcessor {
    public static final int PRECEDENCE = 1500;
    public static final String ATTR_NAME = "fragment";

    public StandardFragmentTagProcessor(TemplateMode templateMode, String dialectPrefix) {
        super(templateMode, dialectPrefix, null, false, ATTR_NAME, true, PRECEDENCE);
    }

    @Override // org.thymeleaf.processor.element.AbstractElementTagProcessor
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
        AttributeName attributeName = getMatchingAttributeName().getMatchingAttributeName();
        structureHandler.removeAttribute(attributeName);
    }
}