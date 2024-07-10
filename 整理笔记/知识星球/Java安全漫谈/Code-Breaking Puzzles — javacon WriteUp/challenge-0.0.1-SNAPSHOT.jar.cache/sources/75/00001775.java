package org.springframework.boot.autoconfigure.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.connection.netty.NettyStreamFactoryFactory;
import com.mongodb.reactivestreams.client.MongoClient;
import io.netty.channel.socket.SocketChannel;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import reactor.core.publisher.Flux;

@EnableConfigurationProperties({MongoProperties.class})
@Configuration
@ConditionalOnClass({MongoClient.class, Flux.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/mongo/MongoReactiveAutoConfiguration.class */
public class MongoReactiveAutoConfiguration {
    private final MongoClientSettings settings;
    private MongoClient mongo;

    public MongoReactiveAutoConfiguration(ObjectProvider<MongoClientSettings> settings) {
        this.settings = settings.getIfAvailable();
    }

    @PreDestroy
    public void close() {
        if (this.mongo != null) {
            this.mongo.close();
        }
    }

    @ConditionalOnMissingBean
    @Bean
    public MongoClient reactiveStreamsMongoClient(MongoProperties properties, Environment environment, ObjectProvider<MongoClientSettingsBuilderCustomizer> builderCustomizers) {
        ReactiveMongoClientFactory factory = new ReactiveMongoClientFactory(properties, environment, (List) builderCustomizers.orderedStream().collect(Collectors.toList()));
        this.mongo = factory.createMongoClient(this.settings);
        return this.mongo;
    }

    @Configuration
    @ConditionalOnClass({SocketChannel.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/mongo/MongoReactiveAutoConfiguration$NettyDriverConfiguration.class */
    static class NettyDriverConfiguration {
        NettyDriverConfiguration() {
        }

        @Bean
        @Order(Integer.MIN_VALUE)
        public MongoClientSettingsBuilderCustomizer nettyDriverCustomizer(ObjectProvider<MongoClientSettings> settings) {
            return builder -> {
                if (!isStreamFactoryFactoryDefined((MongoClientSettings) settings.getIfAvailable())) {
                    builder.streamFactoryFactory(NettyStreamFactoryFactory.builder().build());
                }
            };
        }

        private boolean isStreamFactoryFactoryDefined(MongoClientSettings settings) {
            return (settings == null || settings.getStreamFactoryFactory() == null) ? false : true;
        }
    }
}