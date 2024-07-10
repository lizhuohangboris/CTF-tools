package ch.qos.logback.classic.joran;

import ch.qos.logback.classic.joran.action.ConfigurationAction;
import ch.qos.logback.classic.joran.action.ConsolePluginAction;
import ch.qos.logback.classic.joran.action.ContextNameAction;
import ch.qos.logback.classic.joran.action.EvaluatorAction;
import ch.qos.logback.classic.joran.action.InsertFromJNDIAction;
import ch.qos.logback.classic.joran.action.JMXConfiguratorAction;
import ch.qos.logback.classic.joran.action.LevelAction;
import ch.qos.logback.classic.joran.action.LoggerAction;
import ch.qos.logback.classic.joran.action.LoggerContextListenerAction;
import ch.qos.logback.classic.joran.action.ReceiverAction;
import ch.qos.logback.classic.joran.action.RootLoggerAction;
import ch.qos.logback.classic.sift.SiftAction;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.PlatformInfo;
import ch.qos.logback.classic.util.DefaultNestedComponentRules;
import ch.qos.logback.core.joran.JoranConfiguratorBase;
import ch.qos.logback.core.joran.action.AppenderRefAction;
import ch.qos.logback.core.joran.action.IncludeAction;
import ch.qos.logback.core.joran.action.NOPAction;
import ch.qos.logback.core.joran.conditional.ElseAction;
import ch.qos.logback.core.joran.conditional.IfAction;
import ch.qos.logback.core.joran.conditional.ThenAction;
import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.RuleStore;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/joran/JoranConfigurator.class */
public class JoranConfigurator extends JoranConfiguratorBase<ILoggingEvent> {
    @Override // ch.qos.logback.core.joran.JoranConfiguratorBase, ch.qos.logback.core.joran.GenericConfigurator
    public void addInstanceRules(RuleStore rs) {
        super.addInstanceRules(rs);
        rs.addRule(new ElementSelector("configuration"), new ConfigurationAction());
        rs.addRule(new ElementSelector("configuration/contextName"), new ContextNameAction());
        rs.addRule(new ElementSelector("configuration/contextListener"), new LoggerContextListenerAction());
        rs.addRule(new ElementSelector("configuration/insertFromJNDI"), new InsertFromJNDIAction());
        rs.addRule(new ElementSelector("configuration/evaluator"), new EvaluatorAction());
        rs.addRule(new ElementSelector("configuration/appender/sift"), new SiftAction());
        rs.addRule(new ElementSelector("configuration/appender/sift/*"), new NOPAction());
        rs.addRule(new ElementSelector("configuration/logger"), new LoggerAction());
        rs.addRule(new ElementSelector("configuration/logger/level"), new LevelAction());
        rs.addRule(new ElementSelector("configuration/root"), new RootLoggerAction());
        rs.addRule(new ElementSelector("configuration/root/level"), new LevelAction());
        rs.addRule(new ElementSelector("configuration/logger/appender-ref"), new AppenderRefAction());
        rs.addRule(new ElementSelector("configuration/root/appender-ref"), new AppenderRefAction());
        rs.addRule(new ElementSelector("*/if"), new IfAction());
        rs.addRule(new ElementSelector("*/if/then"), new ThenAction());
        rs.addRule(new ElementSelector("*/if/then/*"), new NOPAction());
        rs.addRule(new ElementSelector("*/if/else"), new ElseAction());
        rs.addRule(new ElementSelector("*/if/else/*"), new NOPAction());
        if (PlatformInfo.hasJMXObjectName()) {
            rs.addRule(new ElementSelector("configuration/jmxConfigurator"), new JMXConfiguratorAction());
        }
        rs.addRule(new ElementSelector("configuration/include"), new IncludeAction());
        rs.addRule(new ElementSelector("configuration/consolePlugin"), new ConsolePluginAction());
        rs.addRule(new ElementSelector("configuration/receiver"), new ReceiverAction());
    }

    @Override // ch.qos.logback.core.joran.GenericConfigurator
    protected void addDefaultNestedComponentRegistryRules(DefaultNestedComponentRegistry registry) {
        DefaultNestedComponentRules.addDefaultNestedComponentRegistryRules(registry);
    }
}