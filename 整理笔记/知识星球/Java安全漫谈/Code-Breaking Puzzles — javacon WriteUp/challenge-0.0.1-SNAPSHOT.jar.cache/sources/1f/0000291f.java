package org.thymeleaf.standard.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/FragmentSignatureUtils.class */
public final class FragmentSignatureUtils {
    private static final char FRAGMENT_SIGNATURE_PARAMETERS_START = '(';
    private static final char FRAGMENT_SIGNATURE_PARAMETERS_END = ')';

    public static FragmentSignature parseFragmentSignature(IEngineConfiguration configuration, String input) {
        FragmentSignature cachedFragmentSignature;
        Validate.notNull(configuration, "Configuration cannot be null");
        Validate.notNull(input, "Input cannot be null");
        if (configuration != null && (cachedFragmentSignature = ExpressionCache.getFragmentSignatureFromCache(configuration, input)) != null) {
            return cachedFragmentSignature;
        }
        FragmentSignature fragmentSignature = internalParseFragmentSignature(input.trim());
        if (fragmentSignature == null) {
            throw new TemplateProcessingException("Could not parse as fragment signature: \"" + input + "\"");
        }
        if (configuration != null) {
            ExpressionCache.putFragmentSignatureIntoCache(configuration, input, fragmentSignature);
        }
        return fragmentSignature;
    }

    static FragmentSignature internalParseFragmentSignature(String input) {
        List<String> parameterNames;
        if (StringUtils.isEmptyOrWhitespace(input)) {
            return null;
        }
        int parameterStart = input.lastIndexOf(40);
        int parameterEnd = input.lastIndexOf(41);
        if (parameterStart != -1 && parameterStart >= parameterEnd) {
            return null;
        }
        String fragmentName = parameterStart == -1 ? input.trim() : input.substring(0, parameterStart).trim();
        String parameters = parameterStart == -1 ? null : input.substring(parameterStart + 1, input.length() - 1);
        if (parameters != null) {
            String[] parameterArray = StringUtils.split(parameters, ",");
            if (parameterArray.length == 0) {
                parameterNames = null;
            } else {
                parameterNames = new ArrayList<>(parameterArray.length + 2);
                for (String parameter : parameterArray) {
                    parameterNames.add(parameter.trim());
                }
            }
        } else {
            parameterNames = null;
        }
        return new FragmentSignature(fragmentName, parameterNames);
    }

    public static Map<String, Object> processParameters(FragmentSignature fragmentSignature, Map<String, Object> specifiedParameters, boolean parametersAreSynthetic) {
        Validate.notNull(fragmentSignature, "Fragment signature cannot be null");
        if (specifiedParameters == null || specifiedParameters.size() == 0) {
            if (fragmentSignature.hasParameters()) {
                throw new TemplateProcessingException("Cannot resolve fragment. Signature \"" + fragmentSignature.getStringRepresentation() + "\" declares parameters, but fragment selection did not specify any parameters.");
            }
            return null;
        } else if (parametersAreSynthetic && !fragmentSignature.hasParameters()) {
            throw new TemplateProcessingException("Cannot resolve fragment. Signature \"" + fragmentSignature.getStringRepresentation() + "\" declares no parameters, but fragment selection did specify parameters in a synthetic manner (without names), which is not correct due to the fact parameters cannot be assigned names unless signature specifies these names.");
        } else {
            if (parametersAreSynthetic) {
                List<String> signatureParameterNames = fragmentSignature.getParameterNames();
                if (signatureParameterNames.size() != specifiedParameters.size()) {
                    throw new TemplateProcessingException("Cannot resolve fragment. Signature \"" + fragmentSignature.getStringRepresentation() + "\" declares " + signatureParameterNames.size() + " parameters, but fragment selection specifies " + specifiedParameters.size() + " parameters. Fragment selection does not correctly match.");
                }
                Map<String, Object> processedParameters = new HashMap<>(signatureParameterNames.size() + 1, 1.0f);
                int index = 0;
                for (String parameterName : signatureParameterNames) {
                    int i = index;
                    index++;
                    String syntheticParameterName = getSyntheticParameterNameForIndex(i);
                    Object parameterValue = specifiedParameters.get(syntheticParameterName);
                    processedParameters.put(parameterName, parameterValue);
                }
                return processedParameters;
            } else if (!fragmentSignature.hasParameters()) {
                return specifiedParameters;
            } else {
                List<String> parameterNames = fragmentSignature.getParameterNames();
                for (String parameterName2 : parameterNames) {
                    if (!specifiedParameters.containsKey(parameterName2)) {
                        throw new TemplateProcessingException("Cannot resolve fragment. Signature \"" + fragmentSignature.getStringRepresentation() + "\" declares parameter \"" + parameterName2 + "\", which is not specified at the fragment selection.");
                    }
                }
                return specifiedParameters;
            }
        }
    }

    static String getSyntheticParameterNameForIndex(int i) {
        return "_arg" + i;
    }

    private FragmentSignatureUtils() {
    }
}