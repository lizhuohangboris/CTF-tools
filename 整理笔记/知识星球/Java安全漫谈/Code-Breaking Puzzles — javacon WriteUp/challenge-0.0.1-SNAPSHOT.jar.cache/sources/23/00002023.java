package org.springframework.http;

import java.beans.PropertyEditorSupport;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/http/MediaTypeEditor.class */
public class MediaTypeEditor extends PropertyEditorSupport {
    public void setAsText(String text) {
        if (StringUtils.hasText(text)) {
            setValue(MediaType.parseMediaType(text));
        } else {
            setValue(null);
        }
    }

    public String getAsText() {
        MediaType mediaType = (MediaType) getValue();
        return mediaType != null ? mediaType.toString() : "";
    }
}