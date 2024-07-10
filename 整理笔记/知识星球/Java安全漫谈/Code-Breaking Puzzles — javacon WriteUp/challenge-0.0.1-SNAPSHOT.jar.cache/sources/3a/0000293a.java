package org.thymeleaf.standard.expression;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ognl.ArrayPropertyAccessor;
import ognl.EnumerationPropertyAccessor;
import ognl.IteratorPropertyAccessor;
import ognl.ListPropertyAccessor;
import ognl.MapPropertyAccessor;
import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;
import ognl.SetPropertyAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.web.servlet.tags.form.InputTag;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.cache.ExpressionCacheKey;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.context.IContext;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/OGNLShortcutExpression.class */
final class OGNLShortcutExpression {
    private static final String EXPRESSION_CACHE_TYPE_OGNL_SHORTCUT = "ognlsc";
    private final String[] expressionLevels;
    private static final Logger LOGGER = LoggerFactory.getLogger(OGNLShortcutExpression.class);
    private static final Object[] NO_PARAMS = new Object[0];

    /* JADX INFO: Access modifiers changed from: package-private */
    public OGNLShortcutExpression(String[] expressionLevels) {
        this.expressionLevels = expressionLevels;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Object evaluate(IEngineConfiguration configuration, Map<String, Object> context, Object root) throws Exception {
        String[] strArr;
        Object arrayProperty;
        ICacheManager cacheManager = configuration.getCacheManager();
        ICache<ExpressionCacheKey, Object> expressionCache = cacheManager == null ? null : cacheManager.getExpressionCache();
        Object target = root;
        for (String propertyName : this.expressionLevels) {
            if (target == null) {
                throw new OgnlException("source is null for getProperty(null, \"" + propertyName + "\")");
            }
            Class<?> targetClass = OgnlRuntime.getTargetClass(target);
            PropertyAccessor ognlPropertyAccessor = OgnlRuntime.getPropertyAccessor(targetClass);
            if (target instanceof Class) {
                arrayProperty = getObjectProperty(expressionCache, propertyName, target);
            } else if (OGNLContextPropertyAccessor.class.equals(ognlPropertyAccessor.getClass())) {
                arrayProperty = getContextProperty(propertyName, context, target);
            } else if (ObjectPropertyAccessor.class.equals(ognlPropertyAccessor.getClass())) {
                arrayProperty = getObjectProperty(expressionCache, propertyName, target);
            } else if (MapPropertyAccessor.class.equals(ognlPropertyAccessor.getClass())) {
                arrayProperty = getMapProperty(propertyName, (Map) target);
            } else if (ListPropertyAccessor.class.equals(ognlPropertyAccessor.getClass())) {
                arrayProperty = getListProperty(expressionCache, propertyName, (List) target);
            } else if (SetPropertyAccessor.class.equals(ognlPropertyAccessor.getClass())) {
                arrayProperty = getSetProperty(expressionCache, propertyName, (Set) target);
            } else if (IteratorPropertyAccessor.class.equals(ognlPropertyAccessor.getClass())) {
                arrayProperty = getIteratorProperty(expressionCache, propertyName, (Iterator) target);
            } else if (EnumerationPropertyAccessor.class.equals(ognlPropertyAccessor.getClass())) {
                arrayProperty = getEnumerationProperty(expressionCache, propertyName, (Enumeration) target);
            } else if (ArrayPropertyAccessor.class.equals(ognlPropertyAccessor.getClass())) {
                arrayProperty = getArrayProperty(expressionCache, propertyName, (Object[]) target);
            } else {
                throw new OGNLShortcutExpressionNotApplicableException();
            }
            target = arrayProperty;
        }
        return target;
    }

    private static Object getContextProperty(String propertyName, Map<String, Object> context, Object target) throws OgnlException {
        Object execInfoResult;
        if ("param".equals(propertyName) && context != null && context.containsKey(OGNLContextPropertyAccessor.RESTRICT_REQUEST_PARAMETERS)) {
            throw new OgnlException("Access to variable \"" + propertyName + "\" is forbidden in this context. Note some restrictions apply to variable access. For example, accessing request parameters is forbidden in preprocessing and unescaped expressions, and also in fragment inclusion specifications.");
        }
        if (StandardExpressionObjectFactory.EXECUTION_INFO_OBJECT_NAME.equals(propertyName) && (execInfoResult = checkExecInfo(propertyName, context)) != null) {
            return execInfoResult;
        }
        return ((IContext) target).getVariable(propertyName);
    }

    @Deprecated
    private static Object checkExecInfo(String propertyName, Map<String, Object> context) {
        if (StandardExpressionObjectFactory.EXECUTION_INFO_OBJECT_NAME.equals(propertyName)) {
            LOGGER.warn("[THYMELEAF][{}] Found Thymeleaf Standard Expression containing a call to the context variable \"execInfo\" (e.g. \"${execInfo.templateName}\"), which has been deprecated. The Execution Info should be now accessed as an expression object instead (e.g. \"${#execInfo.templateName}\"). Deprecated use is still allowed, but will be removed in future versions of Thymeleaf.", TemplateEngine.threadIndex());
            return context.get(StandardExpressionObjectFactory.EXECUTION_INFO_OBJECT_NAME);
        }
        return null;
    }

    private static Object getObjectProperty(ICache<ExpressionCacheKey, Object> expressionCache, String propertyName, Object target) {
        Class<?> currClass = OgnlRuntime.getTargetClass(target);
        ExpressionCacheKey cacheKey = computeMethodCacheKey(currClass, propertyName);
        Method readMethod = null;
        if (expressionCache != null) {
            readMethod = (Method) expressionCache.get(cacheKey);
        }
        if (readMethod == null) {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(currClass);
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                if (propertyDescriptors != null) {
                    int length = propertyDescriptors.length;
                    int i = 0;
                    while (true) {
                        if (i >= length) {
                            break;
                        }
                        PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
                        if (!propertyDescriptor.getName().equals(propertyName)) {
                            i++;
                        } else {
                            readMethod = propertyDescriptor.getReadMethod();
                            if (readMethod != null && expressionCache != null) {
                                expressionCache.put(cacheKey, readMethod);
                            }
                        }
                    }
                }
            } catch (IntrospectionException e) {
                throw new OGNLShortcutExpressionNotApplicableException();
            }
        }
        if (readMethod == null) {
            throw new OGNLShortcutExpressionNotApplicableException();
        }
        try {
            return readMethod.invoke(target, NO_PARAMS);
        } catch (IllegalAccessException e2) {
            throw new OGNLShortcutExpressionNotApplicableException();
        } catch (InvocationTargetException e3) {
            throw new OGNLShortcutExpressionNotApplicableException();
        }
    }

