package org.apache.catalina.ssi;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/ExpressionParseTree.class */
public class ExpressionParseTree {
    private final LinkedList<Node> nodeStack = new LinkedList<>();
    private final LinkedList<OppNode> oppStack = new LinkedList<>();
    private Node root;
    private final SSIMediator ssiMediator;
    private static final int PRECEDENCE_NOT = 5;
    private static final int PRECEDENCE_COMPARE = 4;
    private static final int PRECEDENCE_LOGICAL = 1;

    public ExpressionParseTree(String expr, SSIMediator ssiMediator) throws ParseException {
        this.ssiMediator = ssiMediator;
        parseExpression(expr);
    }

    public boolean evaluateTree() {
        return this.root.evaluate();
    }

    private void pushOpp(OppNode node) {
        OppNode top;
        if (node == null) {
            this.oppStack.add(0, node);
            return;
        }
        while (this.oppStack.size() != 0 && (top = this.oppStack.get(0)) != null && top.getPrecedence() >= node.getPrecedence()) {
            this.oppStack.remove(0);
            top.popValues(this.nodeStack);
            this.nodeStack.add(0, top);
        }
        this.oppStack.add(0, node);
    }

    private void resolveGroup() {
        while (true) {
            OppNode top = this.oppStack.remove(0);
            if (top != null) {
                top.popValues(this.nodeStack);
                this.nodeStack.add(0, top);
            } else {
                return;
            }
        }
    }

