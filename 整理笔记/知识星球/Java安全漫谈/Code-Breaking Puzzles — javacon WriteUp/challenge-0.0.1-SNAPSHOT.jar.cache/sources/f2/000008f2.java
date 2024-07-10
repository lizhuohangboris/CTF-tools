package org.apache.catalina.ssi;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;
import org.apache.catalina.util.IOTools;
import org.thymeleaf.standard.processor.StandardIfTagProcessor;
import org.thymeleaf.standard.processor.StandardIncludeTagProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/SSIProcessor.class */
public class SSIProcessor {
    protected static final String COMMAND_START = "<!--#";
    protected static final String COMMAND_END = "-->";
    protected final SSIExternalResolver ssiExternalResolver;
    protected final HashMap<String, SSICommand> commands = new HashMap<>();
    protected final int debug;
    protected final boolean allowExec;

    public SSIProcessor(SSIExternalResolver ssiExternalResolver, int debug, boolean allowExec) {
        this.ssiExternalResolver = ssiExternalResolver;
        this.debug = debug;
        this.allowExec = allowExec;
        addBuiltinCommands();
    }

    protected void addBuiltinCommands() {
        addCommand("config", new SSIConfig());
        addCommand("echo", new SSIEcho());
        if (this.allowExec) {
            addCommand("exec", new SSIExec());
        }
        addCommand(StandardIncludeTagProcessor.ATTR_NAME, new SSIInclude());
        addCommand("flastmod", new SSIFlastmod());
        addCommand("fsize", new SSIFsize());
        addCommand("printenv", new SSIPrintenv());
        addCommand("set", new SSISet());
        SSIConditional ssiConditional = new SSIConditional();
        addCommand(StandardIfTagProcessor.ATTR_NAME, ssiConditional);
        addCommand("elif", ssiConditional);
        addCommand("endif", ssiConditional);
        addCommand("else", ssiConditional);
    }

    public void addCommand(String name, SSICommand command) {
        this.commands.put(name, command);
    }

    public long process(Reader reader, long lastModifiedDate, PrintWriter writer) throws IOException {
        SSIMediator ssiMediator = new SSIMediator(this.ssiExternalResolver, lastModifiedDate);
        StringWriter stringWriter = new StringWriter();
        IOTools.flow(reader, stringWriter);
        String fileContents = stringWriter.toString();
        int index = 0;
        boolean inside = false;
        StringBuilder command = new StringBuilder();
        while (index < fileContents.length()) {
            try {
                char c = fileContents.charAt(index);
                if (!inside) {
                    if (c == COMMAND_START.charAt(0) && charCmp(fileContents, index, COMMAND_START)) {
                        inside = true;
                        index += COMMAND_START.length();
                        command.setLength(0);
                    } else {
                        if (!ssiMediator.getConditionalState().processConditionalCommandsOnly) {
                            writer.write(c);
                        }
                        index++;
                    }
                } else if (c == COMMAND_END.charAt(0) && charCmp(fileContents, index, COMMAND_END)) {
                    inside = false;
                    index += COMMAND_END.length();
                    String strCmd = parseCmd(command);
                    if (this.debug > 0) {
                        this.ssiExternalResolver.log("SSIProcessor.process -- processing command: " + strCmd, null);
                    }
                    String[] paramNames = parseParamNames(command, strCmd.length());
                    String[] paramValues = parseParamValues(command, strCmd.length(), paramNames.length);
                    String configErrMsg = ssiMediator.getConfigErrMsg();
                    SSICommand ssiCommand = this.commands.get(strCmd.toLowerCase(Locale.ENGLISH));
                    String errorMessage = null;
                    if (ssiCommand == null) {
                        errorMessage = "Unknown command: " + strCmd;
                    } else if (paramValues == null) {
                        errorMessage = "Error parsing directive parameters.";
                    } else if (paramNames.length != paramValues.length) {
                        errorMessage = "Parameter names count does not match parameter values count on command: " + strCmd;
                    } else if (!ssiMediator.getConditionalState().processConditionalCommandsOnly || (ssiCommand instanceof SSIConditional)) {
                        long lmd = ssiCommand.process(ssiMediator, strCmd, paramNames, paramValues, writer);
                        if (lmd > lastModifiedDate) {
                            lastModifiedDate = lmd;
                        }
                    }
                    if (errorMessage != null) {
                        this.ssiExternalResolver.log(errorMessage, null);
                        writer.write(configErrMsg);
                    }
                } else {
                    command.append(c);
                    index++;
                }
            } catch (SSIStopProcessingException e) {
            }
        }
        return lastModifiedDate;
    }

