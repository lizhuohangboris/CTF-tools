package org.thymeleaf.util;

import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.css.CssEscape;
import org.unbescape.html.HtmlEscape;
import org.unbescape.javascript.JavaScriptEscape;
import org.unbescape.xml.XmlEscape;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/EscapedAttributeUtils.class */
public final class EscapedAttributeUtils {
    public static String escapeAttribute(TemplateMode templateMode, String input) {
        if (input == null) {
            return null;
        }
        Validate.notNull(templateMode, "Template mode cannot be null");
        switch (templateMode) {
            case HTML:
                return HtmlEscape.escapeHtml4Xml(input);
            case XML:
                return XmlEscape.escapeXml10Attribute(input);
            default:
                throw new TemplateProcessingException("Unrecognized template mode " + templateMode + ". Cannot produce escaped attributes for this template mode.");
        }
    }

    public static String unescapeAttribute(TemplateMode templateMode, String input) {
        if (input == null) {
            return null;
        }
        Validate.notNull(templateMode, "Template mode cannot be null");
        switch (templateMode) {
            case HTML:
            case TEXT:
                return HtmlEscape.unescapeHtml(input);
            case XML:
                return XmlEscape.unescapeXml(input);
            case JAVASCRIPT:
                return JavaScriptEscape.unescapeJavaScript(input);
            case CSS:
                return CssEscape.unescapeCss(input);
            case RAW:
                return input;
            default:
                throw new TemplateProcessingException("Unrecognized template mode " + templateMode + ". Cannot unescape attribute value for this template mode.");
        }
    }

    private EscapedAttributeUtils() {
    }
}