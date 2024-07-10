package org.thymeleaf.templateparser.markup;

import org.attoparser.config.ParseConfiguration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/markup/XMLTemplateParser.class */
public final class XMLTemplateParser extends AbstractMarkupTemplateParser {
    static final ParseConfiguration MARKUP_PARSING_CONFIGURATION = ParseConfiguration.xmlConfiguration();

    static {
        MARKUP_PARSING_CONFIGURATION.setElementBalancing(ParseConfiguration.ElementBalancing.REQUIRE_BALANCED);
        MARKUP_PARSING_CONFIGURATION.setCaseSensitive(true);
        MARKUP_PARSING_CONFIGURATION.setNoUnmatchedCloseElementsRequired(true);
        MARKUP_PARSING_CONFIGURATION.setUniqueAttributesInElementRequired(true);
        MARKUP_PARSING_CONFIGURATION.setXmlWellFormedAttributeValuesRequired(true);
        MARKUP_PARSING_CONFIGURATION.setUniqueRootElementPresence(ParseConfiguration.UniqueRootElementPresence.NOT_VALIDATED);
        MARKUP_PARSING_CONFIGURATION.getPrologParseConfiguration().setDoctypePresence(ParseConfiguration.PrologPresence.ALLOWED);
        MARKUP_PARSING_CONFIGURATION.getPrologParseConfiguration().setRequireDoctypeKeywordsUpperCase(false);
        MARKUP_PARSING_CONFIGURATION.getPrologParseConfiguration().setValidateProlog(false);
        MARKUP_PARSING_CONFIGURATION.getPrologParseConfiguration().setXmlDeclarationPresence(ParseConfiguration.PrologPresence.ALLOWED);
    }

    public XMLTemplateParser(int bufferPoolSize, int bufferSize) {
        super(MARKUP_PARSING_CONFIGURATION, bufferPoolSize, bufferSize);
    }
}