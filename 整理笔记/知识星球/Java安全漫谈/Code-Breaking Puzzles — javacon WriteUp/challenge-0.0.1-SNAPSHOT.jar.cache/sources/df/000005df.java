package javax.el;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-el-9.0.12.jar:javax/el/EvaluationListener.class */
public abstract class EvaluationListener {
    public void beforeEvaluation(ELContext context, String expression) {
    }

    public void afterEvaluation(ELContext context, String expression) {
    }

    public void propertyResolved(ELContext context, Object base, Object property) {
    }
}