package org.springframework.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/HttpRange.class */
public abstract class HttpRange {
    private static final int MAX_RANGES = 100;
    private static final String BYTE_RANGE_PREFIX = "bytes=";

    public abstract long getRangeStart(long j);

    public abstract long getRangeEnd(long j);

    public ResourceRegion toResourceRegion(Resource resource) {
        Assert.isTrue(resource.getClass() != InputStreamResource.class, "Cannot convert an InputStreamResource to a ResourceRegion");
        long contentLength = getLengthFor(resource);
        long start = getRangeStart(contentLength);
        long end = getRangeEnd(contentLength);
        return new ResourceRegion(resource, start, (end - start) + 1);
    }

    private static long getLengthFor(Resource resource) {
        try {
            long contentLength = resource.contentLength();
            Assert.isTrue(contentLength > 0, "Resource content length should be > 0");
            return contentLength;
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to obtain Resource content length", ex);
        }
    }

    public static HttpRange createByteRange(long firstBytePos) {
        return new ByteRange(firstBytePos, null);
    }

    public static HttpRange createByteRange(long firstBytePos, long lastBytePos) {
        return new ByteRange(firstBytePos, Long.valueOf(lastBytePos));
    }

    public static HttpRange createSuffixRange(long suffixLength) {
        return new SuffixByteRange(suffixLength);
    }

    public static List<HttpRange> parseRanges(@Nullable String ranges) {
        if (!StringUtils.hasLength(ranges)) {
            return Collections.emptyList();
        }
        if (!ranges.startsWith(BYTE_RANGE_PREFIX)) {
            throw new IllegalArgumentException("Range '" + ranges + "' does not start with 'bytes='");
        }
        String[] tokens = StringUtils.tokenizeToStringArray(ranges.substring(BYTE_RANGE_PREFIX.length()), ",");
        Assert.isTrue(tokens.length <= 100, () -> {
            return "Too many ranges " + tokens.length;
        });
        List<HttpRange> result = new ArrayList<>(tokens.length);
        for (String token : tokens) {
            result.add(parseRange(token));
        }
        return result;
    }

    private static HttpRange parseRange(String range) {
        Assert.hasLength(range, "Range String must not be empty");
        int dashIdx = range.indexOf(45);
        if (dashIdx > 0) {
            long firstPos = Long.parseLong(range.substring(0, dashIdx));
            if (dashIdx < range.length() - 1) {
                Long lastPos = Long.valueOf(Long.parseLong(range.substring(dashIdx + 1, range.length())));
                return new ByteRange(firstPos, lastPos);
            }
            return new ByteRange(firstPos, null);
        } else if (dashIdx == 0) {
            long suffixLength = Long.parseLong(range.substring(1));
            return new SuffixByteRange(suffixLength);
        } else {
            throw new IllegalArgumentException("Range '" + range + "' does not contain \"-\"");
        }
    }

    public static List<ResourceRegion> toResourceRegions(List<HttpRange> ranges, Resource resource) {
        if (CollectionUtils.isEmpty(ranges)) {
            return Collections.emptyList();
        }
        List<ResourceRegion> regions = new ArrayList<>(ranges.size());
        for (HttpRange range : ranges) {
            regions.add(range.toResourceRegion(resource));
        }
        if (ranges.size() > 1) {
            long length = getLengthFor(resource);
            long total = ((Long) regions.stream().map((v0) -> {
                return v0.getCount();
            }).reduce(0L, count, sum -> {
                return Long.valueOf(sum.longValue() + count.longValue());
            })).longValue();
            Assert.isTrue(total < length, () -> {
                return "The sum of all ranges (" + total + ") should be less than the resource length (" + length + ")";
            });
        }
        return regions;
    }

    public static String toString(Collection<HttpRange> ranges) {
        Assert.notEmpty(ranges, "Ranges Collection must not be empty");
        StringBuilder builder = new StringBuilder(BYTE_RANGE_PREFIX);
        Iterator<HttpRange> iterator = ranges.iterator();
        while (iterator.hasNext()) {
            HttpRange range = iterator.next();
            builder.append(range);
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/HttpRange$ByteRange.class */
    public static class ByteRange extends HttpRange {
        private final long firstPos;
        @Nullable
        private final Long lastPos;

        public ByteRange(long firstPos, @Nullable Long lastPos) {
            assertPositions(firstPos, lastPos);
            this.firstPos = firstPos;
            this.lastPos = lastPos;
        }

        private void assertPositions(long firstBytePos, @Nullable Long lastBytePos) {
            if (firstBytePos < 0) {
                throw new IllegalArgumentException("Invalid first byte position: " + firstBytePos);
            }
            if (lastBytePos != null && lastBytePos.longValue() < firstBytePos) {
                throw new IllegalArgumentException("firstBytePosition=" + firstBytePos + " should be less then or equal to lastBytePosition=" + lastBytePos);
            }
        }

        @Override // org.springframework.http.HttpRange
        public long getRangeStart(long length) {
            return this.firstPos;
        }

        @Override // org.springframework.http.HttpRange
        public long getRangeEnd(long length) {
            if (this.lastPos != null && this.lastPos.longValue() < length) {
                return this.lastPos.longValue();
            }
            return length - 1;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ByteRange)) {
                return false;
            }
            ByteRange otherRange = (ByteRange) other;
            return this.firstPos == otherRange.firstPos && ObjectUtils.nullSafeEquals(this.lastPos, otherRange.lastPos);
        }

        public int hashCode() {
            return (ObjectUtils.nullSafeHashCode(Long.valueOf(this.firstPos)) * 31) + ObjectUtils.nullSafeHashCode(this.lastPos);
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(this.firstPos);
            builder.append('-');
            if (this.lastPos != null) {
                builder.append(this.lastPos);
            }
            return builder.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/HttpRange$SuffixByteRange.class */
    public static class SuffixByteRange extends HttpRange {
        private final long suffixLength;

        public SuffixByteRange(long suffixLength) {
            if (suffixLength < 0) {
                throw new IllegalArgumentException("Invalid suffix length: " + suffixLength);
            }
            this.suffixLength = suffixLength;
        }

        @Override // org.springframework.http.HttpRange
        public long getRangeStart(long length) {
            if (this.suffixLength < length) {
                return length - this.suffixLength;
            }
            return 0L;
        }

        @Override // org.springframework.http.HttpRange
        public long getRangeEnd(long length) {
            return length - 1;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof SuffixByteRange)) {
                return false;
            }
            SuffixByteRange otherRange = (SuffixByteRange) other;
            return this.suffixLength == otherRange.suffixLength;
        }

        public int hashCode() {
            return Long.hashCode(this.suffixLength);
        }

        public String toString() {
            return "-" + this.suffixLength;
        }
    }
}