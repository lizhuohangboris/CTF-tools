package org.springframework.boot.autoconfigure.mongo.embedded;

import de.flapdoodle.embed.mongo.distribution.Feature;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

@ConfigurationProperties(prefix = "spring.mongodb.embedded")
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/mongo/embedded/EmbeddedMongoProperties.class */
public class EmbeddedMongoProperties {
    private String version = "3.5.5";
    private final Storage storage = new Storage();
    private Set<Feature> features = null;

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Set<Feature> getFeatures() {
        return this.features;
    }

    public void setFeatures(Set<Feature> features) {
        this.features = features;
    }

    public Storage getStorage() {
        return this.storage;
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/mongo/embedded/EmbeddedMongoProperties$Storage.class */
    public static class Storage {
        @DataSizeUnit(DataUnit.MEGABYTES)
        private DataSize oplogSize;
        private String replSetName;
        private String databaseDir;

        public DataSize getOplogSize() {
            return this.oplogSize;
        }

        public void setOplogSize(DataSize oplogSize) {
            this.oplogSize = oplogSize;
        }

        public String getReplSetName() {
            return this.replSetName;
        }

        public void setReplSetName(String replSetName) {
            this.replSetName = replSetName;
        }

        public String getDatabaseDir() {
            return this.databaseDir;
        }

        public void setDatabaseDir(String databaseDir) {
            this.databaseDir = databaseDir;
        }
    }
}