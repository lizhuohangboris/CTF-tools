package org.apache.tomcat.util.descriptor.tld;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.jsp.tagext.FunctionInfo;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/tld/TaglibXml.class */
public class TaglibXml {
    private String tlibVersion;
    private String jspVersion;
    private String shortName;
    private String uri;
    private String info;
    private ValidatorXml validator;
    private final List<TagXml> tags = new ArrayList();
    private final List<TagFileXml> tagFiles = new ArrayList();
    private final List<String> listeners = new ArrayList();
    private final List<FunctionInfo> functions = new ArrayList();

    public String getTlibVersion() {
        return this.tlibVersion;
    }

    public void setTlibVersion(String tlibVersion) {
        this.tlibVersion = tlibVersion;
    }

    public String getJspVersion() {
        return this.jspVersion;
    }

    public void setJspVersion(String jspVersion) {
        this.jspVersion = jspVersion;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public ValidatorXml getValidator() {
        return this.validator;
    }

    public void setValidator(ValidatorXml validator) {
        this.validator = validator;
    }

    public void addTag(TagXml tag) {
        this.tags.add(tag);
    }

    public List<TagXml> getTags() {
        return this.tags;
    }

    public void addTagFile(TagFileXml tag) {
        this.tagFiles.add(tag);
    }

    public List<TagFileXml> getTagFiles() {
        return this.tagFiles;
    }

    public void addListener(String listener) {
        this.listeners.add(listener);
    }

    public List<String> getListeners() {
        return this.listeners;
    }

    public void addFunction(String name, String klass, String signature) {
        this.functions.add(new FunctionInfo(name, klass, signature));
    }

    public List<FunctionInfo> getFunctions() {
        return this.functions;
    }
}