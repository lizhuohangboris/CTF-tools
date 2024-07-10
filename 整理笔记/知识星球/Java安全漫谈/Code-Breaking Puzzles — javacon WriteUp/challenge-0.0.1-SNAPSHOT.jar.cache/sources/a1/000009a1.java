package org.apache.catalina.valves;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import org.apache.catalina.AccessLog;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.http.MimeHeaders;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/RemoteIpValve.class */
public class RemoteIpValve extends ValveBase {
    private static final Pattern commaSeparatedValuesPattern = Pattern.compile("\\s*,\\s*");
    private static final Log log = LogFactory.getLog(RemoteIpValve.class);
    private int httpServerPort;
    private int httpsServerPort;
    private boolean changeLocalPort;
    private Pattern internalProxies;
    private String protocolHeader;
    private String protocolHeaderHttpsValue;
    private String portHeader;
    private String proxiesHeader;
    private String remoteIpHeader;
    private boolean requestAttributesEnabled;
    private Pattern trustedProxies;

    protected static String[] commaDelimitedListToStringArray(String commaDelimitedStrings) {
        return (commaDelimitedStrings == null || commaDelimitedStrings.length() == 0) ? new String[0] : commaSeparatedValuesPattern.split(commaDelimitedStrings);
    }

    protected static String listToCommaDelimitedString(List<String> stringList) {
        if (stringList == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        Iterator<String> it = stringList.iterator();
        while (it.hasNext()) {
            Object element = it.next();
            if (element != null) {
                result.append(element);
                if (it.hasNext()) {
                    result.append(", ");
                }
            }
        }
        return result.toString();
    }

    public RemoteIpValve() {
        super(true);
        this.httpServerPort = 80;
        this.httpsServerPort = 443;
        this.changeLocalPort = false;
        this.internalProxies = Pattern.compile("10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|192\\.168\\.\\d{1,3}\\.\\d{1,3}|169\\.254\\.\\d{1,3}\\.\\d{1,3}|127\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|172\\.1[6-9]{1}\\.\\d{1,3}\\.\\d{1,3}|172\\.2[0-9]{1}\\.\\d{1,3}\\.\\d{1,3}|172\\.3[0-1]{1}\\.\\d{1,3}\\.\\d{1,3}|0:0:0:0:0:0:0:1|::1");
        this.protocolHeader = null;
        this.protocolHeaderHttpsValue = "https";
        this.portHeader = null;
        this.proxiesHeader = "X-Forwarded-By";
        this.remoteIpHeader = "X-Forwarded-For";
        this.requestAttributesEnabled = true;
        this.trustedProxies = null;
    }

    public int getHttpsServerPort() {
        return this.httpsServerPort;
    }

    public int getHttpServerPort() {
        return this.httpServerPort;
    }

    public boolean isChangeLocalPort() {
        return this.changeLocalPort;
    }

    public void setChangeLocalPort(boolean changeLocalPort) {
        this.changeLocalPort = changeLocalPort;
    }

    public String getPortHeader() {
        return this.portHeader;
    }

    public void setPortHeader(String portHeader) {
        this.portHeader = portHeader;
    }

    public String getInternalProxies() {
        if (this.internalProxies == null) {
            return null;
        }
        return this.internalProxies.toString();
    }

    public String getProtocolHeader() {
        return this.protocolHeader;
    }

    public String getProtocolHeaderHttpsValue() {
        return this.protocolHeaderHttpsValue;
    }

    public String getProxiesHeader() {
        return this.proxiesHeader;
    }

    public String getRemoteIpHeader() {
        return this.remoteIpHeader;
    }

    public boolean getRequestAttributesEnabled() {
        return this.requestAttributesEnabled;
    }

    public String getTrustedProxies() {
        if (this.trustedProxies == null) {
            return null;
        }
        return this.trustedProxies.toString();
    }

    @Override // org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws IOException, ServletException {
        String protocolHeaderValue;
        String originalRemoteAddr = request.getRemoteAddr();
        String originalRemoteHost = request.getRemoteHost();
        String originalScheme = request.getScheme();
        boolean originalSecure = request.isSecure();
        int originalServerPort = request.getServerPort();
        String originalProxiesHeader = request.getHeader(this.proxiesHeader);
        String originalRemoteIpHeader = request.getHeader(this.remoteIpHeader);
        boolean isInternal = this.internalProxies != null && this.internalProxies.matcher(originalRemoteAddr).matches();
        if (isInternal || (this.trustedProxies != null && this.trustedProxies.matcher(originalRemoteAddr).matches())) {
            String remoteIp = null;
            LinkedList<String> proxiesHeaderValue = new LinkedList<>();
            StringBuilder concatRemoteIpHeaderValue = new StringBuilder();
            Enumeration<String> e = request.getHeaders(this.remoteIpHeader);
            while (e.hasMoreElements()) {
                if (concatRemoteIpHeaderValue.length() > 0) {
                    concatRemoteIpHeaderValue.append(", ");
                }
                concatRemoteIpHeaderValue.append(e.nextElement());
            }
            String[] remoteIpHeaderValue = commaDelimitedListToStringArray(concatRemoteIpHeaderValue.toString());
            if (!isInternal) {
                proxiesHeaderValue.addFirst(originalRemoteAddr);
            }
            int idx = remoteIpHeaderValue.length - 1;
            while (idx >= 0) {
                String currentRemoteIp = remoteIpHeaderValue[idx];
                remoteIp = currentRemoteIp;
                if (this.internalProxies == null || !this.internalProxies.matcher(currentRemoteIp).matches()) {
                    if (this.trustedProxies != null && this.trustedProxies.matcher(currentRemoteIp).matches()) {
                        proxiesHeaderValue.addFirst(currentRemoteIp);
                    } else {
                        idx--;
                        break;
                    }
                }
                idx--;
            }
            LinkedList<String> newRemoteIpHeaderValue = new LinkedList<>();
            while (idx >= 0) {
                newRemoteIpHeaderValue.addFirst(remoteIpHeaderValue[idx]);
                idx--;
            }
            if (remoteIp != null) {
                request.setRemoteAddr(remoteIp);
                request.setRemoteHost(remoteIp);
                if (proxiesHeaderValue.size() == 0) {
                    request.getCoyoteRequest().getMimeHeaders().removeHeader(this.proxiesHeader);
                } else {
                    String commaDelimitedListOfProxies = listToCommaDelimitedString(proxiesHeaderValue);
                    request.getCoyoteRequest().getMimeHeaders().setValue(this.proxiesHeader).setString(commaDelimitedListOfProxies);
                }
                if (newRemoteIpHeaderValue.size() == 0) {
                    request.getCoyoteRequest().getMimeHeaders().removeHeader(this.remoteIpHeader);
                } else {
                    String commaDelimitedRemoteIpHeaderValue = listToCommaDelimitedString(newRemoteIpHeaderValue);
                    request.getCoyoteRequest().getMimeHeaders().setValue(this.remoteIpHeader).setString(commaDelimitedRemoteIpHeaderValue);
                }
            }
            if (this.protocolHeader != null && (protocolHeaderValue = request.getHeader(this.protocolHeader)) != null) {
                if (this.protocolHeaderHttpsValue.equalsIgnoreCase(protocolHeaderValue)) {
                    request.setSecure(true);
                    request.getCoyoteRequest().scheme().setString("https");
                    setPorts(request, this.httpsServerPort);
                } else {
                    request.setSecure(false);
                    request.getCoyoteRequest().scheme().setString("http");
                    setPorts(request, this.httpServerPort);
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("Incoming request " + request.getRequestURI() + " with originalRemoteAddr '" + originalRemoteAddr + "', originalRemoteHost='" + originalRemoteHost + "', originalSecure='" + originalSecure + "', originalScheme='" + originalScheme + "' will be seen as newRemoteAddr='" + request.getRemoteAddr() + "', newRemoteHost='" + request.getRemoteHost() + "', newScheme='" + request.getScheme() + "', newSecure='" + request.isSecure() + "'");
            }
        } else if (log.isDebugEnabled()) {
            log.debug("Skip RemoteIpValve for request " + request.getRequestURI() + " with originalRemoteAddr '" + request.getRemoteAddr() + "'");
        }
        if (this.requestAttributesEnabled) {
            request.setAttribute(AccessLog.REMOTE_ADDR_ATTRIBUTE, request.getRemoteAddr());
            request.setAttribute("org.apache.tomcat.remoteAddr", request.getRemoteAddr());
            request.setAttribute(AccessLog.REMOTE_HOST_ATTRIBUTE, request.getRemoteHost());
            request.setAttribute(AccessLog.PROTOCOL_ATTRIBUTE, request.getProtocol());
            request.setAttribute(AccessLog.SERVER_PORT_ATTRIBUTE, Integer.valueOf(request.getServerPort()));
        }
        try {
            getNext().invoke(request, response);
            request.setRemoteAddr(originalRemoteAddr);
            request.setRemoteHost(originalRemoteHost);
            request.setSecure(originalSecure);
            MimeHeaders headers = request.getCoyoteRequest().getMimeHeaders();
            request.getCoyoteRequest().scheme().setString(originalScheme);
            request.setServerPort(originalServerPort);
            if (originalProxiesHeader == null || originalProxiesHeader.length() == 0) {
                headers.removeHeader(this.proxiesHeader);
            } else {
                headers.setValue(this.proxiesHeader).setString(originalProxiesHeader);
            }
            if (originalRemoteIpHeader == null || originalRemoteIpHeader.length() == 0) {
                headers.removeHeader(this.remoteIpHeader);
            } else {
                headers.setValue(this.remoteIpHeader).setString(originalRemoteIpHeader);
            }
        } catch (Throwable th) {
            request.setRemoteAddr(originalRemoteAddr);
            request.setRemoteHost(originalRemoteHost);
            request.setSecure(originalSecure);
            MimeHeaders headers2 = request.getCoyoteRequest().getMimeHeaders();
            request.getCoyoteRequest().scheme().setString(originalScheme);
            request.setServerPort(originalServerPort);
            if (originalProxiesHeader == null || originalProxiesHeader.length() == 0) {
                headers2.removeHeader(this.proxiesHeader);
            } else {
                headers2.setValue(this.proxiesHeader).setString(originalProxiesHeader);
            }
            if (originalRemoteIpHeader == null || originalRemoteIpHeader.length() == 0) {
                headers2.removeHeader(this.remoteIpHeader);
            } else {
                headers2.setValue(this.remoteIpHeader).setString(originalRemoteIpHeader);
            }
            throw th;
        }
    }

    private void setPorts(Request request, int defaultPort) {
        String portHeaderValue;
        int port = defaultPort;
        if (this.portHeader != null && (portHeaderValue = request.getHeader(this.portHeader)) != null) {
            try {
                port = Integer.parseInt(portHeaderValue);
            } catch (NumberFormatException nfe) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("remoteIpValve.invalidPortHeader", portHeaderValue, this.portHeader), nfe);
                }
            }
        }
        request.setServerPort(port);
        if (this.changeLocalPort) {
            request.setLocalPort(port);
        }
    }

    public void setHttpServerPort(int httpServerPort) {
        this.httpServerPort = httpServerPort;
    }

    public void setHttpsServerPort(int httpsServerPort) {
        this.httpsServerPort = httpsServerPort;
    }

    public void setInternalProxies(String internalProxies) {
        if (internalProxies == null || internalProxies.length() == 0) {
            this.internalProxies = null;
        } else {
            this.internalProxies = Pattern.compile(internalProxies);
        }
    }

    public void setProtocolHeader(String protocolHeader) {
        this.protocolHeader = protocolHeader;
    }

    public void setProtocolHeaderHttpsValue(String protocolHeaderHttpsValue) {
        this.protocolHeaderHttpsValue = protocolHeaderHttpsValue;
    }

    public void setProxiesHeader(String proxiesHeader) {
        this.proxiesHeader = proxiesHeader;
    }

    public void setRemoteIpHeader(String remoteIpHeader) {
        this.remoteIpHeader = remoteIpHeader;
    }

    public void setRequestAttributesEnabled(boolean requestAttributesEnabled) {
        this.requestAttributesEnabled = requestAttributesEnabled;
    }

    public void setTrustedProxies(String trustedProxies) {
        if (trustedProxies == null || trustedProxies.length() == 0) {
            this.trustedProxies = null;
        } else {
            this.trustedProxies = Pattern.compile(trustedProxies);
        }
    }
}