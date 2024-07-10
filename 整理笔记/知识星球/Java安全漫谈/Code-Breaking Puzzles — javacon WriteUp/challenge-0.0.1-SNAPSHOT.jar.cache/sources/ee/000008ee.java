package org.apache.catalina.ssi;

import ch.qos.logback.core.util.FileSize;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/SSIFsize.class */
public final class SSIFsize implements SSICommand {
    static final int ONE_KILOBYTE = 1024;
    static final int ONE_MEGABYTE = 1048576;

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
                    long size = ssiMediator.getFileSize(substitutedValue, virtual);
                    String configSizeFmt = ssiMediator.getConfigSizeFmt();
                    writer.write(formatSize(size, configSizeFmt));
                } else {
                    ssiMediator.log("#fsize--Invalid attribute: " + paramName);
                    writer.write(configErrMsg);
                }
            } catch (IOException e) {
                ssiMediator.log("#fsize--Couldn't get size for file: " + substitutedValue, e);
                writer.write(configErrMsg);
            }
        }
        return lastModified;
    }

    public String repeat(char aChar, int numChars) {
        if (numChars < 0) {
            throw new IllegalArgumentException("Num chars can't be negative");
        }
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < numChars; i++) {
            buf.append(aChar);
        }
        return buf.toString();
    }

    public String padLeft(String str, int maxChars) {
        String result = str;
        int charsToAdd = maxChars - str.length();
        if (charsToAdd > 0) {
            result = repeat(' ', charsToAdd) + str;
        }
        return result;
    }

    protected String formatSize(long size, String format) {
        String retString;
        String retString2;
        if (format.equalsIgnoreCase("bytes")) {
            DecimalFormat decimalFormat = new DecimalFormat("#,##0");
            retString2 = decimalFormat.format(size);
        } else {
            if (size == 0) {
                retString = "0k";
            } else if (size < FileSize.KB_COEFFICIENT) {
                retString = "1k";
            } else if (size < FileSize.MB_COEFFICIENT) {
                String retString3 = Long.toString((size + 512) / FileSize.KB_COEFFICIENT);
                retString = retString3 + "k";
            } else if (size < 103809024) {
                DecimalFormat decimalFormat2 = new DecimalFormat("0.0M");
                retString = decimalFormat2.format(size / 1048576.0d);
            } else {
                String retString4 = Long.toString((size + 541696) / FileSize.MB_COEFFICIENT);
                retString = retString4 + "M";
            }
            retString2 = padLeft(retString, 5);
        }
        return retString2;
    }
}