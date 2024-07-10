package org.attoparser.config;

import java.io.Serializable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/config/ParseConfiguration.class */
public final class ParseConfiguration implements Serializable, Cloneable {
    private static final long serialVersionUID = 5191449744126332911L;
    private static final ParseConfiguration DEFAULT_HTML_PARSE_CONFIGURATION = new ParseConfiguration();
    private static final ParseConfiguration DEFAULT_XML_PARSE_CONFIGURATION;
    private ParsingMode mode = ParsingMode.XML;
    private boolean caseSensitive = true;
    private boolean textSplittable = false;
    private ElementBalancing elementBalancing = ElementBalancing.NO_BALANCING;
    private boolean noUnmatchedCloseElementsRequired = false;
    private boolean xmlWellFormedAttributeValuesRequired = false;
    private boolean uniqueAttributesInElementRequired = false;
    private PrologParseConfiguration prologParseConfiguration = new PrologParseConfiguration();
    private UniqueRootElementPresence uniqueRootElementPresence = UniqueRootElementPresence.DEPENDS_ON_PROLOG_DOCTYPE;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/config/ParseConfiguration$ElementBalancing.class */
    public enum ElementBalancing {
        NO_BALANCING,
        REQUIRE_BALANCED,
        AUTO_OPEN_CLOSE,
        AUTO_CLOSE
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/config/ParseConfiguration$ParsingMode.class */
    public enum ParsingMode {
        HTML,
        XML
    }

    static {
        DEFAULT_HTML_PARSE_CONFIGURATION.setMode(ParsingMode.HTML);
        DEFAULT_HTML_PARSE_CONFIGURATION.setTextSplittable(false);
        DEFAULT_HTML_PARSE_CONFIGURATION.setElementBalancing(ElementBalancing.AUTO_CLOSE);
        DEFAULT_HTML_PARSE_CONFIGURATION.setNoUnmatchedCloseElementsRequired(false);
        DEFAULT_HTML_PARSE_CONFIGURATION.setUniqueAttributesInElementRequired(false);
        DEFAULT_HTML_PARSE_CONFIGURATION.setXmlWellFormedAttributeValuesRequired(false);
        DEFAULT_HTML_PARSE_CONFIGURATION.setUniqueRootElementPresence(UniqueRootElementPresence.NOT_VALIDATED);
        DEFAULT_HTML_PARSE_CONFIGURATION.getPrologParseConfiguration().setValidateProlog(false);
        DEFAULT_HTML_PARSE_CONFIGURATION.getPrologParseConfiguration().setPrologPresence(PrologPresence.ALLOWED);
        DEFAULT_HTML_PARSE_CONFIGURATION.getPrologParseConfiguration().setXmlDeclarationPresence(PrologPresence.ALLOWED);
        DEFAULT_HTML_PARSE_CONFIGURATION.getPrologParseConfiguration().setDoctypePresence(PrologPresence.ALLOWED);
        DEFAULT_HTML_PARSE_CONFIGURATION.getPrologParseConfiguration().setRequireDoctypeKeywordsUpperCase(false);
        DEFAULT_XML_PARSE_CONFIGURATION = new ParseConfiguration();
        DEFAULT_XML_PARSE_CONFIGURATION.setMode(ParsingMode.XML);
        DEFAULT_XML_PARSE_CONFIGURATION.setTextSplittable(false);
        DEFAULT_XML_PARSE_CONFIGURATION.setElementBalancing(ElementBalancing.REQUIRE_BALANCED);
        DEFAULT_XML_PARSE_CONFIGURATION.setNoUnmatchedCloseElementsRequired(true);
        DEFAULT_XML_PARSE_CONFIGURATION.setUniqueAttributesInElementRequired(true);
        DEFAULT_XML_PARSE_CONFIGURATION.setXmlWellFormedAttributeValuesRequired(true);
        DEFAULT_XML_PARSE_CONFIGURATION.setUniqueRootElementPresence(UniqueRootElementPresence.DEPENDS_ON_PROLOG_DOCTYPE);
        DEFAULT_XML_PARSE_CONFIGURATION.getPrologParseConfiguration().setValidateProlog(true);
        DEFAULT_XML_PARSE_CONFIGURATION.getPrologParseConfiguration().setPrologPresence(PrologPresence.ALLOWED);
        DEFAULT_XML_PARSE_CONFIGURATION.getPrologParseConfiguration().setXmlDeclarationPresence(PrologPresence.ALLOWED);
        DEFAULT_XML_PARSE_CONFIGURATION.getPrologParseConfiguration().setDoctypePresence(PrologPresence.ALLOWED);
        DEFAULT_XML_PARSE_CONFIGURATION.getPrologParseConfiguration().setRequireDoctypeKeywordsUpperCase(true);
    }

