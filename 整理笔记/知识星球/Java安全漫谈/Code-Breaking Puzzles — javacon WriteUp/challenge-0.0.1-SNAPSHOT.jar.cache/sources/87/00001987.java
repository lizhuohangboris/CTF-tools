package org.springframework.boot.diagnostics.analyzer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.util.ClassUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/diagnostics/analyzer/NoSuchMethodFailureAnalyzer.class */
class NoSuchMethodFailureAnalyzer extends AbstractFailureAnalyzer<NoSuchMethodError> {
    NoSuchMethodFailureAnalyzer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, NoSuchMethodError cause) {
        List<URL> candidates;
        URL actual;
        String className = extractClassName(cause);
        if (className == null || (candidates = findCandidates(className)) == null || (actual = getActual(className)) == null) {
            return null;
        }
        String description = getDescription(cause, className, candidates, actual);
        return new FailureAnalysis(description, "Correct the classpath of your application so that it contains a single, compatible version of " + className, cause);
    }

    private String extractClassName(NoSuchMethodError cause) {
        String classAndMethodName;
        int methodNameIndex;
        int descriptorIndex = cause.getMessage().indexOf(40);
        if (descriptorIndex == -1 || (methodNameIndex = (classAndMethodName = cause.getMessage().substring(0, descriptorIndex)).lastIndexOf(46)) == -1) {
            return null;
        }
        return classAndMethodName.substring(0, methodNameIndex);
    }

    private List<URL> findCandidates(String className) {
        try {
            return Collections.list(NoSuchMethodFailureAnalyzer.class.getClassLoader().getResources(ClassUtils.convertClassNameToResourcePath(className) + ClassUtils.CLASS_FILE_SUFFIX));
        } catch (Throwable th) {
            return null;
        }
    }

    private URL getActual(String className) {
        try {
            return getClass().getClassLoader().loadClass(className).getProtectionDomain().getCodeSource().getLocation();
        } catch (Throwable th) {
            return null;
        }
    }

    private String getDescription(NoSuchMethodError cause, String className, List<URL> candidates, URL actual) {
        StringWriter description = new StringWriter();
        PrintWriter writer = new PrintWriter(description);
        writer.print("An attempt was made to call the method ");
        writer.print(cause.getMessage());
        writer.print(" but it does not exist. Its class, ");
        writer.print(className);
        writer.println(", is available from the following locations:");
        writer.println();
        for (URL candidate : candidates) {
            writer.print("    ");
            writer.println(candidate);
        }
        writer.println();
        writer.println("It was loaded from the following location:");
        writer.println();
        writer.print("    ");
        writer.println(actual);
        return description.toString();
    }
}