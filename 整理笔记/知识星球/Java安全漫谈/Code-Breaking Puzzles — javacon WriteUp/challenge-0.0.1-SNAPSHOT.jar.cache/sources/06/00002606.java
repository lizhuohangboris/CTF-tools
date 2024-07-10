package org.springframework.web.servlet.mvc.condition;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.WebUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/condition/ParamsRequestCondition.class */
public final class ParamsRequestCondition extends AbstractRequestCondition<ParamsRequestCondition> {
    private final Set<ParamExpression> expressions;

    public ParamsRequestCondition(String... params) {
        this(parseExpressions(params));
    }

    private ParamsRequestCondition(Collection<ParamExpression> conditions) {
        this.expressions = Collections.unmodifiableSet(new LinkedHashSet(conditions));
    }

    private static Collection<ParamExpression> parseExpressions(String... params) {
        Set<ParamExpression> expressions = new LinkedHashSet<>();
        for (String param : params) {
            expressions.add(new ParamExpression(param));
        }
        return expressions;
    }

    public Set<NameValueExpression<String>> getExpressions() {
        return new LinkedHashSet(this.expressions);
    }

    @Override // org.springframework.web.servlet.mvc.condition.AbstractRequestCondition
    protected Collection<ParamExpression> getContent() {
        return this.expressions;
    }

    @Override // org.springframework.web.servlet.mvc.condition.AbstractRequestCondition
    protected String getToStringInfix() {
        return " && ";
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    public ParamsRequestCondition combine(ParamsRequestCondition other) {
        Set<ParamExpression> set = new LinkedHashSet<>(this.expressions);
        set.addAll(other.expressions);
        return new ParamsRequestCondition(set);
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    @Nullable
    public ParamsRequestCondition getMatchingCondition(HttpServletRequest request) {
        for (ParamExpression expression : this.expressions) {
            if (!expression.match(request)) {
                return null;
            }
        }
        return this;
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    public int compareTo(ParamsRequestCondition other, HttpServletRequest request) {
        int result = other.expressions.size() - this.expressions.size();
        if (result != 0) {
            return result;
        }
        return (int) (getValueMatchCount(other.expressions) - getValueMatchCount(this.expressions));
    }

    private long getValueMatchCount(Set<ParamExpression> expressions) {
        return expressions.stream().filter(e -> {
            return (e.getValue() == null || e.isNegated()) ? false : true;
        }).count();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/condition/ParamsRequestCondition$ParamExpression.class */
    public static class ParamExpression extends AbstractNameValueExpression<String> {
        ParamExpression(String expression) {
            super(expression);
        }

        @Override // org.springframework.web.servlet.mvc.condition.AbstractNameValueExpression
        protected boolean isCaseSensitiveName() {
            return true;
        }

        @Override // org.springframework.web.servlet.mvc.condition.AbstractNameValueExpression
        public String parseValue(String valueExpression) {
            return valueExpression;
        }

        @Override // org.springframework.web.servlet.mvc.condition.AbstractNameValueExpression
        protected boolean matchName(HttpServletRequest request) {
            return WebUtils.hasSubmitParameter(request, this.name) || request.getParameterMap().containsKey(this.name);
        }

        @Override // org.springframework.web.servlet.mvc.condition.AbstractNameValueExpression
        protected boolean matchValue(HttpServletRequest request) {
            return ObjectUtils.nullSafeEquals(this.value, request.getParameter(this.name));
        }
    }
}