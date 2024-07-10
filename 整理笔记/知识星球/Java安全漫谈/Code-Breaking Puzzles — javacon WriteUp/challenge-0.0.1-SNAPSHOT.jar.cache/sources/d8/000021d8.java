package org.springframework.jmx.export.assembler;

import javax.management.JMException;
import javax.management.modelmbean.ModelMBeanInfo;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/export/assembler/MBeanInfoAssembler.class */
public interface MBeanInfoAssembler {
    ModelMBeanInfo getMBeanInfo(Object obj, String str) throws JMException;
}