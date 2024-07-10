package org.apache.tomcat.util.http;

import java.util.Enumeration;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: MimeHeaders.java */
/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/tomcat-embed-core-9.0.12.jar:org/apache/tomcat/util/http/NamesEnumerator.class */
public class NamesEnumerator implements Enumeration<String> {
    private int pos = 0;
    private final int size;
    private String next;
    private final MimeHeaders headers;

    public NamesEnumerator(MimeHeaders headers) {
        this.headers = headers;
        this.size = headers.size();
        findNext();
    }

    private void findNext() {
        this.next = null;
        while (this.pos < this.size) {
            this.next = this.headers.getName(this.pos).toString();
            int j = 0;
            while (true) {
                if (j >= this.pos) {
                    break;
                } else if (!this.headers.getName(j).equalsIgnoreCase(this.next)) {
                    j++;
                } else {
                    this.next = null;
                    break;
                }
            }
            if (this.next != null) {
                break;
            }
            this.pos++;
        }
        this.pos++;
    }

    @Override // java.util.Enumeration
    public boolean hasMoreElements() {
        return this.next != null;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.Enumeration
    public String nextElement() {
        String current = this.next;
        findNext();
        return current;
    }
}