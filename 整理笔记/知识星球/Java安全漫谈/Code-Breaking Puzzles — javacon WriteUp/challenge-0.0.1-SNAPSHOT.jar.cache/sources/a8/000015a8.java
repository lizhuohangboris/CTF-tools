package org.springframework.boot.autoconfigure.condition;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/condition/ConditionMessage.class */
public final class ConditionMessage {
    private String message;

    private ConditionMessage() {
        this(null);
    }

    private ConditionMessage(String message) {
        this.message = message;
    }

    private ConditionMessage(ConditionMessage prior, String message) {
        this.message = prior.isEmpty() ? message : prior + "; " + message;
    }

    public boolean isEmpty() {
        return !StringUtils.hasLength(this.message);
    }

    public boolean equals(Object obj) {
        if (obj == null || !ConditionMessage.class.isInstance(obj)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        return ObjectUtils.nullSafeEquals(((ConditionMessage) obj).message, this.message);
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.message);
    }

    public String toString() {
        return this.message != null ? this.message : "";
    }

    public ConditionMessage append(String message) {
        if (!StringUtils.hasLength(message)) {
            return this;
        }
        if (!StringUtils.hasLength(this.message)) {
            return new ConditionMessage(message);
        }
        return new ConditionMessage(this.message + " " + message);
    }

    public Builder andCondition(Class<? extends Annotation> condition, Object... details) {
        Assert.notNull(condition, "Condition must not be null");
        return andCondition("@" + ClassUtils.getShortName(condition), details);
    }

    public Builder andCondition(String condition, Object... details) {
        Assert.notNull(condition, "Condition must not be null");
        String detail = StringUtils.arrayToDelimitedString(details, " ");
        if (StringUtils.hasLength(detail)) {
            return new Builder(condition + " " + detail);
        }
        return new Builder(condition);
    }

    public static ConditionMessage empty() {
        return new ConditionMessage();
    }

    public static ConditionMessage of(String message, Object... args) {
        if (ObjectUtils.isEmpty(args)) {
            return new ConditionMessage(message);
        }
        return new ConditionMessage(String.format(message, args));
    }

    public static ConditionMessage of(Collection<? extends ConditionMessage> messages) {
        ConditionMessage result = new ConditionMessage();
        if (messages != null) {
            for (ConditionMessage message : messages) {
                result = new ConditionMessage(result, message.toString());
            }
        }
        return result;
    }

    public static Builder forCondition(Class<? extends Annotation> condition, Object... details) {
        return new ConditionMessage().andCondition(condition, details);
    }

    public static Builder forCondition(String condition, Object... details) {
        return new ConditionMessage().andCondition(condition, details);
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/condition/ConditionMessage$Builder.class */
    public final class Builder {
        private final String condition;

        private Builder(String condition) {
            ConditionMessage.this = this$0;
            this.condition = condition;
        }

        public ConditionMessage foundExactly(Object result) {
            return found("").items(result);
        }

        public ItemsBuilder found(String article) {
            return found(article, article);
        }

        public ItemsBuilder found(String singular, String plural) {
            return new ItemsBuilder(this, "found", singular, plural);
        }

        public ItemsBuilder didNotFind(String article) {
            return didNotFind(article, article);
        }

        public ItemsBuilder didNotFind(String singular, String plural) {
            return new ItemsBuilder(this, "did not find", singular, plural);
        }

        public ConditionMessage resultedIn(Object result) {
            return because("resulted in " + result);
        }

        public ConditionMessage available(String item) {
            return because(item + " is available");
        }

        public ConditionMessage notAvailable(String item) {
            return because(item + " is not available");
        }

        public ConditionMessage because(String reason) {
            if (StringUtils.isEmpty(reason)) {
                return new ConditionMessage(this.condition);
            }
            return new ConditionMessage(this.condition + (StringUtils.isEmpty(this.condition) ? "" : " ") + reason);
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/condition/ConditionMessage$ItemsBuilder.class */
    public final class ItemsBuilder {
        private final Builder condition;
        private final String reason;
        private final String singular;
        private final String plural;

        private ItemsBuilder(Builder condition, String reason, String singular, String plural) {
            ConditionMessage.this = this$0;
            this.condition = condition;
            this.reason = reason;
            this.singular = singular;
            this.plural = plural;
        }

        public ConditionMessage atAll() {
            return items(Collections.emptyList());
        }

        public ConditionMessage items(Object... items) {
            return items(Style.NORMAL, items);
        }

        public ConditionMessage items(Style style, Object... items) {
            return items(style, items != null ? Arrays.asList(items) : null);
        }

        public ConditionMessage items(Collection<?> items) {
            return items(Style.NORMAL, items);
        }

        public ConditionMessage items(Style style, Collection<?> items) {
            Assert.notNull(style, "Style must not be null");
            StringBuilder message = new StringBuilder(this.reason);
            Collection<?> items2 = style.applyTo(items);
            if ((this.condition == null || items2.size() <= 1) && StringUtils.hasLength(this.singular)) {
                message.append(" " + this.singular);
            } else if (StringUtils.hasLength(this.plural)) {
                message.append(" " + this.plural);
            }
            if (items2 != null && !items2.isEmpty()) {
                message.append(" " + StringUtils.collectionToDelimitedString(items2, ", "));
            }
            return this.condition.because(message.toString());
        }
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.1.0.RELEASE.jar:org/springframework/boot/autoconfigure/condition/ConditionMessage$Style.class */
    public enum Style {
        NORMAL { // from class: org.springframework.boot.autoconfigure.condition.ConditionMessage.Style.1
            @Override // org.springframework.boot.autoconfigure.condition.ConditionMessage.Style
            protected Object applyToItem(Object item) {
                return item;
            }
        },
        QUOTE { // from class: org.springframework.boot.autoconfigure.condition.ConditionMessage.Style.2
            @Override // org.springframework.boot.autoconfigure.condition.ConditionMessage.Style
            public String applyToItem(Object item) {
                if (item != null) {
                    return "'" + item + "'";
                }
                return null;
            }
        };

        protected abstract Object applyToItem(Object item);

        public Collection<?> applyTo(Collection<?> items) {
            List<Object> result = new ArrayList<>();
            for (Object item : items) {
                result.add(applyToItem(item));
            }
            return result;
        }
    }
}