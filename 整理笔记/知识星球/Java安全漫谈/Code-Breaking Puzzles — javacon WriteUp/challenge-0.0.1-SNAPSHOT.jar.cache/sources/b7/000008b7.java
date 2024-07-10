package org.apache.catalina.servlets;

import ch.qos.logback.core.util.FileSize;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.ResponseFacade;
import org.apache.catalina.util.ServerInfo;
import org.apache.catalina.util.TomcatCSS;
import org.apache.catalina.util.URLEncoder;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.http.ResponseUtil;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.Escape;
import org.apache.tomcat.util.security.PrivilegedGetTccl;
import org.apache.tomcat.util.security.PrivilegedSetTccl;
import org.springframework.http.HttpHeaders;
import org.springframework.jmx.support.JmxUtils;
import org.springframework.web.servlet.support.WebContentGenerator;
import org.springframework.web.servlet.tags.form.AbstractHtmlElementTag;
import org.springframework.web.servlet.tags.form.AbstractHtmlInputElementTag;
import org.thymeleaf.engine.XMLDeclaration;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.EntityResolver2;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/servlets/DefaultServlet.class */
public class DefaultServlet extends HttpServlet {
    private static final long serialVersionUID = 1;
    private static final DocumentBuilderFactory factory;
    private static final SecureEntityResolver secureEntityResolver;
    protected static final String mimeSeparation = "CATALINA_MIME_BOUNDARY";
    protected static final int BUFFER_SIZE = 4096;
    protected CompressionFormat[] compressionFormats;
    protected static final StringManager sm = StringManager.getManager(Constants.Package);
    protected static final ArrayList<Range> FULL = new ArrayList<>();
    protected int debug = 0;
    protected int input = 2048;
    protected boolean listings = false;
    protected boolean readOnly = true;
    protected int output = 2048;
    protected String localXsltFile = null;
    protected String contextXsltFile = null;
    protected String globalXsltFile = null;
    protected String readmeFile = null;
    protected transient WebResourceRoot resources = null;
    protected String fileEncoding = null;
    private transient Charset fileEncodingCharset = null;
    private boolean useBomIfPresent = true;
    protected int sendfileSize = 49152;
    protected boolean useAcceptRanges = true;
    protected boolean showServerInfo = true;

    static {
        if (Globals.IS_SECURITY_ENABLED) {
            factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(false);
            secureEntityResolver = new SecureEntityResolver();
            return;
        }
        factory = null;
        secureEntityResolver = null;
    }

    @Override // javax.servlet.GenericServlet, javax.servlet.Servlet
    public void destroy() {
    }

    @Override // javax.servlet.GenericServlet
    public void init() throws ServletException {
        if (getServletConfig().getInitParameter("debug") != null) {
            this.debug = Integer.parseInt(getServletConfig().getInitParameter("debug"));
        }
        if (getServletConfig().getInitParameter("input") != null) {
            this.input = Integer.parseInt(getServletConfig().getInitParameter("input"));
        }
        if (getServletConfig().getInitParameter("output") != null) {
            this.output = Integer.parseInt(getServletConfig().getInitParameter("output"));
        }
        this.listings = Boolean.parseBoolean(getServletConfig().getInitParameter("listings"));
        if (getServletConfig().getInitParameter(AbstractHtmlInputElementTag.READONLY_ATTRIBUTE) != null) {
            this.readOnly = Boolean.parseBoolean(getServletConfig().getInitParameter(AbstractHtmlInputElementTag.READONLY_ATTRIBUTE));
        }
        this.compressionFormats = parseCompressionFormats(getServletConfig().getInitParameter("precompressed"), getServletConfig().getInitParameter("gzip"));
        if (getServletConfig().getInitParameter("sendfileSize") != null) {
            this.sendfileSize = Integer.parseInt(getServletConfig().getInitParameter("sendfileSize")) * 1024;
        }
        this.fileEncoding = getServletConfig().getInitParameter("fileEncoding");
        if (this.fileEncoding == null) {
            this.fileEncodingCharset = Charset.defaultCharset();
            this.fileEncoding = this.fileEncodingCharset.name();
        } else {
            try {
                this.fileEncodingCharset = B2CConverter.getCharset(this.fileEncoding);
            } catch (UnsupportedEncodingException e) {
                throw new ServletException(e);
            }
        }
        if (getServletConfig().getInitParameter("useBomIfPresent") != null) {
            this.useBomIfPresent = Boolean.parseBoolean(getServletConfig().getInitParameter("useBomIfPresent"));
        }
        this.globalXsltFile = getServletConfig().getInitParameter("globalXsltFile");
        this.contextXsltFile = getServletConfig().getInitParameter("contextXsltFile");
        this.localXsltFile = getServletConfig().getInitParameter("localXsltFile");
        this.readmeFile = getServletConfig().getInitParameter("readmeFile");
        if (getServletConfig().getInitParameter("useAcceptRanges") != null) {
            this.useAcceptRanges = Boolean.parseBoolean(getServletConfig().getInitParameter("useAcceptRanges"));
        }
        if (this.input < 256) {
            this.input = 256;
        }
        if (this.output < 256) {
            this.output = 256;
        }
        if (this.debug > 0) {
            log("DefaultServlet.init:  input buffer size=" + this.input + ", output buffer size=" + this.output);
        }
        this.resources = (WebResourceRoot) getServletContext().getAttribute(Globals.RESOURCES_ATTR);
        if (this.resources == null) {
            throw new UnavailableException("No resources");
        }
        if (getServletConfig().getInitParameter("showServerInfo") != null) {
            this.showServerInfo = Boolean.parseBoolean(getServletConfig().getInitParameter("showServerInfo"));
        }
    }

    private CompressionFormat[] parseCompressionFormats(String precompressed, String gzip) {
        String[] split;
        List<CompressionFormat> ret = new ArrayList<>();
        if (precompressed != null && precompressed.indexOf(61) > 0) {
            for (String pair : precompressed.split(",")) {
                String[] setting = pair.split("=");
                String encoding = setting[0];
                String extension = setting[1];
                ret.add(new CompressionFormat(extension, encoding));
            }
        } else if (precompressed != null) {
            if (Boolean.parseBoolean(precompressed)) {
                ret.add(new CompressionFormat(".br", "br"));
                ret.add(new CompressionFormat(".gz", "gzip"));
            }
        } else if (Boolean.parseBoolean(gzip)) {
            ret.add(new CompressionFormat(".gz", "gzip"));
        }
        return (CompressionFormat[]) ret.toArray(new CompressionFormat[ret.size()]);
    }

    protected String getRelativePath(HttpServletRequest request) {
        return getRelativePath(request, false);
    }

