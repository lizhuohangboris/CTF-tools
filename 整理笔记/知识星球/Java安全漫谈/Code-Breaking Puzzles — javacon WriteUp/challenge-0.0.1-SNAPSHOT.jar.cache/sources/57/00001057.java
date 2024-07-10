package org.hibernate.validator.internal.constraintvalidators.hv;

import java.net.MalformedURLException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraints.URL;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/internal/constraintvalidators/hv/URLValidator.class */
public class URLValidator implements ConstraintValidator<URL, CharSequence> {
    private String protocol;
    private String host;
    private int port;

    @Override // javax.validation.ConstraintValidator
    public void initialize(URL url) {
        this.protocol = url.protocol();
        this.host = url.host();
        this.port = url.port();
    }

    @Override // javax.validation.ConstraintValidator
    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.length() == 0) {
            return true;
        }
        try {
            java.net.URL url = new java.net.URL(value.toString());
            if (this.protocol != null && this.protocol.length() > 0 && !url.getProtocol().equals(this.protocol)) {
                return false;
            }
            if (this.host != null && this.host.length() > 0 && !url.getHost().equals(this.host)) {
                return false;
            }
            if (this.port != -1 && url.getPort() != this.port) {
                return false;
            }
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}