package org.apache.tomcat.util.modeler.modules;

import java.util.List;
import javax.management.ObjectName;
import org.apache.tomcat.util.modeler.Registry;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/modeler/modules/ModelerSource.class */
public abstract class ModelerSource {
    protected Object source;

    public abstract List<ObjectName> loadDescriptors(Registry registry, String str, Object obj) throws Exception;
}