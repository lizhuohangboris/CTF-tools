package org.attoparser.dom;

import java.io.Serializable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/dom/Text.class */
public class Text extends AbstractNode implements Serializable {
    private static final long serialVersionUID = -6449838157196892217L;
    private String content;

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

    public Text(String content) {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        this.content = content;
    }

    public void setContent(char[] buffer, int offset, int len) {
        this.content = new String(buffer, offset, len);
    }

    @Override // org.attoparser.dom.INode
    public Text cloneNode(INestableNode parent) {
        Text text = new Text(this.content);
        text.setLine(getLine());
        text.setCol(getCol());
        text.setParent(parent);
        return text;
    }
}