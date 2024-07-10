package org.springframework.boot.autoconfigure.context;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.boot.convert.DurationUnit;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/context/MessageSourceProperties.class */
public class MessageSourceProperties {
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration cacheDuration;
    private String basename = "messages";
    private Charset encoding = StandardCharsets.UTF_8;
    private boolean fallbackToSystemLocale = true;
    private boolean alwaysUseMessageFormat = false;
    private boolean useCodeAsDefaultMessage = false;

    public String getBasename() {
        return this.basename;
    }

    public void setBasename(String basename) {
        this.basename = basename;
    }

    public Charset getEncoding() {
        return this.encoding;
    }

    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }

    public Duration getCacheDuration() {
        return this.cacheDuration;
    }

    public void setCacheDuration(Duration cacheDuration) {
        this.cacheDuration = cacheDuration;
    }

    public boolean isFallbackToSystemLocale() {
        return this.fallbackToSystemLocale;
    }

    public void setFallbackToSystemLocale(boolean fallbackToSystemLocale) {
        this.fallbackToSystemLocale = fallbackToSystemLocale;
    }

    public boolean isAlwaysUseMessageFormat() {
        return this.alwaysUseMessageFormat;
    }

    public void setAlwaysUseMessageFormat(boolean alwaysUseMessageFormat) {
        this.alwaysUseMessageFormat = alwaysUseMessageFormat;
    }

    public boolean isUseCodeAsDefaultMessage() {
        return this.useCodeAsDefaultMessage;
    }

    public void setUseCodeAsDefaultMessage(boolean useCodeAsDefaultMessage) {
        this.useCodeAsDefaultMessage = useCodeAsDefaultMessage;
    }
}