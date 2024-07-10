package org.springframework.web.multipart;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/multipart/MultipartRequest.class */
public interface MultipartRequest {
    Iterator<String> getFileNames();

    @Nullable
    MultipartFile getFile(String str);

    List<MultipartFile> getFiles(String str);

    Map<String, MultipartFile> getFileMap();

    MultiValueMap<String, MultipartFile> getMultiFileMap();

    @Nullable
    String getMultipartContentType(String str);
}