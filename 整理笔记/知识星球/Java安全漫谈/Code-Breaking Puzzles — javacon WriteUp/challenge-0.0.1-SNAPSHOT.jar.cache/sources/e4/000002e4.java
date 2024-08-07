package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import java.lang.ref.SoftReference;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-core-2.9.7.jar:com/fasterxml/jackson/core/util/BufferRecyclers.class */
public class BufferRecyclers {
    public static final String SYSTEM_PROPERTY_TRACK_REUSABLE_BUFFERS = "com.fasterxml.jackson.core.util.BufferRecyclers.trackReusableBuffers";
    private static final ThreadLocalBufferManager _bufferRecyclerTracker;
    protected static final ThreadLocal<SoftReference<BufferRecycler>> _recyclerRef;
    protected static final ThreadLocal<SoftReference<JsonStringEncoder>> _encoderRef;

    static {
        _bufferRecyclerTracker = "true".equals(System.getProperty(SYSTEM_PROPERTY_TRACK_REUSABLE_BUFFERS)) ? ThreadLocalBufferManager.instance() : null;
        _recyclerRef = new ThreadLocal<>();
        _encoderRef = new ThreadLocal<>();
    }

    public static BufferRecycler getBufferRecycler() {
        SoftReference<BufferRecycler> ref;
        SoftReference<BufferRecycler> ref2 = _recyclerRef.get();
        BufferRecycler br = ref2 == null ? null : ref2.get();
        if (br == null) {
            br = new BufferRecycler();
            if (_bufferRecyclerTracker != null) {
                ref = _bufferRecyclerTracker.wrapAndTrack(br);
            } else {
                ref = new SoftReference<>(br);
            }
            _recyclerRef.set(ref);
        }
        return br;
    }

    public static int releaseBuffers() {
        if (_bufferRecyclerTracker != null) {
            return _bufferRecyclerTracker.releaseBuffers();
        }
        return -1;
    }

    public static JsonStringEncoder getJsonStringEncoder() {
        SoftReference<JsonStringEncoder> ref = _encoderRef.get();
        JsonStringEncoder enc = ref == null ? null : ref.get();
        if (enc == null) {
            enc = new JsonStringEncoder();
            _encoderRef.set(new SoftReference<>(enc));
        }
        return enc;
    }

    public static byte[] encodeAsUTF8(String text) {
        return getJsonStringEncoder().encodeAsUTF8(text);
    }

    public static char[] quoteAsJsonText(String rawText) {
        return getJsonStringEncoder().quoteAsString(rawText);
    }

    public static void quoteAsJsonText(CharSequence input, StringBuilder output) {
        getJsonStringEncoder().quoteAsString(input, output);
    }

    public static byte[] quoteAsJsonUTF8(String rawText) {
        return getJsonStringEncoder().quoteAsUTF8(rawText);
    }
}