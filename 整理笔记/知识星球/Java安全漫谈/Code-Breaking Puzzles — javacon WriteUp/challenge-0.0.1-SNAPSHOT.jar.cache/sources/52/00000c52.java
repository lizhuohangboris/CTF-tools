package org.apache.tomcat.util.descriptor.tld;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.jsp.tagext.TagAttributeInfo;
import javax.servlet.jsp.tagext.TagVariableInfo;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/tld/TagXml.class */
public class TagXml {
    private String name;
    private String tagClass;
    private String teiClass;
    private String displayName;
    private String smallIcon;
    private String largeIcon;
    private String info;
    private boolean dynamicAttributes;
    private String bodyContent = "JSP";
    private final List<TagAttributeInfo> attributes = new ArrayList();
    private final List<TagVariableInfo> variables = new ArrayList();

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTagClass() {
        return this.tagClass;
    }

    public void setTagClass(String tagClass) {
        this.tagClass = tagClass;
    }

    public String getTeiClass() {
        return this.teiClass;
    }

    public void setTeiClass(String teiClass) {
        this.teiClass = teiClass;
    }

    public String getBodyContent() {
        return this.bodyContent;
    }

    public void setBodyContent(String bodyContent) {
        this.bodyContent = bodyContent;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSmallIcon() {
        return this.smallIcon;
    }

    public void setSmallIcon(String smallIcon) {
        this.smallIcon = smallIcon;
    }

    public String getLargeIcon() {
        return this.largeIcon;
    }

    public void setLargeIcon(String largeIcon) {
        this.largeIcon = largeIcon;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean hasDynamicAttributes() {
        return this.dynamicAttributes;
    }

    public void setDynamicAttributes(boolean dynamicAttributes) {
        this.dynamicAttributes = dynamicAttributes;
    }

    public List<TagAttributeInfo> getAttributes() {
        return this.attributes;
    }

    public List<TagVariableInfo> getVariables() {
        return this.variables;
    }
}