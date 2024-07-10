package org.thymeleaf.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* loaded from: challenge-0.0.1-SNAPSHOT.jar:BOOT-INF/lib/thymeleaf-3.0.11.RELEASE.jar:org/thymeleaf/util/NumberUtils.class */
public final class NumberUtils {
    public static String format(Number target, Integer minIntegerDigits, Locale locale) {
        if (target == null) {
            return null;
        }
        Validate.notNull(minIntegerDigits, "Minimum integer digits cannot be null");
        return formatNumber(target, minIntegerDigits, NumberPointType.NONE, 0, NumberPointType.NONE, locale);
    }

    public static String format(Number target, Integer minIntegerDigits, NumberPointType thousandsPointType, Locale locale) {
        if (target == null) {
            return null;
        }
        Validate.notNull(minIntegerDigits, "Minimum integer digits cannot be null");
        Validate.notNull(thousandsPointType, "Thousands point type cannot be null");
        return formatNumber(target, minIntegerDigits, thousandsPointType, 0, NumberPointType.NONE, locale);
    }

    public static String format(Number target, Integer minIntegerDigits, Integer decimalDigits, Locale locale) {
        if (target == null) {
            return null;
        }
        Validate.notNull(minIntegerDigits, "Minimum integer digits cannot be null");
        Validate.notNull(decimalDigits, "Decimal digits cannot be null");
        return formatNumber(target, minIntegerDigits, NumberPointType.NONE, decimalDigits, NumberPointType.DEFAULT, locale);
    }

    public static String format(Number target, Integer minIntegerDigits, Integer decimalDigits, NumberPointType decimalPointType, Locale locale) {
        if (target == null) {
            return null;
        }
        Validate.notNull(minIntegerDigits, "Minimum integer digits cannot be null");
        Validate.notNull(decimalDigits, "Decimal digits cannot be null");
        Validate.notNull(decimalPointType, "Decimal point type cannot be null");
        return formatNumber(target, minIntegerDigits, NumberPointType.NONE, decimalDigits, decimalPointType, locale);
    }

    public static String format(Number target, Integer minIntegerDigits, NumberPointType thousandsPointType, Integer decimalDigits, Locale locale) {
        if (target == null) {
            return null;
        }
        Validate.notNull(minIntegerDigits, "Minimum integer digits cannot be null");
        Validate.notNull(thousandsPointType, "Thousands point type cannot be null");
        Validate.notNull(decimalDigits, "Decimal digits cannot be null");
        return formatNumber(target, minIntegerDigits, thousandsPointType, decimalDigits, NumberPointType.DEFAULT, locale);
    }

    public static String format(Number target, Integer minIntegerDigits, NumberPointType thousandsPointType, Integer decimalDigits, NumberPointType decimalPointType, Locale locale) {
        if (target == null) {
            return null;
        }
        Validate.notNull(minIntegerDigits, "Minimum integer digits cannot be null");
        Validate.notNull(thousandsPointType, "Thousands point type cannot be null");
        Validate.notNull(decimalDigits, "Decimal digits cannot be null");
        Validate.notNull(decimalPointType, "Decimal point type cannot be null");
        return formatNumber(target, minIntegerDigits, thousandsPointType, decimalDigits, decimalPointType, locale);
    }

    public static Integer[] sequence(Integer from, Integer to) {
        return sequence(from, to, Integer.valueOf(from.intValue() <= to.intValue() ? 1 : -1));
    }

    public static Integer[] sequence(Integer from, Integer to, Integer step) {
        Validate.notNull(from, "Value to start the sequence from cannot be null");
        Validate.notNull(to, "Value to generate the sequence up to cannot be null");
        Validate.notNull(step, "Step to generate the sequence cannot be null");
        int iFrom = from.intValue();
        int iTo = to.intValue();
        int iStep = step.intValue();
        if (iFrom == iTo) {
            return new Integer[]{Integer.valueOf(iFrom)};
        }
        if (iStep == 0) {
            throw new IllegalArgumentException("Cannot create sequence from " + iFrom + " to " + iTo + " with step " + iStep);
        }
        List<Integer> values = new ArrayList<>(10);
        if (iFrom < iTo && iStep > 0) {
            int i = iFrom;
            while (true) {
                int i2 = i;
                if (i2 > iTo) {
                    break;
                }
                values.add(Integer.valueOf(i2));
                i = i2 + iStep;
            }
        } else if (iFrom > iTo && iStep < 0) {
            int i3 = iFrom;
            while (true) {
                int i4 = i3;
                if (i4 < iTo) {
                    break;
                }
                values.add(Integer.valueOf(i4));
                i3 = i4 + iStep;
            }
        }
        return (Integer[]) values.toArray(new Integer[values.size()]);
    }

