package org.apache.catalina.ssi;

import java.io.PrintWriter;
import java.util.Collection;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/SSIPrintenv.class */
public class SSIPrintenv implements SSICommand {
    @Override // org.apache.catalina.ssi.SSICommand
    public long process(SSIMediator ssiMediator, String commandName, String[] paramNames, String[] paramValues, PrintWriter writer) {
        long lastModified = 0;
        if (paramNames.length > 0) {
            String errorMessage = ssiMediator.getConfigErrMsg();
            writer.write(errorMessage);
        } else {
            Collection<String> variableNames = ssiMediator.getVariableNames();
            for (String variableName : variableNames) {
                String variableValue = ssiMediator.getVariableValue(variableName);
                if (variableValue == null) {
                    variableValue = "(none)";
                }
                writer.write(variableName);
                writer.write(61);
                writer.write(variableValue);
                writer.write(10);
                lastModified = System.currentTimeMillis();
            }
        }
        return lastModified;
    }
}