package org.apache.catalina.ssi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import org.apache.catalina.util.Strftime;
import org.apache.catalina.util.URLEncoder;
import org.apache.tomcat.util.security.Escape;
import org.springframework.beans.factory.BeanFactory;
import org.thymeleaf.spring5.processor.SpringInputGeneralFieldTagProcessor;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/catalina/ssi/SSIMediator.class */
public class SSIMediator {
    protected static final String DEFAULT_CONFIG_ERR_MSG = "[an error occurred while processing this directive]";
    protected static final String DEFAULT_CONFIG_TIME_FMT = "%A, %d-%b-%Y %T %Z";
    protected static final String DEFAULT_CONFIG_SIZE_FMT = "abbrev";
    protected final SSIExternalResolver ssiExternalResolver;
    protected final long lastModifiedDate;
    protected Strftime strftime;
    protected String configErrMsg = DEFAULT_CONFIG_ERR_MSG;
    protected String configTimeFmt = DEFAULT_CONFIG_TIME_FMT;
    protected String configSizeFmt = DEFAULT_CONFIG_SIZE_FMT;
    protected final String className = getClass().getName();
    protected final SSIConditionalState conditionalState = new SSIConditionalState();
    protected int lastMatchCount = 0;

    public SSIMediator(SSIExternalResolver ssiExternalResolver, long lastModifiedDate) {
        this.ssiExternalResolver = ssiExternalResolver;
        this.lastModifiedDate = lastModifiedDate;
        setConfigTimeFmt(DEFAULT_CONFIG_TIME_FMT, true);
    }

    public void setConfigErrMsg(String configErrMsg) {
        this.configErrMsg = configErrMsg;
    }

    public void setConfigTimeFmt(String configTimeFmt) {
        setConfigTimeFmt(configTimeFmt, false);
    }

    public void setConfigTimeFmt(String configTimeFmt, boolean fromConstructor) {
        this.configTimeFmt = configTimeFmt;
        this.strftime = new Strftime(configTimeFmt, Locale.US);
        setDateVariables(fromConstructor);
    }

    public void setConfigSizeFmt(String configSizeFmt) {
        this.configSizeFmt = configSizeFmt;
    }

    public String getConfigErrMsg() {
        return this.configErrMsg;
    }

    public String getConfigTimeFmt() {
        return this.configTimeFmt;
    }

    public String getConfigSizeFmt() {
        return this.configSizeFmt;
    }

    public SSIConditionalState getConditionalState() {
        return this.conditionalState;
    }

    public Collection<String> getVariableNames() {
        Set<String> variableNames = new HashSet<>();
        variableNames.add("DATE_GMT");
        variableNames.add("DATE_LOCAL");
        variableNames.add("LAST_MODIFIED");
        this.ssiExternalResolver.addVariableNames(variableNames);
        Iterator<String> iter = variableNames.iterator();
        while (iter.hasNext()) {
            String name = iter.next();
            if (isNameReserved(name)) {
                iter.remove();
            }
        }
        return variableNames;
    }

    public long getFileSize(String path, boolean virtual) throws IOException {
        return this.ssiExternalResolver.getFileSize(path, virtual);
    }

    public long getFileLastModified(String path, boolean virtual) throws IOException {
        return this.ssiExternalResolver.getFileLastModified(path, virtual);
    }

    public String getFileText(String path, boolean virtual) throws IOException {
        return this.ssiExternalResolver.getFileText(path, virtual);
    }

    protected boolean isNameReserved(String name) {
        return name.startsWith(this.className + ".");
    }

    public String getVariableValue(String variableName) {
        return getVariableValue(variableName, "none");
    }

    public void setVariableValue(String variableName, String variableValue) {
        if (!isNameReserved(variableName)) {
            this.ssiExternalResolver.setVariableValue(variableName, variableValue);
        }
    }

    public String getVariableValue(String variableName, String encoding) {
        String lowerCaseVariableName = variableName.toLowerCase(Locale.ENGLISH);
        String variableValue = null;
        if (!isNameReserved(lowerCaseVariableName)) {
            variableValue = this.ssiExternalResolver.getVariableValue(variableName);
            if (variableValue == null) {
                variableValue = this.ssiExternalResolver.getVariableValue(this.className + "." + variableName.toUpperCase(Locale.ENGLISH));
            }
            if (variableValue != null) {
                variableValue = encode(variableValue, encoding);
            }
        }
        return variableValue;
    }

