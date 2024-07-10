package org.springframework.web.util.pattern;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.web.util.pattern.PathPattern;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/pattern/CaptureVariablePathElement.class */
class CaptureVariablePathElement extends PathElement {
    private final String variableName;
    @Nullable
    private Pattern constraintPattern;

    /* JADX INFO: Access modifiers changed from: package-private */
    public CaptureVariablePathElement(int pos, char[] captureDescriptor, boolean caseSensitive, char separator) {
        super(pos, separator);
        int colon = -1;
        int i = 0;
        while (true) {
            if (i >= captureDescriptor.length) {
                break;
            } else if (captureDescriptor[i] != ':') {
                i++;
            } else {
                colon = i;
                break;
            }
        }
        if (colon == -1) {
            this.variableName = new String(captureDescriptor, 1, captureDescriptor.length - 2);
            return;
        }
        this.variableName = new String(captureDescriptor, 1, colon - 1);
        if (caseSensitive) {
            this.constraintPattern = Pattern.compile(new String(captureDescriptor, colon + 1, (captureDescriptor.length - colon) - 2));
        } else {
            this.constraintPattern = Pattern.compile(new String(captureDescriptor, colon + 1, (captureDescriptor.length - colon) - 2), 2);
        }
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public boolean matches(int pathIndex, PathPattern.MatchingContext matchingContext) {
        if (pathIndex >= matchingContext.pathLength) {
            return false;
        }
        String candidateCapture = matchingContext.pathElementValue(pathIndex);
        if (candidateCapture.length() == 0) {
            return false;
        }
        if (this.constraintPattern != null) {
            Matcher matcher = this.constraintPattern.matcher(candidateCapture);
            if (matcher.groupCount() != 0) {
                throw new IllegalArgumentException("No capture groups allowed in the constraint regex: " + this.constraintPattern.pattern());
            }
            if (!matcher.matches()) {
                return false;
            }
        }
        boolean match = false;
        int pathIndex2 = pathIndex + 1;
        if (isNoMorePattern()) {
            if (matchingContext.determineRemainingPath) {
                matchingContext.remainingPathIndex = pathIndex2;
                match = true;
            } else {
                match = pathIndex2 == matchingContext.pathLength;
                if (!match && matchingContext.isMatchOptionalTrailingSeparator()) {
                    match = pathIndex2 + 1 == matchingContext.pathLength && matchingContext.isSeparator(pathIndex2);
                }
            }
        } else if (this.next != null) {
            match = this.next.matches(pathIndex2, matchingContext);
        }
        if (match && matchingContext.extractingVariables) {
            matchingContext.set(this.variableName, candidateCapture, ((PathContainer.PathSegment) matchingContext.pathElements.get(pathIndex2 - 1)).parameters());
        }
        return match;
    }

    public String getVariableName() {
        return this.variableName;
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public int getNormalizedLength() {
        return 1;
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public int getWildcardCount() {
        return 0;
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public int getCaptureCount() {
        return 1;
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public int getScore() {
        return 1;
    }

    public String toString() {
        return "CaptureVariable({" + this.variableName + (this.constraintPattern != null ? ":" + this.constraintPattern.pattern() : "") + "})";
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public char[] getChars() {
        StringBuilder b = new StringBuilder();
        b.append("{");
        b.append(this.variableName);
        if (this.constraintPattern != null) {
            b.append(":").append(this.constraintPattern.pattern());
        }
        b.append("}");
        return b.toString().toCharArray();
    }
}