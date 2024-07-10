package org.apache.el.parser;

import javax.el.ELException;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.util.MessageFactory;
import org.apache.el.util.Validation;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:org/apache/el/parser/AstDotSuffix.class */
public final class AstDotSuffix extends SimpleNode {
    public AstDotSuffix(int id) {
        super(id);
    }

    @Override // org.apache.el.parser.SimpleNode, org.apache.el.parser.Node
    public Object getValue(EvaluationContext ctx) throws ELException {
        return this.image;
    }

    @Override // org.apache.el.parser.SimpleNode
    public void setImage(String image) {
        if (!Validation.isIdentifier(image)) {
            throw new ELException(MessageFactory.get("error.identifier.notjava", image));
        }
        this.image = image;
    }
}