package org.apache.catalina.startup;

import org.apache.tomcat.util.digester.Rule;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/startup/CertificateCreateRule.class */
public class CertificateCreateRule extends Rule {
    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        SSLHostConfigCertificate.Type type;
        SSLHostConfig sslHostConfig = (SSLHostConfig) this.digester.peek();
        String typeValue = attributes.getValue("type");
        if (typeValue == null || typeValue.length() == 0) {
            type = SSLHostConfigCertificate.Type.UNDEFINED;
        } else {
            type = SSLHostConfigCertificate.Type.valueOf(typeValue);
        }
        SSLHostConfigCertificate certificate = new SSLHostConfigCertificate(sslHostConfig, type);
        this.digester.push(certificate);
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void end(String namespace, String name) throws Exception {
        this.digester.pop();
    }
}