    private void parseExpression(String expr) throws ParseException {
        StringNode currStringNode = null;
        pushOpp(null);
        ExpressionTokenizer et = new ExpressionTokenizer(expr);
        while (et.hasMoreTokens()) {
            int token = et.nextToken();
            if (token != 0) {
                currStringNode = null;
            }
            switch (token) {
                case 0:
                    if (currStringNode == null) {
                        currStringNode = new StringNode(et.getTokenValue());
                        this.nodeStack.add(0, currStringNode);
                        break;
                    } else {
                        currStringNode.value.append(" ");
                        currStringNode.value.append(et.getTokenValue());
                        break;
                    }
                case 1:
                    pushOpp(new AndNode());
                    break;
                case 2:
                    pushOpp(new OrNode());
                    break;
                case 3:
                    pushOpp(new NotNode());
                    break;
                case 4:
                    pushOpp(new EqualNode());
                    break;
                case 5:
                    pushOpp(new NotNode());
                    this.oppStack.add(0, new EqualNode());
                    break;
                case 6:
                    resolveGroup();
                    break;
                case 7:
                    pushOpp(null);
                    break;
                case 8:
                    pushOpp(new NotNode());
                    this.oppStack.add(0, new LessThanNode());
                    break;
                case 9:
                    pushOpp(new NotNode());
                    this.oppStack.add(0, new GreaterThanNode());
                    break;
                case 10:
                    pushOpp(new GreaterThanNode());
                    break;
                case 11:
                    pushOpp(new LessThanNode());
                    break;
            }
        }
        resolveGroup();
        if (this.nodeStack.size() == 0) {
            throw new ParseException("No nodes created.", et.getIndex());
        }
        if (this.nodeStack.size() > 1) {
            throw new ParseException("Extra nodes created.", et.getIndex());
        }
        if (this.oppStack.size() != 0) {
            throw new ParseException("Unused opp nodes exist.", et.getIndex());
        }
        this.root = this.nodeStack.get(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/ExpressionParseTree$Node.class */
    public abstract class Node {
        public abstract boolean evaluate();

        private Node() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/ExpressionParseTree$StringNode.class */
    public class StringNode extends Node {
        StringBuilder value;
        String resolved;

        public StringNode(String value) {
            super();
            this.resolved = null;
            this.value = new StringBuilder(value);
        }

        public String getValue() {
            if (this.resolved == null) {
                this.resolved = ExpressionParseTree.this.ssiMediator.substituteVariables(this.value.toString());
            }
            return this.resolved;
        }

        @Override // org.apache.catalina.ssi.ExpressionParseTree.Node
        public boolean evaluate() {
            return getValue().length() != 0;
        }

        public String toString() {
            return this.value.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/ExpressionParseTree$OppNode.class */
    public abstract class OppNode extends Node {
        Node left;
        Node right;

        public abstract int getPrecedence();

        private OppNode() {
            super();
        }

        public void popValues(List<Node> values) {
            this.right = values.remove(0);
            this.left = values.remove(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/ExpressionParseTree$NotNode.class */
    public final class NotNode extends OppNode {
        private NotNode() {
            super();
        }

        @Override // org.apache.catalina.ssi.ExpressionParseTree.Node
        public boolean evaluate() {
            return !this.left.evaluate();
        }

        @Override // org.apache.catalina.ssi.ExpressionParseTree.OppNode
        public int getPrecedence() {
            return 5;
        }

        @Override // org.apache.catalina.ssi.ExpressionParseTree.OppNode
        public void popValues(List<Node> values) {
            this.left = values.remove(0);
        }

        public String toString() {
            return this.left + " NOT";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/ExpressionParseTree$AndNode.class */
    public final class AndNode extends OppNode {
        private AndNode() {
            super();
        }

        @Override // org.apache.catalina.ssi.ExpressionParseTree.Node
        public boolean evaluate() {
            if (!this.left.evaluate()) {
                return false;
            }
            return this.right.evaluate();
        }

        @Override // org.apache.catalina.ssi.ExpressionParseTree.OppNode
        public int getPrecedence() {
            return 1;
        }

        public String toString() {
            return this.left + " " + this.right + " AND";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/ExpressionParseTree$OrNode.class */
    public final class OrNode extends OppNode {
        private OrNode() {
            super();
        }

        @Override // org.apache.catalina.ssi.ExpressionParseTree.Node
        public boolean evaluate() {
            if (this.left.evaluate()) {
                return true;
            }
            return this.right.evaluate();
        }

        @Override // org.apache.catalina.ssi.ExpressionParseTree.OppNode
        public int getPrecedence() {
            return 1;
        }

        public String toString() {
            return this.left + " " + this.right + " OR";
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/ExpressionParseTree$CompareNode.class */
    private abstract class CompareNode extends OppNode {
        private CompareNode() {
            super();
        }

        protected int compareBranches() {
            String val1 = ((StringNode) this.left).getValue();
            String val2 = ((StringNode) this.right).getValue();
            int val2Len = val2.length();
            if (val2Len > 1 && val2.charAt(0) == '/' && val2.charAt(val2Len - 1) == '/') {
                String expr = val2.substring(1, val2Len - 1);
                ExpressionParseTree.this.ssiMediator.clearMatchGroups();
                try {
                    Pattern pattern = Pattern.compile(expr);
                    Matcher matcher = pattern.matcher(val1);
                    if (matcher.find()) {
                        ExpressionParseTree.this.ssiMediator.populateMatchGroups(matcher);
                        return 0;
                    }
                    return -1;
                } catch (PatternSyntaxException pse) {
                    ExpressionParseTree.this.ssiMediator.log("Invalid expression: " + expr, pse);
                    return 0;
                }
            }
            return val1.compareTo(val2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/ExpressionParseTree$EqualNode.class */
    public final class EqualNode extends CompareNode {
        private EqualNode() {
            super();
        }

        @Override // org.apache.catalina.ssi.ExpressionParseTree.Node
        public boolean evaluate() {
            return compareBranches() == 0;
        }

        @Override // org.apache.catalina.ssi.ExpressionParseTree.OppNode
        public int getPrecedence() {
            return 4;
        }

        public String toString() {
            return this.left + " " + this.right + " EQ";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/ExpressionParseTree$GreaterThanNode.class */
    public final class GreaterThanNode extends CompareNode {
        private GreaterThanNode() {
            super();
        }

        @Override // org.apache.catalina.ssi.ExpressionParseTree.Node
        public boolean evaluate() {
            return compareBranches() > 0;
        }

        @Override // org.apache.catalina.ssi.ExpressionParseTree.OppNode
        public int getPrecedence() {
            return 4;
        }

        public String toString() {
            return this.left + " " + this.right + " GT";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/ExpressionParseTree$LessThanNode.class */
    public final class LessThanNode extends CompareNode {
        private LessThanNode() {
            super();
        }

        @Override // org.apache.catalina.ssi.ExpressionParseTree.Node
        public boolean evaluate() {
            return compareBranches() < 0;
        }

        @Override // org.apache.catalina.ssi.ExpressionParseTree.OppNode
        public int getPrecedence() {
            return 4;
        }

        public String toString() {
            return this.left + " " + this.right + " LT";
        }
    }
}