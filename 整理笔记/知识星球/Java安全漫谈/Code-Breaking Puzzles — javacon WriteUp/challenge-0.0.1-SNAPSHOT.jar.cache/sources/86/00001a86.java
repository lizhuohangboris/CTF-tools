package org.springframework.boot.web.embedded.netty;

import java.util.function.Function;
import reactor.netty.http.server.HttpServer;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/netty/NettyServerCustomizer.class */
public interface NettyServerCustomizer extends Function<HttpServer, HttpServer> {
}