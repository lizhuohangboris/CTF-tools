package org.apache.tomcat.util.http;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.parser.Vary;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/ResponseUtil.class */
public class ResponseUtil {
    private static final String VARY_HEADER = "vary";
    private static final String VARY_ALL = "*";

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/ResponseUtil$Adapter.class */
    public interface Adapter {
        Collection<String> getHeaders(String str);

        void setHeader(String str, String str2);

        void addHeader(String str, String str2);
    }

    private ResponseUtil() {
    }

    public static void addVaryFieldName(MimeHeaders headers, String name) {
        addVaryFieldName(new HeaderAdapter(headers), name);
    }

    public static void addVaryFieldName(HttpServletResponse response, String name) {
        addVaryFieldName(new ResponseAdapter(response), name);
    }

    private static void addVaryFieldName(Adapter adapter, String name) {
        Collection<String> varyHeaders = adapter.getHeaders(VARY_HEADER);
        if (varyHeaders.size() == 1 && varyHeaders.iterator().next().trim().equals("*")) {
            return;
        }
        if (varyHeaders.size() == 0) {
            adapter.addHeader(VARY_HEADER, name);
        } else if ("*".equals(name.trim())) {
            adapter.setHeader(VARY_HEADER, "*");
        } else {
            Set<String> fieldNames = new HashSet<>();
            for (String varyHeader : varyHeaders) {
                StringReader input = new StringReader(varyHeader);
                try {
                    Vary.parseVary(input, fieldNames);
                } catch (IOException e) {
                }
            }
            if (fieldNames.contains("*")) {
                adapter.setHeader(VARY_HEADER, "*");
                return;
            }
            fieldNames.add(name);
            StringBuilder varyHeader2 = new StringBuilder();
            varyHeader2.append(name);
            for (String fieldName : fieldNames) {
                varyHeader2.append(',');
                varyHeader2.append(fieldName);
            }
            adapter.setHeader(VARY_HEADER, varyHeader2.toString());
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/ResponseUtil$HeaderAdapter.class */
    private static final class HeaderAdapter implements Adapter {
        private final MimeHeaders headers;

        public HeaderAdapter(MimeHeaders headers) {
            this.headers = headers;
        }

        @Override // org.apache.tomcat.util.http.ResponseUtil.Adapter
        public Collection<String> getHeaders(String name) {
            Enumeration<String> values = this.headers.values(name);
            List<String> result = new ArrayList<>();
            while (values.hasMoreElements()) {
                result.add(values.nextElement());
            }
            return result;
        }

        @Override // org.apache.tomcat.util.http.ResponseUtil.Adapter
        public void setHeader(String name, String value) {
            this.headers.setValue(name).setString(value);
        }

        @Override // org.apache.tomcat.util.http.ResponseUtil.Adapter
        public void addHeader(String name, String value) {
            this.headers.addValue(name).setString(value);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/ResponseUtil$ResponseAdapter.class */
    private static final class ResponseAdapter implements Adapter {
        private final HttpServletResponse response;

        public ResponseAdapter(HttpServletResponse response) {
            this.response = response;
        }

        @Override // org.apache.tomcat.util.http.ResponseUtil.Adapter
        public Collection<String> getHeaders(String name) {
            return this.response.getHeaders(name);
        }

        @Override // org.apache.tomcat.util.http.ResponseUtil.Adapter
        public void setHeader(String name, String value) {
            this.response.setHeader(name, value);
        }

        @Override // org.apache.tomcat.util.http.ResponseUtil.Adapter
        public void addHeader(String name, String value) {
            this.response.addHeader(name, value);
        }
    }
}