package org.thymeleaf.standard.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardSwitchTagProcessor.class */
public final class StandardSwitchTagProcessor extends AbstractAttributeTagProcessor {
    public static final int PRECEDENCE = 250;
    public static final String ATTR_NAME = "switch";
    public static final String SWITCH_VARIABLE_NAME = "%%SWITCH_EXPR%%";

    public StandardSwitchTagProcessor(TemplateMode templateMode, String dialectPrefix) {
        super(templateMode, dialectPrefix, null, false, ATTR_NAME, true, PRECEDENCE, true);
    }

    @Override // org.thymeleaf.processor.element.AbstractAttributeTagProcessor
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());
        IStandardExpression switchExpression = expressionParser.parseExpression(context, attributeValue);
        structureHandler.setLocalVariable(SWITCH_VARIABLE_NAME, new SwitchStructure(switchExpression));
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/StandardSwitchTagProcessor$SwitchStructure.class */
    public static final class SwitchStructure {
        private final IStandardExpression expression;
        private boolean executed = false;

        public SwitchStructure(IStandardExpression expression) {
            this.expression = expression;
        }

        public IStandardExpression getExpression() {
            return this.expression;
        }

        public boolean isExecuted() {
            return this.executed;
        }

        public void setExecuted(boolean executed) {
            this.executed = executed;
        }
    }
}