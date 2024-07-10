package org.apache.catalina.ssi;

import java.io.PrintWriter;
import org.thymeleaf.engine.XMLDeclaration;
import org.thymeleaf.spring5.processor.SpringInputGeneralFieldTagProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/SSIEcho.class */
public class SSIEcho implements SSICommand {
    protected static final String DEFAULT_ENCODING = "entity";
    protected static final String MISSING_VARIABLE_VALUE = "(none)";

    @Override // org.apache.catalina.ssi.SSICommand
    public long process(SSIMediator ssiMediator, String commandName, String[] paramNames, String[] paramValues, PrintWriter writer) {
        String encoding = DEFAULT_ENCODING;
        String originalValue = null;
        String errorMessage = ssiMediator.getConfigErrMsg();
        for (int i = 0; i < paramNames.length; i++) {
            String paramName = paramNames[i];
            String paramValue = paramValues[i];
            if (paramName.equalsIgnoreCase("var")) {
                originalValue = paramValue;
            } else if (paramName.equalsIgnoreCase(XMLDeclaration.ATTRIBUTE_NAME_ENCODING)) {
                if (isValidEncoding(paramValue)) {
                    encoding = paramValue;
                } else {
                    ssiMediator.log("#echo--Invalid encoding: " + paramValue);
                    writer.write(errorMessage);
                }
            } else {
                ssiMediator.log("#echo--Invalid attribute: " + paramName);
                writer.write(errorMessage);
            }
        }
        String variableValue = ssiMediator.getVariableValue(originalValue, encoding);
        if (variableValue == null) {
            variableValue = MISSING_VARIABLE_VALUE;
        }
        writer.write(variableValue);
        return System.currentTimeMillis();
    }

    protected boolean isValidEncoding(String encoding) {
        return encoding.equalsIgnoreCase(SpringInputGeneralFieldTagProcessor.URL_INPUT_TYPE_ATTR_VALUE) || encoding.equalsIgnoreCase(DEFAULT_ENCODING) || encoding.equalsIgnoreCase("none");
    }
}