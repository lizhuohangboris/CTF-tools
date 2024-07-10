package ch.qos.logback.core.joran.spi;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.OptionHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/spi/SimpleRuleStore.class */
public class SimpleRuleStore extends ContextAwareBase implements RuleStore {
    static String KLEENE_STAR = "*";
    HashMap<ElementSelector, List<Action>> rules = new HashMap<>();

    public SimpleRuleStore(Context context) {
        setContext(context);
    }

    @Override // ch.qos.logback.core.joran.spi.RuleStore
    public void addRule(ElementSelector elementSelector, Action action) {
        action.setContext(this.context);
        List<Action> a4p = this.rules.get(elementSelector);
        if (a4p == null) {
            a4p = new ArrayList<>();
            this.rules.put(elementSelector, a4p);
        }
        a4p.add(action);
    }

    @Override // ch.qos.logback.core.joran.spi.RuleStore
    public void addRule(ElementSelector elementSelector, String actionClassName) {
        Action action = null;
        try {
            action = (Action) OptionHelper.instantiateByClassName(actionClassName, Action.class, this.context);
        } catch (Exception e) {
            addError("Could not instantiate class [" + actionClassName + "]", e);
        }
        if (action != null) {
            addRule(elementSelector, action);
        }
    }

    @Override // ch.qos.logback.core.joran.spi.RuleStore
    public List<Action> matchActions(ElementPath elementPath) {
        List<Action> actionList = fullPathMatch(elementPath);
        if (actionList != null) {
            return actionList;
        }
        List<Action> actionList2 = suffixMatch(elementPath);
        if (actionList2 != null) {
            return actionList2;
        }
        List<Action> actionList3 = prefixMatch(elementPath);
        if (actionList3 != null) {
            return actionList3;
        }
        List<Action> actionList4 = middleMatch(elementPath);
        if (actionList4 != null) {
            return actionList4;
        }
        return null;
    }

    List<Action> fullPathMatch(ElementPath elementPath) {
        for (ElementSelector selector : this.rules.keySet()) {
            if (selector.fullPathMatch(elementPath)) {
                return this.rules.get(selector);
            }
        }
        return null;
    }

    List<Action> suffixMatch(ElementPath elementPath) {
        int r;
        int max = 0;
        ElementSelector longestMatchingElementSelector = null;
        for (ElementSelector selector : this.rules.keySet()) {
            if (isSuffixPattern(selector) && (r = selector.getTailMatchLength(elementPath)) > max) {
                max = r;
                longestMatchingElementSelector = selector;
            }
        }
        if (longestMatchingElementSelector != null) {
            return this.rules.get(longestMatchingElementSelector);
        }
        return null;
    }

    private boolean isSuffixPattern(ElementSelector p) {
        return p.size() > 1 && p.get(0).equals(KLEENE_STAR);
    }

    List<Action> prefixMatch(ElementPath elementPath) {
        int r;
        int max = 0;
        ElementSelector longestMatchingElementSelector = null;
        for (ElementSelector selector : this.rules.keySet()) {
            String last = selector.peekLast();
            if (isKleeneStar(last) && (r = selector.getPrefixMatchLength(elementPath)) == selector.size() - 1 && r > max) {
                max = r;
                longestMatchingElementSelector = selector;
            }
        }
        if (longestMatchingElementSelector != null) {
            return this.rules.get(longestMatchingElementSelector);
        }
        return null;
    }

    private boolean isKleeneStar(String last) {
        return KLEENE_STAR.equals(last);
    }

    List<Action> middleMatch(ElementPath path) {
        int max = 0;
        ElementSelector longestMatchingElementSelector = null;
        for (ElementSelector selector : this.rules.keySet()) {
            String last = selector.peekLast();
            String first = null;
            if (selector.size() > 1) {
                first = selector.get(0);
            }
            if (isKleeneStar(last) && isKleeneStar(first)) {
                List<String> copyOfPartList = selector.getCopyOfPartList();
                if (copyOfPartList.size() > 2) {
                    copyOfPartList.remove(0);
                    copyOfPartList.remove(copyOfPartList.size() - 1);
                }
                int r = 0;
                ElementSelector clone = new ElementSelector(copyOfPartList);
                if (clone.isContainedIn(path)) {
                    r = clone.size();
                }
                if (r > max) {
                    max = r;
                    longestMatchingElementSelector = selector;
                }
            }
        }
        if (longestMatchingElementSelector != null) {
            return this.rules.get(longestMatchingElementSelector);
        }
        return null;
    }

    public String toString() {
        StringBuilder retValue = new StringBuilder();
        retValue.append("SimpleRuleStore ( ").append("rules = ").append(this.rules).append("  ").append(" )");
        return retValue.toString();
    }
}