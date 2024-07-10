package org.springframework.boot.diagnostics.analyzer;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.web.embedded.tomcat.ConnectorStartFailedException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/diagnostics/analyzer/ConnectorStartFailureAnalyzer.class */
class ConnectorStartFailureAnalyzer extends AbstractFailureAnalyzer<ConnectorStartFailedException> {
    ConnectorStartFailureAnalyzer() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.boot.diagnostics.AbstractFailureAnalyzer
    public FailureAnalysis analyze(Throwable rootFailure, ConnectorStartFailedException cause) {
        return new FailureAnalysis("The Tomcat connector configured to listen on port " + cause.getPort() + " failed to start. The port may already be in use or the connector may be misconfigured.", "Verify the connector's configuration, identify and stop any process that's listening on port " + cause.getPort() + ", or configure this application to listen on another port.", cause);
    }
}