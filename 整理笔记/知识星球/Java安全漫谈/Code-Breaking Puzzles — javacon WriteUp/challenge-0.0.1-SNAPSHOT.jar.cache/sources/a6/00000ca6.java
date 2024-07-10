package org.apache.tomcat.util.digester;

import java.util.List;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/digester/Rules.class */
public interface Rules {
    Digester getDigester();

    void setDigester(Digester digester);

    void add(String str, Rule rule);

    void clear();

    List<Rule> match(String str, String str2);

    List<Rule> rules();
}