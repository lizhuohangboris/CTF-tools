package org.attoparser.dom;

import java.io.Serializable;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/dom/DocType.class */
public class DocType extends AbstractNode implements Serializable {
    private static final long serialVersionUID = 763084654353190744L;
    private String rootElementName;
    private String publicId;
    private String systemId;
    private String internalSubset;

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

    public DocType(String rootElementName, String publicId, String systemId, String internalSubset) {
        if (rootElementName == null) {
            throw new IllegalArgumentException("Root element name cannot be null");
        }
        this.rootElementName = rootElementName;
        this.publicId = publicId;
        this.systemId = systemId;
        this.internalSubset = internalSubset;
    }

    public String getRootElementName() {
        return this.rootElementName;
    }

    public void setRootElementName(String rootElementName) {
        if (rootElementName == null) {
            throw new IllegalArgumentException("Root element name cannot be null");
        }
        this.rootElementName = rootElementName;
    }

    public String getPublicId() {
        return this.publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getInternalSubset() {
        return this.internalSubset;
    }

    public void setInternalSubset(String internalSubset) {
        this.internalSubset = internalSubset;
    }

    @Override // org.attoparser.dom.INode
    public DocType cloneNode(INestableNode parent) {
        DocType docType = new DocType(this.rootElementName, this.publicId, this.systemId, this.internalSubset);
        docType.setLine(getLine());
        docType.setCol(getCol());
        docType.setParent(parent);
        return docType;
    }
}