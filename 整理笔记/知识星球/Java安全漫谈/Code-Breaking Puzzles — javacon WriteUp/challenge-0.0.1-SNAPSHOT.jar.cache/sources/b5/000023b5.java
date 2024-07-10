package org.springframework.util.xml;

import java.io.BufferedReader;
import java.io.CharConversionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/util/xml/XmlValidationModeDetector.class */
public class XmlValidationModeDetector {
    public static final int VALIDATION_NONE = 0;
    public static final int VALIDATION_AUTO = 1;
    public static final int VALIDATION_DTD = 2;
    public static final int VALIDATION_XSD = 3;
    private static final String DOCTYPE = "DOCTYPE";
    private static final String START_COMMENT = "<!--";
    private static final String END_COMMENT = "-->";
    private boolean inComment;

    public int detectValidationMode(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        boolean isDtdValidated = false;
        while (true) {
            try {
                String content = reader.readLine();
                if (content == null) {
                    break;
                }
                String content2 = consumeCommentTokens(content);
                if (!this.inComment && StringUtils.hasText(content2)) {
                    if (hasDoctype(content2)) {
                        isDtdValidated = true;
                        break;
                    } else if (hasOpeningTag(content2)) {
                        break;
                    }
                }
            } catch (CharConversionException e) {
                reader.close();
                return 1;
            } catch (Throwable th) {
                reader.close();
                throw th;
            }
        }
        int i = isDtdValidated ? 2 : 3;
        reader.close();
        return i;
    }

    private boolean hasDoctype(String content) {
        return content.contains("DOCTYPE");
    }

    private boolean hasOpeningTag(String content) {
        int openTagIndex;
        return !this.inComment && (openTagIndex = content.indexOf(60)) > -1 && content.length() > openTagIndex + 1 && Character.isLetter(content.charAt(openTagIndex + 1));
    }

    @Nullable
    private String consumeCommentTokens(String line) {
        if (!line.contains(START_COMMENT) && !line.contains(END_COMMENT)) {
            return line;
        }
        String currLine = line;
        while (true) {
            String consume = consume(currLine);
            currLine = consume;
            if (consume != null) {
                if (!this.inComment && !currLine.trim().startsWith(START_COMMENT)) {
                    return currLine;
                }
            } else {
                return null;
            }
        }
    }

    @Nullable
    private String consume(String line) {
        int index = this.inComment ? endComment(line) : startComment(line);
        if (index == -1) {
            return null;
        }
        return line.substring(index);
    }

    private int startComment(String line) {
        return commentToken(line, START_COMMENT, true);
    }

    private int endComment(String line) {
        return commentToken(line, END_COMMENT, false);
    }

    private int commentToken(String line, String token, boolean inCommentIfPresent) {
        int index = line.indexOf(token);
        if (index > -1) {
            this.inComment = inCommentIfPresent;
        }
        return index == -1 ? index : index + token.length();
    }
}