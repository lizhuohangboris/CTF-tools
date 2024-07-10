package org.springframework.web.multipart.support;

import java.io.IOException;
import org.springframework.beans.propertyeditors.ByteArrayPropertyEditor;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-web-5.1.2.RELEASE.jar:org/springframework/web/multipart/support/ByteArrayMultipartFileEditor.class */
public class ByteArrayMultipartFileEditor extends ByteArrayPropertyEditor {
    public void setValue(@Nullable Object value) {
        if (value instanceof MultipartFile) {
            MultipartFile multipartFile = (MultipartFile) value;
            try {
                super.setValue(multipartFile.getBytes());
            } catch (IOException ex) {
                throw new IllegalArgumentException("Cannot read contents of multipart file", ex);
            }
        } else if (value instanceof byte[]) {
            super.setValue(value);
        } else {
            super.setValue(value != null ? value.toString().getBytes() : null);
        }
    }

    @Override // org.springframework.beans.propertyeditors.ByteArrayPropertyEditor
    public String getAsText() {
        byte[] value = (byte[]) getValue();
        return value != null ? new String(value) : "";
    }
}