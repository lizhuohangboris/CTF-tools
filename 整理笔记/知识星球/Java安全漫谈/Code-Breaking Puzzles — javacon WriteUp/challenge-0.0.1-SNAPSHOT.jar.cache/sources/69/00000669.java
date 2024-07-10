package javax.servlet.http;

import java.text.MessageFormat;
import java.util.BitSet;
import java.util.ResourceBundle;

/* compiled from: Cookie.java */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/http/CookieNameValidator.class */
class CookieNameValidator {
    private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
    protected static final ResourceBundle lStrings = ResourceBundle.getBundle(LSTRING_FILE);
    protected final BitSet allowed = new BitSet(128);

    /* JADX INFO: Access modifiers changed from: protected */
    public CookieNameValidator(String separators) {
        this.allowed.set(32, 127);
        for (int i = 0; i < separators.length(); i++) {
            char ch2 = separators.charAt(i);
            this.allowed.clear(ch2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void validate(String name) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException(lStrings.getString("err.cookie_name_blank"));
        }
        if (!isToken(name)) {
            String errMsg = lStrings.getString("err.cookie_name_is_token");
            throw new IllegalArgumentException(MessageFormat.format(errMsg, name));
        }
    }

    private boolean isToken(String possibleToken) {
        int len = possibleToken.length();
        for (int i = 0; i < len; i++) {
            char c = possibleToken.charAt(i);
            if (!this.allowed.get(c)) {
                return false;
            }
        }
        return true;
    }
}