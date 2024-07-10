package org.thymeleaf.standard;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.dialect.IExecutionAttributeDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.expression.IStandardConversionService;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.IStandardVariableExpressionEvaluator;
import org.thymeleaf.standard.expression.OGNLVariableExpressionEvaluator;
import org.thymeleaf.standard.expression.StandardConversionService;
import org.thymeleaf.standard.expression.StandardExpressionObjectFactory;
import org.thymeleaf.standard.expression.StandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.processor.StandardActionTagProcessor;
import org.thymeleaf.standard.processor.StandardAltTitleTagProcessor;
import org.thymeleaf.standard.processor.StandardAssertTagProcessor;
import org.thymeleaf.standard.processor.StandardAttrTagProcessor;
import org.thymeleaf.standard.processor.StandardAttrappendTagProcessor;
import org.thymeleaf.standard.processor.StandardAttrprependTagProcessor;
import org.thymeleaf.standard.processor.StandardBlockTagProcessor;
import org.thymeleaf.standard.processor.StandardCaseTagProcessor;
import org.thymeleaf.standard.processor.StandardClassappendTagProcessor;
import org.thymeleaf.standard.processor.StandardConditionalCommentProcessor;
import org.thymeleaf.standard.processor.StandardConditionalFixedValueTagProcessor;
import org.thymeleaf.standard.processor.StandardDOMEventAttributeTagProcessor;
import org.thymeleaf.standard.processor.StandardDefaultAttributesTagProcessor;
import org.thymeleaf.standard.processor.StandardEachTagProcessor;
import org.thymeleaf.standard.processor.StandardFragmentTagProcessor;
import org.thymeleaf.standard.processor.StandardHrefTagProcessor;
import org.thymeleaf.standard.processor.StandardIfTagProcessor;
import org.thymeleaf.standard.processor.StandardIncludeTagProcessor;
import org.thymeleaf.standard.processor.StandardInlineEnablementTemplateBoundariesProcessor;
import org.thymeleaf.standard.processor.StandardInlineHTMLTagProcessor;
import org.thymeleaf.standard.processor.StandardInlineTextualTagProcessor;
import org.thymeleaf.standard.processor.StandardInlineXMLTagProcessor;
import org.thymeleaf.standard.processor.StandardInliningCDATASectionProcessor;
import org.thymeleaf.standard.processor.StandardInliningCommentProcessor;
import org.thymeleaf.standard.processor.StandardInliningTextProcessor;
import org.thymeleaf.standard.processor.StandardInsertTagProcessor;
import org.thymeleaf.standard.processor.StandardLangXmlLangTagProcessor;
import org.thymeleaf.standard.processor.StandardMethodTagProcessor;
import org.thymeleaf.standard.processor.StandardNonRemovableAttributeTagProcessor;
import org.thymeleaf.standard.processor.StandardObjectTagProcessor;
import org.thymeleaf.standard.processor.StandardRefAttributeTagProcessor;
import org.thymeleaf.standard.processor.StandardRemovableAttributeTagProcessor;
import org.thymeleaf.standard.processor.StandardRemoveTagProcessor;
import org.thymeleaf.standard.processor.StandardReplaceTagProcessor;
import org.thymeleaf.standard.processor.StandardSrcTagProcessor;
import org.thymeleaf.standard.processor.StandardStyleappendTagProcessor;
import org.thymeleaf.standard.processor.StandardSubstituteByTagProcessor;
import org.thymeleaf.standard.processor.StandardSwitchTagProcessor;
import org.thymeleaf.standard.processor.StandardTextTagProcessor;
import org.thymeleaf.standard.processor.StandardTranslationDocTypeProcessor;
import org.thymeleaf.standard.processor.StandardUnlessTagProcessor;
import org.thymeleaf.standard.processor.StandardUtextTagProcessor;
import org.thymeleaf.standard.processor.StandardValueTagProcessor;
import org.thymeleaf.standard.processor.StandardWithTagProcessor;
import org.thymeleaf.standard.processor.StandardXmlBaseTagProcessor;
import org.thymeleaf.standard.processor.StandardXmlLangTagProcessor;
import org.thymeleaf.standard.processor.StandardXmlNsTagProcessor;
import org.thymeleaf.standard.processor.StandardXmlSpaceTagProcessor;
import org.thymeleaf.standard.serializer.IStandardCSSSerializer;
import org.thymeleaf.standard.serializer.IStandardJavaScriptSerializer;
import org.thymeleaf.standard.serializer.StandardCSSSerializer;
import org.thymeleaf.standard.serializer.StandardJavaScriptSerializer;
import org.thymeleaf.standard.serializer.StandardSerializers;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/StandardDialect.class */
public class StandardDialect extends AbstractProcessorDialect implements IExecutionAttributeDialect, IExpressionObjectDialect {
    public static final String NAME = "Standard";
    public static final String PREFIX = "th";
    public static final int PROCESSOR_PRECEDENCE = 1000;
    private IStandardVariableExpressionEvaluator variableExpressionEvaluator;
    private IStandardExpressionParser expressionParser;
    private IStandardConversionService conversionService;
    private IStandardJavaScriptSerializer javaScriptSerializer;
    private IStandardCSSSerializer cssSerializer;
    private IExpressionObjectFactory expressionObjectFactory;

