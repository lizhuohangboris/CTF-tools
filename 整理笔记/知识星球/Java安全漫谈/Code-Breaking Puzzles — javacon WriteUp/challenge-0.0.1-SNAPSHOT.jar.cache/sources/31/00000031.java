package ch.qos.logback.classic.joran.action;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.ReconfigureOnChangeTask;
import ch.qos.logback.classic.util.EnvUtil;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.util.ContextUtil;
import ch.qos.logback.core.util.Duration;
import ch.qos.logback.core.util.OptionHelper;
import ch.qos.logback.core.util.StatusListenerConfigHelper;
import java.net.URL;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/joran/action/ConfigurationAction.class */
public class ConfigurationAction extends Action {
    static final String INTERNAL_DEBUG_ATTR = "debug";
    static final String PACKAGING_DATA_ATTR = "packagingData";
    static final String SCAN_ATTR = "scan";
    static final String SCAN_PERIOD_ATTR = "scanPeriod";
    static final String DEBUG_SYSTEM_PROPERTY_KEY = "logback.debug";
    long threshold = 0;

    @Override // ch.qos.logback.core.joran.action.Action
    public void begin(InterpretationContext ic, String name, Attributes attributes) {
        this.threshold = System.currentTimeMillis();
        String debugAttrib = getSystemProperty(DEBUG_SYSTEM_PROPERTY_KEY);
        if (debugAttrib == null) {
            debugAttrib = ic.subst(attributes.getValue(INTERNAL_DEBUG_ATTR));
        }
        if (OptionHelper.isEmpty(debugAttrib) || debugAttrib.equalsIgnoreCase("false") || debugAttrib.equalsIgnoreCase(BeanDefinitionParserDelegate.NULL_ELEMENT)) {
            addInfo("debug attribute not set");
        } else {
            StatusListenerConfigHelper.addOnConsoleListenerInstance(this.context, new OnConsoleStatusListener());
        }
        processScanAttrib(ic, attributes);
        LoggerContext lc = (LoggerContext) this.context;
        boolean packagingData = OptionHelper.toBoolean(ic.subst(attributes.getValue(PACKAGING_DATA_ATTR)), false);
        lc.setPackagingDataEnabled(packagingData);
        if (EnvUtil.isGroovyAvailable()) {
            ContextUtil contextUtil = new ContextUtil(this.context);
            contextUtil.addGroovyPackages(lc.getFrameworkPackages());
        }
        ic.pushObject(getContext());
    }

    String getSystemProperty(String name) {
        try {
            return System.getProperty(name);
        } catch (SecurityException e) {
            return null;
        }
    }

    void processScanAttrib(InterpretationContext ic, Attributes attributes) {
        String scanAttrib = ic.subst(attributes.getValue(SCAN_ATTR));
        if (!OptionHelper.isEmpty(scanAttrib) && !"false".equalsIgnoreCase(scanAttrib)) {
            ScheduledExecutorService scheduledExecutorService = this.context.getScheduledExecutorService();
            URL mainURL = ConfigurationWatchListUtil.getMainWatchURL(this.context);
            if (mainURL == null) {
                addWarn("Due to missing top level configuration file, reconfiguration on change (configuration file scanning) cannot be done.");
                return;
            }
            ReconfigureOnChangeTask rocTask = new ReconfigureOnChangeTask();
            rocTask.setContext(this.context);
            this.context.putObject(CoreConstants.RECONFIGURE_ON_CHANGE_TASK, rocTask);
            String scanPeriodAttrib = ic.subst(attributes.getValue(SCAN_PERIOD_ATTR));
            Duration duration = getDuration(scanAttrib, scanPeriodAttrib);
            if (duration == null) {
                return;
            }
            addInfo("Will scan for changes in [" + mainURL + "] ");
            addInfo("Setting ReconfigureOnChangeTask scanning period to " + duration);
            ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(rocTask, duration.getMilliseconds(), duration.getMilliseconds(), TimeUnit.MILLISECONDS);
            this.context.addScheduledFuture(scheduledFuture);
        }
    }

    private Duration getDuration(String scanAttrib, String scanPeriodAttrib) {
        Duration duration = null;
        if (!OptionHelper.isEmpty(scanPeriodAttrib)) {
            try {
                duration = Duration.valueOf(scanPeriodAttrib);
            } catch (NumberFormatException nfe) {
                addError("Error while converting [" + scanAttrib + "] to long", nfe);
            }
        }
        return duration;
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void end(InterpretationContext ec, String name) {
        addInfo("End of configuration.");
        ec.popObject();
    }
}