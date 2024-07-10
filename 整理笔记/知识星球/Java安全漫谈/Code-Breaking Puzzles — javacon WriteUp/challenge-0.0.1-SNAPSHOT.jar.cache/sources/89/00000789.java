package org.apache.catalina.authenticator;

import ch.qos.logback.classic.spi.CallerData;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Realm;
import org.apache.catalina.connector.Request;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.http.parser.Authorization;
import org.apache.tomcat.util.security.ConcurrentMessageDigest;
import org.apache.tomcat.util.security.MD5Encoder;
import org.thymeleaf.standard.expression.StandardExpressionObjectFactory;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/authenticator/DigestAuthenticator.class */
public class DigestAuthenticator extends AuthenticatorBase {
    protected static final String QOP = "auth";
    protected Map<String, NonceInfo> nonces;
    protected String opaque;
    private final Log log = LogFactory.getLog(DigestAuthenticator.class);
    protected long lastTimestamp = 0;
    protected final Object lastTimestampLock = new Object();
    protected int nonceCacheSize = 1000;
    protected int nonceCountWindowSize = 100;
    protected String key = null;
    protected long nonceValidity = 300000;
    protected boolean validateUri = true;

    public DigestAuthenticator() {
        setCache(false);
    }

    public int getNonceCountWindowSize() {
        return this.nonceCountWindowSize;
    }

    public void setNonceCountWindowSize(int nonceCountWindowSize) {
        this.nonceCountWindowSize = nonceCountWindowSize;
    }

    public int getNonceCacheSize() {
        return this.nonceCacheSize;
    }

