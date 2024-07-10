package org.thymeleaf.engine;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.ICDATASection;
import org.thymeleaf.model.IComment;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.model.IText;
import org.thymeleaf.standard.expression.FragmentExpression;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/engine/EngineEventUtils.class */
public final class EngineEventUtils {
    public static boolean isWhitespace(IText text) {
        if (text == null) {
            return false;
        }
        if (text instanceof Text) {
            return ((Text) text).isWhitespace();
        }
        return computeWhitespace(text);
    }

    public static boolean isWhitespace(ICDATASection cdataSection) {
        if (cdataSection == null) {
            return false;
        }
        if (cdataSection instanceof CDATASection) {
            return ((CDATASection) cdataSection).isWhitespace();
        }
        return computeWhitespace(cdataSection.getContent());
    }

    public static boolean isWhitespace(IComment comment) {
        if (comment == null) {
            return false;
        }
        if (comment instanceof Comment) {
            return ((Comment) comment).isWhitespace();
        }
        return computeWhitespace(comment.getContent());
    }

    public static boolean isInlineable(IText text) {
        if (text == null) {
            return false;
        }
        if (text instanceof Text) {
            return ((Text) text).isInlineable();
        }
        return computeInlineable(text);
    }

    public static boolean isInlineable(ICDATASection cdataSection) {
        if (cdataSection == null) {
            return false;
        }
        if (cdataSection instanceof CDATASection) {
            return ((CDATASection) cdataSection).isInlineable();
        }
        return computeInlineable(cdataSection.getContent());
    }

    public static boolean isInlineable(IComment comment) {
        if (comment == null) {
            return false;
        }
        if (comment instanceof Comment) {
            return ((Comment) comment).isInlineable();
        }
        return computeInlineable(comment.getContent());
    }

    private static boolean computeWhitespace(CharSequence text) {
        char c;
        int n = text.length();
        if (n == 0) {
            return false;
        }
        do {
            int i = n;
            n--;
            if (i != 0) {
                c = text.charAt(n);
            } else {
                return true;
            }
        } while (Character.isWhitespace(c));
        return false;
    }

    private static boolean computeInlineable(CharSequence text) {
        int n = text.length();
        if (n == 0) {
            return false;
        }
        char c0 = 0;
        int inline = 0;
        while (true) {
            int i = n;
            n--;
            if (i != 0) {
                char c1 = text.charAt(n);
                if (c1 == ']' && c0 == ']') {
                    inline = 1;
                } else if (c1 == ')' && c0 == ']') {
                    inline = 2;
                } else if (inline == 1 && c1 == '[' && c0 == '[') {
                    return true;
                } else {
                    if (inline == 2 && c1 == '[' && c0 == '(') {
                        return true;
                    }
                }
                c0 = c1;
            } else {
                return false;
            }
        }
    }

    public static IStandardExpression computeAttributeExpression(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue) {
        if (!(tag instanceof AbstractProcessableElementTag)) {
            return parseAttributeExpression(context, attributeValue);
        }
        AbstractProcessableElementTag processableElementTag = (AbstractProcessableElementTag) tag;
        Attribute attribute = (Attribute) processableElementTag.getAttribute(attributeName);
        IStandardExpression expression = attribute.getCachedStandardExpression();
        if (expression != null) {
            return expression;
        }
        IStandardExpression expression2 = parseAttributeExpression(context, attributeValue);
        if (expression2 != null && !(expression2 instanceof FragmentExpression) && attributeValue.indexOf(95) < 0) {
            attribute.setCachedStandardExpression(expression2);
        }
        return expression2;
    }

    private static IStandardExpression parseAttributeExpression(ITemplateContext context, String attributeValue) {
        IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser(context.getConfiguration());
        return expressionParser.parseExpression(context, attributeValue);
    }

    private EngineEventUtils() {
    }
}