    public StandardDialect() {
        super(NAME, "th", 1000);
        this.variableExpressionEvaluator = null;
        this.expressionParser = null;
        this.conversionService = null;
        this.javaScriptSerializer = null;
        this.cssSerializer = null;
        this.expressionObjectFactory = null;
    }

    public StandardDialect(String name, String prefix, int processorPrecedence) {
        super(name, prefix, processorPrecedence);
        this.variableExpressionEvaluator = null;
        this.expressionParser = null;
        this.conversionService = null;
        this.javaScriptSerializer = null;
        this.cssSerializer = null;
        this.expressionObjectFactory = null;
    }

    public IStandardVariableExpressionEvaluator getVariableExpressionEvaluator() {
        if (this.variableExpressionEvaluator == null) {
            this.variableExpressionEvaluator = new OGNLVariableExpressionEvaluator(true);
        }
        return this.variableExpressionEvaluator;
    }

    public void setVariableExpressionEvaluator(IStandardVariableExpressionEvaluator variableExpressionEvaluator) {
        Validate.notNull(variableExpressionEvaluator, "Standard Variable Expression Evaluator cannot be null");
        this.variableExpressionEvaluator = variableExpressionEvaluator;
    }

    public IStandardExpressionParser getExpressionParser() {
        if (this.expressionParser == null) {
            this.expressionParser = new StandardExpressionParser();
        }
        return this.expressionParser;
    }

    public void setExpressionParser(IStandardExpressionParser expressionParser) {
        Validate.notNull(expressionParser, "Standard Expression Parser cannot be null");
        this.expressionParser = expressionParser;
    }

    public IStandardConversionService getConversionService() {
        if (this.conversionService == null) {
            this.conversionService = new StandardConversionService();
        }
        return this.conversionService;
    }

    public void setConversionService(IStandardConversionService conversionService) {
        Validate.notNull(conversionService, "Standard Conversion Service cannot be null");
        this.conversionService = conversionService;
    }

    public IStandardJavaScriptSerializer getJavaScriptSerializer() {
        if (this.javaScriptSerializer == null) {
            this.javaScriptSerializer = new StandardJavaScriptSerializer(true);
        }
        return this.javaScriptSerializer;
    }

    public void setJavaScriptSerializer(IStandardJavaScriptSerializer javaScriptSerializer) {
        Validate.notNull(javaScriptSerializer, "Standard JavaScript Serializer cannot be null");
        this.javaScriptSerializer = javaScriptSerializer;
    }

    public IStandardCSSSerializer getCSSSerializer() {
        if (this.cssSerializer == null) {
            this.cssSerializer = new StandardCSSSerializer();
        }
        return this.cssSerializer;
    }

    public void setCSSSerializer(IStandardCSSSerializer cssSerializer) {
        Validate.notNull(cssSerializer, "Standard CSS Serializer cannot be null");
        this.cssSerializer = cssSerializer;
    }

