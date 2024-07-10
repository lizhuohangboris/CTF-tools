package org.thymeleaf.templateparser.raw;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Set;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.engine.ITemplateHandler;
import org.thymeleaf.engine.TemplateHandlerAdapterRawHandler;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateparser.ITemplateParser;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/raw/RawTemplateParser.class */
public final class RawTemplateParser implements ITemplateParser {
    private final RawParser parser;

    public RawTemplateParser(int bufferPoolSize, int bufferSize) {
        this.parser = new RawParser(bufferPoolSize, bufferSize);
    }

    @Override // org.thymeleaf.templateparser.ITemplateParser
    public void parseStandalone(IEngineConfiguration configuration, String ownerTemplate, String template, Set<String> templateSelectors, ITemplateResource resource, TemplateMode templateMode, boolean useDecoupledLogic, ITemplateHandler handler) {
        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(template, "Template cannot be null");
        Validate.notNull(resource, "Template Resource cannot be null");
        Validate.isTrue(templateSelectors == null || templateSelectors.isEmpty(), "Template selectors cannot be specified for a template using RAW template mode: template insertion operations must be always performed on whole template files, not fragments");
        Validate.notNull(templateMode, "Template Mode cannot be null");
        Validate.isTrue(templateMode == TemplateMode.RAW, "Template Mode has to be RAW");
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
        Validate.isTrue(templateMode == TemplateMode.RAW, "Template Mode has to be RAW");
        Validate.notNull(handler, "Template Handler cannot be null");
        parse(configuration, ownerTemplate, template, null, null, lineOffset, colOffset, templateMode, handler);
    }

    private void parse(IEngineConfiguration configuration, String ownerTemplate, String template, Set<String> templateSelectors, ITemplateResource resource, int lineOffset, int colOffset, TemplateMode templateMode, ITemplateHandler templateHandler) {
        String templateName = resource != null ? template : ownerTemplate;
        try {
            IRawHandler handler = new TemplateHandlerAdapterRawHandler(templateName, templateHandler, lineOffset, colOffset);
            Reader templateReader = resource != null ? resource.reader() : new StringReader(template);
            this.parser.parse(templateReader, handler);
        } catch (IOException e) {
            throw new TemplateInputException("An error happened during template parsing", resource != null ? resource.getDescription() : template, e);
        } catch (RawParseException e2) {
            if (e2.getLine() != null && e2.getCol() != null) {
                throw new TemplateInputException("An error happened during template parsing", resource != null ? resource.getDescription() : template, e2.getLine().intValue(), e2.getCol().intValue(), e2);
            }
            throw new TemplateInputException("An error happened during template parsing", resource != null ? resource.getDescription() : template, e2);
        }
    }
}