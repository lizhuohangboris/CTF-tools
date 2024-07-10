package ch.qos.logback.core.joran.action;

import ch.qos.logback.core.joran.action.ActionUtil;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.pattern.util.RegularEscapeUtil;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.OptionHelper;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/action/PropertyAction.class */
public class PropertyAction extends Action {
    static final String RESOURCE_ATTRIBUTE = "resource";
    static String INVALID_ATTRIBUTES = "In <property> element, either the \"file\" attribute alone, or the \"resource\" element alone, or both the \"name\" and \"value\" attributes must be set.";

    @Override // ch.qos.logback.core.joran.action.Action
    public void begin(InterpretationContext ec, String localName, Attributes attributes) {
        if ("substitutionProperty".equals(localName)) {
            addWarn("[substitutionProperty] element has been deprecated. Please use the [property] element instead.");
        }
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        String scopeStr = attributes.getValue("scope");
        ActionUtil.Scope scope = ActionUtil.stringToScope(scopeStr);
        if (checkFileAttributeSanity(attributes)) {
            String file = ec.subst(attributes.getValue("file"));
            try {
                FileInputStream istream = new FileInputStream(file);
                loadAndSetProperties(ec, istream, scope);
            } catch (FileNotFoundException e) {
                addError("Could not find properties file [" + file + "].");
            } catch (IOException e1) {
                addError("Could not read properties file [" + file + "].", e1);
            }
        } else if (!checkResourceAttributeSanity(attributes)) {
            if (checkValueNameAttributesSanity(attributes)) {
                ActionUtil.setProperty(ec, name, ec.subst(RegularEscapeUtil.basicEscape(value).trim()), scope);
            } else {
                addError(INVALID_ATTRIBUTES);
            }
        } else {
            String resource = ec.subst(attributes.getValue("resource"));
            URL resourceURL = Loader.getResourceBySelfClassLoader(resource);
            if (resourceURL == null) {
                addError("Could not find resource [" + resource + "].");
                return;
            }
            try {
                InputStream istream2 = resourceURL.openStream();
                loadAndSetProperties(ec, istream2, scope);
            } catch (IOException e2) {
                addError("Could not read resource file [" + resource + "].", e2);
            }
        }
    }

    void loadAndSetProperties(InterpretationContext ec, InputStream istream, ActionUtil.Scope scope) throws IOException {
        Properties props = new Properties();
        props.load(istream);
        istream.close();
        ActionUtil.setProperties(ec, props, scope);
    }

    boolean checkFileAttributeSanity(Attributes attributes) {
        String file = attributes.getValue("file");
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        String resource = attributes.getValue("resource");
        return !OptionHelper.isEmpty(file) && OptionHelper.isEmpty(name) && OptionHelper.isEmpty(value) && OptionHelper.isEmpty(resource);
    }

    boolean checkResourceAttributeSanity(Attributes attributes) {
        String file = attributes.getValue("file");
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        String resource = attributes.getValue("resource");
        return !OptionHelper.isEmpty(resource) && OptionHelper.isEmpty(name) && OptionHelper.isEmpty(value) && OptionHelper.isEmpty(file);
    }

    boolean checkValueNameAttributesSanity(Attributes attributes) {
        String file = attributes.getValue("file");
        String name = attributes.getValue("name");
        String value = attributes.getValue("value");
        String resource = attributes.getValue("resource");
        return !OptionHelper.isEmpty(name) && !OptionHelper.isEmpty(value) && OptionHelper.isEmpty(file) && OptionHelper.isEmpty(resource);
    }

    @Override // ch.qos.logback.core.joran.action.Action
    public void end(InterpretationContext ec, String name) {
    }

    public void finish(InterpretationContext ec) {
    }
}