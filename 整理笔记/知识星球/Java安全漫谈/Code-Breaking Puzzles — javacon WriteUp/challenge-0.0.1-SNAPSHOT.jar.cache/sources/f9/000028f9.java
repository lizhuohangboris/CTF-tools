package org.thymeleaf.spring5.view;

import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.WebExpressionContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.spring5.ISpringTemplateEngine;
import org.thymeleaf.spring5.context.webmvc.SpringWebMvcThymeleafRequestContext;
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContext;
import org.thymeleaf.spring5.naming.SpringContextVariableNames;
import org.thymeleaf.spring5.util.SpringContentTypeUtils;
import org.thymeleaf.standard.expression.FragmentExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.util.FastStringWriter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/view/ThymeleafView.class */
public class ThymeleafView extends AbstractThymeleafView {
    private static final String pathVariablesSelector;
    private Set<String> markupSelectors;

    static {
        String pathVariablesSelectorValue;
        try {
            Field pathVariablesField = View.class.getDeclaredField("PATH_VARIABLES");
            pathVariablesSelectorValue = (String) pathVariablesField.get(null);
        } catch (IllegalAccessException e) {
            pathVariablesSelectorValue = null;
        } catch (NoSuchFieldException e2) {
            pathVariablesSelectorValue = null;
        }
        pathVariablesSelector = pathVariablesSelectorValue;
    }

    public ThymeleafView() {
        this.markupSelectors = null;
    }

    public ThymeleafView(String templateName) {
        super(templateName);
        this.markupSelectors = null;
    }

    public String getMarkupSelector() {
        if (this.markupSelectors == null || this.markupSelectors.size() == 0) {
            return null;
        }
        return this.markupSelectors.iterator().next();
    }

    public void setMarkupSelector(String markupSelector) {
        this.markupSelectors = (markupSelector == null || markupSelector.trim().length() == 0) ? null : Collections.singleton(markupSelector.trim());
    }

    @Override // org.springframework.web.servlet.View
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        renderFragment(this.markupSelectors, model, request, response);
    }

    public void renderFragment(Set<String> markupSelectorsToRender, Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String templateName;
        Set<String> markupSelectors;
        Set<String> processMarkupSelectors;
        Map<String, Object> pathVars;
        ServletContext servletContext = getServletContext();
        String viewTemplateName = getTemplateName();
        ISpringTemplateEngine viewTemplateEngine = getTemplateEngine();
        if (viewTemplateName == null) {
            throw new IllegalArgumentException("Property 'templateName' is required");
        }
        if (getLocale() == null) {
            throw new IllegalArgumentException("Property 'locale' is required");
        }
        if (viewTemplateEngine == null) {
            throw new IllegalArgumentException("Property 'templateEngine' is required");
        }
        Map<String, Object> mergedModel = new HashMap<>(30);
        Map<String, Object> templateStaticVariables = getStaticVariables();
        if (templateStaticVariables != null) {
            mergedModel.putAll(templateStaticVariables);
        }
        if (pathVariablesSelector != null && (pathVars = (Map) request.getAttribute(pathVariablesSelector)) != null) {
            mergedModel.putAll(pathVars);
        }
        if (model != null) {
            mergedModel.putAll(model);
        }
        ApplicationContext applicationContext = getApplicationContext();
        RequestContext requestContext = new RequestContext(request, response, getServletContext(), mergedModel);
        SpringWebMvcThymeleafRequestContext thymeleafRequestContext = new SpringWebMvcThymeleafRequestContext(requestContext, request);
        addRequestContextAsVariable(mergedModel, SpringContextVariableNames.SPRING_REQUEST_CONTEXT, requestContext);
        addRequestContextAsVariable(mergedModel, AbstractTemplateView.SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE, requestContext);
        mergedModel.put(SpringContextVariableNames.THYMELEAF_REQUEST_CONTEXT, thymeleafRequestContext);
        ConversionService conversionService = (ConversionService) request.getAttribute(ConversionService.class.getName());
        ThymeleafEvaluationContext evaluationContext = new ThymeleafEvaluationContext(applicationContext, conversionService);
        mergedModel.put(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, evaluationContext);
        IEngineConfiguration configuration = viewTemplateEngine.getConfiguration();
        WebExpressionContext context = new WebExpressionContext(configuration, request, response, servletContext, getLocale(), mergedModel);
        if (!viewTemplateName.contains("::")) {
            templateName = viewTemplateName;
            markupSelectors = null;
        } else {
            IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);
            try {
                FragmentExpression fragmentExpression = (FragmentExpression) parser.parseExpression(context, "~{" + viewTemplateName + "}");
                FragmentExpression.ExecutedFragmentExpression fragment = FragmentExpression.createExecutedFragmentExpression(context, fragmentExpression);
                templateName = FragmentExpression.resolveTemplateName(fragment);
                markupSelectors = FragmentExpression.resolveFragments(fragment);
                Map<String, Object> nameFragmentParameters = fragment.getFragmentParameters();
                if (nameFragmentParameters != null) {
                    if (fragment.hasSyntheticParameters()) {
                        throw new IllegalArgumentException("Parameters in a view specification must be named (non-synthetic): '" + viewTemplateName + "'");
                    }
                    context.setVariables(nameFragmentParameters);
                }
            } catch (TemplateProcessingException e) {
                throw new IllegalArgumentException("Invalid template name specification: '" + viewTemplateName + "'");
            }
        }
        String templateContentType = getContentType();
        Locale templateLocale = getLocale();
        String templateCharacterEncoding = getCharacterEncoding();
        if (markupSelectors != null && markupSelectors.size() > 0) {
            if (markupSelectorsToRender != null && markupSelectorsToRender.size() > 0) {
                throw new IllegalArgumentException("A markup selector has been specified (" + Arrays.asList(markupSelectors) + ") for a view that was already being executed as a fragment (" + Arrays.asList(markupSelectorsToRender) + "). Only one fragment selection is allowed.");
            }
            processMarkupSelectors = markupSelectors;
        } else if (markupSelectorsToRender != null && markupSelectorsToRender.size() > 0) {
            processMarkupSelectors = markupSelectorsToRender;
        } else {
            processMarkupSelectors = null;
        }
        response.setLocale(templateLocale);
        if (!getForceContentType()) {
            String computedContentType = SpringContentTypeUtils.computeViewContentType(request, templateContentType != null ? templateContentType : "text/html;charset=ISO-8859-1", templateCharacterEncoding != null ? Charset.forName(templateCharacterEncoding) : null);
            response.setContentType(computedContentType);
        } else {
            if (templateContentType != null) {
                response.setContentType(templateContentType);
            } else {
                response.setContentType("text/html;charset=ISO-8859-1");
            }
            if (templateCharacterEncoding != null) {
                response.setCharacterEncoding(templateCharacterEncoding);
            }
        }
        boolean producePartialOutputWhileProcessing = getProducePartialOutputWhileProcessing();
        Writer templateWriter = producePartialOutputWhileProcessing ? response.getWriter() : new FastStringWriter(1024);
        viewTemplateEngine.process(templateName, processMarkupSelectors, context, templateWriter);
        if (!producePartialOutputWhileProcessing) {
            response.getWriter().write(templateWriter.toString());
            response.getWriter().flush();
        }
    }
}