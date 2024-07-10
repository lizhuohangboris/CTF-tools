package org.thymeleaf.standard.serializer;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.exceptions.TemplateProcessingException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/serializer/StandardSerializers.class */
public final class StandardSerializers {
    public static final String STANDARD_JAVASCRIPT_SERIALIZER_ATTRIBUTE_NAME = "StandardJavaScriptSerializer";
    public static final String STANDARD_CSS_SERIALIZER_ATTRIBUTE_NAME = "StandardCSSSerializer";

    private StandardSerializers() {
    }

    public static IStandardJavaScriptSerializer getJavaScriptSerializer(IEngineConfiguration configuration) {
        Object serializer = configuration.getExecutionAttributes().get(STANDARD_JAVASCRIPT_SERIALIZER_ATTRIBUTE_NAME);
        if (serializer == null || !(serializer instanceof IStandardJavaScriptSerializer)) {
            throw new TemplateProcessingException("No JavaScript Serializer has been registered as an execution argument. This is a requirement for using Standard serialization, and might happen if neither the Standard or the SpringStandard dialects have been added to the Template Engine and none of the specified dialects registers an attribute of type " + IStandardJavaScriptSerializer.class.getName() + " with name \"" + STANDARD_JAVASCRIPT_SERIALIZER_ATTRIBUTE_NAME + "\"");
        }
        return (IStandardJavaScriptSerializer) serializer;
    }

    public static IStandardCSSSerializer getCSSSerializer(IEngineConfiguration configuration) {
        Object serializer = configuration.getExecutionAttributes().get(STANDARD_CSS_SERIALIZER_ATTRIBUTE_NAME);
        if (serializer == null || !(serializer instanceof IStandardCSSSerializer)) {
            throw new TemplateProcessingException("No CSS Serializer has been registered as an execution argument. This is a requirement for using Standard serialization, and might happen if neither the Standard or the SpringStandard dialects have been added to the Template Engine and none of the specified dialects registers an attribute of type " + IStandardCSSSerializer.class.getName() + " with name \"" + STANDARD_CSS_SERIALIZER_ATTRIBUTE_NAME + "\"");
        }
        return (IStandardCSSSerializer) serializer;
    }
}