package org.springframework.boot.web.embedded.tomcat;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/web/embedded/tomcat/TldSkipPatterns.class */
final class TldSkipPatterns {
    private static final Set<String> TOMCAT;
    private static final Set<String> ADDITIONAL;
    static final Set<String> DEFAULT;

    static {
        Set<String> patterns = new LinkedHashSet<>();
        patterns.add("ant-*.jar");
        patterns.add("aspectj*.jar");
        patterns.add("commons-beanutils*.jar");
        patterns.add("commons-codec*.jar");
        patterns.add("commons-collections*.jar");
        patterns.add("commons-dbcp*.jar");
        patterns.add("commons-digester*.jar");
        patterns.add("commons-fileupload*.jar");
        patterns.add("commons-httpclient*.jar");
        patterns.add("commons-io*.jar");
        patterns.add("commons-lang*.jar");
        patterns.add("commons-logging*.jar");
        patterns.add("commons-math*.jar");
        patterns.add("commons-pool*.jar");
        patterns.add("geronimo-spec-jaxrpc*.jar");
        patterns.add("h2*.jar");
        patterns.add("hamcrest*.jar");
        patterns.add("hibernate*.jar");
        patterns.add("jmx*.jar");
        patterns.add("jmx-tools-*.jar");
        patterns.add("jta*.jar");
        patterns.add("junit-*.jar");
        patterns.add("httpclient*.jar");
        patterns.add("log4j-*.jar");
        patterns.add("mail*.jar");
        patterns.add("org.hamcrest*.jar");
        patterns.add("slf4j*.jar");
        patterns.add("tomcat-embed-core-*.jar");
        patterns.add("tomcat-embed-logging-*.jar");
        patterns.add("tomcat-jdbc-*.jar");
        patterns.add("tomcat-juli-*.jar");
        patterns.add("tools.jar");
        patterns.add("wsdl4j*.jar");
        patterns.add("xercesImpl-*.jar");
        patterns.add("xmlParserAPIs-*.jar");
        patterns.add("xml-apis-*.jar");
        TOMCAT = Collections.unmodifiableSet(patterns);
        Set<String> patterns2 = new LinkedHashSet<>();
        patterns2.add("antlr-*.jar");
        patterns2.add("aopalliance-*.jar");
        patterns2.add("aspectjrt-*.jar");
        patterns2.add("aspectjweaver-*.jar");
        patterns2.add("classmate-*.jar");
        patterns2.add("dom4j-*.jar");
        patterns2.add("ecj-*.jar");
        patterns2.add("ehcache-core-*.jar");
        patterns2.add("hibernate-core-*.jar");
        patterns2.add("hibernate-commons-annotations-*.jar");
        patterns2.add("hibernate-entitymanager-*.jar");
        patterns2.add("hibernate-jpa-2.1-api-*.jar");
        patterns2.add("hibernate-validator-*.jar");
        patterns2.add("hsqldb-*.jar");
        patterns2.add("jackson-annotations-*.jar");
        patterns2.add("jackson-core-*.jar");
        patterns2.add("jackson-databind-*.jar");
        patterns2.add("jandex-*.jar");
        patterns2.add("javassist-*.jar");
        patterns2.add("jboss-logging-*.jar");
        patterns2.add("jboss-transaction-api_*.jar");
        patterns2.add("jcl-over-slf4j-*.jar");
        patterns2.add("jdom-*.jar");
        patterns2.add("jul-to-slf4j-*.jar");
        patterns2.add("log4j-over-slf4j-*.jar");
        patterns2.add("logback-classic-*.jar");
        patterns2.add("logback-core-*.jar");
        patterns2.add("rome-*.jar");
        patterns2.add("slf4j-api-*.jar");
        patterns2.add("spring-aop-*.jar");
        patterns2.add("spring-aspects-*.jar");
        patterns2.add("spring-beans-*.jar");
        patterns2.add("spring-boot-*.jar");
        patterns2.add("spring-core-*.jar");
        patterns2.add("spring-context-*.jar");
        patterns2.add("spring-data-*.jar");
        patterns2.add("spring-expression-*.jar");
        patterns2.add("spring-jdbc-*.jar,");
        patterns2.add("spring-orm-*.jar");
        patterns2.add("spring-oxm-*.jar");
        patterns2.add("spring-tx-*.jar");
        patterns2.add("snakeyaml-*.jar");
        patterns2.add("tomcat-embed-el-*.jar");
        patterns2.add("validation-api-*.jar");
        patterns2.add("xml-apis-*.jar");
        ADDITIONAL = Collections.unmodifiableSet(patterns2);
        Set<String> patterns3 = new LinkedHashSet<>();
        patterns3.addAll(TOMCAT);
        patterns3.addAll(ADDITIONAL);
        DEFAULT = Collections.unmodifiableSet(patterns3);
    }

    private TldSkipPatterns() {
    }
}