    public Map<String, Object> getExecutionAttributes() {
        Map<String, Object> executionAttributes = new HashMap<>(5, 1.0f);
        executionAttributes.put(StandardExpressions.STANDARD_VARIABLE_EXPRESSION_EVALUATOR_ATTRIBUTE_NAME, getVariableExpressionEvaluator());
        executionAttributes.put(StandardExpressions.STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME, getExpressionParser());
        executionAttributes.put(StandardExpressions.STANDARD_CONVERSION_SERVICE_ATTRIBUTE_NAME, getConversionService());
        executionAttributes.put(StandardSerializers.STANDARD_JAVASCRIPT_SERIALIZER_ATTRIBUTE_NAME, getJavaScriptSerializer());
        executionAttributes.put(StandardSerializers.STANDARD_CSS_SERIALIZER_ATTRIBUTE_NAME, getCSSSerializer());
        return executionAttributes;
    }

    public IExpressionObjectFactory getExpressionObjectFactory() {
        if (this.expressionObjectFactory == null) {
            this.expressionObjectFactory = new StandardExpressionObjectFactory();
        }
        return this.expressionObjectFactory;
    }

    public Set<IProcessor> getProcessors(String dialectPrefix) {
        return createStandardProcessorsSet(dialectPrefix);
    }

