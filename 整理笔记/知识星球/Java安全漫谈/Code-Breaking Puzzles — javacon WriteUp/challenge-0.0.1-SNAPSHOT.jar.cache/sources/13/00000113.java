package ch.qos.logback.core.joran.event;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.joran.spi.ElementPath;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.ContextAwareImpl;
import ch.qos.logback.core.status.Status;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/joran/event/SaxEventRecorder.class */
public class SaxEventRecorder extends DefaultHandler implements ContextAware {
    final ContextAwareImpl cai;
    Locator locator;
    public List<SaxEvent> saxEventList = new ArrayList();
    ElementPath globalElementPath = new ElementPath();

    public SaxEventRecorder(Context context) {
        this.cai = new ContextAwareImpl(context, this);
    }

    public final void recordEvents(InputStream inputStream) throws JoranException {
        recordEvents(new InputSource(inputStream));
    }

    public List<SaxEvent> recordEvents(InputSource inputSource) throws JoranException {
        SAXParser saxParser = buildSaxParser();
        try {
            saxParser.parse(inputSource, this);
            return this.saxEventList;
        } catch (IOException ie) {
            handleError("I/O error occurred while parsing xml file", ie);
            throw new IllegalStateException("This point can never be reached");
        } catch (SAXException se) {
            throw new JoranException("Problem parsing XML document. See previously reported errors.", se);
        } catch (Exception ex) {
            handleError("Unexpected exception while parsing XML document.", ex);
            throw new IllegalStateException("This point can never be reached");
        }
    }

    private void handleError(String errMsg, Throwable t) throws JoranException {
        addError(errMsg, t);
        throw new JoranException(errMsg, t);
    }

    private SAXParser buildSaxParser() throws JoranException {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setValidating(false);
            spf.setNamespaceAware(true);
            return spf.newSAXParser();
        } catch (Exception pce) {
            addError("Parser configuration error occurred", pce);
            throw new JoranException("Parser configuration error occurred", pce);
        }
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startDocument() {
    }

    public Locator getLocator() {
        return this.locator;
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void setDocumentLocator(Locator l) {
        this.locator = l;
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
        String tagName = getTagName(localName, qName);
        this.globalElementPath.push(tagName);
        ElementPath current = this.globalElementPath.duplicate();
        this.saxEventList.add(new StartEvent(current, namespaceURI, localName, qName, atts, getLocator()));
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void characters(char[] ch2, int start, int length) {
        String bodyStr = new String(ch2, start, length);
        SaxEvent lastEvent = getLastEvent();
        if (lastEvent instanceof BodyEvent) {
            BodyEvent be = (BodyEvent) lastEvent;
            be.append(bodyStr);
        } else if (!isSpaceOnly(bodyStr)) {
            this.saxEventList.add(new BodyEvent(bodyStr, getLocator()));
        }
    }

    boolean isSpaceOnly(String bodyStr) {
        String bodyTrimmed = bodyStr.trim();
        return bodyTrimmed.length() == 0;
    }

    SaxEvent getLastEvent() {
        if (this.saxEventList.isEmpty()) {
            return null;
        }
        int size = this.saxEventList.size();
        return this.saxEventList.get(size - 1);
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
    public void endElement(String namespaceURI, String localName, String qName) {
        this.saxEventList.add(new EndEvent(namespaceURI, localName, qName, getLocator()));
        this.globalElementPath.pop();
    }

    String getTagName(String localName, String qName) {
        String tagName = localName;
        if (tagName == null || tagName.length() < 1) {
            tagName = qName;
        }
        return tagName;
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ErrorHandler
    public void error(SAXParseException spe) throws SAXException {
        addError("XML_PARSING - Parsing error on line " + spe.getLineNumber() + " and column " + spe.getColumnNumber());
        addError(spe.toString());
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ErrorHandler
    public void fatalError(SAXParseException spe) throws SAXException {
        addError("XML_PARSING - Parsing fatal error on line " + spe.getLineNumber() + " and column " + spe.getColumnNumber());
        addError(spe.toString());
    }

    @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ErrorHandler
    public void warning(SAXParseException spe) throws SAXException {
        addWarn("XML_PARSING - Parsing warning on line " + spe.getLineNumber() + " and column " + spe.getColumnNumber(), spe);
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addError(String msg) {
        this.cai.addError(msg);
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addError(String msg, Throwable ex) {
        this.cai.addError(msg, ex);
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addInfo(String msg) {
        this.cai.addInfo(msg);
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addInfo(String msg, Throwable ex) {
        this.cai.addInfo(msg, ex);
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addStatus(Status status) {
        this.cai.addStatus(status);
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addWarn(String msg) {
        this.cai.addWarn(msg);
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void addWarn(String msg, Throwable ex) {
        this.cai.addWarn(msg, ex);
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public Context getContext() {
        return this.cai.getContext();
    }

    @Override // ch.qos.logback.core.spi.ContextAware
    public void setContext(Context context) {
        this.cai.setContext(context);
    }

    public List<SaxEvent> getSaxEventList() {
        return this.saxEventList;
    }
}