package org.springframework.ui.context.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.context.HierarchicalThemeSource;
import org.springframework.ui.context.ThemeSource;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/ui/context/support/UiApplicationContextUtils.class */
public abstract class UiApplicationContextUtils {
    public static final String THEME_SOURCE_BEAN_NAME = "themeSource";
    private static final Log logger = LogFactory.getLog(UiApplicationContextUtils.class);

    public static ThemeSource initThemeSource(ApplicationContext context) {
        HierarchicalThemeSource themeSource;
        if (context.containsLocalBean(THEME_SOURCE_BEAN_NAME)) {
            ThemeSource themeSource2 = (ThemeSource) context.getBean(THEME_SOURCE_BEAN_NAME, ThemeSource.class);
            if ((context.getParent() instanceof ThemeSource) && (themeSource2 instanceof HierarchicalThemeSource)) {
                HierarchicalThemeSource hts = (HierarchicalThemeSource) themeSource2;
                if (hts.getParentThemeSource() == null) {
                    hts.setParentThemeSource((ThemeSource) context.getParent());
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Using ThemeSource [" + themeSource2 + "]");
            }
            return themeSource2;
        }
        if (context.getParent() instanceof ThemeSource) {
            themeSource = new DelegatingThemeSource();
            themeSource.setParentThemeSource((ThemeSource) context.getParent());
        } else {
            themeSource = new ResourceBundleThemeSource();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Unable to locate ThemeSource with name 'themeSource': using default [" + themeSource + "]");
        }
        return themeSource;
    }
}