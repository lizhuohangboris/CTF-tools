package org.thymeleaf.standard.expression;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.thymeleaf.util.Validate;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/standard/expression/AssignationSequence.class */
public final class AssignationSequence implements Iterable<Assignation>, Serializable {
    private static final long serialVersionUID = -4915282307441011014L;
    private final List<Assignation> assignations;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AssignationSequence(List<Assignation> assignations) {
        Validate.notNull(assignations, "Assignation list cannot be null");
        Validate.containsNoNulls(assignations, "Assignation list cannot contain any nulls");
        this.assignations = Collections.unmodifiableList(assignations);
    }

    public List<Assignation> getAssignations() {
        return this.assignations;
    }

    public int size() {
        return this.assignations.size();
    }

    @Override // java.lang.Iterable
    public Iterator<Assignation> iterator() {
        return this.assignations.iterator();
    }

    public String getStringRepresentation() {
        StringBuilder sb = new StringBuilder();
        if (this.assignations.size() > 0) {
            sb.append(this.assignations.get(0));
            for (int i = 1; i < this.assignations.size(); i++) {
                sb.append(',');
                sb.append(this.assignations.get(i));
            }
        }
        return sb.toString();
    }

    public String toString() {
        return getStringRepresentation();
    }
}