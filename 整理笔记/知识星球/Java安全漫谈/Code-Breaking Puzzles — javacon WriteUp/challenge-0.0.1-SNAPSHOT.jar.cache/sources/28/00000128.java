package ch.qos.logback.core.joran.spi;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.action.ImplicitAction;
import ch.qos.logback.core.joran.event.BodyEvent;
import ch.qos.logback.core.joran.event.EndEvent;
import ch.qos.logback.core.joran.event.StartEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/spi/Interpreter.class */
public class Interpreter {
    private static List<Action> EMPTY_LIST = new Vector(0);
    private final RuleStore ruleStore;
    private final InterpretationContext interpretationContext;
    private final CAI_WithLocatorSupport cai;
    private ElementPath elementPath;
    Locator locator;
    ElementPath skip = null;
    private final ArrayList<ImplicitAction> implicitActions = new ArrayList<>(3);
    Stack<List<Action>> actionListStack = new Stack<>();
    EventPlayer eventPlayer = new EventPlayer(this);

    public Interpreter(Context context, RuleStore rs, ElementPath initialElementPath) {
        this.cai = new CAI_WithLocatorSupport(context, this);
        this.ruleStore = rs;
        this.interpretationContext = new InterpretationContext(context, this);
        this.elementPath = initialElementPath;
    }

    public EventPlayer getEventPlayer() {
        return this.eventPlayer;
    }

    public void setInterpretationContextPropertiesMap(Map<String, String> propertiesMap) {
        this.interpretationContext.setPropertiesMap(propertiesMap);
    }

    public InterpretationContext getExecutionContext() {
        return getInterpretationContext();
    }

    public InterpretationContext getInterpretationContext() {
        return this.interpretationContext;
    }

    public void startDocument() {
    }

    public void startElement(StartEvent se) {
        setDocumentLocator(se.getLocator());
        startElement(se.namespaceURI, se.localName, se.qName, se.attributes);
    }

    private void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
        String tagName = getTagName(localName, qName);
        this.elementPath.push(tagName);
        if (this.skip != null) {
            pushEmptyActionList();
            return;
        }
        List<Action> applicableActionList = getApplicableActionList(this.elementPath, atts);
        if (applicableActionList != null) {
            this.actionListStack.add(applicableActionList);
            callBeginAction(applicableActionList, tagName, atts);
            return;
        }
        pushEmptyActionList();
        String errMsg = "no applicable action for [" + tagName + "], current ElementPath  is [" + this.elementPath + "]";
        this.cai.addError(errMsg);
    }

    private void pushEmptyActionList() {
        this.actionListStack.add(EMPTY_LIST);
    }

    public void characters(BodyEvent be) {
        setDocumentLocator(be.locator);
        String body = be.getText();
        List<Action> applicableActionList = this.actionListStack.peek();
        if (body != null) {
            String body2 = body.trim();
            if (body2.length() > 0) {
                callBodyAction(applicableActionList, body2);
            }
        }
    }

    public void endElement(EndEvent endEvent) {
        setDocumentLocator(endEvent.locator);
        endElement(endEvent.namespaceURI, endEvent.localName, endEvent.qName);
    }

    private void endElement(String namespaceURI, String localName, String qName) {
        List<Action> applicableActionList = this.actionListStack.pop();
        if (this.skip != null) {
            if (this.skip.equals(this.elementPath)) {
                this.skip = null;
            }
        } else if (applicableActionList != EMPTY_LIST) {
            callEndAction(applicableActionList, getTagName(localName, qName));
        }
        this.elementPath.pop();
    }

    public Locator getLocator() {
        return this.locator;
    }

    public void setDocumentLocator(Locator l) {
        this.locator = l;
    }

    String getTagName(String localName, String qName) {
        String tagName = localName;
        if (tagName == null || tagName.length() < 1) {
            tagName = qName;
        }
        return tagName;
    }

    public void addImplicitAction(ImplicitAction ia) {
        this.implicitActions.add(ia);
    }

    List<Action> lookupImplicitAction(ElementPath elementPath, Attributes attributes, InterpretationContext ec) {
        int len = this.implicitActions.size();
        for (int i = 0; i < len; i++) {
            ImplicitAction ia = this.implicitActions.get(i);
            if (ia.isApplicable(elementPath, attributes, ec)) {
                List<Action> actionList = new ArrayList<>(1);
                actionList.add(ia);
                return actionList;
            }
        }
        return null;
    }

    List<Action> getApplicableActionList(ElementPath elementPath, Attributes attributes) {
        List<Action> applicableActionList = this.ruleStore.matchActions(elementPath);
        if (applicableActionList == null) {
            applicableActionList = lookupImplicitAction(elementPath, attributes, this.interpretationContext);
        }
        return applicableActionList;
    }

    void callBeginAction(List<Action> applicableActionList, String tagName, Attributes atts) {
        if (applicableActionList == null) {
            return;
        }
        for (Action action : applicableActionList) {
            try {
                action.begin(this.interpretationContext, tagName, atts);
            } catch (ActionException e) {
                this.skip = this.elementPath.duplicate();
                this.cai.addError("ActionException in Action for tag [" + tagName + "]", e);
            } catch (RuntimeException e2) {
                this.skip = this.elementPath.duplicate();
                this.cai.addError("RuntimeException in Action for tag [" + tagName + "]", e2);
            }
        }
    }

    private void callBodyAction(List<Action> applicableActionList, String body) {
        if (applicableActionList == null) {
            return;
        }
        for (Action action : applicableActionList) {
            try {
                action.body(this.interpretationContext, body);
            } catch (ActionException ae) {
                this.cai.addError("Exception in end() methd for action [" + action + "]", ae);
            }
        }
    }

    private void callEndAction(List<Action> applicableActionList, String tagName) {
        if (applicableActionList == null) {
            return;
        }
        for (Action action : applicableActionList) {
            try {
                action.end(this.interpretationContext, tagName);
            } catch (ActionException ae) {
                this.cai.addError("ActionException in Action for tag [" + tagName + "]", ae);
            } catch (RuntimeException e) {
                this.cai.addError("RuntimeException in Action for tag [" + tagName + "]", e);
            }
        }
    }

    public RuleStore getRuleStore() {
        return this.ruleStore;
    }
}