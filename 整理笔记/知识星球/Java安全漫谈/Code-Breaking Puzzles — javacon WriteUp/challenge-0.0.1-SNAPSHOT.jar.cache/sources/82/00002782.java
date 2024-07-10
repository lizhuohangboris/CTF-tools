package org.springframework.web.util.pattern;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/util/pattern/PathPatternParser.class */
public class PathPatternParser {
    private boolean matchOptionalTrailingSeparator = true;
    private boolean caseSensitive = true;

    public void setMatchOptionalTrailingSeparator(boolean matchOptionalTrailingSeparator) {
        this.matchOptionalTrailingSeparator = matchOptionalTrailingSeparator;
    }

    public boolean isMatchOptionalTrailingSeparator() {
        return this.matchOptionalTrailingSeparator;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public boolean isCaseSensitive() {
        return this.caseSensitive;
    }

    public char getSeparator() {
        return '/';
    }

    public PathPattern parse(String pathPattern) throws PatternParseException {
        return new InternalPathPatternParser(this).parse(pathPattern);
    }
}