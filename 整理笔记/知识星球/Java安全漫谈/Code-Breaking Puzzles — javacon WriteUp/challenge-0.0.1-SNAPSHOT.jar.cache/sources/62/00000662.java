package javax.servlet.descriptor;

import java.util.Collection;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:javax/servlet/descriptor/JspConfigDescriptor.class */
public interface JspConfigDescriptor {
    Collection<TaglibDescriptor> getTaglibs();

    Collection<JspPropertyGroupDescriptor> getJspPropertyGroups();
}