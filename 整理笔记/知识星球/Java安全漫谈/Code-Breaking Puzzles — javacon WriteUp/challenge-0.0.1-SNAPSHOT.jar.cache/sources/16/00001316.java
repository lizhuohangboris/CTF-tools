package org.springframework.aop.support;

import java.io.Serializable;
import org.springframework.aop.ClassFilter;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/support/ClassFilters.class */
public abstract class ClassFilters {
    public static ClassFilter union(ClassFilter cf1, ClassFilter cf2) {
        Assert.notNull(cf1, "First ClassFilter must not be null");
        Assert.notNull(cf2, "Second ClassFilter must not be null");
        return new UnionClassFilter(new ClassFilter[]{cf1, cf2});
    }

    public static ClassFilter union(ClassFilter[] classFilters) {
        Assert.notEmpty(classFilters, "ClassFilter array must not be empty");
        return new UnionClassFilter(classFilters);
    }

    public static ClassFilter intersection(ClassFilter cf1, ClassFilter cf2) {
        Assert.notNull(cf1, "First ClassFilter must not be null");
        Assert.notNull(cf2, "Second ClassFilter must not be null");
        return new IntersectionClassFilter(new ClassFilter[]{cf1, cf2});
    }

    public static ClassFilter intersection(ClassFilter[] classFilters) {
        Assert.notEmpty(classFilters, "ClassFilter array must not be empty");
        return new IntersectionClassFilter(classFilters);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/support/ClassFilters$UnionClassFilter.class */
    private static class UnionClassFilter implements ClassFilter, Serializable {
        private ClassFilter[] filters;

        public UnionClassFilter(ClassFilter[] filters) {
            this.filters = filters;
        }

        @Override // org.springframework.aop.ClassFilter
        public boolean matches(Class<?> clazz) {
            ClassFilter[] classFilterArr;
            for (ClassFilter filter : this.filters) {
                if (filter.matches(clazz)) {
                    return true;
                }
            }
            return false;
        }

        public boolean equals(Object other) {
            return this == other || ((other instanceof UnionClassFilter) && ObjectUtils.nullSafeEquals(this.filters, ((UnionClassFilter) other).filters));
        }

        public int hashCode() {
            return ObjectUtils.nullSafeHashCode((Object[]) this.filters);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-aop-5.1.2.RELEASE.jar:org/springframework/aop/support/ClassFilters$IntersectionClassFilter.class */
    private static class IntersectionClassFilter implements ClassFilter, Serializable {
        private ClassFilter[] filters;

        public IntersectionClassFilter(ClassFilter[] filters) {
            this.filters = filters;
        }

        @Override // org.springframework.aop.ClassFilter
        public boolean matches(Class<?> clazz) {
            ClassFilter[] classFilterArr;
            for (ClassFilter filter : this.filters) {
                if (!filter.matches(clazz)) {
                    return false;
                }
            }
            return true;
        }

        public boolean equals(Object other) {
            return this == other || ((other instanceof IntersectionClassFilter) && ObjectUtils.nullSafeEquals(this.filters, ((IntersectionClassFilter) other).filters));
        }

        public int hashCode() {
            return ObjectUtils.nullSafeHashCode((Object[]) this.filters);
        }
    }
}