    protected String getRelativePath(HttpServletRequest request, boolean allowEmptyPath) {
        String pathInfo;
        String servletPath;
        if (request.getAttribute("javax.servlet.include.request_uri") != null) {
            pathInfo = (String) request.getAttribute("javax.servlet.include.path_info");
            servletPath = (String) request.getAttribute("javax.servlet.include.servlet_path");
        } else {
            pathInfo = request.getPathInfo();
            servletPath = request.getServletPath();
        }
        StringBuilder result = new StringBuilder();
        if (servletPath.length() > 0) {
            result.append(servletPath);
        }
        if (pathInfo != null) {
            result.append(pathInfo);
        }
        if (result.length() == 0 && !allowEmptyPath) {
            result.append('/');
        }
        return result.toString();
    }

    protected String getPathPrefix(HttpServletRequest request) {
        return request.getContextPath();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // javax.servlet.http.HttpServlet
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getDispatcherType() == DispatcherType.ERROR) {
            doGet(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // javax.servlet.http.HttpServlet
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        serveResource(request, response, true, this.fileEncoding);
    }

    @Override // javax.servlet.http.HttpServlet
    protected void doHead(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        boolean serveContent = DispatcherType.INCLUDE.equals(request.getDispatcherType());
        serveResource(request, response, serveContent, this.fileEncoding);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // javax.servlet.http.HttpServlet
    public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader(HttpHeaders.ALLOW, determineMethodsAllowed(req));
    }

    protected String determineMethodsAllowed(HttpServletRequest req) {
        StringBuilder allow = new StringBuilder();
        allow.append("OPTIONS, GET, HEAD, POST");
        if (!this.readOnly) {
            allow.append(", PUT, DELETE");
        }
        if ((req instanceof RequestFacade) && ((RequestFacade) req).getAllowTrace()) {
            allow.append(", TRACE");
        }
        return allow.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void sendNotAllowed(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.addHeader(HttpHeaders.ALLOW, determineMethodsAllowed(req));
        resp.sendError(405);
    }

    @Override // javax.servlet.http.HttpServlet
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGet(request, response);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // javax.servlet.http.HttpServlet
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        InputStream resourceInputStream;
        if (this.readOnly) {
            sendNotAllowed(req, resp);
            return;
        }
        String path = getRelativePath(req);
        WebResource resource = this.resources.getResource(path);
        Range range = parseContentRange(req, resp);
        InputStream resourceInputStream2 = null;
        try {
            if (range != null) {
                File contentFile = executePartialPut(req, range, path);
                resourceInputStream = new FileInputStream(contentFile);
            } else {
                resourceInputStream = req.getInputStream();
            }
            if (this.resources.write(path, resourceInputStream, true)) {
                if (resource.exists()) {
                    resp.setStatus(204);
                } else {
                    resp.setStatus(201);
                }
            } else {
                resp.sendError(409);
            }
            if (resourceInputStream != null) {
                try {
                    resourceInputStream.close();
                } catch (IOException e) {
                }
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    resourceInputStream2.close();
                } catch (IOException e2) {
                }
            }
            throw th;
        }
    }

