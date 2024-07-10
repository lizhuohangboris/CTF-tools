package org.thymeleaf.linkbuilder;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.Validate;
import org.unbescape.uri.UriEscape;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/linkbuilder/StandardLinkBuilder.class */
public class StandardLinkBuilder extends AbstractLinkBuilder {
    private static final char URL_TEMPLATE_DELIMITER_PREFIX = '{';
    private static final char URL_TEMPLATE_DELIMITER_SUFFIX = '}';
    private static final String URL_TEMPLATE_DELIMITER_SEGMENT_PREFIX = "{/";

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/linkbuilder/StandardLinkBuilder$LinkType.class */
    protected enum LinkType {
        ABSOLUTE,
        CONTEXT_RELATIVE,
        SERVER_RELATIVE,
        BASE_RELATIVE
    }

    @Override // org.thymeleaf.linkbuilder.ILinkBuilder
    public final String buildLink(IExpressionContext context, String base, Map<String, Object> parameters) {
        LinkType linkType;
        Validate.notNull(context, "Expression context cannot be null");
        if (base == null) {
            return null;
        }
        Map<String, Object> linkParameters = (parameters == null || parameters.size() == 0) ? null : new LinkedHashMap<>(parameters);
        if (isLinkBaseAbsolute(base)) {
            linkType = LinkType.ABSOLUTE;
        } else if (isLinkBaseContextRelative(base)) {
            linkType = LinkType.CONTEXT_RELATIVE;
        } else if (isLinkBaseServerRelative(base)) {
            linkType = LinkType.SERVER_RELATIVE;
        } else {
            linkType = LinkType.BASE_RELATIVE;
        }
        int hashPosition = findCharInSequence(base, '#');
        boolean mightHaveVariableTemplates = findCharInSequence(base, '{') >= 0;
        String contextPath = linkType == LinkType.CONTEXT_RELATIVE ? computeContextPath(context, base, parameters) : null;
        boolean contextPathEmpty = contextPath == null || contextPath.length() == 0 || contextPath.equals("/");
        if (contextPathEmpty && linkType != LinkType.SERVER_RELATIVE && ((linkParameters == null || linkParameters.size() == 0) && hashPosition < 0 && !mightHaveVariableTemplates)) {
            return processLink(context, base);
        }
        StringBuilder linkBase = new StringBuilder(base);
        String urlFragment = "";
        if (hashPosition > 0) {
            urlFragment = linkBase.substring(hashPosition);
            linkBase.delete(hashPosition, linkBase.length());
        }
        if (mightHaveVariableTemplates) {
            linkBase = replaceTemplateParamsInBase(linkBase, linkParameters);
        }
        if (linkParameters != null && linkParameters.size() > 0) {
            boolean linkBaseHasQuestionMark = findCharInSequence(linkBase, '?') >= 0;
            if (linkBaseHasQuestionMark) {
                linkBase.append('&');
            } else {
                linkBase.append('?');
            }
            processAllRemainingParametersAsQueryParams(linkBase, linkParameters);
        }
        if (urlFragment.length() > 0) {
            linkBase.append(urlFragment);
        }
        if (linkType == LinkType.SERVER_RELATIVE) {
            linkBase.delete(0, 1);
        }
        if (linkType == LinkType.CONTEXT_RELATIVE && !contextPathEmpty) {
            linkBase.insert(0, contextPath);
        }
        return processLink(context, linkBase.toString());
    }

    private static int findCharInSequence(CharSequence seq, char character) {
        char c;
        int n = seq.length();
        do {
            int i = n;
            n--;
            if (i != 0) {
                c = seq.charAt(n);
            } else {
                return -1;
            }
        } while (c != character);
        return n;
    }

    private static boolean isLinkBaseAbsolute(CharSequence linkBase) {
        int linkBaseLen = linkBase.length();
        if (linkBaseLen < 2) {
            return false;
        }
        char c0 = linkBase.charAt(0);
        if (c0 == 'm' || c0 == 'M') {
            if (linkBase.length() >= 7 && Character.toLowerCase(linkBase.charAt(1)) == 'a' && Character.toLowerCase(linkBase.charAt(2)) == 'i' && Character.toLowerCase(linkBase.charAt(3)) == 'l' && Character.toLowerCase(linkBase.charAt(4)) == 't' && Character.toLowerCase(linkBase.charAt(5)) == 'o' && Character.toLowerCase(linkBase.charAt(6)) == ':') {
                return true;
            }
        } else if (c0 == '/') {
            return linkBase.charAt(1) == '/';
        }
        for (int i = 0; i < linkBaseLen - 2; i++) {
            if (linkBase.charAt(i) == ':' && linkBase.charAt(i + 1) == '/' && linkBase.charAt(i + 2) == '/') {
                return true;
            }
        }
        return false;
    }

    private static boolean isLinkBaseContextRelative(CharSequence linkBase) {
        if (linkBase.length() == 0 || linkBase.charAt(0) != '/') {
            return false;
        }
        return linkBase.length() == 1 || linkBase.charAt(1) != '/';
    }

