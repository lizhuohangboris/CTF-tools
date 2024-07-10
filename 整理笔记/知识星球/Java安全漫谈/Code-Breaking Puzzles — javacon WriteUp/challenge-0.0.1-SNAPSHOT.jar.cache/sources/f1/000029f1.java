package org.thymeleaf.util;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.tomcat.websocket.BasicAuthenticator;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.http.MediaType;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.thymeleaf.templatemode.TemplateMode;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/ContentTypeUtils.class */
public final class ContentTypeUtils {
    private static final String[] MIME_TYPES_HTML = {"text/html", MediaType.APPLICATION_XHTML_XML_VALUE};
    private static final String[] MIME_TYPES_XML = {"application/xml", "text/xml"};
    private static final String[] MIME_TYPES_RSS = {MediaType.APPLICATION_RSS_XML_VALUE};
    private static final String[] MIME_TYPES_ATOM = {MediaType.APPLICATION_ATOM_XML_VALUE};
    private static final String[] MIME_TYPES_JAVASCRIPT = {"application/javascript", "application/x-javascript", "application/ecmascript", "text/javascript", "text/ecmascript"};
    private static final String[] MIME_TYPES_JSON = {"application/json"};
    private static final String[] MIME_TYPES_CSS = {"text/css"};
    private static final String[] MIME_TYPES_TEXT = {"text/plain"};
    private static final String[] MIME_TYPES_SSE = {MediaType.TEXT_EVENT_STREAM_VALUE};
    private static final String[] FILE_EXTENSIONS_HTML = {ThymeleafProperties.DEFAULT_SUFFIX, ".htm", ".xhtml"};
    private static final String[] FILE_EXTENSIONS_XML = {XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_SUFFIX};
    private static final String[] FILE_EXTENSIONS_RSS = {".rss"};
    private static final String[] FILE_EXTENSIONS_ATOM = {".atom"};
    private static final String[] FILE_EXTENSIONS_JAVASCRIPT = {".js"};
    private static final String[] FILE_EXTENSIONS_JSON = {".json"};
    private static final String[] FILE_EXTENSIONS_CSS = {".css"};
    private static final String[] FILE_EXTENSIONS_TEXT = {".txt"};
    private static final Map<String, String> NORMALIZED_MIME_TYPES;
    private static final Map<String, String> MIME_TYPE_BY_FILE_EXTENSION;
    private static final Map<String, TemplateMode> TEMPLATE_MODE_BY_MIME_TYPE;

