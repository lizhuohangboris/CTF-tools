package org.apache.el.parser;

import java.util.Arrays;
import javax.el.ELException;
import javax.el.MethodInfo;
import javax.el.PropertyNotWritableException;
import javax.el.ValueReference;
import org.apache.el.lang.ELSupport;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.util.MessageFactory;
import org.springframework.beans.PropertyAccessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/SimpleNode.class */
public abstract class SimpleNode extends ELSupport implements Node {
    protected Node parent;
    protected Node[] children;
    protected final int id;
    protected String image;

    public SimpleNode(int i) {
        this.id = i;
    }

    @Override // org.apache.el.parser.Node
    public void jjtOpen() {
    }

    @Override // org.apache.el.parser.Node
    public void jjtClose() {
    }

    @Override // org.apache.el.parser.Node
    public void jjtSetParent(Node n) {
        this.parent = n;
    }

    @Override // org.apache.el.parser.Node
    public Node jjtGetParent() {
        return this.parent;
    }

    @Override // org.apache.el.parser.Node
    public void jjtAddChild(Node n, int i) {
        if (this.children == null) {
            this.children = new Node[i + 1];
        } else if (i >= this.children.length) {
            Node[] c = new Node[i + 1];
            System.arraycopy(this.children, 0, c, 0, this.children.length);
            this.children = c;
        }
        this.children[i] = n;
    }

    @Override // org.apache.el.parser.Node
    public Node jjtGetChild(int i) {
        return this.children[i];
    }

    @Override // org.apache.el.parser.Node
    public int jjtGetNumChildren() {
        if (this.children == null) {
            return 0;
        }
        return this.children.length;
    }

    public String toString() {
        if (this.image != null) {
            return ELParserTreeConstants.jjtNodeName[this.id] + PropertyAccessor.PROPERTY_KEY_PREFIX + this.image + "]";
        }
        return ELParserTreeConstants.jjtNodeName[this.id];
    }

    @Override // org.apache.el.parser.Node
    public String getImage() {
        return this.image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Class<?> getType(EvaluationContext ctx) throws ELException {
        throw new UnsupportedOperationException();
    }

    public Object getValue(EvaluationContext ctx) throws ELException {
        throw new UnsupportedOperationException();
    }

    public boolean isReadOnly(EvaluationContext ctx) throws ELException {
        return true;
    }

    public void setValue(EvaluationContext ctx, Object value) throws ELException {
        throw new PropertyNotWritableException(MessageFactory.get("error.syntax.set"));
    }

    @Override // org.apache.el.parser.Node
    public void accept(NodeVisitor visitor) throws Exception {
        visitor.visit(this);
        if (this.children != null && this.children.length > 0) {
            for (int i = 0; i < this.children.length; i++) {
                this.children[i].accept(visitor);
            }
        }
    }

    public Object invoke(EvaluationContext ctx, Class<?>[] paramTypes, Object[] paramValues) throws ELException {
        throw new UnsupportedOperationException();
    }

    public MethodInfo getMethodInfo(EvaluationContext ctx, Class<?>[] paramTypes) throws ELException {
        throw new UnsupportedOperationException();
    }

    public int hashCode() {
        int result = (31 * 1) + Arrays.hashCode(this.children);
        return (31 * ((31 * result) + this.id)) + (this.image == null ? 0 : this.image.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SimpleNode)) {
            return false;
        }
        SimpleNode other = (SimpleNode) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.image == null) {
            if (other.image != null) {
                return false;
            }
        } else if (!this.image.equals(other.image)) {
            return false;
        }
        if (!Arrays.equals(this.children, other.children)) {
            return false;
        }
        return true;
    }

    public ValueReference getValueReference(EvaluationContext ctx) {
        return null;
    }

    public boolean isParametersProvided() {
        return false;
    }
}