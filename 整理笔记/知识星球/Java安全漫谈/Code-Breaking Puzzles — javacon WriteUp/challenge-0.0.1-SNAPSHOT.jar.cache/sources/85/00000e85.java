package org.attoparser.dom;

import java.io.Serializable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/dom/ProcessingInstruction.class */
public class ProcessingInstruction extends AbstractNode implements Serializable {
    private static final long serialVersionUID = 7832638382597687056L;
    private String target;
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

    public ProcessingInstruction(String target, String content) {
        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null");
        }
        this.target = target;
        this.content = content;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null");
        }
        this.target = target;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override // org.attoparser.dom.INode
    public ProcessingInstruction cloneNode(INestableNode parent) {
        ProcessingInstruction processingInstruction = new ProcessingInstruction(this.target, this.content);
        processingInstruction.setLine(getLine());
        processingInstruction.setCol(getCol());
        processingInstruction.setParent(parent);
        return processingInstruction;
    }
}