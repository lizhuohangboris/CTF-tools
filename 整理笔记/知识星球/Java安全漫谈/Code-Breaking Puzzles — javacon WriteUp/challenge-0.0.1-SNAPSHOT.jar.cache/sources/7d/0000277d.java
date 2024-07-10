package org.springframework.web.util.pattern;

import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.pattern.PathPattern;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/pattern/PathElement.class */
public abstract class PathElement {
    protected static final int WILDCARD_WEIGHT = 100;
    protected static final int CAPTURE_VARIABLE_WEIGHT = 1;
    protected static final MultiValueMap<String, String> NO_PARAMETERS = new LinkedMultiValueMap();
    protected final int pos;
    protected final char separator;
    @Nullable
    protected PathElement next;
    @Nullable
    protected PathElement prev;

    public abstract boolean matches(int i, PathPattern.MatchingContext matchingContext);

    public abstract int getNormalizedLength();

    public abstract char[] getChars();

    public PathElement(int pos, char separator) {
        this.pos = pos;
        this.separator = separator;
    }

    public int getCaptureCount() {
        return 0;
    }

    public int getWildcardCount() {
        return 0;
    }

    public int getScore() {
        return 0;
    }

    public final boolean isNoMorePattern() {
        return this.next == null;
    }
}