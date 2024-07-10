package org.springframework.core.type.filter;

import java.util.regex.Pattern;
import org.springframework.core.type.ClassMetadata;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/type/filter/RegexPatternTypeFilter.class */
public class RegexPatternTypeFilter extends AbstractClassTestingTypeFilter {
    private final Pattern pattern;

    public RegexPatternTypeFilter(Pattern pattern) {
        Assert.notNull(pattern, "Pattern must not be null");
        this.pattern = pattern;
    }

    @Override // org.springframework.core.type.filter.AbstractClassTestingTypeFilter
    protected boolean match(ClassMetadata metadata) {
        return this.pattern.matcher(metadata.getClassName()).matches();
    }
}