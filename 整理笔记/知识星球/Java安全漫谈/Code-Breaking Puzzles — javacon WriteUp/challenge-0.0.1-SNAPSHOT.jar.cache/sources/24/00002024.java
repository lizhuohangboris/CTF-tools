package org.springframework.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/MediaTypeFactory.class */
public final class MediaTypeFactory {
    private static final String MIME_TYPES_FILE_NAME = "/org/springframework/http/mime.types";
    private static final MultiValueMap<String, MediaType> fileExtensionToMediaTypes = parseMimeTypes();

    private MediaTypeFactory() {
    }

    private static MultiValueMap<String, MediaType> parseMimeTypes() {
        try {
            InputStream is = MediaTypeFactory.class.getResourceAsStream(MIME_TYPES_FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.US_ASCII));
            MultiValueMap<String, MediaType> result = new LinkedMultiValueMap<>();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                } else if (!line.isEmpty() && line.charAt(0) != '#') {
                    String[] tokens = StringUtils.tokenizeToStringArray(line, " \t\n\r\f");
                    MediaType mediaType = MediaType.parseMediaType(tokens[0]);
                    for (int i = 1; i < tokens.length; i++) {
                        String fileExtension = tokens[i].toLowerCase(Locale.ENGLISH);
                        result.add(fileExtension, mediaType);
                    }
                }
            }
            if (is != null) {
                if (0 != 0) {
                    is.close();
                } else {
                    is.close();
                }
            }
            return result;
        } catch (IOException ex) {
            throw new IllegalStateException("Could not load '/org/springframework/http/mime.types'", ex);
        }
    }

    public static Optional<MediaType> getMediaType(@Nullable Resource resource) {
        return Optional.ofNullable(resource).map((v0) -> {
            return v0.getFilename();
        }).flatMap(MediaTypeFactory::getMediaType);
    }

    public static Optional<MediaType> getMediaType(@Nullable String filename) {
        return getMediaTypes(filename).stream().findFirst();
    }

    public static List<MediaType> getMediaTypes(@Nullable String filename) {
        Optional map = Optional.ofNullable(StringUtils.getFilenameExtension(filename)).map(s -> {
            return s.toLowerCase(Locale.ENGLISH);
        });
        MultiValueMap<String, MediaType> multiValueMap = fileExtensionToMediaTypes;
        multiValueMap.getClass();
        return (List) map.map((v1) -> {
            return r1.get(v1);
        }).orElse(Collections.emptyList());
    }
}