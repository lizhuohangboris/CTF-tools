package org.thymeleaf.standard.processor;

import java.util.List;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.AssignationUtils;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardWithTagProcessor.class */
public final class StandardWithTagProcessor extends AbstractAttributeTagProcessor {
    public static final int PRECEDENCE = 600;
    public static final String ATTR_NAME = "with";

    public StandardWithTagProcessor(TemplateMode templateMode, String dialectPrefix) {
        super(templateMode, dialectPrefix, null, false, "with", true, PRECEDENCE, true);
    }

    @Override // org.thymeleaf.processor.element.AbstractAttributeTagProcessor
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        AssignationSequence assignations = AssignationUtils.parseAssignationSequence(context, attributeValue, false);
        if (assignations == null) {
            throw new TemplateProcessingException("Could not parse value as attribute assignations: \"" + attributeValue + "\"");
        }
        IEngineContext engineContext = null;
        if (context instanceof IEngineContext) {
            engineContext = (IEngineContext) context;
        }
        List<Assignation> assignationValues = assignations.getAssignations();
        int assignationValuesLen = assignationValues.size();
        for (int i = 0; i < assignationValuesLen; i++) {
            Assignation assignation = assignationValues.get(i);
            IStandardExpression leftExpr = assignation.getLeft();
            Object leftValue = leftExpr.execute(context);
            IStandardExpression rightExpr = assignation.getRight();
            Object rightValue = rightExpr.execute(context);
            String newVariableName = leftValue == null ? null : leftValue.toString();
            if (StringUtils.isEmptyOrWhitespace(newVariableName)) {
                throw new TemplateProcessingException("Variable name expression evaluated as null or empty: \"" + leftExpr + "\"");
            }
            if (engineContext != null) {
                engineContext.setVariable(newVariableName, rightValue);
            } else {
                structureHandler.setLocalVariable(newVariableName, rightValue);
            }
        }
    }
}