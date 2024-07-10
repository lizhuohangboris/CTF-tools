package javax.websocket.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.server.ServerEndpointConfig;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.12.jar:javax/websocket/server/ServerEndpoint.class */
public @interface ServerEndpoint {
    String value();

    String[] subprotocols() default {};

    Class<? extends Decoder>[] decoders() default {};

    Class<? extends Encoder>[] encoders() default {};

    Class<? extends ServerEndpointConfig.Configurator> configurator() default ServerEndpointConfig.Configurator.class;
}