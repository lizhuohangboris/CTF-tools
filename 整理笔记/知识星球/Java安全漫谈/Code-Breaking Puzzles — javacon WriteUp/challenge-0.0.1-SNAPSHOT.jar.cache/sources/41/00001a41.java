package org.springframework.boot.origin;

import org.springframework.core.io.Resource;
import org.springframework.util.ObjectUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/origin/TextResourceOrigin.class */
public class TextResourceOrigin implements Origin {
    private final Resource resource;
    private final Location location;

    public TextResourceOrigin(Resource resource, Location location) {
        this.resource = resource;
        this.location = location;
    }

    public Resource getResource() {
        return this.resource;
    }

    public Location getLocation() {
        return this.location;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof TextResourceOrigin) {
            TextResourceOrigin other = (TextResourceOrigin) obj;
            boolean result = 1 != 0 && ObjectUtils.nullSafeEquals(this.resource, other.resource);
            boolean result2 = result && ObjectUtils.nullSafeEquals(this.location, other.location);
            return result2;
        }
        return super.equals(obj);
    }

    public int hashCode() {
        int result = (31 * 1) + ObjectUtils.nullSafeHashCode(this.resource);
        return (31 * result) + ObjectUtils.nullSafeHashCode(this.location);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(this.resource != null ? this.resource.getDescription() : "unknown resource [?]");
        if (this.location != null) {
            result.append(":").append(this.location);
        }
        return result.toString();
    }

    /* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-boot-2.1.0.RELEASE.jar:org/springframework/boot/origin/TextResourceOrigin$Location.class */
    public static final class Location {
        private final int line;
        private final int column;

        public Location(int line, int column) {
            this.line = line;
            this.column = column;
        }

        public int getLine() {
            return this.line;
        }

        public int getColumn() {
            return this.column;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Location other = (Location) obj;
            boolean result = 1 != 0 && this.line == other.line;
            boolean result2 = result && this.column == other.column;
            return result2;
        }

        public int hashCode() {
            return (31 * this.line) + this.column;
        }

        public String toString() {
            return (this.line + 1) + ":" + (this.column + 1);
        }
    }
}