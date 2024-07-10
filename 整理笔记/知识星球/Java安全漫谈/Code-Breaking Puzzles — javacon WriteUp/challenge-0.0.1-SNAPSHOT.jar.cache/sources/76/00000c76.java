package org.apache.tomcat.util.descriptor.web;

import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.descriptor.JspPropertyGroupDescriptor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/descriptor/web/JspPropertyGroupDescriptorImpl.class */
public class JspPropertyGroupDescriptorImpl implements JspPropertyGroupDescriptor {
    private final JspPropertyGroup jspPropertyGroup;

    public JspPropertyGroupDescriptorImpl(JspPropertyGroup jspPropertyGroup) {
        this.jspPropertyGroup = jspPropertyGroup;
    }

    @Override // javax.servlet.descriptor.JspPropertyGroupDescriptor
    public String getBuffer() {
        return this.jspPropertyGroup.getBuffer();
    }

    @Override // javax.servlet.descriptor.JspPropertyGroupDescriptor
    public String getDefaultContentType() {
        return this.jspPropertyGroup.getDefaultContentType();
    }

    @Override // javax.servlet.descriptor.JspPropertyGroupDescriptor
    public String getDeferredSyntaxAllowedAsLiteral() {
        String result = null;
        if (this.jspPropertyGroup.getDeferredSyntax() != null) {
            result = this.jspPropertyGroup.getDeferredSyntax().toString();
        }
        return result;
    }

    @Override // javax.servlet.descriptor.JspPropertyGroupDescriptor
    public String getElIgnored() {
        String result = null;
        if (this.jspPropertyGroup.getElIgnored() != null) {
            result = this.jspPropertyGroup.getElIgnored().toString();
        }
        return result;
    }

    @Override // javax.servlet.descriptor.JspPropertyGroupDescriptor
    public String getErrorOnUndeclaredNamespace() {
        String result = null;
        if (this.jspPropertyGroup.getErrorOnUndeclaredNamespace() != null) {
            result = this.jspPropertyGroup.getErrorOnUndeclaredNamespace().toString();
        }
        return result;
    }

    @Override // javax.servlet.descriptor.JspPropertyGroupDescriptor
    public Collection<String> getIncludeCodas() {
        return new ArrayList(this.jspPropertyGroup.getIncludeCodas());
    }

    @Override // javax.servlet.descriptor.JspPropertyGroupDescriptor
    public Collection<String> getIncludePreludes() {
        return new ArrayList(this.jspPropertyGroup.getIncludePreludes());
    }

    @Override // javax.servlet.descriptor.JspPropertyGroupDescriptor
    public String getIsXml() {
        String result = null;
        if (this.jspPropertyGroup.getIsXml() != null) {
            result = this.jspPropertyGroup.getIsXml().toString();
        }
        return result;
    }

    @Override // javax.servlet.descriptor.JspPropertyGroupDescriptor
    public String getPageEncoding() {
        return this.jspPropertyGroup.getPageEncoding();
    }

    @Override // javax.servlet.descriptor.JspPropertyGroupDescriptor
    public String getScriptingInvalid() {
        String result = null;
        if (this.jspPropertyGroup.getScriptingInvalid() != null) {
            result = this.jspPropertyGroup.getScriptingInvalid().toString();
        }
        return result;
    }

    @Override // javax.servlet.descriptor.JspPropertyGroupDescriptor
    public String getTrimDirectiveWhitespaces() {
        String result = null;
        if (this.jspPropertyGroup.getTrimWhitespace() != null) {
            result = this.jspPropertyGroup.getTrimWhitespace().toString();
        }
        return result;
    }

    @Override // javax.servlet.descriptor.JspPropertyGroupDescriptor
    public Collection<String> getUrlPatterns() {
        return new ArrayList(this.jspPropertyGroup.getUrlPatterns());
    }
}