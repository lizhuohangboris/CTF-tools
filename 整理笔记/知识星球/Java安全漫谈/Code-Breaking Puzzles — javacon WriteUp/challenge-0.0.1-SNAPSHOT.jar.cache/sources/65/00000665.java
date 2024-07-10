package javax.servlet.http;

import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/http/Cookie.class */
public class Cookie implements Cloneable, Serializable {
    private static final CookieNameValidator validation;
    private static final long serialVersionUID = 1;
    private final String name;
    private String value;
    private String comment;
    private String domain;
    private String path;
    private boolean secure;
    private boolean httpOnly;
    private int version = 0;
    private int maxAge = -1;

    static {
        boolean strictServletCompliance;
        String propStrictNaming;
        String propFwdSlashIsSeparator;
        boolean strictNaming;
        boolean allowSlash;
        if (System.getSecurityManager() == null) {
            strictServletCompliance = Boolean.getBoolean("org.apache.catalina.STRICT_SERVLET_COMPLIANCE");
            propStrictNaming = System.getProperty("org.apache.tomcat.util.http.ServerCookie.STRICT_NAMING");
            propFwdSlashIsSeparator = System.getProperty("org.apache.tomcat.util.http.ServerCookie.FWD_SLASH_IS_SEPARATOR");
        } else {
            strictServletCompliance = ((Boolean) AccessController.doPrivileged(new PrivilegedAction<Boolean>() { // from class: javax.servlet.http.Cookie.1
                @Override // java.security.PrivilegedAction
                public Boolean run() {
                    return Boolean.valueOf(System.getProperty("org.apache.catalina.STRICT_SERVLET_COMPLIANCE"));
                }
            })).booleanValue();
            propStrictNaming = (String) AccessController.doPrivileged(new PrivilegedAction<String>() { // from class: javax.servlet.http.Cookie.2
                @Override // java.security.PrivilegedAction
                public String run() {
                    return System.getProperty("org.apache.tomcat.util.http.ServerCookie.STRICT_NAMING");
                }
            });
            propFwdSlashIsSeparator = (String) AccessController.doPrivileged(new PrivilegedAction<String>() { // from class: javax.servlet.http.Cookie.3
                @Override // java.security.PrivilegedAction
                public String run() {
                    return System.getProperty("org.apache.tomcat.util.http.ServerCookie.FWD_SLASH_IS_SEPARATOR");
                }
            });
        }
        if (propStrictNaming == null) {
            strictNaming = strictServletCompliance;
        } else {
            strictNaming = Boolean.parseBoolean(propStrictNaming);
        }
        if (propFwdSlashIsSeparator == null) {
            allowSlash = !strictServletCompliance;
        } else {
            allowSlash = !Boolean.parseBoolean(propFwdSlashIsSeparator);
        }
        if (strictNaming) {
            validation = new RFC2109Validator(allowSlash);
        } else {
            validation = new RFC6265Validator();
        }
    }

    public Cookie(String name, String value) {
        validation.validate(name);
        this.name = name;
        this.value = value;
    }

    public void setComment(String purpose) {
        this.comment = purpose;
    }

    public String getComment() {
        return this.comment;
    }

    public void setDomain(String pattern) {
        this.domain = pattern.toLowerCase(Locale.ENGLISH);
    }

    public String getDomain() {
        return this.domain;
    }

    public void setMaxAge(int expiry) {
        this.maxAge = expiry;
    }

    public int getMaxAge() {
        return this.maxAge;
    }

    public void setPath(String uri) {
        this.path = uri;
    }

    public String getPath() {
        return this.path;
    }

    public void setSecure(boolean flag) {
        this.secure = flag;
    }

    public boolean getSecure() {
        return this.secure;
    }

    public String getName() {
        return this.name;
    }

    public void setValue(String newValue) {
        this.value = newValue;
    }

    public String getValue() {
        return this.value;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int v) {
        this.version = v;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public boolean isHttpOnly() {
        return this.httpOnly;
    }
}