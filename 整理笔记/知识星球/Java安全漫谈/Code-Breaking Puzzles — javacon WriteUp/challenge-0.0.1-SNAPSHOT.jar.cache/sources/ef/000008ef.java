package org.apache.catalina.ssi;

import java.io.IOException;
import java.io.PrintWriter;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/SSIInclude.class */
public final class SSIInclude implements SSICommand {
    @Override // org.apache.catalina.ssi.SSICommand
    public long process(SSIMediator ssiMediator, String commandName, String[] paramNames, String[] paramValues, PrintWriter writer) {
        long lastModified = 0;
        String configErrMsg = ssiMediator.getConfigErrMsg();
        for (int i = 0; i < paramNames.length; i++) {
            String paramName = paramNames[i];
            String paramValue = paramValues[i];
            String substitutedValue = ssiMediator.substituteVariables(paramValue);
            try {
                if (paramName.equalsIgnoreCase("file") || paramName.equalsIgnoreCase("virtual")) {
                    boolean virtual = paramName.equalsIgnoreCase("virtual");
                    lastModified = ssiMediator.getFileLastModified(substitutedValue, virtual);
                    String text = ssiMediator.getFileText(substitutedValue, virtual);
                    writer.write(text);
                } else {
                    ssiMediator.log("#include--Invalid attribute: " + paramName);
                    writer.write(configErrMsg);
                }
            } catch (IOException e) {
                ssiMediator.log("#include--Couldn't include file: " + substitutedValue, e);
                writer.write(configErrMsg);
            }
        }
        return lastModified;
    }
}