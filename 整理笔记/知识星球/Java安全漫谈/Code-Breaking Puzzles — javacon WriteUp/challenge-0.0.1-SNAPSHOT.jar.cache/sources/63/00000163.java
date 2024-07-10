package ch.qos.logback.core.net.ssl;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/net/ssl/SSLConfiguration.class */
public class SSLConfiguration extends SSLContextFactoryBean {
    private SSLParametersConfiguration parameters;

    public SSLParametersConfiguration getParameters() {
        if (this.parameters == null) {
            this.parameters = new SSLParametersConfiguration();
        }
        return this.parameters;
    }

    public void setParameters(SSLParametersConfiguration parameters) {
        this.parameters = parameters;
    }
}