package org.thymeleaf.expression;

import java.math.BigDecimal;
import org.thymeleaf.util.AggregateUtils;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/expression/Aggregates.class */
public final class Aggregates {
    public BigDecimal sum(Iterable<? extends Number> target) {
        return AggregateUtils.sum(target);
    }

    public BigDecimal sum(Number[] target) {
        return AggregateUtils.sum(target);
    }

    public BigDecimal sum(byte[] target) {
        return AggregateUtils.sum(target);
    }

    public BigDecimal sum(short[] target) {
        return AggregateUtils.sum(target);
    }

    public BigDecimal sum(int[] target) {
        return AggregateUtils.sum(target);
    }

    public BigDecimal sum(long[] target) {
        return AggregateUtils.sum(target);
    }

    public BigDecimal sum(float[] target) {
        return AggregateUtils.sum(target);
    }

    public BigDecimal sum(double[] target) {
        return AggregateUtils.sum(target);
    }

    public BigDecimal avg(Iterable<? extends Number> target) {
        return AggregateUtils.avg(target);
    }

    public BigDecimal avg(Number[] target) {
        return AggregateUtils.avg(target);
    }

    public BigDecimal avg(byte[] target) {
        return AggregateUtils.avg(target);
    }

    public BigDecimal avg(short[] target) {
        return AggregateUtils.avg(target);
    }

    public BigDecimal avg(int[] target) {
        return AggregateUtils.avg(target);
    }

    public BigDecimal avg(long[] target) {
        return AggregateUtils.avg(target);
    }

    public BigDecimal avg(float[] target) {
        return AggregateUtils.avg(target);
    }

    public BigDecimal avg(double[] target) {
        return AggregateUtils.avg(target);
    }
}