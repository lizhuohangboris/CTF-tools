package org.thymeleaf.spring5.processor;

import java.util.Collections;
import java.util.Map;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.spring5.naming.SpringContextVariableNames;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.VariableExpression;
import org.thymeleaf.standard.processor.AbstractStandardTargetSelectionTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/processor/SpringObjectTagProcessor.class */
public final class SpringObjectTagProcessor extends AbstractStandardTargetSelectionTagProcessor {
    public static final int ATTR_PRECEDENCE = 500;
    public static final String ATTR_NAME = "object";

    public SpringObjectTagProcessor(String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, "object", 500);
    }

    @Override // org.thymeleaf.standard.processor.AbstractStandardTargetSelectionTagProcessor
    protected void validateSelectionValue(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IStandardExpression expression) {
        if (expression == null || !(expression instanceof VariableExpression)) {
            throw new TemplateProcessingException("The expression used for object selection is " + expression + ", which is not valid: only variable expressions (${...}) are allowed in '" + attributeName + "' attributes in Spring-enabled environments.");
        }
    }

    @Override // org.thymeleaf.standard.processor.AbstractStandardTargetSelectionTagProcessor
    protected Map<String, Object> computeAdditionalLocalVariables(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IStandardExpression expression) {
        return Collections.singletonMap(SpringContextVariableNames.SPRING_BOUND_OBJECT_EXPRESSION, expression);
    }
}