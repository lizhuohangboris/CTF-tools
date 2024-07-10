package ch.qos.logback.core.sift;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/logback-core-1.2.3.jar:ch/qos/logback/core/sift/DefaultDiscriminator.class */
public class DefaultDiscriminator<E> extends AbstractDiscriminator<E> {
    public static final String DEFAULT = "default";

    @Override // ch.qos.logback.core.sift.Discriminator
    public String getDiscriminatingValue(E e) {
        return "default";
    }

    @Override // ch.qos.logback.core.sift.Discriminator
    public String getKey() {
        return "default";
    }
}