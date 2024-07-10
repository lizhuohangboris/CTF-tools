package ch.qos.logback.classic.joran;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.gaffer.GafferUtil;
import ch.qos.logback.classic.util.EnvUtil;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.spi.ConfigurationWatchList;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.StatusUtil;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.thymeleaf.engine.XMLDeclaration;
import org.thymeleaf.standard.processor.StandardIncludeTagProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/joran/ReconfigureOnChangeTask.class */
public class ReconfigureOnChangeTask extends ContextAwareBase implements Runnable {
    public static final String DETECTED_CHANGE_IN_CONFIGURATION_FILES = "Detected change in configuration files.";
    static final String RE_REGISTERING_PREVIOUS_SAFE_CONFIGURATION = "Re-registering previous fallback configuration once more as a fallback configuration point";
    static final String FALLING_BACK_TO_SAFE_CONFIGURATION = "Given previous errors, falling back to previously registered safe configuration.";
    long birthdate = System.currentTimeMillis();
    List<ReconfigureOnChangeTaskListener> listeners;

    void addListener(ReconfigureOnChangeTaskListener listener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList();
        }
        this.listeners.add(listener);
    }

    @Override // java.lang.Runnable
    public void run() {
        fireEnteredRunMethod();
        ConfigurationWatchList configurationWatchList = ConfigurationWatchListUtil.getConfigurationWatchList(this.context);
        if (configurationWatchList == null) {
            addWarn("Empty ConfigurationWatchList in context");
            return;
        }
        List<File> filesToWatch = configurationWatchList.getCopyOfFileWatchList();
        if (filesToWatch == null || filesToWatch.isEmpty()) {
            addInfo("Empty watch file list. Disabling ");
        } else if (!configurationWatchList.changeDetected()) {
        } else {
            fireChangeDetected();
            URL mainConfigurationURL = configurationWatchList.getMainURL();
            addInfo(DETECTED_CHANGE_IN_CONFIGURATION_FILES);
            addInfo("Will reset and reconfigure context named [" + this.context.getName() + "]");
            LoggerContext lc = (LoggerContext) this.context;
            if (mainConfigurationURL.toString().endsWith(XMLDeclaration.DEFAULT_KEYWORD)) {
                performXMLConfiguration(lc, mainConfigurationURL);
            } else if (mainConfigurationURL.toString().endsWith("groovy")) {
                if (EnvUtil.isGroovyAvailable()) {
                    lc.reset();
                    GafferUtil.runGafferConfiguratorOn(lc, this, mainConfigurationURL);
                } else {
                    addError("Groovy classes are not available on the class path. ABORTING INITIALIZATION.");
                }
            }
            fireDoneReconfiguring();
        }
    }

    private void fireEnteredRunMethod() {
        if (this.listeners == null) {
            return;
        }
        for (ReconfigureOnChangeTaskListener listener : this.listeners) {
            listener.enteredRunMethod();
        }
    }

    private void fireChangeDetected() {
        if (this.listeners == null) {
            return;
        }
        for (ReconfigureOnChangeTaskListener listener : this.listeners) {
            listener.changeDetected();
        }
    }

    private void fireDoneReconfiguring() {
        if (this.listeners == null) {
            return;
        }
        for (ReconfigureOnChangeTaskListener listener : this.listeners) {
            listener.doneReconfiguring();
        }
    }

    private void performXMLConfiguration(LoggerContext lc, URL mainConfigurationURL) {
        JoranConfigurator jc = new JoranConfigurator();
        jc.setContext(this.context);
        StatusUtil statusUtil = new StatusUtil(this.context);
        List<SaxEvent> eventList = jc.recallSafeConfiguration();
        URL mainURL = ConfigurationWatchListUtil.getMainWatchURL(this.context);
        lc.reset();
        long threshold = System.currentTimeMillis();
        try {
            jc.doConfigure(mainConfigurationURL);
            if (statusUtil.hasXMLParsingErrors(threshold)) {
                fallbackConfiguration(lc, eventList, mainURL);
            }
        } catch (JoranException e) {
            fallbackConfiguration(lc, eventList, mainURL);
        }
    }

    private List<SaxEvent> removeIncludeEvents(List<SaxEvent> unsanitizedEventList) {
        List<SaxEvent> sanitizedEvents = new ArrayList<>();
        if (unsanitizedEventList == null) {
            return sanitizedEvents;
        }
        for (SaxEvent e : unsanitizedEventList) {
            if (!StandardIncludeTagProcessor.ATTR_NAME.equalsIgnoreCase(e.getLocalName())) {
                sanitizedEvents.add(e);
            }
        }
        return sanitizedEvents;
    }

    private void fallbackConfiguration(LoggerContext lc, List<SaxEvent> eventList, URL mainURL) {
        List<SaxEvent> failsafeEvents = removeIncludeEvents(eventList);
        JoranConfigurator joranConfigurator = new JoranConfigurator();
        joranConfigurator.setContext(this.context);
        ConfigurationWatchList oldCWL = ConfigurationWatchListUtil.getConfigurationWatchList(this.context);
        ConfigurationWatchList newCWL = oldCWL.buildClone();
        if (failsafeEvents == null || failsafeEvents.isEmpty()) {
            addWarn("No previous configuration to fall back on.");
            return;
        }
        addWarn(FALLING_BACK_TO_SAFE_CONFIGURATION);
        try {
            lc.reset();
            ConfigurationWatchListUtil.registerConfigurationWatchList(this.context, newCWL);
            joranConfigurator.doConfigure(failsafeEvents);
            addInfo(RE_REGISTERING_PREVIOUS_SAFE_CONFIGURATION);
            joranConfigurator.registerSafeConfiguration(eventList);
            addInfo("after registerSafeConfiguration: " + eventList);
        } catch (JoranException e) {
            addError("Unexpected exception thrown by a configuration considered safe.", e);
        }
    }

    public String toString() {
        return "ReconfigureOnChangeTask(born:" + this.birthdate + ")";
    }
}