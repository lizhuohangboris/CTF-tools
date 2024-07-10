package org.springframework.boot.autoconfigure.http;

import javax.json.bind.Jsonb;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.JsonbHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
@ConditionalOnClass({Jsonb.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/http/JsonbHttpMessageConvertersConfiguration.class */
class JsonbHttpMessageConvertersConfiguration {
    JsonbHttpMessageConvertersConfiguration() {
    }

    @Configuration
    @ConditionalOnBean({Jsonb.class})
    @Conditional({PreferJsonbOrMissingJacksonAndGsonCondition.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/http/JsonbHttpMessageConvertersConfiguration$JsonbHttpMessageConverterConfiguration.class */
    protected static class JsonbHttpMessageConverterConfiguration {
        protected JsonbHttpMessageConverterConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        public JsonbHttpMessageConverter jsonbHttpMessageConverter(Jsonb jsonb) {
            JsonbHttpMessageConverter converter = new JsonbHttpMessageConverter();
            converter.setJsonb(jsonb);
            return converter;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/http/JsonbHttpMessageConvertersConfiguration$PreferJsonbOrMissingJacksonAndGsonCondition.class */
    private static class PreferJsonbOrMissingJacksonAndGsonCondition extends AnyNestedCondition {
        PreferJsonbOrMissingJacksonAndGsonCondition() {
            super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnProperty(name = {"spring.http.converters.preferred-json-mapper"}, havingValue = "jsonb")
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/http/JsonbHttpMessageConvertersConfiguration$PreferJsonbOrMissingJacksonAndGsonCondition$JsonbPreferred.class */
        static class JsonbPreferred {
            JsonbPreferred() {
            }
        }

        @ConditionalOnMissingBean({MappingJackson2HttpMessageConverter.class, GsonHttpMessageConverter.class})
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/http/JsonbHttpMessageConvertersConfiguration$PreferJsonbOrMissingJacksonAndGsonCondition$JacksonAndGsonMissing.class */
        static class JacksonAndGsonMissing {
            JacksonAndGsonMissing() {
            }
        }
    }
}