    static {
        String[] strArr;
        String[] strArr2;
        String[] strArr3;
        String[] strArr4;
        String[] strArr5;
        String[] strArr6;
        String[] strArr7;
        String[] strArr8;
        String[] strArr9;
        String[] strArr10;
        String[] strArr11;
        String[] strArr12;
        String[] strArr13;
        String[] strArr14;
        String[] strArr15;
        String[] strArr16;
        String[] strArr17;
        Map<String, String> normalizedMimeTypes = new HashMap<>(20, 1.0f);
        for (String type : MIME_TYPES_HTML) {
            normalizedMimeTypes.put(type, MIME_TYPES_HTML[0]);
        }
        for (String type2 : MIME_TYPES_XML) {
            normalizedMimeTypes.put(type2, MIME_TYPES_XML[0]);
        }
        for (String type3 : MIME_TYPES_RSS) {
            normalizedMimeTypes.put(type3, MIME_TYPES_RSS[0]);
        }
        for (String type4 : MIME_TYPES_ATOM) {
            normalizedMimeTypes.put(type4, MIME_TYPES_ATOM[0]);
        }
        for (String type5 : MIME_TYPES_JAVASCRIPT) {
            normalizedMimeTypes.put(type5, MIME_TYPES_JAVASCRIPT[0]);
        }
        for (String type6 : MIME_TYPES_JSON) {
            normalizedMimeTypes.put(type6, MIME_TYPES_JSON[0]);
        }
        for (String type7 : MIME_TYPES_CSS) {
            normalizedMimeTypes.put(type7, MIME_TYPES_CSS[0]);
        }
        for (String type8 : MIME_TYPES_TEXT) {
            normalizedMimeTypes.put(type8, MIME_TYPES_TEXT[0]);
        }
        for (String type9 : MIME_TYPES_SSE) {
            normalizedMimeTypes.put(type9, MIME_TYPES_SSE[0]);
        }
        NORMALIZED_MIME_TYPES = Collections.unmodifiableMap(normalizedMimeTypes);
        Map<String, String> mimeTypesByExtension = new HashMap<>(20, 1.0f);
        for (String type10 : FILE_EXTENSIONS_HTML) {
            mimeTypesByExtension.put(type10, MIME_TYPES_HTML[0]);
        }
        for (String type11 : FILE_EXTENSIONS_XML) {
            mimeTypesByExtension.put(type11, MIME_TYPES_XML[0]);
        }
        for (String type12 : FILE_EXTENSIONS_RSS) {
            mimeTypesByExtension.put(type12, MIME_TYPES_RSS[0]);
        }
        for (String type13 : FILE_EXTENSIONS_ATOM) {
            mimeTypesByExtension.put(type13, MIME_TYPES_ATOM[0]);
        }
        for (String type14 : FILE_EXTENSIONS_JAVASCRIPT) {
            mimeTypesByExtension.put(type14, MIME_TYPES_JAVASCRIPT[0]);
        }
        for (String type15 : FILE_EXTENSIONS_JSON) {
            mimeTypesByExtension.put(type15, MIME_TYPES_JSON[0]);
        }
        for (String type16 : FILE_EXTENSIONS_CSS) {
            mimeTypesByExtension.put(type16, MIME_TYPES_CSS[0]);
        }
        for (String type17 : FILE_EXTENSIONS_TEXT) {
            mimeTypesByExtension.put(type17, MIME_TYPES_TEXT[0]);
        }
        MIME_TYPE_BY_FILE_EXTENSION = Collections.unmodifiableMap(mimeTypesByExtension);
        Map<String, TemplateMode> templateModeByMimeType = new HashMap<>(10, 1.0f);
        templateModeByMimeType.put(MIME_TYPES_HTML[0], TemplateMode.HTML);
        templateModeByMimeType.put(MIME_TYPES_XML[0], TemplateMode.XML);
        templateModeByMimeType.put(MIME_TYPES_RSS[0], TemplateMode.XML);
        templateModeByMimeType.put(MIME_TYPES_ATOM[0], TemplateMode.XML);
        templateModeByMimeType.put(MIME_TYPES_JAVASCRIPT[0], TemplateMode.JAVASCRIPT);
        templateModeByMimeType.put(MIME_TYPES_JSON[0], TemplateMode.JAVASCRIPT);
        templateModeByMimeType.put(MIME_TYPES_CSS[0], TemplateMode.CSS);
        templateModeByMimeType.put(MIME_TYPES_TEXT[0], TemplateMode.TEXT);
        TEMPLATE_MODE_BY_MIME_TYPE = Collections.unmodifiableMap(templateModeByMimeType);
    }

    public static boolean isContentTypeHTML(String contentType) {
        return isContentType(contentType, MIME_TYPES_HTML[0]);
    }

    public static boolean isContentTypeXML(String contentType) {
        return isContentType(contentType, MIME_TYPES_XML[0]);
    }

    public static boolean isContentTypeRSS(String contentType) {
        return isContentType(contentType, MIME_TYPES_RSS[0]);
    }

    public static boolean isContentTypeAtom(String contentType) {
        return isContentType(contentType, MIME_TYPES_ATOM[0]);
    }

    public static boolean isContentTypeJavaScript(String contentType) {
        return isContentType(contentType, MIME_TYPES_JAVASCRIPT[0]);
    }

