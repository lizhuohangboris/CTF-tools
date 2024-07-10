package javax.servlet;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/UnavailableException.class */
public class UnavailableException extends ServletException {
    private static final long serialVersionUID = 1;
    private final Servlet servlet;
    private final boolean permanent;
    private final int seconds;

    @Deprecated
    public UnavailableException(Servlet servlet, String msg) {
        super(msg);
        this.servlet = servlet;
        this.permanent = true;
        this.seconds = 0;
    }

    @Deprecated
    public UnavailableException(int seconds, Servlet servlet, String msg) {
        super(msg);
        this.servlet = servlet;
        if (seconds <= 0) {
            this.seconds = -1;
        } else {
            this.seconds = seconds;
        }
        this.permanent = false;
    }

    public UnavailableException(String msg) {
        super(msg);
        this.seconds = 0;
        this.servlet = null;
        this.permanent = true;
    }

    public UnavailableException(String msg, int seconds) {
        super(msg);
        if (seconds <= 0) {
            this.seconds = -1;
        } else {
            this.seconds = seconds;
        }
        this.servlet = null;
        this.permanent = false;
    }

    public boolean isPermanent() {
        return this.permanent;
    }

    @Deprecated
    public Servlet getServlet() {
        return this.servlet;
    }

    public int getUnavailableSeconds() {
        if (this.permanent) {
            return -1;
        }
        return this.seconds;
    }
}