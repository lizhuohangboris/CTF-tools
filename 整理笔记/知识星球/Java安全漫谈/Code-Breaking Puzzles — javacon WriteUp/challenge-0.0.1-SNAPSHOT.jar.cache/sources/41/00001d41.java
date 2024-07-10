package org.springframework.context.support;

import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/context/support/AbstractResourceBasedMessageSource.class */
public abstract class AbstractResourceBasedMessageSource extends AbstractMessageSource {
    @Nullable
    private String defaultEncoding;
    private final Set<String> basenameSet = new LinkedHashSet(4);
    private boolean fallbackToSystemLocale = true;
    private long cacheMillis = -1;

    public void setBasename(String basename) {
        setBasenames(basename);
    }

    public void setBasenames(String... basenames) {
        this.basenameSet.clear();
        addBasenames(basenames);
    }

    public void addBasenames(String... basenames) {
        if (!ObjectUtils.isEmpty((Object[]) basenames)) {
            for (String basename : basenames) {
                Assert.hasText(basename, "Basename must not be empty");
                this.basenameSet.add(basename.trim());
            }
        }
    }

    public Set<String> getBasenameSet() {
        return this.basenameSet;
    }

    public void setDefaultEncoding(@Nullable String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public String getDefaultEncoding() {
        return this.defaultEncoding;
    }

    public void setFallbackToSystemLocale(boolean fallbackToSystemLocale) {
        this.fallbackToSystemLocale = fallbackToSystemLocale;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isFallbackToSystemLocale() {
        return this.fallbackToSystemLocale;
    }

    public void setCacheSeconds(int cacheSeconds) {
        this.cacheMillis = cacheSeconds * 1000;
    }

    public void setCacheMillis(long cacheMillis) {
        this.cacheMillis = cacheMillis;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public long getCacheMillis() {
        return this.cacheMillis;
    }
}