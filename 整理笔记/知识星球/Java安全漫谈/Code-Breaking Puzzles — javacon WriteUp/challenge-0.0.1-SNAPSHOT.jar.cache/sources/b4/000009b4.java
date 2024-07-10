package org.apache.catalina.valves.rewrite;

import ch.qos.logback.core.net.ssl.SSL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import org.apache.catalina.util.URLEncoder;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/rewrite/Substitution.class */
public class Substitution {
    protected SubstitutionElement[] elements = null;
    protected String sub = null;
    private boolean escapeBackReferences;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/rewrite/Substitution$SubstitutionElement.class */
    public abstract class SubstitutionElement {
        public abstract String evaluate(Matcher matcher, Matcher matcher2, Resolver resolver);

        public SubstitutionElement() {
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/rewrite/Substitution$StaticElement.class */
    public class StaticElement extends SubstitutionElement {
        public String value;

        public StaticElement() {
            super();
        }

        @Override // org.apache.catalina.valves.rewrite.Substitution.SubstitutionElement
        public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
            return this.value;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/rewrite/Substitution$RewriteRuleBackReferenceElement.class */
    public class RewriteRuleBackReferenceElement extends SubstitutionElement {
        public int n;

        public RewriteRuleBackReferenceElement() {
            super();
        }

        @Override // org.apache.catalina.valves.rewrite.Substitution.SubstitutionElement
        public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
            String result = rule.group(this.n);
            if (result == null) {
                result = "";
            }
            if (Substitution.this.escapeBackReferences) {
                return URLEncoder.DEFAULT.encode(result, resolver.getUriCharset());
            }
            return result;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/rewrite/Substitution$RewriteCondBackReferenceElement.class */
    public class RewriteCondBackReferenceElement extends SubstitutionElement {
        public int n;

        public RewriteCondBackReferenceElement() {
            super();
        }

        @Override // org.apache.catalina.valves.rewrite.Substitution.SubstitutionElement
        public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
            return cond.group(this.n) == null ? "" : cond.group(this.n);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/rewrite/Substitution$ServerVariableElement.class */
    public class ServerVariableElement extends SubstitutionElement {
        public String key;

        public ServerVariableElement() {
            super();
        }

        @Override // org.apache.catalina.valves.rewrite.Substitution.SubstitutionElement
        public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
            return resolver.resolve(this.key);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/rewrite/Substitution$ServerVariableEnvElement.class */
    public class ServerVariableEnvElement extends SubstitutionElement {
        public String key;

        public ServerVariableEnvElement() {
            super();
        }

        @Override // org.apache.catalina.valves.rewrite.Substitution.SubstitutionElement
        public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
            return resolver.resolveEnv(this.key);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/rewrite/Substitution$ServerVariableSslElement.class */
    public class ServerVariableSslElement extends SubstitutionElement {
        public String key;

        public ServerVariableSslElement() {
            super();
        }

        @Override // org.apache.catalina.valves.rewrite.Substitution.SubstitutionElement
        public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
            return resolver.resolveSsl(this.key);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/rewrite/Substitution$ServerVariableHttpElement.class */
    public class ServerVariableHttpElement extends SubstitutionElement {
        public String key;

        public ServerVariableHttpElement() {
            super();
        }

        @Override // org.apache.catalina.valves.rewrite.Substitution.SubstitutionElement
        public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
            return resolver.resolveHttp(this.key);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/valves/rewrite/Substitution$MapElement.class */
    public class MapElement extends SubstitutionElement {
        public RewriteMap map;
        public SubstitutionElement[] defaultValue;
        public SubstitutionElement[] key;

        public MapElement() {
            super();
            this.map = null;
            this.defaultValue = null;
            this.key = null;
        }

        @Override // org.apache.catalina.valves.rewrite.Substitution.SubstitutionElement
        public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
            String result = this.map.lookup(Substitution.this.evaluateSubstitution(this.key, rule, cond, resolver));
            if (result == null && this.defaultValue != null) {
                result = Substitution.this.evaluateSubstitution(this.defaultValue, rule, cond, resolver);
            }
            return result;
        }
    }

    public String getSub() {
        return this.sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setEscapeBackReferences(boolean escapeBackReferences) {
        this.escapeBackReferences = escapeBackReferences;
    }

    public void parse(Map<String, RewriteMap> maps) {
        this.elements = parseSubtitution(this.sub, maps);
    }

    private SubstitutionElement[] parseSubtitution(String sub, Map<String, RewriteMap> maps) {
        String key;
        SubstitutionElement newElement;
        List<SubstitutionElement> elements = new ArrayList<>();
        int pos = 0;
        while (pos < sub.length()) {
            int percentPos = sub.indexOf(37, pos);
            int dollarPos = sub.indexOf(36, pos);
            int backslashPos = sub.indexOf(92, pos);
            if (percentPos == -1 && dollarPos == -1 && backslashPos == -1) {
                StaticElement newElement2 = new StaticElement();
                newElement2.value = sub.substring(pos, sub.length());
                pos = sub.length();
                elements.add(newElement2);
            } else if (isFirstPos(backslashPos, dollarPos, percentPos)) {
                if (backslashPos + 1 == sub.length()) {
                    throw new IllegalArgumentException(sub);
                }
                StaticElement newElement3 = new StaticElement();
                newElement3.value = sub.substring(pos, backslashPos) + sub.substring(backslashPos + 1, backslashPos + 2);
                pos = backslashPos + 2;
                elements.add(newElement3);
            } else if (isFirstPos(dollarPos, percentPos)) {
                if (dollarPos + 1 == sub.length()) {
                    throw new IllegalArgumentException(sub);
                }
                if (pos < dollarPos) {
                    StaticElement newElement4 = new StaticElement();
                    newElement4.value = sub.substring(pos, dollarPos);
                    elements.add(newElement4);
                }
                if (Character.isDigit(sub.charAt(dollarPos + 1))) {
                    RewriteRuleBackReferenceElement newElement5 = new RewriteRuleBackReferenceElement();
                    newElement5.n = Character.digit(sub.charAt(dollarPos + 1), 10);
                    pos = dollarPos + 2;
                    elements.add(newElement5);
                } else if (sub.charAt(dollarPos + 1) == '{') {
                    MapElement newElement6 = new MapElement();
                    int open = sub.indexOf(123, dollarPos);
                    int colon = sub.indexOf(58, dollarPos);
                    int def = sub.indexOf(124, dollarPos);
                    int close = sub.indexOf(125, dollarPos);
                    if (-1 >= open || open >= colon || colon >= close) {
                        throw new IllegalArgumentException(sub);
                    }
                    newElement6.map = maps.get(sub.substring(open + 1, colon));
                    if (newElement6.map == null) {
                        throw new IllegalArgumentException(sub + ": No map: " + sub.substring(open + 1, colon));
                    }
                    String defaultValue = null;
                    if (def > -1) {
                        if (colon >= def || def >= close) {
                            throw new IllegalArgumentException(sub);
                        }
                        key = sub.substring(colon + 1, def);
                        defaultValue = sub.substring(def + 1, close);
                    } else {
                        key = sub.substring(colon + 1, close);
                    }
                    newElement6.key = parseSubtitution(key, maps);
                    if (defaultValue != null) {
                        newElement6.defaultValue = parseSubtitution(defaultValue, maps);
                    }
                    pos = close + 1;
                    elements.add(newElement6);
                } else {
                    throw new IllegalArgumentException(sub + ": missing digit or curly brace.");
                }
            } else if (percentPos + 1 == sub.length()) {
                throw new IllegalArgumentException(sub);
            } else {
                if (pos < percentPos) {
                    StaticElement newElement7 = new StaticElement();
                    newElement7.value = sub.substring(pos, percentPos);
                    elements.add(newElement7);
                }
                if (Character.isDigit(sub.charAt(percentPos + 1))) {
                    RewriteCondBackReferenceElement newElement8 = new RewriteCondBackReferenceElement();
                    newElement8.n = Character.digit(sub.charAt(percentPos + 1), 10);
                    pos = percentPos + 2;
                    elements.add(newElement8);
                } else if (sub.charAt(percentPos + 1) == '{') {
                    int open2 = sub.indexOf(123, percentPos);
                    int colon2 = sub.indexOf(58, percentPos);
                    int close2 = sub.indexOf(125, percentPos);
                    if (-1 >= open2 || open2 >= close2) {
                        throw new IllegalArgumentException(sub);
                    }
                    if (colon2 > -1 && open2 < colon2 && colon2 < close2) {
                        String type = sub.substring(open2 + 1, colon2);
                        if (type.equals("ENV")) {
                            newElement = new ServerVariableEnvElement();
                            ((ServerVariableEnvElement) newElement).key = sub.substring(colon2 + 1, close2);
                        } else if (type.equals(SSL.DEFAULT_PROTOCOL)) {
                            newElement = new ServerVariableSslElement();
                            ((ServerVariableSslElement) newElement).key = sub.substring(colon2 + 1, close2);
                        } else if (type.equals("HTTP")) {
                            newElement = new ServerVariableHttpElement();
                            ((ServerVariableHttpElement) newElement).key = sub.substring(colon2 + 1, close2);
                        } else {
                            throw new IllegalArgumentException(sub + ": Bad type: " + type);
                        }
                    } else {
                        newElement = new ServerVariableElement();
                        ((ServerVariableElement) newElement).key = sub.substring(open2 + 1, close2);
                    }
                    pos = close2 + 1;
                    elements.add(newElement);
                } else {
                    throw new IllegalArgumentException(sub + ": missing digit or curly brace.");
                }
            }
        }
        return (SubstitutionElement[]) elements.toArray(new SubstitutionElement[0]);
    }

    public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
        return evaluateSubstitution(this.elements, rule, cond, resolver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String evaluateSubstitution(SubstitutionElement[] elements, Matcher rule, Matcher cond, Resolver resolver) {
        StringBuffer buf = new StringBuffer();
        for (SubstitutionElement substitutionElement : elements) {
            buf.append(substitutionElement.evaluate(rule, cond, resolver));
        }
        return buf.toString();
    }

    private boolean isFirstPos(int testPos, int... others) {
        if (testPos < 0) {
            return false;
        }
        for (int other : others) {
            if (other >= 0 && other < testPos) {
                return false;
            }
        }
        return true;
    }
}