package org.attoparser.dom;

import java.io.Serializable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/dom/XmlDeclaration.class */
public class XmlDeclaration extends AbstractNode implements Serializable {
    private static final long serialVersionUID = 8210232665354213283L;
    private String version;
    private String encoding;
    private String standalone;

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

    public XmlDeclaration(String version, String encoding, String standalone) {
        if (version == null) {
            throw new IllegalArgumentException("Version cannot be null");
        }
        this.version = version;
        this.encoding = encoding;
        this.standalone = standalone;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        if (version == null) {
            throw new IllegalArgumentException("Version cannot be null");
        }
        this.version = version;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getStandalone() {
        return this.standalone;
    }

    public void setStandalone(String standalone) {
        this.standalone = standalone;
    }

    @Override // org.attoparser.dom.INode
    public XmlDeclaration cloneNode(INestableNode parent) {
        XmlDeclaration xmlDeclaration = new XmlDeclaration(this.version, this.encoding, this.standalone);
        xmlDeclaration.setLine(getLine());
        xmlDeclaration.setCol(getCol());
        xmlDeclaration.setParent(parent);
        return xmlDeclaration;
    }
}