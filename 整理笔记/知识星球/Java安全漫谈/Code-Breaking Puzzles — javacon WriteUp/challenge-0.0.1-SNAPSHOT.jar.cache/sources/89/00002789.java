package org.springframework.web.util.pattern;

import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/pattern/WildcardPathElement.class */
public class WildcardPathElement extends PathElement {
    public WildcardPathElement(int pos, char separator) {
        super(pos, separator);
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public boolean matches(int pathIndex, PathPattern.MatchingContext matchingContext) {
        String segmentData = null;
        if (pathIndex < matchingContext.pathLength) {
            PathContainer.Element element = matchingContext.pathElements.get(pathIndex);
            if (!(element instanceof PathContainer.PathSegment)) {
                return false;
            }
            segmentData = ((PathContainer.PathSegment) element).valueToMatch();
            pathIndex++;
        }
        if (!isNoMorePattern()) {
            return (segmentData == null || segmentData.length() == 0 || this.next == null || !this.next.matches(pathIndex, matchingContext)) ? false : true;
        } else if (matchingContext.determineRemainingPath) {
            matchingContext.remainingPathIndex = pathIndex;
            return true;
        } else if (pathIndex == matchingContext.pathLength) {
            return true;
        } else {
            return matchingContext.isMatchOptionalTrailingSeparator() && segmentData != null && segmentData.length() > 0 && pathIndex + 1 == matchingContext.pathLength && matchingContext.isSeparator(pathIndex);
        }
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public int getNormalizedLength() {
        return 1;
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public int getWildcardCount() {
        return 1;
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public int getScore() {
        return 100;
    }

    public String toString() {
        return "Wildcard(*)";
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public char[] getChars() {
        return new char[]{'*'};
    }
}