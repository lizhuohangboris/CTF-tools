package org.apache.catalina.ssi;

import java.io.PrintWriter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/SSICommand.class */
public interface SSICommand {
    long process(SSIMediator sSIMediator, String str, String[] strArr, String[] strArr2, PrintWriter printWriter) throws SSIStopProcessingException;
}