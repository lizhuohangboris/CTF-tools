package org.thymeleaf.templateparser.markup;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Set;
import org.attoparser.IMarkupHandler;
import org.attoparser.IMarkupParser;
import org.attoparser.MarkupParser;
import org.attoparser.ParseException;
import org.attoparser.config.ParseConfiguration;
import org.attoparser.select.BlockSelectorMarkupHandler;
import org.attoparser.select.NodeSelectorMarkupHandler;
import org.thymeleaf.EngineConfiguration;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.engine.TemplateHandlerAdapterMarkupHandler;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.templateparser.markup.decoupled.DecoupledTemplateLogic;
import org.thymeleaf.templateparser.markup.decoupled.DecoupledTemplateLogicMarkupHandler;
import org.thymeleaf.templateparser.markup.decoupled.DecoupledTemplateLogicUtils;
import org.thymeleaf.templateparser.reader.ParserLevelCommentMarkupReader;
import org.thymeleaf.templateparser.reader.PrototypeOnlyCommentMarkupReader;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/markup/AbstractMarkupTemplateParser.class */
public abstract class AbstractMarkupTemplateParser implements ITemplateParser {
    private final IMarkupParser parser;
    private final boolean html;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractMarkupTemplateParser(ParseConfiguration parseConfiguration, int bufferPoolSize, int bufferSize) {
        Validate.notNull(parseConfiguration, "Parse configuration cannot be null");
        this.parser = new MarkupParser(parseConfiguration, bufferPoolSize, bufferSize);
        this.html = parseConfiguration.getMode().equals(ParseConfiguration.ParsingMode.HTML);
    }

    @Override // org.thymeleaf.templateparser.ITemplateParser
    public void parseStandalone(IEngineConfiguration configuration, String ownerTemplate, String template, Set<String> templateSelectors, ITemplateResource resource, TemplateMode templateMode, boolean useDecoupledLogic, ITemplateHandler handler) {
        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(template, "Template cannot be null");
        Validate.notNull(resource, "Template Resource cannot be null");
        Validate.notNull(templateMode, "Template Mode cannot be null");
        Validate.isTrue(templateMode.isMarkup(), "Template Mode has to be a markup template mode");
        Validate.notNull(handler, "Template Handler cannot be null");
        parse(configuration, ownerTemplate, template, templateSelectors, resource, 0, 0, templateMode, useDecoupledLogic, handler);
    }

    @Override // org.thymeleaf.templateparser.ITemplateParser
    public void parseString(IEngineConfiguration configuration, String ownerTemplate, String template, int lineOffset, int colOffset, TemplateMode templateMode, ITemplateHandler handler) {
        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(ownerTemplate, "Owner template cannot be null");
        Validate.notNull(template, "Template cannot be null");
        Validate.notNull(templateMode, "Template mode cannot be null");
        Validate.isTrue(templateMode.isMarkup(), "Template Mode has to be a markup template mode");
        Validate.notNull(handler, "Template Handler cannot be null");
        parse(configuration, ownerTemplate, template, null, null, lineOffset, colOffset, templateMode, false, handler);
    }

    private void parse(IEngineConfiguration configuration, String ownerTemplate, String template, Set<String> templateSelectors, ITemplateResource resource, int lineOffset, int colOffset, TemplateMode templateMode, boolean useDecoupledLogic, ITemplateHandler templateHandler) {
        DecoupledTemplateLogic decoupledTemplateLogic;
        TemplateFragmentMarkupReferenceResolver referenceResolver;
        if (templateMode == TemplateMode.HTML) {
            Validate.isTrue(this.html, "Parser is configured as XML, but HTML-mode template parsing is being requested");
        } else if (templateMode == TemplateMode.XML) {
            Validate.isTrue(!this.html, "Parser is configured as HTML, but XML-mode template parsing is being requested");
        } else {
            throw new IllegalArgumentException("Parser is configured as " + (this.html ? "HTML" : "XML") + " but an unsupported template mode has been specified: " + templateMode);
        }
        String templateName = resource != null ? template : ownerTemplate;
        if (useDecoupledLogic && resource != null) {
            try {
                decoupledTemplateLogic = DecoupledTemplateLogicUtils.computeDecoupledTemplateLogic(configuration, ownerTemplate, template, templateSelectors, resource, templateMode, this.parser);
            } catch (IOException e) {
                throw new TemplateInputException("An error happened during template parsing", resource != null ? resource.getDescription() : template, e);
            } catch (ParseException e2) {
                if (e2.getLine() != null && e2.getCol() != null) {
                    throw new TemplateInputException("An error happened during template parsing", resource != null ? resource.getDescription() : template, e2.getLine().intValue(), e2.getCol().intValue(), e2);
                }
                throw new TemplateInputException("An error happened during template parsing", resource != null ? resource.getDescription() : template, e2);
            }
        } else {
            decoupledTemplateLogic = null;
        }
        DecoupledTemplateLogic decoupledTemplateLogic2 = decoupledTemplateLogic;
        IMarkupHandler handler = new TemplateHandlerAdapterMarkupHandler(templateName, templateHandler, configuration.getElementDefinitions(), configuration.getAttributeDefinitions(), templateMode, lineOffset, colOffset);
        if ((configuration instanceof EngineConfiguration) && ((EngineConfiguration) configuration).isModelReshapeable(templateMode)) {
            handler = new InlinedOutputExpressionMarkupHandler(configuration, templateMode, configuration.getStandardDialectPrefix(), handler);
        }
        boolean injectAttributes = decoupledTemplateLogic2 != null && decoupledTemplateLogic2.hasInjectedAttributes();
        boolean selectBlock = (templateSelectors == null || templateSelectors.isEmpty()) ? false : true;
        if (injectAttributes || selectBlock) {
            String standardDialectPrefix = configuration.getStandardDialectPrefix();
            referenceResolver = standardDialectPrefix != null ? TemplateFragmentMarkupReferenceResolver.forPrefix(this.html, standardDialectPrefix) : null;
        } else {
            referenceResolver = null;
        }
        if (selectBlock) {
            handler = new BlockSelectorMarkupHandler(handler, (String[]) templateSelectors.toArray(new String[templateSelectors.size()]), referenceResolver);
        }
        if (injectAttributes) {
            IMarkupHandler handler2 = new DecoupledTemplateLogicMarkupHandler(decoupledTemplateLogic2, handler);
            Set<String> nodeSelectors = decoupledTemplateLogic2.getAllInjectedAttributeSelectors();
            handler = new NodeSelectorMarkupHandler(handler2, handler2, (String[]) nodeSelectors.toArray(new String[nodeSelectors.size()]), referenceResolver);
        }
        Reader templateReader = resource != null ? resource.reader() : new StringReader(template);
        this.parser.parse(new ParserLevelCommentMarkupReader(new PrototypeOnlyCommentMarkupReader(templateReader)), handler);
    }
}