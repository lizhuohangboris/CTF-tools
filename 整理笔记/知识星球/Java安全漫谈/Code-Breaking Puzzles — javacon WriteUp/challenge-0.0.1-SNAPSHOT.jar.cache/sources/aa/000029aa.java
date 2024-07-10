package org.thymeleaf.templateparser.markup;

import org.attoparser.config.ParseConfiguration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/markup/HTMLTemplateParser.class */
public final class HTMLTemplateParser extends AbstractMarkupTemplateParser {
    static final ParseConfiguration MARKUP_PARSING_CONFIGURATION = ParseConfiguration.htmlConfiguration();

    static {
        MARKUP_PARSING_CONFIGURATION.setElementBalancing(ParseConfiguration.ElementBalancing.AUTO_CLOSE);
        MARKUP_PARSING_CONFIGURATION.setCaseSensitive(false);
        MARKUP_PARSING_CONFIGURATION.setNoUnmatchedCloseElementsRequired(false);
        MARKUP_PARSING_CONFIGURATION.setUniqueAttributesInElementRequired(true);
        MARKUP_PARSING_CONFIGURATION.setXmlWellFormedAttributeValuesRequired(false);
        MARKUP_PARSING_CONFIGURATION.setUniqueRootElementPresence(ParseConfiguration.UniqueRootElementPresence.NOT_VALIDATED);
        MARKUP_PARSING_CONFIGURATION.getPrologParseConfiguration().setDoctypePresence(ParseConfiguration.PrologPresence.ALLOWED);
        MARKUP_PARSING_CONFIGURATION.getPrologParseConfiguration().setRequireDoctypeKeywordsUpperCase(false);
        MARKUP_PARSING_CONFIGURATION.getPrologParseConfiguration().setValidateProlog(false);
        MARKUP_PARSING_CONFIGURATION.getPrologParseConfiguration().setXmlDeclarationPresence(ParseConfiguration.PrologPresence.ALLOWED);
    }

    public HTMLTemplateParser(int bufferPoolSize, int bufferSize) {
        super(MARKUP_PARSING_CONFIGURATION, bufferPoolSize, bufferSize);
    }
}