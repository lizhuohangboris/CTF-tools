package org.thymeleaf.context;

import java.util.HashMap;
import java.util.Map;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/context/IdentifierSequences.class */
public final class IdentifierSequences {
    private final Map<String, Integer> idCounts = new HashMap(1, 1.0f);

    public Integer getAndIncrementIDSeq(String id) {
        Validate.notNull(id, "ID cannot be null");
        Integer count = this.idCounts.get(id);
        if (count == null) {
            count = 1;
        }
        this.idCounts.put(id, Integer.valueOf(count.intValue() + 1));
        return count;
    }

    public Integer getNextIDSeq(String id) {
        Validate.notNull(id, "ID cannot be null");
        Integer count = this.idCounts.get(id);
        if (count == null) {
            count = 1;
        }
        return count;
    }

    public Integer getPreviousIDSeq(String id) {
        Validate.notNull(id, "ID cannot be null");
        Integer count = this.idCounts.get(id);
        if (count == null) {
            throw new TemplateProcessingException("Cannot obtain previous ID count for ID \"" + id + "\"");
        }
        return Integer.valueOf(count.intValue() - 1);
    }
}