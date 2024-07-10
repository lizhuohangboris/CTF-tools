package org.apache.catalina.session;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.catalina.Globals;
import org.apache.catalina.valves.CrawlerSessionManagerValve;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/session/Constants.class */
public class Constants {
    public static final Set<String> excludedAttributeNames;

    static {
        Set<String> names = new HashSet<>();
        names.add(Globals.SUBJECT_ATTR);
        names.add(Globals.GSS_CREDENTIAL_ATTR);
        names.add(CrawlerSessionManagerValve.class.getName());
        excludedAttributeNames = Collections.unmodifiableSet(names);
    }
}