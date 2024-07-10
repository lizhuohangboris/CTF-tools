package org.springframework.expression.spel.ast;

import java.util.List;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/ast/FormatHelper.class */
abstract class FormatHelper {
    FormatHelper() {
    }

    public static String formatMethodForMessage(String name, List<TypeDescriptor> argumentTypes) {
        StringBuilder sb = new StringBuilder(name);
        sb.append("(");
        for (int i = 0; i < argumentTypes.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            TypeDescriptor typeDescriptor = argumentTypes.get(i);
            if (typeDescriptor != null) {
                sb.append(formatClassNameForMessage(typeDescriptor.getType()));
            } else {
                sb.append(formatClassNameForMessage(null));
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public static String formatClassNameForMessage(@Nullable Class<?> clazz) {
        return clazz != null ? ClassUtils.getQualifiedName(clazz) : BeanDefinitionParserDelegate.NULL_ELEMENT;
    }
}