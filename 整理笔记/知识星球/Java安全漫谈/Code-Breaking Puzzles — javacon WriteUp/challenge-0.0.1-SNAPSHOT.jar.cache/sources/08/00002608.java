package org.springframework.web.servlet.mvc.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UrlPathHelper;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-webmvc-5.1.2.RELEASE.jar:org/springframework/web/servlet/mvc/condition/PatternsRequestCondition.class */
public final class PatternsRequestCondition extends AbstractRequestCondition<PatternsRequestCondition> {
    private final Set<String> patterns;
    private final UrlPathHelper pathHelper;
    private final PathMatcher pathMatcher;
    private final boolean useSuffixPatternMatch;
    private final boolean useTrailingSlashMatch;
    private final List<String> fileExtensions;

    public PatternsRequestCondition(String... patterns) {
        this((Collection<String>) Arrays.asList(patterns), (UrlPathHelper) null, (PathMatcher) null, true, true, (List<String>) null);
    }

    public PatternsRequestCondition(String[] patterns, @Nullable UrlPathHelper urlPathHelper, @Nullable PathMatcher pathMatcher, boolean useSuffixPatternMatch, boolean useTrailingSlashMatch) {
        this(Arrays.asList(patterns), urlPathHelper, pathMatcher, useSuffixPatternMatch, useTrailingSlashMatch, (List<String>) null);
    }

    public PatternsRequestCondition(String[] patterns, @Nullable UrlPathHelper urlPathHelper, @Nullable PathMatcher pathMatcher, boolean useSuffixPatternMatch, boolean useTrailingSlashMatch, @Nullable List<String> fileExtensions) {
        this(Arrays.asList(patterns), urlPathHelper, pathMatcher, useSuffixPatternMatch, useTrailingSlashMatch, fileExtensions);
    }

    private PatternsRequestCondition(Collection<String> patterns, @Nullable UrlPathHelper urlPathHelper, @Nullable PathMatcher pathMatcher, boolean useSuffixPatternMatch, boolean useTrailingSlashMatch, @Nullable List<String> fileExtensions) {
        this.fileExtensions = new ArrayList();
        this.patterns = Collections.unmodifiableSet(prependLeadingSlash(patterns));
        this.pathHelper = urlPathHelper != null ? urlPathHelper : new UrlPathHelper();
        this.pathMatcher = pathMatcher != null ? pathMatcher : new AntPathMatcher();
        this.useSuffixPatternMatch = useSuffixPatternMatch;
        this.useTrailingSlashMatch = useTrailingSlashMatch;
        if (fileExtensions != null) {
            for (String fileExtension : fileExtensions) {
                if (fileExtension.charAt(0) != '.') {
                    fileExtension = "." + fileExtension;
                }
                this.fileExtensions.add(fileExtension);
            }
        }
    }

    private static Set<String> prependLeadingSlash(Collection<String> patterns) {
        Set<String> result = new LinkedHashSet<>(patterns.size());
        for (String pattern : patterns) {
            if (StringUtils.hasLength(pattern) && !pattern.startsWith("/")) {
                pattern = "/" + pattern;
            }
            result.add(pattern);
        }
        return result;
    }

    public Set<String> getPatterns() {
        return this.patterns;
    }

    @Override // org.springframework.web.servlet.mvc.condition.AbstractRequestCondition
    protected Collection<String> getContent() {
        return this.patterns;
    }

    @Override // org.springframework.web.servlet.mvc.condition.AbstractRequestCondition
    protected String getToStringInfix() {
        return " || ";
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    public PatternsRequestCondition combine(PatternsRequestCondition other) {
        Set<String> result = new LinkedHashSet<>();
        if (!this.patterns.isEmpty() && !other.patterns.isEmpty()) {
            for (String pattern1 : this.patterns) {
                for (String pattern2 : other.patterns) {
                    result.add(this.pathMatcher.combine(pattern1, pattern2));
                }
            }
        } else if (!this.patterns.isEmpty()) {
            result.addAll(this.patterns);
        } else if (!other.patterns.isEmpty()) {
            result.addAll(other.patterns);
        } else {
            result.add("");
        }
        return new PatternsRequestCondition(result, this.pathHelper, this.pathMatcher, this.useSuffixPatternMatch, this.useTrailingSlashMatch, this.fileExtensions);
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    @Nullable
    public PatternsRequestCondition getMatchingCondition(HttpServletRequest request) {
        if (this.patterns.isEmpty()) {
            return this;
        }
        String lookupPath = this.pathHelper.getLookupPathForRequest(request);
        List<String> matches = getMatchingPatterns(lookupPath);
        if (matches.isEmpty()) {
            return null;
        }
        return new PatternsRequestCondition(matches, this.pathHelper, this.pathMatcher, this.useSuffixPatternMatch, this.useTrailingSlashMatch, this.fileExtensions);
    }

    public List<String> getMatchingPatterns(String lookupPath) {
        List<String> matches = new ArrayList<>();
        for (String pattern : this.patterns) {
            String match = getMatchingPattern(pattern, lookupPath);
            if (match != null) {
                matches.add(match);
            }
        }
        if (matches.size() > 1) {
            matches.sort(this.pathMatcher.getPatternComparator(lookupPath));
        }
        return matches;
    }

    @Nullable
    private String getMatchingPattern(String pattern, String lookupPath) {
        if (pattern.equals(lookupPath)) {
            return pattern;
        }
        if (this.useSuffixPatternMatch) {
            if (!this.fileExtensions.isEmpty() && lookupPath.indexOf(46) != -1) {
                for (String extension : this.fileExtensions) {
                    if (this.pathMatcher.match(pattern + extension, lookupPath)) {
                        return pattern + extension;
                    }
                }
            } else {
                boolean hasSuffix = pattern.indexOf(46) != -1;
                if (!hasSuffix && this.pathMatcher.match(pattern + ".*", lookupPath)) {
                    return pattern + ".*";
                }
            }
        }
        if (this.pathMatcher.match(pattern, lookupPath)) {
            return pattern;
        }
        if (this.useTrailingSlashMatch && !pattern.endsWith("/") && this.pathMatcher.match(pattern + "/", lookupPath)) {
            return pattern + "/";
        }
        return null;
    }

    @Override // org.springframework.web.servlet.mvc.condition.RequestCondition
    public int compareTo(PatternsRequestCondition other, HttpServletRequest request) {
        String lookupPath = this.pathHelper.getLookupPathForRequest(request);
        Comparator<String> patternComparator = this.pathMatcher.getPatternComparator(lookupPath);
        Iterator<String> iterator = this.patterns.iterator();
        Iterator<String> iteratorOther = other.patterns.iterator();
        while (iterator.hasNext() && iteratorOther.hasNext()) {
            int result = patternComparator.compare(iterator.next(), iteratorOther.next());
            if (result != 0) {
                return result;
            }
        }
        if (iterator.hasNext()) {
            return -1;
        }
        if (iteratorOther.hasNext()) {
            return 1;
        }
        return 0;
    }
}