    public static ParseConfiguration htmlConfiguration() {
        try {
            return DEFAULT_HTML_PARSE_CONFIGURATION.m934clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }

    public static ParseConfiguration xmlConfiguration() {
        try {
            return DEFAULT_XML_PARSE_CONFIGURATION.m934clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }

    private ParseConfiguration() {
    }

    public ParsingMode getMode() {
        return this.mode;
    }

    public void setMode(ParsingMode mode) {
        this.mode = mode;
        if (ParsingMode.HTML.equals(this.mode)) {
            this.caseSensitive = false;
        }
    }

    public boolean isCaseSensitive() {
        return this.caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        if (caseSensitive && ParsingMode.HTML.equals(this.mode)) {
            throw new IllegalArgumentException("Cannot set parser as case-sensitive for HTML mode. Use XML mode instead.");
        }
        this.caseSensitive = caseSensitive;
    }

    public boolean isTextSplittable() {
        return this.textSplittable;
    }

    public void setTextSplittable(boolean textSplittable) {
        this.textSplittable = textSplittable;
    }

    public ElementBalancing getElementBalancing() {
        return this.elementBalancing;
    }

    public void setElementBalancing(ElementBalancing elementBalancing) {
        this.elementBalancing = elementBalancing;
    }

    public PrologParseConfiguration getPrologParseConfiguration() {
        return this.prologParseConfiguration;
    }

    public boolean isNoUnmatchedCloseElementsRequired() {
        return this.noUnmatchedCloseElementsRequired;
    }

    public void setNoUnmatchedCloseElementsRequired(boolean noUnmatchedCloseElementsRequired) {
        this.noUnmatchedCloseElementsRequired = noUnmatchedCloseElementsRequired;
    }

    public boolean isXmlWellFormedAttributeValuesRequired() {
        return this.xmlWellFormedAttributeValuesRequired;
    }

    public void setXmlWellFormedAttributeValuesRequired(boolean xmlWellFormedAttributeValuesRequired) {
        this.xmlWellFormedAttributeValuesRequired = xmlWellFormedAttributeValuesRequired;
    }

    public boolean isUniqueAttributesInElementRequired() {
        return this.uniqueAttributesInElementRequired;
    }

    public void setUniqueAttributesInElementRequired(boolean uniqueAttributesInElementRequired) {
        this.uniqueAttributesInElementRequired = uniqueAttributesInElementRequired;
    }

    public UniqueRootElementPresence getUniqueRootElementPresence() {
        return this.uniqueRootElementPresence;
    }

    public void setUniqueRootElementPresence(UniqueRootElementPresence uniqueRootElementPresence) {
        validateNotNull(uniqueRootElementPresence, "The \"unique root element presence\" configuration value cannot be null");
        this.uniqueRootElementPresence = uniqueRootElementPresence;
    }

    /* renamed from: clone */
    public ParseConfiguration m934clone() throws CloneNotSupportedException {
        ParseConfiguration conf = (ParseConfiguration) super.clone();
        conf.mode = this.mode;
        conf.caseSensitive = this.caseSensitive;
        conf.elementBalancing = this.elementBalancing;
        conf.uniqueAttributesInElementRequired = this.uniqueAttributesInElementRequired;
        conf.xmlWellFormedAttributeValuesRequired = this.xmlWellFormedAttributeValuesRequired;
        conf.uniqueRootElementPresence = this.uniqueRootElementPresence;
        conf.prologParseConfiguration = this.prologParseConfiguration.m937clone();
        return conf;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/config/ParseConfiguration$PrologPresence.class */
    public enum PrologPresence {
        REQUIRED(true, false, false),
        ALLOWED(false, true, false),
        FORBIDDEN(false, false, true);
        
        private final boolean required;
        private final boolean allowed;
        private final boolean forbidden;

        PrologPresence(boolean required, boolean allowed, boolean forbidden) {
            this.required = required;
            this.allowed = allowed;
            this.forbidden = forbidden;
        }

        public boolean isRequired() {
            return this.required;
        }

        public boolean isAllowed() {
            return this.allowed;
        }

        public boolean isForbidden() {
            return this.forbidden;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/config/ParseConfiguration$UniqueRootElementPresence.class */
    public enum UniqueRootElementPresence {
        REQUIRED_ALWAYS(true, false),
        DEPENDS_ON_PROLOG_DOCTYPE(false, true),
        NOT_VALIDATED(false, false);
        
        private final boolean requiredAlways;
        private final boolean dependsOnPrologDoctype;

        UniqueRootElementPresence(boolean requiredAlways, boolean dependsOnPrologDoctype) {
            this.requiredAlways = requiredAlways;
            this.dependsOnPrologDoctype = dependsOnPrologDoctype;
        }

        public boolean isRequiredAlways() {
            return this.requiredAlways;
        }

        public boolean isDependsOnPrologDoctype() {
            return this.dependsOnPrologDoctype;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/config/ParseConfiguration$PrologParseConfiguration.class */
    public static class PrologParseConfiguration implements Serializable, Cloneable {
        private static final long serialVersionUID = -4291053503740751549L;
        private boolean validateProlog = false;
        private PrologPresence prologPresence = PrologPresence.ALLOWED;
        private PrologPresence xmlDeclarationPresence = PrologPresence.ALLOWED;
        private PrologPresence doctypePresence = PrologPresence.ALLOWED;
        private boolean requireDoctypeKeywordsUpperCase = true;

        protected PrologParseConfiguration() {
        }

        public boolean isValidateProlog() {
            return this.validateProlog;
        }

        public void setValidateProlog(boolean validateProlog) {
            this.validateProlog = validateProlog;
        }

        public PrologPresence getPrologPresence() {
            return this.prologPresence;
        }

        public void setPrologPresence(PrologPresence prologPresence) {
            ParseConfiguration.validateNotNull(this.prologPresence, "Prolog presence cannot be null");
            this.prologPresence = prologPresence;
        }

        public PrologPresence getXmlDeclarationPresence() {
            return this.xmlDeclarationPresence;
        }

        public void setXmlDeclarationPresence(PrologPresence xmlDeclarationPresence) {
            ParseConfiguration.validateNotNull(this.prologPresence, "XML Declaration presence cannot be null");
            this.xmlDeclarationPresence = xmlDeclarationPresence;
        }

        public PrologPresence getDoctypePresence() {
            return this.doctypePresence;
        }

        public void setDoctypePresence(PrologPresence doctypePresence) {
            ParseConfiguration.validateNotNull(this.prologPresence, "DOCTYPE presence cannot be null");
            this.doctypePresence = doctypePresence;
        }

        public boolean isRequireDoctypeKeywordsUpperCase() {
            return this.requireDoctypeKeywordsUpperCase;
        }

        public void setRequireDoctypeKeywordsUpperCase(boolean requireDoctypeKeywordsUpperCase) {
            this.requireDoctypeKeywordsUpperCase = requireDoctypeKeywordsUpperCase;
        }

        public void validateConfiguration() {
            if (!this.validateProlog) {
                return;
            }
            if (PrologPresence.FORBIDDEN.equals(this.prologPresence)) {
                if (PrologPresence.FORBIDDEN.equals(this.xmlDeclarationPresence) && PrologPresence.FORBIDDEN.equals(this.doctypePresence)) {
                    return;
                }
            } else if (PrologPresence.REQUIRED.equals(this.xmlDeclarationPresence) || PrologPresence.REQUIRED.equals(this.doctypePresence)) {
                return;
            } else {
                if (PrologPresence.ALLOWED.equals(this.prologPresence) && (!PrologPresence.FORBIDDEN.equals(this.xmlDeclarationPresence) || !PrologPresence.FORBIDDEN.equals(this.doctypePresence))) {
                    return;
                }
            }
            throw new IllegalArgumentException("Prolog parsing configuration is not valid: Prolog presence: " + this.prologPresence + ", XML Declaration presence: " + this.xmlDeclarationPresence + ", DOCTYPE presence: " + this.doctypePresence);
        }

        /* renamed from: clone */
        public PrologParseConfiguration m937clone() throws CloneNotSupportedException {
            PrologParseConfiguration conf = (PrologParseConfiguration) super.clone();
            conf.validateProlog = this.validateProlog;
            conf.prologPresence = this.prologPresence;
            conf.doctypePresence = this.doctypePresence;
            conf.xmlDeclarationPresence = this.xmlDeclarationPresence;
            conf.requireDoctypeKeywordsUpperCase = this.requireDoctypeKeywordsUpperCase;
            return conf;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void validateNotNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }
}