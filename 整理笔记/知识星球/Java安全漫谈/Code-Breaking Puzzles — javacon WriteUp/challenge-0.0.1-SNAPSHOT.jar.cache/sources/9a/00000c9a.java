package org.apache.tomcat.util.digester;

import org.apache.tomcat.util.IntrospectionUtils;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/digester/CallMethodRule.class */
public class CallMethodRule extends Rule {
    protected String bodyText;
    protected final int targetOffset;
    protected final String methodName;
    protected final int paramCount;
    protected Class<?>[] paramTypes;
    protected boolean useExactMatch;

    public CallMethodRule(String methodName, int paramCount) {
        this(0, methodName, paramCount);
    }

    public CallMethodRule(int targetOffset, String methodName, int paramCount) {
        this.bodyText = null;
        this.paramTypes = null;
        this.useExactMatch = false;
        this.targetOffset = targetOffset;
        this.methodName = methodName;
        this.paramCount = paramCount;
        if (paramCount == 0) {
            this.paramTypes = new Class[]{String.class};
            return;
        }
        this.paramTypes = new Class[paramCount];
        for (int i = 0; i < this.paramTypes.length; i++) {
            this.paramTypes[i] = String.class;
        }
    }

    public CallMethodRule(String methodName) {
        this(0, methodName, 0, null);
    }

    public CallMethodRule(int targetOffset, String methodName, int paramCount, Class<?>[] paramTypes) {
        this.bodyText = null;
        this.paramTypes = null;
        this.useExactMatch = false;
        this.targetOffset = targetOffset;
        this.methodName = methodName;
        this.paramCount = paramCount;
        if (paramTypes == null) {
            this.paramTypes = new Class[paramCount];
            for (int i = 0; i < this.paramTypes.length; i++) {
                this.paramTypes[i] = String.class;
            }
            return;
        }
        this.paramTypes = new Class[paramTypes.length];
        System.arraycopy(paramTypes, 0, this.paramTypes, 0, this.paramTypes.length);
    }

    public boolean getUseExactMatch() {
        return this.useExactMatch;
    }

    public void setUseExactMatch(boolean useExactMatch) {
        this.useExactMatch = useExactMatch;
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        if (this.paramCount > 0) {
            Object[] parameters = new Object[this.paramCount];
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = null;
            }
            this.digester.pushParams(parameters);
        }
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void body(String namespace, String name, String bodyText) throws Exception {
        if (this.paramCount == 0) {
            this.bodyText = bodyText.trim();
        }
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void end(String namespace, String name) throws Exception {
        Object target;
        Object[] parameters = null;
        if (this.paramCount > 0) {
            parameters = (Object[]) this.digester.popParams();
            if (this.digester.log.isTraceEnabled()) {
                int size = parameters.length;
                for (int i = 0; i < size; i++) {
                    this.digester.log.trace("[CallMethodRule](" + i + ")" + parameters[i]);
                }
            }
            if (this.paramCount == 1 && parameters[0] == null) {
                return;
            }
        } else if (this.paramTypes != null && this.paramTypes.length != 0) {
            if (this.bodyText == null) {
                return;
            }
            parameters = new Object[]{this.bodyText};
        }
        Object[] paramValues = new Object[this.paramTypes.length];
        for (int i2 = 0; i2 < this.paramTypes.length; i2++) {
            if (parameters[i2] == null || ((parameters[i2] instanceof String) && !String.class.isAssignableFrom(this.paramTypes[i2]))) {
                paramValues[i2] = IntrospectionUtils.convert((String) parameters[i2], this.paramTypes[i2]);
            } else {
                paramValues[i2] = parameters[i2];
            }
        }
        if (this.targetOffset >= 0) {
            target = this.digester.peek(this.targetOffset);
        } else {
            target = this.digester.peek(this.digester.getCount() + this.targetOffset);
        }
        if (target == null) {
            throw new SAXException("[CallMethodRule]{" + this.digester.match + "} Call target is null (targetOffset=" + this.targetOffset + ",stackdepth=" + this.digester.getCount() + ")");
        }
        if (this.digester.log.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder("[CallMethodRule]{");
            sb.append(this.digester.match);
            sb.append("} Call ");
            sb.append(target.getClass().getName());
            sb.append(".");
            sb.append(this.methodName);
            sb.append("(");
            for (int i3 = 0; i3 < paramValues.length; i3++) {
                if (i3 > 0) {
                    sb.append(",");
                }
                if (paramValues[i3] == null) {
                    sb.append(BeanDefinitionParserDelegate.NULL_ELEMENT);
                } else {
                    sb.append(paramValues[i3].toString());
                }
                sb.append("/");
                if (this.paramTypes[i3] == null) {
                    sb.append(BeanDefinitionParserDelegate.NULL_ELEMENT);
                } else {
                    sb.append(this.paramTypes[i3].getName());
                }
            }
            sb.append(")");
            this.digester.log.debug(sb.toString());
        }
        Object result = IntrospectionUtils.callMethodN(target, this.methodName, paramValues, this.paramTypes);
        processMethodCallResult(result);
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void finish() throws Exception {
        this.bodyText = null;
    }

    protected void processMethodCallResult(Object result) {
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("CallMethodRule[");
        sb.append("methodName=");
        sb.append(this.methodName);
        sb.append(", paramCount=");
        sb.append(this.paramCount);
        sb.append(", paramTypes={");
        if (this.paramTypes != null) {
            for (int i = 0; i < this.paramTypes.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(this.paramTypes[i].getName());
            }
        }
        sb.append("}");
        sb.append("]");
        return sb.toString();
    }
}