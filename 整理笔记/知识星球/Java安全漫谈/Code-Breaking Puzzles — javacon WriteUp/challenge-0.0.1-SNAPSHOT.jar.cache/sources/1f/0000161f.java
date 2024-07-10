package org.springframework.boot.autoconfigure.data.ldap;

import javax.naming.ldap.LdapContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.data.ldap.repository.support.LdapRepositoryFactoryBean;

@Configuration
@ConditionalOnClass({LdapContext.class, LdapRepository.class})
@ConditionalOnMissingBean({LdapRepositoryFactoryBean.class})
@ConditionalOnProperty(prefix = "spring.data.ldap.repositories", name = {"enabled"}, havingValue = "true", matchIfMissing = true)
@Import({LdapRepositoriesRegistrar.class})
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/data/ldap/LdapRepositoriesAutoConfiguration.class */
public class LdapRepositoriesAutoConfiguration {
}