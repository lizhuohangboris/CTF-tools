package org.attoparser.dom;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/dom/AbstractNode.class */
abstract class AbstractNode implements INode {
    private Integer line = null;
    private Integer col = null;
    private INestableNode parent;

    @Override // org.attoparser.dom.INode
    public boolean hasLine() {
        return this.line != null;
    }

    @Override // org.attoparser.dom.INode
    public Integer getLine() {
        return this.line;
    }

    @Override // org.attoparser.dom.INode
    public void setLine(Integer line) {
        this.line = line;
    }

    @Override // org.attoparser.dom.INode
    public boolean hasCol() {
        return this.col != null;
    }

    @Override // org.attoparser.dom.INode
    public Integer getCol() {
        return this.col;
    }

    @Override // org.attoparser.dom.INode
    public void setCol(Integer col) {
        this.col = col;
    }

    @Override // org.attoparser.dom.INode
    public boolean hasParent() {
        return this.parent != null;
    }

    @Override // org.attoparser.dom.INode
    public INestableNode getParent() {
        return this.parent;
    }

    @Override // org.attoparser.dom.INode
    public void setParent(INestableNode parent) {
        this.parent = parent;
    }
}