package org.thymeleaf.spring5.expression;

import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.spring5.util.SpringVersionUtils;
import org.thymeleaf.util.ClassLoaderUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/expression/Mvc.class */
public class Mvc {
    private static final MvcUriComponentsBuilderDelegate mvcUriComponentsBuilderDelegate;
    private static final String SPRING41_MVC_URI_COMPONENTS_BUILDER_DELEGATE_CLASS_NAME = Mvc.class.getName() + "$Spring41MvcUriComponentsBuilderDelegate";
    private static final String NON_SPRING41_MVC_URI_COMPONENTS_BUILDER_DELEGATE_CLASS_NAME = Mvc.class.getName() + "$NonSpring41MvcUriComponentsBuilderDelegate";

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/expression/Mvc$MethodArgumentBuilderWrapper.class */
    public interface MethodArgumentBuilderWrapper {
        MethodArgumentBuilderWrapper arg(int i, Object obj);

        String build();

        String buildAndExpand(Object... objArr);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/expression/Mvc$MvcUriComponentsBuilderDelegate.class */
    interface MvcUriComponentsBuilderDelegate {
        MethodArgumentBuilderWrapper fromMappingName(String str);
    }

    static {
        String str;
        if (SpringVersionUtils.isSpring41AtLeast()) {
            str = SPRING41_MVC_URI_COMPONENTS_BUILDER_DELEGATE_CLASS_NAME;
        } else {
            str = NON_SPRING41_MVC_URI_COMPONENTS_BUILDER_DELEGATE_CLASS_NAME;
        }
        String delegateClassName = str;
        try {
            Class<?> implClass = ClassLoaderUtils.loadClass(delegateClassName);
            mvcUriComponentsBuilderDelegate = (MvcUriComponentsBuilderDelegate) implClass.newInstance();
        } catch (Exception e) {
            throw new ExceptionInInitializerError(new ConfigurationException("Thymeleaf could not initialize a delegate of class \"" + delegateClassName + "\" for taking care of the " + SpringStandardExpressionObjectFactory.MVC_EXPRESSION_OBJECT_NAME + " expression utility object", e));
        }
    }

    public MethodArgumentBuilderWrapper url(String mappingName) {
        return mvcUriComponentsBuilderDelegate.fromMappingName(mappingName);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/expression/Mvc$Spring41MvcUriComponentsBuilderDelegate.class */
    static class Spring41MvcUriComponentsBuilderDelegate implements MvcUriComponentsBuilderDelegate {
        Spring41MvcUriComponentsBuilderDelegate() {
        }

        @Override // org.thymeleaf.spring5.expression.Mvc.MvcUriComponentsBuilderDelegate
        public MethodArgumentBuilderWrapper fromMappingName(String mappingName) {
            return new Spring41MethodArgumentBuilderWrapper(MvcUriComponentsBuilder.fromMappingName(mappingName));
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/expression/Mvc$NonSpring41MvcUriComponentsBuilderDelegate.class */
    static class NonSpring41MvcUriComponentsBuilderDelegate implements MvcUriComponentsBuilderDelegate {
        NonSpring41MvcUriComponentsBuilderDelegate() {
        }

        @Override // org.thymeleaf.spring5.expression.Mvc.MvcUriComponentsBuilderDelegate
        public MethodArgumentBuilderWrapper fromMappingName(String mappingName) {
            throw new UnsupportedOperationException("MVC URI component building is only supported in Spring versions 4.1 or newer");
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-spring5-3.0.11.RELEASE.jar:org/thymeleaf/spring5/expression/Mvc$Spring41MethodArgumentBuilderWrapper.class */
    static class Spring41MethodArgumentBuilderWrapper implements MethodArgumentBuilderWrapper {
        private final MvcUriComponentsBuilder.MethodArgumentBuilder builder;

        private Spring41MethodArgumentBuilderWrapper(MvcUriComponentsBuilder.MethodArgumentBuilder builder) {
            this.builder = builder;
        }

        @Override // org.thymeleaf.spring5.expression.Mvc.MethodArgumentBuilderWrapper
        public MethodArgumentBuilderWrapper arg(int index, Object value) {
            return new Spring41MethodArgumentBuilderWrapper(this.builder.arg(index, value));
        }

        @Override // org.thymeleaf.spring5.expression.Mvc.MethodArgumentBuilderWrapper
        public String build() {
            return this.builder.build();
        }

        @Override // org.thymeleaf.spring5.expression.Mvc.MethodArgumentBuilderWrapper
        public String buildAndExpand(Object... uriVariables) {
            return this.builder.buildAndExpand(uriVariables);
        }
    }
}