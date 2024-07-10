package org.springframework.boot.autoconfigure.influx;

import java.util.function.Supplier;
import okhttp3.OkHttpClient;

@FunctionalInterface
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/influx/InfluxDbOkHttpClientBuilderProvider.class */
public interface InfluxDbOkHttpClientBuilderProvider extends Supplier<OkHttpClient.Builder> {
}