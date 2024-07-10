package org.springframework.util.xml;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import org.apache.commons.logging.Log;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/xml/SimpleTransformErrorListener.class */
public class SimpleTransformErrorListener implements ErrorListener {
    private final Log logger;

    public SimpleTransformErrorListener(Log logger) {
        this.logger = logger;
    }

    @Override // javax.xml.transform.ErrorListener
    public void warning(TransformerException ex) throws TransformerException {
        this.logger.warn("XSLT transformation warning", ex);
    }

    @Override // javax.xml.transform.ErrorListener
    public void error(TransformerException ex) throws TransformerException {
        this.logger.error("XSLT transformation error", ex);
    }

    @Override // javax.xml.transform.ErrorListener
    public void fatalError(TransformerException ex) throws TransformerException {
        throw ex;
    }
}