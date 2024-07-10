package org.springframework.web.servlet.mvc.condition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.mvc.condition.HeadersRequestCondition;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/condition/ProducesRequestCondition.class */
public final class ProducesRequestCondition extends AbstractRequestCondition<ProducesRequestCondition> {
    private static final ProducesRequestCondition PRE_FLIGHT_MATCH = new ProducesRequestCondition(new String[0]);
    private static final ProducesRequestCondition EMPTY_CONDITION = new ProducesRequestCondition(new String[0]);
    private static final List<ProduceMediaTypeExpression> MEDIA_TYPE_ALL_LIST = Collections.singletonList(new ProduceMediaTypeExpression("*/*"));
    private final List<ProduceMediaTypeExpression> expressions;
    private final ContentNegotiationManager contentNegotiationManager;

    public ProducesRequestCondition(String... produces) {
        this(produces, null, null);
    }

    public ProducesRequestCondition(String[] produces, @Nullable String[] headers) {
        this(produces, headers, null);
    }

    public ProducesRequestCondition(String[] produces, @Nullable String[] headers, @Nullable ContentNegotiationManager manager) {
        this.expressions = new ArrayList(parseExpressions(produces, headers));
        Collections.sort(this.expressions);
        this.contentNegotiationManager = manager != null ? manager : new ContentNegotiationManager();
    }

    private ProducesRequestCondition(Collection<ProduceMediaTypeExpression> expressions, @Nullable ContentNegotiationManager manager) {
        this.expressions = new ArrayList(expressions);
        Collections.sort(this.expressions);
        this.contentNegotiationManager = manager != null ? manager : new ContentNegotiationManager();
    }

    private Set<ProduceMediaTypeExpression> parseExpressions(String[] produces, @Nullable String[] headers) {
        Set<ProduceMediaTypeExpression> result = new LinkedHashSet<>();
        if (headers != null) {
            for (String header : headers) {
                HeadersRequestCondition.HeaderExpression expr = new HeadersRequestCondition.HeaderExpression(header);
                if (HttpHeaders.ACCEPT.equalsIgnoreCase(expr.name) && expr.value != 0) {
                    for (MediaType mediaType : MediaType.parseMediaTypes((String) expr.value)) {
                        result.add(new ProduceMediaTypeExpression(mediaType, expr.isNegated));
                    }
                }
            }
        }
        for (String produce : produces) {
            result.add(new ProduceMediaTypeExpression(produce));
        }
        return result;
    }

    public Set<MediaTypeExpression> getExpressions() {
        return new LinkedHashSet(this.expressions);
    }

    public Set<MediaType> getProducibleMediaTypes() {
        Set<MediaType> result = new LinkedHashSet<>();
        for (ProduceMediaTypeExpression expression : this.expressions) {
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
    public List<ProduceMediaTypeExpression> getContent() {
        return this.expressions;
    }

    @Override // org.springframework.web.servlet.mvc.condition.AbstractRequestCondition
    protected String getToStringInfix() {
        return " || ";
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    public ProducesRequestCondition combine(ProducesRequestCondition other) {
        return !other.expressions.isEmpty() ? other : this;
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    @Nullable
    public ProducesRequestCondition getMatchingCondition(HttpServletRequest request) {
        if (CorsUtils.isPreFlightRequest(request)) {
            return PRE_FLIGHT_MATCH;
        }
        if (isEmpty()) {
            return this;
        }
        try {
            List<MediaType> acceptedMediaTypes = getAcceptedMediaTypes(request);
            Set<ProduceMediaTypeExpression> result = new LinkedHashSet<>(this.expressions);
            result.removeIf(expression -> {
                return !expression.match(acceptedMediaTypes);
            });
            if (!result.isEmpty()) {
                return new ProducesRequestCondition(result, this.contentNegotiationManager);
            }
            if (acceptedMediaTypes.contains(MediaType.ALL)) {
                return EMPTY_CONDITION;
            }
            return null;
        } catch (HttpMediaTypeException e) {
            return null;
        }
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    public int compareTo(ProducesRequestCondition other, HttpServletRequest request) {
        try {
            List<MediaType> acceptedMediaTypes = getAcceptedMediaTypes(request);
            for (MediaType acceptedMediaType : acceptedMediaTypes) {
                int thisIndex = indexOfEqualMediaType(acceptedMediaType);
                int otherIndex = other.indexOfEqualMediaType(acceptedMediaType);
                int result = compareMatchingMediaTypes(this, thisIndex, other, otherIndex);
                if (result != 0) {
                    return result;
                }
                int thisIndex2 = indexOfIncludedMediaType(acceptedMediaType);
                int otherIndex2 = other.indexOfIncludedMediaType(acceptedMediaType);
                int result2 = compareMatchingMediaTypes(this, thisIndex2, other, otherIndex2);
                if (result2 != 0) {
                    return result2;
                }
            }
            return 0;
        } catch (HttpMediaTypeNotAcceptableException ex) {
            throw new IllegalStateException("Cannot compare without having any requested media types", ex);
        }
    }

    private List<MediaType> getAcceptedMediaTypes(HttpServletRequest request) throws HttpMediaTypeNotAcceptableException {
        return this.contentNegotiationManager.resolveMediaTypes(new ServletWebRequest(request));
    }

    private int indexOfEqualMediaType(MediaType mediaType) {
        for (int i = 0; i < getExpressionsToCompare().size(); i++) {
            MediaType currentMediaType = getExpressionsToCompare().get(i).getMediaType();
            if (mediaType.getType().equalsIgnoreCase(currentMediaType.getType()) && mediaType.getSubtype().equalsIgnoreCase(currentMediaType.getSubtype())) {
                return i;
            }
        }
        return -1;
    }

    private int indexOfIncludedMediaType(MediaType mediaType) {
        for (int i = 0; i < getExpressionsToCompare().size(); i++) {
            if (mediaType.includes(getExpressionsToCompare().get(i).getMediaType())) {
                return i;
            }
        }
        return -1;
    }

    private int compareMatchingMediaTypes(ProducesRequestCondition condition1, int index1, ProducesRequestCondition condition2, int index2) {
        int result = 0;
        if (index1 != index2) {
            result = index2 - index1;
        } else if (index1 != -1) {
            ProduceMediaTypeExpression expr1 = condition1.getExpressionsToCompare().get(index1);
            ProduceMediaTypeExpression expr2 = condition2.getExpressionsToCompare().get(index2);
            int result2 = expr1.compareTo((AbstractMediaTypeExpression) expr2);
            result = result2 != 0 ? result2 : expr1.getMediaType().compareTo((MimeType) expr2.getMediaType());
        }
        return result;
    }

    private List<ProduceMediaTypeExpression> getExpressionsToCompare() {
        return this.expressions.isEmpty() ? MEDIA_TYPE_ALL_LIST : this.expressions;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/condition/ProducesRequestCondition$ProduceMediaTypeExpression.class */
    public static class ProduceMediaTypeExpression extends AbstractMediaTypeExpression {
        ProduceMediaTypeExpression(MediaType mediaType, boolean negated) {
            super(mediaType, negated);
        }

        ProduceMediaTypeExpression(String expression) {
            super(expression);
        }

        public final boolean match(List<MediaType> acceptedMediaTypes) {
            boolean match = matchMediaType(acceptedMediaTypes);
            return !isNegated() ? match : !match;
        }

        private boolean matchMediaType(List<MediaType> acceptedMediaTypes) {
            for (MediaType acceptedMediaType : acceptedMediaTypes) {
                if (getMediaType().isCompatibleWith(acceptedMediaType)) {
                    return true;
                }
            }
            return false;
        }
    }
}