package org.springframework.jmx.export.metadata;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/spring-context-5.1.2.RELEASE.jar:org/springframework/jmx/export/metadata/AbstractJmxAttribute.class */
public class AbstractJmxAttribute {
    private String description = "";
    private int currencyTimeLimit = -1;

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setCurrencyTimeLimit(int currencyTimeLimit) {
        this.currencyTimeLimit = currencyTimeLimit;
    }

    public int getCurrencyTimeLimit() {
        return this.currencyTimeLimit;
    }
}