package org.springframework.expression.spel.standard;

import org.springframework.expression.ParseException;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateAwareExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-expression-5.1.2.RELEASE.jar:org/springframework/expression/spel/standard/SpelExpressionParser.class */
public class SpelExpressionParser extends TemplateAwareExpressionParser {
    private final SpelParserConfiguration configuration;

    public SpelExpressionParser() {
        this.configuration = new SpelParserConfiguration();
    }

    public SpelExpressionParser(SpelParserConfiguration configuration) {
        Assert.notNull(configuration, "SpelParserConfiguration must not be null");
        this.configuration = configuration;
    }

    public SpelExpression parseRaw(String expressionString) throws ParseException {
        return doParseExpression(expressionString, (ParserContext) null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.springframework.expression.common.TemplateAwareExpressionParser
    public SpelExpression doParseExpression(String expressionString, @Nullable ParserContext context) throws ParseException {
        return new InternalSpelExpressionParser(this.configuration).doParseExpression(expressionString, context);
    }
}