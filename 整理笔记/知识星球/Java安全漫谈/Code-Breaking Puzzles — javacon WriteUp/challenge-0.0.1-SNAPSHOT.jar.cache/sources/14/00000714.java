package javax.websocket;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.websocket.ClientEndpointConfig;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/ClientEndpoint.class */
public @interface ClientEndpoint {
    String[] subprotocols() default {};

    Class<? extends Decoder>[] decoders() default {};

    Class<? extends Encoder>[] encoders() default {};

    Class<? extends ClientEndpointConfig.Configurator> configurator() default ClientEndpointConfig.Configurator.class;
}