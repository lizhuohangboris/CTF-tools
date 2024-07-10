package javax.servlet.http;

/* compiled from: Cookie.java */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/http/RFC6265Validator.class */
class RFC6265Validator extends CookieNameValidator {
    private static final String RFC2616_SEPARATORS = "()<>@,;:\\\"/[]?={} \t";

    /* JADX INFO: Access modifiers changed from: package-private */
    public RFC6265Validator() {
        super(RFC2616_SEPARATORS);
    }
}