    public String substituteVariables(String val) {
        int charEnd;
        if (val.indexOf(36) >= 0 || val.indexOf(38) >= 0) {
            StringBuilder sb = new StringBuilder(val.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&amp;", BeanFactory.FACTORY_BEAN_PREFIX));
            int indexOf = sb.indexOf("&#");
            while (true) {
                int charStart = indexOf;
                if (charStart <= -1 || (charEnd = sb.indexOf(";", charStart)) <= -1) {
                    break;
                }
                char c = (char) Integer.parseInt(sb.substring(charStart + 2, charEnd));
                sb.delete(charStart, charEnd + 1);
                sb.insert(charStart, c);
                indexOf = sb.indexOf("&#");
            }
            int i = 0;
            while (i < sb.length()) {
                while (true) {
                    if (i >= sb.length()) {
                        break;
                    } else if (sb.charAt(i) != '$') {
                        i++;
                    } else {
                        i++;
                        break;
                    }
                }
                if (i == sb.length()) {
                    break;
                } else if (i > 1 && sb.charAt(i - 2) == '\\') {
                    sb.deleteCharAt(i - 2);
                    i--;
                } else {
                    int nameStart = i;
                    int start = i - 1;
                    char endChar = ' ';
                    if (sb.charAt(i) == '{') {
                        nameStart++;
                        endChar = '}';
                    }
                    while (i < sb.length() && sb.charAt(i) != endChar) {
                        i++;
                    }
                    int end = i;
                    if (endChar == '}') {
                        end++;
                    }
                    String varName = sb.substring(nameStart, end);
                    String value = getVariableValue(varName);
                    if (value == null) {
                        value = "";
                    }
                    sb.replace(start, end, value);
                    i = start + value.length();
                }
            }
            return sb.toString();
        }
        return val;
    }

    protected String formatDate(Date date, TimeZone timeZone) {
        String retVal;
        if (timeZone != null) {
            TimeZone oldTimeZone = this.strftime.getTimeZone();
            this.strftime.setTimeZone(timeZone);
            retVal = this.strftime.format(date);
            this.strftime.setTimeZone(oldTimeZone);
        } else {
            retVal = this.strftime.format(date);
        }
        return retVal;
    }

    protected String encode(String value, String encoding) {
        String retVal;
        if (encoding.equalsIgnoreCase(SpringInputGeneralFieldTagProcessor.URL_INPUT_TYPE_ATTR_VALUE)) {
            retVal = URLEncoder.DEFAULT.encode(value, StandardCharsets.UTF_8);
        } else if (encoding.equalsIgnoreCase("none")) {
            retVal = value;
        } else if (encoding.equalsIgnoreCase("entity")) {
            retVal = Escape.htmlElementContent(value);
        } else {
            throw new IllegalArgumentException("Unknown encoding: " + encoding);
        }
        return retVal;
    }

    public void log(String message) {
        this.ssiExternalResolver.log(message, null);
    }

    public void log(String message, Throwable throwable) {
        this.ssiExternalResolver.log(message, throwable);
    }

    protected void setDateVariables(boolean fromConstructor) {
        boolean alreadySet = this.ssiExternalResolver.getVariableValue(new StringBuilder().append(this.className).append(".alreadyset").toString()) != null;
        if (!fromConstructor || !alreadySet) {
            this.ssiExternalResolver.setVariableValue(this.className + ".alreadyset", "true");
            Date date = new Date();
            TimeZone timeZone = TimeZone.getTimeZone("GMT");
            String retVal = formatDate(date, timeZone);
            setVariableValue("DATE_GMT", null);
            this.ssiExternalResolver.setVariableValue(this.className + ".DATE_GMT", retVal);
            String retVal2 = formatDate(date, null);
            setVariableValue("DATE_LOCAL", null);
            this.ssiExternalResolver.setVariableValue(this.className + ".DATE_LOCAL", retVal2);
            String retVal3 = formatDate(new Date(this.lastModifiedDate), null);
            setVariableValue("LAST_MODIFIED", null);
            this.ssiExternalResolver.setVariableValue(this.className + ".LAST_MODIFIED", retVal3);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void clearMatchGroups() {
        for (int i = 1; i <= this.lastMatchCount; i++) {
            setVariableValue(Integer.toString(i), "");
        }
        this.lastMatchCount = 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void populateMatchGroups(Matcher matcher) {
        this.lastMatchCount = matcher.groupCount();
        if (this.lastMatchCount == 0) {
            return;
        }
        for (int i = 1; i <= this.lastMatchCount; i++) {
            setVariableValue(Integer.toString(i), matcher.group(i));
        }
    }
}