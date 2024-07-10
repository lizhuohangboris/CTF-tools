package org.apache.tomcat.util.descriptor.tld;

import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.Rule;
import org.apache.tomcat.util.digester.RuleSet;
import org.apache.tomcat.util.res.StringManager;
import org.thymeleaf.engine.XMLDeclaration;
import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/tld/ImplicitTldRuleSet.class */
public class ImplicitTldRuleSet implements RuleSet {
    private static final StringManager sm = StringManager.getManager(ImplicitTldRuleSet.class);
    private static final String PREFIX = "taglib";
    private static final String VALIDATOR_PREFIX = "taglib/validator";
    private static final String TAG_PREFIX = "taglib/tag";
    private static final String TAGFILE_PREFIX = "taglib/tag-file";
    private static final String FUNCTION_PREFIX = "taglib/function";

    @Override // org.apache.tomcat.util.digester.RuleSet
    public void addRuleInstances(Digester digester) {
        digester.addCallMethod("taglib/tlibversion", "setTlibVersion", 0);
        digester.addCallMethod("taglib/tlib-version", "setTlibVersion", 0);
        digester.addCallMethod("taglib/jspversion", "setJspVersion", 0);
        digester.addCallMethod("taglib/jsp-version", "setJspVersion", 0);
        digester.addRule(PREFIX, new Rule() { // from class: org.apache.tomcat.util.descriptor.tld.ImplicitTldRuleSet.1
            @Override // org.apache.tomcat.util.digester.Rule
            public void begin(String namespace, String name, Attributes attributes) {
                TaglibXml taglibXml = (TaglibXml) this.digester.peek();
                taglibXml.setJspVersion(attributes.getValue(XMLDeclaration.ATTRIBUTE_NAME_VERSION));
            }
        });
        digester.addCallMethod("taglib/shortname", "setShortName", 0);
        digester.addCallMethod("taglib/short-name", "setShortName", 0);
        digester.addRule("taglib/uri", new ElementNotAllowedRule());
        digester.addRule("taglib/info", new ElementNotAllowedRule());
        digester.addRule("taglib/description", new ElementNotAllowedRule());
        digester.addRule("taglib/listener/listener-class", new ElementNotAllowedRule());
        digester.addRule(VALIDATOR_PREFIX, new ElementNotAllowedRule());
        digester.addRule(TAG_PREFIX, new ElementNotAllowedRule());
        digester.addRule(TAGFILE_PREFIX, new ElementNotAllowedRule());
        digester.addRule(FUNCTION_PREFIX, new ElementNotAllowedRule());
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/tld/ImplicitTldRuleSet$ElementNotAllowedRule.class */
    private static class ElementNotAllowedRule extends Rule {
        private ElementNotAllowedRule() {
        }

        @Override // org.apache.tomcat.util.digester.Rule
        public void begin(String namespace, String name, Attributes attributes) throws Exception {
            throw new IllegalArgumentException(ImplicitTldRuleSet.sm.getString("implicitTldRule.elementNotAllowed", name));
        }
    }
}