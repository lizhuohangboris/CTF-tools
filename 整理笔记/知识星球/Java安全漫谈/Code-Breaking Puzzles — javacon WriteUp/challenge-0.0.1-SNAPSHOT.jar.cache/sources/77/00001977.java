package org.springframework.boot.diagnostics;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.SpringBootExceptionReporter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/diagnostics/FailureAnalyzers.class */
final class FailureAnalyzers implements SpringBootExceptionReporter {
    private static final Log logger = LogFactory.getLog(FailureAnalyzers.class);
    private final ClassLoader classLoader;
    private final List<FailureAnalyzer> analyzers;

    FailureAnalyzers(ConfigurableApplicationContext context) {
        this(context, null);
    }

    FailureAnalyzers(ConfigurableApplicationContext context, ClassLoader classLoader) {
        Assert.notNull(context, "Context must not be null");
        this.classLoader = classLoader != null ? classLoader : context.getClassLoader();
        this.analyzers = loadFailureAnalyzers(this.classLoader);
        prepareFailureAnalyzers(this.analyzers, context);
    }

    private List<FailureAnalyzer> loadFailureAnalyzers(ClassLoader classLoader) {
        List<String> analyzerNames = SpringFactoriesLoader.loadFactoryNames(FailureAnalyzer.class, classLoader);
        List<FailureAnalyzer> analyzers = new ArrayList<>();
        for (String analyzerName : analyzerNames) {
            try {
                Constructor<?> constructor = ClassUtils.forName(analyzerName, classLoader).getDeclaredConstructor(new Class[0]);
                ReflectionUtils.makeAccessible(constructor);
                analyzers.add((FailureAnalyzer) constructor.newInstance(new Object[0]));
            } catch (Throwable ex) {
                logger.trace("Failed to load " + analyzerName, ex);
            }
        }
        AnnotationAwareOrderComparator.sort(analyzers);
        return analyzers;
    }

    private void prepareFailureAnalyzers(List<FailureAnalyzer> analyzers, ConfigurableApplicationContext context) {
        for (FailureAnalyzer analyzer : analyzers) {
            prepareAnalyzer(context, analyzer);
        }
    }

    private void prepareAnalyzer(ConfigurableApplicationContext context, FailureAnalyzer analyzer) {
        if (analyzer instanceof BeanFactoryAware) {
            ((BeanFactoryAware) analyzer).setBeanFactory(context.getBeanFactory());
        }
        if (analyzer instanceof EnvironmentAware) {
            ((EnvironmentAware) analyzer).setEnvironment(context.getEnvironment());
        }
    }

    @Override // org.springframework.boot.SpringBootExceptionReporter
    public boolean reportException(Throwable failure) {
        FailureAnalysis analysis = analyze(failure, this.analyzers);
        return report(analysis, this.classLoader);
    }

    private FailureAnalysis analyze(Throwable failure, List<FailureAnalyzer> analyzers) {
        FailureAnalysis analysis;
        for (FailureAnalyzer analyzer : analyzers) {
            try {
                analysis = analyzer.analyze(failure);
            } catch (Throwable ex) {
                logger.debug("FailureAnalyzer " + analyzer + " failed", ex);
            }
            if (analysis != null) {
                return analysis;
            }
        }
        return null;
    }

    private boolean report(FailureAnalysis analysis, ClassLoader classLoader) {
        List<FailureAnalysisReporter> reporters = SpringFactoriesLoader.loadFactories(FailureAnalysisReporter.class, classLoader);
        if (analysis == null || reporters.isEmpty()) {
            return false;
        }
        for (FailureAnalysisReporter reporter : reporters) {
            reporter.report(analysis);
        }
        return true;
    }
}