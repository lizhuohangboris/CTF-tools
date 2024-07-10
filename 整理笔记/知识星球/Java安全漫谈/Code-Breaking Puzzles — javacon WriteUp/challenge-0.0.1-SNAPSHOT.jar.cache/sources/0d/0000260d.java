package org.springframework.web.servlet.mvc.condition;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.cors.CorsUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/condition/RequestMethodsRequestCondition.class */
public final class RequestMethodsRequestCondition extends AbstractRequestCondition<RequestMethodsRequestCondition> {
    private static final RequestMethodsRequestCondition GET_CONDITION = new RequestMethodsRequestCondition(RequestMethod.GET);
    private final Set<RequestMethod> methods;

    public RequestMethodsRequestCondition(RequestMethod... requestMethods) {
        this(Arrays.asList(requestMethods));
    }

    private RequestMethodsRequestCondition(Collection<RequestMethod> requestMethods) {
        this.methods = Collections.unmodifiableSet(new LinkedHashSet(requestMethods));
    }

    public Set<RequestMethod> getMethods() {
        return this.methods;
    }

    @Override // org.springframework.web.servlet.mvc.condition.AbstractRequestCondition
    protected Collection<RequestMethod> getContent() {
        return this.methods;
    }

    @Override // org.springframework.web.servlet.mvc.condition.AbstractRequestCondition
    protected String getToStringInfix() {
        return " || ";
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    public RequestMethodsRequestCondition combine(RequestMethodsRequestCondition other) {
        Set<RequestMethod> set = new LinkedHashSet<>(this.methods);
        set.addAll(other.methods);
        return new RequestMethodsRequestCondition(set);
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    @Nullable
    public RequestMethodsRequestCondition getMatchingCondition(HttpServletRequest request) {
        if (CorsUtils.isPreFlightRequest(request)) {
            return matchPreFlight(request);
        }
        if (getMethods().isEmpty()) {
            if (RequestMethod.OPTIONS.name().equals(request.getMethod()) && !DispatcherType.ERROR.equals(request.getDispatcherType())) {
                return null;
            }
            return this;
        }
        return matchRequestMethod(request.getMethod());
    }

    @Nullable
    private RequestMethodsRequestCondition matchPreFlight(HttpServletRequest request) {
        if (getMethods().isEmpty()) {
            return this;
        }
        String expectedMethod = request.getHeader("Access-Control-Request-Method");
        return matchRequestMethod(expectedMethod);
    }

    @Nullable
    private RequestMethodsRequestCondition matchRequestMethod(String httpMethodValue) {
        HttpMethod httpMethod = HttpMethod.resolve(httpMethodValue);
        if (httpMethod != null) {
            for (RequestMethod method : getMethods()) {
                if (httpMethod.matches(method.name())) {
                    return new RequestMethodsRequestCondition(method);
                }
            }
            if (httpMethod == HttpMethod.HEAD && getMethods().contains(RequestMethod.GET)) {
                return GET_CONDITION;
            }
            return null;
        }
        return null;
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    public int compareTo(RequestMethodsRequestCondition other, HttpServletRequest request) {
        if (other.methods.size() != this.methods.size()) {
            return other.methods.size() - this.methods.size();
        }
        if (this.methods.size() == 1) {
            if (this.methods.contains(RequestMethod.HEAD) && other.methods.contains(RequestMethod.GET)) {
                return -1;
            }
            if (this.methods.contains(RequestMethod.GET) && other.methods.contains(RequestMethod.HEAD)) {
                return 1;
            }
            return 0;
        }
        return 0;
    }
}