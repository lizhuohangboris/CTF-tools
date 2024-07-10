package org.springframework.web.servlet.mvc.condition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/condition/ConsumesRequestCondition.class */
public final class ConsumesRequestCondition extends AbstractRequestCondition<ConsumesRequestCondition> {
    private static final ConsumesRequestCondition PRE_FLIGHT_MATCH = new ConsumesRequestCondition(new String[0]);
    private final List<ConsumeMediaTypeExpression> expressions;

    public ConsumesRequestCondition(String... consumes) {
        this(consumes, null);
    }

    public ConsumesRequestCondition(String[] consumes, @Nullable String[] headers) {
        this(parseExpressions(consumes, headers));
    }

    private ConsumesRequestCondition(Collection<ConsumeMediaTypeExpression> expressions) {
        this.expressions = new ArrayList(expressions);
        Collections.sort(this.expressions);
    }

    private static Set<ConsumeMediaTypeExpression> parseExpressions(String[] consumes, @Nullable String[] headers) {
        Set<ConsumeMediaTypeExpression> result = new LinkedHashSet<>();
        if (headers != null) {
            for (String header : headers) {
                HeadersRequestCondition.HeaderExpression expr = new HeadersRequestCondition.HeaderExpression(header);
                if (HttpHeaders.CONTENT_TYPE.equalsIgnoreCase(expr.name) && expr.value != 0) {
                    for (MediaType mediaType : MediaType.parseMediaTypes((String) expr.value)) {
                        result.add(new ConsumeMediaTypeExpression(mediaType, expr.isNegated));
                    }
                }
            }
        }
        for (String consume : consumes) {
            result.add(new ConsumeMediaTypeExpression(consume));
        }
        return result;
    }

    public Set<MediaTypeExpression> getExpressions() {
        return new LinkedHashSet(this.expressions);
    }

    public Set<MediaType> getConsumableMediaTypes() {
        Set<MediaType> result = new LinkedHashSet<>();
        for (ConsumeMediaTypeExpression expression : this.expressions) {
            if (!expression.isNegated()) {
                result.add(expression.getMediaType());
            }
        }
        return result;
    }

    @Override // org.springframework.web.servlet.mvc.condition.AbstractRequestCondition
    public boolean isEmpty() {
        return this.expressions.isEmpty();
    }

    @Override // org.springframework.web.servlet.mvc.condition.AbstractRequestCondition
    protected Collection<ConsumeMediaTypeExpression> getContent() {
        return this.expressions;
    }

    @Override // org.springframework.web.servlet.mvc.condition.AbstractRequestCondition
    protected String getToStringInfix() {
        return " || ";
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    public ConsumesRequestCondition combine(ConsumesRequestCondition other) {
        return !other.expressions.isEmpty() ? other : this;
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    @Nullable
    public ConsumesRequestCondition getMatchingCondition(HttpServletRequest request) {
        if (CorsUtils.isPreFlightRequest(request)) {
            return PRE_FLIGHT_MATCH;
        }
        if (isEmpty()) {
            return this;
        }
        try {
            MediaType contentType = StringUtils.hasLength(request.getContentType()) ? MediaType.parseMediaType(request.getContentType()) : MediaType.APPLICATION_OCTET_STREAM;
            Set<ConsumeMediaTypeExpression> result = new LinkedHashSet<>(this.expressions);
            result.removeIf(expression -> {
                return !expression.match(contentType);
            });
            if (result.isEmpty()) {
                return null;
            }
            return new ConsumesRequestCondition(result);
        } catch (InvalidMediaTypeException e) {
            return null;
        }
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    public int compareTo(ConsumesRequestCondition other, HttpServletRequest request) {
        if (this.expressions.isEmpty() && other.expressions.isEmpty()) {
            return 0;
        }
        if (this.expressions.isEmpty()) {
            return 1;
        }
        if (other.expressions.isEmpty()) {
            return -1;
        }
        return this.expressions.get(0).compareTo((AbstractMediaTypeExpression) other.expressions.get(0));
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/condition/ConsumesRequestCondition$ConsumeMediaTypeExpression.class */
    public static class ConsumeMediaTypeExpression extends AbstractMediaTypeExpression {
        ConsumeMediaTypeExpression(String expression) {
            super(expression);
        }

        ConsumeMediaTypeExpression(MediaType mediaType, boolean negated) {
            super(mediaType, negated);
        }

        public final boolean match(MediaType contentType) {
            boolean match = getMediaType().includes(contentType);
            return !isNegated() ? match : !match;
        }
    }
}