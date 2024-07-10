package ch.qos.logback.classic.turbo;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.gaffer.GafferUtil;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.util.EnvUtil;
import ch.qos.logback.core.joran.event.SaxEvent;
import ch.qos.logback.core.joran.spi.ConfigurationWatchList;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.status.StatusUtil;
import java.io.File;
import java.net.URL;
import java.util.List;
import org.slf4j.Marker;
import org.thymeleaf.engine.XMLDeclaration;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/turbo/ReconfigureOnChangeFilter.class */
public class ReconfigureOnChangeFilter extends TurboFilter {
    public static final long DEFAULT_REFRESH_PERIOD = 60000;
    URL mainConfigurationURL;
    protected volatile long nextCheck;
    ConfigurationWatchList configurationWatchList;
    private static final int MAX_MASK = 65535;
    private static final long MASK_INCREASE_THRESHOLD = 100;
    private static final long MASK_DECREASE_THRESHOLD = 800;
    long refreshPeriod = 60000;
    private long invocationCounter = 0;
    private volatile long mask = 15;
    private volatile long lastMaskCheck = System.currentTimeMillis();

    @Override // ch.qos.logback.classic.turbo.TurboFilter, ch.qos.logback.core.spi.LifeCycle
    public void start() {
        this.configurationWatchList = ConfigurationWatchListUtil.getConfigurationWatchList(this.context);
        if (this.configurationWatchList != null) {
            this.mainConfigurationURL = this.configurationWatchList.getMainURL();
            if (this.mainConfigurationURL == null) {
                addWarn("Due to missing top level configuration file, automatic reconfiguration is impossible.");
                return;
            }
            List<File> watchList = this.configurationWatchList.getCopyOfFileWatchList();
            long inSeconds = this.refreshPeriod / 1000;
            addInfo("Will scan for changes in [" + watchList + "] every " + inSeconds + " seconds. ");
            synchronized (this.configurationWatchList) {
                updateNextCheck(System.currentTimeMillis());
            }
            super.start();
            return;
        }
        addWarn("Empty ConfigurationWatchList in context");
    }

    public String toString() {
        return "ReconfigureOnChangeFilter{invocationCounter=" + this.invocationCounter + '}';
    }

    @Override // ch.qos.logback.classic.turbo.TurboFilter
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }
        long j = this.invocationCounter;
        this.invocationCounter = j + 1;
        if ((j & this.mask) != this.mask) {
            return FilterReply.NEUTRAL;
        }
        long now = System.currentTimeMillis();
        synchronized (this.configurationWatchList) {
            updateMaskIfNecessary(now);
            if (changeDetected(now)) {
                disableSubsequentReconfiguration();
                detachReconfigurationToNewThread();
            }
        }
        return FilterReply.NEUTRAL;
    }

    private void updateMaskIfNecessary(long now) {
        long timeElapsedSinceLastMaskUpdateCheck = now - this.lastMaskCheck;
        this.lastMaskCheck = now;
        if (timeElapsedSinceLastMaskUpdateCheck < MASK_INCREASE_THRESHOLD && this.mask < 65535) {
            this.mask = (this.mask << 1) | 1;
        } else if (timeElapsedSinceLastMaskUpdateCheck > MASK_DECREASE_THRESHOLD) {
            this.mask >>>= 2;
        }
    }

    void detachReconfigurationToNewThread() {
        addInfo("Detected change in [" + this.configurationWatchList.getCopyOfFileWatchList() + "]");
        this.context.getExecutorService().submit(new ReconfiguringThread());
    }

    void updateNextCheck(long now) {
        this.nextCheck = now + this.refreshPeriod;
    }

    protected boolean changeDetected(long now) {
        if (now >= this.nextCheck) {
            updateNextCheck(now);
            return this.configurationWatchList.changeDetected();
        }
        return false;
    }

    void disableSubsequentReconfiguration() {
        this.nextCheck = Long.MAX_VALUE;
    }

    public long getRefreshPeriod() {
        return this.refreshPeriod;
    }

    public void setRefreshPeriod(long refreshPeriod) {
        this.refreshPeriod = refreshPeriod;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/turbo/ReconfigureOnChangeFilter$ReconfiguringThread.class */
    public class ReconfiguringThread implements Runnable {
        ReconfiguringThread() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (ReconfigureOnChangeFilter.this.mainConfigurationURL != null) {
                LoggerContext lc = (LoggerContext) ReconfigureOnChangeFilter.this.context;
                ReconfigureOnChangeFilter.this.addInfo("Will reset and reconfigure context named [" + ReconfigureOnChangeFilter.this.context.getName() + "]");
                if (ReconfigureOnChangeFilter.this.mainConfigurationURL.toString().endsWith(XMLDeclaration.DEFAULT_KEYWORD)) {
                    performXMLConfiguration(lc);
                    return;
                } else if (ReconfigureOnChangeFilter.this.mainConfigurationURL.toString().endsWith("groovy")) {
                    if (EnvUtil.isGroovyAvailable()) {
                        lc.reset();
                        GafferUtil.runGafferConfiguratorOn(lc, this, ReconfigureOnChangeFilter.this.mainConfigurationURL);
                        return;
                    }
                    ReconfigureOnChangeFilter.this.addError("Groovy classes are not available on the class path. ABORTING INITIALIZATION.");
                    return;
                } else {
                    return;
                }
            }
            ReconfigureOnChangeFilter.this.addInfo("Due to missing top level configuration file, skipping reconfiguration");
        }

        private void performXMLConfiguration(LoggerContext lc) {
            JoranConfigurator jc = new JoranConfigurator();
            jc.setContext(ReconfigureOnChangeFilter.this.context);
            StatusUtil statusUtil = new StatusUtil(ReconfigureOnChangeFilter.this.context);
            List<SaxEvent> eventList = jc.recallSafeConfiguration();
            URL mainURL = ConfigurationWatchListUtil.getMainWatchURL(ReconfigureOnChangeFilter.this.context);
            lc.reset();
            long threshold = System.currentTimeMillis();
            try {
                jc.doConfigure(ReconfigureOnChangeFilter.this.mainConfigurationURL);
                if (statusUtil.hasXMLParsingErrors(threshold)) {
                    fallbackConfiguration(lc, eventList, mainURL);
                }
            } catch (JoranException e) {
                fallbackConfiguration(lc, eventList, mainURL);
            }
        }

        private void fallbackConfiguration(LoggerContext lc, List<SaxEvent> eventList, URL mainURL) {
            JoranConfigurator joranConfigurator = new JoranConfigurator();
            joranConfigurator.setContext(ReconfigureOnChangeFilter.this.context);
            if (eventList != null) {
                ReconfigureOnChangeFilter.this.addWarn("Falling back to previously registered safe configuration.");
                try {
                    lc.reset();
                    JoranConfigurator.informContextOfURLUsedForConfiguration(ReconfigureOnChangeFilter.this.context, mainURL);
                    joranConfigurator.doConfigure(eventList);
                    ReconfigureOnChangeFilter.this.addInfo("Re-registering previous fallback configuration once more as a fallback configuration point");
                    joranConfigurator.registerSafeConfiguration(eventList);
                    return;
                } catch (JoranException e) {
                    ReconfigureOnChangeFilter.this.addError("Unexpected exception thrown by a configuration considered safe.", e);
                    return;
                }
            }
            ReconfigureOnChangeFilter.this.addWarn("No previous configuration to fall back on.");
        }
    }
}