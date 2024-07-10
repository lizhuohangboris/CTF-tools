package ch.qos.logback.classic.pattern;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/pattern/ClassNameOnlyAbbreviator.class */
public class ClassNameOnlyAbbreviator implements Abbreviator {
    @Override // ch.qos.logback.classic.pattern.Abbreviator
    public String abbreviate(String fqClassName) {
        int lastIndex = fqClassName.lastIndexOf(46);
        if (lastIndex != -1) {
            return fqClassName.substring(lastIndex + 1, fqClassName.length());
        }
        return fqClassName;
    }
}