    private static String formatNumber(Number target, Integer minIntegerDigits, NumberPointType thousandsPointType, Integer fractionDigits, NumberPointType decimalPointType, Locale locale) {
        Validate.notNull(fractionDigits, "Fraction digits cannot be null");
        Validate.notNull(decimalPointType, "Decimal point type cannot be null");
        Validate.notNull(thousandsPointType, "Thousands point type cannot be null");
        Validate.notNull(locale, "Locale cannot be null");
        if (target == null) {
            return null;
        }
        DecimalFormat format = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        format.setMinimumFractionDigits(fractionDigits.intValue());
        format.setMaximumFractionDigits(fractionDigits.intValue());
        if (minIntegerDigits != null) {
            format.setMinimumIntegerDigits(minIntegerDigits.intValue());
        }
        format.setDecimalSeparatorAlwaysShown(decimalPointType != NumberPointType.NONE && fractionDigits.intValue() > 0);
        format.setGroupingUsed(thousandsPointType != NumberPointType.NONE);
        format.setDecimalFormatSymbols(computeDecimalFormatSymbols(decimalPointType, thousandsPointType, locale));
        return format.format(target);
    }

    private static DecimalFormatSymbols computeDecimalFormatSymbols(NumberPointType decimalPointType, NumberPointType thousandsPointType, Locale locale) {
        Validate.notNull(decimalPointType, "Decimal point type cannot be null");
        Validate.notNull(thousandsPointType, "Thousands point type cannot be null");
        Validate.notNull(locale, "Locale cannot be null");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        switch (decimalPointType) {
            case POINT:
                symbols.setDecimalSeparator('.');
                break;
            case COMMA:
                symbols.setDecimalSeparator(',');
                break;
            case WHITESPACE:
                symbols.setDecimalSeparator(' ');
                break;
            case DEFAULT:
                DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
                symbols.setDecimalSeparator(dfs.getDecimalSeparator());
                break;
            case NONE:
                symbols.setDecimalSeparator('?');
                break;
        }
        switch (thousandsPointType) {
            case POINT:
                symbols.setGroupingSeparator('.');
                break;
            case COMMA:
                symbols.setGroupingSeparator(',');
                break;
            case WHITESPACE:
                symbols.setGroupingSeparator(' ');
                break;
            case DEFAULT:
                DecimalFormatSymbols dfs2 = new DecimalFormatSymbols(locale);
                symbols.setGroupingSeparator(dfs2.getGroupingSeparator());
                break;
            case NONE:
                symbols.setGroupingSeparator('?');
                break;
        }
        return symbols;
    }

    public static String formatCurrency(Number target, Locale locale) {
        Validate.notNull(locale, "Locale cannot be null");
        if (target == null) {
            return null;
        }
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        return format.format(target);
    }

    public static String formatPercent(Number target, Integer minIntegerDigits, Integer fractionDigits, Locale locale) {
        Validate.notNull(fractionDigits, "Fraction digits cannot be null");
        Validate.notNull(locale, "Locale cannot be null");
        if (target == null) {
            return null;
        }
        NumberFormat format = NumberFormat.getPercentInstance(locale);
        format.setMinimumFractionDigits(fractionDigits.intValue());
        format.setMaximumFractionDigits(fractionDigits.intValue());
        if (minIntegerDigits != null) {
            format.setMinimumIntegerDigits(minIntegerDigits.intValue());
        }
        return format.format(target);
    }

    private NumberUtils() {
    }
}