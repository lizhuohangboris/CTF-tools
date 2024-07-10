package javax.servlet;

import java.util.ResourceBundle;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/HttpMethodConstraintElement.class */
public class HttpMethodConstraintElement extends HttpConstraintElement {
    private static final String LSTRING_FILE = "javax.servlet.LocalStrings";
    private static final ResourceBundle lStrings = ResourceBundle.getBundle(LSTRING_FILE);
    private final String methodName;

    public HttpMethodConstraintElement(String methodName) {
        if (methodName == null || methodName.length() == 0) {
            throw new IllegalArgumentException(lStrings.getString("httpMethodConstraintElement.invalidMethod"));
        }
        this.methodName = methodName;
    }

    public HttpMethodConstraintElement(String methodName, HttpConstraintElement constraint) {
        super(constraint.getEmptyRoleSemantic(), constraint.getTransportGuarantee(), constraint.getRolesAllowed());
        if (methodName == null || methodName.length() == 0) {
            throw new IllegalArgumentException(lStrings.getString("httpMethodConstraintElement.invalidMethod"));
        }
        this.methodName = methodName;
    }

    public String getMethodName() {
        return this.methodName;
    }
}