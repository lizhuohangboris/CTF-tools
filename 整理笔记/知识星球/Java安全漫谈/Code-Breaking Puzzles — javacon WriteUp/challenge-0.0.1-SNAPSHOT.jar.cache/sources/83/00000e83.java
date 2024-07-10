package org.attoparser.dom;

import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/dom/INestableNode.class */
public interface INestableNode extends INode {
    boolean hasChildren();

    int numChildren();

    List<INode> getChildren();

    <T extends INode> List<T> getChildrenOfType(Class<T> cls);

    INode getFirstChild();

    <T extends INode> T getFirstChildOfType(Class<T> cls);

    void addChild(INode iNode);

    void insertChild(int i, INode iNode);

    void insertChildBefore(INode iNode, INode iNode2);

    void insertChildAfter(INode iNode, INode iNode2);

    void removeChild(INode iNode);

    void clearChildren();
}