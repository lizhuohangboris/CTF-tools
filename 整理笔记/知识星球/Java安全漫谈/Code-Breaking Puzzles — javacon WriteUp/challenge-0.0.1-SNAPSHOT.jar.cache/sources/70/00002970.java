package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.Each;
import org.thymeleaf.standard.expression.EachUtils;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardEachTagProcessor.class */
public final class StandardEachTagProcessor extends AbstractAttributeTagProcessor {
    public static final int PRECEDENCE = 200;
    public static final String ATTR_NAME = "each";

    public StandardEachTagProcessor(TemplateMode templateMode, String dialectPrefix) {
        super(templateMode, dialectPrefix, null, false, ATTR_NAME, true, 200, true);
    }

    @Override // org.thymeleaf.processor.element.AbstractAttributeTagProcessor
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        Object statusVarValue;
        Each each = EachUtils.parseEach(context, attributeValue);
        IStandardExpression iterVarExpr = each.getIterVar();
        Object iterVarValue = iterVarExpr.execute(context);
        IStandardExpression statusVarExpr = each.getStatusVar();
        if (statusVarExpr != null) {
            statusVarValue = statusVarExpr.execute(context);
        } else {
            statusVarValue = null;
        }
        IStandardExpression iterableExpr = each.getIterable();
        Object iteratedValue = iterableExpr.execute(context);
        String iterVarName = iterVarValue == null ? null : iterVarValue.toString();
        if (StringUtils.isEmptyOrWhitespace(iterVarName)) {
            throw new TemplateProcessingException("Iteration variable name expression evaluated as null: \"" + iterVarExpr + "\"");
        }
        String statusVarName = statusVarValue == null ? null : statusVarValue.toString();
        if (statusVarExpr != null && StringUtils.isEmptyOrWhitespace(statusVarName)) {
            throw new TemplateProcessingException("Status variable name expression evaluated as null or empty: \"" + statusVarExpr + "\"");
        }
        structureHandler.iterateElement(iterVarName, statusVarName, iteratedValue);
    }
}