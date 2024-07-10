package org.springframework.boot.autoconfigure.couchbase;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.OnPropertyListCondition;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/couchbase/OnBootstrapHostsCondition.class */
class OnBootstrapHostsCondition extends OnPropertyListCondition {
    OnBootstrapHostsCondition() {
        super("spring.couchbase.bootstrap-hosts", () -> {
            return ConditionMessage.forCondition("Couchbase Bootstrap Hosts", new Object[0]);
        });
    }
}