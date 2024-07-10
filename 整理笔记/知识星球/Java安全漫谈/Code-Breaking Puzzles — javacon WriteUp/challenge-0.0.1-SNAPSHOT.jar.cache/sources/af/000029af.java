package org.thymeleaf.templateparser.markup.decoupled;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/templateparser/markup/decoupled/DecoupledInjectedAttribute.class */
public final class DecoupledInjectedAttribute {
    final char[] buffer;
    final int nameOffset;
    final int nameLen;
    final int operatorOffset;
    final int operatorLen;
    final int valueContentOffset;
    final int valueContentLen;
    final int valueOuterOffset;
    final int valueOuterLen;

    public static DecoupledInjectedAttribute createAttribute(char[] buffer, int nameOffset, int nameLen, int operatorOffset, int operatorLen, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen) {
        char[] newBuffer = new char[nameLen + operatorLen + valueOuterLen];
        System.arraycopy(buffer, nameOffset, newBuffer, 0, nameLen);
        System.arraycopy(buffer, operatorOffset, newBuffer, nameLen, operatorLen);
        System.arraycopy(buffer, valueOuterOffset, newBuffer, nameLen + operatorLen, valueOuterLen);
        return new DecoupledInjectedAttribute(newBuffer, 0, nameLen, operatorOffset - nameOffset, operatorLen, valueContentOffset - nameOffset, valueContentLen, valueOuterOffset - nameOffset, valueOuterLen);
    }

    private DecoupledInjectedAttribute(char[] buffer, int nameOffset, int nameLen, int operatorOffset, int operatorLen, int valueContentOffset, int valueContentLen, int valueOuterOffset, int valueOuterLen) {
        this.buffer = buffer;
        this.nameOffset = nameOffset;
        this.nameLen = nameLen;
        this.operatorOffset = operatorOffset;
        this.operatorLen = operatorLen;
        this.valueContentOffset = valueContentOffset;
        this.valueContentLen = valueContentLen;
        this.valueOuterOffset = valueOuterOffset;
        this.valueOuterLen = valueOuterLen;
    }

    public String getName() {
        return new String(this.buffer, this.nameOffset, this.nameLen);
    }

    public String getOperator() {
        return new String(this.buffer, this.operatorOffset, this.operatorLen);
    }

    public String getValueContent() {
        return new String(this.buffer, this.valueContentOffset, this.valueContentLen);
    }

    public String getValueOuter() {
        return new String(this.buffer, this.valueOuterOffset, this.valueOuterLen);
    }

    public String toString() {
        return new String(this.buffer);
    }
}