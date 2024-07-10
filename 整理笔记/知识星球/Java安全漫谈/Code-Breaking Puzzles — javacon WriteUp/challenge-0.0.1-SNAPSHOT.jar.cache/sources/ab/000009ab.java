package org.apache.catalina.valves.rewrite;

import java.nio.charset.Charset;
import java.util.Calendar;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.connector.Request;
import org.apache.tomcat.util.http.FastHttpDateFormat;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/rewrite/ResolverImpl.class */
public class ResolverImpl extends Resolver {
    protected Request request;

    public ResolverImpl(Request request) {
        this.request = null;
        this.request = request;
    }

    @Override // org.apache.catalina.valves.rewrite.Resolver
    public String resolve(String key) {
        int index;
        if (key.equals("HTTP_USER_AGENT")) {
            return this.request.getHeader("user-agent");
        }
        if (key.equals("HTTP_REFERER")) {
            return this.request.getHeader("referer");
        }
        if (key.equals("HTTP_COOKIE")) {
            return this.request.getHeader("cookie");
        }
        if (key.equals("HTTP_FORWARDED")) {
            return this.request.getHeader("forwarded");
        }
        if (key.equals("HTTP_HOST")) {
            String host = this.request.getHeader("host");
            if (host != null && (index = host.indexOf(58)) != -1) {
                host = host.substring(0, index);
            }
            return host;
        } else if (key.equals("HTTP_PROXY_CONNECTION")) {
            return this.request.getHeader("proxy-connection");
        } else {
            if (key.equals("HTTP_ACCEPT")) {
                return this.request.getHeader("accept");
            }
            if (key.equals("REMOTE_ADDR")) {
                return this.request.getRemoteAddr();
            }
            if (key.equals("REMOTE_HOST")) {
                return this.request.getRemoteHost();
            }
            if (key.equals("REMOTE_PORT")) {
                return String.valueOf(this.request.getRemotePort());
            }
            if (key.equals("REMOTE_USER")) {
                return this.request.getRemoteUser();
            }
            if (key.equals("REMOTE_IDENT")) {
                return this.request.getRemoteUser();
            }
            if (key.equals("REQUEST_METHOD")) {
                return this.request.getMethod();
            }
            if (key.equals("SCRIPT_FILENAME")) {
                return this.request.getServletContext().getRealPath(this.request.getServletPath());
            }
            if (key.equals("REQUEST_PATH")) {
                return this.request.getRequestPathMB().toString();
            }
            if (key.equals("CONTEXT_PATH")) {
                return this.request.getContextPath();
            }
            if (key.equals("SERVLET_PATH")) {
                return emptyStringIfNull(this.request.getServletPath());
            }
            if (key.equals("PATH_INFO")) {
                return emptyStringIfNull(this.request.getPathInfo());
            }
            if (key.equals("QUERY_STRING")) {
                return emptyStringIfNull(this.request.getQueryString());
            }
            if (key.equals("AUTH_TYPE")) {
                return this.request.getAuthType();
            }
            if (key.equals("DOCUMENT_ROOT")) {
                return this.request.getServletContext().getRealPath("/");
            }
            if (key.equals("SERVER_NAME")) {
                return this.request.getLocalName();
            }
            if (key.equals("SERVER_ADDR")) {
                return this.request.getLocalAddr();
            }
            if (key.equals("SERVER_PORT")) {
                return String.valueOf(this.request.getLocalPort());
            }
            if (key.equals("SERVER_PROTOCOL")) {
                return this.request.getProtocol();
            }
            if (key.equals("SERVER_SOFTWARE")) {
                return "tomcat";
            }
            if (key.equals("THE_REQUEST")) {
                return this.request.getMethod() + " " + this.request.getRequestURI() + " " + this.request.getProtocol();
            }
            if (key.equals("REQUEST_URI")) {
                return this.request.getRequestURI();
            }
            if (key.equals("REQUEST_FILENAME")) {
                return this.request.getPathTranslated();
            }
            if (key.equals("HTTPS")) {
                return this.request.isSecure() ? CustomBooleanEditor.VALUE_ON : CustomBooleanEditor.VALUE_OFF;
            } else if (key.equals("TIME_YEAR")) {
                return String.valueOf(Calendar.getInstance().get(1));
            } else {
                if (key.equals("TIME_MON")) {
                    return String.valueOf(Calendar.getInstance().get(2));
                }
                if (key.equals("TIME_DAY")) {
                    return String.valueOf(Calendar.getInstance().get(5));
                }
                if (key.equals("TIME_HOUR")) {
                    return String.valueOf(Calendar.getInstance().get(11));
                }
                if (key.equals("TIME_MIN")) {
                    return String.valueOf(Calendar.getInstance().get(12));
                }
                if (key.equals("TIME_SEC")) {
                    return String.valueOf(Calendar.getInstance().get(13));
                }
                if (key.equals("TIME_WDAY")) {
                    return String.valueOf(Calendar.getInstance().get(7));
                }
                if (key.equals("TIME")) {
                    return FastHttpDateFormat.getCurrentDate();
                }
                return null;
            }
        }
    }

    @Override // org.apache.catalina.valves.rewrite.Resolver
    public String resolveEnv(String key) {
        Object result = this.request.getAttribute(key);
        return result != null ? result.toString() : System.getProperty(key);
    }

    @Override // org.apache.catalina.valves.rewrite.Resolver
    public String resolveSsl(String key) {
        return null;
    }

    @Override // org.apache.catalina.valves.rewrite.Resolver
    public String resolveHttp(String key) {
        String header = this.request.getHeader(key);
        if (header == null) {
            return "";
        }
        return header;
    }

    @Override // org.apache.catalina.valves.rewrite.Resolver
    public boolean resolveResource(int type, String name) {
        WebResourceRoot resources = this.request.getContext().getResources();
        WebResource resource = resources.getResource(name);
        if (!resource.exists()) {
            return false;
        }
        switch (type) {
            case 0:
                return resource.isDirectory();
            case 1:
                return resource.isFile();
            case 2:
                return resource.isFile() && resource.getContentLength() > 0;
            default:
                return false;
        }
    }

    private static final String emptyStringIfNull(String value) {
        if (value == null) {
            return "";
        }
        return value;
    }

    @Override // org.apache.catalina.valves.rewrite.Resolver
    public Charset getUriCharset() {
        return this.request.getConnector().getURICharset();
    }
}