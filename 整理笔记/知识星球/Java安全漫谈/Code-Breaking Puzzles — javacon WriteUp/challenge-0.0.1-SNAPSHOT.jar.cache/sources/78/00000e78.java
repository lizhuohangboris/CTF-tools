package org.attoparser.dom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/attoparser-2.0.5.RELEASE.jar:org/attoparser/dom/AbstractNestableNode.class */
public abstract class AbstractNestableNode extends AbstractNode implements INestableNode {
    private List<INode> children = null;
    private int childrenLen = 0;

    @Override // org.attoparser.dom.INestableNode
    public boolean hasChildren() {
        return this.childrenLen != 0;
    }

    @Override // org.attoparser.dom.INestableNode
    public int numChildren() {
        return this.childrenLen;
    }

    @Override // org.attoparser.dom.INestableNode
    public List<INode> getChildren() {
        if (this.childrenLen == 0) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(this.children);
    }

    @Override // org.attoparser.dom.INestableNode
    public <T extends INode> List<T> getChildrenOfType(Class<T> type) {
        if (this.childrenLen == 0) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList(5);
        for (INode child : this.children) {
            if (type.isInstance(child)) {
                arrayList.add(child);
            }
        }
        return Collections.unmodifiableList(arrayList);
    }

    @Override // org.attoparser.dom.INestableNode
    public INode getFirstChild() {
        if (this.childrenLen == 0) {
            return null;
        }
        return this.children.get(0);
    }

    @Override // org.attoparser.dom.INestableNode
    public <T extends INode> T getFirstChildOfType(Class<T> type) {
        if (this.childrenLen == 0) {
            return null;
        }
        Iterator<INode> it = this.children.iterator();
        while (it.hasNext()) {
            T t = (T) it.next();
            if (type.isInstance(t)) {
                return t;
            }
        }
        return null;
    }

    @Override // org.attoparser.dom.INestableNode
    public void addChild(INode newChild) {
        if (newChild != null) {
            if (this.childrenLen == 0) {
                this.children = new ArrayList(5);
            }
            this.children.add(newChild);
            this.childrenLen++;
            newChild.setParent(this);
        }
    }

    @Override // org.attoparser.dom.INestableNode
    public final void insertChild(int index, INode newChild) {
        if (newChild != null) {
            if (this.childrenLen == 0) {
                this.children = new ArrayList(5);
            }
            if (index <= this.childrenLen) {
                this.children.add(index, newChild);
                this.childrenLen++;
                newChild.setParent(this);
            }
        }
    }

    @Override // org.attoparser.dom.INestableNode
    public final void insertChildBefore(INode before, INode newChild) {
        if (newChild != null && this.childrenLen > 0) {
            for (int i = 0; i < this.childrenLen; i++) {
                INode currentChild = this.children.get(i);
                if (currentChild == before) {
                    insertChild(i, newChild);
                    return;
                }
            }
        }
    }

    @Override // org.attoparser.dom.INestableNode
    public final void insertChildAfter(INode after, INode newChild) {
        if (newChild != null && this.childrenLen > 0) {
            for (int i = 0; i < this.childrenLen; i++) {
                INode currentChild = this.children.get(i);
                if (currentChild == after) {
                    insertChild(i + 1, newChild);
                    return;
                }
            }
        }
    }

    @Override // org.attoparser.dom.INestableNode
    public final void removeChild(INode child) {
        if (child != null && child.getParent() == this) {
            Iterator<INode> childrenIter = this.children.iterator();
            while (true) {
                if (!childrenIter.hasNext()) {
                    break;
                }
                INode nodeChild = childrenIter.next();
                if (nodeChild == child) {
                    childrenIter.remove();
                    this.childrenLen--;
                    break;
                }
            }
            if (this.childrenLen == 0) {
                this.children = null;
            }
        }
    }

    @Override // org.attoparser.dom.INestableNode
    public final void clearChildren() {
        this.children = null;
        this.childrenLen = 0;
    }
}