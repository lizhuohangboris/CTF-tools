package org.apache.catalina.ssi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import org.apache.catalina.util.IOTools;
import org.thymeleaf.standard.processor.StandardIncludeTagProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/SSIExec.class */
public class SSIExec implements SSICommand {
    protected final SSIInclude ssiInclude = new SSIInclude();
    protected static final int BUFFER_SIZE = 1024;

    @Override // org.apache.catalina.ssi.SSICommand
    public long process(SSIMediator ssiMediator, String commandName, String[] paramNames, String[] paramValues, PrintWriter writer) {
        long lastModified = 0;
        String configErrMsg = ssiMediator.getConfigErrMsg();
        String paramName = paramNames[0];
        String paramValue = paramValues[0];
        String substitutedValue = ssiMediator.substituteVariables(paramValue);
        if (paramName.equalsIgnoreCase("cgi")) {
            lastModified = this.ssiInclude.process(ssiMediator, StandardIncludeTagProcessor.ATTR_NAME, new String[]{"virtual"}, new String[]{substitutedValue}, writer);
        } else if (paramName.equalsIgnoreCase("cmd")) {
            boolean foundProgram = false;
            try {
                Runtime rt = Runtime.getRuntime();
                Process proc = rt.exec(substitutedValue);
                foundProgram = true;
                BufferedReader stdOutReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                BufferedReader stdErrReader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                char[] buf = new char[1024];
                IOTools.flow(stdErrReader, writer, buf);
                IOTools.flow(stdOutReader, writer, buf);
                proc.waitFor();
                lastModified = System.currentTimeMillis();
            } catch (IOException e) {
                if (!foundProgram) {
                }
                ssiMediator.log("Couldn't exec file: " + substitutedValue, e);
            } catch (InterruptedException e2) {
                ssiMediator.log("Couldn't exec file: " + substitutedValue, e2);
                writer.write(configErrMsg);
            }
        }
        return lastModified;
    }
}