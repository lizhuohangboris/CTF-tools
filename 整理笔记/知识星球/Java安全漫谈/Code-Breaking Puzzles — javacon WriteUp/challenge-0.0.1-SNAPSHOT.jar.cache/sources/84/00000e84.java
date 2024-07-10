package org.attoparser.dom;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/dom/INode.class */
public interface INode {
    boolean hasLine();

    Integer getLine();

    void setLine(Integer num);

    boolean hasCol();

    Integer getCol();

    void setCol(Integer num);

    boolean hasParent();

    INestableNode getParent();

    void setParent(INestableNode iNestableNode);

    INode cloneNode(INestableNode iNestableNode);
}