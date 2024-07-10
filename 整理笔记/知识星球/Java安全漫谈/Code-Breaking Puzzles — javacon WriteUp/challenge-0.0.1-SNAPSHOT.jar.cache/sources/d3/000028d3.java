package org.thymeleaf.spring5.expression;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.standard.expression.StandardExpressionObjectFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/expression/SpringStandardExpressionObjectFactory.class */
public class SpringStandardExpressionObjectFactory extends StandardExpressionObjectFactory {
    public static final String FIELDS_EXPRESSION_OBJECT_NAME = "fields";
    public static final String THEMES_EXPRESSION_OBJECT_NAME = "themes";
    public static final String MVC_EXPRESSION_OBJECT_NAME = "mvc";
    public static final String REQUESTDATAVALUES_EXPRESSION_OBJECT_NAME = "requestdatavalues";
    public static final Set<String> ALL_EXPRESSION_OBJECT_NAMES;
    private static final Mvc MVC_EXPRESSION_OBJECT = new Mvc();

    static {
        Set<String> allExpressionObjectNames = new LinkedHashSet<>();
        allExpressionObjectNames.addAll(StandardExpressionObjectFactory.ALL_EXPRESSION_OBJECT_NAMES);
        allExpressionObjectNames.add(FIELDS_EXPRESSION_OBJECT_NAME);
        allExpressionObjectNames.add(THEMES_EXPRESSION_OBJECT_NAME);
        allExpressionObjectNames.add(MVC_EXPRESSION_OBJECT_NAME);
        allExpressionObjectNames.add(REQUESTDATAVALUES_EXPRESSION_OBJECT_NAME);
        ALL_EXPRESSION_OBJECT_NAMES = Collections.unmodifiableSet(allExpressionObjectNames);
    }

    @Override // org.thymeleaf.standard.expression.StandardExpressionObjectFactory, org.thymeleaf.expression.IExpressionObjectFactory
    public Set<String> getAllExpressionObjectNames() {
        return ALL_EXPRESSION_OBJECT_NAMES;
    }

    @Override // org.thymeleaf.standard.expression.StandardExpressionObjectFactory, org.thymeleaf.expression.IExpressionObjectFactory
    public Object buildObject(IExpressionContext context, String expressionObjectName) {
        if (MVC_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return MVC_EXPRESSION_OBJECT;
        }
        if (THEMES_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return new Themes(context);
        }
        if (FIELDS_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            return new Fields(context);
        }
        if (REQUESTDATAVALUES_EXPRESSION_OBJECT_NAME.equals(expressionObjectName)) {
            if (context instanceof ITemplateContext) {
                return new RequestDataValues((ITemplateContext) context);
            }
            return null;
        }
        return super.buildObject(context, expressionObjectName);
    }
}