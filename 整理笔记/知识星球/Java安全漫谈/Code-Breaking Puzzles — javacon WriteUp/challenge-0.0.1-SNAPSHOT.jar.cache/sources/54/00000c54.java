package org.apache.tomcat.util.descriptor.tld;

import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.descriptor.Constants;
import org.apache.tomcat.util.descriptor.DigesterFactory;
import org.apache.tomcat.util.descriptor.XmlErrorHandler;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.RuleSet;
import org.apache.tomcat.util.security.PrivilegedGetTccl;
import org.apache.tomcat.util.security.PrivilegedSetTccl;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/tld/TldParser.class */
public class TldParser {
    private final Log log;
    private final Digester digester;

    public TldParser(boolean namespaceAware, boolean validation, boolean blockExternal) {
        this(namespaceAware, validation, new TldRuleSet(), blockExternal);
    }

    public TldParser(boolean namespaceAware, boolean validation, RuleSet ruleSet, boolean blockExternal) {
        this.log = LogFactory.getLog(TldParser.class);
        this.digester = DigesterFactory.newDigester(validation, namespaceAware, ruleSet, blockExternal);
    }

    public TaglibXml parse(TldResourcePath path) throws IOException, SAXException {
        ClassLoader original;
        if (Constants.IS_SECURITY_ENABLED) {
            PrivilegedGetTccl pa = new PrivilegedGetTccl();
            original = (ClassLoader) AccessController.doPrivileged(pa);
        } else {
            original = Thread.currentThread().getContextClassLoader();
        }
        try {
            InputStream is = path.openStream();
            if (Constants.IS_SECURITY_ENABLED) {
                PrivilegedSetTccl pa2 = new PrivilegedSetTccl(TldParser.class.getClassLoader());
                AccessController.doPrivileged(pa2);
            } else {
                Thread.currentThread().setContextClassLoader(TldParser.class.getClassLoader());
            }
            XmlErrorHandler handler = new XmlErrorHandler();
            this.digester.setErrorHandler(handler);
            TaglibXml taglibXml = new TaglibXml();
            this.digester.push(taglibXml);
            InputSource source = new InputSource(path.toExternalForm());
            source.setByteStream(is);
            this.digester.parse(source);
            if (!handler.getWarnings().isEmpty() || !handler.getErrors().isEmpty()) {
                handler.logFindings(this.log, source.getSystemId());
                if (!handler.getErrors().isEmpty()) {
                    throw handler.getErrors().iterator().next();
                }
            }
            if (is != null) {
                if (0 != 0) {
                    is.close();
                } else {
                    is.close();
                }
            }
            return taglibXml;
        } finally {
            this.digester.reset();
            if (Constants.IS_SECURITY_ENABLED) {
                PrivilegedSetTccl pa3 = new PrivilegedSetTccl(original);
                AccessController.doPrivileged(pa3);
            } else {
                Thread.currentThread().setContextClassLoader(original);
            }
        }
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.digester.setClassLoader(classLoader);
    }
}