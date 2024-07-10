package org.thymeleaf.spring5.dialect;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import org.springframework.web.server.ServerWebExchange;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.spring5.expression.SPELVariableExpressionEvaluator;
import org.thymeleaf.spring5.expression.SpringStandardConversionService;
import org.thymeleaf.spring5.expression.SpringStandardExpressionObjectFactory;
import org.thymeleaf.spring5.expression.SpringStandardExpressions;
import org.thymeleaf.spring5.processor.SpringActionTagProcessor;
import org.thymeleaf.spring5.processor.SpringErrorClassTagProcessor;
import org.thymeleaf.spring5.processor.SpringErrorsTagProcessor;
import org.thymeleaf.spring5.processor.SpringHrefTagProcessor;
import org.thymeleaf.spring5.processor.SpringInputCheckboxFieldTagProcessor;
import org.thymeleaf.spring5.processor.SpringInputFileFieldTagProcessor;
import org.thymeleaf.spring5.processor.SpringInputGeneralFieldTagProcessor;
import org.thymeleaf.spring5.processor.SpringInputPasswordFieldTagProcessor;
import org.thymeleaf.spring5.processor.SpringInputRadioFieldTagProcessor;
import org.thymeleaf.spring5.processor.SpringMethodTagProcessor;
import org.thymeleaf.spring5.processor.SpringObjectTagProcessor;
import org.thymeleaf.spring5.processor.SpringOptionFieldTagProcessor;
import org.thymeleaf.spring5.processor.SpringOptionInSelectFieldTagProcessor;
import org.thymeleaf.spring5.processor.SpringSelectFieldTagProcessor;
import org.thymeleaf.spring5.processor.SpringSrcTagProcessor;
import org.thymeleaf.spring5.processor.SpringTextareaFieldTagProcessor;
import org.thymeleaf.spring5.processor.SpringUErrorsTagProcessor;
import org.thymeleaf.spring5.processor.SpringValueTagProcessor;
import org.thymeleaf.spring5.util.SpringVersionUtils;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.expression.IStandardConversionService;
import org.thymeleaf.standard.expression.IStandardVariableExpressionEvaluator;
import org.thymeleaf.standard.processor.StandardActionTagProcessor;
import org.thymeleaf.standard.processor.StandardHrefTagProcessor;
import org.thymeleaf.standard.processor.StandardMethodTagProcessor;
import org.thymeleaf.standard.processor.StandardObjectTagProcessor;
import org.thymeleaf.standard.processor.StandardSrcTagProcessor;
import org.thymeleaf.standard.processor.StandardValueTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/dialect/SpringStandardDialect.class */
public class SpringStandardDialect extends StandardDialect {
    public static final String NAME = "SpringStandard";
    public static final String PREFIX = "th";
    public static final int PROCESSOR_PRECEDENCE = 1000;
    public static final boolean DEFAULT_ENABLE_SPRING_EL_COMPILER = false;
    public static final boolean DEFAULT_RENDER_HIDDEN_MARKERS_BEFORE_CHECKBOXES = false;
    private boolean enableSpringELCompiler;
    private boolean renderHiddenMarkersBeforeCheckboxes;
    private static final Map<String, Object> REACTIVE_MODEL_ADDITIONS_EXECUTION_ATTRIBUTES;
    private static final String WEB_SESSION_EXECUTION_ATTRIBUTE_NAME = "ThymeleafReactiveModelAdditions:thymeleafWebSession";
    private IExpressionObjectFactory expressionObjectFactory;
    private IStandardConversionService conversionService;

    static {
        if (!SpringVersionUtils.isSpringWebFluxPresent()) {
            REACTIVE_MODEL_ADDITIONS_EXECUTION_ATTRIBUTES = Collections.emptyMap();
            return;
        }
        Function<ServerWebExchange, Object> webSessionInitializer = exchange -> {
            return exchange.getSession();
        };
        REACTIVE_MODEL_ADDITIONS_EXECUTION_ATTRIBUTES = Collections.singletonMap(WEB_SESSION_EXECUTION_ATTRIBUTE_NAME, webSessionInitializer);
    }

    public SpringStandardDialect() {
        super(NAME, "th", 1000);
        this.enableSpringELCompiler = false;
        this.renderHiddenMarkersBeforeCheckboxes = false;
        this.expressionObjectFactory = null;
        this.conversionService = null;
    }

    public boolean getEnableSpringELCompiler() {
        return this.enableSpringELCompiler;
    }

    public void setEnableSpringELCompiler(boolean enableSpringELCompiler) {
        this.enableSpringELCompiler = enableSpringELCompiler;
    }

