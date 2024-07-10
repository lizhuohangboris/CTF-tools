package org.attoparser.dom;

import java.io.Serializable;
import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/dom/Document.class */
public class Document extends AbstractNestableNode implements Serializable {
    private static final long serialVersionUID = 1;
    private String documentName;

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

    public Document(String documentName) {
        this.documentName = null;
        this.documentName = documentName;
    }

    public String getDocumentName() {
        return this.documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    @Override // org.attoparser.dom.INode
    public Document cloneNode(INestableNode parent) {
        Document document = new Document(this.documentName);
        document.setLine(getLine());
        document.setCol(getCol());
        document.setParent(parent);
        return document;
    }
}