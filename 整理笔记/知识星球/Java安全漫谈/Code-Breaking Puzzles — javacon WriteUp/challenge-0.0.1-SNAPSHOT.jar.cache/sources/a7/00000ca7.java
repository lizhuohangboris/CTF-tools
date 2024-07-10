package org.apache.tomcat.util.digester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.springframework.util.ResourceUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/digester/RulesBase.class */
public class RulesBase implements Rules {
    protected HashMap<String, List<Rule>> cache = new HashMap<>();
    protected Digester digester = null;
    protected ArrayList<Rule> rules = new ArrayList<>();

    @Override // org.apache.tomcat.util.digester.Rules
    public Digester getDigester() {
        return this.digester;
    }

    @Override // org.apache.tomcat.util.digester.Rules
    public void setDigester(Digester digester) {
        this.digester = digester;
        Iterator<Rule> it = this.rules.iterator();
        while (it.hasNext()) {
            Rule item = it.next();
            item.setDigester(digester);
        }
    }

    @Override // org.apache.tomcat.util.digester.Rules
    public void add(String pattern, Rule rule) {
        int patternLength = pattern.length();
        if (patternLength > 1 && pattern.endsWith("/")) {
            pattern = pattern.substring(0, patternLength - 1);
        }
        List<Rule> list = this.cache.get(pattern);
        if (list == null) {
            list = new ArrayList<>();
            this.cache.put(pattern, list);
        }
        list.add(rule);
        this.rules.add(rule);
        if (this.digester != null) {
            rule.setDigester(this.digester);
        }
    }

    @Override // org.apache.tomcat.util.digester.Rules
    public void clear() {
        this.cache.clear();
        this.rules.clear();
    }

    @Override // org.apache.tomcat.util.digester.Rules
    public List<Rule> match(String namespaceURI, String pattern) {
        List<Rule> rulesList = lookup(namespaceURI, pattern);
        if (rulesList == null || rulesList.size() < 1) {
            String longKey = "";
            for (String key : this.cache.keySet()) {
                if (key.startsWith(ResourceUtils.WAR_URL_SEPARATOR) && (pattern.equals(key.substring(2)) || pattern.endsWith(key.substring(1)))) {
                    if (key.length() > longKey.length()) {
                        rulesList = lookup(namespaceURI, key);
                        longKey = key;
                    }
                }
            }
        }
        if (rulesList == null) {
            rulesList = new ArrayList<>();
        }
        return rulesList;
    }

    @Override // org.apache.tomcat.util.digester.Rules
    public List<Rule> rules() {
        return this.rules;
    }

    protected List<Rule> lookup(String namespaceURI, String pattern) {
        List<Rule> list = this.cache.get(pattern);
        if (list == null) {
            return null;
        }
        if (namespaceURI == null || namespaceURI.length() == 0) {
            return list;
        }
        List<Rule> results = new ArrayList<>();
        for (Rule item : list) {
            if (namespaceURI.equals(item.getNamespaceURI()) || item.getNamespaceURI() == null) {
                results.add(item);
            }
        }
        return results;
    }
}