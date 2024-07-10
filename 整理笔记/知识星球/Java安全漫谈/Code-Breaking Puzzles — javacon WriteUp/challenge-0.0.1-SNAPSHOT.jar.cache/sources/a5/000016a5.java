package org.springframework.boot.autoconfigure.http;

import com.google.gson.Gson;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.NoneNestedConditions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
@ConditionalOnClass({Gson.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/http/GsonHttpMessageConvertersConfiguration.class */
class GsonHttpMessageConvertersConfiguration {
    GsonHttpMessageConvertersConfiguration() {
    }

    @Configuration
    @ConditionalOnBean({Gson.class})
    @Conditional({PreferGsonOrJacksonAndJsonbUnavailableCondition.class})
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/http/GsonHttpMessageConvertersConfiguration$GsonHttpMessageConverterConfiguration.class */
    protected static class GsonHttpMessageConverterConfiguration {
        protected GsonHttpMessageConverterConfiguration() {
        }

        @ConditionalOnMissingBean
        @Bean
        public GsonHttpMessageConverter gsonHttpMessageConverter(Gson gson) {
            GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
            converter.setGson(gson);
            return converter;
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/http/GsonHttpMessageConvertersConfiguration$PreferGsonOrJacksonAndJsonbUnavailableCondition.class */
    private static class PreferGsonOrJacksonAndJsonbUnavailableCondition extends AnyNestedCondition {
        PreferGsonOrJacksonAndJsonbUnavailableCondition() {
            super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnProperty(name = {"spring.http.converters.preferred-json-mapper"}, havingValue = "gson")
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/http/GsonHttpMessageConvertersConfiguration$PreferGsonOrJacksonAndJsonbUnavailableCondition$GsonPreferred.class */
        static class GsonPreferred {
            GsonPreferred() {
            }
        }

        @Conditional({JacksonAndJsonbUnavailableCondition.class})
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/http/GsonHttpMessageConvertersConfiguration$PreferGsonOrJacksonAndJsonbUnavailableCondition$JacksonJsonbUnavailable.class */
        static class JacksonJsonbUnavailable {
            JacksonJsonbUnavailable() {
            }
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/http/GsonHttpMessageConvertersConfiguration$JacksonAndJsonbUnavailableCondition.class */
    private static class JacksonAndJsonbUnavailableCondition extends NoneNestedConditions {
        JacksonAndJsonbUnavailableCondition() {
            super(ConfigurationCondition.ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnBean({MappingJackson2HttpMessageConverter.class})
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/http/GsonHttpMessageConvertersConfiguration$JacksonAndJsonbUnavailableCondition$JacksonAvailable.class */
        static class JacksonAvailable {
            JacksonAvailable() {
            }
        }

        @ConditionalOnProperty(name = {"spring.http.converters.preferred-json-mapper"}, havingValue = "jsonb")
        /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/http/GsonHttpMessageConvertersConfiguration$JacksonAndJsonbUnavailableCondition$JsonbPreferred.class */
        static class JsonbPreferred {
            JsonbPreferred() {
            }
        }
    }
}