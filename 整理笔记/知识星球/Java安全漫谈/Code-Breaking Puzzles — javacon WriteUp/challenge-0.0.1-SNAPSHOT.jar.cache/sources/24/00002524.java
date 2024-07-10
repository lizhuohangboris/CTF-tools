package org.springframework.web.method.support;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.lang.Nullable;
import org.springframework.web.util.UriComponentsBuilder;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/method/support/CompositeUriComponentsContributor.class */
public class CompositeUriComponentsContributor implements UriComponentsContributor {
    private final List<Object> contributors;
    private final ConversionService conversionService;

    public CompositeUriComponentsContributor(UriComponentsContributor... contributors) {
        this.contributors = new LinkedList();
        Collections.addAll(this.contributors, contributors);
        this.conversionService = new DefaultFormattingConversionService();
    }

    public CompositeUriComponentsContributor(Collection<?> contributors) {
        this(contributors, null);
    }

    public CompositeUriComponentsContributor(@Nullable Collection<?> contributors, @Nullable ConversionService cs) {
        this.contributors = new LinkedList();
        if (contributors != null) {
            this.contributors.addAll(contributors);
        }
        this.conversionService = cs != null ? cs : new DefaultFormattingConversionService();
    }

    public boolean hasContributors() {
        return this.contributors.isEmpty();
    }

    @Override // org.springframework.web.method.support.UriComponentsContributor
    public boolean supportsParameter(MethodParameter parameter) {
        for (Object contributor : this.contributors) {
            if (contributor instanceof UriComponentsContributor) {
                if (((UriComponentsContributor) contributor).supportsParameter(parameter)) {
                    return true;
                }
            } else if ((contributor instanceof HandlerMethodArgumentResolver) && ((HandlerMethodArgumentResolver) contributor).supportsParameter(parameter)) {
                return false;
            }
        }
        return false;
    }

    @Override // org.springframework.web.method.support.UriComponentsContributor
    public void contributeMethodArgument(MethodParameter parameter, Object value, UriComponentsBuilder builder, Map<String, Object> uriVariables, ConversionService conversionService) {
        for (Object contributor : this.contributors) {
            if (contributor instanceof UriComponentsContributor) {
                UriComponentsContributor ucc = (UriComponentsContributor) contributor;
                if (ucc.supportsParameter(parameter)) {
                    ucc.contributeMethodArgument(parameter, value, builder, uriVariables, conversionService);
                    return;
                }
            } else if ((contributor instanceof HandlerMethodArgumentResolver) && ((HandlerMethodArgumentResolver) contributor).supportsParameter(parameter)) {
                return;
            }
        }
    }

    public void contributeMethodArgument(MethodParameter parameter, Object value, UriComponentsBuilder builder, Map<String, Object> uriVariables) {
        contributeMethodArgument(parameter, value, builder, uriVariables, this.conversionService);
    }
}