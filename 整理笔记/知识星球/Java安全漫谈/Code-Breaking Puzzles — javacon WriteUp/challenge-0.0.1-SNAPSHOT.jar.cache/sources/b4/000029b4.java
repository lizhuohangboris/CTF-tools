package org.thymeleaf.templateparser.markup.decoupled;

import java.io.IOException;
import java.util.Set;
import org.attoparser.IMarkupParser;
import org.attoparser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.util.LoggingUtils;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/markup/decoupled/DecoupledTemplateLogicUtils.class */
public final class DecoupledTemplateLogicUtils {
    private static final Logger logger = LoggerFactory.getLogger(DecoupledTemplateLogicUtils.class);

    public static DecoupledTemplateLogic computeDecoupledTemplateLogic(IEngineConfiguration configuration, String ownerTemplate, String template, Set<String> templateSelectors, ITemplateResource resource, TemplateMode templateMode, IMarkupParser parser) throws IOException, ParseException {
        Validate.notNull(configuration, "Engine Configuration cannot be null");
        Validate.notNull(template, "Template cannot be null");
        Validate.notNull(resource, "Template Resource cannot be null");
        Validate.notNull(templateMode, "Template Mode cannot be null");
        IDecoupledTemplateLogicResolver decoupledTemplateLogicResolver = configuration.getDecoupledTemplateLogicResolver();
        ITemplateResource decoupledResource = decoupledTemplateLogicResolver.resolveDecoupledTemplateLogic(configuration, ownerTemplate, template, templateSelectors, resource, templateMode);
        if (!decoupledResource.exists()) {
            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] Decoupled logic for template \"{}\" could not be resolved as relative resource \"{}\". This does not need to be an error, as templates may lack a corresponding decoupled logic file.", TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(template), decoupledResource.getDescription());
                return null;
            }
            return null;
        }
        if (logger.isTraceEnabled()) {
            logger.trace("[THYMELEAF][{}] Decoupled logic for template \"{}\" has been resolved as relative resource \"{}\"", TemplateEngine.threadIndex(), LoggingUtils.loggifyTemplateName(template), decoupledResource.getDescription());
        }
        DecoupledTemplateLogicBuilderMarkupHandler decoupledMarkupHandler = new DecoupledTemplateLogicBuilderMarkupHandler(template, templateMode);
        parser.parse(decoupledResource.reader(), decoupledMarkupHandler);
        return decoupledMarkupHandler.getDecoupledTemplateLogic();
    }

    private DecoupledTemplateLogicUtils() {
    }
}