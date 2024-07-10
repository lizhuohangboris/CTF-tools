package org.springframework.boot.autoconfigure.freemarker;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.template.TemplateLocation;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

@EnableConfigurationProperties({FreeMarkerProperties.class})
@Configuration
@ConditionalOnClass({freemarker.template.Configuration.class, FreeMarkerConfigurationFactory.class})
@Import({FreeMarkerServletWebConfiguration.class, FreeMarkerReactiveWebConfiguration.class, FreeMarkerNonWebConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/freemarker/FreeMarkerAutoConfiguration.class */
public class FreeMarkerAutoConfiguration {
    private static final Log logger = LogFactory.getLog(FreeMarkerAutoConfiguration.class);
    private final ApplicationContext applicationContext;
    private final FreeMarkerProperties properties;

    public FreeMarkerAutoConfiguration(ApplicationContext applicationContext, FreeMarkerProperties properties) {
        this.applicationContext = applicationContext;
        this.properties = properties;
    }

    @PostConstruct
    public void checkTemplateLocationExists() {
        if (logger.isWarnEnabled() && this.properties.isCheckTemplateLocation()) {
            List<TemplateLocation> locations = getLocations();
            if (locations.stream().noneMatch(this::locationExists)) {
                logger.warn("Cannot find template location(s): " + locations + " (please add some templates, check your FreeMarker configuration, or set spring.freemarker.checkTemplateLocation=false)");
            }
        }
    }

    private List<TemplateLocation> getLocations() {
        String[] templateLoaderPath;
        List<TemplateLocation> locations = new ArrayList<>();
        for (String templateLoaderPath2 : this.properties.getTemplateLoaderPath()) {
            TemplateLocation location = new TemplateLocation(templateLoaderPath2);
            locations.add(location);
        }
        return locations;
    }

    private boolean locationExists(TemplateLocation location) {
        return location.exists(this.applicationContext);
    }
}