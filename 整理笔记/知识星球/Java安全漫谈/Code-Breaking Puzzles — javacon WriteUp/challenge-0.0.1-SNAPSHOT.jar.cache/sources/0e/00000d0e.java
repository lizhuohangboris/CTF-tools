package org.apache.tomcat.util.modeler;

import java.io.Serializable;
import javax.management.MBeanFeatureInfo;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/modeler/FeatureInfo.class */
public class FeatureInfo implements Serializable {
    static final long serialVersionUID = -911529176124712296L;
    protected String description = null;
    protected String name = null;
    protected MBeanFeatureInfo info = null;
    protected String type = null;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}