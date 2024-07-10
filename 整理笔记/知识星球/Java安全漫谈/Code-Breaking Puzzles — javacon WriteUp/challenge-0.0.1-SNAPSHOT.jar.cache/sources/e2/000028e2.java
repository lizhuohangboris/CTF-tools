package org.thymeleaf.spring5.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;
import org.thymeleaf.spring5.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.spring5.util.SpringValueFormatter;
import org.thymeleaf.standard.util.StandardProcessorUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/processor/SpringInputGeneralFieldTagProcessor.class */
public final class SpringInputGeneralFieldTagProcessor extends AbstractSpringFieldTagProcessor {
    public static final String TEXT_INPUT_TYPE_ATTR_VALUE = "text";
    public static final String HIDDEN_INPUT_TYPE_ATTR_VALUE = "hidden";
    public static final String DATETIME_INPUT_TYPE_ATTR_VALUE = "datetime";
    public static final String DATETIMELOCAL_INPUT_TYPE_ATTR_VALUE = "datetime-local";
    public static final String DATE_INPUT_TYPE_ATTR_VALUE = "date";
    public static final String MONTH_INPUT_TYPE_ATTR_VALUE = "month";
    public static final String TIME_INPUT_TYPE_ATTR_VALUE = "time";
    public static final String WEEK_INPUT_TYPE_ATTR_VALUE = "week";
    public static final String NUMBER_INPUT_TYPE_ATTR_VALUE = "number";
    public static final String RANGE_INPUT_TYPE_ATTR_VALUE = "range";
    public static final String EMAIL_INPUT_TYPE_ATTR_VALUE = "email";
    public static final String URL_INPUT_TYPE_ATTR_VALUE = "url";
    public static final String SEARCH_INPUT_TYPE_ATTR_VALUE = "search";
    public static final String TEL_INPUT_TYPE_ATTR_VALUE = "tel";
    public static final String COLOR_INPUT_TYPE_ATTR_VALUE = "color";
    private static final String[] ALL_TYPE_ATTR_VALUES = {null, "text", HIDDEN_INPUT_TYPE_ATTR_VALUE, DATETIME_INPUT_TYPE_ATTR_VALUE, DATETIMELOCAL_INPUT_TYPE_ATTR_VALUE, DATE_INPUT_TYPE_ATTR_VALUE, MONTH_INPUT_TYPE_ATTR_VALUE, TIME_INPUT_TYPE_ATTR_VALUE, WEEK_INPUT_TYPE_ATTR_VALUE, NUMBER_INPUT_TYPE_ATTR_VALUE, RANGE_INPUT_TYPE_ATTR_VALUE, EMAIL_INPUT_TYPE_ATTR_VALUE, URL_INPUT_TYPE_ATTR_VALUE, SEARCH_INPUT_TYPE_ATTR_VALUE, TEL_INPUT_TYPE_ATTR_VALUE, COLOR_INPUT_TYPE_ATTR_VALUE};

    public SpringInputGeneralFieldTagProcessor(String dialectPrefix) {
        super(dialectPrefix, "input", "type", ALL_TYPE_ATTR_VALUES, true);
    }

    @Override // org.thymeleaf.spring5.processor.AbstractSpringFieldTagProcessor
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IThymeleafBindStatus bindStatus, IElementTagStructureHandler structureHandler) {
        String displayString;
        String name = bindStatus.getExpression();
        String name2 = name == null ? "" : name;
        String id = computeId(context, tag, name2, false);
        String type = tag.getAttributeValue(this.typeAttributeDefinition.getAttributeName());
        if (applyConversion(type)) {
            displayString = SpringValueFormatter.getDisplayString(bindStatus.getValue(), bindStatus.getEditor(), true);
        } else {
            displayString = SpringValueFormatter.getDisplayString(bindStatus.getActualValue(), true);
        }
        String value = displayString;
        StandardProcessorUtils.setAttribute(structureHandler, this.idAttributeDefinition, "id", id);
        StandardProcessorUtils.setAttribute(structureHandler, this.nameAttributeDefinition, "name", name2);
        StandardProcessorUtils.setAttribute(structureHandler, this.valueAttributeDefinition, "value", RequestDataValueProcessorUtils.processFormFieldValue(context, name2, value, type));
    }

    private static boolean applyConversion(String type) {
        return type == null || !(NUMBER_INPUT_TYPE_ATTR_VALUE.equalsIgnoreCase(type) || RANGE_INPUT_TYPE_ATTR_VALUE.equalsIgnoreCase(type));
    }
}