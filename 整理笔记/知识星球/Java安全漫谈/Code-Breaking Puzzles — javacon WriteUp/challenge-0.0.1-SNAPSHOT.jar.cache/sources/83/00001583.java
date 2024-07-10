package org.springframework.boot.autoconfigure.cache;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.spring.cache.CacheBuilder;
import com.couchbase.client.spring.cache.CouchbaseCacheManager;
import java.util.List;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnClass({Bucket.class, CouchbaseCacheManager.class})
@ConditionalOnSingleCandidate(Bucket.class)
@ConditionalOnMissingBean({CacheManager.class})
@Conditional({CacheCondition.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/cache/CouchbaseCacheConfiguration.class */
public class CouchbaseCacheConfiguration {
    private final CacheProperties cacheProperties;
    private final CacheManagerCustomizers customizers;
    private final Bucket bucket;

    public CouchbaseCacheConfiguration(CacheProperties cacheProperties, CacheManagerCustomizers customizers, Bucket bucket) {
        this.cacheProperties = cacheProperties;
        this.customizers = customizers;
        this.bucket = bucket;
    }

    @Bean
    public CouchbaseCacheManager cacheManager() {
        List<String> cacheNames = this.cacheProperties.getCacheNames();
        CacheBuilder builder = CacheBuilder.newInstance(this.bucket);
        CacheProperties.Couchbase couchbase = this.cacheProperties.getCouchbase();
        PropertyMapper propertyMapper = PropertyMapper.get();
        couchbase.getClass();
        PropertyMapper.Source<Integer> asInt = propertyMapper.from(this::getExpiration).whenNonNull().asInt((v0) -> {
            return v0.getSeconds();
        });
        builder.getClass();
        asInt.to((v1) -> {
            r1.withExpiration(v1);
        });
        String[] names = StringUtils.toStringArray(cacheNames);
        CouchbaseCacheManager cacheManager = new CouchbaseCacheManager(builder, names);
        return this.customizers.customize(cacheManager);
    }
}