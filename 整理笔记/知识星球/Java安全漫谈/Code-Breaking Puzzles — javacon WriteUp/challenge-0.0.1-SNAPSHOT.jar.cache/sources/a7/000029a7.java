package org.thymeleaf.templatemode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templatemode/TemplateMode.class */
public enum TemplateMode {
    HTML(true, false, false),
    XML(false, true, false),
    TEXT(false, false, true),
    JAVASCRIPT(false, false, true),
    CSS(false, false, true),
    RAW(false, false, false),
    HTML5(true, false, false),
    LEGACYHTML5(true, false, false),
    XHTML(true, false, false),
    VALIDXHTML(true, false, false),
    VALIDXML(false, true, false);
    
    private static Logger logger = LoggerFactory.getLogger(TemplateMode.class);
    private final boolean html;
    private final boolean xml;
    private final boolean text;
    private final boolean caseSensitive;

    TemplateMode(boolean html, boolean xml, boolean text) {
        this.html = html;
        this.xml = xml;
        this.text = text;
        this.caseSensitive = !this.html;
    }

    public boolean isMarkup() {
        return this.html || this.xml;
    }

    public boolean isText() {
        return this.text;
    }

    public boolean isCaseSensitive() {
        return this.caseSensitive;
    }

    public static TemplateMode parse(String mode) {
        if (mode == null || mode.trim().length() == 0) {
            throw new IllegalArgumentException("Template mode cannot be null or empty");
        }
        if ("HTML".equalsIgnoreCase(mode)) {
            return HTML;
        }
        if ("XML".equalsIgnoreCase(mode)) {
            return XML;
        }
        if ("TEXT".equalsIgnoreCase(mode)) {
            return TEXT;
        }
        if ("JAVASCRIPT".equalsIgnoreCase(mode)) {
            return JAVASCRIPT;
        }
        if ("CSS".equalsIgnoreCase(mode)) {
            return CSS;
        }
        if ("RAW".equalsIgnoreCase(mode)) {
            return RAW;
        }
        if ("HTML5".equalsIgnoreCase(mode) || "XHTML".equalsIgnoreCase(mode) || "VALIDXHTML".equalsIgnoreCase(mode) || "LEGACYHTML5".equalsIgnoreCase(mode)) {
            logger.warn("[THYMELEAF][{}] Template Mode '{}' is deprecated. Using Template Mode '{}' instead.", TemplateEngine.threadIndex(), mode, HTML);
            return HTML;
        } else if ("VALIDXML".equalsIgnoreCase(mode)) {
            logger.warn("[THYMELEAF][{}] Template Mode '{}' is deprecated. Using Template Mode '{}' instead.", TemplateEngine.threadIndex(), mode, XML);
            return XML;
        } else {
            logger.warn("[THYMELEAF][{}] Unknown Template Mode '{}'. Must be one of: 'HTML', 'XML', 'TEXT', 'JAVASCRIPT', 'CSS', 'RAW'. Using default Template Mode '{}'.", TemplateEngine.threadIndex(), mode, HTML);
            return HTML;
        }
    }
}