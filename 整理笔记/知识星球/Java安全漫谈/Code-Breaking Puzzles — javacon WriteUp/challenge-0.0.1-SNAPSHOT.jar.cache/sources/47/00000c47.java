package org.apache.tomcat.util.descriptor;

import java.io.InputStream;
import org.apache.tomcat.util.ExceptionUtils;
import org.xml.sax.InputSource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/InputSourceUtil.class */
public final class InputSourceUtil {
    private InputSourceUtil() {
    }

    public static void close(InputSource inputSource) {
        InputStream is;
        if (inputSource != null && (is = inputSource.getByteStream()) != null) {
            try {
                is.close();
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
            }
        }
    }
}