    public static boolean isContentTypeJSON(String contentType) {
        return isContentType(contentType, MIME_TYPES_JSON[0]);
    }

    public static boolean isContentTypeCSS(String contentType) {
        return isContentType(contentType, MIME_TYPES_CSS[0]);
    }

    public static boolean isContentTypeText(String contentType) {
        return isContentType(contentType, MIME_TYPES_TEXT[0]);
    }

    public static boolean isContentTypeSSE(String contentType) {
        return isContentType(contentType, MIME_TYPES_SSE[0]);
    }

    private static boolean isContentType(String contentType, String matcher) {
        ContentType contentTypeObj;
        String normalisedMimeType;
        if (contentType == null || contentType.trim().length() == 0 || (contentTypeObj = ContentType.parseContentType(contentType)) == null || (normalisedMimeType = NORMALIZED_MIME_TYPES.get(contentTypeObj.getMimeType())) == null) {
            return false;
        }
        return normalisedMimeType.equals(matcher);
    }

    public static TemplateMode computeTemplateModeForContentType(String contentType) {
        ContentType contentTypeObj;
        String normalisedMimeType;
        if (contentType == null || contentType.trim().length() == 0 || (contentTypeObj = ContentType.parseContentType(contentType)) == null || (normalisedMimeType = NORMALIZED_MIME_TYPES.get(contentTypeObj.getMimeType())) == null) {
            return null;
        }
        return TEMPLATE_MODE_BY_MIME_TYPE.get(normalisedMimeType);
    }

    public static TemplateMode computeTemplateModeForTemplateName(String templateName) {
        String mimeType;
        String fileExtension = computeFileExtensionFromTemplateName(templateName);
        if (fileExtension == null || (mimeType = MIME_TYPE_BY_FILE_EXTENSION.get(fileExtension)) == null) {
            return null;
        }
        return TEMPLATE_MODE_BY_MIME_TYPE.get(mimeType);
    }

    public static TemplateMode computeTemplateModeForRequestPath(String requestPath) {
        String mimeType;
        String fileExtension = computeFileExtensionFromRequestPath(requestPath);
        if (fileExtension == null || (mimeType = MIME_TYPE_BY_FILE_EXTENSION.get(fileExtension)) == null) {
            return null;
        }
        return TEMPLATE_MODE_BY_MIME_TYPE.get(mimeType);
    }

    public static boolean hasRecognizedFileExtension(String templateName) {
        String fileExtension = computeFileExtensionFromTemplateName(templateName);
        if (fileExtension == null) {
            return false;
        }
        return MIME_TYPE_BY_FILE_EXTENSION.containsKey(fileExtension);
    }

    public static String computeContentTypeForTemplateName(String templateName, Charset charset) {
        String mimeType;
        ContentType contentType;
        String fileExtension = computeFileExtensionFromTemplateName(templateName);
        if (fileExtension == null || (mimeType = MIME_TYPE_BY_FILE_EXTENSION.get(fileExtension)) == null || (contentType = ContentType.parseContentType(mimeType)) == null) {
            return null;
        }
        if (charset != null) {
            contentType.setCharset(charset);
        }
        return contentType.toString();
    }

    public static String computeContentTypeForRequestPath(String requestPath, Charset charset) {
        String mimeType;
        ContentType contentType;
        String fileExtension = computeFileExtensionFromRequestPath(requestPath);
        if (fileExtension == null || (mimeType = MIME_TYPE_BY_FILE_EXTENSION.get(fileExtension)) == null || (contentType = ContentType.parseContentType(mimeType)) == null) {
            return null;
        }
        if (charset != null) {
            contentType.setCharset(charset);
        }
        return contentType.toString();
    }

    public static Charset computeCharsetFromContentType(String contentType) {
        ContentType contentTypeObj;
        if (contentType == null || contentType.trim().length() == 0 || (contentTypeObj = ContentType.parseContentType(contentType)) == null) {
            return null;
        }
        return contentTypeObj.getCharset();
    }

