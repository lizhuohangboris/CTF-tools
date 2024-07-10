package io.tricking.challenge;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "keywords")
@Component
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/classes/io/tricking/challenge/KeyworkProperties.class */
public class KeyworkProperties {
    private String[] blacklist;

    public String[] getBlacklist() {
        return this.blacklist;
    }

    public void setBlacklist(String[] blacklist) {
        this.blacklist = blacklist;
    }
}