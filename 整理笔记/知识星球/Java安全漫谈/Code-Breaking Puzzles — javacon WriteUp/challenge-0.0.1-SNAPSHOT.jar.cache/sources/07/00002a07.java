package org.thymeleaf.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/PatternSpec.class */
public final class PatternSpec {
    private static final int DEFAULT_PATTERN_SET_SIZE = 3;
    private LinkedHashSet<String> patternStrs;
    private LinkedHashSet<Pattern> patterns;

    public boolean isEmpty() {
        return this.patterns == null || this.patterns.size() == 0;
    }

    public Set<String> getPatterns() {
        if (this.patternStrs == null) {
            return Collections.EMPTY_SET;
        }
        return Collections.unmodifiableSet(this.patternStrs);
    }

    public void setPatterns(Set<String> newPatterns) {
        if (newPatterns != null) {
            if (this.patterns == null) {
                this.patternStrs = new LinkedHashSet<>(3);
                this.patterns = new LinkedHashSet<>(3);
            } else {
                this.patternStrs.clear();
                this.patterns.clear();
            }
            this.patternStrs.addAll(newPatterns);
            for (String pattern : newPatterns) {
                this.patterns.add(PatternUtils.strPatternToPattern(pattern));
            }
        } else if (this.patterns != null) {
            this.patternStrs.clear();
            this.patterns.clear();
        }
    }

    public void addPattern(String pattern) {
        Validate.notEmpty(pattern, "Pattern cannot be null or empty");
        if (this.patterns == null) {
            this.patternStrs = new LinkedHashSet<>(3);
            this.patterns = new LinkedHashSet<>(3);
        }
        this.patternStrs.add(pattern);
        this.patterns.add(PatternUtils.strPatternToPattern(pattern));
    }

    public void clearPatterns() {
        if (this.patterns != null) {
            this.patternStrs.clear();
            this.patterns.clear();
        }
    }

    public boolean matches(String templateName) {
        if (this.patterns == null) {
            return false;
        }
        Iterator<Pattern> it = this.patterns.iterator();
        while (it.hasNext()) {
            Pattern p = it.next();
            if (p.matcher(templateName).matches()) {
                return true;
            }
        }
        return false;
    }
}