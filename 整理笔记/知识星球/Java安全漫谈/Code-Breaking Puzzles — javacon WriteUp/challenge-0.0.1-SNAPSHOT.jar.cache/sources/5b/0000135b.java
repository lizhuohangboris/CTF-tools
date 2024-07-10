package org.springframework.asm;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-core-5.1.2.RELEASE.jar:org/springframework/asm/Edge.class */
public final class Edge {
    static final int JUMP = 0;
    static final int EXCEPTION = Integer.MAX_VALUE;
    final int info;
    final Label successor;
    Edge nextEdge;

    public Edge(int info, Label successor, Edge nextEdge) {
        this.info = info;
        this.successor = successor;
        this.nextEdge = nextEdge;
    }
}