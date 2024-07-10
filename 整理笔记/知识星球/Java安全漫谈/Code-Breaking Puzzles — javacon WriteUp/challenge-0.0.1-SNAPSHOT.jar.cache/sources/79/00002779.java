package org.springframework.web.util.pattern;

import java.util.List;
import org.springframework.http.server.PathContainer;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.pattern.PathPattern;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/pattern/CaptureTheRestPathElement.class */
public class CaptureTheRestPathElement extends PathElement {
    private final String variableName;

    public CaptureTheRestPathElement(int pos, char[] captureDescriptor, char separator) {
        super(pos, separator);
        this.variableName = new String(captureDescriptor, 2, captureDescriptor.length - 3);
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public boolean matches(int pathIndex, PathPattern.MatchingContext matchingContext) {
        if (pathIndex < matchingContext.pathLength && !matchingContext.isSeparator(pathIndex)) {
            return false;
        }
        if (matchingContext.determineRemainingPath) {
            matchingContext.remainingPathIndex = matchingContext.pathLength;
        }
        if (matchingContext.extractingVariables) {
            MultiValueMap<String, String> parametersCollector = null;
            for (int i = pathIndex; i < matchingContext.pathLength; i++) {
                PathContainer.Element element = matchingContext.pathElements.get(i);
                if (element instanceof PathContainer.PathSegment) {
                    MultiValueMap<String, String> parameters = ((PathContainer.PathSegment) element).parameters();
                    if (!parameters.isEmpty()) {
                        if (parametersCollector == null) {
                            parametersCollector = new LinkedMultiValueMap<>();
                        }
                        parametersCollector.addAll(parameters);
                    }
                }
            }
            matchingContext.set(this.variableName, pathToString(pathIndex, matchingContext.pathElements), parametersCollector == null ? NO_PARAMETERS : parametersCollector);
            return true;
        }
        return true;
    }

    private String pathToString(int fromSegment, List<PathContainer.Element> pathElements) {
        StringBuilder buf = new StringBuilder();
        int max = pathElements.size();
        for (int i = fromSegment; i < max; i++) {
            PathContainer.Element element = pathElements.get(i);
            if (element instanceof PathContainer.PathSegment) {
                buf.append(((PathContainer.PathSegment) element).valueToMatch());
            } else {
                buf.append(element.value());
            }
        }
        return buf.toString();
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

    public String toString() {
        return "CaptureTheRest(/{*" + this.variableName + "})";
    }

    @Override // org.springframework.web.util.pattern.PathElement
    public char[] getChars() {
        return ("/{*" + this.variableName + "}").toCharArray();
    }
}