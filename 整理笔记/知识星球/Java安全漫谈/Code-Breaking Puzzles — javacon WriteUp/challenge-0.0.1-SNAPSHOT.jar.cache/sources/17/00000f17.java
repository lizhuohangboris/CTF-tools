package org.hibernate.validator.cfg.defs;

import javax.validation.constraints.Pattern;
import org.hibernate.validator.cfg.ConstraintDef;
import org.hibernate.validator.constraints.URL;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/hibernate-validator-6.0.13.Final.jar:org/hibernate/validator/cfg/defs/URLDef.class */
public class URLDef extends ConstraintDef<URLDef, URL> {
    public URLDef() {
        super(URL.class);
    }

    public URLDef protocol(String protocol) {
        addParameter("protocol", protocol);
        return this;
    }

    public URLDef host(String host) {
        addParameter("host", host);
        return this;
    }

    public URLDef port(int port) {
        addParameter("port", Integer.valueOf(port));
        return this;
    }

    public URLDef regexp(String regexp) {
        addParameter("regexp", regexp);
        return this;
    }

    public URLDef flags(Pattern.Flag... flags) {
        addParameter("flags", flags);
        return this;
    }
}