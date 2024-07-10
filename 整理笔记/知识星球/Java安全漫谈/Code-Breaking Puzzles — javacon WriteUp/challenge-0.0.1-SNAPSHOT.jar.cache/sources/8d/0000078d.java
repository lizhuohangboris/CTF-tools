package org.apache.catalina.authenticator;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Manager;
import org.apache.catalina.Realm;
import org.apache.catalina.Session;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.coyote.ActionCode;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.descriptor.web.LoginConfig;
import org.apache.tomcat.util.http.MimeHeaders;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.support.WebContentGenerator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/authenticator/FormAuthenticator.class */
public class FormAuthenticator extends AuthenticatorBase {
    private final Log log = LogFactory.getLog(FormAuthenticator.class);
    protected String characterEncoding = null;
    protected String landingPage = null;

    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    public void setCharacterEncoding(String encoding) {
        this.characterEncoding = encoding;
    }

    public String getLandingPage() {
        return this.landingPage;
    }

    public void setLandingPage(String landingPage) {
        this.landingPage = landingPage;
    }

    @Override // org.apache.catalina.authenticator.AuthenticatorBase
    protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
        if (checkForCachedAuthentication(request, response, true)) {
            return true;
        }
        Session session = null;
        if (!this.cache) {
            session = request.getSessionInternal(true);
            if (this.log.isDebugEnabled()) {
                this.log.debug("Checking for reauthenticate in session " + session);
            }
            String username = (String) session.getNote(Constants.SESS_USERNAME_NOTE);
            String password = (String) session.getNote(Constants.SESS_PASSWORD_NOTE);
            if (username != null && password != null) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Reauthenticating username '" + username + "'");
                }
                Principal principal = this.context.getRealm().authenticate(username, password);
                if (principal != null) {
                    session.setNote(Constants.FORM_PRINCIPAL_NOTE, principal);
                    if (!matchRequest(request)) {
                        register(request, response, principal, HttpServletRequest.FORM_AUTH, username, password);
                        return true;
                    }
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Reauthentication failed, proceed normally");
                }
            }
        }
        if (matchRequest(request)) {
            Session session2 = request.getSessionInternal(true);
            if (this.log.isDebugEnabled()) {
                this.log.debug("Restore request from session '" + session2.getIdInternal() + "'");
            }
            register(request, response, (Principal) session2.getNote(Constants.FORM_PRINCIPAL_NOTE), HttpServletRequest.FORM_AUTH, (String) session2.getNote(Constants.SESS_USERNAME_NOTE), (String) session2.getNote(Constants.SESS_PASSWORD_NOTE));
            if (this.cache) {
                session2.removeNote(Constants.SESS_USERNAME_NOTE);
                session2.removeNote(Constants.SESS_PASSWORD_NOTE);
            }
            if (restoreRequest(request, session2)) {
                if (this.log.isDebugEnabled()) {
                    this.log.debug("Proceed to restored request");
                    return true;
                }
                return true;
            }
            if (this.log.isDebugEnabled()) {
                this.log.debug("Restore of original request failed");
            }
            response.sendError(400);
            return false;
        }
        String contextPath = request.getContextPath();
        String requestURI = request.getDecodedRequestURI();
        boolean loginAction = requestURI.startsWith(contextPath) && requestURI.endsWith(Constants.FORM_ACTION);
        LoginConfig config = this.context.getLoginConfig();
        if (!loginAction) {
            if (request.getServletPath().length() == 0 && request.getPathInfo() == null) {
                StringBuilder location = new StringBuilder(requestURI);
                location.append('/');
                if (request.getQueryString() != null) {
                    location.append('?');
                    location.append(request.getQueryString());
                }
                response.sendRedirect(response.encodeRedirectURL(location.toString()));
                return false;
            }
            Session session3 = request.getSessionInternal(true);
            if (this.log.isDebugEnabled()) {
                this.log.debug("Save request in session '" + session3.getIdInternal() + "'");
            }
            try {
                saveRequest(request, session3);
                forwardToLoginPage(request, response, config);
                return false;
            } catch (IOException e) {
                this.log.debug("Request body too big to save during authentication");
                response.sendError(403, sm.getString("authenticator.requestBodyTooBig"));
                return false;
            }
        }
        request.getResponse().sendAcknowledgement();
        Realm realm = this.context.getRealm();
        if (this.characterEncoding != null) {
            request.setCharacterEncoding(this.characterEncoding);
        }
        String username2 = request.getParameter(Constants.FORM_USERNAME);
        String password2 = request.getParameter(Constants.FORM_PASSWORD);
        if (this.log.isDebugEnabled()) {
            this.log.debug("Authenticating username '" + username2 + "'");
        }
        Principal principal2 = realm.authenticate(username2, password2);
        if (principal2 == null) {
            forwardToErrorPage(request, response, config);
            return false;
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("Authentication of '" + username2 + "' was successful");
        }
        if (session == null) {
            session = request.getSessionInternal(false);
        }
        if (session == null) {
            if (this.containerLog.isDebugEnabled()) {
                this.containerLog.debug("User took so long to log on the session expired");
            }
            if (this.landingPage == null) {
                response.sendError(HttpServletResponse.SC_REQUEST_TIMEOUT, sm.getString("authenticator.sessionExpired"));
                return false;
            }
            String uri = request.getContextPath() + this.landingPage;
            SavedRequest saved = new SavedRequest();
            saved.setMethod("GET");
            saved.setRequestURI(uri);
            saved.setDecodedRequestURI(uri);
            request.getSessionInternal(true).setNote(Constants.FORM_REQUEST_NOTE, saved);
            response.sendRedirect(response.encodeRedirectURL(uri));
            return false;
        }
        session.setNote(Constants.FORM_PRINCIPAL_NOTE, principal2);
        session.setNote(Constants.SESS_USERNAME_NOTE, username2);
        session.setNote(Constants.SESS_PASSWORD_NOTE, password2);
        String requestURI2 = savedRequestURL(session);
        if (this.log.isDebugEnabled()) {
            this.log.debug("Redirecting to original '" + requestURI2 + "'");
        }
        if (requestURI2 == null) {
            if (this.landingPage == null) {
                response.sendError(400, sm.getString("authenticator.formlogin"));
                return false;
            }
            String uri2 = request.getContextPath() + this.landingPage;
            SavedRequest saved2 = new SavedRequest();
            saved2.setMethod("GET");
            saved2.setRequestURI(uri2);
            saved2.setDecodedRequestURI(uri2);
            session.setNote(Constants.FORM_REQUEST_NOTE, saved2);
            response.sendRedirect(response.encodeRedirectURL(uri2));
            return false;
        }
        Response internalResponse = request.getResponse();
        String location2 = response.encodeRedirectURL(requestURI2);
        if (org.apache.coyote.http11.Constants.HTTP_11.equals(request.getProtocol())) {
            internalResponse.sendRedirect(location2, 303);
            return false;
        }
        internalResponse.sendRedirect(location2, 302);
        return false;
    }

    @Override // org.apache.catalina.authenticator.AuthenticatorBase
    protected boolean isContinuationRequired(Request request) {
        SavedRequest savedRequest;
        String contextPath = this.context.getPath();
        String decodedRequestURI = request.getDecodedRequestURI();
        if (decodedRequestURI.startsWith(contextPath) && decodedRequestURI.endsWith(Constants.FORM_ACTION)) {
            return true;
        }
        Session session = request.getSessionInternal(false);
        if (session != null && (savedRequest = (SavedRequest) session.getNote(Constants.FORM_REQUEST_NOTE)) != null && decodedRequestURI.equals(savedRequest.getDecodedRequestURI())) {
            return true;
        }
        return false;
    }

    @Override // org.apache.catalina.authenticator.AuthenticatorBase
    protected String getAuthMethod() {
        return HttpServletRequest.FORM_AUTH;
    }

    protected void forwardToLoginPage(Request request, HttpServletResponse response, LoginConfig config) throws IOException {
        Session session;
        if (this.log.isDebugEnabled()) {
            this.log.debug(sm.getString("formAuthenticator.forwardLogin", request.getRequestURI(), request.getMethod(), config.getLoginPage(), this.context.getName()));
        }
        String loginPage = config.getLoginPage();
        if (loginPage == null || loginPage.length() == 0) {
            String msg = sm.getString("formAuthenticator.noLoginPage", this.context.getName());
            this.log.warn(msg);
            response.sendError(500, msg);
            return;
        }
        if (getChangeSessionIdOnAuthentication() && (session = request.getSessionInternal(false)) != null) {
            Manager manager = request.getContext().getManager();
            manager.changeSessionId(session);
            request.changeSessionId(session.getId());
        }
        String oldMethod = request.getMethod();
        request.getCoyoteRequest().method().setString("GET");
        RequestDispatcher disp = this.context.getServletContext().getRequestDispatcher(loginPage);
        try {
            if (this.context.fireRequestInitEvent(request.getRequest())) {
                disp.forward(request.getRequest(), response);
                this.context.fireRequestDestroyEvent(request.getRequest());
            }
        } catch (Throwable t) {
            try {
                ExceptionUtils.handleThrowable(t);
                String msg2 = sm.getString("formAuthenticator.forwardLoginFail");
                this.log.warn(msg2, t);
                request.setAttribute("javax.servlet.error.exception", t);
                response.sendError(500, msg2);
                request.getCoyoteRequest().method().setString(oldMethod);
            } finally {
                request.getCoyoteRequest().method().setString(oldMethod);
            }
        }
    }

    protected void forwardToErrorPage(Request request, HttpServletResponse response, LoginConfig config) throws IOException {
        String errorPage = config.getErrorPage();
        if (errorPage == null || errorPage.length() == 0) {
            String msg = sm.getString("formAuthenticator.noErrorPage", this.context.getName());
            this.log.warn(msg);
            response.sendError(500, msg);
            return;
        }
        RequestDispatcher disp = this.context.getServletContext().getRequestDispatcher(config.getErrorPage());
        try {
            if (this.context.fireRequestInitEvent(request.getRequest())) {
                disp.forward(request.getRequest(), response);
                this.context.fireRequestDestroyEvent(request.getRequest());
            }
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            String msg2 = sm.getString("formAuthenticator.forwardErrorFail");
            this.log.warn(msg2, t);
            request.setAttribute("javax.servlet.error.exception", t);
            response.sendError(500, msg2);
        }
    }

    protected boolean matchRequest(Request request) {
        SavedRequest sreq;
        String decodedRequestURI;
        Session session = request.getSessionInternal(false);
        if (session == null || (sreq = (SavedRequest) session.getNote(Constants.FORM_REQUEST_NOTE)) == null || session.getNote(Constants.FORM_PRINCIPAL_NOTE) == null || (decodedRequestURI = request.getDecodedRequestURI()) == null) {
            return false;
        }
        return decodedRequestURI.equals(sreq.getDecodedRequestURI());
    }

    protected boolean restoreRequest(Request request, Session session) throws IOException {
        SavedRequest saved = (SavedRequest) session.getNote(Constants.FORM_REQUEST_NOTE);
        session.removeNote(Constants.FORM_REQUEST_NOTE);
        session.removeNote(Constants.FORM_PRINCIPAL_NOTE);
        if (saved == null) {
            return false;
        }
        byte[] buffer = new byte[4096];
        InputStream is = request.createInputStream();
        do {
        } while (is.read(buffer) >= 0);
        request.clearCookies();
        Iterator<Cookie> cookies = saved.getCookies();
        while (cookies.hasNext()) {
            request.addCookie(cookies.next());
        }
        String method = saved.getMethod();
        MimeHeaders rmh = request.getCoyoteRequest().getMimeHeaders();
        rmh.recycle();
        boolean cacheable = "GET".equalsIgnoreCase(method) || WebContentGenerator.METHOD_HEAD.equalsIgnoreCase(method);
        Iterator<String> names = saved.getHeaderNames();
        while (names.hasNext()) {
            String name = names.next();
            if (!HttpHeaders.IF_MODIFIED_SINCE.equalsIgnoreCase(name) && (!cacheable || !HttpHeaders.IF_NONE_MATCH.equalsIgnoreCase(name))) {
                Iterator<String> values = saved.getHeaderValues(name);
                while (values.hasNext()) {
                    rmh.addValue(name).setString(values.next());
                }
            }
        }
        request.clearLocales();
        Iterator<Locale> locales = saved.getLocales();
        while (locales.hasNext()) {
            request.addLocale(locales.next());
        }
        request.getCoyoteRequest().getParameters().recycle();
        ByteChunk body = saved.getBody();
        if (body != null) {
            request.getCoyoteRequest().action(ActionCode.REQ_SET_BODY_REPLAY, body);
            MessageBytes contentType = MessageBytes.newInstance();
            String savedContentType = saved.getContentType();
            if (savedContentType == null && WebContentGenerator.METHOD_POST.equalsIgnoreCase(method)) {
                savedContentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE;
            }
            contentType.setString(savedContentType);
            request.getCoyoteRequest().setContentType(contentType);
        }
        request.getCoyoteRequest().method().setString(method);
        request.getRequestURI();
        request.getQueryString();
        request.getProtocol();
        return true;
    }

    protected void saveRequest(Request request, Session session) throws IOException {
        SavedRequest saved = new SavedRequest();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                saved.addCookie(cookie);
            }
        }
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            Enumeration<String> values = request.getHeaders(name);
            while (values.hasMoreElements()) {
                String value = values.nextElement();
                saved.addHeader(name, value);
            }
        }
        Enumeration<Locale> locales = request.getLocales();
        while (locales.hasMoreElements()) {
            Locale locale = locales.nextElement();
            saved.addLocale(locale);
        }
        request.getResponse().sendAcknowledgement();
        int maxSavePostSize = request.getConnector().getMaxSavePostSize();
        if (maxSavePostSize != 0) {
            ByteChunk body = new ByteChunk();
            body.setLimit(maxSavePostSize);
            byte[] buffer = new byte[4096];
            InputStream is = request.getInputStream();
            while (true) {
                int bytesRead = is.read(buffer);
                if (bytesRead < 0) {
                    break;
                }
                body.append(buffer, 0, bytesRead);
            }
            if (body.getLength() > 0) {
                saved.setContentType(request.getContentType());
                saved.setBody(body);
            }
        }
        saved.setMethod(request.getMethod());
        saved.setQueryString(request.getQueryString());
        saved.setRequestURI(request.getRequestURI());
        saved.setDecodedRequestURI(request.getDecodedRequestURI());
        session.setNote(Constants.FORM_REQUEST_NOTE, saved);
    }

    protected String savedRequestURL(Session session) {
        SavedRequest saved = (SavedRequest) session.getNote(Constants.FORM_REQUEST_NOTE);
        if (saved == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(saved.getRequestURI());
        if (saved.getQueryString() != null) {
            sb.append('?');
            sb.append(saved.getQueryString());
        }
        return sb.toString();
    }
}