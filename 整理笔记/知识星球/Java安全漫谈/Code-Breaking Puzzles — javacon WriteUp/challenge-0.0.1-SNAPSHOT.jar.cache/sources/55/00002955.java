package org.thymeleaf.standard.inline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/inline/StandardInlineMode.class */
public enum StandardInlineMode {
    NONE,
    HTML,
    XML,
    TEXT,
    JAVASCRIPT,
    CSS;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StandardInlineMode.class);

    public static StandardInlineMode parse(String mode) {
        if (mode == null || mode.trim().length() == 0) {
            throw new IllegalArgumentException("Inline mode cannot be null or empty");
        }
        if ("NONE".equalsIgnoreCase(mode)) {
            return NONE;
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
        if (checkDartInline(mode)) {
            return JAVASCRIPT;
        }
        throw new IllegalArgumentException("Unrecognized inline mode: " + mode);
    }

    @Deprecated
    private static boolean checkDartInline(String inliner) {
        if ("DART".equalsIgnoreCase(inliner)) {
            LOGGER.warn("[THYMELEAF][{}] Found inline call with value \"dart\", which has been deprecated as no corresponding template mode exists for it. Inline will be redirected to \"javascript\", which should now be used instead. This redirection will be removed in future versions of Thymeleaf.", TemplateEngine.threadIndex());
            return true;
        }
        return false;
    }
}