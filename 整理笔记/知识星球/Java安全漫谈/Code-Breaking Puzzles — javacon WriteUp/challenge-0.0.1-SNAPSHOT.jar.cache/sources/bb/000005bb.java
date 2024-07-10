package io.tricking.challenge;

import ch.qos.logback.classic.ClassicConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = ClassicConstants.USER_MDC_KEY)
@Component
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/classes/io/tricking/challenge/UserConfig.class */
public class UserConfig {
    private String username;
    private String password;
    private String rememberMeKey;

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getRememberMeKey() {
        return this.rememberMeKey;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRememberMeKey(String rememberMeKey) {
        this.rememberMeKey = rememberMeKey;
    }

    public String encryptRememberMe() {
        String encryptd = Encryptor.encrypt(this.rememberMeKey, "0123456789abcdef", this.username);
        return encryptd;
    }

    public String decryptRememberMe(String encryptd) {
        return Encryptor.decrypt(this.rememberMeKey, "0123456789abcdef", encryptd);
    }
}