    protected File executePartialPut(HttpServletRequest req, Range range, String path) throws IOException {
        File tempDir = (File) getServletContext().getAttribute("javax.servlet.context.tempdir");
        String convertedResourcePath = path.replace('/', '.');
        File contentFile = new File(tempDir, convertedResourcePath);
        if (contentFile.createNewFile()) {
            contentFile.deleteOnExit();
        }
        RandomAccessFile randAccessContentFile = new RandomAccessFile(contentFile, "rw");
        Throwable th = null;
        try {
            WebResource oldResource = this.resources.getResource(path);
            if (oldResource.isFile()) {
                BufferedInputStream bufOldRevStream = new BufferedInputStream(oldResource.getInputStream(), 4096);
                byte[] copyBuffer = new byte[4096];
                while (true) {
                    int numBytesRead = bufOldRevStream.read(copyBuffer);
                    if (numBytesRead == -1) {
                        break;
                    }
                    randAccessContentFile.write(copyBuffer, 0, numBytesRead);
                }
                if (bufOldRevStream != null) {
                    if (0 != 0) {
                        bufOldRevStream.close();
                    } else {
                        bufOldRevStream.close();
                    }
                }
            }
            randAccessContentFile.setLength(range.length);
            randAccessContentFile.seek(range.start);
            byte[] transferBuffer = new byte[4096];
            BufferedInputStream requestBufInStream = new BufferedInputStream(req.getInputStream(), 4096);
            while (true) {
                int numBytesRead2 = requestBufInStream.read(transferBuffer);
                if (numBytesRead2 == -1) {
                    break;
                }
                randAccessContentFile.write(transferBuffer, 0, numBytesRead2);
            }
            if (requestBufInStream != null) {
                if (0 != 0) {
                    requestBufInStream.close();
                } else {
                    requestBufInStream.close();
                }
            }
            if (randAccessContentFile != null) {
                if (0 != 0) {
                    try {
                        randAccessContentFile.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                } else {
                    randAccessContentFile.close();
                }
            }
            return contentFile;
        } finally {
        }
    }

    @Override // javax.servlet.http.HttpServlet
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (this.readOnly) {
            sendNotAllowed(req, resp);
            return;
        }
        String path = getRelativePath(req);
        WebResource resource = this.resources.getResource(path);
        if (resource.exists()) {
            if (resource.delete()) {
                resp.setStatus(204);
                return;
            } else {
                resp.sendError(405);
                return;
            }
        }
        resp.sendError(404);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean checkIfHeaders(HttpServletRequest request, HttpServletResponse response, WebResource resource) throws IOException {
        return checkIfMatch(request, response, resource) && checkIfModifiedSince(request, response, resource) && checkIfNoneMatch(request, response, resource) && checkIfUnmodifiedSince(request, response, resource);
    }

    protected String rewriteUrl(String path) {
        return URLEncoder.DEFAULT.encode(path, StandardCharsets.UTF_8);
    }

    protected void serveResource(HttpServletRequest request, HttpServletResponse response, boolean content, String inputEncoding) throws IOException, ServletException {
        boolean conversionRequired;
        InputStream renderResult;
        boolean serveContent = content;
        String path = getRelativePath(request, true);
        if (this.debug > 0) {
            if (serveContent) {
                log("DefaultServlet.serveResource:  Serving resource '" + path + "' headers and data");
            } else {
                log("DefaultServlet.serveResource:  Serving resource '" + path + "' headers only");
            }
        }
        if (path.length() == 0) {
            doDirectoryRedirect(request, response);
            return;
        }
        WebResource resource = this.resources.getResource(path);
        boolean isError = DispatcherType.ERROR == request.getDispatcherType();
        if (!resource.exists()) {
            String requestUri = (String) request.getAttribute("javax.servlet.include.request_uri");
            if (requestUri == null) {
                String requestUri2 = request.getRequestURI();
                if (isError) {
                    response.sendError(((Integer) request.getAttribute("javax.servlet.error.status_code")).intValue());
                    return;
                } else {
                    response.sendError(404, requestUri2);
                    return;
                }
            }
            throw new FileNotFoundException(sm.getString("defaultServlet.missingResource", requestUri));
        } else if (!resource.canRead()) {
            String requestUri3 = (String) request.getAttribute("javax.servlet.include.request_uri");
            if (requestUri3 == null) {
                String requestUri4 = request.getRequestURI();
                if (isError) {
                    response.sendError(((Integer) request.getAttribute("javax.servlet.error.status_code")).intValue());
                    return;
                } else {
                    response.sendError(403, requestUri4);
                    return;
                }
            }
            throw new FileNotFoundException(sm.getString("defaultServlet.missingResource", requestUri3));
        } else {
            boolean included = false;
            if (resource.isFile()) {
                included = request.getAttribute("javax.servlet.include.context_path") != null;
                if (!included && !isError && !checkIfHeaders(request, response, resource)) {
                    return;
                }
            }
            String contentType = resource.getMimeType();
            if (contentType == null) {
                contentType = getServletContext().getMimeType(resource.getName());
                resource.setMimeType(contentType);
            }
            String eTag = null;
            String lastModifiedHttp = null;
            if (resource.isFile() && !isError) {
                eTag = resource.getETag();
                lastModifiedHttp = resource.getLastModifiedHttp();
            }
            boolean usingPrecompressedVersion = false;
            if (this.compressionFormats.length > 0 && !included && resource.isFile() && !pathEndsWithCompressedExtension(path)) {
                List<PrecompressedResource> precompressedResources = getAvailablePrecompressedResources(path);
                if (!precompressedResources.isEmpty()) {
                    ResponseUtil.addVaryFieldName(response, "accept-encoding");
                    PrecompressedResource bestResource = getBestPrecompressedResource(request, precompressedResources);
                    if (bestResource != null) {
                        response.addHeader(HttpHeaders.CONTENT_ENCODING, bestResource.format.encoding);
                        resource = bestResource.resource;
                        usingPrecompressedVersion = true;
                    }
                }
            }
            ArrayList<Range> ranges = null;
            long contentLength = -1;
            if (resource.isDirectory()) {
                if (!path.endsWith("/")) {
                    doDirectoryRedirect(request, response);
                    return;
                } else if (!this.listings) {
                    response.sendError(404, request.getRequestURI());
                    return;
                } else {
                    contentType = "text/html;charset=UTF-8";
                }
            } else {
                if (!isError) {
                    if (this.useAcceptRanges) {
                        response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
                    }
                    ranges = parseRange(request, response, resource);
                    response.setHeader(HttpHeaders.ETAG, eTag);
                    response.setHeader(HttpHeaders.LAST_MODIFIED, lastModifiedHttp);
                }
                contentLength = resource.getContentLength();
                if (contentLength == 0) {
                    serveContent = false;
                }
            }
            ServletOutputStream ostream = null;
            PrintWriter writer = null;
            if (serveContent) {
                try {
                    ostream = response.getOutputStream();
                } catch (IllegalStateException e) {
                    if (!usingPrecompressedVersion && isText(contentType)) {
                        writer = response.getWriter();
                        ranges = FULL;
                    } else {
                        throw e;
                    }
                }
            }
            ServletResponse r = response;
            long contentWritten = 0;
            while (r instanceof ServletResponseWrapper) {
                r = ((ServletResponseWrapper) r).getResponse();
            }
            if (r instanceof ResponseFacade) {
                contentWritten = ((ResponseFacade) r).getContentWritten();
            }
            if (contentWritten > 0) {
                ranges = FULL;
            }
            String outputEncoding = response.getCharacterEncoding();
            Charset charset = B2CConverter.getCharset(outputEncoding);
            boolean outputEncodingSpecified = (outputEncoding == org.apache.coyote.Constants.DEFAULT_BODY_CHARSET.name() || outputEncoding == this.resources.getContext().getResponseCharacterEncoding()) ? false : true;
            if (!usingPrecompressedVersion && isText(contentType) && outputEncodingSpecified && !charset.equals(this.fileEncodingCharset)) {
                conversionRequired = true;
                ranges = FULL;
            } else {
                conversionRequired = false;
            }
            if (resource.isDirectory() || isError || (((ranges == null || ranges.isEmpty()) && request.getHeader(HttpHeaders.RANGE) == null) || ranges == FULL)) {
                if (contentType != null) {
                    if (this.debug > 0) {
                        log("DefaultServlet.serveFile:  contentType='" + contentType + "'");
                    }
                    response.setContentType(contentType);
                }
                if (resource.isFile() && contentLength >= 0 && (!serveContent || ostream != null)) {
                    if (this.debug > 0) {
                        log("DefaultServlet.serveFile:  contentLength=" + contentLength);
                    }
                    if (contentWritten == 0 && !conversionRequired) {
                        response.setContentLengthLong(contentLength);
                    }
                }
                if (serveContent) {
                    try {
                        response.setBufferSize(this.output);
                    } catch (IllegalStateException e2) {
                    }
                    InputStream renderResult2 = null;
                    if (ostream == null) {
                        if (resource.isDirectory()) {
                            renderResult = render(getPathPrefix(request), resource, inputEncoding);
                        } else {
                            renderResult = resource.getInputStream();
                            if (included) {
                                if (!renderResult.markSupported()) {
                                    renderResult = new BufferedInputStream(renderResult);
                                }
                                Charset bomCharset = processBom(renderResult);
                                if (bomCharset != null && this.useBomIfPresent) {
                                    inputEncoding = bomCharset.name();
                                }
                            }
                        }
                        copy(renderResult, writer, inputEncoding);
                        return;
                    }
                    if (resource.isDirectory()) {
                        renderResult2 = render(getPathPrefix(request), resource, inputEncoding);
                    } else if (conversionRequired || included) {
                        InputStream source = resource.getInputStream();
                        if (!source.markSupported()) {
                            source = new BufferedInputStream(source);
                        }
                        Charset bomCharset2 = processBom(source);
                        if (bomCharset2 != null && this.useBomIfPresent) {
                            inputEncoding = bomCharset2.name();
                        }
                        if (outputEncodingSpecified) {
                            OutputStreamWriter osw = new OutputStreamWriter(ostream, charset);
                            PrintWriter pw = new PrintWriter(osw);
                            copy(source, pw, inputEncoding);
                            pw.flush();
                        } else {
                            renderResult2 = source;
                        }
                    } else if (!checkSendfile(request, response, resource, contentLength, null)) {
                        byte[] resourceBody = resource.getContent();
                        if (resourceBody == null) {
                            renderResult2 = resource.getInputStream();
                        } else {
                            ostream.write(resourceBody);
                        }
                    }
                    if (renderResult2 != null) {
                        copy(renderResult2, ostream);
                    }
                }
            } else if (ranges == null || ranges.isEmpty()) {
            } else {
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                if (ranges.size() == 1) {
                    Range range = ranges.get(0);
                    response.addHeader(HttpHeaders.CONTENT_RANGE, "bytes " + range.start + "-" + range.end + "/" + range.length);
                    long length = (range.end - range.start) + serialVersionUID;
                    response.setContentLengthLong(length);
                    if (contentType != null) {
                        if (this.debug > 0) {
                            log("DefaultServlet.serveFile:  contentType='" + contentType + "'");
                        }
                        response.setContentType(contentType);
                    }
                    if (serveContent) {
                        try {
                            response.setBufferSize(this.output);
                        } catch (IllegalStateException e3) {
                        }
                        if (ostream != null) {
                            if (!checkSendfile(request, response, resource, (range.end - range.start) + serialVersionUID, range)) {
                                copy(resource, ostream, range);
                                return;
                            }
                            return;
                        }
                        throw new IllegalStateException();
                    }
                    return;
                }
                response.setContentType("multipart/byteranges; boundary=CATALINA_MIME_BOUNDARY");
                if (serveContent) {
                    try {
                        response.setBufferSize(this.output);
                    } catch (IllegalStateException e4) {
                    }
                    if (ostream != null) {
                        copy(resource, ostream, ranges.iterator(), contentType);
                        return;
                    }
                    throw new IllegalStateException();
                }
            }
        }
    }

    private static Charset processBom(InputStream is) throws IOException {
        byte[] bom = new byte[4];
        is.mark(bom.length);
        int count = is.read(bom);
        if (count < 2) {
            skip(is, 0);
            return null;
        }
        int b0 = bom[0] & 255;
        int b1 = bom[1] & 255;
        if (b0 == 254 && b1 == 255) {
            skip(is, 2);
            return StandardCharsets.UTF_16BE;
        } else if (b0 == 255 && b1 == 254) {
            skip(is, 2);
            return StandardCharsets.UTF_16LE;
        } else if (count < 3) {
            skip(is, 0);
            return null;
        } else {
            int b2 = bom[2] & 255;
            if (b0 == 239 && b1 == 187 && b2 == 191) {
                skip(is, 3);
                return StandardCharsets.UTF_8;
            } else if (count < 4) {
                skip(is, 0);
                return null;
            } else {
                int b3 = bom[3] & 255;
                if (b0 == 0 && b1 == 0 && b2 == 254 && b3 == 255) {
                    return Charset.forName("UTF32-BE");
                }
                if (b0 == 255 && b1 == 254 && b2 == 0 && b3 == 0) {
                    return Charset.forName("UTF32-LE");
                }
                skip(is, 0);
                return null;
            }
        }
    }

    private static void skip(InputStream is, int skip) throws IOException {
        is.reset();
        while (true) {
            int i = skip;
            skip--;
            if (i > 0) {
                is.read();
            } else {
                return;
            }
        }
    }

    private static boolean isText(String contentType) {
        return contentType == null || contentType.startsWith("text") || contentType.endsWith(XMLDeclaration.DEFAULT_KEYWORD) || contentType.contains("/javascript");
    }

    private boolean pathEndsWithCompressedExtension(String path) {
        CompressionFormat[] compressionFormatArr;
        for (CompressionFormat format : this.compressionFormats) {
            if (path.endsWith(format.extension)) {
                return true;
            }
        }
        return false;
    }

    private List<PrecompressedResource> getAvailablePrecompressedResources(String path) {
        CompressionFormat[] compressionFormatArr;
        List<PrecompressedResource> ret = new ArrayList<>(this.compressionFormats.length);
        for (CompressionFormat format : this.compressionFormats) {
            WebResource precompressedResource = this.resources.getResource(path + format.extension);
            if (precompressedResource.exists() && precompressedResource.isFile()) {
                ret.add(new PrecompressedResource(precompressedResource, format));
            }
        }
        return ret;
    }

    private PrecompressedResource getBestPrecompressedResource(HttpServletRequest request, List<PrecompressedResource> precompressedResources) {
        String[] split;
        Enumeration<String> headers = request.getHeaders(HttpHeaders.ACCEPT_ENCODING);
        PrecompressedResource bestResource = null;
        double bestResourceQuality = 0.0d;
        int bestResourcePreference = Integer.MAX_VALUE;
        while (headers.hasMoreElements()) {
            String header = headers.nextElement();
            for (String preference : header.split(",")) {
                double quality = 1.0d;
                int qualityIdx = preference.indexOf(59);
                if (qualityIdx > 0) {
                    int equalsIdx = preference.indexOf(61, qualityIdx + 1);
                    if (equalsIdx != -1) {
                        quality = Double.parseDouble(preference.substring(equalsIdx + 1).trim());
                    }
                }
                if (quality >= bestResourceQuality) {
                    String encoding = preference;
                    if (qualityIdx > 0) {
                        encoding = encoding.substring(0, qualityIdx);
                    }
                    String encoding2 = encoding.trim();
                    if (JmxUtils.IDENTITY_OBJECT_NAME_KEY.equals(encoding2)) {
                        bestResource = null;
                        bestResourceQuality = quality;
                        bestResourcePreference = Integer.MAX_VALUE;
                    } else if ("*".equals(encoding2)) {
                        bestResource = precompressedResources.get(0);
                        bestResourceQuality = quality;
                        bestResourcePreference = 0;
                    } else {
                        int i = 0;
                        while (true) {
                            if (i < precompressedResources.size()) {
                                PrecompressedResource resource = precompressedResources.get(i);
                                if (!encoding2.equals(resource.format.encoding)) {
                                    i++;
                                } else if (quality > bestResourceQuality || i < bestResourcePreference) {
                                    bestResource = resource;
                                    bestResourceQuality = quality;
                                    bestResourcePreference = i;
                                }
                            }
                        }
                    }
                }
            }
        }
        return bestResource;
    }

    private void doDirectoryRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuilder location = new StringBuilder(request.getRequestURI());
        location.append('/');
        if (request.getQueryString() != null) {
            location.append('?');
            location.append(request.getQueryString());
        }
        while (location.length() > 1 && location.charAt(1) == '/') {
            location.deleteCharAt(0);
        }
        response.sendRedirect(response.encodeRedirectURL(location.toString()));
    }

