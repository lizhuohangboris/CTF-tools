package org.apache.catalina.filters;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Globals;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.http.Parameters;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/filters/FailedRequestFilter.class */
public class FailedRequestFilter extends FilterBase {
    private final Log log = LogFactory.getLog(FailedRequestFilter.class);

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.filters.FilterBase
    public Log getLogger() {
        return this.log;
    }

    @Override // javax.servlet.Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        int status;
        if (!isGoodRequest(request)) {
            Parameters.FailReason reason = (Parameters.FailReason) request.getAttribute(Globals.PARAMETER_PARSE_FAILED_REASON_ATTR);
            switch (reason) {
                case IO_ERROR:
                    status = 500;
                    break;
                case POST_TOO_LARGE:
                    status = 413;
                    break;
                case TOO_MANY_PARAMETERS:
                case UNKNOWN:
                case INVALID_CONTENT_TYPE:
                case MULTIPART_CONFIG_INVALID:
                case NO_NAME:
                case REQUEST_BODY_INCOMPLETE:
                case URL_DECODING:
                case CLIENT_DISCONNECT:
                default:
                    status = 400;
                    break;
            }
            ((HttpServletResponse) response).sendError(status);
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean isGoodRequest(ServletRequest request) {
        request.getParameter("none");
        if (request.getAttribute(Globals.PARAMETER_PARSE_FAILED_ATTR) != null) {
            return false;
        }
        return true;
    }

    @Override // org.apache.catalina.filters.FilterBase
    protected boolean isConfigProblemFatal() {
        return true;
    }
}