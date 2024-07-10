package org.thymeleaf.standard.serializer;

import java.io.Writer;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/serializer/IStandardJavaScriptSerializer.class */
public interface IStandardJavaScriptSerializer {
    void serializeValue(Object obj, Writer writer);
}