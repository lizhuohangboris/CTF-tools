package org.springframework.beans.factory.config;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.PropertyAccessor;
import org.springframework.core.CollectionFactory;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.reader.UnicodeReader;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/config/YamlProcessor.class */
public abstract class YamlProcessor {
    private final Log logger = LogFactory.getLog(getClass());
    private ResolutionMethod resolutionMethod = ResolutionMethod.OVERRIDE;
    private Resource[] resources = new Resource[0];
    private List<DocumentMatcher> documentMatchers = Collections.emptyList();
    private boolean matchDefault = true;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/config/YamlProcessor$DocumentMatcher.class */
    public interface DocumentMatcher {
        MatchStatus matches(Properties properties);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/config/YamlProcessor$MatchCallback.class */
    public interface MatchCallback {
        void process(Properties properties, Map<String, Object> map);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/config/YamlProcessor$ResolutionMethod.class */
    public enum ResolutionMethod {
        OVERRIDE,
        OVERRIDE_AND_IGNORE,
        FIRST_FOUND
    }

    public void setDocumentMatchers(DocumentMatcher... matchers) {
        this.documentMatchers = Arrays.asList(matchers);
    }

    public void setMatchDefault(boolean matchDefault) {
        this.matchDefault = matchDefault;
    }

    public void setResolutionMethod(ResolutionMethod resolutionMethod) {
        Assert.notNull(resolutionMethod, "ResolutionMethod must not be null");
        this.resolutionMethod = resolutionMethod;
    }

    public void setResources(Resource... resources) {
        this.resources = resources;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void process(MatchCallback callback) {
        Resource[] resourceArr;
        Yaml yaml = createYaml();
        for (Resource resource : this.resources) {
            boolean found = process(callback, yaml, resource);
            if (this.resolutionMethod == ResolutionMethod.FIRST_FOUND && found) {
                return;
            }
        }
    }

    protected Yaml createYaml() {
        LoaderOptions options = new LoaderOptions();
        options.setAllowDuplicateKeys(false);
        return new Yaml(options);
    }

    private boolean process(MatchCallback callback, Yaml yaml, Resource resource) {
        int count = 0;
        try {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Loading from YAML: " + resource);
            }
            Reader reader = new UnicodeReader(resource.getInputStream());
            for (Object object : yaml.loadAll(reader)) {
                if (object != null && process(asMap(object), callback)) {
                    count++;
                    if (this.resolutionMethod == ResolutionMethod.FIRST_FOUND) {
                        break;
                    }
                }
            }
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Loaded " + count + " document" + (count > 1 ? "s" : "") + " from YAML resource: " + resource);
            }
            reader.close();
        } catch (IOException ex) {
            handleProcessError(resource, ex);
        }
        return count > 0;
    }

    private void handleProcessError(Resource resource, IOException ex) {
        if (this.resolutionMethod != ResolutionMethod.FIRST_FOUND && this.resolutionMethod != ResolutionMethod.OVERRIDE_AND_IGNORE) {
            throw new IllegalStateException(ex);
        }
        if (this.logger.isWarnEnabled()) {
            this.logger.warn("Could not load map from " + resource + ": " + ex.getMessage());
        }
    }

    private Map<String, Object> asMap(Object object) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (!(object instanceof Map)) {
            result.put("document", object);
            return result;
        }
        Map<Object, Object> map = (Map) object;
        map.forEach(key, value -> {
            if (value instanceof Map) {
                value = asMap(value);
            }
            if (key instanceof CharSequence) {
                result.put(key.toString(), value);
            } else {
                result.put(PropertyAccessor.PROPERTY_KEY_PREFIX + key.toString() + "]", value);
            }
        });
        return result;
    }

    private boolean process(Map<String, Object> map, MatchCallback callback) {
        Properties properties = CollectionFactory.createStringAdaptingProperties();
        properties.putAll(getFlattenedMap(map));
        if (this.documentMatchers.isEmpty()) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Merging document (no matchers set): " + map);
            }
            callback.process(properties, map);
            return true;
        }
        MatchStatus result = MatchStatus.ABSTAIN;
        for (DocumentMatcher matcher : this.documentMatchers) {
            MatchStatus match = matcher.matches(properties);
            result = MatchStatus.getMostSpecific(match, result);
            if (match == MatchStatus.FOUND) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Matched document with document matcher: " + properties);
                }
                callback.process(properties, map);
                return true;
            }
        }
        if (result == MatchStatus.ABSTAIN && this.matchDefault) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Matched document with default matcher: " + map);
            }
            callback.process(properties, map);
            return true;
        } else if (this.logger.isDebugEnabled()) {
            this.logger.debug("Unmatched document: " + map);
            return false;
        } else {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final Map<String, Object> getFlattenedMap(Map<String, Object> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        buildFlattenedMap(result, source, null);
        return result;
    }

    private void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, @Nullable String path) {
        source.forEach(key, value -> {
            if (StringUtils.hasText(path)) {
                if (key.startsWith(PropertyAccessor.PROPERTY_KEY_PREFIX)) {
                    key = path + key;
                } else {
                    key = path + '.' + key;
                }
            }
            if (value instanceof String) {
                result.put(key, value);
            } else if (value instanceof Map) {
                Map<String, Object> map = (Map) value;
                buildFlattenedMap(result, map, key);
            } else if (value instanceof Collection) {
                Collection<Object> collection = (Collection) value;
                if (collection.isEmpty()) {
                    result.put(key, "");
                    return;
                }
                int count = 0;
                for (Object object : collection) {
                    int i = count;
                    count++;
                    buildFlattenedMap(result, Collections.singletonMap(PropertyAccessor.PROPERTY_KEY_PREFIX + i + "]", object), key);
                }
            } else {
                result.put(key, value != null ? value : "");
            }
        });
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-beans-5.1.2.RELEASE.jar:org/springframework/beans/factory/config/YamlProcessor$MatchStatus.class */
    public enum MatchStatus {
        FOUND,
        NOT_FOUND,
        ABSTAIN;

        public static MatchStatus getMostSpecific(MatchStatus a, MatchStatus b) {
            return a.ordinal() < b.ordinal() ? a : b;
        }
    }
}