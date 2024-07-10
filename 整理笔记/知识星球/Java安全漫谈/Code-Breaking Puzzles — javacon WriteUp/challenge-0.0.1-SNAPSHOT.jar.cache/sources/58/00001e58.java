package org.springframework.core.env;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Predicate;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/env/ProfilesParser.class */
public final class ProfilesParser {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/env/ProfilesParser$Operator.class */
    public enum Operator {
        AND,
        OR
    }

    private ProfilesParser() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Profiles parse(String... expressions) {
        Assert.notEmpty(expressions, "Must specify at least one profile");
        Profiles[] parsed = new Profiles[expressions.length];
        for (int i = 0; i < expressions.length; i++) {
            parsed[i] = parseExpression(expressions[i]);
        }
        return new ParsedProfiles(expressions, parsed);
    }

    private static Profiles parseExpression(String expression) {
        Assert.hasText(expression, () -> {
            return "Invalid profile expression [" + expression + "]: must contain text";
        });
        StringTokenizer tokens = new StringTokenizer(expression, "()&|!", true);
        return parseTokens(expression, tokens);
    }

    private static Profiles parseTokens(String expression, StringTokenizer tokens) {
        List<Profiles> elements = new ArrayList<>();
        Operator operator = null;
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken().trim();
            if (!token.isEmpty()) {
                boolean z = true;
                switch (token.hashCode()) {
                    case 33:
                        if (token.equals("!")) {
                            z = true;
                            break;
                        }
                        break;
                    case 38:
                        if (token.equals(BeanFactory.FACTORY_BEAN_PREFIX)) {
                            z = true;
                            break;
                        }
                        break;
                    case 40:
                        if (token.equals("(")) {
                            z = false;
                            break;
                        }
                        break;
                    case 41:
                        if (token.equals(")")) {
                            z = true;
                            break;
                        }
                        break;
                    case 124:
                        if (token.equals("|")) {
                            z = true;
                            break;
                        }
                        break;
                }
                switch (z) {
                    case false:
                        elements.add(parseTokens(expression, tokens));
                        continue;
                    case true:
                        assertWellFormed(expression, operator == null || operator == Operator.AND);
                        operator = Operator.AND;
                        continue;
                    case true:
                        assertWellFormed(expression, operator == null || operator == Operator.OR);
                        operator = Operator.OR;
                        continue;
                    case true:
                        elements.add(not(parseTokens(expression, tokens)));
                        continue;
                    case true:
                        Profiles merged = merge(expression, elements, operator);
                        elements.clear();
                        elements.add(merged);
                        operator = null;
                        continue;
                    default:
                        elements.add(equals(token));
                        continue;
                }
            }
        }
        return merge(expression, elements, operator);
    }

    private static Profiles merge(String expression, List<Profiles> elements, @Nullable Operator operator) {
        assertWellFormed(expression, !elements.isEmpty());
        if (elements.size() == 1) {
            return elements.get(0);
        }
        Profiles[] profiles = (Profiles[]) elements.toArray(new Profiles[0]);
        return operator == Operator.AND ? and(profiles) : or(profiles);
    }

    private static void assertWellFormed(String expression, boolean wellFormed) {
        Assert.isTrue(wellFormed, () -> {
            return "Malformed profile expression [" + expression + "]";
        });
    }

    private static Profiles or(Profiles... profiles) {
        return activeProfile -> {
            return Arrays.stream(profiles).anyMatch(isMatch(activeProfile));
        };
    }

    private static Profiles and(Profiles... profiles) {
        return activeProfile -> {
            return Arrays.stream(profiles).allMatch(isMatch(activeProfile));
        };
    }

    private static Profiles not(Profiles profiles) {
        return activeProfile -> {
            return !profiles.matches(activeProfile);
        };
    }

    private static Profiles equals(String profile) {
        return activeProfile -> {
            return activeProfile.test(profile);
        };
    }

    private static Predicate<Profiles> isMatch(Predicate<String> activeProfile) {
        return profiles -> {
            return profiles.matches(activeProfile);
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/core/env/ProfilesParser$ParsedProfiles.class */
    public static class ParsedProfiles implements Profiles {
        private final String[] expressions;
        private final Profiles[] parsed;

        ParsedProfiles(String[] expressions, Profiles[] parsed) {
            this.expressions = expressions;
            this.parsed = parsed;
        }

        @Override // org.springframework.core.env.Profiles
        public boolean matches(Predicate<String> activeProfiles) {
            Profiles[] profilesArr;
            for (Profiles candidate : this.parsed) {
                if (candidate.matches(activeProfiles)) {
                    return true;
                }
            }
            return false;
        }

        public String toString() {
            return StringUtils.arrayToDelimitedString(this.expressions, " or ");
        }
    }
}