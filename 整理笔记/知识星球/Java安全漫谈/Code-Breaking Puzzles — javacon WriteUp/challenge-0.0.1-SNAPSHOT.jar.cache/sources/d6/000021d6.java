package org.springframework.jmx.export.assembler;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/export/assembler/AutodetectCapableMBeanInfoAssembler.class */
public interface AutodetectCapableMBeanInfoAssembler extends MBeanInfoAssembler {
    boolean includeBean(Class<?> cls, String str);
}