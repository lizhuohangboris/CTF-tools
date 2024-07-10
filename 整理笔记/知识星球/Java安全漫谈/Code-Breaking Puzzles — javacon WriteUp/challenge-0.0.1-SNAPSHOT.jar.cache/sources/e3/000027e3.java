package org.thymeleaf.engine;

import java.io.IOException;
import java.io.Writer;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IModelVisitor;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/Comment.class */
public final class Comment extends AbstractTextualTemplateEvent implements IComment {
    private static final String COMMENT_PREFIX = "<!--";
    private static final String COMMENT_SUFFIX = "-->";
    final String prefix;
    final String suffix;
    private volatile String computedCommentStr;

    /* JADX INFO: Access modifiers changed from: package-private */
    public Comment(CharSequence content) {
        this(COMMENT_PREFIX, content, COMMENT_SUFFIX);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Comment(String prefix, CharSequence content, String suffix) {
        super(content);
        this.computedCommentStr = null;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    Comment(CharSequence content, String templateName, int line, int col) {
        this(COMMENT_PREFIX, content, COMMENT_SUFFIX, templateName, line, col);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Comment(String prefix, CharSequence content, String suffix, String templateName, int line, int col) {
        super(content, templateName, line, col);
        this.computedCommentStr = null;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override // org.thymeleaf.model.IComment
    public String getComment() {
        String c = this.computedCommentStr;
        if (c == null) {
            String str = this.prefix + getContentText() + this.suffix;
            c = str;
            this.computedCommentStr = str;
        }
        return c;
    }

    @Override // org.thymeleaf.model.IComment
    public String getContent() {
        return getContentText();
    }

    @Override // java.lang.CharSequence
    public int length() {
        return this.prefix.length() + getContentLength() + this.suffix.length();
    }

    @Override // java.lang.CharSequence
    public char charAt(int index) {
        if (index < this.prefix.length()) {
            return this.prefix.charAt(index);
        }
        int prefixedContentLen = this.prefix.length() + getContentLength();
        if (index >= prefixedContentLen) {
            return this.suffix.charAt(index - prefixedContentLen);
        }
        return charAtContent(index - this.prefix.length());
    }

    @Override // java.lang.CharSequence
    public CharSequence subSequence(int start, int end) {
        if (start >= this.prefix.length() && end < this.prefix.length() + getContentLength()) {
            return contentSubSequence(start - this.prefix.length(), end - this.prefix.length());
        }
        return getComment().subSequence(start, end);
    }

    @Override // org.thymeleaf.model.ITemplateEvent
    public void accept(IModelVisitor visitor) {
        visitor.visit(this);
    }

    @Override // org.thymeleaf.model.ITemplateEvent
    public void write(Writer writer) throws IOException {
        writer.write(this.prefix);
        writeContent(writer);
        writer.write(this.suffix);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Comment asEngineComment(IComment comment) {
        if (comment instanceof Comment) {
            return (Comment) comment;
        }
        return new Comment(comment.getContent(), comment.getTemplateName(), comment.getLine(), comment.getCol());
    }

    @Override // org.thymeleaf.engine.IEngineTemplateEvent
    public void beHandled(ITemplateHandler handler) {
        handler.handleComment(this);
    }

    @Override // org.thymeleaf.engine.AbstractTextualTemplateEvent, java.lang.CharSequence
    public String toString() {
        return getComment();
    }
}