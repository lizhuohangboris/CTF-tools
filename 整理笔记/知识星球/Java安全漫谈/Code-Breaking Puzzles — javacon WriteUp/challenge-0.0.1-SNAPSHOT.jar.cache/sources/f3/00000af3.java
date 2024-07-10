package org.apache.juli;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import org.apache.juli.AsyncFileHandler;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/juli/OneLineFormatter.class */
public class OneLineFormatter extends Formatter {
    private static final String UNKNOWN_THREAD_NAME = "Unknown thread with ID ";
    private static final int THREAD_NAME_CACHE_SIZE = 10000;
    private static final String DEFAULT_TIME_FORMAT = "dd-MMM-yyyy HH:mm:ss";
    private static final int globalCacheSize = 30;
    private static final int localCacheSize = 5;
    private ThreadLocal<DateFormatCache> localDateCache;
    private static final String ST_SEP = System.lineSeparator() + " ";
    private static final Object threadMxBeanLock = new Object();
    private static volatile ThreadMXBean threadMxBean = null;
    private static ThreadLocal<ThreadNameCache> threadNameCache = new ThreadLocal<ThreadNameCache>() { // from class: org.apache.juli.OneLineFormatter.1
        @Override // java.lang.ThreadLocal
        public ThreadNameCache initialValue() {
            return new ThreadNameCache(10000);
        }
    };

    public OneLineFormatter() {
        String timeFormat = LogManager.getLogManager().getProperty(OneLineFormatter.class.getName() + ".timeFormat");
        setTimeFormat(timeFormat == null ? DEFAULT_TIME_FORMAT : timeFormat);
    }

    public void setTimeFormat(final String timeFormat) {
        final DateFormatCache globalDateCache = new DateFormatCache(30, timeFormat, null);
        this.localDateCache = new ThreadLocal<DateFormatCache>() { // from class: org.apache.juli.OneLineFormatter.2
            {
                OneLineFormatter.this = this;
            }

            @Override // java.lang.ThreadLocal
            public DateFormatCache initialValue() {
                return new DateFormatCache(5, timeFormat, globalDateCache);
            }
        };
    }

    public String getTimeFormat() {
        return this.localDateCache.get().getTimeFormat();
    }

    @Override // java.util.logging.Formatter
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        addTimestamp(sb, record.getMillis());
        sb.append(' ');
        sb.append(record.getLevel().getLocalizedName());
        sb.append(' ');
        sb.append('[');
        if (Thread.currentThread() instanceof AsyncFileHandler.LoggerThread) {
            sb.append(getThreadName(record.getThreadID()));
        } else {
            sb.append(Thread.currentThread().getName());
        }
        sb.append(']');
        sb.append(' ');
        sb.append(record.getSourceClassName());
        sb.append('.');
        sb.append(record.getSourceMethodName());
        sb.append(' ');
        sb.append(formatMessage(record));
        if (record.getThrown() != null) {
            sb.append(ST_SEP);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            record.getThrown().printStackTrace(pw);
            pw.close();
            sb.append(sw.getBuffer());
        }
        sb.append(System.lineSeparator());
        return sb.toString();
    }

    protected void addTimestamp(StringBuilder buf, long timestamp) {
        buf.append(this.localDateCache.get().getFormat(timestamp));
        long frac = timestamp % 1000;
        buf.append('.');
        if (frac < 100) {
            if (frac < 10) {
                buf.append('0');
                buf.append('0');
            } else {
                buf.append('0');
            }
        }
        buf.append(frac);
    }

    private static String getThreadName(int logRecordThreadId) {
        String result;
        Map<Integer, String> cache = threadNameCache.get();
        String result2 = null;
        if (logRecordThreadId > 1073741823) {
            result2 = cache.get(Integer.valueOf(logRecordThreadId));
        }
        if (result2 != null) {
            return result2;
        }
        if (logRecordThreadId > 1073741823) {
            result = UNKNOWN_THREAD_NAME + logRecordThreadId;
        } else {
            if (threadMxBean == null) {
                synchronized (threadMxBeanLock) {
                    if (threadMxBean == null) {
                        threadMxBean = ManagementFactory.getThreadMXBean();
                    }
                }
            }
            ThreadInfo threadInfo = threadMxBean.getThreadInfo(logRecordThreadId);
            if (threadInfo == null) {
                return Long.toString(logRecordThreadId);
            }
            result = threadInfo.getThreadName();
        }
        cache.put(Integer.valueOf(logRecordThreadId), result);
        return result;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/juli/OneLineFormatter$ThreadNameCache.class */
    public static class ThreadNameCache extends LinkedHashMap<Integer, String> {
        private static final long serialVersionUID = 1;
        private final int cacheSize;

        public ThreadNameCache(int cacheSize) {
            this.cacheSize = cacheSize;
        }

        @Override // java.util.LinkedHashMap
        protected boolean removeEldestEntry(Map.Entry<Integer, String> eldest) {
            return size() > this.cacheSize;
        }
    }
}