    protected String[] parseParamNames(StringBuilder cmd, int start) {
        boolean z;
        int bIdx = start;
        int i = 0;
        boolean inside = false;
        StringBuilder retBuf = new StringBuilder();
        while (bIdx < cmd.length()) {
            if (!inside) {
                while (bIdx < cmd.length() && isSpace(cmd.charAt(bIdx))) {
                    bIdx++;
                }
                if (bIdx >= cmd.length()) {
                    break;
                }
                inside = !inside;
            } else {
                while (bIdx < cmd.length() && cmd.charAt(bIdx) != '=') {
                    retBuf.append(cmd.charAt(bIdx));
                    bIdx++;
                }
                retBuf.append('=');
                inside = !inside;
                int quotes = 0;
                boolean escaped = false;
                while (bIdx < cmd.length() && quotes != 2) {
                    char c = cmd.charAt(bIdx);
                    if (c == '\\' && !escaped) {
                        z = true;
                    } else {
                        if (c == '\"' && !escaped) {
                            quotes++;
                        }
                        z = false;
                    }
                    escaped = z;
                    bIdx++;
                }
            }
        }
        StringTokenizer str = new StringTokenizer(retBuf.toString(), "=");
        String[] retString = new String[str.countTokens()];
        while (str.hasMoreTokens()) {
            int i2 = i;
            i++;
            retString[i2] = str.nextToken().trim();
        }
        return retString;
    }

    protected String[] parseParamValues(StringBuilder cmd, int start, int count) {
        int valIndex = 0;
        boolean inside = false;
        String[] vals = new String[count];
        StringBuilder sb = new StringBuilder();
        char endQuote = 0;
        int bIdx = start;
        while (bIdx < cmd.length()) {
            if (!inside) {
                while (bIdx < cmd.length() && !isQuote(cmd.charAt(bIdx))) {
                    bIdx++;
                }
                if (bIdx >= cmd.length()) {
                    break;
                }
                inside = !inside;
                endQuote = cmd.charAt(bIdx);
            } else {
                boolean escaped = false;
                while (bIdx < cmd.length()) {
                    char c = cmd.charAt(bIdx);
                    if (c == '\\' && !escaped) {
                        escaped = true;
                    } else if (c == endQuote && !escaped) {
                        break;
                    } else {
                        if (c == '$' && escaped) {
                            sb.append('\\');
                        }
                        escaped = false;
                        sb.append(c);
                    }
                    bIdx++;
                }
                if (bIdx == cmd.length()) {
                    return null;
                }
                int i = valIndex;
                valIndex++;
                vals[i] = sb.toString();
                sb.delete(0, sb.length());
                inside = !inside;
            }
            bIdx++;
        }
        return vals;
    }

    private String parseCmd(StringBuilder cmd) {
        int firstLetter = -1;
        int lastLetter = -1;
        for (int i = 0; i < cmd.length(); i++) {
            char c = cmd.charAt(i);
            if (Character.isLetter(c)) {
                if (firstLetter == -1) {
                    firstLetter = i;
                }
                lastLetter = i;
            } else if (!isSpace(c) || lastLetter > -1) {
                break;
            }
        }
        if (firstLetter == -1) {
            return "";
        }
        return cmd.substring(firstLetter, lastLetter + 1);
    }

    protected boolean charCmp(String buf, int index, String command) {
        return buf.regionMatches(index, command, 0, command.length());
    }

    protected boolean isSpace(char c) {
        return c == ' ' || c == '\n' || c == '\t' || c == '\r';
    }

    protected boolean isQuote(char c) {
        return c == '\'' || c == '\"' || c == '`';
    }
}