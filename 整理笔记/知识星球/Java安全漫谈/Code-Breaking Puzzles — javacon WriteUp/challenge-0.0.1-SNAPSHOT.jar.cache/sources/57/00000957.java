package org.apache.catalina.valves;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import org.apache.catalina.AccessLog;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Session;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.TLSUtil;
import org.apache.catalina.valves.Constants;
import org.apache.coyote.ActionCode;
import org.apache.coyote.RequestInfo;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.codec.binary.BaseNCodec;
import org.apache.tomcat.util.collections.SynchronizedStack;
import org.apache.tomcat.util.net.IPv6Utils;
import org.springframework.asm.Opcodes;
import org.springframework.asm.TypeReference;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve.class */
public abstract class AbstractAccessLogValve extends ValveBase implements AccessLog {
    protected boolean enabled;
    private boolean ipv6Canonical;
    protected String pattern;
    private static final int globalCacheSize = 300;
    private static final int localCacheSize = 60;
    protected String condition;
    protected String conditionIf;
    protected String localeName;
    protected Locale locale;
    protected AccessLogElement[] logElements;
    protected boolean requestAttributesEnabled;
    private SynchronizedStack<CharArrayWriter> charArrayWriters;
    private int maxLogMessageBufferSize;
    private boolean tlsAttributeRequired;
    private static final Log log = LogFactory.getLog(AbstractAccessLogValve.class);
    private static final DateFormatCache globalDateCache = new DateFormatCache(300, Locale.getDefault(), null);
    private static final ThreadLocal<DateFormatCache> localDateCache = new ThreadLocal<DateFormatCache>() { // from class: org.apache.catalina.valves.AbstractAccessLogValve.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public DateFormatCache initialValue() {
            return new DateFormatCache(60, Locale.getDefault(), AbstractAccessLogValve.globalDateCache);
        }
    };
    private static final ThreadLocal<Date> localDate = new ThreadLocal<Date>() { // from class: org.apache.catalina.valves.AbstractAccessLogValve.2
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.lang.ThreadLocal
        public Date initialValue() {
            return new Date();
        }
    };

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$AccessLogElement.class */
    public interface AccessLogElement {
        void addElement(CharArrayWriter charArrayWriter, Date date, Request request, Response response, long j);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$FormatType.class */
    private enum FormatType {
        CLF,
        SEC,
        MSEC,
        MSEC_FRAC,
        SDF
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$PortType.class */
    private enum PortType {
        LOCAL,
        REMOTE
    }

    protected abstract void log(CharArrayWriter charArrayWriter);

    public AbstractAccessLogValve() {
        super(true);
        this.enabled = true;
        this.ipv6Canonical = false;
        this.pattern = null;
        this.condition = null;
        this.conditionIf = null;
        this.localeName = Locale.getDefault().toString();
        this.locale = Locale.getDefault();
        this.logElements = null;
        this.requestAttributesEnabled = false;
        this.charArrayWriters = new SynchronizedStack<>();
        this.maxLogMessageBufferSize = 256;
        this.tlsAttributeRequired = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$DateFormatCache.class */
    public static class DateFormatCache {
        private int cacheSize;
        private final Locale cacheDefaultLocale;
        private final DateFormatCache parent;
        protected final Cache cLFCache;
        private final Map<String, Cache> formatCache = new HashMap();

        /* JADX INFO: Access modifiers changed from: protected */
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$DateFormatCache$Cache.class */
        public class Cache {
            private static final String cLFFormat = "dd/MMM/yyyy:HH:mm:ss Z";
            private long previousSeconds;
            private String previousFormat;
            private long first;
            private long last;
            private int offset;
            private final Date currentDate;
            protected final String[] cache;
            private SimpleDateFormat formatter;
            private boolean isCLF;
            private Cache parent;

            private Cache(DateFormatCache this$0, Cache parent) {
                this(this$0, (String) null, parent);
            }

            private Cache(DateFormatCache this$0, String format, Cache parent) {
                this(format, null, parent);
            }

            private Cache(String format, Locale loc, Cache parent) {
                this.previousSeconds = Long.MIN_VALUE;
                this.previousFormat = "";
                this.first = Long.MIN_VALUE;
                this.last = Long.MIN_VALUE;
                this.offset = 0;
                this.currentDate = new Date();
                this.isCLF = false;
                this.parent = null;
                this.cache = new String[DateFormatCache.this.cacheSize];
                for (int i = 0; i < DateFormatCache.this.cacheSize; i++) {
                    this.cache[i] = null;
                }
                loc = loc == null ? DateFormatCache.this.cacheDefaultLocale : loc;
                if (format == null) {
                    this.isCLF = true;
                    this.formatter = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.US);
                } else {
                    this.formatter = new SimpleDateFormat(format, loc);
                }
                this.formatter.setTimeZone(TimeZone.getDefault());
                this.parent = parent;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public String getFormatInternal(long time) {
                long seconds = time / 1000;
                if (seconds == this.previousSeconds) {
                    return this.previousFormat;
                }
                this.previousSeconds = seconds;
                int index = (this.offset + ((int) (seconds - this.first))) % DateFormatCache.this.cacheSize;
                if (index < 0) {
                    index += DateFormatCache.this.cacheSize;
                }
                if (seconds >= this.first && seconds <= this.last) {
                    if (this.cache[index] != null) {
                        this.previousFormat = this.cache[index];
                        return this.previousFormat;
                    }
                } else if (seconds >= this.last + DateFormatCache.this.cacheSize || seconds <= this.first - DateFormatCache.this.cacheSize) {
                    this.first = seconds;
                    this.last = (this.first + DateFormatCache.this.cacheSize) - 1;
                    index = 0;
                    this.offset = 0;
                    for (int i = 1; i < DateFormatCache.this.cacheSize; i++) {
                        this.cache[i] = null;
                    }
                } else if (seconds > this.last) {
                    for (int i2 = 1; i2 < seconds - this.last; i2++) {
                        this.cache[((index + DateFormatCache.this.cacheSize) - i2) % DateFormatCache.this.cacheSize] = null;
                    }
                    this.first = seconds - (DateFormatCache.this.cacheSize - 1);
                    this.last = seconds;
                    this.offset = (index + 1) % DateFormatCache.this.cacheSize;
                } else if (seconds < this.first) {
                    for (int i3 = 1; i3 < this.first - seconds; i3++) {
                        this.cache[(index + i3) % DateFormatCache.this.cacheSize] = null;
                    }
                    this.first = seconds;
                    this.last = seconds + (DateFormatCache.this.cacheSize - 1);
                    this.offset = index;
                }
                if (this.parent != null) {
                    synchronized (this.parent) {
                        this.previousFormat = this.parent.getFormatInternal(time);
                    }
                } else {
                    this.currentDate.setTime(time);
                    this.previousFormat = this.formatter.format(this.currentDate);
                    if (this.isCLF) {
                        StringBuilder current = new StringBuilder(32);
                        current.append('[');
                        current.append(this.previousFormat);
                        current.append(']');
                        this.previousFormat = current.toString();
                    }
                }
                this.cache[index] = this.previousFormat;
                return this.previousFormat;
            }
        }

        protected DateFormatCache(int size, Locale loc, DateFormatCache parent) {
            this.cacheSize = 0;
            this.cacheSize = size;
            this.cacheDefaultLocale = loc;
            this.parent = parent;
            Cache parentCache = null;
            if (parent != null) {
                synchronized (parent) {
                    parentCache = parent.getCache(null, null);
                }
            }
            this.cLFCache = new Cache(parentCache);
        }

        private Cache getCache(String format, Locale loc) {
            Cache cache;
            if (format == null) {
                cache = this.cLFCache;
            } else {
                cache = this.formatCache.get(format);
                if (cache == null) {
                    Cache parentCache = null;
                    if (this.parent != null) {
                        synchronized (this.parent) {
                            parentCache = this.parent.getCache(format, loc);
                        }
                    }
                    cache = new Cache(format, loc, parentCache);
                    this.formatCache.put(format, cache);
                }
            }
            return cache;
        }

        public String getFormat(long time) {
            return this.cLFCache.getFormatInternal(time);
        }

        public String getFormat(String format, Locale loc, long time) {
            return getCache(format, loc).getFormatInternal(time);
        }
    }

    public boolean getIpv6Canonical() {
        return this.ipv6Canonical;
    }

    public void setIpv6Canonical(boolean ipv6Canonical) {
        this.ipv6Canonical = ipv6Canonical;
    }

    @Override // org.apache.catalina.AccessLog
    public void setRequestAttributesEnabled(boolean requestAttributesEnabled) {
        this.requestAttributesEnabled = requestAttributesEnabled;
    }

    @Override // org.apache.catalina.AccessLog
    public boolean getRequestAttributesEnabled() {
        return this.requestAttributesEnabled;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPattern() {
        return this.pattern;
    }

    public void setPattern(String pattern) {
        if (pattern == null) {
            this.pattern = "";
        } else if (pattern.equals(Constants.AccessLog.COMMON_ALIAS)) {
            this.pattern = Constants.AccessLog.COMMON_PATTERN;
        } else if (pattern.equals(Constants.AccessLog.COMBINED_ALIAS)) {
            this.pattern = Constants.AccessLog.COMBINED_PATTERN;
        } else {
            this.pattern = pattern;
        }
        this.logElements = createLogElements();
    }

    public String getCondition() {
        return this.condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getConditionUnless() {
        return getCondition();
    }

    public void setConditionUnless(String condition) {
        setCondition(condition);
    }

    public String getConditionIf() {
        return this.conditionIf;
    }

    public void setConditionIf(String condition) {
        this.conditionIf = condition;
    }

    public String getLocale() {
        return this.localeName;
    }

    public void setLocale(String localeName) {
        this.localeName = localeName;
        this.locale = findLocale(localeName, this.locale);
    }

    @Override // org.apache.catalina.Valve
    public void invoke(Request request, Response response) throws IOException, ServletException {
        if (this.tlsAttributeRequired) {
            request.getAttribute("javax.servlet.request.X509Certificate");
        }
        getNext().invoke(request, response);
    }

    @Override // org.apache.catalina.AccessLog
    public void log(Request request, Response response, long time) {
        if (getState().isAvailable() && getEnabled() && this.logElements != null) {
            if (this.condition == null || null == request.getRequest().getAttribute(this.condition)) {
                if (this.conditionIf != null && null == request.getRequest().getAttribute(this.conditionIf)) {
                    return;
                }
                long start = request.getCoyoteRequest().getStartTime();
                Date date = getDate(start + time);
                CharArrayWriter result = this.charArrayWriters.pop();
                if (result == null) {
                    result = new CharArrayWriter(128);
                }
                for (int i = 0; i < this.logElements.length; i++) {
                    this.logElements[i].addElement(result, date, request, response, time);
                }
                log(result);
                if (result.size() <= this.maxLogMessageBufferSize) {
                    result.reset();
                    this.charArrayWriters.push(result);
                }
            }
        }
    }

    private static Date getDate(long systime) {
        Date date = localDate.get();
        date.setTime(systime);
        return date;
    }

    protected static Locale findLocale(String name, Locale fallback) {
        Locale[] availableLocales;
        if (name == null || name.isEmpty()) {
            return Locale.getDefault();
        }
        for (Locale l : Locale.getAvailableLocales()) {
            if (name.equals(l.toString())) {
                return l;
            }
        }
        log.error(sm.getString("accessLogValve.invalidLocale", name));
        return fallback;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleBase
    public synchronized void startInternal() throws LifecycleException {
        setState(LifecycleState.STARTING);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleBase
    public synchronized void stopInternal() throws LifecycleException {
        setState(LifecycleState.STOPPING);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$ThreadNameElement.class */
    public static class ThreadNameElement implements AccessLogElement {
        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            RequestInfo info = request.getCoyoteRequest().getRequestProcessor();
            if (info != null) {
                buf.append((CharSequence) info.getWorkerThreadName());
            } else {
                buf.append("-");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$LocalAddrElement.class */
    public static class LocalAddrElement implements AccessLogElement {
        private final String localAddrValue;

        public LocalAddrElement(boolean ipv6Canonical) {
            String init;
            try {
                init = InetAddress.getLocalHost().getHostAddress();
            } catch (Throwable e) {
                ExceptionUtils.handleThrowable(e);
                init = "127.0.0.1";
            }
            if (ipv6Canonical) {
                this.localAddrValue = IPv6Utils.canonize(init);
            } else {
                this.localAddrValue = init;
            }
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            buf.append((CharSequence) this.localAddrValue);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$RemoteAddrElement.class */
    public class RemoteAddrElement implements AccessLogElement {
        /* JADX INFO: Access modifiers changed from: protected */
        public RemoteAddrElement() {
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            String value;
            if (AbstractAccessLogValve.this.requestAttributesEnabled) {
                Object addr = request.getAttribute(AccessLog.REMOTE_ADDR_ATTRIBUTE);
                if (addr == null) {
                    value = request.getRemoteAddr();
                } else {
                    value = addr.toString();
                }
            } else {
                value = request.getRemoteAddr();
            }
            if (AbstractAccessLogValve.this.ipv6Canonical) {
                value = IPv6Utils.canonize(value);
            }
            buf.append((CharSequence) value);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$HostElement.class */
    public class HostElement implements AccessLogElement {
        /* JADX INFO: Access modifiers changed from: protected */
        public HostElement() {
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            Object host;
            String value = null;
            if (AbstractAccessLogValve.this.requestAttributesEnabled && (host = request.getAttribute(AccessLog.REMOTE_HOST_ATTRIBUTE)) != null) {
                value = host.toString();
            }
            if (value == null || value.length() == 0) {
                value = request.getRemoteHost();
            }
            value = (value == null || value.length() == 0) ? "-" : "-";
            if (AbstractAccessLogValve.this.ipv6Canonical) {
                value = IPv6Utils.canonize(value);
            }
            buf.append((CharSequence) value);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$LogicalUserNameElement.class */
    public static class LogicalUserNameElement implements AccessLogElement {
        protected LogicalUserNameElement() {
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            buf.append('-');
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$ProtocolElement.class */
    public class ProtocolElement implements AccessLogElement {
        protected ProtocolElement() {
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (AbstractAccessLogValve.this.requestAttributesEnabled) {
                Object proto = request.getAttribute(AccessLog.PROTOCOL_ATTRIBUTE);
                if (proto == null) {
                    buf.append((CharSequence) request.getProtocol());
                    return;
                } else {
                    buf.append((CharSequence) proto.toString());
                    return;
                }
            }
            buf.append((CharSequence) request.getProtocol());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$UserElement.class */
    public static class UserElement implements AccessLogElement {
        protected UserElement() {
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (request != null) {
                String value = request.getRemoteUser();
                if (value != null) {
                    buf.append((CharSequence) value);
                    return;
                } else {
                    buf.append('-');
                    return;
                }
            }
            buf.append('-');
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$DateAndTimeElement.class */
    public class DateAndTimeElement implements AccessLogElement {
        private static final String requestStartPrefix = "begin";
        private static final String responseEndPrefix = "end";
        private static final String prefixSeparator = ":";
        private static final String secFormat = "sec";
        private static final String msecFormat = "msec";
        private static final String msecFractionFormat = "msec_frac";
        private static final String msecPattern = "{#}";
        private static final String trippleMsecPattern = "{#}{#}{#}";
        private final String format;
        private final boolean usesBegin;
        private final FormatType type;
        private boolean usesMsecs;

        protected DateAndTimeElement(AbstractAccessLogValve this$0) {
            this(null);
        }

        private String tidyFormat(String format) {
            boolean escape = false;
            StringBuilder result = new StringBuilder();
            int len = format.length();
            for (int i = 0; i < len; i++) {
                char x = format.charAt(i);
                if (escape || x != 'S') {
                    result.append(x);
                } else {
                    result.append(msecPattern);
                    this.usesMsecs = true;
                }
                if (x == '\'') {
                    escape = !escape;
                }
            }
            return result.toString();
        }

        protected DateAndTimeElement(String header) {
            this.usesMsecs = false;
            String format = header;
            boolean usesBegin = false;
            FormatType type = FormatType.CLF;
            if (format != null) {
                if (format.equals(requestStartPrefix)) {
                    usesBegin = true;
                    format = "";
                } else if (format.startsWith("begin:")) {
                    usesBegin = true;
                    format = format.substring(6);
                } else if (format.equals(responseEndPrefix)) {
                    usesBegin = false;
                    format = "";
                } else if (format.startsWith("end:")) {
                    usesBegin = false;
                    format = format.substring(4);
                }
                if (format.length() == 0) {
                    type = FormatType.CLF;
                } else if (format.equals(secFormat)) {
                    type = FormatType.SEC;
                } else if (format.equals(msecFormat)) {
                    type = FormatType.MSEC;
                } else if (format.equals(msecFractionFormat)) {
                    type = FormatType.MSEC_FRAC;
                } else {
                    type = FormatType.SDF;
                    format = tidyFormat(format);
                }
            }
            this.format = format;
            this.usesBegin = usesBegin;
            this.type = type;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            long timestamp = date.getTime();
            if (this.usesBegin) {
                timestamp -= time;
            }
            if (this.type == FormatType.CLF) {
                buf.append((CharSequence) ((DateFormatCache) AbstractAccessLogValve.localDateCache.get()).getFormat(timestamp));
            } else if (this.type == FormatType.SEC) {
                buf.append((CharSequence) Long.toString(timestamp / 1000));
            } else if (this.type == FormatType.MSEC) {
                buf.append((CharSequence) Long.toString(timestamp));
            } else if (this.type != FormatType.MSEC_FRAC) {
                String temp = ((DateFormatCache) AbstractAccessLogValve.localDateCache.get()).getFormat(this.format, AbstractAccessLogValve.this.locale, timestamp);
                if (this.usesMsecs) {
                    long frac = timestamp % 1000;
                    StringBuilder trippleMsec = new StringBuilder(4);
                    if (frac < 100) {
                        if (frac < 10) {
                            trippleMsec.append('0');
                            trippleMsec.append('0');
                        } else {
                            trippleMsec.append('0');
                        }
                    }
                    trippleMsec.append(frac);
                    temp = temp.replace(trippleMsecPattern, trippleMsec).replace(msecPattern, Long.toString(frac));
                }
                buf.append((CharSequence) temp);
            } else {
                long frac2 = timestamp % 1000;
                if (frac2 < 100) {
                    if (frac2 < 10) {
                        buf.append('0');
                        buf.append('0');
                    } else {
                        buf.append('0');
                    }
                }
                buf.append((CharSequence) Long.toString(frac2));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$RequestElement.class */
    public static class RequestElement implements AccessLogElement {
        protected RequestElement() {
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (request != null) {
                String method = request.getMethod();
                if (method == null) {
                    buf.append('-');
                    return;
                }
                buf.append((CharSequence) request.getMethod());
                buf.append(' ');
                buf.append((CharSequence) request.getRequestURI());
                if (request.getQueryString() != null) {
                    buf.append('?');
                    buf.append((CharSequence) request.getQueryString());
                }
                buf.append(' ');
                buf.append((CharSequence) request.getProtocol());
                return;
            }
            buf.append('-');
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$HttpStatusCodeElement.class */
    public static class HttpStatusCodeElement implements AccessLogElement {
        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (response != null) {
                int status = response.getStatus();
                if (100 <= status && status < 1000) {
                    buf.append((char) (48 + (status / 100))).append((char) (48 + ((status / 10) % 10))).append((char) (48 + (status % 10)));
                    return;
                } else {
                    buf.append((CharSequence) Integer.toString(status));
                    return;
                }
            }
            buf.append('-');
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$PortElement.class */
    public class PortElement implements AccessLogElement {
        private static final String localPort = "local";
        private static final String remotePort = "remote";
        private final PortType portType;

        public PortElement() {
            this.portType = PortType.LOCAL;
        }

        public PortElement(String type) {
            boolean z = true;
            switch (type.hashCode()) {
                case -934610874:
                    if (type.equals("remote")) {
                        z = false;
                        break;
                    }
                    break;
                case 103145323:
                    if (type.equals(localPort)) {
                        z = true;
                        break;
                    }
                    break;
            }
            switch (z) {
                case false:
                    this.portType = PortType.REMOTE;
                    return;
                case true:
                    this.portType = PortType.LOCAL;
                    return;
                default:
                    AbstractAccessLogValve.log.error(ValveBase.sm.getString("accessLogValve.invalidPortType", type));
                    this.portType = PortType.LOCAL;
                    return;
            }
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (AbstractAccessLogValve.this.requestAttributesEnabled && this.portType == PortType.LOCAL) {
                Object port = request.getAttribute(AccessLog.SERVER_PORT_ATTRIBUTE);
                if (port == null) {
                    buf.append((CharSequence) Integer.toString(request.getServerPort()));
                } else {
                    buf.append((CharSequence) port.toString());
                }
            } else if (this.portType == PortType.LOCAL) {
                buf.append((CharSequence) Integer.toString(request.getServerPort()));
            } else {
                buf.append((CharSequence) Integer.toString(request.getRemotePort()));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$ByteSentElement.class */
    public static class ByteSentElement implements AccessLogElement {
        private final boolean conversion;

        public ByteSentElement(boolean conversion) {
            this.conversion = conversion;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            long length = response.getBytesWritten(false);
            if (length <= 0) {
                Object start = request.getAttribute("org.apache.tomcat.sendfile.start");
                if (start instanceof Long) {
                    Object end = request.getAttribute("org.apache.tomcat.sendfile.end");
                    if (end instanceof Long) {
                        length = ((Long) end).longValue() - ((Long) start).longValue();
                    }
                }
            }
            if (length <= 0 && this.conversion) {
                buf.append('-');
            } else {
                buf.append((CharSequence) Long.toString(length));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$MethodElement.class */
    public static class MethodElement implements AccessLogElement {
        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (request != null) {
                buf.append((CharSequence) request.getMethod());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$ElapsedTimeElement.class */
    public static class ElapsedTimeElement implements AccessLogElement {
        private final boolean millis;

        public ElapsedTimeElement(boolean millis) {
            this.millis = millis;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (this.millis) {
                buf.append((CharSequence) Long.toString(time));
                return;
            }
            buf.append((CharSequence) Long.toString(time / 1000));
            buf.append('.');
            int remains = (int) (time % 1000);
            buf.append((CharSequence) Long.toString(remains / 100));
            int remains2 = remains % 100;
            buf.append((CharSequence) Long.toString(remains2 / 10));
            buf.append((CharSequence) Long.toString(remains2 % 10));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$FirstByteTimeElement.class */
    public static class FirstByteTimeElement implements AccessLogElement {
        protected FirstByteTimeElement() {
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            long commitTime = response.getCoyoteResponse().getCommitTime();
            if (commitTime == -1) {
                buf.append('-');
                return;
            }
            long delta = commitTime - request.getCoyoteRequest().getStartTime();
            buf.append((CharSequence) Long.toString(delta));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$QueryElement.class */
    public static class QueryElement implements AccessLogElement {
        protected QueryElement() {
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            String query = null;
            if (request != null) {
                query = request.getQueryString();
            }
            if (query != null) {
                buf.append('?');
                buf.append((CharSequence) query);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$SessionIdElement.class */
    public static class SessionIdElement implements AccessLogElement {
        protected SessionIdElement() {
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (request == null) {
                buf.append('-');
                return;
            }
            Session session = request.getSessionInternal(false);
            if (session == null) {
                buf.append('-');
            } else {
                buf.append((CharSequence) session.getIdInternal());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$RequestURIElement.class */
    public static class RequestURIElement implements AccessLogElement {
        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (request != null) {
                buf.append((CharSequence) request.getRequestURI());
            } else {
                buf.append('-');
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$LocalServerNameElement.class */
    public class LocalServerNameElement implements AccessLogElement {
        protected LocalServerNameElement() {
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (AbstractAccessLogValve.this.ipv6Canonical) {
                buf.append((CharSequence) IPv6Utils.canonize(request.getServerName()));
            } else {
                buf.append((CharSequence) request.getServerName());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$StringElement.class */
    public static class StringElement implements AccessLogElement {
        private final String str;

        public StringElement(String str) {
            this.str = str;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            buf.append((CharSequence) this.str);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$HeaderElement.class */
    public static class HeaderElement implements AccessLogElement {
        private final String header;

        public HeaderElement(String header) {
            this.header = header;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            Enumeration<String> iter = request.getHeaders(this.header);
            if (iter.hasMoreElements()) {
                buf.append((CharSequence) iter.nextElement());
                while (iter.hasMoreElements()) {
                    buf.append(',').append((CharSequence) iter.nextElement());
                }
                return;
            }
            buf.append('-');
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$CookieElement.class */
    public static class CookieElement implements AccessLogElement {
        private final String header;

        public CookieElement(String header) {
            this.header = header;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            String value = "-";
            Cookie[] c = request.getCookies();
            if (c != null) {
                int i = 0;
                while (true) {
                    if (i >= c.length) {
                        break;
                    } else if (!this.header.equals(c[i].getName())) {
                        i++;
                    } else {
                        value = c[i].getValue();
                        break;
                    }
                }
            }
            buf.append((CharSequence) value);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$ResponseHeaderElement.class */
    public static class ResponseHeaderElement implements AccessLogElement {
        private final String header;

        public ResponseHeaderElement(String header) {
            this.header = header;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (null != response) {
                Iterator<String> iter = response.getHeaders(this.header).iterator();
                if (iter.hasNext()) {
                    buf.append((CharSequence) iter.next());
                    while (iter.hasNext()) {
                        buf.append(',').append((CharSequence) iter.next());
                    }
                    return;
                }
            }
            buf.append('-');
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$RequestAttributeElement.class */
    public static class RequestAttributeElement implements AccessLogElement {
        private final String header;

        public RequestAttributeElement(String header) {
            this.header = header;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            Object value;
            if (request != null) {
                value = request.getAttribute(this.header);
            } else {
                value = "??";
            }
            if (value != null) {
                if (value instanceof String) {
                    buf.append((CharSequence) ((String) value));
                    return;
                } else {
                    buf.append((CharSequence) value.toString());
                    return;
                }
            }
            buf.append('-');
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$SessionAttributeElement.class */
    public static class SessionAttributeElement implements AccessLogElement {
        private final String header;

        public SessionAttributeElement(String header) {
            this.header = header;
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            Object value = null;
            if (null != request) {
                HttpSession sess = request.getSession(false);
                if (null != sess) {
                    value = sess.getAttribute(this.header);
                }
            } else {
                value = "??";
            }
            if (value != null) {
                if (value instanceof String) {
                    buf.append((CharSequence) ((String) value));
                    return;
                } else {
                    buf.append((CharSequence) value.toString());
                    return;
                }
            }
            buf.append('-');
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/AbstractAccessLogValve$ConnectionStatusElement.class */
    public static class ConnectionStatusElement implements AccessLogElement {
        protected ConnectionStatusElement() {
        }

        @Override // org.apache.catalina.valves.AbstractAccessLogValve.AccessLogElement
        public void addElement(CharArrayWriter buf, Date date, Request request, Response response, long time) {
            if (response != null && request != null) {
                boolean statusFound = false;
                AtomicBoolean isIoAllowed = new AtomicBoolean(false);
                request.getCoyoteRequest().action(ActionCode.IS_IO_ALLOWED, isIoAllowed);
                if (!isIoAllowed.get()) {
                    buf.append('X');
                    statusFound = true;
                } else if (response.isError()) {
                    Throwable ex = (Throwable) request.getAttribute("javax.servlet.error.exception");
                    if (ex instanceof ClientAbortException) {
                        buf.append('X');
                        statusFound = true;
                    }
                }
                if (!statusFound) {
                    String connStatus = response.getHeader("Connection");
                    if (org.apache.coyote.http11.Constants.CLOSE.equalsIgnoreCase(connStatus)) {
                        buf.append('-');
                        return;
                    } else {
                        buf.append('+');
                        return;
                    }
                }
                return;
            }
            buf.append('?');
        }
    }

    protected AccessLogElement[] createLogElements() {
        List<AccessLogElement> list = new ArrayList<>();
        boolean replace = false;
        StringBuilder buf = new StringBuilder();
        int i = 0;
        while (i < this.pattern.length()) {
            char ch2 = this.pattern.charAt(i);
            if (replace) {
                if ('{' == ch2) {
                    StringBuilder name = new StringBuilder();
                    int j = i + 1;
                    while (j < this.pattern.length() && '}' != this.pattern.charAt(j)) {
                        name.append(this.pattern.charAt(j));
                        j++;
                    }
                    if (j + 1 < this.pattern.length()) {
                        int j2 = j + 1;
                        list.add(createAccessLogElement(name.toString(), this.pattern.charAt(j2)));
                        i = j2;
                    } else {
                        list.add(createAccessLogElement(ch2));
                    }
                } else {
                    list.add(createAccessLogElement(ch2));
                }
                replace = false;
            } else if (ch2 == '%') {
                replace = true;
                list.add(new StringElement(buf.toString()));
                buf = new StringBuilder();
            } else {
                buf.append(ch2);
            }
            i++;
        }
        if (buf.length() > 0) {
            list.add(new StringElement(buf.toString()));
        }
        return (AccessLogElement[]) list.toArray(new AccessLogElement[0]);
    }

    protected AccessLogElement createAccessLogElement(String name, char pattern) {
        switch (pattern) {
            case 'c':
                return new CookieElement(name);
            case 'd':
            case 'e':
            case Opcodes.FSUB /* 102 */:
            case Opcodes.DSUB /* 103 */:
            case 'h':
            case Opcodes.FMUL /* 106 */:
            case Opcodes.DMUL /* 107 */:
            case 'l':
            case Opcodes.LDIV /* 109 */:
            case Opcodes.FDIV /* 110 */:
            case Opcodes.LREM /* 113 */:
            default:
                return new StringElement("???");
            case Opcodes.LMUL /* 105 */:
                return new HeaderElement(name);
            case Opcodes.DDIV /* 111 */:
                return new ResponseHeaderElement(name);
            case 'p':
                return new PortElement(name);
            case Opcodes.FREM /* 114 */:
                if (TLSUtil.isTLSRequestAttribute(name)) {
                    this.tlsAttributeRequired = true;
                }
                return new RequestAttributeElement(name);
            case 's':
                return new SessionAttributeElement(name);
            case 't':
                return new DateAndTimeElement(name);
        }
    }

    protected AccessLogElement createAccessLogElement(char pattern) {
        switch (pattern) {
            case 'A':
                return new LocalAddrElement(this.ipv6Canonical);
            case 'B':
                return new ByteSentElement(false);
            case 'C':
            case TypeReference.CONSTRUCTOR_REFERENCE /* 69 */:
            case TypeReference.CAST /* 71 */:
            case 'J':
            case TypeReference.METHOD_REFERENCE_TYPE_ARGUMENT /* 75 */:
            case BaseNCodec.MIME_CHUNK_SIZE /* 76 */:
            case 'M':
            case 'N':
            case Opcodes.IASTORE /* 79 */:
            case 'P':
            case Opcodes.FASTORE /* 81 */:
            case Opcodes.DASTORE /* 82 */:
            case Opcodes.SASTORE /* 86 */:
            case Opcodes.POP /* 87 */:
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case Opcodes.DUP2_X2 /* 94 */:
            case Opcodes.SWAP /* 95 */:
            case '`':
            case 'c':
            case 'd':
            case 'e':
            case Opcodes.FSUB /* 102 */:
            case Opcodes.DSUB /* 103 */:
            case Opcodes.LMUL /* 105 */:
            case Opcodes.FMUL /* 106 */:
            case Opcodes.DMUL /* 107 */:
            case Opcodes.FDIV /* 110 */:
            case Opcodes.DDIV /* 111 */:
            default:
                return new StringElement("???" + pattern + "???");
            case 'D':
                return new ElapsedTimeElement(true);
            case 'F':
                return new FirstByteTimeElement();
            case 'H':
                return new ProtocolElement();
            case 'I':
                return new ThreadNameElement();
            case 'S':
                return new SessionIdElement();
            case Opcodes.BASTORE /* 84 */:
                return new ElapsedTimeElement(false);
            case Opcodes.CASTORE /* 85 */:
                return new RequestURIElement();
            case 'X':
                return new ConnectionStatusElement();
            case 'a':
                return new RemoteAddrElement();
            case Opcodes.FADD /* 98 */:
                return new ByteSentElement(true);
            case 'h':
                return new HostElement();
            case 'l':
                return new LogicalUserNameElement();
            case Opcodes.LDIV /* 109 */:
                return new MethodElement();
            case 'p':
                return new PortElement();
            case Opcodes.LREM /* 113 */:
                return new QueryElement();
            case Opcodes.FREM /* 114 */:
                return new RequestElement();
            case 's':
                return new HttpStatusCodeElement();
            case 't':
                return new DateAndTimeElement(this);
            case Opcodes.LNEG /* 117 */:
                return new UserElement();
            case Opcodes.FNEG /* 118 */:
                return new LocalServerNameElement();
        }
    }
}