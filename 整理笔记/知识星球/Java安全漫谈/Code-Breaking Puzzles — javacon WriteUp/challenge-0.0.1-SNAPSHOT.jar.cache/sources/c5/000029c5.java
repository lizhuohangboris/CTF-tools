package org.thymeleaf.templateparser.text;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Set;
import org.thymeleaf.EngineConfiguration;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.engine.TemplateHandlerAdapterTextHandler;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.templateparser.reader.ParserLevelCommentTextReader;
import org.thymeleaf.templateparser.reader.PrototypeOnlyCommentTextReader;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/text/AbstractTextTemplateParser.class */
public abstract class AbstractTextTemplateParser implements ITemplateParser {
    private final TextParser parser;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractTextTemplateParser(int bufferPoolSize, int bufferSize, boolean processCommentsAndLiterals, boolean standardDialectPresent) {
        this.parser = new TextParser(bufferPoolSize, bufferSize, processCommentsAndLiterals, standardDialectPresent);
    }

    @Override // org.thymeleaf.templateparser.ITemplateParser
    public void parseStandalone(IEngineConfiguration configuration, String ownerTemplate, String template, Set<String> templateSelectors, ITemplateResource resource, TemplateMode templateMode, boolean useDecoupledLogic, ITemplateHandler handler) {
        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(template, "Template cannot be null");
        Validate.notNull(resource, "Template Resource cannot be null");
        Validate.isTrue(templateSelectors == null || templateSelectors.isEmpty(), "Template selectors cannot be specified for a template using a TEXT template mode: template insertion operations must be always performed on whole template files, not fragments");
        Validate.notNull(templateMode, "Template Mode cannot be null");
        Validate.isTrue(templateMode.isText(), "Template Mode has to be a text template mode");
        Validate.isTrue(!useDecoupledLogic, "Cannot use decoupled logic in template mode " + templateMode);
        Validate.notNull(handler, "Template Handler cannot be null");
        parse(configuration, ownerTemplate, template, templateSelectors, resource, 0, 0, templateMode, handler);
    }

    @Override // org.thymeleaf.templateparser.ITemplateParser
    public void parseString(IEngineConfiguration configuration, String ownerTemplate, String template, int lineOffset, int colOffset, TemplateMode templateMode, ITemplateHandler handler) {
        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(ownerTemplate, "Owner template cannot be null");
        Validate.notNull(template, "Template cannot be null");
        Validate.notNull(templateMode, "Template mode cannot be null");
        Validate.isTrue(templateMode.isText(), "Template Mode has to be a text template mode");
        Validate.notNull(handler, "Template Handler cannot be null");
        parse(configuration, ownerTemplate, template, null, null, lineOffset, colOffset, templateMode, handler);
    }

    private void parse(IEngineConfiguration configuration, String ownerTemplate, String template, Set<String> templateSelectors, ITemplateResource resource, int lineOffset, int colOffset, TemplateMode templateMode, ITemplateHandler templateHandler) {
        Reader templateReader;
        String templateName = resource != null ? template : ownerTemplate;
        try {
            ITextHandler handler = new TemplateHandlerAdapterTextHandler(templateName, templateHandler, configuration.getElementDefinitions(), configuration.getAttributeDefinitions(), templateMode, lineOffset, colOffset);
            if ((configuration instanceof EngineConfiguration) && ((EngineConfiguration) configuration).isModelReshapeable(templateMode)) {
                handler = new InlinedOutputExpressionTextHandler(configuration, templateMode, configuration.getStandardDialectPrefix(), handler);
            }
            Reader templateReader2 = resource != null ? resource.reader() : new StringReader(template);
            if (templateMode == TemplateMode.TEXT) {
                templateReader = new ParserLevelCommentTextReader(templateReader2);
            } else {
                templateReader = new ParserLevelCommentTextReader(new PrototypeOnlyCommentTextReader(templateReader2));
            }
            this.parser.parse(templateReader, handler);
        } catch (IOException e) {
            throw new TemplateInputException("An error happened during template parsing", resource != null ? resource.getDescription() : template, e);
        } catch (TextParseException e2) {
            if (e2.getLine() != null && e2.getCol() != null) {
                throw new TemplateInputException("An error happened during template parsing", resource != null ? resource.getDescription() : template, e2.getLine().intValue(), e2.getCol().intValue(), e2);
            }
            throw new TemplateInputException("An error happened during template parsing", resource != null ? resource.getDescription() : template, e2);
        }
    }
}