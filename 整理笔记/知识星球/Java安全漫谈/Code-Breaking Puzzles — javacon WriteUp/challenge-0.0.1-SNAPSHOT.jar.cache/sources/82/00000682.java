package javax.servlet.http;

import java.text.MessageFormat;

/* compiled from: Cookie.java */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/http/RFC2109Validator.class */
class RFC2109Validator extends RFC6265Validator {
    /* JADX INFO: Access modifiers changed from: package-private */
    public RFC2109Validator(boolean allowSlash) {
        if (allowSlash) {
            this.allowed.set(47);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // javax.servlet.http.CookieNameValidator
    public void validate(String name) {
        super.validate(name);
        if (name.charAt(0) == '$') {
            String errMsg = lStrings.getString("err.cookie_name_is_token");
            throw new IllegalArgumentException(MessageFormat.format(errMsg, name));
        }
    }
}