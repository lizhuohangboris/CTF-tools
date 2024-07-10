package org.springframework.boot.autoconfigure.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@EnableConfigurationProperties({GsonProperties.class})
@Configuration
@ConditionalOnClass({Gson.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/gson/GsonAutoConfiguration.class */
public class GsonAutoConfiguration {
    @ConditionalOnMissingBean
    @Bean
    public GsonBuilder gsonBuilder(List<GsonBuilderCustomizer> customizers) {
        GsonBuilder builder = new GsonBuilder();
        customizers.forEach(c -> {
            c.customize(builder);
        });
        return builder;
    }

    @ConditionalOnMissingBean
    @Bean
    public Gson gson(GsonBuilder gsonBuilder) {
        return gsonBuilder.create();
    }

    @Bean
    public StandardGsonBuilderCustomizer standardGsonBuilderCustomizer(GsonProperties gsonProperties) {
        return new StandardGsonBuilderCustomizer(gsonProperties);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/gson/GsonAutoConfiguration$StandardGsonBuilderCustomizer.class */
    static final class StandardGsonBuilderCustomizer implements GsonBuilderCustomizer, Ordered {
        private final GsonProperties properties;

        StandardGsonBuilderCustomizer(GsonProperties properties) {
            this.properties = properties;
        }

        @Override // org.springframework.core.Ordered
        public int getOrder() {
            return 0;
        }

        @Override // org.springframework.boot.autoconfigure.gson.GsonBuilderCustomizer
        public void customize(GsonBuilder builder) {
            GsonProperties properties = this.properties;
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            properties.getClass();
            PropertyMapper.Source from = map.from(this::getGenerateNonExecutableJson);
            builder.getClass();
            from.toCall(this::generateNonExecutableJson);
            properties.getClass();
            PropertyMapper.Source from2 = map.from(this::getExcludeFieldsWithoutExposeAnnotation);
            builder.getClass();
            from2.toCall(this::excludeFieldsWithoutExposeAnnotation);
            properties.getClass();
            PropertyMapper.Source from3 = map.from(this::getSerializeNulls);
            builder.getClass();
            from3.toCall(this::serializeNulls);
            properties.getClass();
            PropertyMapper.Source from4 = map.from(this::getEnableComplexMapKeySerialization);
            builder.getClass();
            from4.toCall(this::enableComplexMapKeySerialization);
            properties.getClass();
            PropertyMapper.Source from5 = map.from(this::getDisableInnerClassSerialization);
            builder.getClass();
            from5.toCall(this::disableInnerClassSerialization);
            properties.getClass();
            PropertyMapper.Source from6 = map.from(this::getLongSerializationPolicy);
            builder.getClass();
            from6.to(this::setLongSerializationPolicy);
            properties.getClass();
            PropertyMapper.Source from7 = map.from(this::getFieldNamingPolicy);
            builder.getClass();
            from7.to(this::setFieldNamingPolicy);
            properties.getClass();
            PropertyMapper.Source from8 = map.from(this::getPrettyPrinting);
            builder.getClass();
            from8.toCall(this::setPrettyPrinting);
            properties.getClass();
            PropertyMapper.Source from9 = map.from(this::getLenient);
            builder.getClass();
            from9.toCall(this::setLenient);
            properties.getClass();
            PropertyMapper.Source from10 = map.from(this::getDisableHtmlEscaping);
            builder.getClass();
            from10.toCall(this::disableHtmlEscaping);
            properties.getClass();
            PropertyMapper.Source from11 = map.from(this::getDateFormat);
            builder.getClass();
            from11.to(this::setDateFormat);
        }
    }
}