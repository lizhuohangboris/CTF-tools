package org.apache.tomcat.util.descriptor.tld;

import java.lang.reflect.Method;
import javax.servlet.jsp.tagext.TagAttributeInfo;
import javax.servlet.jsp.tagext.TagVariableInfo;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.Rule;
import org.apache.tomcat.util.digester.RuleSet;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.thymeleaf.engine.XMLDeclaration;
import org.xml.sax.Attributes;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/tld/TldRuleSet.class */
public class TldRuleSet implements RuleSet {
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
        digester.addRule(PREFIX, new Rule() { // from class: org.apache.tomcat.util.descriptor.tld.TldRuleSet.1
            @Override // org.apache.tomcat.util.digester.Rule
            public void begin(String namespace, String name, Attributes attributes) {
                TaglibXml taglibXml = (TaglibXml) this.digester.peek();
                taglibXml.setJspVersion(attributes.getValue(XMLDeclaration.ATTRIBUTE_NAME_VERSION));
            }
        });
        digester.addCallMethod("taglib/shortname", "setShortName", 0);
        digester.addCallMethod("taglib/short-name", "setShortName", 0);
        digester.addCallMethod("taglib/uri", "setUri", 0);
        digester.addCallMethod("taglib/info", "setInfo", 0);
        digester.addCallMethod("taglib/description", "setInfo", 0);
        digester.addCallMethod("taglib/listener/listener-class", "addListener", 0);
        digester.addObjectCreate(VALIDATOR_PREFIX, ValidatorXml.class.getName());
        digester.addCallMethod("taglib/validator/validator-class", "setValidatorClass", 0);
        digester.addCallMethod("taglib/validator/init-param", "addInitParam", 2);
        digester.addCallParam("taglib/validator/init-param/param-name", 0);
        digester.addCallParam("taglib/validator/init-param/param-value", 1);
        digester.addSetNext(VALIDATOR_PREFIX, "setValidator", ValidatorXml.class.getName());
        digester.addObjectCreate(TAG_PREFIX, TagXml.class.getName());
        addDescriptionGroup(digester, TAG_PREFIX);
        digester.addCallMethod("taglib/tag/name", "setName", 0);
        digester.addCallMethod("taglib/tag/tagclass", "setTagClass", 0);
        digester.addCallMethod("taglib/tag/tag-class", "setTagClass", 0);
        digester.addCallMethod("taglib/tag/teiclass", "setTeiClass", 0);
        digester.addCallMethod("taglib/tag/tei-class", "setTeiClass", 0);
        digester.addCallMethod("taglib/tag/bodycontent", "setBodyContent", 0);
        digester.addCallMethod("taglib/tag/body-content", "setBodyContent", 0);
        digester.addRule("taglib/tag/variable", new ScriptVariableRule());
        digester.addCallMethod("taglib/tag/variable/name-given", "setNameGiven", 0);
        digester.addCallMethod("taglib/tag/variable/name-from-attribute", "setNameFromAttribute", 0);
        digester.addCallMethod("taglib/tag/variable/variable-class", "setClassName", 0);
        digester.addRule("taglib/tag/variable/declare", new GenericBooleanRule(Variable.class, "setDeclare"));
        digester.addCallMethod("taglib/tag/variable/scope", "setScope", 0);
        digester.addRule("taglib/tag/attribute", new TagAttributeRule());
        digester.addCallMethod("taglib/tag/attribute/description", "setDescription", 0);
        digester.addCallMethod("taglib/tag/attribute/name", "setName", 0);
        digester.addRule("taglib/tag/attribute/required", new GenericBooleanRule(Attribute.class, "setRequired"));
        digester.addRule("taglib/tag/attribute/rtexprvalue", new GenericBooleanRule(Attribute.class, "setRequestTime"));
        digester.addCallMethod("taglib/tag/attribute/type", "setType", 0);
        digester.addCallMethod("taglib/tag/attribute/deferred-value", "setDeferredValue");
        digester.addCallMethod("taglib/tag/attribute/deferred-value/type", "setExpectedTypeName", 0);
        digester.addCallMethod("taglib/tag/attribute/deferred-method", "setDeferredMethod");
        digester.addCallMethod("taglib/tag/attribute/deferred-method/method-signature", "setMethodSignature", 0);
        digester.addRule("taglib/tag/attribute/fragment", new GenericBooleanRule(Attribute.class, "setFragment"));
        digester.addRule("taglib/tag/dynamic-attributes", new GenericBooleanRule(TagXml.class, "setDynamicAttributes"));
        digester.addSetNext(TAG_PREFIX, "addTag", TagXml.class.getName());
        digester.addObjectCreate(TAGFILE_PREFIX, TagFileXml.class.getName());
        addDescriptionGroup(digester, TAGFILE_PREFIX);
        digester.addCallMethod("taglib/tag-file/name", "setName", 0);
        digester.addCallMethod("taglib/tag-file/path", "setPath", 0);
        digester.addSetNext(TAGFILE_PREFIX, "addTagFile", TagFileXml.class.getName());
        digester.addCallMethod(FUNCTION_PREFIX, "addFunction", 3);
        digester.addCallParam("taglib/function/name", 0);
        digester.addCallParam("taglib/function/function-class", 1);
        digester.addCallParam("taglib/function/function-signature", 2);
    }

    private void addDescriptionGroup(Digester digester, String prefix) {
        digester.addCallMethod(prefix + "/info", "setInfo", 0);
        digester.addCallMethod(prefix + "small-icon", "setSmallIcon", 0);
        digester.addCallMethod(prefix + "large-icon", "setLargeIcon", 0);
        digester.addCallMethod(prefix + "/description", "setInfo", 0);
        digester.addCallMethod(prefix + "/display-name", "setDisplayName", 0);
        digester.addCallMethod(prefix + "/icon/small-icon", "setSmallIcon", 0);
        digester.addCallMethod(prefix + "/icon/large-icon", "setLargeIcon", 0);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/tld/TldRuleSet$TagAttributeRule.class */
    private static class TagAttributeRule extends Rule {
        private TagAttributeRule() {
        }

        @Override // org.apache.tomcat.util.digester.Rule
        public void begin(String namespace, String name, Attributes attributes) throws Exception {
            TaglibXml taglibXml = (TaglibXml) this.digester.peek(this.digester.getCount() - 1);
            this.digester.push(new Attribute("1.2".equals(taglibXml.getJspVersion())));
        }

        @Override // org.apache.tomcat.util.digester.Rule
        public void end(String namespace, String name) throws Exception {
            Attribute attribute = (Attribute) this.digester.pop();
            TagXml tag = (TagXml) this.digester.peek();
            tag.getAttributes().add(attribute.toTagAttributeInfo());
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/tld/TldRuleSet$Attribute.class */
    public static class Attribute {
        private final boolean allowShortNames;
        private String name;
        private boolean required;
        private String type;
        private boolean requestTime;
        private boolean fragment;
        private String description;
        private boolean deferredValue;
        private boolean deferredMethod;
        private String expectedTypeName;
        private String methodSignature;

        private Attribute(boolean allowShortNames) {
            this.allowShortNames = allowShortNames;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public void setType(String type) {
            if (this.allowShortNames) {
                boolean z = true;
                switch (type.hashCode()) {
                    case -1939501217:
                        if (type.equals("Object")) {
                            z = true;
                            break;
                        }
                        break;
                    case -1808118735:
                        if (type.equals("String")) {
                            z = true;
                            break;
                        }
                        break;
                    case -726803703:
                        if (type.equals("Character")) {
                            z = true;
                            break;
                        }
                        break;
                    case -672261858:
                        if (type.equals("Integer")) {
                            z = true;
                            break;
                        }
                        break;
                    case 2086184:
                        if (type.equals("Byte")) {
                            z = true;
                            break;
                        }
                        break;
                    case 2374300:
                        if (type.equals("Long")) {
                            z = true;
                            break;
                        }
                        break;
                    case 67973692:
                        if (type.equals("Float")) {
                            z = true;
                            break;
                        }
                        break;
                    case 79860828:
                        if (type.equals("Short")) {
                            z = true;
                            break;
                        }
                        break;
                    case 1729365000:
                        if (type.equals("Boolean")) {
                            z = false;
                            break;
                        }
                        break;
                    case 2052876273:
                        if (type.equals("Double")) {
                            z = true;
                            break;
                        }
                        break;
                }
                switch (z) {
                    case false:
                        this.type = "java.lang.Boolean";
                        return;
                    case true:
                        this.type = "java.lang.Character";
                        return;
                    case true:
                        this.type = "java.lang.Byte";
                        return;
                    case true:
                        this.type = "java.lang.Short";
                        return;
                    case true:
                        this.type = "java.lang.Integer";
                        return;
                    case true:
                        this.type = "java.lang.Long";
                        return;
                    case true:
                        this.type = "java.lang.Float";
                        return;
                    case true:
                        this.type = "java.lang.Double";
                        return;
                    case true:
                        this.type = "java.lang.String";
                        return;
                    case true:
                        this.type = "java.lang.Object";
                        return;
                    default:
                        this.type = type;
                        return;
                }
            }
            this.type = type;
        }

        public void setRequestTime(boolean requestTime) {
            this.requestTime = requestTime;
        }

        public void setFragment(boolean fragment) {
            this.fragment = fragment;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setDeferredValue() {
            this.deferredValue = true;
        }

        public void setDeferredMethod() {
            this.deferredMethod = true;
        }

        public void setExpectedTypeName(String expectedTypeName) {
            this.expectedTypeName = expectedTypeName;
        }

        public void setMethodSignature(String methodSignature) {
            this.methodSignature = methodSignature;
        }

        public TagAttributeInfo toTagAttributeInfo() {
            if (this.fragment) {
                this.type = "javax.servlet.jsp.tagext.JspFragment";
                this.requestTime = true;
            } else if (this.deferredValue) {
                this.type = "javax.el.ValueExpression";
                if (this.expectedTypeName == null) {
                    this.expectedTypeName = "java.lang.Object";
                }
            } else if (this.deferredMethod) {
                this.type = "javax.el.MethodExpression";
                if (this.methodSignature == null) {
                    this.methodSignature = "java.lang.Object method()";
                }
            }
            if (!this.requestTime && this.type == null) {
                this.type = "java.lang.String";
            }
            return new TagAttributeInfo(this.name, this.required, this.type, this.requestTime, this.fragment, this.description, this.deferredValue, this.deferredMethod, this.expectedTypeName, this.methodSignature);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/tld/TldRuleSet$ScriptVariableRule.class */
    private static class ScriptVariableRule extends Rule {
        private ScriptVariableRule() {
        }

        @Override // org.apache.tomcat.util.digester.Rule
        public void begin(String namespace, String name, Attributes attributes) throws Exception {
            this.digester.push(new Variable());
        }

        @Override // org.apache.tomcat.util.digester.Rule
        public void end(String namespace, String name) throws Exception {
            Variable variable = (Variable) this.digester.pop();
            TagXml tag = (TagXml) this.digester.peek();
            tag.getVariables().add(variable.toTagVariableInfo());
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/tld/TldRuleSet$Variable.class */
    public static class Variable {
        private String nameGiven;
        private String nameFromAttribute;
        private String className = "java.lang.String";
        private boolean declare = true;
        private int scope = 0;

        public void setNameGiven(String nameGiven) {
            this.nameGiven = nameGiven;
        }

        public void setNameFromAttribute(String nameFromAttribute) {
            this.nameFromAttribute = nameFromAttribute;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public void setDeclare(boolean declare) {
            this.declare = declare;
        }

        public void setScope(String scopeName) {
            boolean z = true;
            switch (scopeName.hashCode()) {
                case -1995614985:
                    if (scopeName.equals("NESTED")) {
                        z = false;
                        break;
                    }
                    break;
                case 1637267837:
                    if (scopeName.equals("AT_BEGIN")) {
                        z = true;
                        break;
                    }
                    break;
                case 1941369519:
                    if (scopeName.equals("AT_END")) {
                        z = true;
                        break;
                    }
                    break;
            }
            switch (z) {
                case false:
                    this.scope = 0;
                    return;
                case true:
                    this.scope = 1;
                    return;
                case true:
                    this.scope = 2;
                    return;
                default:
                    return;
            }
        }

        public TagVariableInfo toTagVariableInfo() {
            return new TagVariableInfo(this.nameGiven, this.nameFromAttribute, this.className, this.declare, this.scope);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/tld/TldRuleSet$GenericBooleanRule.class */
    private static class GenericBooleanRule extends Rule {
        private final Method setter;

        private GenericBooleanRule(Class<?> type, String setterName) {
            try {
                this.setter = type.getMethod(setterName, Boolean.TYPE);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override // org.apache.tomcat.util.digester.Rule
        public void body(String namespace, String name, String text) throws Exception {
            if (null != text) {
                text = text.trim();
            }
            boolean value = "true".equalsIgnoreCase(text) || CustomBooleanEditor.VALUE_YES.equalsIgnoreCase(text);
            this.setter.invoke(this.digester.peek(), Boolean.valueOf(value));
        }
    }
}