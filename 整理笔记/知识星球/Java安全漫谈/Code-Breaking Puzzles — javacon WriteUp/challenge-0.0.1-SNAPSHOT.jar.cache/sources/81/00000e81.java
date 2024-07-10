package org.attoparser.dom;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/dom/Element.class */
public class Element extends AbstractNestableNode implements Serializable {
    private static final long serialVersionUID = -8980986739486971174L;
    private String elementName;
    private Map<String, String> attributes = null;
    private int attributesLen = 0;

    @Override // org.attoparser.dom.AbstractNestableNode, org.attoparser.dom.INestableNode
    public /* bridge */ /* synthetic */ void addChild(INode iNode) {
        super.addChild(iNode);
    }

    @Override // org.attoparser.dom.AbstractNestableNode, org.attoparser.dom.INestableNode
    public /* bridge */ /* synthetic */ INode getFirstChildOfType(Class cls) {
        return super.getFirstChildOfType(cls);
    }

    @Override // org.attoparser.dom.AbstractNestableNode, org.attoparser.dom.INestableNode
    public /* bridge */ /* synthetic */ INode getFirstChild() {
        return super.getFirstChild();
    }

    @Override // org.attoparser.dom.AbstractNestableNode, org.attoparser.dom.INestableNode
    public /* bridge */ /* synthetic */ List getChildrenOfType(Class cls) {
        return super.getChildrenOfType(cls);
    }

    @Override // org.attoparser.dom.AbstractNestableNode, org.attoparser.dom.INestableNode
    public /* bridge */ /* synthetic */ List getChildren() {
        return super.getChildren();
    }

    @Override // org.attoparser.dom.AbstractNestableNode, org.attoparser.dom.INestableNode
    public /* bridge */ /* synthetic */ int numChildren() {
        return super.numChildren();
    }

    @Override // org.attoparser.dom.AbstractNestableNode, org.attoparser.dom.INestableNode
    public /* bridge */ /* synthetic */ boolean hasChildren() {
        return super.hasChildren();
    }

    @Override // org.attoparser.dom.AbstractNode, org.attoparser.dom.INode
    public /* bridge */ /* synthetic */ void setParent(INestableNode iNestableNode) {
        super.setParent(iNestableNode);
    }

    @Override // org.attoparser.dom.AbstractNode, org.attoparser.dom.INode
    public /* bridge */ /* synthetic */ INestableNode getParent() {
        return super.getParent();
    }

    @Override // org.attoparser.dom.AbstractNode, org.attoparser.dom.INode
    public /* bridge */ /* synthetic */ boolean hasParent() {
        return super.hasParent();
    }

    @Override // org.attoparser.dom.AbstractNode, org.attoparser.dom.INode
    public /* bridge */ /* synthetic */ void setCol(Integer num) {
        super.setCol(num);
    }

    @Override // org.attoparser.dom.AbstractNode, org.attoparser.dom.INode
    public /* bridge */ /* synthetic */ Integer getCol() {
        return super.getCol();
    }

    @Override // org.attoparser.dom.AbstractNode, org.attoparser.dom.INode
    public /* bridge */ /* synthetic */ boolean hasCol() {
        return super.hasCol();
    }

    @Override // org.attoparser.dom.AbstractNode, org.attoparser.dom.INode
    public /* bridge */ /* synthetic */ void setLine(Integer num) {
        super.setLine(num);
    }

    @Override // org.attoparser.dom.AbstractNode, org.attoparser.dom.INode
    public /* bridge */ /* synthetic */ Integer getLine() {
        return super.getLine();
    }

    @Override // org.attoparser.dom.AbstractNode, org.attoparser.dom.INode
    public /* bridge */ /* synthetic */ boolean hasLine() {
        return super.hasLine();
    }

    public Element(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Element name cannot be null");
        }
        this.elementName = name;
    }

    public String getElementName() {
        return this.elementName;
    }

    public void setElementName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Element name cannot be null");
        }
        this.elementName = name;
    }

    public boolean elementNameMatches(String name) {
        return this.elementName.equals(name);
    }

    public int numAttributes() {
        return this.attributesLen;
    }

    public boolean hasAttributes() {
        return this.attributesLen != 0;
    }

    public boolean hasAttribute(String attributeName) {
        if (this.attributesLen > 0) {
            return this.attributes.containsKey(attributeName);
        }
        return false;
    }

    public String getAttributeValue(String attributeName) {
        if (this.attributesLen > 0) {
            return this.attributes.get(attributeName);
        }
        return null;
    }

    public Map<String, String> getAttributeMap() {
        if (this.attributesLen > 0) {
            return Collections.unmodifiableMap(this.attributes);
        }
        return Collections.emptyMap();
    }

    public void addAttribute(String attributeName, String attributeValue) {
        if (this.attributesLen == 0) {
            this.attributes = new LinkedHashMap();
        }
        this.attributes.put(attributeName, attributeValue);
        this.attributesLen++;
    }

    public void addAttributes(Map<String, String> newAttributes) {
        if (newAttributes != null) {
            if (this.attributesLen == 0) {
                this.attributes = new LinkedHashMap();
            }
            this.attributes.putAll(newAttributes);
            this.attributesLen += newAttributes.size();
        }
    }

    public void removeAttribute(String attributeName) {
        if (this.attributesLen > 0 && this.attributes.containsKey(attributeName)) {
            this.attributes.remove(attributeName);
            this.attributesLen--;
            if (this.attributesLen == 0) {
                this.attributes = null;
            }
        }
    }

    public void clearAttributes() {
        this.attributes = null;
        this.attributesLen = 0;
    }

    @Override // org.attoparser.dom.INode
    public Element cloneNode(INestableNode parent) {
        Element element = new Element(this.elementName);
        element.addAttributes(this.attributes);
        for (INode child : getChildren()) {
            INode clonedChild = child.cloneNode(element);
            element.addChild(clonedChild);
        }
        element.setLine(getLine());
        element.setCol(getCol());
        element.setParent(parent);
        return element;
    }
}