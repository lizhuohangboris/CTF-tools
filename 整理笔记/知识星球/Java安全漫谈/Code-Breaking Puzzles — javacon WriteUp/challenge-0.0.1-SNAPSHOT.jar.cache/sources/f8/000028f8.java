package org.thymeleaf.spring5.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.RequestContext;
import org.thymeleaf.spring5.ISpringTemplateEngine;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/view/AbstractThymeleafView.class */
public abstract class AbstractThymeleafView extends WebApplicationObjectSupport implements View, BeanNameAware {
    public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=ISO-8859-1";
    public static final boolean DEFAULT_PRODUCE_PARTIAL_OUTPUT_WHILE_PROCESSING = true;
    private String beanName;
    private String contentType;
    private boolean contentTypeSet;
    private boolean forceContentType;
    private boolean forceContentTypeSet;
    private String characterEncoding;
    private boolean producePartialOutputWhileProcessing;
    private boolean producePartialOutputWhileProcessingSet;
    private ISpringTemplateEngine templateEngine;
    private String templateName;
    private Locale locale;
    private Map<String, Object> staticVariables;

    public AbstractThymeleafView() {
        this.beanName = null;
        this.contentType = "text/html;charset=ISO-8859-1";
        this.contentTypeSet = false;
        this.forceContentType = false;
        this.forceContentTypeSet = false;
        this.characterEncoding = null;
        this.producePartialOutputWhileProcessing = true;
        this.producePartialOutputWhileProcessingSet = false;
        this.templateEngine = null;
        this.templateName = null;
        this.locale = null;
        this.staticVariables = null;
    }

    public AbstractThymeleafView(String templateName) {
        this.beanName = null;
        this.contentType = "text/html;charset=ISO-8859-1";
        this.contentTypeSet = false;
        this.forceContentType = false;
        this.forceContentTypeSet = false;
        this.characterEncoding = null;
        this.producePartialOutputWhileProcessing = true;
        this.producePartialOutputWhileProcessingSet = false;
        this.templateEngine = null;
        this.templateName = null;
        this.locale = null;
        this.staticVariables = null;
        this.templateName = templateName;
    }

    @Override // org.springframework.web.servlet.View
    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
        this.contentTypeSet = true;
    }

    public boolean isContentTypeSet() {
        return this.contentTypeSet;
    }

    public boolean getForceContentType() {
        return this.forceContentType;
    }

    public void setForceContentType(boolean forceContentType) {
        this.forceContentType = forceContentType;
        this.forceContentTypeSet = true;
    }

    public boolean isForceContentTypeSet() {
        return this.forceContentTypeSet;
    }

    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    public boolean getProducePartialOutputWhileProcessing() {
        return this.producePartialOutputWhileProcessing;
    }

    public void setProducePartialOutputWhileProcessing(boolean producePartialOutputWhileProcessing) {
        this.producePartialOutputWhileProcessing = producePartialOutputWhileProcessing;
        this.producePartialOutputWhileProcessingSet = true;
    }

    public boolean isProducePartialOutputWhileProcessingSet() {
        return this.producePartialOutputWhileProcessingSet;
    }

    public String getBeanName() {
        return this.beanName;
    }

    @Override // org.springframework.beans.factory.BeanNameAware
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getTemplateName() {
        return this.templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public ISpringTemplateEngine getTemplateEngine() {
        return this.templateEngine;
    }

    public void setTemplateEngine(ISpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public Map<String, Object> getStaticVariables() {
        if (this.staticVariables == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(this.staticVariables);
    }

    public void addStaticVariable(String name, Object value) {
        if (this.staticVariables == null) {
            this.staticVariables = new HashMap(3, 1.0f);
        }
        this.staticVariables.put(name, value);
    }

    public void setStaticVariables(Map<String, ?> variables) {
        if (variables != null) {
            if (this.staticVariables == null) {
                this.staticVariables = new HashMap(3, 1.0f);
            }
            this.staticVariables.putAll(variables);
        }
    }

    public static void addRequestContextAsVariable(Map<String, Object> model, String variableName, RequestContext requestContext) throws ServletException {
        if (model.containsKey(variableName)) {
            throw new ServletException("Cannot expose request context in model attribute '" + variableName + "' because an existing model object of the same name");
        }
        model.put(variableName, requestContext);
    }
}