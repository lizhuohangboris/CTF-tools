package org.thymeleaf.spring5.expression;

import java.util.Locale;
import org.springframework.ui.context.Theme;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring5.context.IThymeleafRequestContext;
import org.thymeleaf.spring5.context.SpringContextUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/expression/Themes.class */
public class Themes {
    private final Theme theme;
    private final Locale locale;

    public Themes(IExpressionContext context) {
        this.locale = context.getLocale();
        IThymeleafRequestContext requestContext = SpringContextUtils.getRequestContext(context);
        this.theme = requestContext != null ? requestContext.getTheme() : null;
    }

    public String code(String code) {
        if (this.theme == null) {
            throw new TemplateProcessingException("Theme cannot be resolved because RequestContext was not found. Are you using a Context object without a RequestContext variable?");
        }
        return this.theme.getMessageSource().getMessage(code, null, "", this.locale);
    }
}