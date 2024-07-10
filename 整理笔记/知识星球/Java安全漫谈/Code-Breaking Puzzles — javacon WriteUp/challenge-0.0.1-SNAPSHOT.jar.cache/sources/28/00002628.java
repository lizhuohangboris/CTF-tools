package org.springframework.web.servlet.mvc.method.annotation;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/method/annotation/MatrixVariableMapMethodArgumentResolver.class */
public class MatrixVariableMapMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    public boolean supportsParameter(MethodParameter parameter) {
        MatrixVariable matrixVariable = (MatrixVariable) parameter.getParameterAnnotation(MatrixVariable.class);
        return (matrixVariable == null || !Map.class.isAssignableFrom(parameter.getParameterType()) || StringUtils.hasText(matrixVariable.name())) ? false : true;
    }

    @Override // org.springframework.web.method.support.HandlerMethodArgumentResolver
    @Nullable
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest request, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        Map<String, MultiValueMap<String, String>> matrixVariables = (Map) request.getAttribute(HandlerMapping.MATRIX_VARIABLES_ATTRIBUTE, 0);
        if (CollectionUtils.isEmpty(matrixVariables)) {
            return Collections.emptyMap();
        }
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        MatrixVariable ann = (MatrixVariable) parameter.getParameterAnnotation(MatrixVariable.class);
        Assert.state(ann != null, "No MatrixVariable annotation");
        String pathVariable = ann.pathVar();
        if (!pathVariable.equals(ValueConstants.DEFAULT_NONE)) {
            MultiValueMap<String, String> mapForPathVariable = matrixVariables.get(pathVariable);
            if (mapForPathVariable == null) {
                return Collections.emptyMap();
            }
            map.putAll(mapForPathVariable);
        } else {
            for (MultiValueMap<String, String> vars : matrixVariables.values()) {
                vars.forEach(name, values -> {
                    Iterator it = values.iterator();
                    while (it.hasNext()) {
                        String value = (String) it.next();
                        map.add(name, value);
                    }
                });
            }
        }
        return isSingleValueMap(parameter) ? map.toSingleValueMap() : map;
    }

    private boolean isSingleValueMap(MethodParameter parameter) {
        if (!MultiValueMap.class.isAssignableFrom(parameter.getParameterType())) {
            ResolvableType[] genericTypes = ResolvableType.forMethodParameter(parameter).getGenerics();
            if (genericTypes.length == 2) {
                Class<?> declaredClass = genericTypes[1].getRawClass();
                return declaredClass == null || !List.class.isAssignableFrom(declaredClass);
            }
            return false;
        }
        return false;
    }
}