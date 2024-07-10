package org.springframework.http.codec;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.logging.Log;
import org.reactivestreams.Publisher;
import org.springframework.beans.PropertyAccessor;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Hints;
import org.springframework.core.codec.ResourceEncoder;
import org.springframework.core.codec.ResourceRegionEncoder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpLogging;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/codec/ResourceHttpMessageWriter.class */
public class ResourceHttpMessageWriter implements HttpMessageWriter<Resource> {
    private static final ResolvableType REGION_TYPE = ResolvableType.forClass(ResourceRegion.class);
    private static final Log logger = HttpLogging.forLogName(ResourceHttpMessageWriter.class);
    private final ResourceEncoder encoder;
    private final ResourceRegionEncoder regionEncoder;
    private final List<MediaType> mediaTypes;

    public ResourceHttpMessageWriter() {
        this(4096);
    }

    public ResourceHttpMessageWriter(int bufferSize) {
        this.encoder = new ResourceEncoder(bufferSize);
        this.regionEncoder = new ResourceRegionEncoder(bufferSize);
        this.mediaTypes = MediaType.asMediaTypes(this.encoder.getEncodableMimeTypes());
    }

    @Override // org.springframework.http.codec.HttpMessageWriter
    public boolean canWrite(ResolvableType elementType, @Nullable MediaType mediaType) {
        return this.encoder.canEncode(elementType, mediaType);
    }

    @Override // org.springframework.http.codec.HttpMessageWriter
    public List<MediaType> getWritableMediaTypes() {
        return this.mediaTypes;
    }

    @Override // org.springframework.http.codec.HttpMessageWriter
    public Mono<Void> write(Publisher<? extends Resource> inputStream, ResolvableType elementType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage message, Map<String, Object> hints) {
        return Mono.from(inputStream).flatMap(resource -> {
            return writeResource(resource, elementType, mediaType, message, hints);
        });
    }

    private Mono<Void> writeResource(Resource resource, ResolvableType type, @Nullable MediaType mediaType, ReactiveHttpOutputMessage message, Map<String, Object> hints) {
        HttpHeaders headers = message.getHeaders();
        MediaType resourceMediaType = getResourceMediaType(mediaType, resource, hints);
        headers.setContentType(resourceMediaType);
        if (headers.getContentLength() < 0) {
            long length = lengthOf(resource);
            if (length != -1) {
                headers.setContentLength(length);
            }
        }
        return zeroCopy(resource, null, message, hints).orElseGet(() -> {
            Mono<Resource> input = Mono.just(resource);
            DataBufferFactory factory = message.bufferFactory();
            Flux<DataBuffer> body = this.encoder.encode((Publisher) input, factory, type, (MimeType) resourceMediaType, (Map<String, Object>) hints);
            return message.writeWith(body);
        });
    }

    private static MediaType getResourceMediaType(@Nullable MediaType mediaType, Resource resource, Map<String, Object> hints) {
        if (mediaType != null && mediaType.isConcrete() && !mediaType.equals(MediaType.APPLICATION_OCTET_STREAM)) {
            return mediaType;
        }
        MediaType mediaType2 = MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM);
        if (logger.isDebugEnabled() && !Hints.isLoggingSuppressed(hints)) {
            logger.debug(Hints.getLogPrefix(hints) + "Resource associated with '" + mediaType2 + "'");
        }
        return mediaType2;
    }

    private static long lengthOf(Resource resource) {
        if (InputStreamResource.class != resource.getClass()) {
            try {
                return resource.contentLength();
            } catch (IOException e) {
                return -1L;
            }
        }
        return -1L;
    }

    private static Optional<Mono<Void>> zeroCopy(Resource resource, @Nullable ResourceRegion region, ReactiveHttpOutputMessage message, Map<String, Object> hints) {
        if ((message instanceof ZeroCopyHttpOutputMessage) && resource.isFile()) {
            try {
                File file = resource.getFile();
                long pos = region != null ? region.getPosition() : 0L;
                long count = region != null ? region.getCount() : file.length();
                if (logger.isDebugEnabled()) {
                    String formatted = region != null ? "region " + pos + "-" + count + " of " : "";
                    logger.debug(Hints.getLogPrefix(hints) + "Zero-copy " + formatted + PropertyAccessor.PROPERTY_KEY_PREFIX + resource + "]");
                }
                return Optional.of(((ZeroCopyHttpOutputMessage) message).writeWith(file, pos, count));
            } catch (IOException e) {
            }
        }
        return Optional.empty();
    }

    @Override // org.springframework.http.codec.HttpMessageWriter
    public Mono<Void> write(Publisher<? extends Resource> inputStream, @Nullable ResolvableType actualType, ResolvableType elementType, @Nullable MediaType mediaType, ServerHttpRequest request, ServerHttpResponse response, Map<String, Object> hints) {
        HttpHeaders headers = response.getHeaders();
        headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");
        try {
            List<HttpRange> ranges = request.getHeaders().getRange();
            return Mono.from(inputStream).flatMap(resource -> {
                if (ranges.isEmpty()) {
                    return writeResource(resource, elementType, mediaType, response, hints);
                }
                response.setStatusCode(HttpStatus.PARTIAL_CONTENT);
                List<ResourceRegion> regions = HttpRange.toResourceRegions(ranges, resource);
                MediaType resourceMediaType = getResourceMediaType(mediaType, resource, hints);
                if (regions.size() == 1) {
                    ResourceRegion region = regions.get(0);
                    headers.setContentType(resourceMediaType);
                    long contentLength = lengthOf(resource);
                    if (contentLength != -1) {
                        long start = region.getPosition();
                        long end = Math.min((start + region.getCount()) - 1, contentLength - 1);
                        headers.add(HttpHeaders.CONTENT_RANGE, "bytes " + start + '-' + end + '/' + contentLength);
                        headers.setContentLength((end - start) + 1);
                    }
                    return writeSingleRegion(region, response, hints);
                }
                String boundary = MimeTypeUtils.generateMultipartBoundaryString();
                MediaType multipartType = MediaType.parseMediaType("multipart/byteranges;boundary=" + boundary);
                headers.setContentType(multipartType);
                Map<String, Object> allHints = Hints.merge(hints, ResourceRegionEncoder.BOUNDARY_STRING_HINT, boundary);
                return encodeAndWriteRegions(Flux.fromIterable(regions), resourceMediaType, response, allHints);
            });
        } catch (IllegalArgumentException e) {
            response.setStatusCode(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
            return response.setComplete();
        }
    }

    private Mono<Void> writeSingleRegion(ResourceRegion region, ReactiveHttpOutputMessage message, Map<String, Object> hints) {
        return zeroCopy(region.getResource(), region, message, hints).orElseGet(() -> {
            Mono just = Mono.just(region);
            MediaType mediaType = message.getHeaders().getContentType();
            return encodeAndWriteRegions(just, mediaType, message, hints);
        });
    }

    private Mono<Void> encodeAndWriteRegions(Publisher<? extends ResourceRegion> publisher, @Nullable MediaType mediaType, ReactiveHttpOutputMessage message, Map<String, Object> hints) {
        Flux<DataBuffer> body = this.regionEncoder.encode(publisher, message.bufferFactory(), REGION_TYPE, mediaType, hints);
        return message.writeWith(body);
    }
}