package org.springframework.boot.autoconfigure.session;

import java.util.Collections;
import java.util.List;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/session/NonUniqueSessionRepositoryException.class */
public class NonUniqueSessionRepositoryException extends RuntimeException {
    private final List<Class<?>> availableCandidates;

    public NonUniqueSessionRepositoryException(List<Class<?>> availableCandidates) {
        super("Multiple session repository candidates are available, set the 'spring.session.store-type' property accordingly");
        this.availableCandidates = !ObjectUtils.isEmpty(availableCandidates) ? availableCandidates : Collections.emptyList();
    }

    public List<Class<?>> getAvailableCandidates() {
        return this.availableCandidates;
    }
}