package org.thymeleaf.standard.processor;

import java.io.Writer;
import java.util.Map;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.engine.TemplateModel;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.model.ICloseElementTag;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IOpenElementTag;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.ITemplateEvent;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.Fragment;
import org.thymeleaf.standard.expression.FragmentExpression;
import org.thymeleaf.standard.expression.FragmentSignature;
import org.thymeleaf.standard.expression.FragmentSignatureUtils;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.NoOpToken;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.EscapedAttributeUtils;
import org.thymeleaf.util.FastStringWriter;
import org.thymeleaf.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/processor/AbstractStandardFragmentInsertionTagProcessor.class */
public abstract class AbstractStandardFragmentInsertionTagProcessor extends AbstractAttributeTagProcessor {
    private static final String FRAGMENT_ATTR_NAME = "fragment";
    private final boolean replaceHost;
    private final boolean insertOnlyContents;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractStandardFragmentInsertionTagProcessor(TemplateMode templateMode, String dialectPrefix, String attrName, int precedence, boolean replaceHost) {
        this(templateMode, dialectPrefix, attrName, precedence, replaceHost, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractStandardFragmentInsertionTagProcessor(TemplateMode templateMode, String dialectPrefix, String attrName, int precedence, boolean replaceHost, boolean insertOnlyContents) {
        super(templateMode, dialectPrefix, null, false, attrName, true, precedence, true);
        this.replaceHost = replaceHost;
        this.insertOnlyContents = insertOnlyContents;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.thymeleaf.processor.element.AbstractAttributeTagProcessor
    public void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        FragmentSignature fragmentSignature;
        if (StringUtils.isEmptyOrWhitespace(attributeValue)) {
            throw new TemplateProcessingException("Fragment specifications cannot be empty");
        }
        IEngineConfiguration configuration = context.getConfiguration();
        Object fragmentObj = computeFragment(context, attributeValue);
        if (fragmentObj == null) {
            throw new TemplateInputException("Error resolving fragment: \"" + attributeValue + "\": template or fragment could not be resolved");
        }
        if (fragmentObj == NoOpToken.VALUE) {
            return;
        }
        if (fragmentObj == Fragment.EMPTY_FRAGMENT) {
            if (this.replaceHost) {
                structureHandler.removeElement();
                return;
            } else {
                structureHandler.removeBody();
                return;
            }
        }
        Fragment fragment = (Fragment) fragmentObj;
        TemplateModel fragmentModel = fragment.getTemplateModel();
        Map<String, Object> fragmentParameters = fragment.getParameters();
        boolean signatureApplied = false;
        ITemplateEvent firstEvent = fragmentModel.size() > 2 ? fragmentModel.get(1) : null;
        if (firstEvent != null && IProcessableElementTag.class.isAssignableFrom(firstEvent.getClass())) {
            String dialectPrefix = attributeName.getPrefix();
            IProcessableElementTag fragmentHolderEvent = (IProcessableElementTag) firstEvent;
            if (fragmentHolderEvent.hasAttribute(dialectPrefix, "fragment")) {
                String fragmentSignatureSpec = EscapedAttributeUtils.unescapeAttribute(fragmentModel.getTemplateMode(), fragmentHolderEvent.getAttributeValue(dialectPrefix, "fragment"));
                if (!StringUtils.isEmptyOrWhitespace(fragmentSignatureSpec) && (fragmentSignature = FragmentSignatureUtils.parseFragmentSignature(configuration, fragmentSignatureSpec)) != null) {
                    fragmentParameters = FragmentSignatureUtils.processParameters(fragmentSignature, fragmentParameters, fragment.hasSyntheticParameters());
                    signatureApplied = true;
                }
            }
        }
        if (!signatureApplied && fragment.hasSyntheticParameters()) {
            throw new TemplateProcessingException("Fragment '" + attributeValue + "' specifies synthetic (unnamed) parameters, but the resolved fragment does not match a fragment signature (th:fragment,data-th-fragment) which could apply names to the specified parameters.");
        }
        if (context.getTemplateMode() != fragmentModel.getTemplateMode()) {
            if (this.insertOnlyContents) {
                throw new TemplateProcessingException("Template being processed uses template mode " + context.getTemplateMode() + ", inserted fragment \"" + attributeValue + "\" uses template mode " + fragmentModel.getTemplateMode() + ". Cross-template-mode fragment insertion is not allowed using the " + attributeName + " attribute, which is no longer recommended for use as of Thymeleaf 3.0. Use {th:insert,data-th-insert} or {th:replace,data-th-replace} instead, which do not remove the container element from the fragment being inserted.");
            }
            if (fragmentParameters != null && fragmentParameters.size() > 0) {
                if (!(context instanceof IEngineContext)) {
                    throw new TemplateProcessingException("Parameterized fragment insertion is not supported because local variable support is DISABLED. This is due to the use of an implementation of the " + ITemplateContext.class.getName() + " interface that does not provide local-variable support. In order to have local-variable support, the variables map implementation should also implement the " + IEngineContext.class.getName() + " interface");
                }
                ((IEngineContext) context).setVariables(fragmentParameters);
            }
            Writer stringWriter = new FastStringWriter(200);
            configuration.getTemplateManager().process(fragmentModel, context, stringWriter);
            if (this.replaceHost) {
                structureHandler.replaceWith((CharSequence) stringWriter.toString(), false);
                return;
            } else {
                structureHandler.setBody((CharSequence) stringWriter.toString(), false);
                return;
            }
        }
        TemplateData fragmentTemplateData = fragmentModel.getTemplateData();
        structureHandler.setTemplateData(fragmentTemplateData);
        if (fragmentParameters != null && fragmentParameters.size() > 0) {
            for (Map.Entry<String, Object> fragmentParameterEntry : fragmentParameters.entrySet()) {
                structureHandler.setLocalVariable(fragmentParameterEntry.getKey(), fragmentParameterEntry.getValue());
            }
        }
        if (this.insertOnlyContents && fragmentTemplateData.hasTemplateSelectors()) {
            IModel model = fragmentModel.cloneModel();
            int modelLevel = 0;
            int n = model.size();
            while (true) {
                int i = n;
                n--;
                if (i == 0) {
                    break;
                }
                ITemplateEvent event = model.get(n);
                if (event instanceof ICloseElementTag) {
                    if (!((ICloseElementTag) event).isUnmatched()) {
                        if (modelLevel <= 0) {
                            model.remove(n);
                        }
                        modelLevel++;
                    }
                } else if (event instanceof IOpenElementTag) {
                    modelLevel--;
                    if (modelLevel <= 0) {
                        model.remove(n);
                    }
                } else if (modelLevel <= 0) {
                    model.remove(n);
                }
            }
            if (this.replaceHost) {
                structureHandler.replaceWith(model, true);
            } else {
                structureHandler.setBody(model, true);
            }
        } else if (this.replaceHost) {
            structureHandler.replaceWith((IModel) fragmentModel, true);
        } else {
            structureHandler.setBody((IModel) fragmentModel, true);
        }
    }

    private static Object computeFragment(ITemplateContext context, String input) {
        Object fragmentExpressionResult;
        Object templateNameExpressionResult;
        IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());
        String trimmedInput = input.trim();
        if (shouldBeWrappedAsFragmentExpression(trimmedInput)) {
            FragmentExpression.ExecutedFragmentExpression executedFragmentExpression = FragmentExpression.createExecutedFragmentExpression(context, (FragmentExpression) expressionParser.parseExpression(context, "~{" + trimmedInput + "}"));
            if (executedFragmentExpression.getFragmentSelectorExpressionResult() == null && executedFragmentExpression.getFragmentParameters() == null && (templateNameExpressionResult = executedFragmentExpression.getTemplateNameExpressionResult()) != null) {
                if (templateNameExpressionResult instanceof Fragment) {
                    return templateNameExpressionResult;
                }
                if (templateNameExpressionResult == NoOpToken.VALUE) {
                    return NoOpToken.VALUE;
                }
            }
            return FragmentExpression.resolveExecutedFragmentExpression(context, executedFragmentExpression, true);
        }
        IStandardExpression fragmentExpression = expressionParser.parseExpression(context, trimmedInput);
        if (fragmentExpression != null && (fragmentExpression instanceof FragmentExpression)) {
            fragmentExpressionResult = FragmentExpression.resolveExecutedFragmentExpression(context, FragmentExpression.createExecutedFragmentExpression(context, (FragmentExpression) fragmentExpression), true);
        } else {
            fragmentExpressionResult = fragmentExpression.execute(context);
        }
        if (fragmentExpressionResult == null || fragmentExpressionResult == NoOpToken.VALUE) {
            return fragmentExpressionResult;
        }
        if (!(fragmentExpressionResult instanceof Fragment)) {
            throw new TemplateProcessingException("Invalid fragment specification: \"" + input + "\": expression does not return a Fragment object");
        }
        return fragmentExpressionResult;
    }

    static boolean shouldBeWrappedAsFragmentExpression(String input) {
        int inputLen = input.length();
        if (inputLen > 2 && input.charAt(0) == '~' && input.charAt(1) == '{') {
            return false;
        }
        int bracketLevel = 0;
        int paramLevel = 0;
        boolean inLiteral = false;
        int n = inputLen;
        int i = 0;
        while (true) {
            int i2 = n;
            n--;
            if (i2 != 0) {
                char c = input.charAt(i);
                if ((c >= 'a' && c <= 'z') || c == ' ') {
                    i++;
                } else {
                    if (c == '\'') {
                        inLiteral = !inLiteral;
                    } else if (!inLiteral) {
                        if (c == '{') {
                            bracketLevel++;
                        } else if (c == '}') {
                            bracketLevel--;
                        } else if (bracketLevel == 0) {
                            if (c == '(') {
                                paramLevel++;
                            } else if (c == ')') {
                                paramLevel--;
                            } else if (c == '=' && paramLevel == 1) {
                                return true;
                            } else {
                                if (c == '~' && n != 0 && input.charAt(i + 1) == '{') {
                                    return false;
                                }
                                if (c == ':' && n != 0 && input.charAt(i + 1) == ':') {
                                    return true;
                                }
                            }
                        }
                    }
                    i++;
                }
            } else {
                return true;
            }
        }
    }
}