    protected Range parseContentRange(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String rangeHeader = request.getHeader(HttpHeaders.CONTENT_RANGE);
        if (rangeHeader == null) {
            return null;
        }
        if (!rangeHeader.startsWith("bytes")) {
            response.sendError(400);
            return null;
        }
        String rangeHeader2 = rangeHeader.substring(6).trim();
        int dashPos = rangeHeader2.indexOf(45);
        int slashPos = rangeHeader2.indexOf(47);
        if (dashPos == -1) {
            response.sendError(400);
            return null;
        } else if (slashPos == -1) {
            response.sendError(400);
            return null;
        } else {
            Range range = new Range();
            try {
                range.start = Long.parseLong(rangeHeader2.substring(0, dashPos));
                range.end = Long.parseLong(rangeHeader2.substring(dashPos + 1, slashPos));
                range.length = Long.parseLong(rangeHeader2.substring(slashPos + 1, rangeHeader2.length()));
                if (!range.validate()) {
                    response.sendError(400);
                    return null;
                }
                return range;
            } catch (NumberFormatException e) {
                response.sendError(400);
                return null;
            }
        }
    }

    protected ArrayList<Range> parseRange(HttpServletRequest request, HttpServletResponse response, WebResource resource) throws IOException {
        String rangeHeader;
        String headerValue = request.getHeader(HttpHeaders.IF_RANGE);
        if (headerValue != null) {
            long headerValueTime = -1;
            try {
                headerValueTime = request.getDateHeader(HttpHeaders.IF_RANGE);
            } catch (IllegalArgumentException e) {
            }
            String eTag = resource.getETag();
            long lastModified = resource.getLastModified();
            if (headerValueTime == -1) {
                if (!eTag.equals(headerValue.trim())) {
                    return FULL;
                }
            } else if (lastModified > headerValueTime + 1000) {
                return FULL;
            }
        }
        long fileLength = resource.getContentLength();
        if (fileLength == 0 || (rangeHeader = request.getHeader(HttpHeaders.RANGE)) == null) {
            return null;
        }
        if (!rangeHeader.startsWith("bytes")) {
            response.addHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength);
            response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            return null;
        }
        String rangeHeader2 = rangeHeader.substring(6);
        ArrayList<Range> result = new ArrayList<>();
        StringTokenizer commaTokenizer = new StringTokenizer(rangeHeader2, ",");
        while (commaTokenizer.hasMoreTokens()) {
            String rangeDefinition = commaTokenizer.nextToken().trim();
            Range currentRange = new Range();
            currentRange.length = fileLength;
            int dashPos = rangeDefinition.indexOf(45);
            if (dashPos == -1) {
                response.addHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength);
                response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                return null;
            }
            if (dashPos == 0) {
                try {
                    long offset = Long.parseLong(rangeDefinition);
                    currentRange.start = fileLength + offset;
                    currentRange.end = fileLength - serialVersionUID;
                } catch (NumberFormatException e2) {
                    response.addHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength);
                    response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                    return null;
                }
            } else {
                try {
                    currentRange.start = Long.parseLong(rangeDefinition.substring(0, dashPos));
                    if (dashPos < rangeDefinition.length() - 1) {
                        currentRange.end = Long.parseLong(rangeDefinition.substring(dashPos + 1, rangeDefinition.length()));
                    } else {
                        currentRange.end = fileLength - serialVersionUID;
                    }
                } catch (NumberFormatException e3) {
                    response.addHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength);
                    response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                    return null;
                }
            }
            if (!currentRange.validate()) {
                response.addHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength);
                response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                return null;
            }
            result.add(currentRange);
        }
        return result;
    }

    protected InputStream render(String contextPath, WebResource resource, String encoding) throws IOException, ServletException {
        Source xsltSource = findXsltSource(resource);
        if (xsltSource == null) {
            return renderHtml(contextPath, resource, encoding);
        }
        return renderXml(contextPath, resource, xsltSource, encoding);
    }

    protected InputStream renderXml(String contextPath, WebResource resource, Source xsltSource, String encoding) throws IOException, ServletException {
        ClassLoader original;
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<listing ");
        sb.append(" contextPath='");
        sb.append(contextPath);
        sb.append("'");
        sb.append(" directory='");
        sb.append(resource.getName());
        sb.append("' ");
        sb.append(" hasParent='").append(!resource.getName().equals("/"));
        sb.append("'>");
        sb.append("<entries>");
        String[] entries = this.resources.list(resource.getWebappPath());
        String rewrittenContextPath = rewriteUrl(contextPath);
        String directoryWebappPath = resource.getWebappPath();
        for (String entry : entries) {
            if (!entry.equalsIgnoreCase("WEB-INF") && !entry.equalsIgnoreCase("META-INF") && !entry.equalsIgnoreCase(this.localXsltFile) && !(directoryWebappPath + entry).equals(this.contextXsltFile)) {
                WebResource childResource = this.resources.getResource(directoryWebappPath + entry);
                if (childResource.exists()) {
                    sb.append("<entry");
                    sb.append(" type='").append(childResource.isDirectory() ? AbstractHtmlElementTag.DIR_ATTRIBUTE : "file").append("'");
                    sb.append(" urlPath='").append(rewrittenContextPath).append(rewriteUrl(directoryWebappPath + entry)).append(childResource.isDirectory() ? "/" : "").append("'");
                    if (childResource.isFile()) {
                        sb.append(" size='").append(renderSize(childResource.getContentLength())).append("'");
                    }
                    sb.append(" date='").append(childResource.getLastModifiedHttp()).append("'");
                    sb.append(">");
                    sb.append(Escape.htmlElementContent(entry));
                    if (childResource.isDirectory()) {
                        sb.append("/");
                    }
                    sb.append("</entry>");
                }
            }
        }
        sb.append("</entries>");
        String readme = getReadme(resource, encoding);
        if (readme != null) {
            sb.append("<readme><![CDATA[");
            sb.append(readme);
            sb.append("]]></readme>");
        }
        sb.append("</listing>");
        if (Globals.IS_SECURITY_ENABLED) {
            PrivilegedGetTccl pa = new PrivilegedGetTccl();
            original = (ClassLoader) AccessController.doPrivileged(pa);
        } else {
            original = Thread.currentThread().getContextClassLoader();
        }
        try {
            try {
                if (Globals.IS_SECURITY_ENABLED) {
                    PrivilegedSetTccl pa2 = new PrivilegedSetTccl(DefaultServlet.class.getClassLoader());
                    AccessController.doPrivileged(pa2);
                } else {
                    Thread.currentThread().setContextClassLoader(DefaultServlet.class.getClassLoader());
                }
                TransformerFactory tFactory = TransformerFactory.newInstance();
                Source xmlSource = new StreamSource(new StringReader(sb.toString()));
                Transformer transformer = tFactory.newTransformer(xsltSource);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                OutputStreamWriter osWriter = new OutputStreamWriter(stream, "UTF8");
                StreamResult out = new StreamResult(osWriter);
                transformer.transform(xmlSource, out);
                osWriter.flush();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(stream.toByteArray());
                if (Globals.IS_SECURITY_ENABLED) {
                    PrivilegedSetTccl pa3 = new PrivilegedSetTccl(original);
                    AccessController.doPrivileged(pa3);
                } else {
                    Thread.currentThread().setContextClassLoader(original);
                }
                return byteArrayInputStream;
            } catch (TransformerException e) {
                throw new ServletException("XSL transformer error", e);
            }
        } catch (Throwable th) {
            if (Globals.IS_SECURITY_ENABLED) {
                PrivilegedSetTccl pa4 = new PrivilegedSetTccl(original);
                AccessController.doPrivileged(pa4);
            } else {
                Thread.currentThread().setContextClassLoader(original);
            }
            throw th;
        }
    }

    protected InputStream renderHtml(String contextPath, WebResource resource, String encoding) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        OutputStreamWriter osWriter = new OutputStreamWriter(stream, "UTF8");
        PrintWriter writer = new PrintWriter(osWriter);
        StringBuilder sb = new StringBuilder();
        String[] entries = this.resources.list(resource.getWebappPath());
        String rewrittenContextPath = rewriteUrl(contextPath);
        String directoryWebappPath = resource.getWebappPath();
        sb.append("<html>\r\n");
        sb.append("<head>\r\n");
        sb.append("<title>");
        sb.append(sm.getString("directory.title", directoryWebappPath));
        sb.append("</title>\r\n");
        sb.append("<STYLE><!--");
        sb.append(TomcatCSS.TOMCAT_CSS);
        sb.append("--></STYLE> ");
        sb.append("</head>\r\n");
        sb.append("<body>");
        sb.append("<h1>");
        sb.append(sm.getString("directory.title", directoryWebappPath));
        String parentDirectory = directoryWebappPath;
        if (parentDirectory.endsWith("/")) {
            parentDirectory = parentDirectory.substring(0, parentDirectory.length() - 1);
        }
        int slash = parentDirectory.lastIndexOf(47);
        if (slash >= 0) {
            String parent = directoryWebappPath.substring(0, slash);
            sb.append(" - <a href=\"");
            sb.append(rewrittenContextPath);
            if (parent.equals("")) {
                parent = "/";
            }
            sb.append(rewriteUrl(parent));
            if (!parent.endsWith("/")) {
                sb.append("/");
            }
            sb.append("\">");
            sb.append("<b>");
            sb.append(sm.getString("directory.parent", parent));
            sb.append("</b>");
            sb.append("</a>");
        }
        sb.append("</h1>");
        sb.append("<HR size=\"1\" noshade=\"noshade\">");
        sb.append("<table width=\"100%\" cellspacing=\"0\" cellpadding=\"5\" align=\"center\">\r\n");
        sb.append("<tr>\r\n");
        sb.append("<td align=\"left\"><font size=\"+1\"><strong>");
        sb.append(sm.getString("directory.filename"));
        sb.append("</strong></font></td>\r\n");
        sb.append("<td align=\"center\"><font size=\"+1\"><strong>");
        sb.append(sm.getString("directory.size"));
        sb.append("</strong></font></td>\r\n");
        sb.append("<td align=\"right\"><font size=\"+1\"><strong>");
        sb.append(sm.getString("directory.lastModified"));
        sb.append("</strong></font></td>\r\n");
        sb.append("</tr>");
        boolean shade = false;
        for (String entry : entries) {
            if (!entry.equalsIgnoreCase("WEB-INF") && !entry.equalsIgnoreCase("META-INF")) {
                WebResource childResource = this.resources.getResource(directoryWebappPath + entry);
                if (childResource.exists()) {
                    sb.append("<tr");
                    if (shade) {
                        sb.append(" bgcolor=\"#eeeeee\"");
                    }
                    sb.append(">\r\n");
                    shade = !shade;
                    sb.append("<td align=\"left\">&nbsp;&nbsp;\r\n");
                    sb.append("<a href=\"");
                    sb.append(rewrittenContextPath);
                    sb.append(rewriteUrl(directoryWebappPath + entry));
                    if (childResource.isDirectory()) {
                        sb.append("/");
                    }
                    sb.append("\"><tt>");
                    sb.append(Escape.htmlElementContent(entry));
                    if (childResource.isDirectory()) {
                        sb.append("/");
                    }
                    sb.append("</tt></a></td>\r\n");
                    sb.append("<td align=\"right\"><tt>");
                    if (childResource.isDirectory()) {
                        sb.append("&nbsp;");
                    } else {
                        sb.append(renderSize(childResource.getContentLength()));
                    }
                    sb.append("</tt></td>\r\n");
                    sb.append("<td align=\"right\"><tt>");
                    sb.append(childResource.getLastModifiedHttp());
                    sb.append("</tt></td>\r\n");
                    sb.append("</tr>\r\n");
                }
            }
        }
        sb.append("</table>\r\n");
        sb.append("<HR size=\"1\" noshade=\"noshade\">");
        String readme = getReadme(resource, encoding);
        if (readme != null) {
            sb.append(readme);
            sb.append("<HR size=\"1\" noshade=\"noshade\">");
        }
        if (this.showServerInfo) {
            sb.append("<h3>").append(ServerInfo.getServerInfo()).append("</h3>");
        }
        sb.append("</body>\r\n");
        sb.append("</html>\r\n");
        writer.write(sb.toString());
        writer.flush();
        return new ByteArrayInputStream(stream.toByteArray());
    }

    protected String renderSize(long size) {
        long leftSide = size / FileSize.KB_COEFFICIENT;
        long rightSide = (size % FileSize.KB_COEFFICIENT) / 103;
        if (leftSide == 0 && rightSide == 0 && size > 0) {
            rightSide = 1;
        }
        return "" + leftSide + "." + rightSide + " kb";
    }

    protected String getReadme(WebResource directory, String encoding) {
        InputStream is;
        Throwable th;
        if (this.readmeFile != null) {
            WebResource resource = this.resources.getResource(directory.getWebappPath() + this.readmeFile);
            if (!resource.isFile()) {
                if (this.debug > 10) {
                    log("readme '" + this.readmeFile + "' not found");
                    return null;
                }
                return null;
            }
            StringWriter buffer = new StringWriter();
            InputStreamReader reader = null;
            try {
                try {
                    is = resource.getInputStream();
                    th = null;
                } catch (IOException e) {
                    log("Failure to close reader", e);
                    if (0 != 0) {
                        try {
                            reader.close();
                        } catch (IOException e2) {
                        }
                    }
                }
                try {
                    InputStreamReader reader2 = encoding != null ? new InputStreamReader(is, encoding) : new InputStreamReader(is);
                    copyRange(reader2, new PrintWriter(buffer));
                    if (is != null) {
                        if (0 != 0) {
                            try {
                                is.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            is.close();
                        }
                    }
                    if (reader2 != null) {
                        try {
                            reader2.close();
                        } catch (IOException e3) {
                        }
                    }
                    return buffer.toString();
                } catch (Throwable th3) {
                    try {
                        throw th3;
                    } catch (Throwable th4) {
                        if (is != null) {
                            if (th3 != null) {
                                try {
                                    is.close();
                                } catch (Throwable th5) {
                                    th3.addSuppressed(th5);
                                }
                            } else {
                                is.close();
                            }
                        }
                        throw th4;
                    }
                }
            } catch (Throwable th6) {
                if (0 != 0) {
                    try {
                        reader.close();
                    } catch (IOException e4) {
                    }
                }
                throw th6;
            }
        }
        return null;
    }

    protected Source findXsltSource(WebResource directory) throws IOException {
        File f;
        InputStream is;
        if (this.localXsltFile != null) {
            WebResource resource = this.resources.getResource(directory.getWebappPath() + this.localXsltFile);
            if (resource.isFile() && (is = resource.getInputStream()) != null) {
                if (Globals.IS_SECURITY_ENABLED) {
                    return secureXslt(is);
                }
                return new StreamSource(is);
            } else if (this.debug > 10) {
                log("localXsltFile '" + this.localXsltFile + "' not found");
            }
        }
        if (this.contextXsltFile != null) {
            InputStream is2 = getServletContext().getResourceAsStream(this.contextXsltFile);
            if (is2 != null) {
                if (Globals.IS_SECURITY_ENABLED) {
                    return secureXslt(is2);
                }
                return new StreamSource(is2);
            } else if (this.debug > 10) {
                log("contextXsltFile '" + this.contextXsltFile + "' not found");
            }
        }
        if (this.globalXsltFile != null && (f = validateGlobalXsltFile()) != null) {
            FileInputStream fis = new FileInputStream(f);
            Throwable th = null;
            try {
                byte[] b = new byte[(int) f.length()];
                fis.read(b);
                StreamSource streamSource = new StreamSource(new ByteArrayInputStream(b));
                if (fis != null) {
                    if (0 != 0) {
                        try {
                            fis.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        fis.close();
                    }
                }
                return streamSource;
            } catch (Throwable th3) {
                try {
                    throw th3;
                } catch (Throwable th4) {
                    if (fis != null) {
                        if (th3 != null) {
                            try {
                                fis.close();
                            } catch (Throwable th5) {
                                th3.addSuppressed(th5);
                            }
                        } else {
                            fis.close();
                        }
                    }
                    throw th4;
                }
            }
        }
        return null;
    }

    private File validateGlobalXsltFile() {
        Context context = this.resources.getContext();
        File baseConf = new File(context.getCatalinaBase(), "conf");
        File result = validateGlobalXsltFile(baseConf);
        if (result == null) {
            File homeConf = new File(context.getCatalinaHome(), "conf");
            if (!baseConf.equals(homeConf)) {
                result = validateGlobalXsltFile(homeConf);
            }
        }
        return result;
    }

    private File validateGlobalXsltFile(File base) {
        File candidate = new File(this.globalXsltFile);
        if (!candidate.isAbsolute()) {
            candidate = new File(base, this.globalXsltFile);
        }
        if (!candidate.isFile()) {
            return null;
        }
        try {
            if (!candidate.getCanonicalPath().startsWith(base.getCanonicalPath())) {
                return null;
            }
            String nameLower = candidate.getName().toLowerCase(Locale.ENGLISH);
            if (!nameLower.endsWith(".xslt") && !nameLower.endsWith(".xsl")) {
                return null;
            }
            return candidate;
        } catch (IOException e) {
            return null;
        }
    }

    private Source secureXslt(InputStream is) {
        Source result = null;
        try {
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                builder.setEntityResolver(secureEntityResolver);
                Document document = builder.parse(is);
                result = new DOMSource(document);
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
            } catch (IOException | ParserConfigurationException | SAXException e2) {
                if (this.debug > 0) {
                    log(e2.getMessage(), e2);
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e3) {
                    }
                }
            }
            return result;
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e4) {
                }
            }
            throw th;
        }
    }

    protected boolean checkSendfile(HttpServletRequest request, HttpServletResponse response, WebResource resource, long length, Range range) {
        String canonicalPath;
        if (this.sendfileSize > 0 && length > this.sendfileSize && Boolean.TRUE.equals(request.getAttribute("org.apache.tomcat.sendfile.support")) && request.getClass().getName().equals("org.apache.catalina.connector.RequestFacade") && response.getClass().getName().equals("org.apache.catalina.connector.ResponseFacade") && resource.isFile() && (canonicalPath = resource.getCanonicalPath()) != null) {
            request.setAttribute("org.apache.tomcat.sendfile.filename", canonicalPath);
            if (range == null) {
                request.setAttribute("org.apache.tomcat.sendfile.start", 0L);
                request.setAttribute("org.apache.tomcat.sendfile.end", Long.valueOf(length));
                return true;
            }
            request.setAttribute("org.apache.tomcat.sendfile.start", Long.valueOf(range.start));
            request.setAttribute("org.apache.tomcat.sendfile.end", Long.valueOf(range.end + serialVersionUID));
            return true;
        }
        return false;
    }

    protected boolean checkIfMatch(HttpServletRequest request, HttpServletResponse response, WebResource resource) throws IOException {
        String eTag = resource.getETag();
        String headerValue = request.getHeader(HttpHeaders.IF_MATCH);
        if (headerValue != null && headerValue.indexOf(42) == -1) {
            StringTokenizer commaTokenizer = new StringTokenizer(headerValue, ",");
            boolean conditionSatisfied = false;
            while (!conditionSatisfied && commaTokenizer.hasMoreTokens()) {
                String currentToken = commaTokenizer.nextToken();
                if (currentToken.trim().equals(eTag)) {
                    conditionSatisfied = true;
                }
            }
            if (!conditionSatisfied) {
                response.sendError(412);
                return false;
            }
            return true;
        }
        return true;
    }

    protected boolean checkIfModifiedSince(HttpServletRequest request, HttpServletResponse response, WebResource resource) {
        try {
            long headerValue = request.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
            long lastModified = resource.getLastModified();
            if (headerValue != -1 && request.getHeader(HttpHeaders.IF_NONE_MATCH) == null && lastModified < headerValue + 1000) {
                response.setStatus(304);
                response.setHeader(HttpHeaders.ETAG, resource.getETag());
                return false;
            }
            return true;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    protected boolean checkIfNoneMatch(HttpServletRequest request, HttpServletResponse response, WebResource resource) throws IOException {
        String eTag = resource.getETag();
        String headerValue = request.getHeader(HttpHeaders.IF_NONE_MATCH);
        if (headerValue != null) {
            boolean conditionSatisfied = false;
            if (!headerValue.equals("*")) {
                StringTokenizer commaTokenizer = new StringTokenizer(headerValue, ",");
                while (!conditionSatisfied && commaTokenizer.hasMoreTokens()) {
                    String currentToken = commaTokenizer.nextToken();
                    if (currentToken.trim().equals(eTag)) {
                        conditionSatisfied = true;
                    }
                }
            } else {
                conditionSatisfied = true;
            }
            if (conditionSatisfied) {
                if ("GET".equals(request.getMethod()) || WebContentGenerator.METHOD_HEAD.equals(request.getMethod())) {
                    response.setStatus(304);
                    response.setHeader(HttpHeaders.ETAG, eTag);
                    return false;
                }
                response.sendError(412);
                return false;
            }
            return true;
        }
        return true;
    }

    protected boolean checkIfUnmodifiedSince(HttpServletRequest request, HttpServletResponse response, WebResource resource) throws IOException {
        try {
            long lastModified = resource.getLastModified();
            long headerValue = request.getDateHeader(HttpHeaders.IF_UNMODIFIED_SINCE);
            if (headerValue != -1 && lastModified >= headerValue + 1000) {
                response.sendError(412);
                return false;
            }
            return true;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    protected void copy(InputStream is, ServletOutputStream ostream) throws IOException {
        InputStream istream = new BufferedInputStream(is, this.input);
        IOException exception = copyRange(istream, ostream);
        istream.close();
        if (exception != null) {
            throw exception;
        }
    }

    protected void copy(InputStream is, PrintWriter writer, String encoding) throws IOException {
        Reader reader;
        if (encoding == null) {
            reader = new InputStreamReader(is);
        } else {
            reader = new InputStreamReader(is, encoding);
        }
        IOException exception = copyRange(reader, writer);
        reader.close();
        if (exception != null) {
            throw exception;
        }
    }

    protected void copy(WebResource resource, ServletOutputStream ostream, Range range) throws IOException {
        InputStream resourceInputStream = resource.getInputStream();
        InputStream istream = new BufferedInputStream(resourceInputStream, this.input);
        IOException exception = copyRange(istream, ostream, range.start, range.end);
        istream.close();
        if (exception != null) {
            throw exception;
        }
    }

    protected void copy(WebResource resource, ServletOutputStream ostream, Iterator<Range> ranges, String contentType) throws IOException {
        IOException exception = null;
        while (exception == null && ranges.hasNext()) {
            InputStream resourceInputStream = resource.getInputStream();
            InputStream istream = new BufferedInputStream(resourceInputStream, this.input);
            Throwable th = null;
            try {
                Range currentRange = ranges.next();
                ostream.println();
                ostream.println("--CATALINA_MIME_BOUNDARY");
                if (contentType != null) {
                    ostream.println("Content-Type: " + contentType);
                }
                ostream.println("Content-Range: bytes " + currentRange.start + "-" + currentRange.end + "/" + currentRange.length);
                ostream.println();
                exception = copyRange(istream, ostream, currentRange.start, currentRange.end);
                if (istream != null) {
                    if (0 != 0) {
                        try {
                            istream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        istream.close();
                    }
                }
            } finally {
            }
        }
        ostream.println();
        ostream.print("--CATALINA_MIME_BOUNDARY--");
        if (exception != null) {
            throw exception;
        }
    }

    protected IOException copyRange(InputStream istream, ServletOutputStream ostream) {
        IOException exception = null;
        byte[] buffer = new byte[this.input];
        int length = buffer.length;
        while (true) {
            try {
                int len = istream.read(buffer);
                if (len == -1) {
                    break;
                }
                ostream.write(buffer, 0, len);
            } catch (IOException e) {
                exception = e;
            }
        }
        return exception;
    }

    protected IOException copyRange(Reader reader, PrintWriter writer) {
        IOException exception = null;
        char[] buffer = new char[this.input];
        int length = buffer.length;
        while (true) {
            try {
                int len = reader.read(buffer);
                if (len == -1) {
                    break;
                }
                writer.write(buffer, 0, len);
            } catch (IOException e) {
                exception = e;
            }
        }
        return exception;
    }

    protected IOException copyRange(InputStream istream, ServletOutputStream ostream, long start, long end) {
        if (this.debug > 10) {
            log("Serving bytes:" + start + "-" + end);
        }
        try {
            long skipped = istream.skip(start);
            if (skipped < start) {
                return new IOException(sm.getString("defaultservlet.skipfail", Long.valueOf(skipped), Long.valueOf(start)));
            }
            IOException exception = null;
            long bytesToRead = (end - start) + serialVersionUID;
            byte[] buffer = new byte[this.input];
            int len = buffer.length;
            while (bytesToRead > 0 && len >= buffer.length) {
                try {
                    len = istream.read(buffer);
                    if (bytesToRead >= len) {
                        ostream.write(buffer, 0, len);
                        bytesToRead -= len;
                    } else {
                        ostream.write(buffer, 0, (int) bytesToRead);
                        bytesToRead = 0;
                    }
                } catch (IOException e) {
                    exception = e;
                    len = -1;
                }
                if (len < buffer.length) {
                    break;
                }
            }
            return exception;
        } catch (IOException e2) {
            return e2;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/servlets/DefaultServlet$Range.class */
    public static class Range {
        public long start;
        public long end;
        public long length;

        protected Range() {
        }

        public boolean validate() {
            if (this.end >= this.length) {
                this.end = this.length - DefaultServlet.serialVersionUID;
            }
            return this.start >= 0 && this.end >= 0 && this.start <= this.end && this.length > 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/servlets/DefaultServlet$CompressionFormat.class */
    public static class CompressionFormat implements Serializable {
        private static final long serialVersionUID = 1;
        public final String extension;
        public final String encoding;

        public CompressionFormat(String extension, String encoding) {
            this.extension = extension;
            this.encoding = encoding;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/servlets/DefaultServlet$PrecompressedResource.class */
    public static class PrecompressedResource {
        public final WebResource resource;
        public final CompressionFormat format;

        private PrecompressedResource(WebResource resource, CompressionFormat format) {
            this.resource = resource;
            this.format = format;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/servlets/DefaultServlet$SecureEntityResolver.class */
    public static class SecureEntityResolver implements EntityResolver2 {
        private SecureEntityResolver() {
        }

        @Override // org.xml.sax.EntityResolver
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            throw new SAXException(DefaultServlet.sm.getString("defaultServlet.blockExternalEntity", publicId, systemId));
        }

        @Override // org.xml.sax.ext.EntityResolver2
        public InputSource getExternalSubset(String name, String baseURI) throws SAXException, IOException {
            throw new SAXException(DefaultServlet.sm.getString("defaultServlet.blockExternalSubset", name, baseURI));
        }

        @Override // org.xml.sax.ext.EntityResolver2
        public InputSource resolveEntity(String name, String publicId, String baseURI, String systemId) throws SAXException, IOException {
            throw new SAXException(DefaultServlet.sm.getString("defaultServlet.blockExternalEntity2", name, publicId, baseURI, systemId));
        }
    }
}