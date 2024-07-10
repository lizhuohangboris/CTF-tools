package org.springframework.http.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StreamUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/converter/ResourceRegionHttpMessageConverter.class */
public class ResourceRegionHttpMessageConverter extends AbstractGenericHttpMessageConverter<Object> {
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected /* bridge */ /* synthetic */ Object readInternal(Class cls, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        return readInternal((Class<?>) cls, httpInputMessage);
    }

    public ResourceRegionHttpMessageConverter() {
        super(MediaType.ALL);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    public MediaType getDefaultContentType(Object object) {
        Resource resource = null;
        if (object instanceof ResourceRegion) {
            resource = ((ResourceRegion) object).getResource();
        } else {
            Collection<ResourceRegion> regions = (Collection) object;
            if (!regions.isEmpty()) {
                resource = regions.iterator().next().getResource();
            }
        }
        return MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM);
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter, org.springframework.http.converter.HttpMessageConverter
    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        return false;
    }

    @Override // org.springframework.http.converter.AbstractGenericHttpMessageConverter, org.springframework.http.converter.GenericHttpMessageConverter
    public boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType) {
        return false;
    }

    @Override // org.springframework.http.converter.GenericHttpMessageConverter
    public Object read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected ResourceRegion readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOperationException();
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter, org.springframework.http.converter.HttpMessageConverter
    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        return canWrite(clazz, null, mediaType);
    }

    @Override // org.springframework.http.converter.AbstractGenericHttpMessageConverter, org.springframework.http.converter.GenericHttpMessageConverter
    public boolean canWrite(@Nullable Type type, @Nullable Class<?> clazz, @Nullable MediaType mediaType) {
        if (!(type instanceof ParameterizedType)) {
            return (type instanceof Class) && ResourceRegion.class.isAssignableFrom((Class) type);
        }
        ParameterizedType parameterizedType = (ParameterizedType) type;
        if (!(parameterizedType.getRawType() instanceof Class)) {
            return false;
        }
        Class<?> rawType = (Class) parameterizedType.getRawType();
        if (!Collection.class.isAssignableFrom(rawType) || parameterizedType.getActualTypeArguments().length != 1) {
            return false;
        }
        Type typeArgument = parameterizedType.getActualTypeArguments()[0];
        if (!(typeArgument instanceof Class)) {
            return false;
        }
        Class<?> typeArgumentClass = (Class) typeArgument;
        return ResourceRegion.class.isAssignableFrom(typeArgumentClass);
    }

    @Override // org.springframework.http.converter.AbstractGenericHttpMessageConverter
    protected void writeInternal(Object object, @Nullable Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if (object instanceof ResourceRegion) {
            writeResourceRegion((ResourceRegion) object, outputMessage);
            return;
        }
        Collection<ResourceRegion> regions = (Collection) object;
        if (regions.size() == 1) {
            writeResourceRegion(regions.iterator().next(), outputMessage);
        } else {
            writeResourceRegionCollection((Collection) object, outputMessage);
        }
    }

    protected void writeResourceRegion(ResourceRegion region, HttpOutputMessage outputMessage) throws IOException {
        Assert.notNull(region, "ResourceRegion must not be null");
        HttpHeaders responseHeaders = outputMessage.getHeaders();
        long start = region.getPosition();
        long end = (start + region.getCount()) - 1;
        Long resourceLength = Long.valueOf(region.getResource().contentLength());
        long end2 = Math.min(end, resourceLength.longValue() - 1);
        long rangeLength = (end2 - start) + 1;
        responseHeaders.add(HttpHeaders.CONTENT_RANGE, "bytes " + start + '-' + end2 + '/' + resourceLength);
        responseHeaders.setContentLength(rangeLength);
        InputStream in = region.getResource().getInputStream();
        try {
            StreamUtils.copyRange(in, outputMessage.getBody(), start, end2);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
            }
        }
    }

    private void writeResourceRegionCollection(Collection<ResourceRegion> resourceRegions, HttpOutputMessage outputMessage) throws IOException {
        Assert.notNull(resourceRegions, "Collection of ResourceRegion should not be null");
        HttpHeaders responseHeaders = outputMessage.getHeaders();
        MediaType contentType = responseHeaders.getContentType();
        String boundaryString = MimeTypeUtils.generateMultipartBoundaryString();
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, "multipart/byteranges; boundary=" + boundaryString);
        OutputStream out = outputMessage.getBody();
        for (ResourceRegion region : resourceRegions) {
            long start = region.getPosition();
            long end = (start + region.getCount()) - 1;
            InputStream in = region.getResource().getInputStream();
            try {
                println(out);
                print(out, "--" + boundaryString);
                println(out);
                if (contentType != null) {
                    print(out, "Content-Type: " + contentType.toString());
                    println(out);
                }
                Long resourceLength = Long.valueOf(region.getResource().contentLength());
                long end2 = Math.min(end, resourceLength.longValue() - 1);
                print(out, "Content-Range: bytes " + start + '-' + end2 + '/' + resourceLength);
                println(out);
                println(out);
                StreamUtils.copyRange(in, out, start, end2);
                try {
                    in.close();
                } catch (IOException e) {
                }
            } catch (Throwable th) {
                try {
                    in.close();
                } catch (IOException e2) {
                }
                throw th;
            }
        }
        println(out);
        print(out, "--" + boundaryString + "--");
    }

    private static void println(OutputStream os) throws IOException {
        os.write(13);
        os.write(10);
    }

    private static void print(OutputStream os, String buf) throws IOException {
        os.write(buf.getBytes(StandardCharsets.US_ASCII));
    }
}