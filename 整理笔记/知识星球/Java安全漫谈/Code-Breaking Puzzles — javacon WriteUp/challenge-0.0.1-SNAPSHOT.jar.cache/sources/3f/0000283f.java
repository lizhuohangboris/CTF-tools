package org.thymeleaf.exceptions;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/exceptions/TemplateProcessingException.class */
public class TemplateProcessingException extends TemplateEngineException {
    private static final long serialVersionUID = 5985749439214775193L;
    private String templateName;
    private Integer line;
    private Integer col;

    public TemplateProcessingException(String message) {
        this(message, null);
    }

    public TemplateProcessingException(String message, Throwable cause) {
        this(message, null, cause);
    }

    public TemplateProcessingException(String message, String templateName, Throwable cause) {
        super(message, cause);
        this.templateName = templateName;
        this.line = null;
        this.col = null;
    }

    public TemplateProcessingException(String message, String templateName, int line, int col) {
        super(message);
        this.templateName = templateName;
        this.line = line < 0 ? null : Integer.valueOf(line);
        this.col = col < 0 ? null : Integer.valueOf(col);
    }

    public TemplateProcessingException(String message, String templateName, int line, int col, Throwable cause) {
        super(message, cause);
        this.templateName = templateName;
        this.line = line < 0 ? null : Integer.valueOf(line);
        this.col = col < 0 ? null : Integer.valueOf(col);
    }

    public String getTemplateName() {
        return this.templateName;
    }

    public boolean hasTemplateName() {
        return this.templateName != null;
    }

    public Integer getLine() {
        return this.line;
    }

    public Integer getCol() {
        return this.col;
    }

    public boolean hasLineAndCol() {
        return (this.line == null || this.col == null) ? false : true;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setLineAndCol(int line, int col) {
        this.line = line < 0 ? null : Integer.valueOf(line);
        this.col = col < 0 ? null : Integer.valueOf(col);
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getMessage());
        if (this.templateName != null) {
            sb.append(' ');
            sb.append('(');
            sb.append("template: \"");
            sb.append(this.templateName);
            sb.append('\"');
            if (this.line != null || this.col != null) {
                sb.append(" - ");
                if (this.line != null) {
                    sb.append("line ");
                    sb.append(this.line);
                }
                if (this.col != null) {
                    sb.append(", col ");
                    sb.append(this.col);
                }
            }
            sb.append(')');
        }
        return sb.toString();
    }
}