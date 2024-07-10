package ch.qos.logback.classic.sift;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.util.DefaultNestedComponentRules;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.action.ActionConst;
import ch.qos.logback.core.joran.action.AppenderAction;
import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import ch.qos.logback.core.joran.spi.ElementPath;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.RuleStore;
import ch.qos.logback.core.sift.SiftingJoranConfiguratorBase;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-classic-1.2.3.jar:ch/qos/logback/classic/sift/SiftingJoranConfigurator.class */
public class SiftingJoranConfigurator extends SiftingJoranConfiguratorBase<ILoggingEvent> {
    public SiftingJoranConfigurator(String key, String value, Map<String, String> parentPropertyMap) {
        super(key, value, parentPropertyMap);
    }

    @Override // ch.qos.logback.core.joran.GenericConfigurator
    protected ElementPath initialElementPath() {
        return new ElementPath("configuration");
    }

    @Override // ch.qos.logback.core.sift.SiftingJoranConfiguratorBase, ch.qos.logback.core.joran.GenericConfigurator
    public void addInstanceRules(RuleStore rs) {
        super.addInstanceRules(rs);
        rs.addRule(new ElementSelector("configuration/appender"), new AppenderAction());
    }

    @Override // ch.qos.logback.core.joran.GenericConfigurator
    protected void addDefaultNestedComponentRegistryRules(DefaultNestedComponentRegistry registry) {
        DefaultNestedComponentRules.addDefaultNestedComponentRegistryRules(registry);
    }

    @Override // ch.qos.logback.core.joran.GenericConfigurator
    public void buildInterpreter() {
        super.buildInterpreter();
        Map<String, Object> omap = this.interpreter.getInterpretationContext().getObjectMap();
        omap.put(ActionConst.APPENDER_BAG, new HashMap());
        Map<String, String> propertiesMap = new HashMap<>();
        propertiesMap.putAll(this.parentPropertyMap);
        propertiesMap.put(this.key, this.value);
        this.interpreter.setInterpretationContextPropertiesMap(propertiesMap);
    }

    @Override // ch.qos.logback.core.sift.SiftingJoranConfiguratorBase
    public Appender<ILoggingEvent> getAppender() {
        Map<String, Object> omap = this.interpreter.getInterpretationContext().getObjectMap();
        HashMap<String, Appender<?>> appenderMap = (HashMap) omap.get(ActionConst.APPENDER_BAG);
        oneAndOnlyOneCheck(appenderMap);
        Collection<Appender<?>> values = appenderMap.values();
        if (values.size() == 0) {
            return null;
        }
        return values.iterator().next();
    }
}