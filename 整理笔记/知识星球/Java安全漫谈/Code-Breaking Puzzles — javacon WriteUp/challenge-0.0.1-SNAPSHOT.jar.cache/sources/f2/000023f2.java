package org.springframework.web.accept;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/accept/MappingMediaTypeFileExtensionResolver.class */
public class MappingMediaTypeFileExtensionResolver implements MediaTypeFileExtensionResolver {
    private final ConcurrentMap<String, MediaType> mediaTypes = new ConcurrentHashMap(64);
    private final MultiValueMap<MediaType, String> fileExtensions = new LinkedMultiValueMap();
    private final List<String> allFileExtensions = new ArrayList();

    public MappingMediaTypeFileExtensionResolver(@Nullable Map<String, MediaType> mediaTypes) {
        if (mediaTypes != null) {
            mediaTypes.forEach(extension, mediaType -> {
                String lowerCaseExtension = extension.toLowerCase(Locale.ENGLISH);
                this.mediaTypes.put(lowerCaseExtension, mediaType);
                this.fileExtensions.add(mediaType, lowerCaseExtension);
                this.allFileExtensions.add(lowerCaseExtension);
            });
        }
    }

    public Map<String, MediaType> getMediaTypes() {
        return this.mediaTypes;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public List<MediaType> getAllMediaTypes() {
        return new ArrayList(this.mediaTypes.values());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void addMapping(String extension, MediaType mediaType) {
        MediaType previous = this.mediaTypes.putIfAbsent(extension, mediaType);
        if (previous == null) {
            this.fileExtensions.add(mediaType, extension);
            this.allFileExtensions.add(extension);
        }
    }

    @Override // org.springframework.web.accept.MediaTypeFileExtensionResolver
    public List<String> resolveFileExtensions(MediaType mediaType) {
        List<String> fileExtensions = (List) this.fileExtensions.get(mediaType);
        return fileExtensions != null ? fileExtensions : Collections.emptyList();
    }

    @Override // org.springframework.web.accept.MediaTypeFileExtensionResolver
    public List<String> getAllFileExtensions() {
        return Collections.unmodifiableList(this.allFileExtensions);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public MediaType lookupMediaType(String extension) {
        return this.mediaTypes.get(extension.toLowerCase(Locale.ENGLISH));
    }
}