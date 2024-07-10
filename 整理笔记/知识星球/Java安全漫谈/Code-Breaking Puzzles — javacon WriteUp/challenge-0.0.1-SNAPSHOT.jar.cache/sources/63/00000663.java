package javax.servlet.descriptor;

import java.util.Collection;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/descriptor/JspPropertyGroupDescriptor.class */
public interface JspPropertyGroupDescriptor {
    Collection<String> getUrlPatterns();

    String getElIgnored();

    String getPageEncoding();

    String getScriptingInvalid();

    String getIsXml();

    Collection<String> getIncludePreludes();

    Collection<String> getIncludeCodas();

    String getDeferredSyntaxAllowedAsLiteral();

    String getTrimDirectiveWhitespaces();

    String getDefaultContentType();

    String getBuffer();

    String getErrorOnUndeclaredNamespace();
}