    public void setNonceCacheSize(int nonceCacheSize) {
        this.nonceCacheSize = nonceCacheSize;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getNonceValidity() {
        return this.nonceValidity;
    }

    public void setNonceValidity(long nonceValidity) {
        this.nonceValidity = nonceValidity;
    }

    public String getOpaque() {
        return this.opaque;
    }

    public void setOpaque(String opaque) {
        this.opaque = opaque;
    }

    public boolean isValidateUri() {
        return this.validateUri;
    }

    public void setValidateUri(boolean validateUri) {
        this.validateUri = validateUri;
    }

    @Override // org.apache.catalina.authenticator.AuthenticatorBase
    protected boolean doAuthenticate(Request request, HttpServletResponse response) throws IOException {
        if (checkForCachedAuthentication(request, response, false)) {
            return true;
        }
        Principal principal = null;
        String authorization = request.getHeader("authorization");
        DigestInfo digestInfo = new DigestInfo(getOpaque(), getNonceValidity(), getKey(), this.nonces, isValidateUri());
        if (authorization != null && digestInfo.parse(request, authorization)) {
            if (digestInfo.validate(request)) {
                principal = digestInfo.authenticate(this.context.getRealm());
            }
            if (principal != null && !digestInfo.isNonceStale()) {
                register(request, response, principal, HttpServletRequest.DIGEST_AUTH, digestInfo.getUsername(), null);
                return true;
            }
        }
        String nonce = generateNonce(request);
        setAuthenticateHeader(request, response, nonce, principal != null && digestInfo.isNonceStale());
        response.sendError(401);
        return false;
    }

    @Override // org.apache.catalina.authenticator.AuthenticatorBase
    protected String getAuthMethod() {
        return HttpServletRequest.DIGEST_AUTH;
    }

    protected static String removeQuotes(String quotedString, boolean quotesRequired) {
        if (quotedString.length() > 0 && quotedString.charAt(0) != '\"' && !quotesRequired) {
            return quotedString;
        }
        if (quotedString.length() > 2) {
            return quotedString.substring(1, quotedString.length() - 1);
        }
        return "";
    }

    protected static String removeQuotes(String quotedString) {
        return removeQuotes(quotedString, false);
    }

    /* JADX WARN: Type inference failed for: r0v18, types: [byte[], byte[][]] */
    protected String generateNonce(Request request) {
        long currentTime = System.currentTimeMillis();
        synchronized (this.lastTimestampLock) {
            if (currentTime > this.lastTimestamp) {
                this.lastTimestamp = currentTime;
            } else {
                long j = this.lastTimestamp + 1;
                this.lastTimestamp = j;
                currentTime = j;
            }
        }
        String ipTimeKey = request.getRemoteAddr() + ":" + currentTime + ":" + getKey();
        byte[] buffer = ConcurrentMessageDigest.digestMD5(new byte[]{ipTimeKey.getBytes(StandardCharsets.ISO_8859_1)});
        String nonce = currentTime + ":" + MD5Encoder.encode(buffer);
        NonceInfo info = new NonceInfo(currentTime, getNonceCountWindowSize());
        synchronized (this.nonces) {
            this.nonces.put(nonce, info);
        }
        return nonce;
    }

    protected void setAuthenticateHeader(HttpServletRequest request, HttpServletResponse response, String nonce, boolean isNonceStale) {
        String authenticateHeader;
        String realmName = getRealmName(this.context);
        if (isNonceStale) {
            authenticateHeader = "Digest realm=\"" + realmName + "\", qop=\"auth\", nonce=\"" + nonce + "\", opaque=\"" + getOpaque() + "\", stale=true";
        } else {
            authenticateHeader = "Digest realm=\"" + realmName + "\", qop=\"auth\", nonce=\"" + nonce + "\", opaque=\"" + getOpaque() + "\"";
        }
        response.setHeader("WWW-Authenticate", authenticateHeader);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.authenticator.AuthenticatorBase, org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        super.startInternal();
        if (getKey() == null) {
            setKey(this.sessionIdGenerator.generateSessionId());
        }
        if (getOpaque() == null) {
            setOpaque(this.sessionIdGenerator.generateSessionId());
        }
        this.nonces = new LinkedHashMap<String, NonceInfo>() { // from class: org.apache.catalina.authenticator.DigestAuthenticator.1
            private static final long serialVersionUID = 1;
            private static final long LOG_SUPPRESS_TIME = 300000;
            private long lastLog = 0;

            @Override // java.util.LinkedHashMap
            protected boolean removeEldestEntry(Map.Entry<String, NonceInfo> eldest) {
                long currentTime = System.currentTimeMillis();
                if (size() > DigestAuthenticator.this.getNonceCacheSize()) {
                    if (this.lastLog < currentTime && currentTime - eldest.getValue().getTimestamp() < DigestAuthenticator.this.getNonceValidity()) {
                        DigestAuthenticator.this.log.warn(AuthenticatorBase.sm.getString("digestAuthenticator.cacheRemove"));
                        this.lastLog = currentTime + LOG_SUPPRESS_TIME;
                        return true;
                    }
                    return true;
                }
                return false;
            }
        };
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/authenticator/DigestAuthenticator$DigestInfo.class */
    public static class DigestInfo {
        private final String opaque;
        private final long nonceValidity;
        private final String key;
        private final Map<String, NonceInfo> nonces;
        private boolean validateUri;
        private String userName = null;
        private String method = null;
        private String uri = null;
        private String response = null;
        private String nonce = null;
        private String nc = null;
        private String cnonce = null;
        private String realmName = null;
        private String qop = null;
        private String opaqueReceived = null;
        private boolean nonceStale = false;

        public DigestInfo(String opaque, long nonceValidity, String key, Map<String, NonceInfo> nonces, boolean validateUri) {
            this.validateUri = true;
            this.opaque = opaque;
            this.nonceValidity = nonceValidity;
            this.key = key;
            this.nonces = nonces;
            this.validateUri = validateUri;
        }

        public String getUsername() {
            return this.userName;
        }

        public boolean parse(Request request, String authorization) {
            if (authorization == null) {
                return false;
            }
            try {
                Map<String, String> directives = Authorization.parseAuthorizationDigest(new StringReader(authorization));
                if (directives == null) {
                    return false;
                }
                this.method = request.getMethod();
                this.userName = directives.get("username");
                this.realmName = directives.get("realm");
                this.nonce = directives.get("nonce");
                this.nc = directives.get("nc");
                this.cnonce = directives.get("cnonce");
                this.qop = directives.get("qop");
                this.uri = directives.get("uri");
                this.response = directives.get(StandardExpressionObjectFactory.RESPONSE_EXPRESSION_OBJECT_NAME);
                this.opaqueReceived = directives.get("opaque");
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        /* JADX WARN: Type inference failed for: r0v53, types: [byte[], byte[][]] */
        public boolean validate(Request request) {
            int i;
            NonceInfo info;
            String uriQuery;
            if (this.userName == null || this.realmName == null || this.nonce == null || this.uri == null || this.response == null) {
                return false;
            }
            if (this.validateUri) {
                String query = request.getQueryString();
                if (query == null) {
                    uriQuery = request.getRequestURI();
                } else {
                    uriQuery = request.getRequestURI() + CallerData.NA + query;
                }
                if (!this.uri.equals(uriQuery)) {
                    String host = request.getHeader("host");
                    String scheme = request.getScheme();
                    if (host != null && !uriQuery.startsWith(scheme)) {
                        if (!this.uri.equals(scheme + "://" + host + uriQuery)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
            String lcRealm = AuthenticatorBase.getRealmName(request.getContext());
            if (!lcRealm.equals(this.realmName) || !this.opaque.equals(this.opaqueReceived) || (i = this.nonce.indexOf(58)) < 0 || i + 1 == this.nonce.length()) {
                return false;
            }
            try {
                long nonceTime = Long.parseLong(this.nonce.substring(0, i));
                String md5clientIpTimeKey = this.nonce.substring(i + 1);
                long currentTime = System.currentTimeMillis();
                if (currentTime - nonceTime > this.nonceValidity) {
                    this.nonceStale = true;
                    synchronized (this.nonces) {
                        this.nonces.remove(this.nonce);
                    }
                }
                String serverIpTimeKey = request.getRemoteAddr() + ":" + nonceTime + ":" + this.key;
                byte[] buffer = ConcurrentMessageDigest.digestMD5(new byte[]{serverIpTimeKey.getBytes(StandardCharsets.ISO_8859_1)});
                String md5ServerIpTimeKey = MD5Encoder.encode(buffer);
                if (!md5ServerIpTimeKey.equals(md5clientIpTimeKey)) {
                    return false;
                }
                if (this.qop != null && !"auth".equals(this.qop)) {
                    return false;
                }
                if (this.qop == null) {
                    if (this.cnonce != null || this.nc != null) {
                        return false;
                    }
                    return true;
                } else if (this.cnonce == null || this.nc == null || this.nc.length() < 6 || this.nc.length() > 8) {
                    return false;
                } else {
                    try {
                        long count = Long.parseLong(this.nc, 16);
                        synchronized (this.nonces) {
                            info = this.nonces.get(this.nonce);
                        }
                        if (info == null) {
                            this.nonceStale = true;
                            return true;
                        } else if (!info.nonceCountValid(count)) {
                            return false;
                        } else {
                            return true;
                        }
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
            } catch (NumberFormatException e2) {
                return false;
            }
        }

        public boolean isNonceStale() {
            return this.nonceStale;
        }

        /* JADX WARN: Type inference failed for: r0v6, types: [byte[], byte[][]] */
        public Principal authenticate(Realm realm) {
            String a2 = this.method + ":" + this.uri;
            byte[] buffer = ConcurrentMessageDigest.digestMD5(new byte[]{a2.getBytes(StandardCharsets.ISO_8859_1)});
            String md5a2 = MD5Encoder.encode(buffer);
            return realm.authenticate(this.userName, this.response, this.nonce, this.nc, this.cnonce, this.qop, this.realmName, md5a2);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/authenticator/DigestAuthenticator$NonceInfo.class */
    public static class NonceInfo {
        private final long timestamp;
        private final boolean[] seen;
        private final int offset;
        private int count = 0;

        public NonceInfo(long currentTime, int seenWindowSize) {
            this.timestamp = currentTime;
            this.seen = new boolean[seenWindowSize];
            this.offset = seenWindowSize / 2;
        }

        public synchronized boolean nonceCountValid(long nonceCount) {
            if (this.count - this.offset >= nonceCount || nonceCount > (this.count - this.offset) + this.seen.length) {
                return false;
            }
            int checkIndex = (int) ((nonceCount + this.offset) % this.seen.length);
            if (this.seen[checkIndex]) {
                return false;
            }
            this.seen[checkIndex] = true;
            this.seen[this.count % this.seen.length] = false;
            this.count++;
            return true;
        }

        public long getTimestamp() {
            return this.timestamp;
        }
    }
}