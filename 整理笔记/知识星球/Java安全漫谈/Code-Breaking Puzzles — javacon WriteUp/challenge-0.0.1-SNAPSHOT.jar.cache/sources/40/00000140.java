package ch.qos.logback.core.net;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/net/LoginAuthenticator.class */
public class LoginAuthenticator extends Authenticator {
    String username;
    String password;

    /* JADX INFO: Access modifiers changed from: package-private */
    public LoginAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(this.username, this.password);
    }
}