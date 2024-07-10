package org.thymeleaf.spring5;

import java.util.Set;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.messageresolver.IMessageResolver;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.spring5.dialect.SpringStandardDialect;
import org.thymeleaf.spring5.messageresolver.SpringMessageResolver;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/SpringTemplateEngine.class */
public class SpringTemplateEngine extends TemplateEngine implements ISpringTemplateEngine, MessageSourceAware {
    private static final SpringStandardDialect SPRINGSTANDARD_DIALECT = new SpringStandardDialect();
    private MessageSource messageSource = null;
    private MessageSource templateEngineMessageSource = null;

    public SpringTemplateEngine() {
        super.setDialect(SPRINGSTANDARD_DIALECT);
    }

    @Override // org.springframework.context.MessageSourceAware
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override // org.thymeleaf.spring5.ISpringTemplateEngine
    public void setTemplateEngineMessageSource(MessageSource templateEngineMessageSource) {
        this.templateEngineMessageSource = templateEngineMessageSource;
    }

    public boolean getEnableSpringELCompiler() {
        Set<IDialect> dialects = getDialects();
        for (IDialect dialect : dialects) {
            if (dialect instanceof SpringStandardDialect) {
                return ((SpringStandardDialect) dialect).getEnableSpringELCompiler();
            }
        }
        return false;
    }

    public void setEnableSpringELCompiler(boolean enableSpringELCompiler) {
        Set<IDialect> dialects = getDialects();
        for (IDialect dialect : dialects) {
            if (dialect instanceof SpringStandardDialect) {
                ((SpringStandardDialect) dialect).setEnableSpringELCompiler(enableSpringELCompiler);
            }
        }
    }

    public boolean getRenderHiddenMarkersBeforeCheckboxes() {
        Set<IDialect> dialects = getDialects();
        for (IDialect dialect : dialects) {
            if (dialect instanceof SpringStandardDialect) {
                return ((SpringStandardDialect) dialect).getRenderHiddenMarkersBeforeCheckboxes();
            }
        }
        return false;
    }

    public void setRenderHiddenMarkersBeforeCheckboxes(boolean renderHiddenMarkersBeforeCheckboxes) {
        Set<IDialect> dialects = getDialects();
        for (IDialect dialect : dialects) {
            if (dialect instanceof SpringStandardDialect) {
                ((SpringStandardDialect) dialect).setRenderHiddenMarkersBeforeCheckboxes(renderHiddenMarkersBeforeCheckboxes);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.thymeleaf.TemplateEngine
    public final void initializeSpecific() {
        IMessageResolver messageResolver;
        initializeSpringSpecific();
        super.initializeSpecific();
        MessageSource messageSource = this.templateEngineMessageSource == null ? this.messageSource : this.templateEngineMessageSource;
        if (messageSource != null) {
            SpringMessageResolver springMessageResolver = new SpringMessageResolver();
            springMessageResolver.setMessageSource(messageSource);
            messageResolver = springMessageResolver;
        } else {
            messageResolver = new StandardMessageResolver();
        }
        super.setMessageResolver(messageResolver);
    }

    protected void initializeSpringSpecific() {
    }
}