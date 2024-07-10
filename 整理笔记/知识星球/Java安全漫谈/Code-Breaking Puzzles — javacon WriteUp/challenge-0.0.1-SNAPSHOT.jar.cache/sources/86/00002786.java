package org.springframework.web.util.pattern;

import org.springframework.web.util.pattern.PathPattern;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/pattern/SeparatorPathElement.class */
public class SeparatorPathElement extends PathElement {
    public SeparatorPathElement(int pos, char separator) {
        super(pos, separator);
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public boolean matches(int pathIndex, PathPattern.MatchingContext matchingContext) {
        if (pathIndex < matchingContext.pathLength && matchingContext.isSeparator(pathIndex)) {
            if (!isNoMorePattern()) {
                return this.next != null && this.next.matches(pathIndex + 1, matchingContext);
            } else if (!matchingContext.determineRemainingPath) {
                return pathIndex + 1 == matchingContext.pathLength;
            } else {
                matchingContext.remainingPathIndex = pathIndex + 1;
                return true;
            }
        }
        return false;
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public int getNormalizedLength() {
        return 1;
    }

    public String toString() {
        return "Separator(" + this.separator + ")";
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public char[] getChars() {
        return new char[]{this.separator};
    }
}