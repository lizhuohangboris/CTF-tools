package org.springframework.aop.aspectj;

import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.TypePatternMatcher;
import org.springframework.aop.ClassFilter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/aspectj/TypePatternClassFilter.class */
public class TypePatternClassFilter implements ClassFilter {
    private String typePattern = "";
    @Nullable
    private TypePatternMatcher aspectJTypePatternMatcher;

    public TypePatternClassFilter() {
    }

    public TypePatternClassFilter(String typePattern) {
        setTypePattern(typePattern);
    }

    public void setTypePattern(String typePattern) {
        Assert.notNull(typePattern, "Type pattern must not be null");
        this.typePattern = typePattern;
        this.aspectJTypePatternMatcher = PointcutParser.getPointcutParserSupportingAllPrimitivesAndUsingContextClassloaderForResolution().parseTypePattern(replaceBooleanOperators(typePattern));
    }

    public String getTypePattern() {
        return this.typePattern;
    }

    @Override // org.springframework.aop.ClassFilter
    public boolean matches(Class<?> clazz) {
        Assert.state(this.aspectJTypePatternMatcher != null, "No type pattern has been set");
        return this.aspectJTypePatternMatcher.matches(clazz);
    }

    private String replaceBooleanOperators(String pcExpr) {
        String result = StringUtils.replace(pcExpr, " and ", " && ");
        return StringUtils.replace(StringUtils.replace(result, " or ", " || "), " not ", " ! ");
    }
}