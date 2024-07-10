package javax.servlet;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/AsyncContext.class */
public interface AsyncContext {
    public static final String ASYNC_REQUEST_URI = "javax.servlet.async.request_uri";
    public static final String ASYNC_CONTEXT_PATH = "javax.servlet.async.context_path";
    public static final String ASYNC_MAPPING = "javax.servlet.async.mapping";
    public static final String ASYNC_PATH_INFO = "javax.servlet.async.path_info";
    public static final String ASYNC_SERVLET_PATH = "javax.servlet.async.servlet_path";
    public static final String ASYNC_QUERY_STRING = "javax.servlet.async.query_string";

    ServletRequest getRequest();

    ServletResponse getResponse();

    boolean hasOriginalRequestAndResponse();

    void dispatch();

    void dispatch(String str);

    void dispatch(ServletContext servletContext, String str);

    void complete();

    void start(Runnable runnable);

    void addListener(AsyncListener asyncListener);

    void addListener(AsyncListener asyncListener, ServletRequest servletRequest, ServletResponse servletResponse);

    <T extends AsyncListener> T createListener(Class<T> cls) throws ServletException;

    void setTimeout(long j);

    long getTimeout();
}