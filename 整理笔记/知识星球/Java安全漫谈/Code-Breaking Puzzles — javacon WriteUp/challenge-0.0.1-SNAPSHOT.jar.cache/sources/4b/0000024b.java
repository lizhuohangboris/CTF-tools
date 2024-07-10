package com.fasterxml.jackson.annotation;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@JacksonAnnotation
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-annotations-2.9.0.jar:com/fasterxml/jackson/annotation/JacksonInject.class */
public @interface JacksonInject {
    String value() default "";

    OptBoolean useInput() default OptBoolean.DEFAULT;

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/jackson-annotations-2.9.0.jar:com/fasterxml/jackson/annotation/JacksonInject$Value.class */
    public static class Value implements JacksonAnnotationValue<JacksonInject>, Serializable {
        private static final long serialVersionUID = 1;
        protected static final Value EMPTY = new Value(null, null);
        protected final Object _id;
        protected final Boolean _useInput;

        protected Value(Object id, Boolean useInput) {
            this._id = id;
            this._useInput = useInput;
        }

        @Override // com.fasterxml.jackson.annotation.JacksonAnnotationValue
        public Class<JacksonInject> valueFor() {
            return JacksonInject.class;
        }

        public static Value empty() {
            return EMPTY;
        }

        public static Value construct(Object id, Boolean useInput) {
            if ("".equals(id)) {
                id = null;
            }
            if (_empty(id, useInput)) {
                return EMPTY;
            }
            return new Value(id, useInput);
        }

        public static Value from(JacksonInject src) {
            if (src == null) {
                return EMPTY;
            }
            return construct(src.value(), src.useInput().asBoolean());
        }

        public static Value forId(Object id) {
            return construct(id, null);
        }

        public Value withId(Object id) {
            if (id == null) {
                if (this._id == null) {
                    return this;
                }
            } else if (id.equals(this._id)) {
                return this;
            }
            return new Value(id, this._useInput);
        }

        public Value withUseInput(Boolean useInput) {
            if (useInput == null) {
                if (this._useInput == null) {
                    return this;
                }
            } else if (useInput.equals(this._useInput)) {
                return this;
            }
            return new Value(this._id, useInput);
        }

        public Object getId() {
            return this._id;
        }

        public Boolean getUseInput() {
            return this._useInput;
        }

        public boolean hasId() {
            return this._id != null;
        }

        public boolean willUseInput(boolean defaultSetting) {
            return this._useInput == null ? defaultSetting : this._useInput.booleanValue();
        }

        public String toString() {
            return String.format("JacksonInject.Value(id=%s,useInput=%s)", this._id, this._useInput);
        }

        public int hashCode() {
            int h = 1;
            if (this._id != null) {
                h = 1 + this._id.hashCode();
            }
            if (this._useInput != null) {
                h += this._useInput.hashCode();
            }
            return h;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o != null && o.getClass() == getClass()) {
                Value other = (Value) o;
                if (OptBoolean.equals(this._useInput, other._useInput)) {
                    if (this._id == null) {
                        return other._id == null;
                    }
                    return this._id.equals(other._id);
                }
                return false;
            }
            return false;
        }

        private static boolean _empty(Object id, Boolean useInput) {
            return id == null && useInput == null;
        }
    }
}