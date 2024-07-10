package org.yaml.snakeyaml.scanner;

import org.yaml.snakeyaml.tokens.Token;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/snakeyaml-1.23.jar:org/yaml/snakeyaml/scanner/Scanner.class */
public interface Scanner {
    boolean checkToken(Token.ID... idArr);

    Token peekToken();

    Token getToken();
}