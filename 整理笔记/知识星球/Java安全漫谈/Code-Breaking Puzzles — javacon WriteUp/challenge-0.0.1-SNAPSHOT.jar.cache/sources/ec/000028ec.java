package org.thymeleaf.spring5.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;
import org.thymeleaf.spring5.naming.SpringContextVariableNames;
import org.thymeleaf.spring5.util.FieldUtils;
import org.thymeleaf.spring5.util.SpringValueFormatter;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/processor/SpringUErrorsTagProcessor.class */
public final class SpringUErrorsTagProcessor extends AbstractAttributeTagProcessor {
    private static final String ERROR_DELIMITER = "<br />";
    public static final int ATTR_PRECEDENCE = 1700;
    public static final String ATTR_NAME = "uerrors";

    public SpringUErrorsTagProcessor(String dialectPrefix) {
        super(TemplateMode.HTML, dialectPrefix, null, false, ATTR_NAME, true, 1700, true);
    }

    @Override // org.thymeleaf.processor.element.AbstractAttributeTagProcessor
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        IThymeleafBindStatus bindStatus = FieldUtils.getBindStatus(context, attributeValue);
        if (bindStatus.isError()) {
            StringBuilder strBuilder = new StringBuilder();
            String[] errorMsgs = bindStatus.getErrorMessages();
            for (int i = 0; i < errorMsgs.length; i++) {
                if (i > 0) {
                    strBuilder.append(ERROR_DELIMITER);
                }
                String displayString = SpringValueFormatter.getDisplayString(errorMsgs[i], false);
                strBuilder.append(displayString);
            }
            structureHandler.setBody(strBuilder.toString(), false);
            structureHandler.setLocalVariable(SpringContextVariableNames.THYMELEAF_FIELD_BIND_STATUS, bindStatus);
            return;
        }
        structureHandler.removeElement();
    }
}