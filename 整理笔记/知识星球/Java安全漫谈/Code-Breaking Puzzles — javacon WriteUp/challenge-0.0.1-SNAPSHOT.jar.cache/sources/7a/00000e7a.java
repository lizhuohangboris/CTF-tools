package org.attoparser.dom;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/dom/CDATASection.class */
public class CDATASection extends Text {
    private static final long serialVersionUID = -131121996532074777L;

    public CDATASection(String content) {
        super(content);
    }

    @Override // org.attoparser.dom.Text, org.attoparser.dom.INode
    public CDATASection cloneNode(INestableNode parent) {
        CDATASection cdataSection = new CDATASection(getContent());
        cdataSection.setLine(getLine());
        cdataSection.setCol(getCol());
        cdataSection.setParent(parent);
        return cdataSection;
    }
}