    public static Set<IProcessor> createStandardProcessorsSet(String dialectPrefix) {
        String[] strArr;
        String[] strArr2;
        String[] strArr3;
        String[] strArr4;
        Set<IProcessor> processors = new LinkedHashSet<>();
        processors.add(new StandardActionTagProcessor(dialectPrefix));
        processors.add(new StandardAltTitleTagProcessor(dialectPrefix));
        processors.add(new StandardAssertTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardAttrTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardAttrappendTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardAttrprependTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardCaseTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardClassappendTagProcessor(dialectPrefix));
        for (String attrName : StandardConditionalFixedValueTagProcessor.ATTR_NAMES) {
            processors.add(new StandardConditionalFixedValueTagProcessor(dialectPrefix, attrName));
        }
        for (String attrName2 : StandardDOMEventAttributeTagProcessor.ATTR_NAMES) {
            processors.add(new StandardDOMEventAttributeTagProcessor(dialectPrefix, attrName2));
        }
        processors.add(new StandardEachTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardFragmentTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardHrefTagProcessor(dialectPrefix));
        processors.add(new StandardIfTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardIncludeTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardInlineHTMLTagProcessor(dialectPrefix));
        processors.add(new StandardInsertTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardLangXmlLangTagProcessor(dialectPrefix));
        processors.add(new StandardMethodTagProcessor(dialectPrefix));
        for (String attrName3 : StandardNonRemovableAttributeTagProcessor.ATTR_NAMES) {
            processors.add(new StandardNonRemovableAttributeTagProcessor(dialectPrefix, attrName3));
        }
        processors.add(new StandardObjectTagProcessor(TemplateMode.HTML, dialectPrefix));
        for (String attrName4 : StandardRemovableAttributeTagProcessor.ATTR_NAMES) {
            processors.add(new StandardRemovableAttributeTagProcessor(dialectPrefix, attrName4));
        }
        processors.add(new StandardRemoveTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardReplaceTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardSrcTagProcessor(dialectPrefix));
        processors.add(new StandardStyleappendTagProcessor(dialectPrefix));
        processors.add(new StandardSubstituteByTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardSwitchTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardTextTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardUnlessTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardUtextTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardValueTagProcessor(dialectPrefix));
        processors.add(new StandardWithTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardXmlBaseTagProcessor(dialectPrefix));
        processors.add(new StandardXmlLangTagProcessor(dialectPrefix));
        processors.add(new StandardXmlSpaceTagProcessor(dialectPrefix));
        processors.add(new StandardXmlNsTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardRefAttributeTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardDefaultAttributesTagProcessor(TemplateMode.HTML, dialectPrefix));
        processors.add(new StandardBlockTagProcessor(TemplateMode.HTML, dialectPrefix, StandardBlockTagProcessor.ELEMENT_NAME));
        processors.add(new StandardInliningTextProcessor(TemplateMode.HTML));
        processors.add(new StandardInliningCDATASectionProcessor(TemplateMode.HTML));
        processors.add(new StandardTranslationDocTypeProcessor());
        processors.add(new StandardInliningCommentProcessor(TemplateMode.HTML));
        processors.add(new StandardConditionalCommentProcessor());
        processors.add(new StandardInlineEnablementTemplateBoundariesProcessor(TemplateMode.HTML));
        processors.add(new StandardAssertTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardAttrTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardAttrappendTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardAttrprependTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardCaseTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardEachTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardFragmentTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardIfTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardIncludeTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardInlineXMLTagProcessor(dialectPrefix));
        processors.add(new StandardInsertTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardObjectTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardRemoveTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardReplaceTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardSubstituteByTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardSwitchTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardTextTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardUnlessTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardUtextTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardWithTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardXmlNsTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardRefAttributeTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardDefaultAttributesTagProcessor(TemplateMode.XML, dialectPrefix));
        processors.add(new StandardBlockTagProcessor(TemplateMode.XML, dialectPrefix, StandardBlockTagProcessor.ELEMENT_NAME));
        processors.add(new StandardInliningTextProcessor(TemplateMode.XML));
        processors.add(new StandardInliningCDATASectionProcessor(TemplateMode.XML));
        processors.add(new StandardInliningCommentProcessor(TemplateMode.XML));
        processors.add(new StandardInlineEnablementTemplateBoundariesProcessor(TemplateMode.XML));
        processors.add(new StandardAssertTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardCaseTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardEachTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardIfTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardInlineTextualTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardInsertTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardObjectTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardRemoveTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardReplaceTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardSwitchTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardTextTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardUnlessTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardUtextTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardWithTagProcessor(TemplateMode.TEXT, dialectPrefix));
        processors.add(new StandardBlockTagProcessor(TemplateMode.TEXT, dialectPrefix, StandardBlockTagProcessor.ELEMENT_NAME));
        processors.add(new StandardBlockTagProcessor(TemplateMode.TEXT, null, ""));
        processors.add(new StandardInliningTextProcessor(TemplateMode.TEXT));
        processors.add(new StandardInlineEnablementTemplateBoundariesProcessor(TemplateMode.TEXT));
        processors.add(new StandardAssertTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardCaseTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardEachTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardIfTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardInlineTextualTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardInsertTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardObjectTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardRemoveTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardReplaceTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardSwitchTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardTextTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardUnlessTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardUtextTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardWithTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix));
        processors.add(new StandardBlockTagProcessor(TemplateMode.JAVASCRIPT, dialectPrefix, StandardBlockTagProcessor.ELEMENT_NAME));
        processors.add(new StandardBlockTagProcessor(TemplateMode.JAVASCRIPT, null, ""));
        processors.add(new StandardInliningTextProcessor(TemplateMode.JAVASCRIPT));
        processors.add(new StandardInlineEnablementTemplateBoundariesProcessor(TemplateMode.JAVASCRIPT));
        processors.add(new StandardAssertTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardCaseTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardEachTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardIfTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardInlineTextualTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardInsertTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardObjectTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardRemoveTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardReplaceTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardSwitchTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardTextTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardUnlessTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardUtextTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardWithTagProcessor(TemplateMode.CSS, dialectPrefix));
        processors.add(new StandardBlockTagProcessor(TemplateMode.CSS, dialectPrefix, StandardBlockTagProcessor.ELEMENT_NAME));
        processors.add(new StandardBlockTagProcessor(TemplateMode.CSS, null, ""));
        processors.add(new StandardInliningTextProcessor(TemplateMode.CSS));
        processors.add(new StandardInlineEnablementTemplateBoundariesProcessor(TemplateMode.CSS));
        return processors;
    }
}