    private static Object getMapProperty(String propertyName, Map<?, ?> map) {
        if (propertyName.equals(InputTag.SIZE_ATTRIBUTE)) {
            return Integer.valueOf(map.size());
        }
        if (propertyName.equals("keys") || propertyName.equals("keySet")) {
            return map.keySet();
        }
        if (propertyName.equals("values")) {
            return map.values();
        }
        if (propertyName.equals("isEmpty")) {
            return map.isEmpty() ? Boolean.TRUE : Boolean.FALSE;
        }
        return map.get(propertyName);
    }

    public static Object getListProperty(ICache<ExpressionCacheKey, Object> expressionCache, String propertyName, List<?> list) {
        if (propertyName.equals(InputTag.SIZE_ATTRIBUTE)) {
            return Integer.valueOf(list.size());
        }
        if (propertyName.equals("iterator")) {
            return list.iterator();
        }
        if (propertyName.equals("isEmpty") || propertyName.equals("empty")) {
            return list.isEmpty() ? Boolean.TRUE : Boolean.FALSE;
        }
        return getObjectProperty(expressionCache, propertyName, list);
    }

    public static Object getArrayProperty(ICache<ExpressionCacheKey, Object> expressionCache, String propertyName, Object[] array) {
        if (propertyName.equals("length")) {
            return Integer.valueOf(Array.getLength(array));
        }
        return getObjectProperty(expressionCache, propertyName, array);
    }

    public static Object getEnumerationProperty(ICache<ExpressionCacheKey, Object> expressionCache, String propertyName, Enumeration enumeration) {
        if (propertyName.equals("next") || propertyName.equals("nextElement")) {
            return enumeration.nextElement();
        }
        if (propertyName.equals("hasNext") || propertyName.equals("hasMoreElements")) {
            return enumeration.hasMoreElements() ? Boolean.TRUE : Boolean.FALSE;
        }
        return getObjectProperty(expressionCache, propertyName, enumeration);
    }

    public static Object getIteratorProperty(ICache<ExpressionCacheKey, Object> expressionCache, String propertyName, Iterator<?> iterator) {
        if (propertyName.equals("next")) {
            return iterator.next();
        }
        if (propertyName.equals("hasNext")) {
            return iterator.hasNext() ? Boolean.TRUE : Boolean.FALSE;
        }
        return getObjectProperty(expressionCache, propertyName, iterator);
    }

    public static Object getSetProperty(ICache<ExpressionCacheKey, Object> expressionCache, String propertyName, Set<?> set) {
        if (propertyName.equals(InputTag.SIZE_ATTRIBUTE)) {
            return Integer.valueOf(set.size());
        }
        if (propertyName.equals("iterator")) {
            return set.iterator();
        }
        if (propertyName.equals("isEmpty")) {
            return set.isEmpty() ? Boolean.TRUE : Boolean.FALSE;
        }
        return getObjectProperty(expressionCache, propertyName, set);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String[] parse(String expression) {
        return doParseExpr(expression, 0, 0, expression.length());
    }

    private static String[] doParseExpr(String expression, int level, int offset, int len) {
        String[] result;
        int codepoint;
        int i = offset;
        boolean firstChar = true;
        while (i < len && (codepoint = Character.codePointAt(expression, i)) != 46) {
            if (firstChar) {
                if (!Character.isJavaIdentifierStart(codepoint)) {
                    return null;
                }
                firstChar = false;
            } else if (!Character.isJavaIdentifierPart(codepoint)) {
                return null;
            }
            i++;
        }
        if (i < len) {
            result = doParseExpr(expression, level + 1, i + 1, len);
            if (result == null) {
                return null;
            }
        } else {
            result = new String[level + 1];
        }
        result[level] = expression.substring(offset, i);
        if ("true".equalsIgnoreCase(result[level]) || "false".equalsIgnoreCase(result[level]) || BeanDefinitionParserDelegate.NULL_ELEMENT.equalsIgnoreCase(result[level])) {
            return null;
        }
        return result;
    }

    private static ExpressionCacheKey computeMethodCacheKey(Class<?> targetClass, String propertyName) {
        return new ExpressionCacheKey(EXPRESSION_CACHE_TYPE_OGNL_SHORTCUT, targetClass.getName(), propertyName);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/OGNLShortcutExpression$OGNLShortcutExpressionNotApplicableException.class */
    public static class OGNLShortcutExpressionNotApplicableException extends RuntimeException {
        OGNLShortcutExpressionNotApplicableException() {
        }
    }
}