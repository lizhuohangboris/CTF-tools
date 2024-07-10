package org.apache.coyote;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.http.ResponseUtil;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.http.HttpHeaders;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/coyote/CompressionConfig.class */
public class CompressionConfig {
    private int compressionLevel = 0;
    private Pattern noCompressionUserAgents = null;
    private String compressibleMimeType = "text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json,application/xml";
    private String[] compressibleMimeTypes = null;
    private int compressionMinSize = 2048;

    public void setCompression(String compression) {
        if (compression.equals(CustomBooleanEditor.VALUE_ON)) {
            this.compressionLevel = 1;
        } else if (compression.equals("force")) {
            this.compressionLevel = 2;
        } else if (compression.equals(CustomBooleanEditor.VALUE_OFF)) {
            this.compressionLevel = 0;
        } else {
            try {
                setCompressionMinSize(Integer.parseInt(compression));
                this.compressionLevel = 1;
            } catch (Exception e) {
                this.compressionLevel = 0;
            }
        }
    }

    public String getCompression() {
        switch (this.compressionLevel) {
            case 0:
                return CustomBooleanEditor.VALUE_OFF;
            case 1:
                return CustomBooleanEditor.VALUE_ON;
            case 2:
                return "force";
            default:
                return CustomBooleanEditor.VALUE_OFF;
        }
    }

    public int getCompressionLevel() {
        return this.compressionLevel;
    }

    public String getNoCompressionUserAgents() {
        if (this.noCompressionUserAgents == null) {
            return null;
        }
        return this.noCompressionUserAgents.toString();
    }

    public Pattern getNoCompressionUserAgentsPattern() {
        return this.noCompressionUserAgents;
    }

    public void setNoCompressionUserAgents(String noCompressionUserAgents) {
        if (noCompressionUserAgents == null || noCompressionUserAgents.length() == 0) {
            this.noCompressionUserAgents = null;
        } else {
            this.noCompressionUserAgents = Pattern.compile(noCompressionUserAgents);
        }
    }

    public String getCompressibleMimeType() {
        return this.compressibleMimeType;
    }

    public void setCompressibleMimeType(String valueS) {
        this.compressibleMimeType = valueS;
        this.compressibleMimeTypes = null;
    }

    public String[] getCompressibleMimeTypes() {
        String[] result = this.compressibleMimeTypes;
        if (result != null) {
            return result;
        }
        List<String> values = new ArrayList<>();
        StringTokenizer tokens = new StringTokenizer(this.compressibleMimeType, ",");
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken().trim();
            if (token.length() > 0) {
                values.add(token);
            }
        }
        String[] result2 = (String[]) values.toArray(new String[values.size()]);
        this.compressibleMimeTypes = result2;
        return result2;
    }

    public int getCompressionMinSize() {
        return this.compressionMinSize;
    }

    public void setCompressionMinSize(int compressionMinSize) {
        this.compressionMinSize = compressionMinSize;
    }

    public boolean useCompression(Request request, Response response) {
        Pattern noCompressionUserAgents;
        MessageBytes userAgentValueMB;
        if (this.compressionLevel == 0) {
            return false;
        }
        MimeHeaders responseHeaders = response.getMimeHeaders();
        MessageBytes contentEncodingMB = responseHeaders.getValue(HttpHeaders.CONTENT_ENCODING);
        if (contentEncodingMB != null && (contentEncodingMB.indexOf("gzip") != -1 || contentEncodingMB.indexOf("br") != -1)) {
            return false;
        }
        if (this.compressionLevel != 2) {
            long contentLength = response.getContentLengthLong();
            if (contentLength != -1 && contentLength < this.compressionMinSize) {
                return false;
            }
            String[] compressibleMimeTypes = getCompressibleMimeTypes();
            if (compressibleMimeTypes != null && !startsWithStringArray(compressibleMimeTypes, response.getContentType())) {
                return false;
            }
        }
        ResponseUtil.addVaryFieldName(responseHeaders, "accept-encoding");
        MessageBytes acceptEncodingMB = request.getMimeHeaders().getValue("accept-encoding");
        if (acceptEncodingMB == null || acceptEncodingMB.indexOf("gzip") == -1) {
            return false;
        }
        if (this.compressionLevel != 2 && (noCompressionUserAgents = this.noCompressionUserAgents) != null && (userAgentValueMB = request.getMimeHeaders().getValue("user-agent")) != null) {
            String userAgentValue = userAgentValueMB.toString();
            if (noCompressionUserAgents.matcher(userAgentValue).matches()) {
                return false;
            }
        }
        response.setContentLength(-1L);
        responseHeaders.setValue(HttpHeaders.CONTENT_ENCODING).setString("gzip");
        return true;
    }

    private static boolean startsWithStringArray(String[] sArray, String value) {
        if (value == null) {
            return false;
        }
        for (String str : sArray) {
            if (value.startsWith(str)) {
                return true;
            }
        }
        return false;
    }
}