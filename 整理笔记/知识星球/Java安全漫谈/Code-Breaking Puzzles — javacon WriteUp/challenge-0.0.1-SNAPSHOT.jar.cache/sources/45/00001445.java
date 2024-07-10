package org.springframework.beans.factory.parsing;

import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/parsing/ReaderContext.class */
public class ReaderContext {
    private final Resource resource;
    private final ProblemReporter problemReporter;
    private final ReaderEventListener eventListener;
    private final SourceExtractor sourceExtractor;

    public ReaderContext(Resource resource, ProblemReporter problemReporter, ReaderEventListener eventListener, SourceExtractor sourceExtractor) {
        this.resource = resource;
        this.problemReporter = problemReporter;
        this.eventListener = eventListener;
        this.sourceExtractor = sourceExtractor;
    }

    public final Resource getResource() {
        return this.resource;
    }

    public void fatal(String message, @Nullable Object source) {
        fatal(message, source, null, null);
    }

    public void fatal(String message, @Nullable Object source, @Nullable Throwable cause) {
        fatal(message, source, null, cause);
    }

    public void fatal(String message, @Nullable Object source, @Nullable ParseState parseState) {
        fatal(message, source, parseState, null);
    }

    public void fatal(String message, @Nullable Object source, @Nullable ParseState parseState, @Nullable Throwable cause) {
        Location location = new Location(getResource(), source);
        this.problemReporter.fatal(new Problem(message, location, parseState, cause));
    }

    public void error(String message, @Nullable Object source) {
        error(message, source, null, null);
    }

    public void error(String message, @Nullable Object source, @Nullable Throwable cause) {
        error(message, source, null, cause);
    }

    public void error(String message, @Nullable Object source, @Nullable ParseState parseState) {
        error(message, source, parseState, null);
    }

    public void error(String message, @Nullable Object source, @Nullable ParseState parseState, @Nullable Throwable cause) {
        Location location = new Location(getResource(), source);
        this.problemReporter.error(new Problem(message, location, parseState, cause));
    }

    public void warning(String message, @Nullable Object source) {
        warning(message, source, null, null);
    }

    public void warning(String message, @Nullable Object source, @Nullable Throwable cause) {
        warning(message, source, null, cause);
    }

    public void warning(String message, @Nullable Object source, @Nullable ParseState parseState) {
        warning(message, source, parseState, null);
    }

    public void warning(String message, @Nullable Object source, @Nullable ParseState parseState, @Nullable Throwable cause) {
        Location location = new Location(getResource(), source);
        this.problemReporter.warning(new Problem(message, location, parseState, cause));
    }

    public void fireDefaultsRegistered(DefaultsDefinition defaultsDefinition) {
        this.eventListener.defaultsRegistered(defaultsDefinition);
    }

    public void fireComponentRegistered(ComponentDefinition componentDefinition) {
        this.eventListener.componentRegistered(componentDefinition);
    }

    public void fireAliasRegistered(String beanName, String alias, @Nullable Object source) {
        this.eventListener.aliasRegistered(new AliasDefinition(beanName, alias, source));
    }

    public void fireImportProcessed(String importedResource, @Nullable Object source) {
        this.eventListener.importProcessed(new ImportDefinition(importedResource, source));
    }

    public void fireImportProcessed(String importedResource, Resource[] actualResources, @Nullable Object source) {
        this.eventListener.importProcessed(new ImportDefinition(importedResource, actualResources, source));
    }

    public SourceExtractor getSourceExtractor() {
        return this.sourceExtractor;
    }

    @Nullable
    public Object extractSource(Object sourceCandidate) {
        return this.sourceExtractor.extractSource(sourceCandidate, this.resource);
    }
}