    private static boolean isLinkBaseServerRelative(CharSequence linkBase) {
        return linkBase.length() >= 2 && linkBase.charAt(0) == '~' && linkBase.charAt(1) == '/';
    }

    private static StringBuilder replaceTemplateParamsInBase(StringBuilder linkBase, Map<String, Object> parameters) {
        String escapeUriPathSegment;
        if (parameters == null) {
            return linkBase;
        }
        int questionMarkPosition = findCharInSequence(linkBase, '?');
        Set<String> parameterNames = parameters.keySet();
        Set<String> alreadyProcessedParameters = null;
        for (String parameterName : parameterNames) {
            boolean escapeAsPathSegment = false;
            String template = '{' + parameterName + '}';
            int templateIndex = linkBase.indexOf(template);
            if (templateIndex < 0) {
                template = URL_TEMPLATE_DELIMITER_SEGMENT_PREFIX + parameterName + '}';
                templateIndex = linkBase.indexOf(template);
                if (templateIndex >= 0) {
                    escapeAsPathSegment = true;
                }
            }
            if (alreadyProcessedParameters == null) {
                alreadyProcessedParameters = new HashSet<>(parameterNames.size());
            }
            alreadyProcessedParameters.add(parameterName);
            Object parameterValue = parameters.get(parameterName);
            String templateReplacement = formatParameterValueAsUnescapedVariableTemplate(parameterValue);
            int templateReplacementLen = templateReplacement.length();
            int templateLen = template.length();
            int i = templateIndex;
            while (true) {
                int start = i;
                if (start > -1) {
                    if (questionMarkPosition == -1 || start < questionMarkPosition) {
                        escapeUriPathSegment = escapeAsPathSegment ? UriEscape.escapeUriPathSegment(templateReplacement) : UriEscape.escapeUriPath(templateReplacement);
                    } else {
                        escapeUriPathSegment = UriEscape.escapeUriQueryParam(templateReplacement);
                    }
                    String escapedReplacement = escapeUriPathSegment;
                    linkBase.replace(start, start + templateLen, escapedReplacement);
                    i = linkBase.indexOf(template, start + templateReplacementLen);
                }
            }
        }
        if (alreadyProcessedParameters != null) {
            for (String alreadyProcessedParameter : alreadyProcessedParameters) {
                parameters.remove(alreadyProcessedParameter);
            }
        }
        return linkBase;
    }

    private static String formatParameterValueAsUnescapedVariableTemplate(Object parameterValue) {
        if (parameterValue == null) {
            return "";
        }
        if (!(parameterValue instanceof List)) {
            return parameterValue.toString();
        }
        List<?> values = (List) parameterValue;
        int valuesLen = values.size();
        StringBuilder strBuilder = new StringBuilder(valuesLen * 16);
        for (int i = 0; i < valuesLen; i++) {
            Object valueItem = values.get(i);
            if (strBuilder.length() > 0) {
                strBuilder.append(',');
            }
            strBuilder.append(valueItem == null ? "" : valueItem.toString());
        }
        return strBuilder.toString();
    }

    private static void processAllRemainingParametersAsQueryParams(StringBuilder strBuilder, Map<String, Object> parameters) {
        int parameterSize = parameters.size();
        if (parameterSize <= 0) {
            return;
        }
        Set<String> parameterNames = parameters.keySet();
        int i = 0;
        for (String parameterName : parameterNames) {
            Object value = parameters.get(parameterName);
            if (value == null) {
                if (i > 0) {
                    strBuilder.append('&');
                }
                strBuilder.append(UriEscape.escapeUriQueryParam(parameterName));
                i++;
            } else if (!(value instanceof List)) {
                if (i > 0) {
                    strBuilder.append('&');
                }
                strBuilder.append(UriEscape.escapeUriQueryParam(parameterName));
                strBuilder.append('=');
                strBuilder.append(UriEscape.escapeUriQueryParam(value.toString()));
                i++;
            } else {
                List<?> values = (List) value;
                int valuesLen = values.size();
                for (int j = 0; j < valuesLen; j++) {
                    Object valueItem = values.get(j);
                    if (i > 0 || j > 0) {
                        strBuilder.append('&');
                    }
                    strBuilder.append(UriEscape.escapeUriQueryParam(parameterName));
                    if (valueItem != null) {
                        strBuilder.append('=');
                        strBuilder.append(UriEscape.escapeUriQueryParam(valueItem.toString()));
                    }
                }
                i++;
            }
        }
    }

    protected String computeContextPath(IExpressionContext context, String base, Map<String, Object> parameters) {
        if (!(context instanceof IWebContext)) {
            throw new TemplateProcessingException("Link base \"" + base + "\" cannot be context relative (/...) unless the context used for executing the engine implements the " + IWebContext.class.getName() + " interface");
        }
        HttpServletRequest request = ((IWebContext) context).getRequest();
        return request.getContextPath();
    }

    protected String processLink(IExpressionContext context, String link) {
        if (!(context instanceof IWebContext)) {
            return link;
        }
        HttpServletResponse response = ((IWebContext) context).getResponse();
        return response != null ? response.encodeURL(link) : link;
    }
}