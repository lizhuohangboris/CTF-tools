package org.apache.juli;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/juli/DateFormatCache.class */
public class DateFormatCache {
    private static final String msecPattern = "#";
    private final String format;
    private final int cacheSize;
    private final Cache cache;

    private String tidyFormat(String format) {
        boolean escape = false;
        StringBuilder result = new StringBuilder();
        int len = format.length();
        for (int i = 0; i < len; i++) {
            char x = format.charAt(i);
            if (escape || x != 'S') {
                result.append(x);
            } else {
                result.append("#");
            }
            if (x == '\'') {
                escape = !escape;
            }
        }
        return result.toString();
    }

    public DateFormatCache(int size, String format, DateFormatCache parent) {
        this.cacheSize = size;
        this.format = tidyFormat(format);
        Cache parentCache = null;
        if (parent != null) {
            synchronized (parent) {
                parentCache = parent.cache;
            }
        }
        this.cache = new Cache(parentCache);
    }

    public String getFormat(long time) {
        return this.cache.getFormat(time);
    }

    public String getTimeFormat() {
        return this.format;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/juli/DateFormatCache$Cache.class */
    private class Cache {
        private long previousSeconds;
        private String previousFormat;
        private long first;
        private long last;
        private int offset;
        private final Date currentDate;
        private String[] cache;
        private SimpleDateFormat formatter;
        private Cache parent;

        private Cache(Cache parent) {
            this.previousSeconds = Long.MIN_VALUE;
            this.previousFormat = "";
            this.first = Long.MIN_VALUE;
            this.last = Long.MIN_VALUE;
            this.offset = 0;
            this.currentDate = new Date();
            this.parent = null;
            this.cache = new String[DateFormatCache.this.cacheSize];
            this.formatter = new SimpleDateFormat(DateFormatCache.this.format, Locale.US);
            this.formatter.setTimeZone(TimeZone.getDefault());
            this.parent = parent;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getFormat(long time) {
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
                    this.previousFormat = this.parent.getFormat(time);
                }
            } else {
                this.currentDate.setTime(time);
                this.previousFormat = this.formatter.format(this.currentDate);
            }
            this.cache[index] = this.previousFormat;
            return this.previousFormat;
        }
    }
}