    public boolean getRenderHiddenMarkersBeforeCheckboxes() {
        return this.renderHiddenMarkersBeforeCheckboxes;
    }

    public void setRenderHiddenMarkersBeforeCheckboxes(boolean renderHiddenMarkersBeforeCheckboxes) {
        this.renderHiddenMarkersBeforeCheckboxes = renderHiddenMarkersBeforeCheckboxes;
    }

    @Override // org.thymeleaf.standard.StandardDialect
    public IStandardVariableExpressionEvaluator getVariableExpressionEvaluator() {
        return SPELVariableExpressionEvaluator.INSTANCE;
    }

    @Override // org.thymeleaf.standard.StandardDialect
    public IStandardConversionService getConversionService() {
        if (this.conversionService == null) {
            this.conversionService = new SpringStandardConversionService();
        }
        return this.conversionService;
    }

    @Override // org.thymeleaf.standard.StandardDialect, org.thymeleaf.dialect.IExpressionObjectDialect
    public IExpressionObjectFactory getExpressionObjectFactory() {
        if (this.expressionObjectFactory == null) {
            this.expressionObjectFactory = new SpringStandardExpressionObjectFactory();
        }
        return this.expressionObjectFactory;
    }

    @Override // org.thymeleaf.standard.StandardDialect, org.thymeleaf.dialect.IProcessorDialect
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        return createSpringStandardProcessorsSet(dialectPrefix, this.renderHiddenMarkersBeforeCheckboxes);
    }

    @Override // org.thymeleaf.standard.StandardDialect, org.thymeleaf.dialect.IExecutionAttributeDialect
    public Map<String, Object> getExecutionAttributes() {
        Map<String, Object> executionAttributes = super.getExecutionAttributes();
        executionAttributes.putAll(REACTIVE_MODEL_ADDITIONS_EXECUTION_ATTRIBUTES);
        executionAttributes.put(SpringStandardExpressions.ENABLE_SPRING_EL_COMPILER_ATTRIBUTE_NAME, Boolean.valueOf(getEnableSpringELCompiler()));
        return executionAttributes;
    }

    public static Set<IProcessor> createSpringStandardProcessorsSet(String dialectPrefix) {
        return createSpringStandardProcessorsSet(dialectPrefix, false);
    }

    public static Set<IProcessor> createSpringStandardProcessorsSet(String dialectPrefix, boolean renderHiddenMarkersBeforeCheckboxes) {
        Set<IProcessor> standardProcessors = StandardDialect.createStandardProcessorsSet(dialectPrefix);
        Set<IProcessor> processors = new LinkedHashSet<>(40);
        for (IProcessor standardProcessor : standardProcessors) {
            if (!(standardProcessor instanceof StandardObjectTagProcessor) && !(standardProcessor instanceof StandardActionTagProcessor) && !(standardProcessor instanceof StandardHrefTagProcessor) && !(standardProcessor instanceof StandardMethodTagProcessor) && !(standardProcessor instanceof StandardSrcTagProcessor) && !(standardProcessor instanceof StandardValueTagProcessor)) {
                processors.add(standardProcessor);
            } else if (standardProcessor.getTemplateMode() != TemplateMode.HTML) {
                processors.add(standardProcessor);
            }
        }
        processors.add(new SpringActionTagProcessor(dialectPrefix));
        processors.add(new SpringHrefTagProcessor(dialectPrefix));
        processors.add(new SpringMethodTagProcessor(dialectPrefix));
        processors.add(new SpringSrcTagProcessor(dialectPrefix));
        processors.add(new SpringValueTagProcessor(dialectPrefix));
        processors.add(new SpringObjectTagProcessor(dialectPrefix));
        processors.add(new SpringErrorsTagProcessor(dialectPrefix));
        processors.add(new SpringUErrorsTagProcessor(dialectPrefix));
        processors.add(new SpringInputGeneralFieldTagProcessor(dialectPrefix));
        processors.add(new SpringInputPasswordFieldTagProcessor(dialectPrefix));
        processors.add(new SpringInputCheckboxFieldTagProcessor(dialectPrefix, renderHiddenMarkersBeforeCheckboxes));
        processors.add(new SpringInputRadioFieldTagProcessor(dialectPrefix));
        processors.add(new SpringInputFileFieldTagProcessor(dialectPrefix));
        processors.add(new SpringSelectFieldTagProcessor(dialectPrefix));
        processors.add(new SpringOptionInSelectFieldTagProcessor(dialectPrefix));
        processors.add(new SpringOptionFieldTagProcessor(dialectPrefix));
        processors.add(new SpringTextareaFieldTagProcessor(dialectPrefix));
        processors.add(new SpringErrorClassTagProcessor(dialectPrefix));
        return processors;
    }
}