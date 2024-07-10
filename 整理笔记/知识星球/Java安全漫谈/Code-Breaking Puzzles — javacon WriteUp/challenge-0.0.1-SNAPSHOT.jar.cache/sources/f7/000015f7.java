package org.springframework.boot.autoconfigure.data.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScanPackages;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.cassandra.config.CassandraEntityClassScanner;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.CassandraAdminOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.convert.CassandraCustomConversions;
import org.springframework.data.cassandra.core.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.core.mapping.SimpleUserTypeResolver;

@EnableConfigurationProperties({CassandraProperties.class})
@Configuration
@ConditionalOnClass({Cluster.class, CassandraAdminOperations.class})
@AutoConfigureAfter({CassandraAutoConfiguration.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/cassandra/CassandraDataAutoConfiguration.class */
public class CassandraDataAutoConfiguration {
    private final BeanFactory beanFactory;
    private final CassandraProperties properties;
    private final Cluster cluster;
    private final Environment environment;

    public CassandraDataAutoConfiguration(BeanFactory beanFactory, CassandraProperties properties, Cluster cluster, Environment environment) {
        this.beanFactory = beanFactory;
        this.properties = properties;
        this.cluster = cluster;
        this.environment = environment;
    }

    @ConditionalOnMissingBean
    @Bean
    public CassandraMappingContext cassandraMapping(CassandraCustomConversions conversions) throws ClassNotFoundException {
        CassandraMappingContext context = new CassandraMappingContext();
        List<String> packages = EntityScanPackages.get(this.beanFactory).getPackageNames();
        if (packages.isEmpty() && AutoConfigurationPackages.has(this.beanFactory)) {
            packages = AutoConfigurationPackages.get(this.beanFactory);
        }
        if (!packages.isEmpty()) {
            context.setInitialEntitySet(CassandraEntityClassScanner.scan(packages));
        }
        PropertyMapper propertyMapper = PropertyMapper.get();
        CassandraProperties cassandraProperties = this.properties;
        cassandraProperties.getClass();
        PropertyMapper.Source as = propertyMapper.from(this::getKeyspaceName).whenHasText().as(this::createSimpleUserTypeResolver);
        context.getClass();
        as.to((v1) -> {
            r1.setUserTypeResolver(v1);
        });
        context.setCustomConversions(conversions);
        return context;
    }

    private SimpleUserTypeResolver createSimpleUserTypeResolver(String keyspaceName) {
        return new SimpleUserTypeResolver(this.cluster, keyspaceName);
    }

    @ConditionalOnMissingBean
    @Bean
    public CassandraConverter cassandraConverter(CassandraMappingContext mapping, CassandraCustomConversions conversions) {
        MappingCassandraConverter converter = new MappingCassandraConverter(mapping);
        converter.setCustomConversions(conversions);
        return converter;
    }

    @ConditionalOnMissingBean({Session.class})
    @Bean
    public CassandraSessionFactoryBean cassandraSession(CassandraConverter converter) throws Exception {
        CassandraSessionFactoryBean session = new CassandraSessionFactoryBean();
        session.setCluster(this.cluster);
        session.setConverter(converter);
        session.setKeyspaceName(this.properties.getKeyspaceName());
        Binder binder = Binder.get(this.environment);
        BindResult bind = binder.bind("spring.data.cassandra.schema-action", SchemaAction.class);
        session.getClass();
        bind.ifBound(this::setSchemaAction);
        return session;
    }

    @ConditionalOnMissingBean
    @Bean
    public CassandraTemplate cassandraTemplate(Session session, CassandraConverter converter) throws Exception {
        return new CassandraTemplate(session, converter);
    }

    @ConditionalOnMissingBean
    @Bean
    public CassandraCustomConversions cassandraCustomConversions() {
        return new CassandraCustomConversions(Collections.emptyList());
    }
}