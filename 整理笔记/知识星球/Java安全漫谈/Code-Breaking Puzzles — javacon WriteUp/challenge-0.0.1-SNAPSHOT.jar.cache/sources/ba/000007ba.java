package org.apache.catalina.connector;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.MultipartConfigElement;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestAttributeEvent;
import javax.servlet.ServletRequestAttributeListener;
import javax.servlet.ServletResponse;
import javax.servlet.SessionTrackingMode;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import javax.servlet.http.PushBuilder;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.Manager;
import org.apache.catalina.Realm;
import org.apache.catalina.Session;
import org.apache.catalina.TomcatPrincipal;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.ApplicationFilterChain;
import org.apache.catalina.core.ApplicationMapping;
import org.apache.catalina.core.ApplicationPart;
import org.apache.catalina.core.ApplicationPushBuilder;
import org.apache.catalina.core.ApplicationSessionCookieConfig;
import org.apache.catalina.core.AsyncContextImpl;
import org.apache.catalina.mapper.MappingData;
import org.apache.catalina.util.ParameterMap;
import org.apache.catalina.util.TLSUtil;
import org.apache.catalina.util.URLEncoder;
import org.apache.coyote.ActionCode;
import org.apache.coyote.Constants;
import org.apache.coyote.UpgradeToken;
import org.apache.coyote.http11.upgrade.InternalHttpUpgradeHandler;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.apache.tomcat.util.http.CookieProcessor;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.apache.tomcat.util.http.Parameters;
import org.apache.tomcat.util.http.RequestUtil;
import org.apache.tomcat.util.http.ServerCookie;
import org.apache.tomcat.util.http.ServerCookies;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;
import org.apache.tomcat.util.http.parser.AcceptLanguage;
import org.apache.tomcat.util.net.SSLSupport;
import org.apache.tomcat.util.res.StringManager;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/connector/Request.class */
public class Request implements HttpServletRequest {
    protected org.apache.coyote.Request coyoteRequest;
    protected static final int CACHED_POST_LEN = 8192;
    protected final Connector connector;
    private static final Log log = LogFactory.getLog(Request.class);
    @Deprecated
    protected static final TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");
    protected static final StringManager sm = StringManager.getManager(Request.class);
    @Deprecated
    private static final SimpleDateFormat[] formatsTemplate = {new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US), new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US), new SimpleDateFormat("EEE MMMM d HH:mm:ss yyyy", Locale.US)};
    protected static final Locale defaultLocale = Locale.getDefault();
    private static final Map<String, SpecialAttributeAdapter> specialAttributes = new HashMap();
    protected Cookie[] cookies = null;
    private final Map<String, Object> attributes = new ConcurrentHashMap();
    protected boolean sslAttributesParsed = false;
    protected final ArrayList<Locale> locales = new ArrayList<>();
    private final transient HashMap<String, Object> notes = new HashMap<>();
    protected String authType = null;
    protected DispatcherType internalDispatcherType = null;
    protected final InputBuffer inputBuffer = new InputBuffer();
    protected CoyoteInputStream inputStream = new CoyoteInputStream(this.inputBuffer);
    protected CoyoteReader reader = new CoyoteReader(this.inputBuffer);
    protected boolean usingInputStream = false;
    protected boolean usingReader = false;
    protected Principal userPrincipal = null;
    protected boolean parametersParsed = false;
    protected boolean cookiesParsed = false;
    protected boolean cookiesConverted = false;
    protected boolean secure = false;
    protected transient Subject subject = null;
    protected byte[] postData = null;
    protected ParameterMap<String, String[]> parameterMap = new ParameterMap<>();
    protected Collection<Part> parts = null;
    protected Exception partsParseException = null;
    protected Session session = null;
    protected Object requestDispatcherPath = null;
    protected boolean requestedSessionCookie = false;
    protected String requestedSessionId = null;
    protected boolean requestedSessionURL = false;
    protected boolean requestedSessionSSL = false;
    protected boolean localesParsed = false;
    protected int localPort = -1;
    protected String remoteAddr = null;
    protected String remoteHost = null;
    protected int remotePort = -1;
    protected String localAddr = null;
    protected String localName = null;
    private volatile AsyncContextImpl asyncContext = null;
    protected Boolean asyncSupported = null;
    private HttpServletRequest applicationRequest = null;
    protected FilterChain filterChain = null;
    protected final MappingData mappingData = new MappingData();
    private final ApplicationMapping applicationMapping = new ApplicationMapping(this.mappingData);
    protected RequestFacade facade = null;
    protected Response response = null;
    protected B2CConverter URIConverter = null;
    @Deprecated
    protected final SimpleDateFormat[] formats = new SimpleDateFormat[formatsTemplate.length];

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/connector/Request$SpecialAttributeAdapter.class */
    public interface SpecialAttributeAdapter {
        Object get(Request request, String str);

        void set(Request request, String str, Object obj);
    }

    static {
        SimpleDateFormat[] simpleDateFormatArr;
        specialAttributes.put(Globals.DISPATCHER_TYPE_ATTR, new SpecialAttributeAdapter() { // from class: org.apache.catalina.connector.Request.1
            @Override // org.apache.catalina.connector.Request.SpecialAttributeAdapter
            public Object get(Request request, String name) {
                return request.internalDispatcherType == null ? DispatcherType.REQUEST : request.internalDispatcherType;
            }

            @Override // org.apache.catalina.connector.Request.SpecialAttributeAdapter
            public void set(Request request, String name, Object value) {
                request.internalDispatcherType = (DispatcherType) value;
            }
        });
        specialAttributes.put(Globals.DISPATCHER_REQUEST_PATH_ATTR, new SpecialAttributeAdapter() { // from class: org.apache.catalina.connector.Request.2
            @Override // org.apache.catalina.connector.Request.SpecialAttributeAdapter
            public Object get(Request request, String name) {
                if (request.requestDispatcherPath == null) {
                    return request.getRequestPathMB().toString();
                }
                return request.requestDispatcherPath.toString();
            }

            @Override // org.apache.catalina.connector.Request.SpecialAttributeAdapter
            public void set(Request request, String name, Object value) {
                request.requestDispatcherPath = value;
            }
        });
        specialAttributes.put(Globals.ASYNC_SUPPORTED_ATTR, new SpecialAttributeAdapter() { // from class: org.apache.catalina.connector.Request.3
            @Override // org.apache.catalina.connector.Request.SpecialAttributeAdapter
            public Object get(Request request, String name) {
                return request.asyncSupported;
            }

            @Override // org.apache.catalina.connector.Request.SpecialAttributeAdapter
            public void set(Request request, String name, Object value) {
                Boolean oldValue = request.asyncSupported;
                request.asyncSupported = (Boolean) value;
                request.notifyAttributeAssigned(name, value, oldValue);
            }
        });
        specialAttributes.put(Globals.GSS_CREDENTIAL_ATTR, new SpecialAttributeAdapter() { // from class: org.apache.catalina.connector.Request.4
            @Override // org.apache.catalina.connector.Request.SpecialAttributeAdapter
            public Object get(Request request, String name) {
                if (request.userPrincipal instanceof TomcatPrincipal) {
                    return ((TomcatPrincipal) request.userPrincipal).getGssCredential();
                }
                return null;
            }

            @Override // org.apache.catalina.connector.Request.SpecialAttributeAdapter
            public void set(Request request, String name, Object value) {
            }
        });
        specialAttributes.put(Globals.PARAMETER_PARSE_FAILED_ATTR, new SpecialAttributeAdapter() { // from class: org.apache.catalina.connector.Request.5
            @Override // org.apache.catalina.connector.Request.SpecialAttributeAdapter
            public Object get(Request request, String name) {
                if (request.getCoyoteRequest().getParameters().isParseFailed()) {
                    return Boolean.TRUE;
                }
                return null;
            }

            @Override // org.apache.catalina.connector.Request.SpecialAttributeAdapter
            public void set(Request request, String name, Object value) {
            }
        });
        specialAttributes.put(Globals.PARAMETER_PARSE_FAILED_REASON_ATTR, new SpecialAttributeAdapter() { // from class: org.apache.catalina.connector.Request.6
            @Override // org.apache.catalina.connector.Request.SpecialAttributeAdapter
            public Object get(Request request, String name) {
                return request.getCoyoteRequest().getParameters().getParseFailedReason();
            }

            @Override // org.apache.catalina.connector.Request.SpecialAttributeAdapter
            public void set(Request request, String name, Object value) {
            }
        });
        specialAttributes.put("org.apache.tomcat.sendfile.support", new SpecialAttributeAdapter() { // from class: org.apache.catalina.connector.Request.7
            @Override // org.apache.catalina.connector.Request.SpecialAttributeAdapter
            public Object get(Request request, String name) {
                return Boolean.valueOf(request.getConnector().getProtocolHandler().isSendfileSupported() && request.getCoyoteRequest().getSendfile());
            }

            @Override // org.apache.catalina.connector.Request.SpecialAttributeAdapter
            public void set(Request request, String name, Object value) {
            }
        });
        for (SimpleDateFormat sdf : formatsTemplate) {
            sdf.setTimeZone(GMT_ZONE);
        }
    }

    public Request(Connector connector) {
        this.connector = connector;
        for (int i = 0; i < this.formats.length; i++) {
            this.formats[i] = (SimpleDateFormat) formatsTemplate[i].clone();
        }
    }

    public void setCoyoteRequest(org.apache.coyote.Request coyoteRequest) {
        this.coyoteRequest = coyoteRequest;
        this.inputBuffer.setRequest(coyoteRequest);
    }

    public org.apache.coyote.Request getCoyoteRequest() {
        return this.coyoteRequest;
    }

    public void addPathParameter(String name, String value) {
        this.coyoteRequest.addPathParameter(name, value);
    }

    public String getPathParameter(String name) {
        return this.coyoteRequest.getPathParameter(name);
    }

    public void setAsyncSupported(boolean asyncSupported) {
        this.asyncSupported = Boolean.valueOf(asyncSupported);
    }

    public void recycle() {
        this.internalDispatcherType = null;
        this.requestDispatcherPath = null;
        this.authType = null;
        this.inputBuffer.recycle();
        this.usingInputStream = false;
        this.usingReader = false;
        this.userPrincipal = null;
        this.subject = null;
        this.parametersParsed = false;
        if (this.parts != null) {
            for (Part part : this.parts) {
                try {
                    part.delete();
                } catch (IOException e) {
                }
            }
            this.parts = null;
        }
        this.partsParseException = null;
        this.locales.clear();
        this.localesParsed = false;
        this.secure = false;
        this.remoteAddr = null;
        this.remoteHost = null;
        this.remotePort = -1;
        this.localPort = -1;
        this.localAddr = null;
        this.localName = null;
        this.attributes.clear();
        this.sslAttributesParsed = false;
        this.notes.clear();
        recycleSessionInfo();
        recycleCookieInfo(false);
        if (Globals.IS_SECURITY_ENABLED || Connector.RECYCLE_FACADES) {
            this.parameterMap = new ParameterMap<>();
        } else {
            this.parameterMap.setLocked(false);
            this.parameterMap.clear();
        }
        this.mappingData.recycle();
        this.applicationMapping.recycle();
        this.applicationRequest = null;
        if (Globals.IS_SECURITY_ENABLED || Connector.RECYCLE_FACADES) {
            if (this.facade != null) {
                this.facade.clear();
                this.facade = null;
            }
            if (this.inputStream != null) {
                this.inputStream.clear();
                this.inputStream = null;
            }
            if (this.reader != null) {
                this.reader.clear();
                this.reader = null;
            }
        }
        this.asyncSupported = null;
        if (this.asyncContext != null) {
            this.asyncContext.recycle();
        }
        this.asyncContext = null;
    }

    public void recycleSessionInfo() {
        if (this.session != null) {
            try {
                this.session.endAccess();
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                log.warn(sm.getString("coyoteRequest.sessionEndAccessFail"), t);
            }
        }
        this.session = null;
        this.requestedSessionCookie = false;
        this.requestedSessionId = null;
        this.requestedSessionURL = false;
        this.requestedSessionSSL = false;
    }

    public void recycleCookieInfo(boolean recycleCoyote) {
        this.cookiesParsed = false;
        this.cookiesConverted = false;
        this.cookies = null;
        if (recycleCoyote) {
            getCoyoteRequest().getCookies().recycle();
        }
    }

    public Connector getConnector() {
        return this.connector;
    }

    public Context getContext() {
        return this.mappingData.context;
    }

    public FilterChain getFilterChain() {
        return this.filterChain;
    }

    public void setFilterChain(FilterChain filterChain) {
        this.filterChain = filterChain;
    }

    public Host getHost() {
        return this.mappingData.host;
    }

    public MappingData getMappingData() {
        return this.mappingData;
    }

    public HttpServletRequest getRequest() {
        if (this.facade == null) {
            this.facade = new RequestFacade(this);
        }
        if (this.applicationRequest == null) {
            this.applicationRequest = this.facade;
        }
        return this.applicationRequest;
    }

    public void setRequest(HttpServletRequest applicationRequest) {
        ServletRequest r;
        ServletRequest servletRequest = applicationRequest;
        while (true) {
            r = servletRequest;
            if (!(r instanceof HttpServletRequestWrapper)) {
                break;
            }
            servletRequest = ((HttpServletRequestWrapper) r).getRequest();
        }
        if (r != this.facade) {
            throw new IllegalArgumentException(sm.getString("request.illegalWrap"));
        }
        this.applicationRequest = applicationRequest;
    }

    public Response getResponse() {
        return this.response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public InputStream getStream() {
        if (this.inputStream == null) {
            this.inputStream = new CoyoteInputStream(this.inputBuffer);
        }
        return this.inputStream;
    }

    public B2CConverter getURIConverter() {
        return this.URIConverter;
    }

    public void setURIConverter(B2CConverter URIConverter) {
        this.URIConverter = URIConverter;
    }

    public Wrapper getWrapper() {
        return this.mappingData.wrapper;
    }

    public ServletInputStream createInputStream() throws IOException {
        if (this.inputStream == null) {
            this.inputStream = new CoyoteInputStream(this.inputBuffer);
        }
        return this.inputStream;
    }

    public void finishRequest() throws IOException {
        if (this.response.getStatus() == 413) {
            checkSwallowInput();
        }
    }

    public Object getNote(String name) {
        return this.notes.get(name);
    }

    public void removeNote(String name) {
        this.notes.remove(name);
    }

    public void setLocalPort(int port) {
        this.localPort = port;
    }

    public void setNote(String name, Object value) {
        this.notes.put(name, value);
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public void setServerPort(int port) {
        this.coyoteRequest.setServerPort(port);
    }

    @Override // javax.servlet.ServletRequest
    public Object getAttribute(String name) {
        SpecialAttributeAdapter adapter = specialAttributes.get(name);
        if (adapter != null) {
            return adapter.get(this, name);
        }
        Object attr = this.attributes.get(name);
        if (attr != null) {
            return attr;
        }
        Object attr2 = this.coyoteRequest.getAttribute(name);
        if (attr2 != null) {
            return attr2;
        }
        if (TLSUtil.isTLSRequestAttribute(name)) {
            this.coyoteRequest.action(ActionCode.REQ_SSL_ATTRIBUTE, this.coyoteRequest);
            Object attr3 = this.coyoteRequest.getAttribute("javax.servlet.request.X509Certificate");
            if (attr3 != null) {
                this.attributes.put("javax.servlet.request.X509Certificate", attr3);
            }
            Object attr4 = this.coyoteRequest.getAttribute("javax.servlet.request.cipher_suite");
            if (attr4 != null) {
                this.attributes.put("javax.servlet.request.cipher_suite", attr4);
            }
            Object attr5 = this.coyoteRequest.getAttribute("javax.servlet.request.key_size");
            if (attr5 != null) {
                this.attributes.put("javax.servlet.request.key_size", attr5);
            }
            Object attr6 = this.coyoteRequest.getAttribute("javax.servlet.request.ssl_session_id");
            if (attr6 != null) {
                this.attributes.put("javax.servlet.request.ssl_session_id", attr6);
            }
            Object attr7 = this.coyoteRequest.getAttribute("javax.servlet.request.ssl_session_mgr");
            if (attr7 != null) {
                this.attributes.put("javax.servlet.request.ssl_session_mgr", attr7);
            }
            Object attr8 = this.coyoteRequest.getAttribute(SSLSupport.PROTOCOL_VERSION_KEY);
            if (attr8 != null) {
                this.attributes.put(SSLSupport.PROTOCOL_VERSION_KEY, attr8);
            }
            attr2 = this.attributes.get(name);
            this.sslAttributesParsed = true;
        }
        return attr2;
    }

    @Override // javax.servlet.ServletRequest
    public long getContentLengthLong() {
        return this.coyoteRequest.getContentLengthLong();
    }

    @Override // javax.servlet.ServletRequest
    public Enumeration<String> getAttributeNames() {
        if (isSecure() && !this.sslAttributesParsed) {
            getAttribute("javax.servlet.request.X509Certificate");
        }
        Set<String> names = new HashSet<>();
        names.addAll(this.attributes.keySet());
        return Collections.enumeration(names);
    }

    @Override // javax.servlet.ServletRequest
    public String getCharacterEncoding() {
        String characterEncoding = this.coyoteRequest.getCharacterEncoding();
        if (characterEncoding != null) {
            return characterEncoding;
        }
        Context context = getContext();
        if (context != null) {
            return context.getRequestCharacterEncoding();
        }
        return null;
    }

    private Charset getCharset() {
        String encoding;
        Charset charset = null;
        try {
            charset = this.coyoteRequest.getCharset();
        } catch (UnsupportedEncodingException e) {
        }
        if (charset != null) {
            return charset;
        }
        Context context = getContext();
        if (context != null && (encoding = context.getRequestCharacterEncoding()) != null) {
            try {
                return B2CConverter.getCharset(encoding);
            } catch (UnsupportedEncodingException e2) {
            }
        }
        return Constants.DEFAULT_BODY_CHARSET;
    }

    @Override // javax.servlet.ServletRequest
    public int getContentLength() {
        return this.coyoteRequest.getContentLength();
    }

    @Override // javax.servlet.ServletRequest
    public String getContentType() {
        return this.coyoteRequest.getContentType();
    }

    public void setContentType(String contentType) {
        this.coyoteRequest.setContentType(contentType);
    }

    @Override // javax.servlet.ServletRequest
    public ServletInputStream getInputStream() throws IOException {
        if (this.usingReader) {
            throw new IllegalStateException(sm.getString("coyoteRequest.getInputStream.ise"));
        }
        this.usingInputStream = true;
        if (this.inputStream == null) {
            this.inputStream = new CoyoteInputStream(this.inputBuffer);
        }
        return this.inputStream;
    }

    @Override // javax.servlet.ServletRequest
    public Locale getLocale() {
        if (!this.localesParsed) {
            parseLocales();
        }
        if (this.locales.size() > 0) {
            return this.locales.get(0);
        }
        return defaultLocale;
    }

    @Override // javax.servlet.ServletRequest
    public Enumeration<Locale> getLocales() {
        if (!this.localesParsed) {
            parseLocales();
        }
        if (this.locales.size() > 0) {
            return Collections.enumeration(this.locales);
        }
        ArrayList<Locale> results = new ArrayList<>();
        results.add(defaultLocale);
        return Collections.enumeration(results);
    }

    @Override // javax.servlet.ServletRequest
    public String getParameter(String name) {
        if (!this.parametersParsed) {
            parseParameters();
        }
        return this.coyoteRequest.getParameters().getParameter(name);
    }

    @Override // javax.servlet.ServletRequest
    public Map<String, String[]> getParameterMap() {
        if (this.parameterMap.isLocked()) {
            return this.parameterMap;
        }
        Enumeration<String> enumeration = getParameterNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String[] values = getParameterValues(name);
            this.parameterMap.put(name, values);
        }
        this.parameterMap.setLocked(true);
        return this.parameterMap;
    }

    @Override // javax.servlet.ServletRequest
    public Enumeration<String> getParameterNames() {
        if (!this.parametersParsed) {
            parseParameters();
        }
        return this.coyoteRequest.getParameters().getParameterNames();
    }

    @Override // javax.servlet.ServletRequest
    public String[] getParameterValues(String name) {
        if (!this.parametersParsed) {
            parseParameters();
        }
        return this.coyoteRequest.getParameters().getParameterValues(name);
    }

    @Override // javax.servlet.ServletRequest
    public String getProtocol() {
        return this.coyoteRequest.protocol().toString();
    }

    @Override // javax.servlet.ServletRequest
    public BufferedReader getReader() throws IOException {
        if (this.usingInputStream) {
            throw new IllegalStateException(sm.getString("coyoteRequest.getReader.ise"));
        }
        this.usingReader = true;
        this.inputBuffer.checkConverter();
        if (this.reader == null) {
            this.reader = new CoyoteReader(this.inputBuffer);
        }
        return this.reader;
    }

    @Override // javax.servlet.ServletRequest
    @Deprecated
    public String getRealPath(String path) {
        ServletContext servletContext;
        Context context = getContext();
        if (context == null || (servletContext = context.getServletContext()) == null) {
            return null;
        }
        try {
            return servletContext.getRealPath(path);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override // javax.servlet.ServletRequest
    public String getRemoteAddr() {
        if (this.remoteAddr == null) {
            this.coyoteRequest.action(ActionCode.REQ_HOST_ADDR_ATTRIBUTE, this.coyoteRequest);
            this.remoteAddr = this.coyoteRequest.remoteAddr().toString();
        }
        return this.remoteAddr;
    }

    @Override // javax.servlet.ServletRequest
    public String getRemoteHost() {
        if (this.remoteHost == null) {
            if (!this.connector.getEnableLookups()) {
                this.remoteHost = getRemoteAddr();
            } else {
                this.coyoteRequest.action(ActionCode.REQ_HOST_ATTRIBUTE, this.coyoteRequest);
                this.remoteHost = this.coyoteRequest.remoteHost().toString();
            }
        }
        return this.remoteHost;
    }

    @Override // javax.servlet.ServletRequest
    public int getRemotePort() {
        if (this.remotePort == -1) {
            this.coyoteRequest.action(ActionCode.REQ_REMOTEPORT_ATTRIBUTE, this.coyoteRequest);
            this.remotePort = this.coyoteRequest.getRemotePort();
        }
        return this.remotePort;
    }

    @Override // javax.servlet.ServletRequest
    public String getLocalName() {
        if (this.localName == null) {
            this.coyoteRequest.action(ActionCode.REQ_LOCAL_NAME_ATTRIBUTE, this.coyoteRequest);
            this.localName = this.coyoteRequest.localName().toString();
        }
        return this.localName;
    }

    @Override // javax.servlet.ServletRequest
    public String getLocalAddr() {
        if (this.localAddr == null) {
            this.coyoteRequest.action(ActionCode.REQ_LOCAL_ADDR_ATTRIBUTE, this.coyoteRequest);
            this.localAddr = this.coyoteRequest.localAddr().toString();
        }
        return this.localAddr;
    }

    @Override // javax.servlet.ServletRequest
    public int getLocalPort() {
        if (this.localPort == -1) {
            this.coyoteRequest.action(ActionCode.REQ_LOCALPORT_ATTRIBUTE, this.coyoteRequest);
            this.localPort = this.coyoteRequest.getLocalPort();
        }
        return this.localPort;
    }

    @Override // javax.servlet.ServletRequest
    public RequestDispatcher getRequestDispatcher(String path) {
        String requestPath;
        String relative;
        Context context = getContext();
        if (context == null || path == null) {
            return null;
        }
        if (path.startsWith("/")) {
            return context.getServletContext().getRequestDispatcher(path);
        }
        String servletPath = (String) getAttribute("javax.servlet.include.servlet_path");
        if (servletPath == null) {
            servletPath = getServletPath();
        }
        String pathInfo = getPathInfo();
        if (pathInfo == null) {
            requestPath = servletPath;
        } else {
            requestPath = servletPath + pathInfo;
        }
        int pos = requestPath.lastIndexOf(47);
        if (context.getDispatchersUseEncodedPaths()) {
            if (pos >= 0) {
                relative = URLEncoder.DEFAULT.encode(requestPath.substring(0, pos + 1), StandardCharsets.UTF_8) + path;
            } else {
                relative = URLEncoder.DEFAULT.encode(requestPath, StandardCharsets.UTF_8) + path;
            }
        } else if (pos >= 0) {
            relative = requestPath.substring(0, pos + 1) + path;
        } else {
            relative = requestPath + path;
        }
        return context.getServletContext().getRequestDispatcher(relative);
    }

    @Override // javax.servlet.ServletRequest
    public String getScheme() {
        return this.coyoteRequest.scheme().toString();
    }

    @Override // javax.servlet.ServletRequest
    public String getServerName() {
        return this.coyoteRequest.serverName().toString();
    }

    @Override // javax.servlet.ServletRequest
    public int getServerPort() {
        return this.coyoteRequest.getServerPort();
    }

    @Override // javax.servlet.ServletRequest
    public boolean isSecure() {
        return this.secure;
    }

    @Override // javax.servlet.ServletRequest
    public void removeAttribute(String name) {
        if (name.startsWith("org.apache.tomcat.")) {
            this.coyoteRequest.getAttributes().remove(name);
        }
        boolean found = this.attributes.containsKey(name);
        if (found) {
            Object value = this.attributes.get(name);
            this.attributes.remove(name);
            notifyAttributeRemoved(name, value);
        }
    }

    @Override // javax.servlet.ServletRequest
    public void setAttribute(String name, Object value) {
        if (name == null) {
            throw new IllegalArgumentException(sm.getString("coyoteRequest.setAttribute.namenull"));
        }
        if (value == null) {
            removeAttribute(name);
            return;
        }
        SpecialAttributeAdapter adapter = specialAttributes.get(name);
        if (adapter != null) {
            adapter.set(this, name, value);
            return;
        }
        if (Globals.IS_SECURITY_ENABLED && name.equals("org.apache.tomcat.sendfile.filename")) {
            try {
                String canonicalPath = new File(value.toString()).getCanonicalPath();
                System.getSecurityManager().checkRead(canonicalPath);
                value = canonicalPath;
            } catch (IOException e) {
                throw new SecurityException(sm.getString("coyoteRequest.sendfileNotCanonical", value), e);
            }
        }
        Object oldValue = this.attributes.put(name, value);
        if (name.startsWith("org.apache.tomcat.")) {
            this.coyoteRequest.setAttribute(name, value);
        }
        notifyAttributeAssigned(name, value, oldValue);
    }

    public void notifyAttributeAssigned(String name, Object value, Object oldValue) {
        Object[] listeners;
        ServletRequestAttributeEvent event;
        Context context = getContext();
        if (context == null || (listeners = context.getApplicationEventListeners()) == null || listeners.length == 0) {
            return;
        }
        boolean replaced = oldValue != null;
        if (replaced) {
            event = new ServletRequestAttributeEvent(context.getServletContext(), getRequest(), name, oldValue);
        } else {
            event = new ServletRequestAttributeEvent(context.getServletContext(), getRequest(), name, value);
        }
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] instanceof ServletRequestAttributeListener) {
                ServletRequestAttributeListener listener = (ServletRequestAttributeListener) listeners[i];
                if (replaced) {
                    try {
                        listener.attributeReplaced(event);
                    } catch (Throwable t) {
                        ExceptionUtils.handleThrowable(t);
                        this.attributes.put("javax.servlet.error.exception", t);
                        context.getLogger().error(sm.getString("coyoteRequest.attributeEvent"), t);
                    }
                } else {
                    listener.attributeAdded(event);
                }
            }
        }
    }

    private void notifyAttributeRemoved(String name, Object value) {
        Context context = getContext();
        Object[] listeners = context.getApplicationEventListeners();
        if (listeners == null || listeners.length == 0) {
            return;
        }
        ServletRequestAttributeEvent event = new ServletRequestAttributeEvent(context.getServletContext(), getRequest(), name, value);
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] instanceof ServletRequestAttributeListener) {
                ServletRequestAttributeListener listener = (ServletRequestAttributeListener) listeners[i];
                try {
                    listener.attributeRemoved(event);
                } catch (Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                    this.attributes.put("javax.servlet.error.exception", t);
                    context.getLogger().error(sm.getString("coyoteRequest.attributeEvent"), t);
                }
            }
        }
    }

    @Override // javax.servlet.ServletRequest
    public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
        if (this.usingReader) {
            return;
        }
        Charset charset = B2CConverter.getCharset(enc);
        this.coyoteRequest.setCharset(charset);
    }

    @Override // javax.servlet.ServletRequest
    public ServletContext getServletContext() {
        return getContext().getServletContext();
    }

    @Override // javax.servlet.ServletRequest
    public AsyncContext startAsync() {
        return startAsync(getRequest(), this.response.getResponse());
    }

    @Override // javax.servlet.ServletRequest
    public AsyncContext startAsync(ServletRequest request, ServletResponse response) {
        if (!isAsyncSupported()) {
            IllegalStateException ise = new IllegalStateException(sm.getString("request.asyncNotSupported"));
            log.warn(sm.getString("coyoteRequest.noAsync", StringUtils.join(getNonAsyncClassNames())), ise);
            throw ise;
        }
        if (this.asyncContext == null) {
            this.asyncContext = new AsyncContextImpl(this);
        }
        this.asyncContext.setStarted(getContext(), request, response, request == getRequest() && response == getResponse().getResponse());
        this.asyncContext.setTimeout(getConnector().getAsyncTimeout());
        return this.asyncContext;
    }

    private Set<String> getNonAsyncClassNames() {
        Set<String> result = new HashSet<>();
        Wrapper wrapper = getWrapper();
        if (!wrapper.isAsyncSupported()) {
            result.add(wrapper.getServletClass());
        }
        FilterChain filterChain = getFilterChain();
        if (filterChain instanceof ApplicationFilterChain) {
            ((ApplicationFilterChain) filterChain).findNonAsyncFilters(result);
        } else {
            result.add(sm.getString("coyoteRequest.filterAsyncSupportUnknown"));
        }
        Container container = wrapper;
        while (true) {
            Container c = container;
            if (c != null) {
                c.getPipeline().findNonAsyncValves(result);
                container = c.getParent();
            } else {
                return result;
            }
        }
    }

    @Override // javax.servlet.ServletRequest
    public boolean isAsyncStarted() {
        if (this.asyncContext == null) {
            return false;
        }
        return this.asyncContext.isStarted();
    }

    public boolean isAsyncDispatching() {
        if (this.asyncContext == null) {
            return false;
        }
        AtomicBoolean result = new AtomicBoolean(false);
        this.coyoteRequest.action(ActionCode.ASYNC_IS_DISPATCHING, result);
        return result.get();
    }

    public boolean isAsyncCompleting() {
        if (this.asyncContext == null) {
            return false;
        }
        AtomicBoolean result = new AtomicBoolean(false);
        this.coyoteRequest.action(ActionCode.ASYNC_IS_COMPLETING, result);
        return result.get();
    }

    public boolean isAsync() {
        if (this.asyncContext == null) {
            return false;
        }
        AtomicBoolean result = new AtomicBoolean(false);
        this.coyoteRequest.action(ActionCode.ASYNC_IS_ASYNC, result);
        return result.get();
    }

    @Override // javax.servlet.ServletRequest
    public boolean isAsyncSupported() {
        if (this.asyncSupported == null) {
            return true;
        }
        return this.asyncSupported.booleanValue();
    }

    @Override // javax.servlet.ServletRequest
    public AsyncContext getAsyncContext() {
        if (!isAsyncStarted()) {
            throw new IllegalStateException(sm.getString("request.notAsync"));
        }
        return this.asyncContext;
    }

    public AsyncContextImpl getAsyncContextInternal() {
        return this.asyncContext;
    }

    @Override // javax.servlet.ServletRequest
    public DispatcherType getDispatcherType() {
        if (this.internalDispatcherType == null) {
            return DispatcherType.REQUEST;
        }
        return this.internalDispatcherType;
    }

    public void addCookie(Cookie cookie) {
        if (!this.cookiesConverted) {
            convertCookies();
        }
        int size = 0;
        if (this.cookies != null) {
            size = this.cookies.length;
        }
        Cookie[] newCookies = new Cookie[size + 1];
        if (this.cookies != null) {
            System.arraycopy(this.cookies, 0, newCookies, 0, size);
        }
        newCookies[size] = cookie;
        this.cookies = newCookies;
    }

    public void addLocale(Locale locale) {
        this.locales.add(locale);
    }

    public void clearCookies() {
        this.cookiesParsed = true;
        this.cookiesConverted = true;
        this.cookies = null;
    }

    public void clearLocales() {
        this.locales.clear();
    }

    public void setAuthType(String type) {
        this.authType = type;
    }

    public void setPathInfo(String path) {
        this.mappingData.pathInfo.setString(path);
    }

    public void setRequestedSessionCookie(boolean flag) {
        this.requestedSessionCookie = flag;
    }

    public void setRequestedSessionId(String id) {
        this.requestedSessionId = id;
    }

    public void setRequestedSessionURL(boolean flag) {
        this.requestedSessionURL = flag;
    }

    public void setRequestedSessionSSL(boolean flag) {
        this.requestedSessionSSL = flag;
    }

    public String getDecodedRequestURI() {
        return this.coyoteRequest.decodedURI().toString();
    }

    public MessageBytes getDecodedRequestURIMB() {
        return this.coyoteRequest.decodedURI();
    }

    public void setUserPrincipal(Principal principal) {
        if (Globals.IS_SECURITY_ENABLED && principal != null) {
            if (this.subject == null) {
                HttpSession session = getSession(false);
                if (session == null) {
                    this.subject = newSubject(principal);
                } else {
                    this.subject = (Subject) session.getAttribute(Globals.SUBJECT_ATTR);
                    if (this.subject == null) {
                        this.subject = newSubject(principal);
                        session.setAttribute(Globals.SUBJECT_ATTR, this.subject);
                    } else {
                        this.subject.getPrincipals().add(principal);
                    }
                }
            } else {
                this.subject.getPrincipals().add(principal);
            }
        }
        this.userPrincipal = principal;
    }

    private Subject newSubject(Principal principal) {
        Subject result = new Subject();
        result.getPrincipals().add(principal);
        return result;
    }

    @Override // javax.servlet.http.HttpServletRequest
    public boolean isTrailerFieldsReady() {
        return this.coyoteRequest.isTrailerFieldsReady();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Map<String, String> getTrailerFields() {
        if (!isTrailerFieldsReady()) {
            throw new IllegalStateException(sm.getString("coyoteRequest.trailersNotReady"));
        }
        Map<String, String> result = new HashMap<>();
        result.putAll(this.coyoteRequest.getTrailerFields());
        return result;
    }

    @Override // javax.servlet.http.HttpServletRequest
    public PushBuilder newPushBuilder() {
        return newPushBuilder(this);
    }

    public PushBuilder newPushBuilder(HttpServletRequest request) {
        AtomicBoolean result = new AtomicBoolean();
        this.coyoteRequest.action(ActionCode.IS_PUSH_SUPPORTED, result);
        if (result.get()) {
            return new ApplicationPushBuilder(this, request);
        }
        return null;
    }

    @Override // javax.servlet.http.HttpServletRequest
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> httpUpgradeHandlerClass) throws IOException, ServletException {
        T handler;
        InstanceManager instanceManager = null;
        try {
            if (InternalHttpUpgradeHandler.class.isAssignableFrom(httpUpgradeHandlerClass)) {
                handler = httpUpgradeHandlerClass.getConstructor(new Class[0]).newInstance(new Object[0]);
            } else {
                instanceManager = getContext().getInstanceManager();
                handler = (HttpUpgradeHandler) instanceManager.newInstance((Class<?>) httpUpgradeHandlerClass);
            }
            UpgradeToken upgradeToken = new UpgradeToken(handler, getContext(), instanceManager);
            this.coyoteRequest.action(ActionCode.UPGRADE, upgradeToken);
            this.response.setStatus(101);
            return (T) handler;
        } catch (ReflectiveOperationException | NamingException | IllegalArgumentException | SecurityException e) {
            throw new ServletException(e);
        }
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getAuthType() {
        return this.authType;
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getContextPath() {
        String candidate;
        boolean match;
        String substring;
        int lastSlash = this.mappingData.contextSlashCount;
        if (lastSlash == 0) {
            return "";
        }
        String canonicalContextPath = getServletContext().getContextPath();
        String uri = getRequestURI();
        int pos = 0;
        if (!getContext().getAllowMultipleLeadingForwardSlashInPath()) {
            do {
                pos++;
                if (pos >= uri.length()) {
                    break;
                }
            } while (uri.charAt(pos) == '/');
            pos--;
            uri = uri.substring(pos);
        }
        char[] uriChars = uri.toCharArray();
        while (lastSlash > 0) {
            pos = nextSlash(uriChars, pos + 1);
            if (pos == -1) {
                break;
            }
            lastSlash--;
        }
        if (pos == -1) {
            candidate = uri;
        } else {
            candidate = uri.substring(0, pos);
        }
        boolean equals = canonicalContextPath.equals(RequestUtil.normalize(UDecoder.URLDecode(removePathParameters(candidate), this.connector.getURICharset())));
        while (true) {
            match = equals;
            if (match || pos == -1) {
                break;
            }
            pos = nextSlash(uriChars, pos + 1);
            if (pos == -1) {
                substring = uri;
            } else {
                substring = uri.substring(0, pos);
            }
            String candidate2 = substring;
            equals = canonicalContextPath.equals(RequestUtil.normalize(UDecoder.URLDecode(removePathParameters(candidate2), this.connector.getURICharset())));
        }
        if (match) {
            if (pos == -1) {
                return uri;
            }
            return uri.substring(0, pos);
        }
        throw new IllegalStateException(sm.getString("coyoteRequest.getContextPath.ise", canonicalContextPath, uri));
    }

    private String removePathParameters(String input) {
        int nextSemiColon = input.indexOf(59);
        if (nextSemiColon == -1) {
            return input;
        }
        StringBuilder result = new StringBuilder(input.length());
        result.append(input.substring(0, nextSemiColon));
        while (true) {
            int nextSlash = input.indexOf(47, nextSemiColon);
            if (nextSlash == -1) {
                break;
            }
            nextSemiColon = input.indexOf(59, nextSlash);
            if (nextSemiColon == -1) {
                result.append(input.substring(nextSlash));
                break;
            }
            result.append(input.substring(nextSlash, nextSemiColon));
        }
        return result.toString();
    }

    private int nextSlash(char[] uri, int startPos) {
        int len = uri.length;
        for (int pos = startPos; pos < len; pos++) {
            if (uri[pos] == '/') {
                return pos;
            }
            if (UDecoder.ALLOW_ENCODED_SLASH && uri[pos] == '%' && pos + 2 < len && uri[pos + 1] == '2' && (uri[pos + 2] == 'f' || uri[pos + 2] == 'F')) {
                return pos;
            }
        }
        return -1;
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Cookie[] getCookies() {
        if (!this.cookiesConverted) {
            convertCookies();
        }
        return this.cookies;
    }

    public ServerCookies getServerCookies() {
        parseCookies();
        return this.coyoteRequest.getCookies();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public long getDateHeader(String name) {
        String value = getHeader(name);
        if (value == null) {
            return -1L;
        }
        long result = FastHttpDateFormat.parseDate(value);
        if (result != -1) {
            return result;
        }
        throw new IllegalArgumentException(value);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getHeader(String name) {
        return this.coyoteRequest.getHeader(name);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Enumeration<String> getHeaders(String name) {
        return this.coyoteRequest.getMimeHeaders().values(name);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Enumeration<String> getHeaderNames() {
        return this.coyoteRequest.getMimeHeaders().names();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public int getIntHeader(String name) {
        String value = getHeader(name);
        if (value == null) {
            return -1;
        }
        return Integer.parseInt(value);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public HttpServletMapping getHttpServletMapping() {
        return this.applicationMapping.getHttpServletMapping();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getMethod() {
        return this.coyoteRequest.method().toString();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getPathInfo() {
        return this.mappingData.pathInfo.toString();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getPathTranslated() {
        Context context = getContext();
        if (context == null || getPathInfo() == null) {
            return null;
        }
        return context.getServletContext().getRealPath(getPathInfo());
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getQueryString() {
        return this.coyoteRequest.queryString().toString();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getRemoteUser() {
        if (this.userPrincipal == null) {
            return null;
        }
        return this.userPrincipal.getName();
    }

    public MessageBytes getRequestPathMB() {
        return this.mappingData.requestPath;
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getRequestedSessionId() {
        return this.requestedSessionId;
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getRequestURI() {
        return this.coyoteRequest.requestURI().toString();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public StringBuffer getRequestURL() {
        return org.apache.catalina.util.RequestUtil.getRequestURL(this);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String getServletPath() {
        return this.mappingData.wrapperPath.toString();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public HttpSession getSession() {
        Session session = doGetSession(true);
        if (session == null) {
            return null;
        }
        return session.getSession();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public HttpSession getSession(boolean create) {
        Session session = doGetSession(create);
        if (session == null) {
            return null;
        }
        return session.getSession();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public boolean isRequestedSessionIdFromCookie() {
        if (this.requestedSessionId == null) {
            return false;
        }
        return this.requestedSessionCookie;
    }

    @Override // javax.servlet.http.HttpServletRequest
    public boolean isRequestedSessionIdFromURL() {
        if (this.requestedSessionId == null) {
            return false;
        }
        return this.requestedSessionURL;
    }

    @Override // javax.servlet.http.HttpServletRequest
    @Deprecated
    public boolean isRequestedSessionIdFromUrl() {
        return isRequestedSessionIdFromURL();
    }

    @Override // javax.servlet.http.HttpServletRequest
    public boolean isRequestedSessionIdValid() {
        Context context;
        Manager manager;
        if (this.requestedSessionId == null || (context = getContext()) == null || (manager = context.getManager()) == null) {
            return false;
        }
        Session session = null;
        try {
            session = manager.findSession(this.requestedSessionId);
        } catch (IOException e) {
        }
        if (session == null || !session.isValid()) {
            if (getMappingData().contexts == null) {
                return false;
            }
            for (int i = getMappingData().contexts.length; i > 0; i--) {
                Context ctxt = getMappingData().contexts[i - 1];
                if (ctxt.getManager().findSession(this.requestedSessionId) != null) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    @Override // javax.servlet.http.HttpServletRequest
    public boolean isUserInRole(String role) {
        Context context;
        if (this.userPrincipal == null || (context = getContext()) == null || "*".equals(role)) {
            return false;
        }
        if (SecurityConstraint.ROLE_ALL_AUTHENTICATED_USERS.equals(role) && !context.findSecurityRole(SecurityConstraint.ROLE_ALL_AUTHENTICATED_USERS)) {
            return this.userPrincipal != null;
        }
        Realm realm = context.getRealm();
        if (realm == null) {
            return false;
        }
        return realm.hasRole(getWrapper(), this.userPrincipal, role);
    }

    public Principal getPrincipal() {
        return this.userPrincipal;
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Principal getUserPrincipal() {
        if (this.userPrincipal instanceof TomcatPrincipal) {
            GSSCredential gssCredential = ((TomcatPrincipal) this.userPrincipal).getGssCredential();
            if (gssCredential != null) {
                int left = -1;
                try {
                    left = gssCredential.getRemainingLifetime();
                } catch (GSSException e) {
                    log.warn(sm.getString("coyoteRequest.gssLifetimeFail", this.userPrincipal.getName()), e);
                }
                if (left == 0) {
                    try {
                        logout();
                        return null;
                    } catch (ServletException e2) {
                        return null;
                    }
                }
            }
            return ((TomcatPrincipal) this.userPrincipal).getUserPrincipal();
        }
        return this.userPrincipal;
    }

    public Session getSessionInternal() {
        return doGetSession(true);
    }

    public void changeSessionId(String newSessionId) {
        if (this.requestedSessionId != null && this.requestedSessionId.length() > 0) {
            this.requestedSessionId = newSessionId;
        }
        Context context = getContext();
        if ((context == null || context.getServletContext().getEffectiveSessionTrackingModes().contains(SessionTrackingMode.COOKIE)) && this.response != null) {
            Cookie newCookie = ApplicationSessionCookieConfig.createSessionCookie(context, newSessionId, isSecure());
            this.response.addSessionCookieInternal(newCookie);
        }
    }

    @Override // javax.servlet.http.HttpServletRequest
    public String changeSessionId() {
        Session session = getSessionInternal(false);
        if (session == null) {
            throw new IllegalStateException(sm.getString("coyoteRequest.changeSessionId"));
        }
        Manager manager = getContext().getManager();
        manager.changeSessionId(session);
        String newSessionId = session.getId();
        changeSessionId(newSessionId);
        return newSessionId;
    }

    public Session getSessionInternal(boolean create) {
        return doGetSession(create);
    }

    public boolean isParametersParsed() {
        return this.parametersParsed;
    }

    public boolean isFinished() {
        return this.coyoteRequest.isFinished();
    }

    protected void checkSwallowInput() {
        Context context = getContext();
        if (context != null && !context.getSwallowAbortedUploads()) {
            this.coyoteRequest.action(ActionCode.DISABLE_SWALLOW_INPUT, null);
        }
    }

    @Override // javax.servlet.http.HttpServletRequest
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        if (response.isCommitted()) {
            throw new IllegalStateException(sm.getString("coyoteRequest.authenticate.ise"));
        }
        return getContext().getAuthenticator().authenticate(this, response);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public void login(String username, String password) throws ServletException {
        if (getAuthType() != null || getRemoteUser() != null || getUserPrincipal() != null) {
            throw new ServletException(sm.getString("coyoteRequest.alreadyAuthenticated"));
        }
        getContext().getAuthenticator().login(username, password, this);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public void logout() throws ServletException {
        getContext().getAuthenticator().logout(this);
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Collection<Part> getParts() throws IOException, IllegalStateException, ServletException {
        parseParts(true);
        if (this.partsParseException != null) {
            if (this.partsParseException instanceof IOException) {
                throw ((IOException) this.partsParseException);
            }
            if (this.partsParseException instanceof IllegalStateException) {
                throw ((IllegalStateException) this.partsParseException);
            }
            if (this.partsParseException instanceof ServletException) {
                throw ((ServletException) this.partsParseException);
            }
        }
        return this.parts;
    }

    private void parseParts(boolean explicit) {
        File location;
        if (this.parts == null && this.partsParseException == null) {
            Context context = getContext();
            MultipartConfigElement mce = getWrapper().getMultipartConfigElement();
            if (mce == null) {
                if (!context.getAllowCasualMultipartParsing()) {
                    if (explicit) {
                        this.partsParseException = new IllegalStateException(sm.getString("coyoteRequest.noMultipartConfig"));
                        return;
                    } else {
                        this.parts = Collections.emptyList();
                        return;
                    }
                }
                mce = new MultipartConfigElement(null, this.connector.getMaxPostSize(), this.connector.getMaxPostSize(), this.connector.getMaxPostSize());
            }
            Parameters parameters = this.coyoteRequest.getParameters();
            parameters.setLimit(getConnector().getMaxParameterCount());
            boolean success = false;
            try {
                String locationStr = mce.getLocation();
                if (locationStr == null || locationStr.length() == 0) {
                    location = (File) context.getServletContext().getAttribute("javax.servlet.context.tempdir");
                } else {
                    location = new File(locationStr);
                    if (!location.isAbsolute()) {
                        location = new File((File) context.getServletContext().getAttribute("javax.servlet.context.tempdir"), locationStr).getAbsoluteFile();
                    }
                }
                if (!location.isDirectory()) {
                    parameters.setParseFailedReason(Parameters.FailReason.MULTIPART_CONFIG_INVALID);
                    this.partsParseException = new IOException(sm.getString("coyoteRequest.uploadLocationInvalid", location));
                    if (this.partsParseException != null || 0 == 0) {
                        parameters.setParseFailedReason(Parameters.FailReason.UNKNOWN);
                        return;
                    }
                    return;
                }
                DiskFileItemFactory factory = new DiskFileItemFactory();
                try {
                    factory.setRepository(location.getCanonicalFile());
                    factory.setSizeThreshold(mce.getFileSizeThreshold());
                    ServletFileUpload upload = new ServletFileUpload();
                    upload.setFileItemFactory(factory);
                    upload.setFileSizeMax(mce.getMaxFileSize());
                    upload.setSizeMax(mce.getMaxRequestSize());
                    this.parts = new ArrayList();
                    try {
                        try {
                            try {
                                List<FileItem> items = upload.parseRequest(new ServletRequestContext(this));
                                int maxPostSize = getConnector().getMaxPostSize();
                                int postSize = 0;
                                Charset charset = getCharset();
                                for (FileItem item : items) {
                                    ApplicationPart part = new ApplicationPart(item, location);
                                    this.parts.add(part);
                                    if (part.getSubmittedFileName() == null) {
                                        String name = part.getName();
                                        String value = null;
                                        try {
                                            value = part.getString(charset.name());
                                        } catch (UnsupportedEncodingException e) {
                                        }
                                        if (maxPostSize >= 0) {
                                            int postSize2 = postSize + name.getBytes(charset).length;
                                            if (value != null) {
                                                postSize2 = (int) (postSize2 + 1 + part.getSize());
                                            }
                                            postSize = postSize2 + 1;
                                            if (postSize > maxPostSize) {
                                                parameters.setParseFailedReason(Parameters.FailReason.POST_TOO_LARGE);
                                                throw new IllegalStateException(sm.getString("coyoteRequest.maxPostSizeExceeded"));
                                            }
                                        }
                                        parameters.addParameter(name, value);
                                    }
                                }
                                success = true;
                            } catch (IllegalStateException e2) {
                                checkSwallowInput();
                                this.partsParseException = e2;
                            } catch (FileUploadException e3) {
                                parameters.setParseFailedReason(Parameters.FailReason.IO_ERROR);
                                this.partsParseException = new IOException(e3);
                            }
                        } catch (FileUploadBase.SizeException e4) {
                            parameters.setParseFailedReason(Parameters.FailReason.POST_TOO_LARGE);
                            checkSwallowInput();
                            this.partsParseException = new IllegalStateException(e4);
                        }
                    } catch (FileUploadBase.InvalidContentTypeException e5) {
                        parameters.setParseFailedReason(Parameters.FailReason.INVALID_CONTENT_TYPE);
                        this.partsParseException = new ServletException(e5);
                    }
                    if (this.partsParseException == null && success) {
                        return;
                    }
                    parameters.setParseFailedReason(Parameters.FailReason.UNKNOWN);
                } catch (IOException ioe) {
                    parameters.setParseFailedReason(Parameters.FailReason.IO_ERROR);
                    this.partsParseException = ioe;
                    if (this.partsParseException != null || 0 == 0) {
                        parameters.setParseFailedReason(Parameters.FailReason.UNKNOWN);
                    }
                }
            } catch (Throwable th) {
                if (this.partsParseException != null || 0 == 0) {
                    parameters.setParseFailedReason(Parameters.FailReason.UNKNOWN);
                }
                throw th;
            }
        }
    }

    @Override // javax.servlet.http.HttpServletRequest
    public Part getPart(String name) throws IOException, IllegalStateException, ServletException {
        for (Part part : getParts()) {
            if (name.equals(part.getName())) {
                return part;
            }
        }
        return null;
    }

    protected Session doGetSession(boolean create) {
        Context context = getContext();
        if (context == null) {
            return null;
        }
        if (this.session != null && !this.session.isValid()) {
            this.session = null;
        }
        if (this.session != null) {
            return this.session;
        }
        Manager manager = context.getManager();
        if (manager == null) {
            return null;
        }
        if (this.requestedSessionId != null) {
            try {
                this.session = manager.findSession(this.requestedSessionId);
            } catch (IOException e) {
                this.session = null;
            }
            if (this.session != null && !this.session.isValid()) {
                this.session = null;
            }
            if (this.session != null) {
                this.session.access();
                return this.session;
            }
        }
        if (!create) {
            return null;
        }
        if (this.response != null && context.getServletContext().getEffectiveSessionTrackingModes().contains(SessionTrackingMode.COOKIE) && this.response.getResponse().isCommitted()) {
            throw new IllegalStateException(sm.getString("coyoteRequest.sessionCreateCommitted"));
        }
        String sessionId = getRequestedSessionId();
        if (!this.requestedSessionSSL) {
            if ("/".equals(context.getSessionCookiePath()) && isRequestedSessionIdFromCookie()) {
                if (context.getValidateClientProvidedNewSessionId()) {
                    boolean found = false;
                    Container[] findChildren = getHost().findChildren();
                    int length = findChildren.length;
                    int i = 0;
                    while (true) {
                        if (i >= length) {
                            break;
                        }
                        Container container = findChildren[i];
                        Manager m = ((Context) container).getManager();
                        if (m != null) {
                            try {
                                if (m.findSession(sessionId) != null) {
                                    found = true;
                                    break;
                                }
                            } catch (IOException e2) {
                            }
                        }
                        i++;
                    }
                    if (!found) {
                        sessionId = null;
                    }
                }
            } else {
                sessionId = null;
            }
        }
        this.session = manager.createSession(sessionId);
        if (this.session != null && context.getServletContext().getEffectiveSessionTrackingModes().contains(SessionTrackingMode.COOKIE)) {
            Cookie cookie = ApplicationSessionCookieConfig.createSessionCookie(context, this.session.getIdInternal(), isSecure());
            this.response.addSessionCookieInternal(cookie);
        }
        if (this.session == null) {
            return null;
        }
        this.session.access();
        return this.session;
    }

    protected String unescape(String s) {
        if (s == null) {
            return null;
        }
        if (s.indexOf(92) == -1) {
            return s;
        }
        StringBuilder buf = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c != '\\') {
                buf.append(c);
            } else {
                i++;
                if (i >= s.length()) {
                    throw new IllegalArgumentException();
                }
                buf.append(s.charAt(i));
            }
            i++;
        }
        return buf.toString();
    }

    protected void parseCookies() {
        if (this.cookiesParsed) {
            return;
        }
        this.cookiesParsed = true;
        ServerCookies serverCookies = this.coyoteRequest.getCookies();
        serverCookies.setLimit(this.connector.getMaxCookieCount());
        CookieProcessor cookieProcessor = getContext().getCookieProcessor();
        cookieProcessor.parseCookieHeader(this.coyoteRequest.getMimeHeaders(), serverCookies);
    }

    protected void convertCookies() {
        if (this.cookiesConverted) {
            return;
        }
        this.cookiesConverted = true;
        if (getContext() == null) {
            return;
        }
        parseCookies();
        ServerCookies serverCookies = this.coyoteRequest.getCookies();
        CookieProcessor cookieProcessor = getContext().getCookieProcessor();
        int count = serverCookies.getCookieCount();
        if (count <= 0) {
            return;
        }
        this.cookies = new Cookie[count];
        int idx = 0;
        for (int i = 0; i < count; i++) {
            ServerCookie scookie = serverCookies.getCookie(i);
            try {
                Cookie cookie = new Cookie(scookie.getName().toString(), null);
                int version = scookie.getVersion();
                cookie.setVersion(version);
                scookie.getValue().getByteChunk().setCharset(cookieProcessor.getCharset());
                cookie.setValue(unescape(scookie.getValue().toString()));
                cookie.setPath(unescape(scookie.getPath().toString()));
                String domain = scookie.getDomain().toString();
                if (domain != null) {
                    cookie.setDomain(unescape(domain));
                }
                String comment = scookie.getComment().toString();
                cookie.setComment(version == 1 ? unescape(comment) : null);
                int i2 = idx;
                idx++;
                this.cookies[i2] = cookie;
            } catch (IllegalArgumentException e) {
            }
        }
        if (idx < count) {
            Cookie[] ncookies = new Cookie[idx];
            System.arraycopy(this.cookies, 0, ncookies, 0, idx);
            this.cookies = ncookies;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:239:0x02a4  */
    /* JADX WARN: Removed duplicated region for block: B:246:0x02be A[ORIG_RETURN, RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void parseParameters() {
        /*
            Method dump skipped, instructions count: 703
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.catalina.connector.Request.parseParameters():void");
    }

    protected int readPostBody(byte[] body, int len) throws IOException {
        int offset = 0;
        do {
            int inputLen = getStream().read(body, offset, len - offset);
            if (inputLen <= 0) {
                return offset;
            }
            offset += inputLen;
        } while (len - offset > 0);
        return len;
    }

    protected byte[] readChunkedPostBody() throws IOException {
        ByteChunk body = new ByteChunk();
        byte[] buffer = new byte[8192];
        int len = 0;
        while (len > -1) {
            len = getStream().read(buffer, 0, 8192);
            if (this.connector.getMaxPostSize() >= 0 && body.getLength() + len > this.connector.getMaxPostSize()) {
                checkSwallowInput();
                throw new IllegalStateException(sm.getString("coyoteRequest.chunkedPostTooLarge"));
            } else if (len > 0) {
                body.append(buffer, 0, len);
            }
        }
        if (body.getLength() == 0) {
            return null;
        }
        if (body.getLength() < body.getBuffer().length) {
            int length = body.getLength();
            byte[] result = new byte[length];
            System.arraycopy(body.getBuffer(), 0, result, 0, length);
            return result;
        }
        return body.getBuffer();
    }

    protected void parseLocales() {
        this.localesParsed = true;
        TreeMap<Double, ArrayList<Locale>> locales = new TreeMap<>();
        Enumeration<String> values = getHeaders("accept-language");
        while (values.hasMoreElements()) {
            String value = values.nextElement();
            parseLocalesHeader(value, locales);
        }
        for (ArrayList<Locale> list : locales.values()) {
            Iterator<Locale> it = list.iterator();
            while (it.hasNext()) {
                Locale locale = it.next();
                addLocale(locale);
            }
        }
    }

    protected void parseLocalesHeader(String value, TreeMap<Double, ArrayList<Locale>> locales) {
        try {
            List<AcceptLanguage> acceptLanguages = AcceptLanguage.parse(new StringReader(value));
            for (AcceptLanguage acceptLanguage : acceptLanguages) {
                Double key = Double.valueOf(-acceptLanguage.getQuality());
                ArrayList<Locale> values = locales.get(key);
                if (values == null) {
                    values = new ArrayList<>();
                    locales.put(key, values);
                }
                values.add(acceptLanguage.getLocale());
            }
        } catch (IOException e) {
        }
    }
}