    private static String computeFileExtensionFromTemplateName(String templateName) {
        int pointPos;
        if (templateName == null || templateName.trim().length() == 0 || (pointPos = templateName.lastIndexOf(46)) < 0) {
            return null;
        }
        return templateName.substring(pointPos).toLowerCase(Locale.US).trim();
    }

    private static String computeFileExtensionFromRequestPath(String requestPath) {
        String path = requestPath;
        int questionMarkPos = path.indexOf(63);
        if (questionMarkPos != -1) {
            path = path.substring(0, questionMarkPos);
        }
        int hashPos = path.indexOf(35);
        if (hashPos != -1) {
            path = path.substring(0, hashPos);
        }
        int semicolonPos = path.indexOf(59);
        if (semicolonPos != -1) {
            path = path.substring(0, semicolonPos);
        }
        int slashPos = path.lastIndexOf(47);
        if (slashPos != -1) {
            path = path.substring(slashPos + 1);
        }
        int dotPos = path.lastIndexOf(46);
        if (dotPos != -1) {
            return path.substring(dotPos);
        }
        return null;
    }

    public static String combineContentTypeAndCharset(String contentType, Charset charset) {
        ContentType contentTypeObj;
        if (charset == null) {
            return contentType;
        }
        if (contentType == null || contentType.trim().length() == 0 || (contentTypeObj = ContentType.parseContentType(contentType)) == null) {
            return null;
        }
        contentTypeObj.setCharset(charset);
        return contentTypeObj.toString();
    }

    private ContentTypeUtils() {
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/ContentTypeUtils$ContentType.class */
    public static class ContentType {
        private final String PARAMETER_CHARSET = BasicAuthenticator.charsetparam;
        private final String mimeType;
        private final LinkedHashMap<String, String> parameters;

        static ContentType parseContentType(String contentType) {
            if (contentType == null || contentType.trim().length() == 0) {
                return null;
            }
            String[] tokens = StringUtils.split(contentType, ";");
            String mimeType = tokens[0].toLowerCase(Locale.US).trim();
            if (tokens.length == 1) {
                return new ContentType(mimeType, new LinkedHashMap(2, 1.0f));
            }
            LinkedHashMap<String, String> parameters = new LinkedHashMap<>(2, 1.0f);
            for (int i = 1; i < tokens.length; i++) {
                String token = tokens[i].toLowerCase(Locale.US).trim();
                int equalPos = token.indexOf(61);
                if (equalPos != -1) {
                    parameters.put(token.substring(0, equalPos).trim(), token.substring(equalPos + 1).trim());
                } else {
                    parameters.put(token.trim(), "");
                }
            }
            return new ContentType(mimeType, parameters);
        }

        ContentType(String mimeType, LinkedHashMap<String, String> parameters) {
            this.mimeType = mimeType;
            this.parameters = parameters;
        }

        String getMimeType() {
            return this.mimeType;
        }

        LinkedHashMap<String, String> getParameters() {
            return this.parameters;
        }

        Charset getCharset() {
            String charsetStr = this.parameters.get(BasicAuthenticator.charsetparam);
            if (charsetStr == null) {
                return null;
            }
            try {
                return Charset.forName(charsetStr);
            } catch (UnsupportedCharsetException e) {
                return null;
            }
        }

        void setCharset(Charset charset) {
            if (charset != null) {
                this.parameters.put(BasicAuthenticator.charsetparam, charset.name());
            }
        }

        public String toString() {
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(this.mimeType);
            for (Map.Entry<String, String> parameterEntry : this.parameters.entrySet()) {
                strBuilder.append(';');
                strBuilder.append(parameterEntry.getKey());
                strBuilder.append('=');
                strBuilder.append(parameterEntry.getValue());
            }
            return strBuilder.toString();
        }
    }
}